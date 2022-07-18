package com.github.vegabondx.dftools

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.{Column, DataFrame}

object Functions {
  /**
   * helper function converting columnames to seq
   * @param columnNames
   * @return Seq[Column]
   */
  def seqToCol(columnNames: Seq[String]):Seq[Column] = columnNames.map(c => col(c))
  def seqToCol(df: DataFrame, columnNames: Seq[String]):Seq[Column]  =
    columnNames.map(c => df(c))

  /**
   *
   * @param keys
   * @param left
   * @param right
   * @param sfx
   * @return
   */
  def joinExpr(keys: Seq[String], left: DataFrame, right: DataFrame, sfx:String="") =
    keys.map { case (c1) => left(c1) === right(c1+sfx) }.reduce(_ && _)
  def joinExprNs(keys: Seq[String], df1: DataFrame, df2: DataFrame, sfx:String="") =
    keys.map { case (c1) => df1(c1) <=> df2(c1+sfx) }.reduce(_ && _)

  /**
   * Adds suffixes to DataFrames
   * @param df DataFrame
   * @param suffix Suffix
   * @param pk Primary key or keys excluded from suffix
   * @return
   */
  def addSuffix(df: DataFrame, suffix: String, pk: Seq[String] = Seq()): DataFrame = {
    val renamedColumns = df.columns.map(c =>
      if (pk.contains(c)) df(c) else df(c).as(s"${c}$suffix")
    )
    df.select(renamedColumns: _*)
  }

  /**
   * Joins two dataframes for comparison
   * @param left Left dataframe
   * @param right Right dataframe
   * @param keys join keys
   * @param sfx suffix
   * @param method method to be used "inner" or "left"
   * @return Dataframe
   */
  def joinDfSfx(left: DataFrame, right: DataFrame, keys:Seq[String], sfx:String, method:String): DataFrame =
  {
    val second = addSuffix(right,sfx)
    val je=joinExpr(keys,left,second,sfx)
    left.join(second,je,method)
  }

  /**
   * Compares similar dataframe columns
   * @param joint joint dataframe with suffixes
   * @param compCol Columns to compare
   * @param sfx suffix used
   * @return
   */
  def compareJoinedDfSfx(joint:DataFrame,compCol:Seq[String],sfx:String): DataFrame =
    compCol.foldLeft(joint)((df,clmn)=> df.withColumn(clmn+"_eq",joint(clmn)<=>joint(clmn+sfx)))
  }