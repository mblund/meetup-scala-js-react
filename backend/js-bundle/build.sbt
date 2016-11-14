name := "js-bundle"

scalaVersion := "2.11.8"

lazy val bundle = taskKey[sbt.Attributed[sbt.File]]("bundle")

enablePlugins(SbtWeb)

enablePlugins(SbtJsEngine)

import com.typesafe.sbt.jse.SbtJsTask._
import com.typesafe.sbt.jse.SbtJsEngine.autoImport.JsEngineKeys._
import scala.concurrent.duration._

bundle := {
  ( npmNodeModules in Assets ).value
  val inputFile = (baseDirectory.value / "bundle-definition.js").getAbsolutePath
  val resources = baseDirectory.value.getParentFile / "src" / "main" / "resources"
  resources.mkdirs
  val outputFile = (resources / "bundle.js").getAbsolutePath
  val modules =  (baseDirectory.value / "node_modules").getAbsolutePath
  println(s"Bundling: ${inputFile}\n into ${outputFile}")

  executeJs(state.value,
    engineType.value,
    None,
    Seq(modules),
    baseDirectory.value / "browserify.js",
    Seq(inputFile, outputFile),
    30.seconds)
  val result:sbt.Attributed[sbt.File] = Attributed.blank(sbt.file(outputFile))
  (result)
}
