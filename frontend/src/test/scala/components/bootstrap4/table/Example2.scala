package components.bootstrap4.table

import components.bootstrap4.Table._
import japgolly.scalajs.react.{BackendScope, Callback, ReactComponentB, ReactDOM}
import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.ReactEvent
import org.scalajs.dom._

import scala.scalajs.js.annotation.JSExport
import scala.util.Random

@JSExport
object Example2 {

    case class RowData(index:Int, name:String,price:Int)
    val random = Random
    val exampleRows:State = exampledata.Crops.set.zipWithIndex.map{ case (cropName,index)=>
        RowData(index = index, name = cropName, price = random.nextInt(100))
    }.toList

    type State = List[RowData]

    class Backend(scope: BackendScope[Unit, State]) {

      def sortByName(e:ReactEvent):Callback =
        scope.modState(state => state.sortBy(rowData=>rowData.name) )

      def sortByPrice(e:ReactEvent):Callback =
        scope.modState(state => state.sortBy(rowData=>rowData.price) )

      val headers = List(
        Header.withKey(1)(HeaderProps("Name", onClick = sortByName)),
        Header.withKey(2)(HeaderProps("Price", onClick = sortByPrice))
      )

      def render(state: State) =   // ← Accept props, state and/or propsChildren as argument
        Table.withProps(TableProps(headers, headerInverse = true))(
          state.map(rowData=>
            Row.withKey(rowData.index)(
              td(rowData.name),
              td(rowData.price)
            )
          )
        )
    }

    /**
      *  See https://github.com/japgolly/scalajs-react/blob/master/doc/USAGE.md#backends
      *
     */
    val component = ReactComponentB[Unit]("SortedTableExample")
      .initialState(exampleRows)
      .renderBackend[Backend]  // ← Use Backend class and backend.render
      .build

    ReactDOM.render(
      component(),
      document.getElementById("app")
    )

}
