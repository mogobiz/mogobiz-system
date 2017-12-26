/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.system

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.directives.LogEntry
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

/**
  * Core is type containing the ``system: ActorSystem`` member. This enables us to use it in our
  * apps as well as in our tests.
  */
trait MogobizSystem {
  implicit def system: ActorSystem
  implicit val materializer = ActorMaterializer()
  //  def breaker: CircuitBreaker
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

  //  lazy val breaker = new CircuitBreaker(system.scheduler,
  //    maxFailures = 5,
  //    callTimeout = 10.seconds,
  //    resetTimeout = 1.minute)
  /**
    * Ensure that the constructed ActorSystem is shut down when the JVM shuts down
    */
  sys.addShutdownHook(system.terminate())
}
