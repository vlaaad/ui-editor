package com.vlaaad.ui.models

import com.badlogic.gdx.scenes.scene2d.Actor

/** Created 01.06.14 by vlaaad */
class ActorModel(val actor: Actor) {
  val children = collection.mutable.ListBuffer[ActorModel]()
}
