name := "ropes"

Global / organization := "io.github.howyp"
Global / organizationName := "Howard Perrin"
Global / startYear := Some(2018)
Global / licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt"))
Global / homepage := Some(url("https://howyp.github.io/ropes/"))
Global / developers := List(
  Developer(
    id = "howyp",
    name = "Howard Perrin",
    email = "howyp@users.noreply.github.com",
    url = url("https://github.com/howyp")
  )
)

Global / resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

Global / scalaVersion := "2.13.0"
Global / scalacOptions ++= Seq(
  // Thanks to @tpolecat for this list at https://tpolecat.github.io/2017/04/25/scalac-flags.html
  // Some options have been commented as they are either not needed for this project, or do not seem to be available
  // in Scala 2.13.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature",      // Emit warning and location for usages of features that should be imported explicitly.
  //"-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  //"-language:experimental.macros", // Allow macro definition (besides implementation and application)
  //"-language:higherKinds", // Allow higher-kinded types
  //"-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",    // Fail the compilation if there are any warnings.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  //"-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:deprecation", // Emit warning and location for usages of deprecated APIs.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",           // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  //"-Xlint:unsound-match", // Pattern match may not be typesafe.
  //"-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  //"-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",      // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  //"-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  //"-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  //"-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  //"-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)
ThisBuild / Compile / console / scalacOptions --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")
ThisBuild / Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oSDI")

val Dependencies = new {
  val scalacheck                 = "org.scalacheck"    %% "scalacheck"               % "1.14.0"
  val scalatest                  = "org.scalatest"     %% "scalatest"                % "3.1.0-SNAP13"
  val `scalatestplus-scalacheck` = "org.scalatestplus" %% "scalatestplus-scalacheck" % "1.0.0-SNAP8"
}

def module(p: Project, modName: String, skipPublish: Boolean) =
  p.in(file(modName))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(
      moduleName := s"ropes-$modName",
      publish / skip := skipPublish
    )

lazy val core = module(project, modName = "core", skipPublish = false)
  .enablePlugins(spray.boilerplate.BoilerplatePlugin)

lazy val dsl = module(project, modName = "dsl", skipPublish = false)
  .dependsOn(core)

lazy val scalacheck = module(project, modName = "scalacheck", skipPublish = false)
  .dependsOn(core)
  .settings(libraryDependencies ++= Seq(Dependencies.scalacheck))

lazy val tests = module(project, modName = "tests", skipPublish = true)
  .dependsOn(core, dsl, scalacheck)
  .settings(
    scalacOptions --= Seq(
      // Scalacheck currently uses `Stream` which is deprecated in 2.13, so we have to turn off:
      "-Xfatal-warnings",
      "-deprecation",
      // Scalatest has lots of assertions which don't return Unit, so we have to turn off:
      "-Ywarn-value-discard"
    ),
    libraryDependencies ++= Seq(
      Dependencies.scalatest                  % Test,
      Dependencies.scalacheck                 % Test,
      Dependencies.`scalatestplus-scalacheck` % Test
    )
  )

lazy val docs = module(project, modName = "docs", skipPublish = true)
  .enablePlugins(MicrositesPlugin)
  .settings(
    name := "ropes",
    description := "Type-level String Formats",
    micrositeBaseUrl := "/ropes",
    micrositeDocumentationUrl := "/ropes/learn-the-ropes/1-basics.html",
    micrositeGithubOwner := "howyp",
    micrositeGithubRepo := "ropes",
    micrositePushSiteWith := GitHub4s,
    micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
    libraryDependencies -= "org.scalameta" %% "mdoc"     % "1.2.10", // MDoc not published for 2.13 yet
    libraryDependencies -= "org.tpolecat"  %% "tut-core" % "0.6.10",
    libraryDependencies += "org.tpolecat"  %% "tut-core" % "0.6.12",
    scalacOptions --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")
  )
  .dependsOn(core, dsl, scalacheck)
