package com.vlaaad.ui.states

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.{MathUtils, Vector2}
import com.badlogic.gdx.scenes.scene2d._
import com.badlogic.gdx.scenes.scene2d.ui._
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.{Payload, Source}
import com.badlogic.gdx.scenes.scene2d.utils._
import com.badlogic.gdx.utils.{JsonReader, ObjectMap}
import com.badlogic.gdx.{Gdx, Input}
import com.vlaaad.ui.UiLayout
import com.vlaaad.ui.app.{AppState, ResizeListener}
import com.vlaaad.ui.scene2d.HorizontalList
import com.vlaaad.ui.util.IStateDispatcher.Listener
import com.vlaaad.ui.util._
import com.vlaaad.ui.util.inputs.EditorInput
import com.vlaaad.ui.view.{CreateLayoutWindow, CreateModelWindow, WorkSpace}

import scala.collection.convert.wrapAsScala._
import scala.swing.FileChooser


/** Created 29.05.14 by vlaaad */
class EditorState(val assets: AssetManager) extends AppState {
  var workspaceContainer: Container[Actor] = _
  var treeContainer: Container[Actor] = _
  var paramsContainer: Container[Actor] = _
  var tools: HorizontalList = _
  var editorSkin: Skin = _
  var layoutSkin: Skin = _
  var root: Actor = _
  var tree: Tree = _
  var params: ObjectMap[Object, ObjectMap[String, Object]] = _
  val workspace: WorkSpace = new WorkSpace() {
    override def draw(batch: Batch, parentAlpha: Float): Unit = {
      super.draw(batch, parentAlpha)

    }
  }
  val renderer = new ShapeRenderer()
  var model: EditorModel = _
  var currentFile: FileHandle = _

  override protected def onEntered(): Unit = {
    val layout = assets.get("ui.layout", classOf[UiLayout])
    editorSkin = layout.skin
    stage.addActor(layout.getActor)
    treeContainer = layout.find[Container[Actor]]("tree")
    workspaceContainer = layout.find[Container[Actor]]("workspace")
    workspaceContainer.setBackground(new TiledDrawable(editorSkin.getRegion("workspace-background")))
    workspaceContainer.setActor(workspace)
    paramsContainer = layout.find[Container[Actor]]("params")
    tools = layout.find[HorizontalList]("tools")
    tree = new Tree(editorSkin)
    tree.setIconSpacing(1, 1)
    tree.setYSpacing(2)
    tree.getSelection.setMultiple(false)
    tree.addListener(() => {
      Option(tree.getSelection.first).map(_.getObject) match {
        case Some(v: EditorModel) => showParams(v)
        case Some(v) => hideParams()
        case None => hideParams()
      }
    })
    treeContainer.setActor(tree)
    layout.find[Button]("open").addListener(() => {
      val c = new FileChooser(new File("."))
      c.multiSelectionEnabled = false
      //      c.fileSelectionMode = FileChooser.SelectionMode.FilesAndDirectories
      c.peer.setAcceptAllFileFilterUsed(false)
      c.peer.setDialogType(JFileChooser.OPEN_DIALOG)
      c.fileFilter = new FileFilter {
        override def getDescription: String = "*.layout"

        override def accept(f: File): Boolean = f.getName.endsWith(".layout") || f.isDirectory
      }
      new Thread(() => {
        if (c.showOpenDialog(null) == FileChooser.Result.Approve) {
          Gdx.app.postRunnable(() => openLayout(new FileHandle(c.selectedFile)))
        }
      }).start()


    })
    layout.find[Button]("save").addListener(() => {
      Option(currentFile) match {
        case Some(f) =>
          currentFile.writeString(EditorToolkit.dump(model, layoutSkin), false)
        case None =>
          val c = new FileChooser(new File("."))
          c.multiSelectionEnabled = false
          //          c.fileSelectionMode = FileChooser.SelectionMode.FilesAndDirectories
          c.peer.setAcceptAllFileFilterUsed(false)
          c.peer.setDialogType(JFileChooser.SAVE_DIALOG)
          c.fileFilter = new FileFilter {
            override def getDescription: String = "*.layout"

            override def accept(f: File): Boolean = f.isDirectory || f.getName.endsWith(".layout")
          }
          new Thread(() => {
            if (c.showSaveDialog(null) == FileChooser.Result.Approve) {
              Gdx.app.postRunnable(() => {
                currentFile = new FileHandle(c.selectedFile)
                if (currentFile.extension() != "layout")
                  currentFile = currentFile.sibling(currentFile.nameWithoutExtension + ".layout")
                currentFile.writeString(EditorToolkit.dump(model, layoutSkin), false)
              })
            }
          }).start()
      }
      ()
    })
    layout.find[Button]("new").addListener(() => {
      openSkin(skinFile => {
        if (root != null) root.remove()
        currentFile = null
        if (layoutSkin != null) layoutSkin.dispose()
        layoutSkin = new Skin(skinFile)
        params = new ObjectMap[Object, ObjectMap[String, Object]]()
        stage.addActor(new CreateLayoutWindow(editorSkin, layoutSkin, EditorState.this, model => {
          withRebuildTree {
            this.model = model
          }
        }))
      })
    })
    stage.addListener(new ResizeListener {
      override def resize(): Unit = Option(root).foreach(invalidate)
    })
    stage.addListener(new InputListener {

      override def keyUp(event: InputEvent, keycode: Int): Boolean = {
        if (keycode == Input.Keys.FORWARD_DEL && stage.getKeyboardFocus == null) {
          Option(tree).map(_.getSelection.first()).filter(v => v != null && v.getParent != null).foreach(v => {
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
    stage.addListener(new DragListener() {
      setTapSquareSize(1f)
      val initial = new Vector2()
      val current = new Vector2()
      val delta = new Vector2()
      var m: Option[EditorModel] = None
      var left = false
      var right = false
      var top = false
      var bottom = false

      override def dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): Unit = {
        val a = stage.hit(event.getStageX, event.getStageY, false)
        m = a match {
          case f if f.isDescendantOf(workspace) =>
            findModel(f)
          case _ => None
        }
        m match {
          case None => cancel()
          case Some(v) =>
            showParams(v)
            val target = v.obj.asInstanceOf[Actor]
            target.stageToLocalCoordinates(initial.set(event.getStageX, event.getStageY))
            left = initial.x < Math.min(target.getWidth / 4, 20)
            right = initial.x > Math.max(target.getWidth * 3 / 4, target.getWidth - 20)
            bottom = initial.y < Math.min(target.getHeight / 4, 20)
            top = initial.y > Math.max(target.getHeight * 3 / 4, target.getHeight - 20)
            target.getParent.stageToLocalCoordinates(initial.set(event.getStageX, event.getStageY))
        }
      }

      override def drag(event: InputEvent, x: Float, y: Float, pointer: Int): Unit = {
        val target = m.get.obj.asInstanceOf[Actor]
        target.getParent.stageToLocalCoordinates(current.set(event.getStageX, event.getStageY))
        delta.set(current).sub(initial)
        val isCorner = (top || bottom) && (left || right)

        if (left || right) {
          target.sizeBy(MathUtils.round(if (left) -delta.x else delta.x), 0)
          inputMap("width").update(target.getWidth.asInstanceOf[AnyRef])
        }
        if (top || bottom) {
          target.sizeBy(0, MathUtils.round(if (bottom) -delta.y else delta.y))
          inputMap("height").update(target.getHeight.asInstanceOf[AnyRef])
        }
        if (!top && !bottom || isCorner) {

          target.moveBy(MathUtils.round(if (right) 0 else delta.x), 0)
          inputMap("x").update(target.getX.asInstanceOf[AnyRef])
        }
        if (!left && !right || isCorner) {
          target.moveBy(0, MathUtils.round(if (top) 0 else delta.y))
          inputMap("y").update(target.getY.asInstanceOf[AnyRef])
        }

        invalidate(root)
        initial.set(current)
      }

      override def dragStop(event: InputEvent, x: Float, y: Float, pointer: Int): Unit = super.dragStop(event, x, y, pointer)
    })
    stage.addListener(new InputListener() {
      override def mouseMoved(event: InputEvent, x: Float, y: Float): Boolean = {
        val a = stage.hit(event.getStageX, event.getStageY, false)
        a match {
          case null =>
          case fitting if fitting.isDescendantOf(workspaceContainer) =>
            findModel(a).foreach(v => tree.setOverNode(tree.findNode(v)))
          case _ =>
        }
        true
      }
    })
    stage.addListener(new ActorGestureListener() {
      override def tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int): Unit = {
        val a = stage.hit(event.getStageX, event.getStageY, false)
        a match {
          case fitting if fitting.isDescendantOf(workspaceContainer) =>
            findModel(a).foreach(v => {
              Option(tree.findNode(v)).foreach(found => tree.getSelection.set(found))
            })
          case _ =>
        }
      }
    })
  }

  def findModel(actor: Actor): Option[EditorModel] = {
    def findForActor(m: EditorModel, a: Actor): Option[EditorModel] = {
      m match {
        case null => None
        case any if any.obj == a => Some(any)
        case w: Wrapper => findForActor(w.wrapped, a)
        case c: Collection => c.elements.map(v => findForActor(v, a)).filter(_.isDefined).map(_.get).headOption
        case _ => None
      }
    }
    var res: Option[EditorModel] = None
    var cur = actor
    while (res.isEmpty && cur != null) {
      res = findForActor(model, cur)
      cur = cur.getParent
    }
    res
  }

  def openLayout(file: FileHandle) = {
    currentFile = file
    params = new ObjectMap[Object, ObjectMap[String, Object]]()
    val skinFile = file.sibling(s"${file.nameWithoutExtension}.json")
    if (skinFile.exists()) {
      load(file, skinFile)
    } else {
      openSkin(skinFile => load(file, skinFile))
    }
  }

  def openSkin(cb: FileHandle => Unit): Unit = {
    val c = new FileChooser(new File("."))
    c.multiSelectionEnabled = false
    //    c.fileSelectionMode = FileChooser.SelectionMode.FilesAndDirectories
    c.peer.setAcceptAllFileFilterUsed(false)
    c.peer.setDialogType(JFileChooser.OPEN_DIALOG)
    c.fileFilter = new FileFilter {
      override def getDescription: String = "*.json"
      override def accept(f: File): Boolean = f.isDirectory || f.getName.endsWith(".json")
    }
    new Thread(() => {
      if (c.showOpenDialog(null) == FileChooser.Result.Approve) {
        Gdx.app.postRunnable(() => cb(new FileHandle(c.selectedFile)))
      }
    }).start()
  }

  def load(layoutFile: FileHandle, skinFile: FileHandle) = {
    Option(layoutSkin).foreach(v => v.dispose())
    try {
      val layout = new UiLayout(layoutFile, new Skin(skinFile), params)
      root = layout.getActor
      layoutSkin = layout.skin
      workspace.setWidget(root)
      model = buildModel(root, params)
      tree.clearChildren()
      tree.add(createNode(model))
      tree.expandAll()
      initDragAndDrop(tree)
    } catch {
      case e: Throwable => println(s"failed to load layout because of $e")
    }
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
        val targetParentModel = if (parentIdx != -1) Some(node.getParent.getObject.asInstanceOf[EditorModel]) else None
        withRebuildTree {
          parentModel match {
            case c: Collection =>
              targetParentModel.foreach(v => {
                if (v == c && c.elements.indexOf(fromModel, true) < parentIdx) {
                  parentIdx = parentIdx - 1
                }
              })
              c.remove(fromModel)
            case w: Wrapper => w.remove(fromModel)
            case _ =>
          }
          if (parentIdx != -1) {
            val targetParentModel = node.getParent.getObject.asInstanceOf[EditorModel]
            targetParentModel.asInstanceOf[Collection].elements.insert(parentIdx, fromModel)
          } else {
            model match {
              case c: Collection => c.add(fromModel)
              case w: Wrapper => w.setWidget(fromModel)
              case _ =>
            }
          }
        }
      }


      override def reset(source: Source, payload: Payload): Unit = {
        delimiter.remove()
      }

      var parentIdx = -1
      val delimiter = new Image(editorSkin, "delimiter") {
        override def hit(x: Float, y: Float, touchable: Boolean): Actor = null
      }
      delimiter.setTouchable(Touchable.disabled)
      delimiter.setWidth(100)

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
            val targetParent = node.getParent
            if (targetParent != null) {
              val targetParentModel = targetParent.getObject.asInstanceOf[EditorModel]
              targetParentModel match {
                case c: Collection if c.accepts(fromModel) =>
                  val target = node.getActor
                  val part = target.getHeight / 3
                  if (y < part) {
                    parentIdx = c.elements.indexOf(model, true) + 1
                    delimiter.setPosition(target.getX, target.getY - 4)
                    target.getParent.addActor(delimiter)
                  } else if (y > target.getHeight - part) {
                    parentIdx = c.elements.indexOf(model, true)
                    delimiter.setPosition(target.getX, target.getY + target.getHeight - 2)
                    target.getParent.addActor(delimiter)
                  } else {
                    delimiter.remove()
                    parentIdx = -1
                  }
                case _ =>
                  delimiter.remove()
                  parentIdx = -1
              }
            }
            model match {
              case c: Collection => c.accepts(fromModel) || parentIdx != -1
              case w: Wrapper => w.accepts(fromModel) || parentIdx != -1
              case _ => parentIdx != -1
            }
          }
        }
      }
    }

    )
  }

  def withRebuildTree[R](block: => R): R = {
    val arr = new GdxArray[AnyRef]()
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
    t.defaults().padTop(-5).padBottom(-3)
    t.setTouchable(Touchable.enabled)
    model.obj match {
      case a: Actor =>
        val label = new Label(model.obj.toString, editorSkin)
        label.setName("name")
        t.add(label).padRight(2).padLeft(2)
        t.add(model.obj.getClass.getSimpleName, "hint")
      case any => t.add(any.getClass.getSimpleName, "hint")
    }
    val c = new Container(t)
    c.setTouchable(Touchable.enabled)
    c.minWidth(100)
    c.fillX()
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

  val inputMap = collection.mutable.Map[String, EditorInput[_]]()

  def showParams(model: EditorModel): Unit = {
    val table = new Table()
    table.columnDefaults(0).width(75).expandX().fillX()
    table.columnDefaults(1).width(150).expandX().fillX()
    inputMap.clear()
    Toolkit.orderedAppliers(model.obj.getClass).foreach(v => {
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
      inputMap += v.key -> input
      table.add(input.getActor).padBottom(1).row()
    })
    paramsContainer.setActor(table)

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
          i.value = new JsonReader().parse("{}")
          i.skin = editorSkin
          val model = buildModel(i.newInstance(res), params)
          i.value = null
          i.skin = null
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
        stage.addActor(new CreateModelWindow(i, editorSkin, layoutSkin, r => make(r)))
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
      s.addListener(() => {
        inst(map(s.getSelected))
      })
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
            val i = Toolkit.instantiator(model.obj.getClass)
            if (applier.defaultValueDefined && value == default && (i == null || !i.requirements.containsKey(key))) {
              model.params.remove(key)
            } else {
              model.params.put(key, value)
            }
            applier.apply(model.obj, value)
            invalidate(root)
          case None =>
            applier.applyDefault(model.obj, layoutSkin)
            invalidate(root)
            model.params.remove(key)
        }
        Option(tree.findNode(model)).map(v => v.getActor.asInstanceOf[Group].findActor[Label]("name")).foreach {
          case label: Label => label.setText(model.obj.toString)
          case _ =>
        }
      }
    })
  }

  def hideParams(): Unit = {
    paramsContainer.setActor(null)
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
