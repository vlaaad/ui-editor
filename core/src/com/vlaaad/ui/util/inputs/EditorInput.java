package com.vlaaad.ui.util.inputs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.vlaaad.ui.util.IStateDispatcher;
import com.vlaaad.ui.util.StateDispatcher;

/**
 * Created 03.06.14 by vlaaad
 */
public abstract class EditorInput<T> {
    protected final StateDispatcher<T> dispatcher;

    protected EditorInput(T initialValue) {
        this.dispatcher = new StateDispatcher<T>(initialValue);
    }

    public abstract Actor getActor();

    public final IStateDispatcher<T> getDispatcher() {
        return dispatcher;
    }

}
