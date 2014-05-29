package com.vlaaad.ui.serializers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.tablelayout.Cell;
import com.vlaaad.ui.UiAlign;

/**
 * Created 29.05.14 by vlaaad
 */
public class TableSerializer extends ActorSerializer<Table> {
    private final Skin skin;

    public TableSerializer(Skin skin) {
        this.skin = skin;
    }

    @Override public Table read(Json json, JsonValue jsonData, Class type) {
        Table table = new Table(skin);
        readActorData(table, jsonData);
        table.setFillParent(jsonData.getBoolean("fillParent", false));
        table.align(UiAlign.valueOf(jsonData.getString("align", "center")).value);
        System.out.println("table: " + table);
        for (JsonValue row : jsonData.get("cells")) {
            for (JsonValue cell : row) {

                Actor widget = deserializeWidget(json, cell.get("widget"));

                Cell tableCell = table.add(widget);
                setupCell(tableCell, cell);
            }
            table.row();
        }
        return table;
    }

    private void setupCell(Cell cell, JsonValue value) {
        if (value.has("width")) cell.width(value.getFloat("width"));
        if (value.has("height")) cell.height(value.getFloat("height"));

        if (value.has("minWidth")) cell.minWidth(value.getFloat("minWidth"));
        if (value.has("minHeight")) cell.minHeight(value.getFloat("minHeight"));

        if (value.has("prefWidth")) cell.prefWidth(value.getFloat("prefWidth"));
        if (value.has("prefHeight")) cell.prefHeight(value.getFloat("prefHeight"));

        if (value.has("maxWidth")) cell.maxWidth(value.getFloat("maxWidth"));
        if (value.has("maxHeight")) cell.maxHeight(value.getFloat("maxHeight"));

        if (value.has("spaceTop")) cell.spaceTop(value.getFloat("spaceTop"));
        if (value.has("spaceLeft")) cell.spaceLeft(value.getFloat("spaceLeft"));
        if (value.has("spaceBottom")) cell.spaceBottom(value.getFloat("spaceBottom"));
        if (value.has("spaceRight")) cell.spaceRight(value.getFloat("spaceRight"));

        if (value.has("padTop")) cell.padTop(value.getFloat("padTop"));
        if (value.has("padLeft")) cell.padLeft(value.getFloat("padLeft"));
        if (value.has("padBottom")) cell.padBottom(value.getFloat("padBottom"));
        if (value.has("padRight")) cell.padRight(value.getFloat("padRight"));

        cell.fill(value.getBoolean("fillX", false), value.getBoolean("fillY", false));
        cell.expand(value.getBoolean("expandX", false), value.getBoolean("expandY", false));
        cell.uniform(value.getBoolean("uniformX", false), value.getBoolean("uniformY", false));

        cell.align(UiAlign.valueOf(value.getString("align", "center")).value);

        cell.colspan(value.getInt("colspan", 1));
    }
}
