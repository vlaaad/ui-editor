package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.Element;

/**
* Created 02.06.14 by vlaaad
*/
public class ElementFactory<T> extends EditorModelFactory<T> {

    @Override public EditorModel create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        return new Element(t, params);
    }
}
