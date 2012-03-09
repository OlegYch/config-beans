package com.olegych.config.beans

import org.specs2.mutable.Specification

/**
 */

class ConfigTest extends Specification {
  "config" should {
    "exist" in {
      new Config shouldNotEqual null
    }
  }
}
