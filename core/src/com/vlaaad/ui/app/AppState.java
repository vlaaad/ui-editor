package com.vlaaad.ui.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Created 28.05.14 by vlaaad
 */
public abstract class AppState {

    public final Stage stage = new Stage(new ScreenViewport());
    private boolean entered = false;
    private boolean resumed = false;
    private int w = -1;
    private int h = -1;


    void enter() {
        if (entered)
            return;
        entered = true;
        onEntered();
        resume();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.input.setInputProcessor(stage);
    }

    void exit() {
        if (!entered)
            return;
        pause();
        entered = false;
        onExited();
    }

    void resize(int w, int h) {
        if (this.w == w && this.h == h)
            return;
        this.w = w;
        this.h = h;
        stage.getViewport().update(w, h, true);
    }

    void resume() {
        if (resumed)
            return;
        resumed = true;
        onResumed();
    }

    void pause() {
        if (!resumed)
            return;
        resumed = false;
        onPaused();
    }

    protected void onEntered() {
    }

    protected void onResumed() {
    }

    protected void onPaused() {
    }

    protected void onExited() {
    }

    protected void onRendered() {

    }

    void render() {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        onRendered();
    }
}
