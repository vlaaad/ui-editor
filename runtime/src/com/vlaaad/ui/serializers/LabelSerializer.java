package com.vlaaad.ui.serializers;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.vlaaad.ui.UiAlign;

/**
 * Created 29.05.14 by vlaaad
 */
public class LabelSerializer extends ActorSerializer<Label> {
    private final Skin skin;

    public LabelSerializer(Skin skin) {
        this.skin = skin;
    }

    @Override public Label read(Json json, JsonValue jsonData, Class type) {
        Label label = new Label(jsonData.getString("text"), skin, jsonData.getString("style"));
        label.setAlignment(UiAlign.valueOf(jsonData.getString("align", "left")).value);
        readActorData(label, jsonData);
        return label;
    }
}
