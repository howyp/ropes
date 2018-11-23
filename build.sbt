name := "ropes"

Global / version := "0.1.0-SNAPSHOT"
Global / scalaVersion := "2.13.0-M5"
Global / resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
ThisBuild / Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oSD")

lazy val core = project
  .in(file("core"))

lazy val scalacheck = project
  .in(file("scalacheck"))
  .dependsOn(core)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.14.0"
    )
  )

lazy val tests = project
  .in(file("tests"))
  .dependsOn(core, scalacheck)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest"  %% "scalatest"  % "3.0.6-SNAP5" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.0"      % Test
    )
  )
