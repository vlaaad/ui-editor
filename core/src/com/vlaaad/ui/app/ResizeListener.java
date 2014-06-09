package com.vlaaad.ui.app;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;


/**
 * Created 09.06.14 by vlaaad
 */
public abstract class ResizeListener implements EventListener {


    @Override public boolean handle(Event event) {
        if (event instanceof ResizeEvent) {
            resize();
            return true;
        }
        return false;
    }
    public abstract void resize();

    public static class ResizeEvent extends Event {

        public int previousWidth;
        public int previousHeight;
        public int newWidth;
        public int newHeight;

        public ResizeEvent setup(int previousWidth, int previousHeight, int newWidth, int newHeight) {
            this.previousWidth = previousWidth;
            this.previousHeight = previousHeight;
            this.newWidth = newWidth;
            this.newHeight = newHeight;
            return this;
        }
    }
}
