package components.bootstrap4

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{Callback, ReactComponentB, ReactElement, ReactEvent}

object Table {

  case class HeaderProps(name: String,
                         onClick: ReactEvent=>Callback = e=>Callback.empty)
  lazy val Header =
    ReactComponentB[HeaderProps]("Header")
      .render_P(props => th(onClick==>props.onClick)(props.name) )
      .build

  lazy val Row =
    ReactComponentB[Unit]("Row")
      .render(scope=>
        tr(
          scope.propsChildren
        )
      )
      .build

  case class TableProps(headers:Iterable[ReactElement] = Iterable.empty,
                        bordered: Boolean = false,
                        striped: Boolean = false,
                        inverse: Boolean = false,
                        hover: Boolean = false,
                        reflow: Boolean = false,
                        responsive: Boolean = false,
                        headerInverse: Boolean = false
                  )
  lazy val Table =
    ReactComponentB[TableProps]("Table")
      .render(scope =>
        table(
          classSet(
            "table"           -> true,
            "table-inverse"   -> scope.props.inverse,
            "table-striped"   -> scope.props.striped,
            "table-bordered"  -> scope.props.bordered,
            "table-hover"     -> scope.props.hover
          )
        )(
          thead(scope.props.headerInverse ?= (cls:="thead-inverse") )(
            tr(
              scope.props.headers.map(header => header)
            )
          ),
          tbody(
            scope.propsChildren
          )
        )
      )
      .build
}