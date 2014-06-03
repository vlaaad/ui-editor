package com.vlaaad.ui.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.tablelayout.Cell;
import com.esotericsoftware.tablelayout.Value;
import com.vlaaad.ui.UiAlign;
import com.vlaaad.ui.scene2d.HorizontalList;
import com.vlaaad.ui.scene2d.VerticalList;
import com.vlaaad.ui.util.instantiators.ReflectionInstantiator;

/**
 * Created 30.05.14 by vlaaad
 */
public class Toolkit {
    private static final ReflectionInstantiator reflectionInstantiator = new ReflectionInstantiator();

    private static final ObjectMap<Class, ObjectMap<String, Applier>> appliers = new ObjectMap<Class, ObjectMap<String, Applier>>();
    private static final ObjectMap<Class, Instantiator> instantiators = new ObjectMap<Class, Instantiator>();
    private static final ObjectMap<String, Class> tags = new ObjectMap<String, Class>();

    static {
        init();
    }


    private Toolkit() {}

    protected static void init() {
        applier("name", Actor.class, String.class, "unnamed", new Applier<Actor, String>() {
            @Override public void apply(Actor o, String v) {
                o.setName(v);
            }
        });
        applier("x", Actor.class, Float.class, 0f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setX(v);
            }
        });
        applier("y", Actor.class, Float.class, 0f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setY(v);
            }
        });
        applier("width", Actor.class, Float.class, 0f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setWidth(v);
            }
        });
        applier("rotation", Actor.class, Float.class, 0f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setRotation(v);
            }
        });
        applier("scaleX", Actor.class, Float.class, 1f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setScaleX(v);
            }
        });
        applier("scaleY", Actor.class, Float.class, 1f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setScaleY(v);
            }
        });
        applier("originX", Actor.class, Float.class, 0f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setOriginX(v);
            }
        });
        applier("originY", Actor.class, Float.class, 0f, new Applier<Actor, Float>() {
            @Override public void apply(Actor o, Float v) {
                o.setOriginY(v);
            }
        });
        applier("color", Actor.class, Color.class, Color.WHITE, new Applier<Actor, Color>() {
            @Override public void apply(Actor o, Color v) {
                o.setColor(v);
            }
        });
        applier("touchable", Actor.class, Touchable.class, new Applier<Actor, Touchable>() {
            @Override protected Touchable getDefaultValue(Actor o, Skin skin) {
                if (o instanceof Button)
                    return Touchable.enabled;
                if (o instanceof Table || o instanceof Container || o instanceof Stack || o instanceof VerticalGroup || o instanceof HorizontalGroup)
                    return Touchable.childrenOnly;
                return Touchable.enabled;
            }

            @Override public void apply(Actor o, Touchable v) {
                o.setTouchable(v);
            }
        });

        applier("transform", Group.class, Boolean.class, new Applier<Group, Boolean>() {

            @Override protected Boolean getDefaultValue(Group o, Skin skin) {
                return !(o instanceof Container || o instanceof Stack || o instanceof Table);
            }

            @Override public void apply(Group o, Boolean v) {
                o.setTransform(v);
            }
        });

        applier("fillParent", Layout.class, Boolean.class, false, new Applier<Layout, Boolean>() {
            @Override public void apply(Layout o, Boolean v) {
                o.setFillParent(v);
            }
        });

        applier("minWidth", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.minWidth(Value.minWidth);
            }

            @Override public void apply(Cell o, Float v) {
                o.minWidth(v);
            }
        });
        applier("minHeight", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.minHeight(Value.minHeight);
            }

            @Override public void apply(Cell o, Float v) {
                o.minHeight(v);
            }
        });
        applier("prefWidth", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.prefWidth(Value.prefWidth);
            }

            @Override public void apply(Cell o, Float v) {
                o.prefWidth(v);
            }
        });
        applier("prefHeight", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.prefHeight(Value.prefHeight);
            }

            @Override public void apply(Cell o, Float v) {
                o.prefHeight(v);
            }
        });
        applier("maxWidth", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.maxWidth(Value.maxWidth);
            }

            @Override public void apply(Cell o, Float v) {
                o.maxWidth(v);
            }
        });
        applier("maxHeight", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.maxHeight(Value.maxHeight);
            }

            @Override public void apply(Cell o, Float v) {
                o.maxHeight(v);
            }
        });
        applier("spaceTop", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.spaceTop(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.spaceTop(v);
            }
        });
        applier("spaceLeft", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.spaceRight(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.spaceLeft(v);
            }
        });
        applier("spaceBottom", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.spaceBottom(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.spaceBottom(v);
            }
        });
        applier("spaceRight", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.spaceRight(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.spaceRight(v);
            }
        });
        applier("padTop", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.padTop(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.padTop(v);
            }
        });
        applier("padLeft", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.padLeft(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.padLeft(v);
            }
        });
        applier("padBottom", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.padBottom(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.padBottom(v);
            }
        });
        applier("padRight", Cell.class, Float.class, new Applier<Cell, Float>() {
            @Override public void applyDefault(Cell o, Skin skin) {
                o.padRight(Value.zero);
            }

            @Override public void apply(Cell o, Float v) {
                o.padRight(v);
            }
        });
        applier("fillX", Cell.class, Boolean.class, false, new Applier<Cell, Boolean>() {
            @Override public void apply(Cell o, Boolean v) {
                o.fill(v, o.getFillY() != 0);
            }
        });
        applier("fillY", Cell.class, Boolean.class, false, new Applier<Cell, Boolean>() {
            @Override public void apply(Cell o, Boolean v) {
                o.fill(o.getFillX() != 0, v);
            }
        });
        applier("expandX", Cell.class, Boolean.class, false, new Applier<Cell, Boolean>() {
            @Override public void apply(Cell o, Boolean v) {
                o.expand(v, o.getExpandY() != 0);
            }
        });
        applier("expandY", Cell.class, Boolean.class, false, new Applier<Cell, Boolean>() {
            @Override public void apply(Cell o, Boolean v) {
                o.expand(o.getExpandX() != 0, v);
            }
        });
        applier("align", Cell.class, UiAlign.class, UiAlign.center, new Applier<Cell, UiAlign>() {
            @Override public void apply(Cell o, UiAlign v) {
                o.align(v.value);
            }
        });
        applier("colspan", Cell.class, Integer.class, 1, new Applier<Cell, Integer>() {
            @Override public void apply(Cell o, Integer v) {
                o.colspan(v);
            }
        });

        applier("text", Label.class, String.class, new Applier<Label, String>() {
            @Override public void apply(Label o, String v) {
                o.setText(v);
            }
        });
        applier("align", Label.class, UiAlign.class, UiAlign.left, new Applier<Label, UiAlign>() {
            @Override public void apply(Label o, UiAlign v) {
                o.setAlignment(v.value);
            }
        });
        applier("wrap", Label.class, Boolean.class, false, new Applier<Label, Boolean>() {
            @Override public void apply(Label o, Boolean v) {
                o.setWrap(v);
            }
        });
        applier("style", Label.class, Label.LabelStyle.class, new Applier<Label, Label.LabelStyle>() {
            @Override protected Label.LabelStyle getDefaultValue(Label o, Skin skin) {
                return skin.get(Label.LabelStyle.class);
            }

            @Override public void apply(Label o, Label.LabelStyle v) {
                o.setStyle(v);
            }
        });
        applier("fontScale", Label.class, Float.class, 1f, new Applier<Label, Float>() {
            @Override public void apply(Label o, Float v) {
                o.setFontScale(v);
            }
        });

        applier("text", TextButton.class, String.class, new Applier<TextButton, String>() {
            @Override public void apply(TextButton o, String v) {
                o.setText(v);
            }
        });
        applier("style", TextButton.class, TextButton.TextButtonStyle.class, new Applier<TextButton, TextButton.TextButtonStyle>() {
            @Override protected TextButton.TextButtonStyle getDefaultValue(TextButton o, Skin skin) {
                return skin.get(TextButton.TextButtonStyle.class);
            }

            @Override public void apply(TextButton o, TextButton.TextButtonStyle v) {
                o.setStyle(v);
            }
        });

        applier("disabled", Disableable.class, Boolean.class, false, new Applier<Disableable, Boolean>() {
            @Override public void apply(Disableable o, Boolean v) {
                o.setDisabled(v);
            }
        });

        applier("align", Container.class, UiAlign.class, UiAlign.center, new Applier<Container, UiAlign>() {
            @Override public void apply(Container o, UiAlign v) {
                o.align(v.value);
            }
        });
        applier("padTop", Container.class, Float.class, new Applier<Container, Float>() {
            @Override public void apply(Container o, Float v) {
                o.padTop(v);
            }
        });
        applier("padLeft", Container.class, Float.class, new Applier<Container, Float>() {
            @Override public void apply(Container o, Float v) {
                o.padLeft(v);
            }
        });
        applier("padBottom", Container.class, Float.class, new Applier<Container, Float>() {
            @Override public void apply(Container o, Float v) {
                o.padBottom(v);
            }
        });
        applier("padRight", Container.class, Float.class, new Applier<Container, Float>() {
            @Override public void apply(Container o, Float v) {
                o.padRight(v);
            }
        });
        applier("fillX", Container.class, Boolean.class, new Applier<Container, Boolean>() {
            @Override public void apply(Container o, Boolean v) {
                o.fill(v, o.getFillY() != 0);
            }
        });
        applier("fillY", Container.class, Boolean.class, new Applier<Container, Boolean>() {
            @Override public void apply(Container o, Boolean v) {
                o.fill(o.getFillX() != 0, v);
            }
        });
        applier("background", Container.class, Drawable.class, new Applier<Container, Drawable>() {
            @Override public void apply(Container o, Drawable v) {
                o.background(v);
            }
        });
        applier("clip", Container.class, Boolean.class, false, new Applier<Container, Boolean>() {
            @Override public void apply(Container o, Boolean v) {
                o.setClip(v);
            }
        });

        applier("min", ProgressBar.class, Float.class, 0f, new Applier<ProgressBar, Float>() {
            @Override public void apply(ProgressBar o, Float v) {
                o.setRange(v, o.getMaxValue());
            }
        });
        applier("max", ProgressBar.class, Float.class, 100f, new Applier<ProgressBar, Float>() {
            @Override public void apply(ProgressBar o, Float v) {
                o.setRange(o.getMinValue(), v);
            }
        });
        applier("step", ProgressBar.class, Float.class, 1f, new Applier<ProgressBar, Float>() {
            @Override public void apply(ProgressBar o, Float v) {
                o.setStepSize(v);
            }
        });
        applier("style", ProgressBar.class, ProgressBar.ProgressBarStyle.class, new Applier<ProgressBar, ProgressBar.ProgressBarStyle>() {
            @Override protected ProgressBar.ProgressBarStyle getDefaultValue(ProgressBar o, Skin skin) {
                return skin.get(ProgressBar.ProgressBarStyle.class);
            }

            @Override public void apply(ProgressBar o, ProgressBar.ProgressBarStyle v) {
                o.setStyle(v);
            }
        });
        applier("value", ProgressBar.class, Float.class, 0f, new Applier<ProgressBar, Float>() {
            @Override public void apply(ProgressBar o, Float v) {
                o.setValue(v);
            }
        });

        applier("style", ScrollPane.class, ScrollPane.ScrollPaneStyle.class, new Applier<ScrollPane, ScrollPane.ScrollPaneStyle>() {
            @Override protected ScrollPane.ScrollPaneStyle getDefaultValue(ScrollPane o, Skin skin) {
                return skin.get(ScrollPane.ScrollPaneStyle.class);
            }

            @Override public void apply(ScrollPane o, ScrollPane.ScrollPaneStyle v) {
                o.setStyle(v);
            }
        });

        applier("background", Table.class, Drawable.class, new Applier<Table, Drawable>() {
            @Override public void apply(Table o, Drawable v) {
                o.setBackground(v);
            }
        });
        applier("align", Table.class, UiAlign.class, UiAlign.center, new Applier<Table, UiAlign>() {
            @Override public void apply(Table o, UiAlign v) {
                o.align(v.value);
            }
        });

        instantiator("label", Label.class, new Instantiator<Label>() {
            {
                require("text", String.class);
                require("style", Label.LabelStyle.class);
            }

            @Override public Label newInstance(Resources resources) {
                return new Label(resources.get("text", String.class), resources.get("style", Label.LabelStyle.class));
            }
        });
        instantiator("text-button", TextButton.class, new Instantiator<TextButton>() {
            {
                require("style", TextButton.TextButtonStyle.class);
            }

            @Override public TextButton newInstance(Resources resources) {
                return new TextButton(resources.get("text", ""), resources.get("style", TextButton.TextButtonStyle.class));
            }
        });
        instantiator("container", Container.class, new Instantiator<Container>() {

            @Override public Container newInstance(Resources resources) {
                return new Container(value.has("widget") ? (Actor) instantiate(value.get("widget")) : null);
            }
        });

        instantiator("vertical-list", VerticalList.class, new Instantiator<VerticalList>() {
            @Override public VerticalList newInstance(Resources resources) {
                VerticalList list = new VerticalList(skin);
                if (!value.has("elements"))
                    return list;
                for (JsonValue element : value.get("elements")) {
                    Actor actor = instantiate(element.get("widget"));
                    Cell cell = list.add(actor);
                    inject(cell, element);
                }
                return list;
            }
        });
        instantiator("horizontal-list", HorizontalList.class, new Instantiator<HorizontalList>() {
            @Override public HorizontalList newInstance(Resources resources) {
                HorizontalList list = new HorizontalList(skin);
                if (!value.has("elements"))
                    return list;
                for (JsonValue element : value.get("elements")) {
                    Actor actor = instantiate(element.get("widget"));
                    Cell cell = list.add(actor);
                    inject(cell, element);
                }
                return list;
            }
        });
        instantiator("group", Group.class, new Instantiator<Group>() {
            @Override public Group newInstance(Resources resources) {
                Group group = new Group();
                if (!value.has("elements"))
                    return group;
                for (JsonValue element : value.get("elements")) {
                    Actor actor = instantiate(element.get("widget"));
                    group.addActor(actor);
                }
                return group;
            }
        });
        instantiator("progressBar", ProgressBar.class, new Instantiator<ProgressBar>() {

            {
                require("style", ProgressBar.ProgressBarStyle.class);
            }

            @Override public ProgressBar newInstance(Resources resources) {
                return new ProgressBar(
                    value.getFloat("min", 0),
                    value.getFloat("max", 100),
                    value.getFloat("step", 1),
                    value.getBoolean("vertical", false),
                    resources.get("style", ProgressBar.ProgressBarStyle.class)
                );
            }
        });

        instantiator("scroll-pane", ScrollPane.class, new Instantiator<ScrollPane>() {
            @Override public ScrollPane newInstance(Resources resources) {
                return new ScrollPane(value.has("widget") ? (Actor) instantiate(value.get("widget")) : null);
            }
        });
    }

    public static ObjectMap<String, Applier> getAppliers(Object o) {
        return getAppliers(o.getClass());
    }

    public static ObjectMap<String, Applier> getAppliers(Class type) {
        ObjectMap<String, Applier> result = new ObjectMap<String, Applier>();
        while (type != null) {
            ObjectMap<String, Applier> typeAppliers = appliers.get(type);
            if (typeAppliers != null) {
                result.putAll(typeAppliers);
            }
            for (Class inf : type.getInterfaces()) {
                ObjectMap<String, Applier> interfaceAppliers = appliers.get(inf);
                if (interfaceAppliers != null) {
                    result.putAll(interfaceAppliers);
                }
            }
            type = type.getSuperclass();
        }
        return result;
    }

    static void inject(ObjectMap<Object, ObjectMap<String, Object>> fill, Object o, JsonValue v, Skin s) {
        Class type = o.getClass();
        ObjectMap<String, Object> injected = null;
        if (fill != null) {
            injected = fill.get(o);
            if (injected == null) {
                injected = new ObjectMap<String, Object>();
                fill.put(o, injected);
            }
        }
        while (type != null) {
            injectAs(injected, type, o, v, s);
            for (Class inf : type.getInterfaces()) {
                injectAs(injected, inf, o, v, s);
            }
            type = type.getSuperclass();
        }
    }

    @SuppressWarnings("unchecked")
    private static void injectAs(ObjectMap<String, Object> injected, Class type, Object o, JsonValue val, Skin s) {
        ObjectMap<String, Applier> typeAppliers = appliers.get(type);
        if (typeAppliers == null)
            return;
        for (JsonValue v : val) {
            Applier applier = typeAppliers.get(v.name());
            if (applier == null)
                continue;
            Object value = extract(applier.valueClass, v, s);
            if (injected != null) injected.put(v.name(), value);
            applier.apply(o, value);
        }
    }

    private static Resources extractResources(ObjectMap<String, Class> requirements, JsonValue value, Skin skin) {
        Resources resources = new Resources();
        for (ObjectMap.Entry<String, Class> entry : requirements.entries()) {
            JsonValue data = value.get(entry.key);
            if (data == null)
                throw new IllegalStateException("requirement not satisfied: there is no " + entry.key + " in " + value);
            Class valueClass = entry.value;
            resources.data.put(entry.key, extract(valueClass, data, skin));
        }
        return resources;
    }

    @SuppressWarnings("unchecked")
    private static Object extract(Class valueClass, JsonValue value, Skin skin) {
        if (valueClass == Float.class) {
            return value.asFloat();
        } else if (valueClass == Boolean.class) {
            return value.asBoolean();
        } else if (valueClass == Integer.class) {
            return value.asInt();
        } else if (valueClass == String.class) {
            return value.asString();
        } else if (valueClass == Color.class) {
            return Color.valueOf(value.asString());
        } else if (valueClass == Drawable.class) {
            return skin.getDrawable(value.asString());
        } else if (valueClass == TiledDrawable.class) {
            return skin.getTiledDrawable(value.asString());
        } else if (value.isString() && skin.has(value.asString(), valueClass)) {
            return skin.get(value.asString(), valueClass);
        } else if (valueClass.isEnum() && value.isString()) {
            Object enumConst = null;
            for (Object c : valueClass.getEnumConstants()) {
                if (c.toString().equals(value.asString())) {
                    enumConst = c;
                    break;
                }
            }
            if (enumConst == null)
                throw new IllegalStateException("no such enum: " + value.asString());
            return enumConst;
        } else if (instantiators.containsKey(valueClass)) {
            return instantiate(value, skin);
        } else if (value.has("type") && tags.containsKey(value.getString("type")) && valueClass.isAssignableFrom(tags.get(value.getString("type")))) {
            return instantiate(value, skin);
        } else {
            throw new IllegalStateException("can't extract " + valueClass + " from " + value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(JsonValue v, Skin s, ObjectMap<Object, ObjectMap<String, Object>> extractedParams) {
        String tag = v.getString("type");
        Class<?> type = tags.get(tag);
        Instantiator instantiator = instantiators.get(type);
        if (instantiator == null) {
            instantiator = reflectionInstantiator.withClass(type);
        }

        JsonValue prevValue = instantiator.value;
        Skin prevSkin = instantiator.skin;
        ObjectMap<Object, ObjectMap<String, Object>> prevParams = instantiator.params;
        instantiator.value = v;
        instantiator.skin = s;
        instantiator.params = extractedParams;
        Resources resources = extractResources(instantiator.requirements, v, s);
        Object o = instantiator.newInstance(resources);
        inject(extractedParams, o, v, s);
        instantiator.value = prevValue;
        instantiator.skin = prevSkin;
        instantiator.params = prevParams;
        return (T) o;
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(JsonValue v, Skin s) {
        return instantiate(v, s, null);
    }

    public static <A, T> void applier(String key, Class<A> objectType, Class<T> valueType, Applier<A, T> applier) {
        applier(key, objectType, valueType, null, applier);
    }

    public static <A, T> void applier(String key, Class<A> objectType, Class<T> valueType, T defaultValue, Applier<A, T> applier) {
        applier.objectClass = objectType;
        applier.valueClass = valueType;
        applier.defaultValue = defaultValue;
        ObjectMap<String, Applier> m = appliers.get(objectType);
        if (m == null) {
            m = new ObjectMap<String, Applier>();
            appliers.put(objectType, m);
        }
        if (m.put(key, applier) != null)
            throw new IllegalStateException("Key \"" + key + "\" already registered!");
    }

    public static boolean hasInstantiator(Class type) {
        return instantiators.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> Instantiator<T> instantiator(Class<T> type) {
        return instantiators.get(type);
    }

    public static String tag(Class type) {
        return tags.findKey(type, true);
    }

    public static <T> void instantiator(String tag, Class<T> objectType, Instantiator<T> instantiator) {
        instantiators.put(objectType, instantiator);
        tags.put(tag, objectType);
    }

    public static Applier<?, ?> applier(Class type, String key) {
        return getAppliers(type).get(key);
    }
}
