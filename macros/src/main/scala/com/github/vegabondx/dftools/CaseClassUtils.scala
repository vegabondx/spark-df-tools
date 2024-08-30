package com.github.vegabondx.dftools

import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.json4s.{Formats, NoTypeHints}

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/** Contains tools primarily meant for Testing and Debugging of case class objects
 * avoids Encoder or Dataframe terminology
 */
object CaseClassUtils {

  implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)
  implicit class caseClassExtension[T: TypeTag: reflect.ClassTag](instance: T) {

    /** Utility to get case class as Map
      *
      * @param instance instance of case class
      * @tparam T case class TypeTag
      * @return
      */
    def toMap: Map[String, Any] = {
      val rm = runtimeMirror(getClass.getClassLoader)
      val im = rm.reflect(instance)
      typeOf[T].members
        .collect {
          case m: MethodSymbol if m.isCaseAccessor =>
            val name = m.name.toString
            val value = im.reflectMethod(m).apply()
            (name, value)
        }(collection.breakOut)
        .toArray
        .toMap
    }

    /** Utility to get case class as json
      *
      * @param instance instance of case class
      * @tparam T TypeTag
      * @return
      */
    def toJson: String = {
      write(this.toMap)
      // TODO enable sorted json
    }
  }

  implicit class productExtension(instance: Product) {

    /** Sum all case class parameters using transform
      * @param instance case class declaration
      * @tparam T Numeric Type
      * @return
      */
    def checkTransformSum[T: ClassTag: Numeric](f: Any => T): T = {
      instance.productIterator
        .map[T] {
          f
        }
        .sum
    }

    /** Count all Non Zero case class parameters of type T
      *
      * @param instance case class declaration
      * @tparam T Numeric Type
      * @return
      */
    def checkNonZeroCount[T: ClassTag: Numeric]: Int =
      checkTransformSum[Int] {
        case i: T => if (i != implicitly[Numeric[T]].zero) 1 else 0
        case _    => 0
      }

    /** Sum all case class parameters of type T
      *
      * @param instance case class declaration
      * @tparam T Numeric Type
      * @return
      */
    def checkSum[T: ClassTag: Numeric]: T = {
      checkTransformSum[T] {
        case i: T => i
        case _    => implicitly[Numeric[T]].zero
      }
    }

    /** get count all non zero numerics of any type
      *
      * @param instance case class instance
      * @return
      */
    def checkAllNonZeroCount: Int = {
      checkTransformSum[Int] {
        case _: Number => 1
        case _         => 0
      }
    }

    /** Get all case class parameters of type T
      *
      * @tparam T Numeric Type
      * @return
      */
    def getAllNonZeroByType[T: ClassTag: Numeric]: Seq[T] = {
      val zero = implicitly[Numeric[T]].zero
      instance.productIterator
        .map[T] {
          case i: T => i
          case _    => zero
        }
        .collect { case x if x != zero => x }
        .toSeq
    }

  }
}
