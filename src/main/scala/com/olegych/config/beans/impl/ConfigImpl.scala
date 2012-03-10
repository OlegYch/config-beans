package com.olegych.config.beans.impl

import com.olegych.config.beans.Config

class ConfigImpl[T: Manifest] extends Config[T] {
  def default = manifest[T].erasure.getConstructors.head.newInstance().asInstanceOf[T]

  def value = default

  def value_=(t: T) {}
}
