package com.vlaaad.ui.util.inputs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created 03.06.14 by vlaaad
 */
public class EmptyInput<T> extends EditorInput<T> {

    private final Label label;

    public EmptyInput(T initialValue, Skin editorSkin) {
        super(initialValue);
        label = new Label(initialValue == null ? "" : String.valueOf(initialValue), editorSkin);
    }

    @Override public Actor getActor() {
        return label;
    }
}
