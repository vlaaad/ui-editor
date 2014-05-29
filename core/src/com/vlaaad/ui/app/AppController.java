package com.vlaaad.ui.app;

import com.badlogic.gdx.ApplicationListener;

/**
 * Created 28.05.14 by vlaaad
 */
public abstract class AppController implements ApplicationListener {

    private AppState state;

    @Override public final void resize(int width, int height) {
        if (state != null) state.resize(width, height);
    }

    @Override public final void render() {
        if (state != null) state.render();
    }

    @Override public final void pause() {
        if (state != null) state.pause();
    }

    @Override public final void resume() {
        if (state != null) state.resume();
    }

    @Override public void dispose() {
        if (state != null) state.exit();
    }

    public AppState getState() {
        return state;
    }

    public void setState(AppState state) {
        if (this.state != null) {
            this.state.exit();
        }
        this.state = state;
        if (this.state != null) {
            this.state.enter();
        }
    }
}
