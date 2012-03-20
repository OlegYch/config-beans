package com.olegych.config.beans.yaml

import org.specs2.mutable.Specification

/**
 */
class YamlConfigTest extends Specification {
  def f = {
    val f = java.io.File.createTempFile("YamlConfigTest", ".yml");
    f.deleteOnExit();
    f.getAbsolutePath
  }

  def yamlConfig[T: Manifest] = new YamlConfig[T](f)

  abstract class ConfigTestHarness[T <: AnyRef : Manifest] {
    val config = yamlConfig[T]

    def default: T

    def custom: T
  }

  def testWithHarness[T <: AnyRef](name: String, h: ConfigTestHarness[T]) = {
    name should {
      import h._
      "parse default " in h.synchronized {
        default mustEqual config.default
      }
      "accept modification" in h.synchronized {
        config.value = default
        default mustEqual config.value
      }
      "save custom value from saved " in h.synchronized {
        config.value = custom
        custom mustEqual config.value
      }
    }
  }

  "yaml config" should {
    "load defaults" in {
      yamlConfig[A].default should_== new A()
    }
    "load saved value" in {
      val config = yamlConfig[A]
      config.value = A()
      config.load.pp should_!= None
    }
    "create default file " in {
      yamlConfig[A].value should_== new A()
    }
  }

  testWithHarness("Simple Config", new ConfigTestHarness[SampleConfig] {
    def custom = SampleConfig("qwe", SampleConfigInner("asd"))

    def default = new SampleConfig()
  })

  testWithHarness("Config with list of beans ", new ConfigTestHarness[SampleConfigList] {
    def custom = SampleConfigList(SampleConfigInner("asd") :: Nil)

    def default = new SampleConfigList()
  })

  testWithHarness("Config with optional property ", new ConfigTestHarness[SampleConfigOption] {
    def custom = SampleConfigOption(Some(SampleConfigInner("asd")))

    def default = new SampleConfigOption()
  })

}

case class SampleConfigInner(user: String) {
  def this() = this("")
}

case class SampleConfigList(configs: List[SampleConfigInner]) {
  def this() = this(SampleConfigInner("hello") :: Nil)
}

case class SampleConfigOption(configs: Option[SampleConfigInner]) {
  def this() = this(Some(SampleConfigInner("hello")))
}

case class SampleConfig(user: String, inner: SampleConfigInner) {
  def this() = this("", SampleConfigInner("none"))
}
