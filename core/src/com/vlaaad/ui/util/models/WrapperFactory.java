package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.EditorToolkit;
import com.vlaaad.ui.util.Wrapper;
import com.vlaaad.ui.util.WrapperController;

/**
 * Created 02.06.14 by vlaaad
 */
public abstract class WrapperFactory<T, That> extends EditorModelFactory<T> implements WrapperController<That> {

    private final Class<? extends That> elementType;
    private T t;

    protected WrapperFactory(Class<? extends That> elementType) {
        this.elementType = elementType;
    }

    @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        That wrapped = getWrapped(t);
        this.t = t;

        return new Wrapper<T, That>(t, elementType, params, this, wrapped == null ? null : EditorToolkit.createModel(wrapped, full));
    }

    @Override public void remove(That widget) {
        remove(t,widget);
    }

    protected abstract That getWrapped(T t);

    @Override public final void setWidget(That widget) {
        setWidget(t, widget);
    }

    protected abstract void remove(T t, That widget);

    protected abstract void setWidget(T t, That widget);
}
