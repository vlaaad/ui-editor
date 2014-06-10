package com.vlaaad.ui.view

import com.badlogic.gdx.scenes.scene2d.ui.{Table, TextButton, Skin, Window}
import com.vlaaad.ui.util.{Resources, EditorToolkit, Instantiator}
import com.badlogic.gdx.scenes.scene2d.{Stage, Actor}
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.math.Interpolation
import com.vlaaad.ui.util.inputs.EditorInput
import com.vlaaad.ui.util.IStateDispatcher.Listener
import com.vlaaad.ui.app.ResizeListener

/** Created 09.06.14 by vlaaad */
class CreateWindow(val i: Instantiator[AnyRef], val editorSkin: Skin, val layoutSkin: Skin, val cb: Resources => Unit) extends Window(s"Create new ${i.objectClass.getSimpleName}", editorSkin) {

  private var listener: ResizeListener = _

  init()

  private[this] def init(): Unit = {
    import com.vlaaad.ui.states._
    import scala.collection.convert.wrapAsScala._
    padTop(20)
    setModal(true)
    val res = collection.mutable.Map[String, AnyRef]()
    val inputs = collection.mutable.Map[String, Actor]()
    for (r <- i.requirements) {
      add(r.key)
      val input = EditorToolkit.createInput(false, false, null, r.value.asInstanceOf[Class[AnyRef]], layoutSkin, editorSkin)
      res.put(r.key, null)
      addInputListener(res, r.key, input)
      add(input.getActor)
      inputs += r.key -> input.getActor
      row()
    }
    val ok = new TextButton("Create", editorSkin)
    val cancel = new TextButton("Cancel", editorSkin)
    ok.addListener(() => {
      val notFilled = res.filter(_._2 == null).map(v => inputs(v._1))
      if (notFilled.nonEmpty) {
        notFilled.foreach(a => {
          a.addAction(Actions.sequence(
            Actions.moveBy(10, 0, 0.1f, Interpolation.swingOut),
            Actions.moveBy(-20, 0, 0.1f, Interpolation.swingOut),
            Actions.moveBy(10, 0, 0.1f, Interpolation.swingOut)
          ))
        })
      } else {
        addAction(Actions.sequence(
          Actions.fadeOut(0.5f),
          Actions.run(() => {
            val r = new Resources
            res.foreach(e => r.data.put(e._1, e._2))
            cb(r)
          }),
          Actions.removeActor()
        ))
      }
    })
    cancel.addListener(() => addAction(Actions.sequence(
      Actions.fadeOut(0.5f),
      Actions.removeActor())
    ))
    val list = new Table()
    list.add(ok)
    list.add(cancel)
    add(list).colspan(2).row()
    pack()
    getColor.a = 0
    addAction(Actions.fadeIn(0.5f))
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

  def addInputListener(map: collection.mutable.Map[String, AnyRef], key: String, input: EditorInput[AnyRef]) = {
    input.getDispatcher.addListener(true, new Listener[AnyRef] {
      override def onChangedState(newState: scala.AnyRef): Unit = map += key -> newState
    })
  }
}
