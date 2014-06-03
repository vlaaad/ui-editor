package com.vlaaad.ui.util.inputs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created 04.06.14 by vlaaad
 */
public class SkinInput<T> extends EditorInput<T> {
    private final SelectBox<String> selectBox;

    public SkinInput(boolean required, final ObjectMap<String, T> resources, T initialValue, Skin editorSkin) {
        super(initialValue);
        selectBox = new SelectBox<String>(editorSkin, required ? "required" : "default");
        Array<String> items = new Array<String>();
        if (!required) items.add("---");
        for (String t : resources.keys()) {
            items.add(t);
        }
        selectBox.setItems(items);
        selectBox.setSelected(initialValue == null ? "---" : resources.findKey(initialValue, true));
        selectBox.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (selectBox.getSelected().equals("---")) {
                    dispatcher.setState(null);
                } else {
                    T result = resources.get(selectBox.getSelected());
                    dispatcher.setState(result);
                }
            }
        });
    }


    @Override public Actor getActor() {
        return selectBox;
    }
}
