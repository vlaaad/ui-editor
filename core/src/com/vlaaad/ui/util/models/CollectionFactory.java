package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.Collection;
import com.vlaaad.ui.util.CollectionController;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.EditorToolkit;

/**
 * Created 02.06.14 by vlaaad
 */
public abstract class CollectionFactory<T, E> extends EditorModelFactory<T> implements CollectionController<E> {

    private final Class<E> elementType;
    private T t;

    protected CollectionFactory(Class<E> elementType) {
        this.elementType = elementType;
    }

    @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
        this.t = t;
        Array<EditorModel<E>> arr = new Array<EditorModel<E>>();
        for (E element : getElements(t)) {
            arr.add(EditorToolkit.createModel(element, full));
        }
        return new Collection<T, E>(t, params, elementType, this, arr);
    }

    protected abstract Iterable<? extends E> getElements(T t);

    @Override public void remove(E element) {
        remove(t, element);
    }

    protected abstract void remove(T t, E element);

    @Override public void add(E element) {
        add(t, element);
    }

    protected abstract void add(T t, E element);
}
