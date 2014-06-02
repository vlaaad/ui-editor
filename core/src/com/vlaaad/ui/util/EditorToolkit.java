package com.vlaaad.ui.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.tablelayout.Cell;
import com.vlaaad.ui.util.inputs.EditorInput;
import com.vlaaad.ui.util.inputs.EmptyInput;
import com.vlaaad.ui.util.inputs.EnumInput;
import com.vlaaad.ui.util.inputs.TextInput;
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
        factory(Cell.class, new WrapperFactory<Cell>() {
            @Override protected Object getWrapped(Cell cell) {
                return cell.getWidget();
            }
        });
        factory(Table.class, new CollectionFactory<Table>() {
            @Override protected Iterable<?> getElements(Table table) {
                return table.getCells();
            }
        });
    }

    static {
        input(Float.class, new EditorInputFactory<Float>() {
            @Override public EditorInput<Float> create(final Float initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new TextInput<Float>(initialValue, editorSkin) {
                    @Override protected Float toValue(String text) {
                        return Float.valueOf(text);
                    }
                };
            }
        });
        input(Integer.class, new EditorInputFactory<Integer>() {
            @Override public EditorInput<Integer> create(final Integer initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new TextInput<Integer>(initialValue, editorSkin) {
                    @Override protected Integer toValue(String text) {
                        return Integer.valueOf(text);
                    }
                };
            }
        });
        input(String.class, new EditorInputFactory<String>() {
            @Override public EditorInput<String> create(final String initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new TextInput<String>(initialValue, editorSkin) {
                    @Override protected String toValue(String text) {
                        return text;
                    }
                };
            }
        });
        input(Boolean.class, new EditorInputFactory<Boolean>() {
            @Override public EditorInput<Boolean> create(final Boolean initialValue, Skin layoutSkin, final Skin editorSkin) {
                return new EditorInput<Boolean>(initialValue) {
                    private final CheckBox checkBox = new CheckBox("", editorSkin);

                    {
                        checkBox.setChecked(initialValue == null ? false : initialValue);
                        checkBox.addListener(new ChangeListener() {
                            @Override public void changed(ChangeEvent event, Actor actor) {
                                dispatcher.setState(checkBox.isChecked());
                            }
                        });
                    }

                    @Override public Actor getActor() {
                        return checkBox;
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
    public static <T> EditorInput<T> createInput(T initialValue, Class<T> type, Skin layoutSkin, Skin editorSkin) {
        if (type.isEnum()) {
            return new EnumInput<T>(type.getEnumConstants(), initialValue, editorSkin);
        }
        EditorInputFactory<T> factory = inputs.get(type);
        if (factory != null) {
            return factory.create(initialValue, layoutSkin, editorSkin);
        }
        return new EmptyInput<T>(initialValue, editorSkin);
    }

    @SuppressWarnings("unchecked")
    public static <T> EditorModel<T> createModel(T t, ObjectMap<Object, ObjectMap<String, Object>> params) {
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

    public static String dump(EditorModel<?> model, Skin skin) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        dump(jsonWriter, model, skin);
        JsonValue.PrettyPrintSettings settings = new JsonValue.PrettyPrintSettings();
        settings.outputType = JsonWriter.OutputType.json;
        settings.singleLineColumns = 60;
        return new JsonReader().parse(stringWriter.toString()).prettyPrint(settings);
    }

    public static void dump(JsonWriter writer, EditorModel<?> model, Skin skin) {
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
