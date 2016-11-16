package tradegui

import akka.actor.{Actor, ActorRef, Status, Terminated}
import akka.actor._
import shared.Model.{Crop, Order, Registered}
import shared.{Model, Protocol}
import shared.Protocol.{ClientToServer, ServerToClient}

import scala.concurrent.duration._
import scala.util.Random


sealed trait TradeSystemEvent
case class NewUser(name: String, subscriber: ActorRef) extends TradeSystemEvent
case class UserLeft(name: String) extends TradeSystemEvent
case class ReceivedMessage(sender: String, message: ClientToServer) extends TradeSystemEvent
case object TimeToUpdatePrices extends TradeSystemEvent

class BackendActor extends Actor {

  private var subscribers = Set.empty[(String, ActorRef)]
  private var crops = createInitialData
  private var orders = Set.empty[Order]

  private val random = Random
  context.system.scheduler.schedule(10.seconds, 1.second){ self ! TimeToUpdatePrices }(executor = context.system.dispatcher)

  def receive: Receive = {
    case NewUser(name, subscriber) ⇒
      context.watch(subscriber)
      subscribers += (name -> subscriber)
      subscriber ! ServerToClient.AllCrops(crops)
      subscriber ! ServerToClient.AllOrders(orders)

    case UserLeft(person) ⇒
      subscribers.find(_._1 == person).foreach {
        case entry @ (name, ref) =>
          // report downstream of completion, otherwise, there's a risk of leaking the
          // downstream when the TCP connection is only half-closed
          ref ! Status.Success(Unit)
          subscribers -= entry
      }

    case Terminated(sub) ⇒
      // clean up dead subscribers, but should have been removed when `ParticipantLeft`
      subscribers = subscribers.filterNot(_._2 == sub)

    case TimeToUpdatePrices =>

      //Generating new Prices
      val (newCrops, newPrices) = crops.map { crop =>
        val newPrice = Math.max((crop.price+random.nextDouble()-0.4).toDouble,0.0)
        (crop.copy(price = newPrice), Model.Price(crop.id,newPrice) )
      }.unzip
      crops = newCrops
      dispatchToAll(ServerToClient.NewPrices(newPrices))

    case ReceivedMessage(sender, msg) ⇒
      msg match {
        case Protocol.ClientToServer.PlaceBuyOrder(cropId,price) ⇒
          println(s"${sender} said: buy crop id:$cropId for price $price")

          crops.find(c=>c.id==cropId) match {
            case None =>
              println(s"Unknown crop $cropId. No order placed")
            case Some(crop) =>
              if (price != crop.price)
                println("Warning, current price and buyOrder doesn't match")
              val newOrder = Order(orders.size, cropId, crop.name, price, sender, Registered)
              orders = orders + newOrder
              dispatchToAll(ServerToClient.NewOrder(newOrder))
          }
      }
  }

  private def dispatchToAll(msg: Protocol.ServerToClient): Unit =
    subscribers.foreach(_._2 ! msg)

  private def members = subscribers.map(_._1).toSeq

  def createInitialData: Set[Crop] = {
    exampledata.Crops.set.take(100).zipWithIndex.map {
      case (name, index) => Crop(index, name, 10)
    }
  }

}
