package reactintro

import japgolly.scalajs.react.{ReactComponentB, ReactDOM}
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom._
import scala.scalajs.js.annotation.JSExport

@JSExport
object Example1 {

  /**
    * See
    * https://github.com/japgolly/scalajs-react/blob/master/doc/USAGE.md#creating-components
    */
  val Hello =
    ReactComponentB[String]("Hello")
      .render_P(name => div("Hello there ", name))
      .build

  val markup =
      div(
        Hello("Scala meetup")
      )

  ReactDOM.render(
    markup,
    document.getElementById("app")
  )
}
