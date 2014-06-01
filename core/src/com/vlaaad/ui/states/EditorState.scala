package com.vlaaad.ui.states

import com.vlaaad.ui.app.AppState
import com.badlogic.gdx.assets.AssetManager
import com.vlaaad.ui.UiLayout
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.{Layout, TiledDrawable, ChangeListener}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import javax.swing.JFileChooser
import java.io.File
import javax.swing.filechooser.FileFilter
import com.badlogic.gdx.files.FileHandle
import collection.convert.wrapAll._
import com.badlogic.gdx.Gdx
import com.vlaaad.ui.models.{Collection, Wrapper, Leaf, ActorModel}
import com.badlogic.gdx.utils.ObjectMap
import scala.collection.mutable.ListBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.{Color, GL20}
import com.vlaaad.ui.view.WorkSpace


/** Created 29.05.14 by vlaaad */
class EditorState(val assets: AssetManager) extends AppState {
  var workspaceContainer: Container = _
  var main: Table = _
  var treeContainer: Container = _
  var skin: Skin = _
  var tree: Tree = _
  var layout: UiLayout = _
  val workspace: WorkSpace = new WorkSpace()
  val renderer = new ShapeRenderer()
  var model: ActorModel = _
  val tmp1 = new Vector2()
  val tmp2 = new Vector2()
  val tmp3 = new Vector2()
  val tmp4 = new Vector2()

  override protected def onEntered(): Unit = {
    val layout = assets.get("ui.layout", classOf[UiLayout])
    skin = layout.skin
    stage.addActor(layout.getActor)
    main = layout.get(classOf[Table])
    main.debug()
    treeContainer = layout.get(classOf[Container], "tree")
    workspaceContainer = layout.get(classOf[Container], "workspace")
    workspaceContainer.setBackground(new TiledDrawable(skin.getRegion("workspace-background")))
    workspaceContainer.setWidget(workspace)
    layout.get(classOf[Button], "open").addListener(new ChangeListener {
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
    val params = new ObjectMap[Object, ObjectMap[String, Object]]()
    layout = new UiLayout(file, skin, params)
    workspace.setWidget(layout.getActor)
    model = buildModel(layout.getActor)
    tree = new Tree(skin)
    tree.add(createTree(model))
    tree.expandAll()
    treeContainer.setWidget(tree)
  }

  def buildModel(actor: Actor): ActorModel = {
    actor match {
      case label: Label => new Leaf(label)
      case tb: TextButton => new Leaf(tb)
      case c: Container => new Wrapper(c, if (c.getWidget == null) None else Some(buildModel(c.getWidget)))
      case t: Group =>
        val buf = ListBuffer[ActorModel]()
        t.getChildren.map(buildModel).foreach(buf += _)
        new Collection(t, buf)
      case any => new Leaf(any)
    }
  }

  def createTree(model: ActorModel): Tree.Node = {
    val t = new Table(skin)
    t.add(model.actor.toString).padRight(2)
    t.add(model.actor.getClass.getSimpleName, "hint")
    val node = new Tree.Node(t)
    node.setObject(model)
    model match {
      case leaf: Leaf =>
      case wrapper: Wrapper =>
        if (wrapper.model.isDefined)
          node.add(createTree(wrapper.model.get))
      case coll: Collection =>
        coll.models.foreach(v => node.add(createTree(v)))
    }
    node
  }

  override protected def onRendered(): Unit = {
    if (model != null) {
      renderer.setProjectionMatrix(stage.getBatch.getProjectionMatrix)
      renderer.setTransformMatrix(stage.getBatch.getTransformMatrix)
      renderer.begin(ShapeRenderer.ShapeType.Line)
      Gdx.gl.glEnable(GL20.GL_BLEND)
      drawDebug(model)
      Gdx.gl.glDisable(GL20.GL_BLEND)
      renderer.end()
    }
    Table.drawDebug(stage)
  }


  def drawDebug(model: ActorModel): Unit = {
    val actor = model.actor
    val bottomLeft = actor.localToStageCoordinates(tmp1.set(0, 0))
    val bottomRight = actor.localToStageCoordinates(tmp2.set(actor.getWidth, 0))
    val topLeft = actor.localToStageCoordinates(tmp3.set(0, actor.getHeight))
    val topRight = actor.localToStageCoordinates(tmp4.set(actor.getWidth, actor.getHeight))
    if (tree.getSelection.first() != null && model == tree.getSelection.first().getObject) {
      renderer.setColor(Color.WHITE)
      Gdx.gl.glLineWidth(6)
    } else if (tree.getOverNode != null && model == tree.getOverNode.getObject) {
      renderer.setColor(Color.GRAY)
      Gdx.gl.glLineWidth(4)
    } else {
      renderer.setColor(Color.BLACK)
      Gdx.gl.glLineWidth(2)
    }
    renderer.line(bottomLeft, bottomRight)
    renderer.line(bottomLeft, topLeft)
    renderer.line(topLeft, topRight)
    renderer.line(bottomRight, topRight)
    renderer.flush()
    Gdx.gl.glLineWidth(1)

    model match {
      case w: Wrapper =>
        w.model.foreach(drawDebug)
      case l: Leaf =>
      case c: Collection =>
        c.models.foreach(drawDebug)
    }
  }
}
