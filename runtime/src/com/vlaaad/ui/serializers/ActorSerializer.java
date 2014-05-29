package com.vlaaad.ui.serializers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created 29.05.14 by vlaaad
 */
public abstract class ActorSerializer<T extends Actor> extends Json.ReadOnlySerializer<T> {
    protected void readActorData(Actor actor, JsonValue jsonData) {
        actor.setName(jsonData.getString("name"));

        if (jsonData.has("x")) actor.setX(jsonData.getFloat("x"));
        if (jsonData.has("y")) actor.setY(jsonData.getFloat("y"));

        if (jsonData.has("width")) actor.setWidth(jsonData.getFloat("width"));
        if (jsonData.has("height")) actor.setHeight(jsonData.getFloat("height"));

        if (jsonData.has("rotation")) actor.setRotation(jsonData.getFloat("rotation"));

        if (jsonData.has("scaleX")) actor.setScaleX(jsonData.getFloat("scaleX"));
        if (jsonData.has("scaleY")) actor.setScaleY(jsonData.getFloat("scaleY"));

        if (jsonData.has("originX")) actor.setOriginX(jsonData.getFloat("originX"));
        if (jsonData.has("originY")) actor.setOriginY(jsonData.getFloat("originY"));

        if (jsonData.has("color")) actor.setColor(Color.valueOf(jsonData.getString("color")));

        if (jsonData.has("touchable")) actor.setTouchable(Touchable.valueOf(jsonData.getString("touchable")));
    }

    protected Actor deserializeWidget(Json json, JsonValue widget) {
        if (widget == null)
            return null;
        String tag = widget.getString("type");
        return (Actor) json.readValue(json.getClass(tag), widget);
    }
}
