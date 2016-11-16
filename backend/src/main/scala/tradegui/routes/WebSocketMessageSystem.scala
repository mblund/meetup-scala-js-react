package tradegui.routes

import akka.actor._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import shared.Protocol
import tradegui.{NewUser, ReceivedMessage, TradeSystemEvent, UserLeft}
import upickle.default

/**
  * Packages all logic around messaging between the server and the client
  *
  */
class WebSocketMessageSystem(val tradeSystemActor: ActorRef) {

  def route =
    get {
      path("message-channel") {
        parameter('name) { name ⇒
          handleWebSocketMessages(webSocketMessageFlow(sender = name))
        }
      }
    }

  def messageFlow(sender: String): Flow[Protocol.ClientToServer, Protocol.ServerToClient, _] = {
    val fromBrowser =
      Flow[Protocol.ClientToServer]
        .map(ReceivedMessage(sender, _))
        .to(Sink.actorRef[TradeSystemEvent](tradeSystemActor, UserLeft(sender)))
    val toBrowser =
      Source
        .actorRef[Protocol.ServerToClient](19, OverflowStrategy.dropNew)
        .mapMaterializedValue { actorRef =>
          tradeSystemActor ! NewUser(sender, actorRef)
        }
    Flow.fromSinkAndSource(fromBrowser, toBrowser)
  }

  def injectMessage(message: Protocol.ServerToClient): Unit =
    tradeSystemActor ! message // non-streams interface

  def webSocketMessageFlow(sender: String): Flow[Message, Message, _] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(msg) ⇒ msg // unpack incoming WS text messages...
        // This will lose (ignore) messages not received in one chunk
        // FIXME: We need to handle TextMessage.Streamed as well.
      }
      .map { s =>
        default.read[Protocol.ClientToServer](s)
      }
      .via(messageFlow(sender)) // ... and route them through the messageFlow ...
      .map { msg =>
        TextMessage.Strict(default.write(msg)) // ... pack outgoing messages into WS JSON messages ...
      }
}
