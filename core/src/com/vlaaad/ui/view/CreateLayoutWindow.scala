package com.vlaaad.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.{Skin, Window}
import com.vlaaad.ui.util.EditorModel
import com.vlaaad.ui.states._
import com.badlogic.gdx.scenes.scene2d.{Stage, Group}
import com.vlaaad.ui.app.ResizeListener
import com.badlogic.gdx.scenes.scene2d.actions.Actions

/** Created 11.06.14 by vlaaad */
class CreateLayoutWindow(editorSkin: Skin, val layoutSkin: Skin, state: EditorState, val callback: EditorModel => Unit) extends Window("Create new layout", editorSkin) {
  private var listener: ResizeListener = _

  init()

  private def init(): Unit = {
    padTop(20)
    setModal(true)
    add(state.createInstantiatorActor(classOf[Group].asInstanceOf[Class[AnyRef]], model => {
      addAction(Actions.sequence(
        Actions.fadeOut(0.5f),
        Actions.removeActor())
      )
      callback(model)
    }))

    getColor.a = 0
    addAction(Actions.fadeIn(0.5f))
    pack()
  }


  override def setStage(stage: Stage): Unit = {
    Option(stage) match {
      case Some(s) =>
        listener = new ResizeListener {
          override protected def resize(): Unit = {
            setPosition(stage.getWidth / 2 - getWidth / 2, stage.getHeight / 2 - getHeight / 2)
          }
        }
        listener.resize()
        s.addListener(listener)
      case None =>
        getStage.removeListener(listener)
    }
    super.setStage(stage)
  }
}
