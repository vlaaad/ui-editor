package com.vlaaad.ui.states

import com.vlaaad.ui.app.AppState
import com.badlogic.gdx.assets.AssetManager
import com.vlaaad.ui.UiLayout
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.{Align, Layout, TiledDrawable, ChangeListener}
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.{Group, Actor}
import javax.swing.JFileChooser
import java.io.File
import javax.swing.filechooser.FileFilter
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.graphics.{Color, GL20}
import com.vlaaad.ui.view.WorkSpace
import com.vlaaad.ui.util._
import collection.convert.wrapAsScala._
import com.vlaaad.ui.util.inputs.EditorInput
import com.vlaaad.ui.util.IStateDispatcher.Listener


/** Created 29.05.14 by vlaaad */
class EditorState(val assets: AssetManager) extends AppState {
  var main: Table = _
  var workspaceContainer: Container = _
  var treeContainer: Container = _
  var paramsContainer: Container = _
  var editorSkin: Skin = _
  var layoutSkin: Skin = _
  var tree: Tree = _
  var layout: UiLayout = _
  val workspace: WorkSpace = new WorkSpace()
  val renderer = new ShapeRenderer()
  var model: EditorModel[_] = _
  var currentFile: FileHandle = _
  val tmp1 = new Vector2()
  val tmp2 = new Vector2()
  val tmp3 = new Vector2()
  val tmp4 = new Vector2()

  override protected def onEntered(): Unit = {
    val layout = assets.get("ui.layout", classOf[UiLayout])
    editorSkin = layout.skin
    stage.addActor(layout.getActor)
    main = layout.get(classOf[Table])
    main.debug()
    treeContainer = layout.get(classOf[Container], "content", "panel", "tree")
    workspaceContainer = layout.get(classOf[Container], "content", "workspace")
    workspaceContainer.setBackground(new TiledDrawable(editorSkin.getRegion("workspace-background")))
    workspaceContainer.setWidget(workspace)
    paramsContainer = layout.find[Container]("params")
    layout.get(classOf[Button], "top-panel", "open").addListener(new ChangeListener {
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
    layout.get(classOf[Button], "top-panel", "save").addListener(new ChangeListener {
      override def changed(event: ChangeEvent, actor: Actor): Unit = {
        currentFile.writeString(EditorToolkit.dump(model, layoutSkin), false)
      }
    })
  }

  def open(file: FileHandle) = {
    currentFile = file
    val params = new ObjectMap[Object, ObjectMap[String, Object]]()
    layout = new UiLayout(file, editorSkin, params)
    layoutSkin = layout.skin
    workspace.setWidget(layout.getActor)
    model = buildModel(layout.getActor, params)
    tree = new Tree(editorSkin)
    tree.add(createNode(model))
    tree.expandAll()
    tree.getSelection.setMultiple(false)
    tree.addListener(new ChangeListener {
      override def changed(event: ChangeEvent, actor: Actor): Unit = {
        Option(tree.getSelection.first).map(_.getObject) match {
          case Some(v: EditorModel[_]) => showParams(v.asInstanceOf[EditorModel[AnyRef]])
          case Some(v) => hideParams()
          case None => hideParams()
        }
      }
    })
    treeContainer.setWidget(tree)
  }

  def buildModel(obj: Object, params: ObjectMap[Object, ObjectMap[String, Object]]): EditorModel[_] = {
    EditorToolkit.createModel(obj, params)
  }

  def createNode(model: EditorModel[_]): Tree.Node = {
    val t = new Table(editorSkin)
    model.obj match {
      case a: Actor =>
        t.add(model.obj.toString).padRight(2)
        t.add(model.obj.getClass.getSimpleName, "hint")
      case any => t.add(any.getClass.getSimpleName, "hint")
    }
    val node = new Tree.Node(t)
    node.setObject(model)
    model match {
      case leaf: Element[_] => println(s"is leaf! $leaf");
      case wrapper: Wrapper[_, _] =>
        Option(wrapper.wrapped).foreach(v => {
          node.add(createNode(v))
        })
      case coll: Collection[_, _] =>
        coll.elements.foreach(v => {
          node.add(createNode(v))
        })
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
  }

  def showParams(model: EditorModel[AnyRef]): Unit = {
    val table = new Table()
    table.columnDefaults(0).width(100).expandX().fillX()
    table.columnDefaults(1).width(100).expandX().fillX()
    Toolkit.getAppliers(model.obj).foreach(v => {
      val label = new Label(v.key, editorSkin)
      label.setAlignment(Align.right)
      table.add(label).align(Align.right).padRight(2)
      val input = EditorToolkit.createInput[AnyRef](model.params.get(v.key), v.value.valueClass.asInstanceOf[Class[AnyRef]], layoutSkin, editorSkin)
      initInput(model, v.key, input)
      table.add(input.getActor).row()
    })
    paramsContainer.setWidget(table)
  }

  def initInput(model: EditorModel[AnyRef], key: String, input: EditorInput[_]): Unit = {
    input.getDispatcher.asInstanceOf[IStateDispatcher[AnyRef]].addListener(false, new Listener[AnyRef] {
      override def onChangedState(newState: AnyRef): Unit = {
        val applier = Toolkit.applier(model.obj.getClass.asInstanceOf[Class[AnyRef]], key).asInstanceOf[Applier[AnyRef, AnyRef]]
        Option(newState) match {
          case Some(value) =>
            applier.apply(model.obj, value)
            invalidate(layout.getActor)
            model.params.put(key, value)
          case None =>
            applier.applyDefault(model.obj, layoutSkin)
            invalidate(layout.getActor)
            model.params.remove(key)
        }

      }
    })
  }

  def hideParams(): Unit = {
    paramsContainer.setWidget(null)
  }

  def invalidate(actor: Actor): Unit = {
    actor match {
      case l: Layout => l.invalidate()
      case _ =>
    }
    actor match {
      case g: Group => g.getChildren.foreach(invalidate)
      case _ =>
    }
  }


  def drawDebug(model: EditorModel[_]): Unit = {
    val actor = model.obj
    actor match {
      case a: Actor =>
        val bottomLeft = a.localToStageCoordinates(tmp1.set(0, 0))
        val bottomRight = a.localToStageCoordinates(tmp2.set(a.getWidth, 0))
        val topLeft = a.localToStageCoordinates(tmp3.set(0, a.getHeight))
        val topRight = a.localToStageCoordinates(tmp4.set(a.getWidth, a.getHeight))
        if (tree.getSelection.first() != null && model == tree.getSelection.first().getObject) {
          renderer.setColor(Color.WHITE)
          Gdx.gl.glLineWidth(3)
        } else if (tree.getOverNode != null && model == tree.getOverNode.getObject) {
          renderer.setColor(Color.GRAY)
          Gdx.gl.glLineWidth(2)
        } else {
          renderer.setColor(Color.BLACK)
          Gdx.gl.glLineWidth(1)
        }
        renderer.line(bottomLeft, bottomRight)
        renderer.line(bottomLeft, topLeft)
        renderer.line(topLeft, topRight)
        renderer.line(bottomRight, topRight)
        renderer.flush()
        Gdx.gl.glLineWidth(1)
      case _ =>
    }

    model match {
      case w: Wrapper[_, _] =>
        Option(w.wrapped) foreach drawDebug
      case l: Element[_] =>
      case c: Collection[_, _] =>
        c.elements foreach drawDebug
    }
  }
}
