package com.olegych.config.beans.yaml

import com.olegych.config.beans.Config

object ConfigSampleRunner extends App {
  def print {
    println(Config[ConfigSample].value)
    println(io.Source.fromFile(Config.defaultFileName[ConfigSample]).mkString)
  }

  print
  Config[ConfigSample].value = Config[ConfigSample].value.copy(name = "demo-user2")
  print
}

case class ConfigSample(name: String, roles: Seq[Role], weapon: Option[Weapon]) {
  def this() = this("demo-user", Role("admin") :: Nil,
    Some(new Weapon()))
}

case class Role(name: String) {
  def this() = this("")
}

case class Weapon(name: String, sizes: Seq[Double] /*, damage: Map[Symbol, Double]*/) {
  def this() = this("sword", 42.0 :: Nil /*, Map('health -> 0.001, 'agility -> 0.02)*/)
}