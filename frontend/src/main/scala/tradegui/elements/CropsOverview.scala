package tradegui.elements

import components.bootstrap4.Table._
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactEvent}
import org.scalajs.dom.WebSocket
import shared.Model.Crop
import shared.Protocol

object  CropsOverview {

  type State = Map[Long, Crop]
  class Backend(scope: BackendScope[ApplicationProps, State]) {

    def onBuyClick(props:ApplicationProps, cropId:Long, price:BigDecimal): (ReactEvent) => Callback = e => Callback {
      val message = upickle.default.write(
        Protocol.ClientToServer.PlaceBuyOrder(cropId,price))
      props.webSocket.send(message)
    }

    def render(props:ApplicationProps, state: State) =   // ← Accept props, state and/or propsChildren as argument
      div(
        h2(id:="crops")("Crops"),
        Table.withProps(TableProps())(
          state.values.map( crop =>
            Row.withKey(crop.id)(
              td(crop.name),
              td(crop.price.setScale(2,BigDecimal.RoundingMode.DOWN).toString),
              td(calculateDiff(crop.price)  ),
              td(button(cls:="btn btn-outline-warning", onClick ==> onBuyClick(props, crop.id, crop.price ))("Buy") )
            )
          )
        )
      )

    /**
      * We just asume that all prices start on 10 every market opening(Server restart)
      */
    def calculateDiff(price:BigDecimal) : String =
      ((price-10)/10).setScale(1,BigDecimal.RoundingMode.DOWN).toString+"%"

  }

  case class ApplicationProps(webSocket: WebSocket)

  val component =
    ReactComponentB[ApplicationProps]("Application")
      .initialState(Map.empty[Long,Crop])
      .renderBackend[Backend]
      .build
}


