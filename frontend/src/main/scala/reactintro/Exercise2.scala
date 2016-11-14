package reactintro

import components.bootstrap4.Table._
import japgolly.scalajs.react.extra.LogLifecycle
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactDOM, ReactEventI}
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport
import scala.util.Random

@JSExport
object Exercise2 {

    type Props = List[RowData]
    type State = List[RowData]

    class Backend(scope: BackendScope[Props, State]) {

      def onInputChange(props:Props)(e:ReactEventI) : Callback =
        Callback.warn("Not implemented")
       //TODO: Time to filter the table

      val headers = List(
        Header.withKey(1)(HeaderProps("Name")),
        Header.withKey(2)(HeaderProps("Price"))
      )

      def resultText(state:State, props:Props) =
        state.size match {
          case x if (x == props.size) => "Enter a value to filter the table below."
          case 0 => "No row is matching filter."
          case 1 => s"Filter match 1 row out of ${props.size}"
          case _ => s"Filter match ${state.size} rows out of ${props.size}"
        }

      def render(props:Props, state: State) =   // ← Accept props, state and/or propsChildren as argument
        div(
          div(cls:="form-group")(
            label(`for`:="filterInput")("Filter Input"),
            input.search(
              cls:="form-control",
              id:="filterInput",
              placeholder:="Enter Filter",
              onChange ==> onInputChange(props)
            ),
            small(cls:="form-text text-muted")(
              resultText(state, props)
            )
          ),
          Table.withProps(TableProps(headers, headerInverse = true))(
            state.map(rowData=>
              Row.withKey(rowData.index)(
                td(rowData.name),
                td(rowData.price)
              )
            )
          )
        )
    }

    val component = ReactComponentB[Props]("SortedTableExample")
      .initialState_P(props=>props)
      .renderBackend[Backend]
      .configure(LogLifecycle.short)
      .build

    val exampleRows:State = RowData.createExampleRows

    ReactDOM.render(
      component(exampleRows),
      document.getElementById("app")
    )
}

case class RowData(index:Int, name:String,price:Int)

object RowData {
  val random = Random

  def createExampleRows: List[RowData] = {
    exampledata.Crops.set.zipWithIndex.map { case (cropName, index) =>
      RowData(index = index, name = cropName, price = random.nextInt(100))
    }.toList
  }
}