package com.vlaaad.ui.util.instantiators;

import com.badlogic.gdx.utils.JsonValue;
import com.vlaaad.ui.util.Instantiator;
import com.vlaaad.ui.util.Resources;

/**
 * Created 07.06.14 by vlaaad
 */
public abstract class CollectionInstantiator<T> extends Instantiator<T> {


    @Override public T newInstance(Resources resources) {
        T list = createInstance(resources);
        if (!value.has("elements"))
            return list;
        for (JsonValue element : value.get("elements")) {
            addElement(list, element);
        }
        return list;
    }
    protected abstract T createInstance(Resources resources);
    protected abstract void addElement(T o, JsonValue v);
}
