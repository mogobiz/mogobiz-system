/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.system

import akka.actor.ActorSystem
import akka.stream.Materializer

/**
 * provide the akka materializer
 */

object MaterializerLocator {
  //  def instance(actor: ActorRef) = new ActorSystemLocator(actor)
  private var instance: Materializer = null

  def apply(mat: Materializer): Materializer = {
    if (instance == null) {
      instance = mat
    }
    instance
  }

  def apply(): Materializer = {
    if (instance == null)
      throw new RuntimeException("MaterializerLocator constructor should be called first")

    instance
  }
}
