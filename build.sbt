import Dependencies._
name := "spark-dataframe-tools"
ThisBuild / scalaVersion := "2.12.12"
ThisBuild / organization := "abhijitoka"
Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
lazy val root = (project in file("."))
  .settings(
    publish / skip := true,
    addCompilerPlugin(paradise),
    scalacOptions := Seq("-feature", "-deprecation")
  )
  .aggregate(macros)


lazy val macros = project
  .settings(
    name := "macros",
    description := "Macros to simplify code",
    libraryDependencies ++= List.concat(spark),
    Test / testOptions += Tests.Argument("-oI"),
    addCompilerPlugin(paradise),
    scalacOptions := Seq("-feature", "-deprecation"),
    assemblySettings
  )

lazy val assemblySettings = Seq(
  assembly / assemblyOption := (assembly / assemblyOption).value.copy(includeScala = false),
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case _ => MergeStrategy.first
  })
