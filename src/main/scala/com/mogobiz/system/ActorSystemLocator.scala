/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.system

import akka.actor.ActorSystem

/**
  * provide the akka system actor in
  */
object ActorSystemLocator {
  //  def instance(actor: ActorRef) = new ActorSystemLocator(actor)
  private var instance: ActorSystem = null

  def apply(system: ActorSystem): ActorSystem = {
    if (instance == null) {
      instance = system
    }
    instance
  }

  def apply(): ActorSystem = {
    if (instance == null)
      throw new RuntimeException("ActorSystemLocator constructor should be called first")

    instance
  }
}
