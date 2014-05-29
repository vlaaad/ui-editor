package com.vlaaad.ui;

import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * Created 29.05.14 by vlaaad
 */
public enum UiAlign {
    top(Align.top),
    left(Align.left),
    bottom(Align.bottom),
    right(Align.right),
    center(Align.center),
    topLeft(Align.top | Align.left),
    topRight(Align.top | Align.right),
    bottomLeft(Align.bottom | Align.left),
    bottomRight(Align.bottom | Align.right);

    public final int value;

    UiAlign(int value) {
        this.value = value;
    }
}
