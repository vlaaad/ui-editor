package com.vlaaad.ui.util.inputs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created 03.06.14 by vlaaad
 */
public class EnumInput<T> extends EditorInput<T> {

    private final SelectBox<String> selectBox;
    private final T[] enumConstants;

    public EnumInput(boolean required, final T[] enumConstants, T initialValue, Skin editorSkin) {
        super(initialValue);
        this.enumConstants = enumConstants;
        selectBox = new SelectBox<String>(editorSkin, required ? "required" : "default");
        Array<String> items = new Array<String>();
        if (!required) items.add("---");
        for (T t : enumConstants) {
            items.add(t.toString());
        }
        selectBox.setItems(items);
        selectBox.setSelected(initialValue == null ? "---" : initialValue.toString());
        selectBox.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (selectBox.getSelected().equals("---")) {
                    dispatcher.setState(null);
                } else {
                    T result = valueOf(selectBox.getSelected());
                    dispatcher.setState(result);
                }
            }
        });
    }
    private T valueOf(String o) {
        for (T t : enumConstants) {
            if (o.equals(t.toString())) {
                return t;
            }
        }
        return null;
    }

    @Override public Actor getActor() {
        return selectBox;
    }

    @Override public void update(Object value) {
        if (value instanceof String) {
            T v = valueOf((String) value);
            if (v != null) {
                dispatcher.setState(v);
                selectBox.setSelected(v.toString());
            }
        }
    }
}
