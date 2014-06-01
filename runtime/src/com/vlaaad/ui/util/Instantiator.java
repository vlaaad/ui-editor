package com.vlaaad.ui.util;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created 31.05.14 by vlaaad
 */
public abstract class Instantiator<T> {
    final ObjectMap<String, Class> requirements = new ObjectMap<String, Class>();
    JsonValue value;
    Skin skin;
    ObjectMap<Object, ObjectMap<String, Object>> params;

    protected void require(String key, Class type) {
        requirements.put(key, type);
    }

    public abstract T newInstance(Resources resources);

    protected T instantiate(JsonValue value) {
        return Toolkit.instantiate(value, skin, params);
    }

    protected <A> A inject(A a, JsonValue value) {
        Toolkit.inject(params, a, value, skin);
        return a;
    }
}
