package com.olegych.config.beans

import yaml.YamlConfig


/**
 */
object Config {
  def apply[T: Manifest]: Config[T] = new YamlConfig[T]
}

trait Config[T] {
  def default: T

  def value: T

  def value_=(t: T)
}