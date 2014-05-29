package com.vlaaad.ui.serializers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.vlaaad.ui.UiAlign;

/**
 * Created 29.05.14 by vlaaad
 */
public class ContainerSerializer extends ActorSerializer<Container> {

    private final Skin skin;

    public ContainerSerializer(Skin skin) {
        this.skin = skin;
    }

    @Override public Container read(Json json, JsonValue jsonData, Class type) {
        Container container = new Container();
        readActorData(container, jsonData);
        JsonValue widget = jsonData.get("widget");
        if (widget == null)
            container.setWidget(new Actor());
        else
            container.setWidget(deserializeWidget(json, widget));
        container.align(UiAlign.valueOf(jsonData.getString("align", "center")).value);

        if (jsonData.has("minWidth")) container.minWidth(jsonData.getFloat("minWidth"));
        if (jsonData.has("minHeight")) container.minHeight(jsonData.getFloat("minHeight"));

        if (jsonData.has("prefWidth")) container.prefWidth(jsonData.getFloat("prefWidth"));
        if (jsonData.has("prefHeight")) container.prefHeight(jsonData.getFloat("prefHeight"));

        if (jsonData.has("maxWidth")) container.maxWidth(jsonData.getFloat("maxWidth"));
        if (jsonData.has("maxHeight")) container.maxHeight(jsonData.getFloat("maxHeight"));

        if (jsonData.has("padTop")) container.padTop(jsonData.getFloat("padTop"));
        if (jsonData.has("padLeft")) container.padLeft(jsonData.getFloat("padLeft"));
        if (jsonData.has("padBottom")) container.padBottom(jsonData.getFloat("padBottom"));
        if (jsonData.has("padRight")) container.padRight(jsonData.getFloat("padRight"));

        container.fill(jsonData.getBoolean("fillX", false), jsonData.getBoolean("fillY", false));

        if (jsonData.has("background")) container.background(skin.getDrawable(jsonData.getString("background")));

        if (jsonData.has("clip")) container.setClip(jsonData.getBoolean("clip"));

        return container;
    }
}
