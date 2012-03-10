package com.olegych.config.beans.yaml

import com.olegych.config.beans.impl.ConfigImpl

class YamlConfig[T: Manifest] extends ConfigImpl[T] {
  def yaml = new ScalaYaml
}
