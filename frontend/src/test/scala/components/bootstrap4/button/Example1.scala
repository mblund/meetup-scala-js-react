package components.bootstrap4.button


import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.Callback
import org.scalajs.dom._
import components.bootstrap4.Button._

import scala.scalajs.js.annotation.JSExport

@JSExport
object Example1 {

    val markup =
      div(
        Button(Props(onClick= e=>Callback.alert("Hello world!")),"Click!")
      )

    ReactDOM.render(
      markup,
      document.getElementById("app")
    )

}
