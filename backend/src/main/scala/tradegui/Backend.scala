package tradegui

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import tradegui.routes.{StaticFiles, WebSocketMessageSystem}

import scala.util.{Failure, Success}

object Backend extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val config = system.settings.config
  val interface = config.getString("app.interface")
  val port = config.getInt("app.port")

  val tradeSystemActor = system.actorOf(Props(new BackendActor))
  import system.dispatcher

  val webSocketMessageSystem = new WebSocketMessageSystem(tradeSystemActor)

  object http extends Directives {
    val routes = StaticFiles.routes ~ webSocketMessageSystem.route
  }

  val binding = Http().bindAndHandle(http.routes, interface, port)
  binding.onComplete {
    case Success(binding) ⇒
      val localAddress = binding.localAddress
      println(s"Server is listening on ${localAddress.getHostName}:${localAddress.getPort}")
    case Failure(e) ⇒
      println(s"Binding failed with ${e.getMessage}")
      system.terminate().onComplete(p=>println(s"Actor system terminated: $p"))
  }
}
