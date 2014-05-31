package com.vlaaad.ui.util.instantiators;

import com.vlaaad.ui.util.Instantiator;
import com.vlaaad.ui.util.Resources;

/**
* Created 31.05.14 by vlaaad
*/
public class ReflectionInstantiator<T> extends Instantiator<T> {
    Class<T> type;

    public ReflectionInstantiator<T> withClass(Class<T> type) {
        this.type = type;
        return this;
    }

    @Override public T newInstance(Resources resources) {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
