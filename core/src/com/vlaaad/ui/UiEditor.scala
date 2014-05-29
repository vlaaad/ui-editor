package com.vlaaad.ui

import com.badlogic.gdx.assets.AssetManager
import com.vlaaad.ui.app.AppController
import com.vlaaad.ui.states.{EditorState, LoadResourcesState}
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class UiEditor extends AppController {
  def create() {
    val assets: AssetManager = new AssetManager
    setState(new LoadResourcesState(assets, () => {
      setState(new EditorState(assets))
    }))
  }
}