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

Global / scalaVersion := "2.13.5"
Global / scalacOptions ++= Seq(
  // Feature options
  "-encoding",
  "utf-8",
  "-explaintypes",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  // Warnings as errors!
  "-Xfatal-warnings",
  // Linting options
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Xlint:constant",
  "-Xlint:delayedinit-select",
  "-Xlint:deprecation",
  "-Xlint:doc-detached",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit",
  "-Xlint:package-object-classes",
  "-Xlint:poly-implicit-overload",
  "-Xlint:private-shadow",
  "-Xlint:stars-align",
  "-Xlint:type-parameter-shadow",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused:implicits",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:params",
  "-Wunused:patvars",
  "-Wunused:privates",
  "-Wvalue-discard"
)
ThisBuild / Compile / console / scalacOptions --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")
ThisBuild / Test / testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oSDI")

val Dependencies = new {
  val scalacheck                 = "org.scalacheck"    %% "scalacheck"      % "1.15.3"
  val scalatest                  = "org.scalatest"     %% "scalatest"       % "3.2.5"
  val `scalatestplus-scalacheck` = "org.scalatestplus" %% "scalacheck-1-15" % (scalatest.revision + ".0")
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
      // Scalatest has lots of assertions which don't return Unit, so we have to turn off:
      "-Wvalue-discard"
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
    scalacOptions --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")
  )
  .dependsOn(core, dsl, scalacheck)
