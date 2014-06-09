package com.vlaaad.ui.util.inputs.factories;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vlaaad.ui.util.inputs.EditorInput;

/**
 * Created 02.06.14 by vlaaad
 */
public abstract class EditorInputFactory<T> {
    public abstract EditorInput<T> create(boolean required, boolean isDefault, T initialValue, Skin layoutSkin, Skin editorSkin);
}
