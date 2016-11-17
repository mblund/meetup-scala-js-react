scalaVersion in ThisBuild := "2.11.8"

val akkaV = "2.4.2"
val upickleV = "0.4.3"

lazy val root = project.in(file(".")).aggregate(frontend, backend)

lazy val javascript = project.in(file("backend/js-bundle"))

// Scala-Js frontend
lazy val frontend = project
  .in(file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    persistLauncher in Compile := true,
    persistLauncher in Test := false,
    testFrameworks += new TestFramework("utest.runner.Framework"),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "com.lihaoyi" %%% "upickle" % upickleV,
      "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
      "com.github.japgolly.scalajs-react" %%% "core" % "0.11.3",
      "com.github.japgolly.scalajs-react" %%% "extra" % "0.11.3"
    )
  )
  .dependsOn(sharedJs)

// Akka Http based backend
lazy val backend = project
  .in(file("backend"))
  .settings(Revolver.settings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
      "org.specs2" %% "specs2" % "2.3.12" % "test",
      "com.lihaoyi" %% "upickle" % upickleV
    ),
    (resourceGenerators in Compile) += Def.task {
      val fastOptAttrFile = (fastOptJS in Compile in frontend).value
      val fastOptFile = fastOptAttrFile.data // the .js file
      val fastOptSourceMapFile = fastOptAttrFile.get(scalaJSSourceMap).get
      val launcher = (packageScalaJSLauncher in Compile in frontend).value
      Seq(fastOptFile.asFile, fastOptSourceMapFile.asFile, launcher.data.asFile)
    }.taskValue,
    watchSources ++= (watchSources in frontend).value
  )
  .dependsOn(sharedJvm)

lazy val shared = crossProject.crossType(CrossType.Pure).in(file("shared"))
lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js
