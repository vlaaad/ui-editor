package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.Collection;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.EditorToolkit;

/**
* Created 02.06.14 by vlaaad
*/
public abstract class CollectionFactory<T> extends EditorModelFactory<T> {
    @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        Array<EditorModel<Object>> arr = new Array<EditorModel<Object>>();
        for (Object element : getElements(t)) {
            arr.add(EditorToolkit.createModel(element, full));
        }
        return new Collection<T, Object>(t, params, arr);
    }

    protected abstract Iterable<? extends Object> getElements(T t);
}
