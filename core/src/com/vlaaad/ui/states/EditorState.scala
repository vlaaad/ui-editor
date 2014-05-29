package com.vlaaad.ui.states

import com.vlaaad.ui.app.AppState
import com.badlogic.gdx.assets.AssetManager
import com.vlaaad.ui.UiLayout
import com.badlogic.gdx.scenes.scene2d.ui.{Container, Button, Table}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import javax.swing.JFileChooser
import java.io.File
import javax.swing.filechooser.FileFilter
import com.badlogic.gdx.files.FileHandle
import scala.collection.convert.wrapAll._

/** Created 29.05.14 by vlaaad */
class EditorState(val assets: AssetManager) extends AppState {
  var workspace: Container = _
  var main: Table = _

  override protected def onEntered(): Unit = {
    val layout = assets.get("ui.layout", classOf[UiLayout])
    stage.addActor(layout)
    main = layout.get(classOf[Table],"main")
    main.debug()
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
          open(new FileHandle(chooser.getSelectedFile))
        }
      }
    })
  }

  def open(file: FileHandle) = {
    val layout = new UiLayout(file)
    workspace.setWidget(layout)
    workspace.invalidateHierarchy()
    main.layout()
    debug(layout)
  }

  def debug(group: Group): Unit = {
    group.getChildren.foreach({
      case t: Table =>
        t.debug()
        debug(t)
      case g: Group =>
        debug(g)
      case _ =>
    })
  }

  override protected def onRendered(): Unit = {
    Table.drawDebug(stage)
  }
}
