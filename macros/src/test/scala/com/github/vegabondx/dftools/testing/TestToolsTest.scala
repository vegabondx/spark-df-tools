package com.github.vegabondx.dftools.testing

// The string argument given to getResource is a path relative to
// the resources directory.

class TestToolsTest extends TestWrapper {
  test("Checking if it works for standard dataframe") {
    val source = getClass.getResource("/dataframeData.csv")
    val df =
      spark.read.format("csv").option("header", true).load(source.getPath)
    assert(
      TestTools.getRowColumnValue[String](
        df,
        "department",
        Map("employee_id" -> 2)
      ) == "engineering"
    )
  }

}
