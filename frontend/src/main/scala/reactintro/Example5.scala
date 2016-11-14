package reactintro

import components.bootstrap4.Table._
import components.wrapped.VisibilitySensor
import components.wrapped.VisibilitySensor.PartialVisibility.All
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom._
import scala.scalajs.js.annotation.JSExport

@JSExport
object Example5 {

  type State = Boolean

  class Backend(scope: BackendScope[Unit, State]) {

    def onChange(isVisible: Boolean): Unit =
      scope.modState(state=>isVisible).runNow()

    def render(state: State) =
      div(
        VisibilitySensor(
          delay = 100,
          onChange = onChange,
          partialVisibility = All
        )(
          h1("Visibility Sensor Example")
        )
        ,
        Table.withProps(
          TableProps(
            headers = List(Header(HeaderProps("Dummy counter"))),
            bordered = true,
            hover = true,
            inverse=state
          )
        )(
          1.to(100).map(i => Row(td(i)))
        )
      )
  }

  val component = ReactComponentB[Unit]("Visibility Example")
    .initialState(true)
    .renderBackend[Backend]
    .build

  ReactDOM.render(
    component(),
    document.getElementById("app")
  )
}
