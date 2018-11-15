name := "ropes"
version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.0-M4"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "org.scalatest"  %% "scalatest"  % "3.0.6-SNAP1" % Test,
  "org.scalacheck" %% "scalacheck" % "1.14.0"      % Test
)
