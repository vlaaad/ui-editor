package com.vlaaad.ui.models

import scala.collection.mutable.ListBuffer
import com.badlogic.gdx.utils.ObjectMap

/** Created 01.06.14 by vlaaad */
sealed abstract class ActorModel(val obj: AnyRef, val data: ObjectMap[String, Object])

class Wrapper(obj: AnyRef, data: ObjectMap[String, Object], var model: Option[ActorModel]) extends ActorModel(obj, data)

class Leaf(obj: AnyRef, data: ObjectMap[String, Object]) extends ActorModel(obj, data)

class Collection(obj: AnyRef, data: ObjectMap[String, Object], val models: ListBuffer[ActorModel]) extends ActorModel(obj, data)
