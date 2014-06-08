package com.vlaaad.ui.util.models;

import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.EditorModel;
import com.vlaaad.ui.util.Instantiator;
import com.vlaaad.ui.util.Toolkit;

/**
 * Created 02.06.14 by vlaaad
 */
public abstract class EditorModelFactory<T> {
    @SuppressWarnings("unchecked")
    public final EditorModel<T> newInstance(T t, ObjectMap<Object, ObjectMap<String, Object>> params) {
        EditorModel<T> model = create(t, params(t, params), params);
        Instantiator<T> instantiator = (Instantiator<T>) Toolkit.instantiator(t.getClass());
        if (instantiator != null) {
            for (String requirement : instantiator.requirements.keys()) {
                model.requirements().add(requirement);
            }
        }
        return model;
    }

    protected final ObjectMap<String, Object> params(Object o, ObjectMap<Object, ObjectMap<String, Object>> params) {
        if (params.containsKey(o))
            return params.get(o);
        else {
            ObjectMap<String, Object> p = new ObjectMap<String, Object>();
            params.put(o, p);
            return p;
        }
    }

    public abstract EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full);
}
