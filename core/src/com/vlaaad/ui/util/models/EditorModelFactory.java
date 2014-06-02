package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.EditorModel;

/**
* Created 02.06.14 by vlaaad
*/
public abstract class EditorModelFactory<T> {
    public final EditorModel<T> newInstance(T t, ObjectMap<Object, ObjectMap<String, Object>> params) {
        return create(t, params(t, params), params);
    }

    protected final ObjectMap<String, Object> params(Object o, ObjectMap<Object, ObjectMap<String, Object>> params) {
        return params.containsKey(o) ? params.get(o) : new ObjectMap<String, Object>();
    }

    public abstract EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full);
}
