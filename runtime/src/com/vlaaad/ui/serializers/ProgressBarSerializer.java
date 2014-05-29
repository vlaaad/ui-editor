package com.vlaaad.ui.serializers;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created 29.05.14 by vlaaad
 */
public class ProgressBarSerializer extends ActorSerializer<ProgressBar> {
    private final Skin skin;

    public ProgressBarSerializer(Skin skin) {this.skin = skin;}

    @Override public ProgressBar read(Json json, JsonValue jsonData, Class type) {
        ProgressBar progressBar = new ProgressBar(
            jsonData.getFloat("min", 0),
            jsonData.getFloat("max", 100),
            jsonData.getFloat("step", 1),
            jsonData.getBoolean("vertical", false),
            skin,
            jsonData.getString("style", "default")
        );
        readActorData(progressBar, jsonData);
        return progressBar;
    }
}
