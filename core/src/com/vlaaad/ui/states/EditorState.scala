package com.vlaaad.ui.states

import com.vlaaad.ui.app.AppState
import com.badlogic.gdx.assets.AssetManager
import com.vlaaad.ui.UiLayout
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import javax.swing.JFileChooser
import java.io.File
import javax.swing.filechooser.FileFilter
import com.badlogic.gdx.files.FileHandle
import collection.convert.wrapAll._
import com.badlogic.gdx.Gdx
import com.vlaaad.ui.models.ActorModel
import com.vlaaad.ui.util.Toolkit


/** Created 29.05.14 by vlaaad */
class EditorState(val assets: AssetManager) extends AppState {
  var workspace: Container = _
  var main: Table = _
  var tree: Container = _
  var skin: Skin = _

  override protected def onEntered(): Unit = {
    val layout = assets.get("ui.layout", classOf[UiLayout])
    skin = layout.skin
    stage.addActor(layout.getActor)
    debug(layout.getActor)
    main = layout.get(classOf[Table], "main")
    tree = layout.get(classOf[Container], "main", "tree")
    workspace = layout.get(classOf[Container], "main", "workspace")
    layout.get(classOf[Button], "main", "open").addListener(new ChangeListener {
      override def changed(event: ChangeEvent, actor: Actor): Unit = {
        val chooser = new JFileChooser()
        chooser.setDialogType(JFileChooser.OPEN_DIALOG)
        chooser.setMultiSelectionEnabled(false)
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY)
        chooser.setAcceptAllFileFilterUsed(false)
        chooser.setCurrentDirectory(new File("."))
        chooser.setFileFilter(new FileFilter {
          override def getDescription: String = "*.layout"

          override def accept(f: File): Boolean = f.getName endsWith ".layout"
        })
        val result = chooser.showDialog(null, "Open")
        if (result == JFileChooser.APPROVE_OPTION) {
          Gdx.app.postRunnable(new Runnable {
            override def run(): Unit = open(new FileHandle(chooser.getSelectedFile))
          })
        }
      }
    })
  }

  def open(file: FileHandle) = {
    val layout = new UiLayout(file)
    workspace.setWidget(layout.getActor)
    workspace.invalidateHierarchy()
    main.layout()
    debug(layout.getActor)
    updateTree(layout.getActor)
  }

  def updateTree(actor: Actor): Unit = {
    val t = new Tree(skin)
    val model = buildModel(actor).get
    tree.setWidget(t)
    t.add(createTree(model))
    t.expandAll()
  }

  def buildModel(actor: Actor): Option[ActorModel] = {
    if (Toolkit.hasInstantiator(actor.getClass)) {
      val model = new ActorModel(actor)
      actor match {
        case g: Group =>
          g.getChildren.map(buildModel).filter(_.isDefined).foreach(model.children += _.get)
        case _ =>
      }
      Some(model)
    } else {
      None
    }
  }

  def createTree(model: ActorModel): Tree.Node = {
    val node = new Tree.Node(new Label(model.actor.toString + ":" + model.actor.getClass.getSimpleName, skin))
    model.children.foreach(v => node.add(createTree(v)))
    node
  }

  def debug(actor: Actor): Unit = {
    actor match {
      case table: Table =>
        table.debug()
        table.getChildren.foreach(debug)
      case group: Group =>
        group.getChildren.foreach(debug)
      case _ =>
    }
  }

  override protected def onRendered(): Unit = {
    Table.drawDebug(stage)
  }
}
