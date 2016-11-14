package components.bootstrap4

import japgolly.scalajs.react.vdom.ReactStyle

import japgolly.scalajs.react.vdom.all._
import japgolly.scalajs.react.{Callback, ReactComponentB, ReactEvent}

object Button {

  final lazy val primary              = "btn-primary"
  final lazy val secondary            = "btn-secondary"
  final lazy val success              = "btn-success"
  final lazy val info                 = "btn-info"
  final lazy val warning              = "btn-warning"
  final lazy val danger               = "btn-danger"
  final lazy val primary_outlined     = "btn-outline-primary"
  final lazy val secondary_outlined   = "btn-outline-secondary"
  final lazy val success_outlined     = "btn-outline-success"
  final lazy val info_outlined        = "btn-outline-info"
  final lazy val warning_outlined     = "btn-outline-warning"
  final lazy val danger_outlined      = "btn-outline-danger"
  final lazy val small                = "btn-sm"
  final lazy val large                = "btn-lg"
  final lazy val block                = "btn-block"
  final lazy val active               = "active"
  final lazy val disabled             = "disabled"

  case class Props(onClick:ReactEvent=>Callback = e=>Callback.empty)

  lazy val Button =
    ReactComponentB[Props]("Button")
      .render(scope =>
        button( cls:=s"btn $secondary", onClick==>scope.props.onClick)(scope.propsChildren)
      )
      .build
}
