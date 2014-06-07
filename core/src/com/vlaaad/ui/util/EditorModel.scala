package com.vlaaad.ui.util

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.{ObjectSet, JsonWriter, ObjectMap}

/**
 * Created 01.06.14 by vlaaad
 */
sealed abstract class EditorModel[A](val obj: A, val params: ObjectMap[String, AnyRef]) {
  val requirements = new ObjectSet[String]()

  def dump(writer: JsonWriter, skin: Skin): Unit = ()
}

class Element[A](obj: A,
                 params: ObjectMap[String, AnyRef])
  extends EditorModel[A](obj, params) {
  override def toString: String = s"element{$obj with params $params}"
}

class Wrapper[A, That](obj: A,
                       val elementType: Class[_ <: That],
                       params: ObjectMap[String, AnyRef],
                       var wrapped: EditorModel[That])
  extends EditorModel[A](obj, params) {

  override def dump(writer: JsonWriter, skin: Skin): Unit = {
    Option(wrapped) match {
      case Some(v) =>
        writer.name("widget")
        EditorToolkit.dump(writer, v, skin)
      case None =>
    }
  }

  def accepts(model: EditorModel[_]) = {
    elementType.isInstance(model.obj) && model.obj != obj
  }

  def remove(model: EditorModel[That]) = {
    wrapped = null
  }

  def setWidget(model: EditorModel[That]) = {
    wrapped = model
  }

  override def toString: String = s"wrapper{$obj with params $params, wrapped: $wrapped}"

}

class Collection[A, That](obj: A,
                          params: ObjectMap[String, AnyRef],
                          val elementType: Class[That],
                          val elements: com.badlogic.gdx.utils.Array[EditorModel[That]])
  extends EditorModel[A](obj, params) {
  override def dump(writer: JsonWriter, skin: Skin): Unit = {
    import collection.convert.wrapAsScala._
    writer.array("elements")
    elements.foreach(v => EditorToolkit.dump(writer, v, skin))
    writer.pop()
  }

  def accepts(model: EditorModel[_]) = {
    elementType.isInstance(model.obj) && model.obj != obj && !elements.contains(model.asInstanceOf[EditorModel[That]], true)
  }

  def remove(model: EditorModel[That]) = {
    elements.removeValue(model, true)
  }

  def add(model: EditorModel[That]) = {
    if (!elements.contains(model, true)) {
      elements.add(model)
    }
  }


  override def toString: String = s"collection{$obj with params $params, elements: $elements}"
}