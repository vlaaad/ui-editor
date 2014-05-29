package com.vlaaad.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.vlaaad.ui.serializers.*;

import java.util.Arrays;

/**
 * Created 29.05.14 by vlaaad
 */
public class UiLayout extends Container {

    private final FileHandle file;
    public final Skin skin;

    public UiLayout(FileHandle file) {
        this(file, new Skin(file.sibling(file.nameWithoutExtension() + ".json")));
    }

    public UiLayout(FileHandle file, Skin skin) {
        this.file = file;
        this.skin = skin;
        load();
    }

    private void init(Json json) {
        json.setUsePrototypes(false);
        json.setTypeName("type");
        reg(json, "label", Label.class, new LabelSerializer(skin));
        reg(json, "progressBar", ProgressBar.class, new ProgressBarSerializer(skin));
        reg(json, "table", Table.class, new TableSerializer(skin));
        reg(json, "container", Container.class, new ContainerSerializer(skin));
        reg(json, "textButton", TextButton.class, new TextButtonSerializer(skin));
    }

    private <T extends Actor> void reg(Json json, String tag, Class<T> type, Json.Serializer<T> serializer) {
        json.addClassTag(tag, type);
        json.setSerializer(type, serializer);
    }

    private void load() {
        Json json = new Json();
        init(json);

        final UiLayout layout = this;
        json.setSerializer(UiLayout.class, new Json.ReadOnlySerializer<UiLayout>() {
            @Override public UiLayout read(Json json, JsonValue jsonData, Class type) {
                layout.setName(jsonData.getString("name"));
                layout.setFillParent(jsonData.getBoolean("fillParent", false));
                layout.fill(jsonData.getBoolean("fill", false));
                JsonValue widget = jsonData.get("widget");
                if (widget != null) {
                    String tag = widget.getString("type");
                    Actor actor = (Actor) json.readValue(json.getClass(tag), widget);
                    setWidget(actor);
                } else {
                    setWidget(new Actor());
                }
                if (jsonData.has("minWidth")) layout.minWidth(jsonData.getFloat("minWidth"));
                if (jsonData.has("minHeight")) layout.minHeight(jsonData.getFloat("minHeight"));

                if (jsonData.has("prefWidth")) layout.prefWidth(jsonData.getFloat("prefWidth"));
                if (jsonData.has("prefHeight")) layout.prefHeight(jsonData.getFloat("prefHeight"));

                if (jsonData.has("maxWidth")) layout.maxWidth(jsonData.getFloat("maxWidth"));
                if (jsonData.has("maxHeight")) layout.maxHeight(jsonData.getFloat("maxHeight"));

                if (jsonData.has("padTop")) layout.padTop(jsonData.getFloat("padTop"));
                if (jsonData.has("padLeft")) layout.padLeft(jsonData.getFloat("padLeft"));
                if (jsonData.has("padBottom")) layout.padBottom(jsonData.getFloat("padBottom"));
                if (jsonData.has("padRight")) layout.padRight(jsonData.getFloat("padRight"));

                layout.fill(jsonData.getBoolean("fillX", false), jsonData.getBoolean("fillY", false));

                if (jsonData.has("background")) layout.background(skin.getDrawable(jsonData.getString("background")));

                if (jsonData.has("clip")) layout.setClip(jsonData.getBoolean("clip"));
                return layout;
            }
        });
        json.fromJson(UiLayout.class, file);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type, String... path) {
        Actor actor = this;
        for (String el : path) {
            if (actor instanceof Group) {
                actor = findActor(((Group) actor), el);
            } else {
                throw new IllegalArgumentException("not a group! " + Arrays.toString(path) + ", failed on: " + el);
            }
        }
        if (actor == null) {
            print();
            throw new IllegalArgumentException("Not found! " + Arrays.toString(path));
        }
        return (T) actor;
    }

    public Actor findActor(Group group, String name) {
        Array<Actor> children = group.getChildren();
        for (int i = 0, n = children.size; i < n; i++) {
            if (name.equals(children.get(i).getName()))
                return children.get(i);
        }
        return null;
    }

}
