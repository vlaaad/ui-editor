package com.vlaaad.ui.util;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created 31.05.14 by vlaaad
 */
public abstract class Applier<Obj, Val> {
    public Class<Obj> objectClass;
    public Class<Val> valueClass;
    public Val defaultValue;
    public boolean defaultValueDefined;

    public void applyDefault(Obj o, Skin skin) {
        apply(o, getDefaultValue(o, skin));
    }

    public Val getDefaultValue(Obj o, Skin skin) {
        if (!defaultValueDefined) {
            ObjectMap<String, Val> all = skin.getAll(valueClass);
            if (all == null || all.size == 0)
                return null;
            if (all.containsKey("default"))
                return all.get("default");
            for (Val v : all.values())
                return v;
        }
        return defaultValue;
    }

    public abstract void apply(Obj o, Val v);
}
