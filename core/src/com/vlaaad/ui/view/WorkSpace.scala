package com.vlaaad.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.{Drawable, Layout}
import com.badlogic.gdx.graphics.g2d.Batch

/** Created 01.06.14 by vlaaad */
class WorkSpace extends WidgetGroup {

  var widget: Option[Actor] = None
  var background: Option[Drawable] = None

  def this(widget: Actor) = {
    this()
    setWidget(widget)
  }


  override def draw(batch: Batch, parentAlpha: Float): Unit = {
    background.foreach(_.draw(batch, getX, getY, getWidth, getHeight))
    batch.flush()
    if (clipBegin()) {
      super.draw(batch, parentAlpha)
      batch.flush()
      clipEnd()
    }
  }

  def setWidget(widget: Actor) = {
    this.widget foreach (_.remove())
    this.widget = Option(widget)
    this.widget foreach addActor
    invalidate()
  }

  override def layout(): Unit = {
    widget match {
      case Some(v) =>
        v match {
          case layout: Layout => layout.validate()
          case _ =>
        }
        v.setPosition(getWidth / 2 - v.getWidth / 2, getHeight / 2 - v.getHeight / 2)
      case None =>
    }
  }

  override def getPrefWidth: Float = widget match {
    case Some(v) => v match {
      case layout: Layout => layout.getPrefWidth
      case actor => actor.getWidth
    }
    case None => 0
  }

  override def getPrefHeight: Float = widget match {
    case Some(v) => v match {
      case layout: Layout => layout.getPrefHeight
      case actor => actor.getHeight
    }
    case None => 0
  }
}
