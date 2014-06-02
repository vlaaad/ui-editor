package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.EditorToolkit;
import com.vlaaad.ui.util.Wrapper;

/**
* Created 02.06.14 by vlaaad
*/
public abstract class WrapperFactory<T> extends EditorModelFactory<T> {

    @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        Object wrapped = getWrapped(t);
        return new Wrapper<T, Object>(t, params, wrapped == null ? null : EditorToolkit.createModel(wrapped, full));
    }

    protected abstract Object getWrapped(T t);
}
