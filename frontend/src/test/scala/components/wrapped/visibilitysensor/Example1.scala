package components.wrapped.visibilitysensor


import components.wrapped.VisibilitySensor
import components.wrapped.VisibilitySensor.PartialVisibility.All
import components.bootstrap4.Table._
import japgolly.scalajs.react.ReactDOM
import japgolly.scalajs.react.vdom.all._
import org.scalajs.dom._
import scala.scalajs.js.annotation.JSExport

@JSExport
object Example1 {

  val headers = List(
    Header(HeaderProps("Counter"))
  )

  def onChange(isVisible: Boolean): Unit = println(s"Header visibility=$isVisible")

  var html = div(
    VisibilitySensor(
      onChange = onChange,
      partialVisibility = All
    )(
      h1("Visibility Sensor Example")
    )
    ,
    Table.withProps(TableProps(headers, bordered = true, hover = true))(//CHECK:
      1.to(100).map(i => Row(td(i)))
    )
  )


  ReactDOM.render(
    html,
    document.getElementById("app")
  )

}
