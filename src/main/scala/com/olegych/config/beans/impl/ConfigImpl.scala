package com.olegych.config.beans.impl

import com.olegych.config.beans.Config
import org.slf4j.LoggerFactory

abstract class ConfigImpl[T: Manifest] extends Config[T] {
  @transient protected val log = LoggerFactory.getLogger(getClass)
  type Repr

  def default = manifest[T].erasure. /*getConstructors.head.*/ newInstance().asInstanceOf[T]

  def load: Option[Repr]

  def save(r: Repr)

  def destruct(t: T): Repr

  def construct(r: Repr): T

  def applyDefault(default: T, r: Repr): Repr

  def extractCustom(r: Repr): Repr

  def value = {
    val result = load.map(loaded => construct(extractCustom(loaded))).getOrElse(default)
    value_=(result)
  }

  def value_=(t: T) = {
    save(applyDefault(default, destruct(t)))
    t
  }
}
