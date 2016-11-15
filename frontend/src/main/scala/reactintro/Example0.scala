package reactintro

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{ReactComponentB, ReactDOM}
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport

@JSExport
object Example0 {

  val markup =
      div(
        h1("Hello world")
      )

  ReactDOM.render(
    markup,
    document.getElementById("app")
  )
}
