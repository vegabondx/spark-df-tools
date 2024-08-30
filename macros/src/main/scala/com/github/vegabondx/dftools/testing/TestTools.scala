package com.github.vegabondx.dftools.testing

import org.apache.spark.sql.functions.{col, lower, when, lit}
import org.apache.spark.sql.{Column, DataFrame}

final case class AnalysisException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
) extends Exception(message, cause)

object TestTools {

  /** Gets Value at a particular row ( using clause ) and a particular column
    * @param df
    * @param m map of primary key value pairs ( defaulted to empty map so it takes the only row )
    * @param colname name of the column to be extracted
    * @tparam T
    * @return
    */
  def getRowColumnValue[T](
      df: DataFrame,
      colname: String,
      m: Map[String, Any] = Map()
  ): T = {
    val clause =
      m.foldLeft[Column](lit(true))((b, t) => b && col(t._1) === t._2)
    val extract = df.where(clause).select(colname)
    if (extract.count() > 1)
      throw AnalysisException(
        "MORE rows than expected"
      )
    extract.first().getAs[T](0)
  }

}
