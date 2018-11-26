name := "ropes"

Global / version := "0.1.0-SNAPSHOT"
Global / scalaVersion := "2.13.0-M5"
Global / resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)
Global / organizationName := "Howard Perrin"
Global / startYear := Some(2018)
Global / licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))

ThisBuild / Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oSD")

lazy val core = project
  .in(file("core"))
  .enablePlugins(AutomateHeaderPlugin)

lazy val scalacheck = project
  .in(file("scalacheck"))
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalacheck" %% "scalacheck" % "1.14.0"
    )
  )

lazy val tests = project
  .in(file("tests"))
  .enablePlugins(AutomateHeaderPlugin)
  .dependsOn(core, scalacheck)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest"  %% "scalatest"  % "3.0.6-SNAP5" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.0"      % Test
    )
  )
