package com.vlaaad.ui.serializers;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created 29.05.14 by vlaaad
 */
public class TextButtonSerializer extends ActorSerializer<TextButton> {

    private final Skin skin;

    public TextButtonSerializer(Skin skin) {
        this.skin = skin;
    }

    @Override public TextButton read(Json json, JsonValue jsonData, Class type) {
        TextButton button = new TextButton(jsonData.getString("text"), skin, jsonData.getString("style"));
        readActorData(button, jsonData);
        return button;
    }
}
