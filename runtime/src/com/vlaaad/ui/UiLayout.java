package com.vlaaad.ui;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.Toolkit;

import java.util.Arrays;

/**
 * Created 29.05.14 by vlaaad
 */
public class UiLayout {

    private final FileHandle file;
    public final Skin skin;
    public final ObjectMap<Object, ObjectMap<String, Object>> params;
    private Actor actor;

    public UiLayout(FileHandle file) {
        this(file, new Skin(file.sibling(file.nameWithoutExtension() + ".json")));
    }

    public UiLayout(FileHandle file, Skin skin) {
        this(file, skin, null);
    }

    public UiLayout(FileHandle file, Skin skin, ObjectMap<Object, ObjectMap<String, Object>> params) {
        this.file = file;
        this.skin = skin;
        this.params = params;
        load();
    }

    private void load() {
        JsonValue value = new JsonReader().parse(file);
        actor = Toolkit.instantiate(value, skin, params);
    }

    public Actor getActor() {
        return actor;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type, String... path) {
        return get(path);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String... path) {
        Actor actor = this.actor;
//        System.out.println("get in " + actor + ": " + Arrays.toString(path));
        for (String el : path) {
            if (actor instanceof Group) {
                actor = findActor(((Group) actor), el);
//                System.out.println("found by name: " + actor);
            } else {
                throw new IllegalArgumentException(actor + " is not a group! " + Arrays.toString(path) + ", failed on: " + el);
            }
        }
        if (actor == null) {
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
