package shared

import shared.Model.{Crop, Order, Price}

object Protocol {

  sealed trait ServerToClient
  object ServerToClient {
    case class AllCrops(set:Set[Crop]) extends ServerToClient
    case class NewPrices(iterable: Iterable[Price]) extends ServerToClient
    case class AllOrders(set:Set[Order]) extends ServerToClient
    case class NewOrder(order:Order) extends ServerToClient
  }

  sealed trait ClientToServer
  object ClientToServer {
    case class PlaceBuyOrder(cropId: Long, price:BigDecimal) extends ClientToServer
  }

}

object Model{
  case class Price(cropsId:Long,price:BigDecimal)
  case class Crop(id:Long,name:String, price:BigDecimal)
  case class Order(orderId:Long, cropId:Long, cropName:String, price:BigDecimal, buyer:String, status:OrderStatus )

  sealed trait OrderStatus
  case object Registered extends OrderStatus
  case object Cleared extends OrderStatus
}

