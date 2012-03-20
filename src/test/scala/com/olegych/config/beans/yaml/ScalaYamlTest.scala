package com.olegych.config.beans.yaml

import org.specs2.mutable.Specification
import java.io.StringReader

class ScalaYamlTest extends Specification {
  def yaml = new ScalaYaml

  def check[T: Manifest](t: T) = {yaml.represent(t).pp; yaml.load(yaml.dump(t).pp) should_== t}

  //  def check[T:Manifest](t:T) = yaml.loadAs[T](yaml.dump(t).pp,
  // manifest[T].erasure.asInstanceOf[Class[T]]) should_== t

  "config" should {
    "dump list of ints" in {
      check(A(5, List(7)))
    }
    "dump list of beans" in {
      check(B(listA = A(51, List(7, 8)) :: A(5, List(7), B(i = 3, listA = A(45) :: Nil) :: Nil) :: Nil))
    }
    "dump option" in {
      check(C(Some(A(51, List(7, 8)))))
    }
    "dump none" in {
      check(C())
    }
    "construct " in {
      yaml.construct[A](yaml.represent(A()).pp).pp should_== A()
    }
    "compose" in {
      yaml.construct[A](yaml.compose(new StringReader(yaml.dump(A()).pp))).pp should_== A()
    }
    //    "traverse" in {
    //      yaml.traverse(yaml.compose(new StringReader("""i: 42
    //      |listA:
    //      |- a: 51
    //      |  b: [7, 8]
    //      |  listB: []
    //      |- a: 5
    //      |  b: [7]
    //      |  listB:
    //      |  - i: 3
    //      |    listA:
    //      |    - a: 45
    //      |      b: [1]
    //      |      listB: []
    //      """.stripMargin)))
    //        .pp should_== A()
    //    }
  }
}

case class B(i: Int = 42, listA: List[A] = Nil) {
  def this() = this(42)
}

case class C(optionA: Option[A] = None) {
  def this() = this(None)
}

case class A(a: Int = 5, b: List[Int] = 1 :: Nil, listB: List[B] = Nil) {
  def this() = this(7)
}
