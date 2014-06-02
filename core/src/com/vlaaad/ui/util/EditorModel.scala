package com.vlaaad.ui.util

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.JsonWriter
import com.badlogic.gdx.utils.ObjectMap

/**
 * Created 01.06.14 by vlaaad
 */
sealed abstract class EditorModel[A](val obj: A, val params: ObjectMap[String, AnyRef]) {
  def dump(writer: JsonWriter, skin: Skin): Unit = ()
}

class Element[A](obj: A,
                 params: ObjectMap[String, AnyRef])
  extends EditorModel[A](obj, params) {
  override def toString: String = s"element{$obj with params $params}"
}

class Wrapper[A, That](obj: A,
                       params: ObjectMap[String, AnyRef],
                       var wrapped: EditorModel[That])
  extends EditorModel[A](obj, params) {
  override def dump(writer: JsonWriter, skin: Skin): Unit = {
    Option(wrapped) match {
      case Some(v)=>
        writer.name("widget")
        EditorToolkit.dump(writer, v, skin)
      case None =>
    }
  }

  override def toString: String = s"wrapper{$obj with params $params, wrapped: $wrapped}"
}

class Collection[A, That](obj: A,
                          params: ObjectMap[String, AnyRef],
                          val elements: com.badlogic.gdx.utils.Array[EditorModel[That]])
  extends EditorModel[A](obj, params) {
  override def dump(writer: JsonWriter, skin: Skin): Unit = {
    import collection.convert.wrapAsScala._
    writer.array("elements")
    elements.foreach(v => EditorToolkit.dump(writer, v, skin))
    writer.pop()
  }

  override def toString: String = s"collection{$obj with params $params, elements: $elements}"
}