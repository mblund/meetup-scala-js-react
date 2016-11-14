package aReactIntro

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactDOM, ReactEvent}
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport

@JSExport
object Example2 {

  type State = Int

  /**
    * https://github.com/japgolly/scalajs-react/blob/master/doc/USAGE.md#backends
    */
  class Backend(scope: BackendScope[Unit, State]) {

    def onIncreaseClick(e:ReactEvent):Callback = scope.modState(state => state+1)

    def render(state: State) =   // ← Accept props, state and/or propsChildren as argument
      div(
        h2(s"Counter $state"),
        button(cls:="btn btn-primary", onClick ==> onIncreaseClick )("Click to increase counter")
      )
  }

  val StateExample = ReactComponentB[Unit]("StateExample")
    .initialState(0)
    .renderBackend[Backend]  // ← Use Backend class and backend.render
    .build

  val markup =
      div(
        StateExample()
      )

  ReactDOM.render(
    markup,
    document.getElementById("app")
  )
}
