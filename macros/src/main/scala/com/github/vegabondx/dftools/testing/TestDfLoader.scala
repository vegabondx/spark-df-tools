package com.github.vegabondx.dftools.testing

import scala.reflect.runtime.universe.TypeTag
import org.apache.spark.sql.{Dataset, Encoder, SparkSession}

import scala.reflect.runtime.universe.typeOf

object TestDfLoader {
  // Incomplete
  def loadTestDataset[T: TypeTag: Encoder](
      directories: Seq[String],
      prefix: String = "",
      method: String = "json"
  )(implicit spark: SparkSession): Dataset[T] = {
    import spark.implicits._
    def camelToDashes(name: String) = {
      "[A-Z\\d]".r
        .replaceAllIn(
          name,
          { m =>
            "-" + m.group(0).toLowerCase()
          }
        )
        .stripPrefix("-")

    }
    val name = camelToDashes(typeOf[T].toString.split('.').last)
    val filename: String =
      if (prefix.isEmpty) name + "." + method
      else prefix + "-" + name + "." + method
    val paths = directories.map(_ + "/" + filename)
    loadTestCases[T](paths, method)
  }

  private def loadTestCases[T: TypeTag: Encoder](
      paths: Seq[String],
      method: String
  )(implicit spark: SparkSession): Dataset[T] = {
    import spark.implicits._
    spark.read
      .schema(implicitly[Encoder[T]].schema)
      .format(method)
      .load(paths: _*)
      .as[T]
  }

}
