import Dependencies._
name := "spark-dataframe-tools"
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
    libraryDependencies ++= List.concat(common),
    Test / testOptions += Tests.Argument("-oI"),
    addCompilerPlugin(paradise),
    scalacOptions := Seq("-feature", "-deprecation"),
    assemblySettings,
    buildSettings,
    assembly / assemblyJarName := "utils.jar"
  )

lazy val sandbox = project
  .settings(
    name := "sandbox",
    description := "Macros to simplify code",
    libraryDependencies ++= List.concat(common),
    Test / testOptions += Tests.Argument("-oI"),
    addCompilerPlugin(paradise),
    buildSettings,
    scalacOptions := Seq("-feature", "-deprecation"),
    assemblySettings,
    assembly / assemblyJarName := "sandbox.jar"
  )

lazy val assemblySettings = Seq(
  assembly / assemblyOption := (assembly / assemblyOption).value
    .copy(includeScala = false),
  assembly / assemblyMergeStrategy := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
    case _                                   => MergeStrategy.first
  }
)

lazy val buildSettings = Seq(
  organization := "com.github.vegabondx",
  scalaVersion := "2.12.8",
  publishMavenStyle := true,
  // This needs to be listed in two places due to a bug in the plugin
  // and the way the plugin works in multi module projects
  scalacOptions += "-Ywarn-unused-import",
  assembly / test := {},
  assembly / aggregate := false,
  Test / fork := true,
  Test / parallelExecution := false,
  Test / testOptions += Tests.Argument("-oI"),
  cleanFiles += baseDirectory.value / ".tmp"
)
