package com.olegych.config.beans.yaml

import com.olegych.config.beans.impl.ConfigImpl
import scala.io.Codec
import com.google.common.io.Files
import org.yaml.snakeyaml.nodes._
import java.io.{FileNotFoundException, File}

class YamlConfig[T: Manifest](val fileName: String) extends ConfigImpl[T] {
  def clazz = manifest[T].erasure.asInstanceOf[Class[T]]

  type Repr = Node

  def yaml = new ScalaYaml

  def destruct(t: T) = yaml.represent(t)

  def construct(r: Repr) = yaml.construct[T](r)

  def applyDefault(default: T, r: Repr) = {
    val d = yaml.represent(default)
    //    println(yaml.traverse(d))
    r
  }

  def extractCustom(r: Repr) = r

  import resource._

  val file = (new File(fileName), Codec.UTF8)

  def logFileNotFound(e: scala.List[scala.Throwable]) {
    log.info("Could not load config from {} {}", fileName, e)
  }

  def load = try {
    managed((Files.newReader _).tupled(file))
      .acquireFor(yaml.compose)
      .fold({e => logFileNotFound(e); None}, Option(_))
  } catch {
    case e: FileNotFoundException => logFileNotFound(e :: Nil); None
  }

  def save(r: Repr) {
    managed((Files.newWriter _).tupled(file)).foreach(yaml.dump(r, _, Tag.MAP))
  }
}
