package com.vlaaad.ui.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.esotericsoftware.tablelayout.Cell;

/**
 * Created 02.06.14 by vlaaad
 */
public class HorizontalList extends Table {
    public HorizontalList() {
    }

    public HorizontalList(Skin skin) {
        super(skin);
    }

    @Override public Cell row() {
        throw new IllegalStateException("horizontal list! no rows!");
    }
}
