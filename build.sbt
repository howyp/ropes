name := "ropes"

Global / version := "0.1.0-SNAPSHOT"
Global / scalaVersion := "2.13.0-M4"
Global / resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

lazy val core = project
  .in(file("core"))

lazy val tests = project
  .in(file("tests"))
  .dependsOn(core)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest"  %% "scalatest"  % "3.0.6-SNAP1" % Test,
      "org.scalacheck" %% "scalacheck" % "1.14.0"      % Test
    )
  )
