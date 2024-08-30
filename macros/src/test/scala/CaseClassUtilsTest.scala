package com.github.vegabondx.dftools

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should
import CaseClassUtils._

class CaseClassUtilsTest extends AnyFreeSpec with should.Matchers {
  case class Person(name: String, age: Int)
  case class Employee(id: Int, p: Person)
  case class Coefficient(A: Int, B: Int, C: Float, D: String, E: Int)
  "case class to map utility " - {
    "handle single level case classes correctly" in {

      val p = Person("A", 29)

      p.toMap should equal(Map(("name" -> "A"), ("age" -> 29)))
    }

    "handle multiple level case classes correctly" in {

      val p = Person("A", 29)
      val e = Employee(1, p)

      e.toMap should equal(Map(("id" -> 1), ("p" -> p)))
    }
  }
  "json utility " - {
    "handle multiple level case classes correctly" in {

      val p = Person("A", 29)
      val e = Employee(1, p)

      val jsonStr = "{\"p\":{\"name\":\"A\",\"age\":29},\"id\":1}"

      e.toJson should equal(jsonStr)
    }
  }

  "aggregation utility " - {
    "aggregate all single level case classes correctly" in {

      val c = Coefficient(1, 2, 3.0f, "hello", 0)

      c.checkSum[Int] should equal(3)
      c.checkNonZeroCount[Int] should equal(2)
      c.getAllNonZeroByType[Float] should equal(Seq(3.0f))
      c.checkAllNonZeroCount should equal(4)

      // Custom transform
      c.checkTransformSum[Float] {
        case i: Float => i * i
        case _        => 1f
      } should equal(13f)

    }
  }
}
