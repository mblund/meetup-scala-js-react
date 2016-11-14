import sbt._
import Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import autoImport._
import sbt.dsl._
import spray.revolver.RevolverPlugin._

object Build extends Build {
  lazy val scalaV = "2.11.8"
  lazy val akkaV = "2.4.2"
  lazy val upickleV  =   "0.4.3"

  lazy val root =
    Project("root", file("."))
      .aggregate(frontend, backend)

  lazy val javascript = project.in(file("backend/js-bundle"))

  // Scala-Js frontend
  lazy val frontend =
    Project("frontend", file("frontend"))
      .enablePlugins(ScalaJSPlugin)
      .settings(commonSettings: _*)
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
  lazy val backend =
    Project("backend", file("backend"))
      .settings(Revolver.settings: _*)
      .settings(commonSettings: _*)
      .settings(
        libraryDependencies ++= Seq(
          "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
          "org.specs2" %% "specs2" % "2.3.12" % "test",
          "com.lihaoyi" %% "upickle" % upickleV
        ),
        (resourceGenerators in Compile) <+=
          (fastOptJS in Compile in frontend, packageScalaJSLauncher in Compile in frontend)
            .map((f1, f2) => Seq(f1.data, f2.data)),
        watchSources <++= (watchSources in frontend)
      )
      .dependsOn(sharedJvm)

  lazy val shared = (crossProject.crossType(CrossType.Pure) in file ("shared")).
    settings(
      scalaVersion:=scalaV
    )

  lazy val sharedJvm= shared.jvm
  lazy val sharedJs= shared.js

  def commonSettings = Seq(
    scalaVersion := scalaV
  )
}
