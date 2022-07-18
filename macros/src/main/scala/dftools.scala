package com.github.vegabondx.dftools

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.DataFrame

object Functions {

  def seqToCol(columnNames: Seq[String]) = columnNames.map(c => col(c))
  def seqToColDf(df: DataFrame, columnNames: Seq[String]) =
    columnNames.map(c => df(c))

  def joinExpr(keys: Seq[String], df1: DataFrame, df2: DataFrame, sfx:String="") =
    keys.map { case (c1) => df1(c1) === df2(c1+sfx) }.reduce(_ && _)
  def joinExprNs(keys: Seq[String], df1: DataFrame, df2: DataFrame, sfx:String="") =
    keys.map { case (c1) => df1(c1) <=> df2(c1+sfx) }.reduce(_ && _)

  def addSuffix(
      df: DataFrame,
      suffix: String,
      pk: Seq[String] = Seq()
  ): DataFrame = {
    val renamedColumns = df.columns.map(c =>
      if (pk.contains(c)) df(c) else df(c).as(s"${c}$suffix")
    )
    df.select(renamedColumns: _*)
  }
}
