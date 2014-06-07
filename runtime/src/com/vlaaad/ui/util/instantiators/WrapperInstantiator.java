package com.vlaaad.ui.util.instantiators;

import com.vlaaad.ui.util.Instantiator;
import com.vlaaad.ui.util.Resources;

/**
 * Created 07.06.14 by vlaaad
 */
public abstract class WrapperInstantiator<T, W> extends Instantiator<T> {
    @Override public T newInstance(Resources resources) {
        W wrapped = value.has("widget") ? this.<W>instantiate(value.get("widget")) : null;
        return createInstance(resources, wrapped);
    }
    protected abstract T createInstance(Resources resources, W optionalWrapped);
}
