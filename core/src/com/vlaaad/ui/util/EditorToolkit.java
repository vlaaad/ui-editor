package com.vlaaad.ui.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.vlaaad.ui.util.inputs.*;
import com.vlaaad.ui.util.inputs.factories.EditorInputFactory;
import com.vlaaad.ui.util.models.CollectionFactory;
import com.vlaaad.ui.util.models.EditorModelFactory;
import com.vlaaad.ui.util.models.ElementFactory;
import com.vlaaad.ui.util.models.WrapperFactory;

import java.io.StringWriter;

/**
 * Created 01.06.14 by vlaaad
 */
public class EditorToolkit {

    private static final ObjectMap<Class, EditorModelFactory> factories = new ObjectMap<Class, EditorModelFactory>();

    private static final ObjectMap<Class, EditorInputFactory> inputs = new ObjectMap<Class, EditorInputFactory>();

    static {
        factory(Object.class, new ElementFactory<Object>());
        factory(TextButton.class, new ElementFactory<TextButton>());
        factory(Cell.class, new WrapperFactory<Cell, Actor>(Actor.class) {
            @Override protected Actor getWrapped(Cell cell) {
                return (Actor) cell.getActor();
            }
        });
        factory(Table.class, new CollectionFactory<Table, Cell>(Cell.class) {
            @Override protected Iterable<Cell> getElements(Table table) {
                return table.getCells();
            }
        });
        factory(ScrollPane.class, new WrapperFactory<ScrollPane, Actor>(Actor.class) {
            @Override protected Actor getWrapped(ScrollPane scrollPane) {
                return scrollPane.getWidget();
            }
        });
        factory(Container.class, new WrapperFactory<Container, Actor>(Actor.class) {
            @Override protected Actor getWrapped(Container container) {
                return container.getActor();
            }
        });
        factory(Group.class, new CollectionFactory<Group, Actor>(Actor.class) {
            @Override protected Iterable<? extends Actor> getElements(Group group) {
                return group.getChildren();
            }
        });
    }

    static {
        input(Float.class, new EditorInputFactory<Float>() {
            @Override public EditorInput<Float> create(boolean required, boolean isDefault, final Float initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new TextInput<Float>(required, isDefault, initialValue, editorSkin) {
                    @Override protected Float toValue(String text) {
                        return text.equals("-") ? 0 : Float.valueOf(text);
                    }
                };
            }
        });
        input(Integer.class, new EditorInputFactory<Integer>() {
            @Override public EditorInput<Integer> create(boolean required, boolean isDefault, final Integer initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new TextInput<Integer>(required, isDefault, initialValue, editorSkin) {
                    @Override protected Integer toValue(String text) {
                        return text.equals("-") ? 0 : Integer.valueOf(text);
                    }
                };
            }
        });
        input(String.class, new EditorInputFactory<String>() {
            @Override public EditorInput<String> create(boolean required, boolean isDefault, final String initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new TextInput<String>(required, isDefault, initialValue, editorSkin) {
                    @Override protected String toValue(String text) {
                        return text;
                    }
                };
            }
        });
        input(Boolean.class, new EditorInputFactory<Boolean>() {
            @Override public EditorInput<Boolean> create(boolean required, boolean isDefault, final Boolean initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new EditorInput<Boolean>(initialValue) {
                    private final CheckBox checkBox = new CheckBox("off", editorSkin);

                    {
                        checkBox.setChecked(initialValue == null ? false : initialValue);
                        checkBox.setText(checkBox.isChecked() ? "on" : "off");
                        checkBox.addListener(new ChangeListener() {
                            @Override public void changed(ChangeEvent event, Actor actor) {
                                checkBox.setText(checkBox.isChecked() ? "on" : "off");
                                dispatcher.setState(checkBox.isChecked());
                            }
                        });
                    }

                    @Override public Actor getActor() {
                        return checkBox;
                    }

                    @Override public void update(Object value) {
                        if (value instanceof Boolean) {
                            checkBox.setChecked(((Boolean) value));
                        }
                    }
                };
            }
        });
    }

    public static <T> void factory(Class<T> type, EditorModelFactory<T> factory) {
        factories.put(type, factory);
    }

    public static <T> void input(Class<T> type, EditorInputFactory<T> inputFactory) {
        inputs.put(type, inputFactory);
    }

    @SuppressWarnings("unchecked")
    public static <T> EditorInput<T> createInput(boolean required, boolean isDefault, T initialValue, Class<T> type, Skin layoutSkin, Skin editorSkin) {
        if (type.isEnum()) {
            return new EnumInput<T>(required, type.getEnumConstants(), initialValue, editorSkin);
        }
        EditorInputFactory<T> factory = inputs.get(type);
        if (factory != null) {
            return factory.create(required, isDefault, initialValue, layoutSkin, editorSkin);
        }

        ObjectMap<String, T> resources = layoutSkin.getAll(type);
//        for(Object r : layoutSkin.)
        if (resources != null) {
            if (type == Drawable.class) {
                for (ObjectMap.Entry<String, TextureRegion> t : layoutSkin.getAll(TextureRegion.class)) {
                    if (!resources.containsKey(t.key))
                        resources.put(t.key, (T) layoutSkin.getDrawable(t.key));
                }
            }
            return new SkinInput<T>(required, resources, initialValue, editorSkin);
        }
        return new EmptyInput<T>(initialValue, editorSkin);
    }

    @SuppressWarnings("unchecked")
    public static EditorModel createModel(Object t, ObjectMap<Object, ObjectMap<String, Object>> params) {
        Class type = t.getClass();
        while (type != null) {
            EditorModelFactory f = factories.get(type);
            if (f != null) {
//                System.out.println("build model for " + t + " using class " + type);
                return f.newInstance(t, params);
            }
            type = type.getSuperclass();
        }
        throw new IllegalStateException();
    }

    public static String dump(EditorModel model, Skin skin) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        dump(jsonWriter, model, skin);
        JsonValue.PrettyPrintSettings settings = new JsonValue.PrettyPrintSettings();
        settings.outputType = JsonWriter.OutputType.json;
        settings.singleLineColumns = 60;
        return new JsonReader().parse(stringWriter.toString()).prettyPrint(settings);
    }

    public static void dump(JsonWriter writer, EditorModel model, Skin skin) {
        try {
            writer.object();
            String tag = Toolkit.tag(model.obj().getClass());
            if (tag != null)
                writer.set("type", tag);
            for (ObjectMap.Entry<String, Object> entry : model.params()) {
                writer.set(entry.key, simplify(entry.value, skin));
            }
            model.dump(writer, skin);
            writer.pop();
        } catch (Exception e) {
            throw new RuntimeException(
                "failed to write model " + model + "\n" +
                    "wrote: " + writer.getWriter().toString(), e
            );
        }
    }

    public static Object simplify(Object o, Skin skin) {
        if (o == null)
            return null;
        Class knownType = o.getClass();
        if (o.getClass().isPrimitive() || knownType == String.class || knownType == Integer.class
            || knownType == Boolean.class || knownType == Float.class || knownType == Long.class || knownType == Double.class
            || knownType == Short.class || knownType == Byte.class || knownType == Character.class)
            return o;
        String skinElement = skin.find(o);
        if (skinElement != null)
            return skinElement;
        String name = skin.find(o);
        if (name != null)
            return name;
        if (o instanceof Drawable) {
            for (ObjectMap.Entry<String, Drawable> entry : skin.getAll(Drawable.class)) {
                if (entry.value == o)
                    return entry.key;
            }
        }
        if (o instanceof TextureRegionDrawable) {
            TextureRegion region = ((TextureRegionDrawable) o).getRegion();
            for (ObjectMap.Entry<String, TextureRegion> entry : skin.getAll(TextureRegion.class)) {
                if (entry.value == region)
                    return entry.key;
            }
        }
        return o.toString();
    }

}
