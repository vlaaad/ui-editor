package com.vlaaad.ui.util;

/**
* Created 31.05.14 by vlaaad
*/
public abstract class Applier<Obj, Val> {
    public Class<Obj> objectClass;
    public Class<Val> valueClass;

    public abstract void apply(Obj o, Val v);
}
