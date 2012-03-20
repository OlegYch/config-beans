package com.olegych.config.beans

import yaml.YamlConfig


/**
 */
object Config {
  def apply[T: Manifest]: Config[T] = apply[T](defaultFileName)

  def apply[T: Manifest](fileName: String): Config[T] = new YamlConfig[T](fileName)

  def defaultFileName[T: Manifest] = "%s/.%s.yml"
    .format(util.Properties.userHome, manifest[T].erasure.getName)
}

trait Config[T] {
  def default: T

  def value: T

  def value_=(t: T): T
}