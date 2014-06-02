package com.vlaaad.ui.util;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Created 31.05.14 by vlaaad
 */
public abstract class Applier<Obj, Val> {
    public Class<Obj> objectClass;
    public Class<Val> valueClass;
    public Val defaultValue;

    public void applyDefault(Obj o, Skin skin) {
        apply(o, getDefaultValue(o, skin));
    }

    protected Val getDefaultValue(Obj o, Skin skin) {
        return defaultValue;
    }

    public abstract void apply(Obj o, Val v);
}
