package com.vlaaad.ui.scene2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created 02.06.14 by vlaaad
 */
public class VerticalList extends Table {
    public VerticalList() {
    }

    public VerticalList(Skin skin) {
        super(skin);
    }

    @Override public Cell<Label> add(String text) {
        Cell<Label> cell = super.add(text);
        row();
        return cell;
    }

    @Override public Cell<Label> add(String text, String labelStyleName) {
        final Cell<Label> add = super.add(text, labelStyleName);
        row();
        return add;
    }

    @Override public Cell<Label> add(String text, String fontName, Color color) {
        final Cell<Label> add = super.add(text, fontName, color);
        row();
        return add;
    }

    @Override public Cell<Label> add(String text, String fontName, String colorName) {
        final Cell<Label> add = super.add(text, fontName, colorName);
        row();
        return add;
    }

    @Override public Cell<Actor> add() {
        final Cell<Actor> add = super.add();
        row();
        return add;
    }

    @Override public Cell add(Actor actor) {
        final Cell add = super.add(actor);
        row();
        return add;
    }

    @Override public void add(Actor... actors) {
        for (Actor actor : actors) add(actor);
    }
}
