package com.vlaaad.ui.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.{ProgressBar, Skin}
import com.vlaaad.ui.UiLayout
import com.vlaaad.ui.app.AppState
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.vlaaad.ui.loaders.UiLayoutLoader

/**
 * Created 28.05.14 by vlaaad
 */
class LoadResourcesState(val assets: AssetManager, val callback: () => Unit) extends AppState {

  var loaded = false
  var progress: ProgressBar = _

  protected override def onEntered() {
    val layout: UiLayout = new UiLayout(Gdx.files.internal("loader.layout"))
    stage.addActor(layout.getActor)
    progress = layout.get(classOf[ProgressBar], "progress")

    val resolver = new InternalFileHandleResolver
    assets.setLoader(classOf[UiLayout], new UiLayoutLoader(resolver))
    assets.load("ui.layout", classOf[UiLayout])
  }

  protected override def onRendered() {
    if (!loaded) {
      loaded = assets.update()
      progress.setValue(progress.getMaxValue * assets.getProgress)
      if (loaded) Gdx.app.postRunnable(new Runnable {
        override def run(): Unit = callback()
      })
    }
  }
}