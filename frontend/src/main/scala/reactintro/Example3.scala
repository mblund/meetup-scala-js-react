package reactintro

import components.bootstrap4.Table._
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport

@JSExport
object Example3 {

    val headers = List(
      Header(HeaderProps("Name")),
      Header(HeaderProps("Price")),
      Header(HeaderProps("Today's Change"))
    )

    val table = Table.withProps( TableProps(headers, bordered = true, hover=true) ) (
      Row(
        td("CHERRY"),
        td("14"),
        td("-1.31%")
      ),
      Row(
        td("CARROT"),
        td("10.1"),
        td("+2.31%")
      ),
      Row(
        td("GINGER"),
        td("50.1"),
        td("+12.31%")
      ),
      Row(
        td("EGGS"),
        td("20"),
        td("+-0")
      )
    )

    ReactDOM.render(
      table,
      document.getElementById("app")
    )

}
