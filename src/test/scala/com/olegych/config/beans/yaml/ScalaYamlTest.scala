package com.olegych.config.beans.yaml

import org.specs2.mutable.Specification

class ScalaYamlTest extends Specification {
  val yaml = new ScalaYaml

  def check[T: Manifest](t: T) = {yaml.represent(t).pp; yaml.load(yaml.dump(t).pp) should_== t}

  //  def check[T:Manifest](t:T) = yaml.loadAs[T](yaml.dump(t).pp,
  // manifest[T].erasure.asInstanceOf[Class[T]]) should_== t

  "config" should {
    "dump list of ints" in {
      check(A(5, List(7)))
    }
    "dump list of beans" in {
      check(B(A(51, List(7, 8)) :: A(5, List(7)) :: Nil))
    }
    "dump option" in {
      check(C(Some(A(51, List(7, 8)))))
    }
    "dump none" in {
      check(C())
    }
  }
}

case class B(a: List[A] = Nil) {
  def this() = this(Nil)
}

case class C(a: Option[A] = None) {
  def this() = this(None)
}

case class A(a: Int = 5, b: List[Int] = 1 :: Nil) {
  def this() = this(7)
}
