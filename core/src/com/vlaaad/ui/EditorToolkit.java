package com.vlaaad.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.models.ActorModel;

/**
 * Created 01.06.14 by vlaaad
 */
public class EditorToolkit {
    private static final ObjectMap<Class, Factory> models = new ObjectMap<Class, Factory>();

    static {
        models.put(Label.class, new Factory<Label>() {
            @Override public ActorModel create(Label label, ObjectMap<Object, ObjectMap<String, Object>> params) {

                return null;
            }
        });
    }

    private <T extends ActorModel> void reg(Class<T> type, Factory<T> factory) {
        models.put(type, factory);
    }

    public abstract static class Factory<T> {
        public abstract ActorModel create(T t, ObjectMap<Object, ObjectMap<String, Object>> params);
    }
}
