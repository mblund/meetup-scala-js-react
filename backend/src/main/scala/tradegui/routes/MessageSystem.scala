package tradegui.routes

import akka.actor._
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.stream.OverflowStrategy
import akka.stream.scaladsl._
import akka.stream.stage.{Context, PushStage, SyncDirective, TerminationDirective}
import shared.Protocol
import tradegui.{NewUser, ReceivedMessage, TradeSystemEvent, UserLeft}
import upickle.default

/**
  * Packages all logic around messaging between the server and the client
  *
  */

trait MessageSystem {
  def messageFlow(sender: String): Flow[String, Protocol.ServerToClient, Any]
  def injectMessage(message: Protocol.ServerToClient): Unit
}

class WebSocketMessageSystem(val tradeSystemActor:ActorRef) extends MessageSystem {

  def route =
    get{
      path("message-channel") {
        parameter('name) { name ⇒
          handleWebSocketMessages(webSocketMessageFlow(sender = name))
        }
      }
    }

  // Wraps the messageActor in a sink. When the stream to this sink will be completed

    // FIXME: here some rate-limiting should be applied to prevent single users flooding the application
    def messageInSink(sender: String) = Sink.actorRef[TradeSystemEvent](tradeSystemActor, UserLeft(sender))


    def messageFlow(sender: String): Flow[String, Protocol.ServerToClient, Any] = {
      val in =
        Flow[String]
          .map(ReceivedMessage(sender, _))
          .to(messageInSink(sender))

      // The counter-part which is a source that will create a target ActorRef per
      // materialization where the messageActor will send its messages to.
      // This source will only buffer one element and will fail if the client doesn't read
      // messages fast enough.
      val out =
        Source.actorRef[Protocol.ServerToClient](19, OverflowStrategy.dropNew)//TODO:check
          .mapMaterializedValue(tradeSystemActor ! NewUser(sender, _))

      Flow.fromSinkAndSource(in, out)
    }

    def injectMessage(message: Protocol.ServerToClient): Unit = tradeSystemActor ! message // non-streams interface


  def webSocketMessageFlow(sender:String): Flow[Message, Message, Any] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(msg) ⇒ msg // unpack incoming WS text messages...
        // This will lose (ignore) messages not received in one chunk
        // FIXME: We need to handle TextMessage.Streamed as well.
      }
      .via(messageFlow(sender)) // ... and route them through the messageFlow ...
      .map {
      case msg: Protocol.ServerToClient ⇒
        TextMessage.Strict(default.write(msg)) // ... pack outgoing messages into WS JSON messages ...
    }
      .via(reportErrorsFlow) // ... then log any processing errors on stdin

  def reportErrorsFlow[T]: Flow[T, T, Any] =
    Flow[T]
      .transform(() ⇒ new PushStage[T, T] {
        def onPush(elem: T, ctx: Context[T]): SyncDirective = ctx.push(elem)

        override def onUpstreamFailure(cause: Throwable, ctx: Context[T]): TerminationDirective = {
          println(s"WS stream failed with $cause")
          super.onUpstreamFailure(cause, ctx)
        }
      })

}
