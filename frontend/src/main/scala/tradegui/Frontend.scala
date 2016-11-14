package tradegui

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom
import org.scalajs.dom.raw._
import shared.Protocol
import tradegui.elements.CropsOverview.ApplicationProps
import tradegui.elements.{CropsOverview, OrdersOverview}

import scala.scalajs.js

object Frontend extends js.JSApp {

  def main(): Unit = {
    println("Started")
    //TODO: Create a join/disconnect button
    joinChat("James Smith")
  }

  def joinChat(name: String): Unit = {
    println( s"Trying to join chat as '$name'...")
    val webSocket = new WebSocket(getWebsocketUri(dom.document, name))

    val cropsOverview = ReactDOM.render(
      CropsOverview.component(ApplicationProps(webSocket)),
      dom.document.getElementById("crops")
    )

    val orderOverview = ReactDOM.render(
      OrdersOverview.component(),
      dom.document.getElementById("orders")
    )

    webSocket.onopen = { (event: Event) ⇒
      println("Chat connection was successful!")
      event
    }

    webSocket.onerror = { (event: ErrorEvent) ⇒
      println(s"Failed: code: ${event.colno}")
    }

    webSocket.onmessage = { (event: MessageEvent) ⇒
      val wsMsg = upickle.default.read[Protocol.ServerToClient](event.data.toString)
      wsMsg match {
        case Protocol.ServerToClient.NewPrices(prices)  ⇒
          println("Updated prices received")
          cropsOverview.modState { state =>
            var newState = state
            prices.foreach(newPrice =>
              newState = newState.get(newPrice.cropsId) match {
                case Some(crop) => newState.updated(newPrice.cropsId, crop.copy(price = newPrice.price))
                case None => newState
              }
            )
            newState
          }
        case Protocol.ServerToClient.AllCrops(set) =>
          println(s"New Crops! ${set.size}")
          cropsOverview.modState(state=>set.map(crop=>(crop.id,crop)).toMap )

        case Protocol.ServerToClient.NewOrder(order)  ⇒
          println("New order received")
          orderOverview.modState(state=> state + order )

        case Protocol.ServerToClient.AllOrders(set) =>
          println(s"New Orders! ${set.size}")
          orderOverview.modState(state=>set)
      }
    }

    webSocket.onclose = { (event: Event) ⇒
      println("Connection to chat lost. You can try to rejoin manually.")
    }
  }

  def getWebsocketUri(document: Document, nameOfChatParticipant: String): String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"

    s"$wsProtocol://${dom.document.location.host}/message-channel?name=$nameOfChatParticipant"
  }

  def p(msg: String) = {
    val paragraph = dom.document.createElement("p")
    paragraph.innerHTML = msg
    paragraph
  }
}