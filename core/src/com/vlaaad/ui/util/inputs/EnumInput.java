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

    public EnumInput(final T[] enumConstants, T initialValue, Skin editorSkin) {
        super(initialValue);
        selectBox = new SelectBox<String>(editorSkin);
        Array<String> items = new Array<String>();
        items.add("---");
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
                    T result = null;
                    for (T t : enumConstants) {
                        if (selectBox.getSelected().equals(t.toString())) {
                            result = t;
                            break;
                        }
                    }
                    dispatcher.setState(result);
                }
            }
        });
    }

    @Override public Actor getActor() {
        return selectBox;
    }
}
