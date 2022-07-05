package com.dftools

import org.apache.spark.sql.functions.{col, lit, unix_timestamp, month, year}
import org.apache.spark.sql.DataFrame

object Functions {
  def seqToCol (columnNames: Seq[String] ) = columnNames.map (c => col(c) )

}