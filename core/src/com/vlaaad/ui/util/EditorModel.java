package com.vlaaad.ui.util;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created 01.06.14 by vlaaad
 */
public abstract class EditorModel {
    private final Object object;
    private final Param<?> param;

    public <T> EditorModel(Object object, Param<T> param) {

        this.object = object;
        this.param = param;
    }

    public static class Param<T> {
        public final T obj;
        public final ObjectMap<String, Applier<T, Object>> appliers;

        public Param(T obj, ObjectMap<String, Applier<T, Object>> appliers) {
            this.obj = obj;
            this.appliers = appliers;
        }
    }
}
