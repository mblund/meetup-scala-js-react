package tradegui.routes

import akka.http.scaladsl.server.Directives._

object StaticFiles {

  lazy val routes =
    get {
      pathSingleSlash {
        getFromResource("web/index.html")
      } ~
        // Scala-JS puts them in the root of the resource directory per default,
        // so that's where we pick them up
        path("frontend-launcher.js")(getFromResource("frontend-launcher.js")) ~
        path("frontend-fastopt.js")(getFromResource("frontend-fastopt.js")) ~
        path("frontend-fastopt.js.map")(getFromResource("frontend-fastopt.js.map")) ~
        // We also put the javascript dependency bundle in the root of the resource directory
        path("bundle.js")(getFromResource("bundle.js"))
    } ~
      getFromResourceDirectory("web")
}
