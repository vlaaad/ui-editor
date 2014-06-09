package com.vlaaad.ui.util

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.{ObjectSet, JsonWriter, ObjectMap}

/**
 * Created 01.06.14 by vlaaad
 */
sealed abstract class EditorModel(val obj: AnyRef, val params: ObjectMap[String, AnyRef]) {
  val requirements = new ObjectSet[String]()

  def dump(writer: JsonWriter, skin: Skin): Unit = ()
}

class Element(obj: AnyRef, params: ObjectMap[String, AnyRef]) extends EditorModel(obj, params) {
  override def toString: String = s"element{$obj with params $params}"
}

class Wrapper(obj: AnyRef, val elementType: Class[AnyRef], params: ObjectMap[String, AnyRef], var wrapped: EditorModel) extends EditorModel(obj, params) {
  override def dump(writer: JsonWriter, skin: Skin): Unit = {
    Option(wrapped).foreach(v => {
      writer.name("widget")
      EditorToolkit.dump(writer, v, skin)
    })
  }

  def accepts(model: EditorModel) = elementType.isInstance(model.obj) && model.obj != obj

  def remove(model: EditorModel) = wrapped = null

  def setWidget(model: EditorModel) = wrapped = model

  override def toString: String = s"wrapper{$obj with params $params, wrapped: $wrapped}"
}

class Collection(obj: AnyRef, params: ObjectMap[String, AnyRef], val elementType: Class[AnyRef], val elements: com.badlogic.gdx.utils.Array[EditorModel]) extends EditorModel(obj, params) {

  override def dump(writer: JsonWriter, skin: Skin): Unit = {
    import collection.convert.wrapAsScala._
    writer.array("elements")
    elements.foreach(v => EditorToolkit.dump(writer, v, skin))
    writer.pop()
  }

  def accepts(model: EditorModel) = elementType.isInstance(model.obj) && model.obj != obj

  def remove(model: EditorModel) = elements.removeValue(model, true)

  def add(model: EditorModel) = if (!elements.contains(model, true)) elements.add(model)

  override def toString: String = s"collection{$obj with params $params, elements: $elements}"
}