package com.github.vegabondx.dftools.testing

import com.holdenkarau.spark.testing.{DataFrameSuiteBase, DatasetSuiteBase}
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession, Encoder}
import org.scalatest.funsuite.AnyFunSuite

class TestWrapper
    extends AnyFunSuite
    with DataFrameSuiteBase
    with DatasetSuiteBase {
  implicit def _spark: SparkSession = spark
}
