package com.github.vegabondx.dftools.macros

import org.apache.spark.sql.functions.{col, lower, when}
import org.apache.spark.sql.{Column, DataFrame}
object DfTools {

  /** helper function converting columnames to seq
    *
    * @param columnNames
    * @return Seq[Column]
    */
  def seqToCol(columnNames: Seq[String]): Seq[Column] =
    columnNames.map(c => col(c))

  def seqToCol(df: DataFrame, columnNames: Seq[String]): Seq[Column] =
    columnNames.map(c => df(c))

  def seqToCol(colString: String, char: Char = ','): Seq[Column] =
    colString.split(char).map(c => col(c))

  /** Creates condition over primary key to filter it to a single row
    * can be then applied to df or any other data frame
    * @param df  a filtered dataframe with a condition to be tested
    * @param key key to be picked
    */
  def pick(df: DataFrame, key: Seq[String]): Column =
    key.map(k => col(k) <=> df.head(1)(0).getAs[String](k)).reduce(_ && _)

  /** Creates join expressions for similar tables
    *
    * @param keys
    * @param left
    * @param right
    * @param sfx suffix attached to the RIGHT table
    * @return
    */
  def getJoinExpr(
      keys: Seq[String],
      left: DataFrame,
      right: DataFrame,
      sfx: String = ""
  ) =
    keys.map { case (c1) => left(c1) === right(c1 + sfx) }.reduce(_ && _)

  def joinExprNs(
      keys: Seq[String],
      df1: DataFrame,
      df2: DataFrame,
      sfx: String = ""
  ) =
    keys.map { case (c1) => df1(c1) <=> df2(c1 + sfx) }.reduce(_ && _)

  /** Adds suffixes to DataFrames
    *
    * @param df     DataFrame
    * @param suffix Suffix
    * @param pk     Primary key or keys excluded from suffix
    * @return
    */
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

  /** Joins two dataframes for comparison
    *
    * @param left   Left dataframe
    * @param right  Right dataframe
    * @param keys   join keys
    * @param sfx    suffix
    * @param method method to be used "inner" or "left"
    * @param dropKeyCols = false  drop the key columns
    * @return Dataframe
    */
  def joinDfSfx(
      left: DataFrame,
      right: DataFrame,
      keys: Seq[String],
      sfx: String,
      method: String,
      dropKeyCols: Boolean = false
  ): DataFrame = {
    val rightsfx = addSuffix(right, sfx)
    val je = getJoinExpr(keys, left, rightsfx, sfx)
    val joint = left.join(rightsfx, je, method)
    dropKeyCols match {
      case true  => joint.drop(keys.map(_ + sfx): _*)
      case false => joint
    }
  }

  /** Compares similar dataframe columns
    *
    * @param joint   joint dataframe with suffixes
    * @param compCol Columns to compare
    * @param sfx     suffix used
    * @return
    */
  def compareJoinedDfSfx(
      joint: DataFrame,
      compCol: Seq[String],
      sfx: String
  ): DataFrame =
    compCol.foldLeft(joint)((df, clmn) =>
      df.withColumn(clmn + "_eq", joint(clmn) <=> joint(clmn + sfx))
    )

  // TODO check if columns match
  // TODO check if primary keys is primary key
  /** @param left Left Dataframe
    * @param right Right dataframe
    * @param keys Primary Key
    * @param compCol columns to compare
    * @param full if all the full join needs to be extracted out
    * @return Key + comparison columns + aggregated comparison
    */
  def checkEquality(
      left: DataFrame,
      right: DataFrame,
      keys: Seq[String],
      compCol: Seq[String],
      full: Boolean = false
  ): DataFrame = {
    val jx = getJoinExpr(keys, left, right)
    val joint = left.join(right, jx, "inner")
    val compColOut = compCol.map(x => left(x) <=> right(x) as x + "_eq")
    val leftNonKeys = left.columns.toSet.diff(keys.toSet).toSeq
    val rightNonKeys = right.columns.toSet.diff(keys.toSet).toSeq
    val outputCols = full match {
      case false => keys.map(left(_)) ++ compColOut
      case _ =>
        keys.map(left(_)) ++ leftNonKeys.map(left(_)) ++ rightNonKeys.map(
          right(_)
        ) ++ compColOut
    }
    val result = compCol.map(x => col(x + "_eq")).reduce(_ && _)
    joint.select(outputCols: _*).withColumn("isCongruent", result)
  }

  /** Calssifies the value in the column
    *
    * @param clmn Column to be analyzed
    * @return
    */
  def getColValueType(clmn: Column): Column =
    when(clmn.isNull, "isNull")
      .when(clmn === "  ", "isTwoBlanks")
      .when(clmn === "", "isBlank")
      .when(
        lower(clmn) === "null" || lower(clmn) === "nan" || lower(
          clmn
        ) === "none" || lower(clmn) === "na" || lower(clmn) === "n/a",
        "isNullString"
      )
      .when(clmn.cast("long").isNull, "isAlphaNum")
      .when(clmn.cast("long") === 0L, "isZero")
      .when(clmn.cast("long") === 1L, "isOne")
      .when(clmn.cast("long") < 0L, "isNegativeNum")
      .otherwise("isPositiveNum")

  /** applies value type on bunch of columns information on bunch of columns
    * @param df
    * @param clmns
    * @return
    */
  def getColValueTypeStats(df: DataFrame, clmns: Seq[String]): DataFrame =
    clmns.foldLeft(df)((d, c) =>
      d.withColumn(c + "_valType", getColValueType(col(c)))
    )
}
