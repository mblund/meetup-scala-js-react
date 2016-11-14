package tradegui.elements

import components.bootstrap4.Table._
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, ReactComponentB}
import shared.Model.Order

object OrdersOverview {

  type State = Set[Order]
  class Backend(scope: BackendScope[Unit, State]) {

    val headers = List(
      Header(HeaderProps("id")),
      Header(HeaderProps("Crop Name")),
      Header(HeaderProps("Price")),
      Header(HeaderProps("Status"))
    )

    def render(state: State) =   // ← Accept props, state and/or propsChildren as argument
      div(
        h2(id:="orders")("Orders"),
        Table.withProps(TableProps(headers = headers ))(
          state.map( order =>
            Row.withKey(order.orderId)(
              td(s"#${order.orderId}"),
              td(order.cropName),
              td(order.price.setScale(2, BigDecimal.RoundingMode.DOWN).toString),
              td(order.buyer),
              td(order.status.toString)
            )
          )
        )
      )
  }

  val component =
    ReactComponentB[Unit]("OrdersOverview")
      .initialState(Set.empty[Order])
      .renderBackend[Backend]
      .build
}


