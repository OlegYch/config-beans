package com.olegych.config.beans.yaml

import com.olegych.config.beans.impl.ConfigImpl
import scala.io.Codec
import com.google.common.io.Files
import java.io.File
import org.yaml.snakeyaml.nodes.{Tag, Node}

class YamlConfig[T: Manifest](val fileName: String = "") extends ConfigImpl[T] {
  def clazz = manifest[T].erasure.asInstanceOf[Class[T]]

  type Repr = Node

  def yaml = new ScalaYaml

  def destruct(t: T) = yaml.represent(t)

  def construct(r: Repr) = yaml.construct[T](r)

  def applyDefault(default: T, r: Repr) = r

  def extractCustom(r: Repr) = r

  import resource._

  val file = (new File(fileName), Codec.UTF8)

  def load = managed((Files.newReader _).tupled(file)).acquireFor(yaml.compose)
    .fold({e => log.info("Could not load config from {} {}", fileName, e); None}, Option(_))

  def save(r: Repr) {
    managed((Files.newWriter _).tupled(file)).foreach(yaml.dump(r, _, Tag.MAP))
  }
}
