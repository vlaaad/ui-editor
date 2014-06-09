package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.Collection;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.EditorToolkit;

/**
 * Created 02.06.14 by vlaaad
 */
public abstract class CollectionFactory<T, E> extends EditorModelFactory<T> {

    private final Class<E> elementType;

    protected CollectionFactory(Class<E> elementType) {
        this.elementType = elementType;
    }

    @Override public EditorModel create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        Array<EditorModel> arr = new Array<EditorModel>();
        for (E element : getElements(t)) {
            arr.add(EditorToolkit.createModel(element, full));
        }
        return new Collection(t, params, (Class<Object>) elementType, arr);
    }

    protected abstract Iterable<? extends E> getElements(T t);
}
