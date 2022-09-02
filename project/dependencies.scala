import sbt._

object Dependencies {
  lazy val delta = Seq(
    "io.delta" %% "delta-core" % "0.7.0" excludeAll (
      ExclusionRule(organization = "org.apache.spark")
    )
  )

  lazy val paradise =
    "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full

  lazy val scalaArm = Seq(
    "com.jsuereth" %% "scala-arm" % "2.0"
  )

  lazy val common = Seq(
    "org.apache.spark" %% "spark-core" % "3.0.1" % "provided",
    "org.apache.spark" %% "spark-hive" % "3.0.1" % "provided",
    "org.apache.spark" %% "spark-sql" % "3.0.1" % "provided",
    "org.scalatest" %% "scalatest" % "3.1.0" % "provided",
    "com.holdenkarau" %% "spark-testing-base" % "3.0.1_1.0.0" % "provided"
  )

  lazy val teradata = Seq(
    "com.teradata" % "terajdbc4" % "16.00.00.03",
    "com.teradata" % "tdgssconfig" % "16.00.00.03"
  )

  lazy val scalaTestPlus = Seq(
    "org.scalatestplus" %% "mockito-1-10" % "3.1.0.0" % Test
  )

  lazy val mockito = Seq(
    "org.mockito" % "mockito-scala_2.12" % "1.16.3",
    "org.mockito" % "mockito-inline" % "3.6.28" % Test
  )

  lazy val sprayJson = Seq(
    "io.spray" %% "spray-json" % "1.3.4"
  )

  lazy val mockitoAll = Seq(
    "org.mockito" % "mockito-all" % "1.8.4"
  )

}
