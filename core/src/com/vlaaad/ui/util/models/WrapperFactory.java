package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.EditorToolkit;
import com.vlaaad.ui.util.Wrapper;

/**
 * Created 02.06.14 by vlaaad
 */
public abstract class WrapperFactory<T, That> extends EditorModelFactory<T>{

    private final Class<? extends That> elementType;

    protected WrapperFactory(Class<? extends That> elementType) {
        this.elementType = elementType;
    }

    @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        That wrapped = getWrapped(t);
        return new Wrapper<T, That>(t, elementType, params, wrapped == null ? null : EditorToolkit.createModel(wrapped, full));
    }

    protected abstract That getWrapped(T t);

}
