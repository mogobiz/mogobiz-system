package com.mogobiz.system


import akka.actor.{ActorLogging, Actor, ActorSystem}
import akka.event.Logging._
import spray.http.StatusCodes._
import spray.http._
import spray.routing._
import spray.routing.directives.LogEntry
import spray.util.LoggingContext

import scala.util.control.NonFatal

/**
 * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
 * apps as well as in our tests.
 */
trait MogobizSystem {
  implicit def system: ActorSystem

  def showRequest(request: HttpRequest): HttpResponsePart ⇒ Option[LogEntry] = {
    case HttpResponse(s, _, _, _) ⇒ Some(LogEntry(s"${s.intValue}: ${request.uri}", InfoLevel))
    case ChunkedResponseStart(HttpResponse(OK, _, _, _)) ⇒ Some(LogEntry(" 200 (chunked): ${request.uri}", InfoLevel))
    case _ ⇒ None
  }


}

/**
 * This trait implements ``System`` by starting the required ``ActorSystem`` and registering the
 * termination handler to stop the system when the JVM exits.
 */
trait BootedMogobizSystem extends MogobizSystem {
  /**
   * Construct the ActorSystem we will use in our application
   */
  implicit lazy val system = ActorSystem("mogobiz")

  /**
   * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
   */
  sys.addShutdownHook(system.shutdown())
}




/**
 * @param responseStatus
 * @param response
 */
case class ErrorResponseException(responseStatus: StatusCode, response: Option[HttpEntity]) extends Exception

/**
 * Allows you to construct Spray ``HttpService`` from a concatenation of routes; and wires in the error handler.
 * It also logs all internal server errors using ``SprayActorLogging``.
 *
 * @param route the (concatenated) route
 */
class RoutedHttpService(route: Route) extends Actor with HttpService with ActorLogging {

  implicit def actorRefFactory = context

  implicit val handler = ExceptionHandler {
    case NonFatal(ErrorResponseException(statusCode, entity)) => ctx =>
      ctx.complete(statusCode, entity)

    case NonFatal(e) => ctx => {
      log.error(e, InternalServerError.defaultMessage)
      ctx.complete(InternalServerError)
    }
  }

  def receive: Receive =
    runRoute(route)(handler, RejectionHandler.Default, context, RoutingSettings.default, LoggingContext.fromActorRefFactory)
}

