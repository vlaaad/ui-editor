package com.vlaaad.ui.util;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created 31.05.14 by vlaaad
 */
public class Resources {
    final ObjectMap<String, Object> data = new ObjectMap<String, Object>();

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) data.get(key);
    }

    public <T> T get(String key, Class<T> type) {
        return get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        return (T) data.get(key, defaultValue);
    }
}
