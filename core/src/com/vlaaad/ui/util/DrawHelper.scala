package com.vlaaad.ui.util

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.{Color, GL20}
import com.badlogic.gdx.scenes.scene2d.{Stage, Actor}
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import collection.convert.wrapAsScala._
import com.badlogic.gdx.math.Vector2

/** Created 06.06.14 by vlaaad */
object DrawHelper {


  private[this] val tmp1 = new Vector2()
  private[this] val tmp2 = new Vector2()
  private[this] val tmp3 = new Vector2()
  private[this] val tmp4 = new Vector2()

  object DrawMode extends Enumeration {
    val all, over, selected = Value
  }

  def drawModel(model: EditorModel[_], renderer: ShapeRenderer, tree: Tree) = {
    val stage: Stage = tree.getStage
    def drawDebug(model: EditorModel[_], mode: DrawMode.Value): Unit = {
      val actor = model.obj
      actor match {
        case a: Actor =>
          val bottomLeft = a.localToStageCoordinates(tmp1.set(0, 0))
          val bottomRight = a.localToStageCoordinates(tmp2.set(a.getWidth, 0))
          val topLeft = a.localToStageCoordinates(tmp3.set(0, a.getHeight))
          val topRight = a.localToStageCoordinates(tmp4.set(a.getWidth, a.getHeight))
          var draw = false
          if (tree.getSelection.first() != null && model == tree.getSelection.first().getObject) {
            renderer.setColor(Color.LIGHT_GRAY)
            Gdx.gl.glLineWidth(1)
            draw = mode == DrawMode.selected
          } else if (tree.getOverNode != null && model == tree.getOverNode.getObject) {
            renderer.setColor(Color.GRAY)
            Gdx.gl.glLineWidth(1)
            draw = mode == DrawMode.over
          } else {
            renderer.setColor(Color.BLACK)
            Gdx.gl.glLineWidth(1)
            draw = mode == DrawMode.all
          }
          if (draw) {
            renderer.line(bottomLeft, bottomRight)
            renderer.line(bottomLeft, topLeft)
            renderer.line(topLeft, topRight)
            renderer.line(bottomRight, topRight)
            renderer.flush()
          }
          Gdx.gl.glLineWidth(1)
        case _ =>
      }

      model match {
        case w: Wrapper[_, _] =>
          Option(w.wrapped).foreach(drawDebug(_, mode))
        case l: Element[_] =>
        case c: Collection[_, _] =>
          c.elements.foreach(drawDebug(_, mode))
      }
    }
    renderer.setProjectionMatrix(stage.getBatch.getProjectionMatrix)
    renderer.setTransformMatrix(stage.getBatch.getTransformMatrix)
    renderer.begin(ShapeRenderer.ShapeType.Line)
    Gdx.gl.glEnable(GL20.GL_BLEND)
    drawDebug(model, DrawMode.all)
    drawDebug(model, DrawMode.over)
    drawDebug(model, DrawMode.selected)
    Gdx.gl.glDisable(GL20.GL_BLEND)
    renderer.end()
  }
}
