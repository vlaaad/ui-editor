package com.vlaaad.ui.models

import scala.collection.mutable.ListBuffer
import com.badlogic.gdx.scenes.scene2d.Actor

/** Created 01.06.14 by vlaaad */
sealed abstract class ActorModel(val actor: Actor)

class Wrapper(actor: Actor, var model: Option[ActorModel]) extends ActorModel(actor)

class Leaf(actor: Actor) extends ActorModel(actor)

class Collection(actor: Actor, val models: ListBuffer[ActorModel]) extends ActorModel(actor)
