package com.vlaaad.ui.states

import com.vlaaad.ui.app.AppState
import com.badlogic.gdx.assets.AssetManager
import com.vlaaad.ui.UiLayout
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils._
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d._
import javax.swing.JFileChooser
import java.io.File
import javax.swing.filechooser.FileFilter
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.{Input, Gdx}
import com.badlogic.gdx.utils.{JsonReader, ObjectMap}
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.vlaaad.ui.view.{CreateWindow, WorkSpace}
import com.vlaaad.ui.util._
import collection.convert.wrapAsScala._
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.{Source, Payload}
import scala.Some
import com.vlaaad.ui.scene2d.HorizontalList
import com.vlaaad.ui.util.inputs.EditorInput
import com.vlaaad.ui.util.IStateDispatcher.Listener


/** Created 29.05.14 by vlaaad */
class EditorState(val assets: AssetManager) extends AppState {
  var workspaceContainer: Container = _
  var treeContainer: Container = _
  var paramsContainer: Container = _
  var tools: HorizontalList = _
  var editorSkin: Skin = _
  var layoutSkin: Skin = _
  var root: Actor = _
  var tree: Tree = _
  var params: ObjectMap[Object, ObjectMap[String, Object]] = _
  val workspace: WorkSpace = new WorkSpace()
  val renderer = new ShapeRenderer()
  var model: EditorModel = _
  var currentFile: FileHandle = _

  override protected def onEntered(): Unit = {
    val layout = assets.get("ui.layout", classOf[UiLayout])
    editorSkin = layout.skin
    stage.addActor(layout.getActor)
    treeContainer = layout.find[Container]("tree")
    workspaceContainer = layout.get(classOf[Container], "content", "workspace")
    workspaceContainer.setBackground(new TiledDrawable(editorSkin.getRegion("workspace-background")))
    workspaceContainer.setWidget(workspace)
    paramsContainer = layout.find[Container]("params")
    tools = layout.find[HorizontalList]("tools")
    layout.find[Button]("open").addListener(() => {
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
    })
    layout.find[Button]("save").addListener(() => currentFile.writeString(EditorToolkit.dump(model, layoutSkin), false))
    layout.find[Button]("new").addListener(() => {
      println("new!")
    })
    stage.addListener(new InputListener {

      override def keyUp(event: InputEvent, keycode: Int): Boolean = {
        if (keycode == Input.Keys.FORWARD_DEL) {
          Option(tree).map(_.getSelection.getLastSelected).filter(v => v != null && v.getParent != null).foreach(v => {
            val parent = v.getParent.getObject.asInstanceOf[EditorModel]
            val model = v.getObject.asInstanceOf[EditorModel]
            withRebuildTree {
              parent match {
                case c: Collection => c.remove(model)
                case w: Wrapper => w.remove(model)
                case _ =>
              }
            }
          })
        }
        true
      }
    })
  }

  def open(file: FileHandle) = {
    currentFile = file
    params = new ObjectMap[Object, ObjectMap[String, Object]]()
    val layout = new UiLayout(file, editorSkin, params)
    root = layout.getActor
    layoutSkin = layout.skin
    workspace.setWidget(root)
    model = buildModel(root, params)
    tree = new Tree(editorSkin)
    tree.setIconSpacing(1, 1)
    tree.setYSpacing(2)
    tree.add(createNode(model))
    tree.expandAll()
    tree.getSelection.setMultiple(false)
    tree.addListener(new ChangeListener {
      override def changed(event: ChangeEvent, actor: Actor): Unit = {
        Option(tree.getSelection.first).map(_.getObject) match {
          case Some(v: EditorModel) => showParams(v)
          case Some(v) => hideParams()
          case None => hideParams()
        }
      }
    })
    treeContainer.setWidget(tree)
    initDragAndDrop(tree)
  }

  def initDragAndDrop(tree: Tree): Unit = {
    val dnd = new DragAndDrop()
    for (node <- tree.getNodes)
      initDragAndDrop(node, dnd)
  }

  def initDragAndDrop(node: Tree.Node, dragAndDrop: DragAndDrop): Unit = {
    for (child <- node.getChildren) initDragAndDrop(child, dragAndDrop)

    val model = node.getObject.asInstanceOf[EditorModel]

    if (node.getParent == null) return

    dragAndDrop.addSource(new DragAndDrop.Source(node.getActor) {

      override def dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): Payload = {
        for (listener <- node.getActor.getCaptureListeners) {
          stage.cancelTouchFocus(listener, node.getActor)
        }
        def createStack(back: String) = {
          val stack = new Stack
          stack.add(new Image(editorSkin, back))
          stack.add(createView(model))
          stack.pack()
          stack
        }
        val payload = new Payload()
        payload.setDragActor(createStack("dragged-target-background"))
        payload.setInvalidDragActor(createStack("invalid-target-background"))
        payload.setValidDragActor(createStack("valid-target-background"))
        payload.setObject(node)
        payload
      }

    })
    dragAndDrop.addTarget(new DragAndDrop.Target(node.getActor) {

      override def drop(source: Source, payload: Payload, x: Float, y: Float, pointer: Int): Unit = {
        val from = payload.getObject.asInstanceOf[Tree.Node]
        val fromModel = from.getObject.asInstanceOf[EditorModel]
        val parentModel = from.getParent.getObject.asInstanceOf[EditorModel]
        withRebuildTree {
          parentModel match {
            case c: Collection => c.remove(fromModel)
            case w: Wrapper => w.remove(fromModel)
            case _ =>
          }
          model match {
            case c: Collection => c.add(fromModel)
            case w: Wrapper => w.setWidget(fromModel)
            case _ =>
          }
        }
      }

      override def drag(source: Source, payload: Payload, x: Float, y: Float, pointer: Int): Boolean = {
        val from = payload.getObject.asInstanceOf[Tree.Node]
        val fromModel = from.getObject.asInstanceOf[EditorModel]
        if (from.getParent == null) {
          false
        } else {
          val parentModel = from.getParent.getObject.asInstanceOf[EditorModel]
          if (parentModel == null) {
            false
          } else {
            model match {
              case c: Collection => c.accepts(fromModel)
              case w: Wrapper => w.accepts(fromModel)
              case _ => false
            }
          }
        }
      }
    })
  }

  def withRebuildTree[R](block: => R): R = {
    val arr = new com.badlogic.gdx.utils.Array[AnyRef]()
    tree.findExpandedObjects(arr)
    val returned = block
    val dumped = EditorToolkit.dump(model, layoutSkin)
    params = new ObjectMap[Object, ObjectMap[String, Object]]()
    root = Toolkit.instantiate(new JsonReader().parse(dumped), layoutSkin, params)
    model = buildModel(root, params)
    workspace.setWidget(root)
    tree.clearChildren()
    tree.add(createNode(model))
    tree.expandAll()
    tree.restoreExpandedObjects(arr)
    initDragAndDrop(tree)
    returned
  }

  def buildModel(obj: Object, params: ObjectMap[Object, ObjectMap[String, Object]]): EditorModel = EditorToolkit.createModel(obj, params)

  def createView(model: EditorModel) = {
    val t = new Table(editorSkin)
    t.defaults().padTop(-5).padBottom(-4)
    t.setTouchable(Touchable.enabled)
    model.obj match {
      case a: Actor =>
        t.add(model.obj.toString).padRight(2).padLeft(2)
        t.add(model.obj.getClass.getSimpleName, "hint")
      case any => t.add(any.getClass.getSimpleName, "hint")
    }
    val c = new Container(t)
    c.minWidth(100)
    c
  }

  def createNode(model: EditorModel): Tree.Node = {
    val node = new Tree.Node(createView(model))
    node.setObject(model)
    model match {
      case leaf: Element =>
      case wrapper: Wrapper =>
        Option(wrapper.wrapped).foreach(v => {
          node.add(createNode(v))
        })
      case coll: Collection =>
        coll.elements.foreach(v => {
          node.add(createNode(v))
        })
    }
    node
  }

  def showParams(model: EditorModel): Unit = {
    val table = new Table()
    table.columnDefaults(0).width(100).expandX().fillX()
    table.columnDefaults(1).width(100).expandX().fillX()
    Toolkit.getAppliers(model.obj).foreach(v => {
      val label = new Label(v.key, editorSkin, "hint")
      label.setAlignment(Align.right)
      table.add(label).align(Align.right).padRight(5)
      val required = model.requirements.contains(v.key)
      val applier = Toolkit.applier(model.obj.getClass, v.key).asInstanceOf[Applier[AnyRef, AnyRef]]
      val (initial, isDefault) = if (model.params.containsKey(v.key))
        model.params.get(v.key) -> false
      else
        applier.getDefaultValue(model.obj, layoutSkin) -> true

      val input = EditorToolkit.createInput[AnyRef](required, isDefault, initial, v.value.valueClass.asInstanceOf[Class[AnyRef]], layoutSkin, editorSkin)
      initInput(model, v.key, input)
      table.add(input.getActor).padBottom(1).row()
    })
    paramsContainer.setWidget(table)

    tools.clearChildren()

    model match {
      case c: Collection =>
        tools.add("Add ")
        tools.add(createInstantiatorActor(c.elementType, m => {
          withRebuildTree(c.add(m))
        }))
      case w: Wrapper =>
        tools.add("Set ")
        tools.add(createInstantiatorActor(w.elementType.asInstanceOf[Class[AnyRef]], m => {
          withRebuildTree(w.setWidget(m))
        }))
      case _ =>
    }
  }

  def createInstantiatorActor(kind: Class[AnyRef], f: (EditorModel) => Unit): Actor = {
    def inst(i: Instantiator[AnyRef]) = {
      def make(res: Resources) = {
        try {
          val model = buildModel(i.newInstance(res), params)
          res.data.foreach(e => model.params.put(e.key, e.value))
          f(model)
        } catch {
          case t: Throwable =>
            println(s"failed to instantiate ${i.objectClass.getSimpleName}: ${t.getMessage}")
            t.printStackTrace()
        }
      }
      if (i.requirements.size == 0) {
        make(new Resources)
      } else {
        stage.addActor(new CreateWindow(i, editorSkin, layoutSkin, r => make(r)))
      }
    }

    val instantiators = Toolkit.getInstantiators(kind).asInstanceOf[GdxArray[Instantiator[AnyRef]]]
    if (instantiators.size == 0) {
      instantiators.add(new Instantiator[AnyRef] {
        override protected def init(): Unit = objectClass = kind

        override def newInstance(resources: Resources): AnyRef = kind.newInstance()
      })
    }

    if (instantiators.size == 1) {
      val b = new TextButton(instantiators.first().objectClass.getSimpleName, editorSkin)
      b.addListener(() => inst(instantiators.first()))
      b
    } else {
      val s = new SelectBox[String](editorSkin)
      val map = instantiators.map(i => i.objectClass.getSimpleName -> i).toMap
      s.setItems(map.keys)
      s.addListener(() => inst(map(s.getSelected)))
      s
    }

  }

  def initInput(model: EditorModel, key: String, input: EditorInput[_]): Unit = {
    input.getDispatcher.asInstanceOf[IStateDispatcher[AnyRef]].addListener(false, new Listener[AnyRef] {
      override def onChangedState(newState: AnyRef): Unit = {
        val applier = Toolkit.applier(model.obj.getClass.asInstanceOf[Class[AnyRef]], key).asInstanceOf[Applier[AnyRef, AnyRef]]
        Option(newState) match {
          case Some(value) =>
            val default = applier.getDefaultValue(model.obj, layoutSkin)
            if (default != null && default == value) {
              applier.applyDefault(model.obj, layoutSkin)
              invalidate(root)
              model.params.remove(key)
            } else {
              applier.apply(model.obj, value)
              invalidate(root)
              model.params.put(key, value)
            }
          case None =>
            applier.applyDefault(model.obj, layoutSkin)
            invalidate(root)
            model.params.remove(key)
        }

      }
    })
  }

  def hideParams(): Unit = {
    paramsContainer.setWidget(null)
    tools.clearChildren()
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


  override protected def onExited(): Unit = {
    editorSkin.dispose()
    Option(layoutSkin).foreach(_.dispose())
  }

  override protected def onRendered(): Unit = {
    if (model != null) {
      DrawHelper.drawModel(model, renderer, tree)
    }
  }
}
