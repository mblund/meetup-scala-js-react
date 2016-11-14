package components.wrapped

import VisibilitySensor.PartialVisibility
import japgolly.scalajs.react.{React, ReactComponentU_, ReactNode}

import scala.scalajs.js

/** *
  * Sensor component for React that notifies you when it goes in or out of the window viewport.
  *
  * @param onChange callback for whenever the element changes from being within the window viewport or not.
  *                 Function is called with 1 argument (isVisible: boolean)
  *
  * @param active (default true) boolean flag for enabling / disabling the sensor.
  *               When active !== true the sensor will not fire the onChange callback.
  *
  * @param partialVisibility (default false) consider element visible if only part of it is visible.
  *                          Also possible values are - 'top', 'right', 'bottom', 'left' -
  *                          in case it's needed to detect when one of these become visible explicitly.
  *
  * @param minTopValue (default false) consider element visible if only part of it is visible and a minimum amount of
  *                    pixels could be set, so if at least 100px are in viewport, we mark element as visible.
  *
  * @param delay  (default 1000) integer, number of milliseconds between checking the element's position in relation
  *               the window viewport. Making this number too low will have a negative impact on performance.
  *
  * @param containment (optional) element to use as a viewport when checking visibility.
  *                    Default behaviour is to use the browser window as viewport.
  *
  * @param delayedCall (default false) if is set to true, wont execute on page load
  *                    ( prevents react apps triggering elements as visible before styles are loaded )
  */
case class VisibilitySensor(onChange          : Boolean => Unit ,
                            active            : Boolean = true,
                            partialVisibility : js.UndefOr[PartialVisibility] = js.undefined,
                            minTopValue       : js.UndefOr[Int] = js.undefined,
                            delay             : Int=1000,
                            containment       : js.UndefOr[org.scalajs.dom.raw.Element] = js.undefined,
                            delayedCall       : Boolean=false
                           ) {
  def toJS = {
    val p = js.Dynamic.literal()
    p.updateDynamic("onChange")(onChange)
    p.updateDynamic("active")(active)
    p.updateDynamic("partialVisibility")(true)
    partialVisibility.foreach(x=>p.updateDynamic("partialVisibility")(
      x match {
        case PartialVisibility.All  => false
        case PartialVisibility.Some => true
        case _                      => x.toString.toLowerCase
      }
    ))
    minTopValue.foreach(p.updateDynamic("minTopValue")(_))
    p.updateDynamic("delay")(delay)
    containment.foreach(p.updateDynamic("containment")(_))
    p.updateDynamic("delayedCall")(delayedCall)
    p
  }

  def apply(child : ReactNode) = {
    val f = React.asInstanceOf[js.Dynamic].createFactory(js.Dynamic.global.Bundle.VisibilitySensor)
    f(toJS, child).asInstanceOf[ReactComponentU_]
  }
}

object VisibilitySensor{
  sealed trait PartialVisibility
  object PartialVisibility {
    case object All extends PartialVisibility
    case object Some extends PartialVisibility
    case object Top extends PartialVisibility
    case object Right extends PartialVisibility
    case object Bottom extends PartialVisibility
    case object Left extends PartialVisibility
  }
}


