package com.vlaaad.ui.util;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.*;
import com.esotericsoftware.tablelayout.Cell;

import java.io.StringWriter;

/**
 * Created 01.06.14 by vlaaad
 */
public class EditorToolkit {

    private static final Json json = new Json();

    static {
        json.setUsePrototypes(false);
        json.setTypeName(null);
        json.setOutputType(JsonWriter.OutputType.json);
    }

    private static final ObjectMap<Class, Factory> models = new ObjectMap<Class, Factory>();
    private static final ObjectMap<Class, Stringifier> stringifiers = new ObjectMap<Class, Stringifier>();
    private static final ObjectMap<Class, Saver> savers = new ObjectMap<Class, Saver>();

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


    @SuppressWarnings("unchecked")
    public static <T> EditorModel<T> createModel(T t, ObjectMap<Object, ObjectMap<String, Object>> params) {
        Class type = t.getClass();
        while (type != null) {
            Factory f = models.get(type);
            if (f != null) {
//                System.out.println("build model for " + t + " using class " + type);
                return f.newInstance(t, params);
            }
            type = type.getSuperclass();
        }
        throw new IllegalStateException();
    }

    private static <T> void factory(Class<T> type, Factory<T> factory) {
        models.put(type, factory);
    }

    public static String dump(EditorModel<?> model, Skin skin) {
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        dump(jsonWriter, model, skin);
        JsonValue.PrettyPrintSettings settings = new JsonValue.PrettyPrintSettings();
        settings.outputType = JsonWriter.OutputType.json;
        settings.singleLineColumns = 60;
        return new JsonReader().parse(stringWriter.toString()).prettyPrint(settings);
//        return stringWriter.toString();
    }

    public static void dump(JsonWriter writer, EditorModel<?> model, Skin skin) {
        try {
            writer.object();
            writer.set("type", Toolkit.tag(model.obj().getClass()));
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

    public interface Saver<T> {
        void save(T v, Skin skin);
    }

    public abstract static class Factory<T> {
        public final EditorModel<T> newInstance(T t, ObjectMap<Object, ObjectMap<String, Object>> params) {
            return create(t, params(t, params), params);
        }

        protected final ObjectMap<String, Object> params(Object o, ObjectMap<Object, ObjectMap<String, Object>> params) {
            return params.containsKey(o) ? params.get(o) : new ObjectMap<String, Object>();
        }

        public abstract EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full);
    }

    public static class ElementFactory<T> extends Factory<T> {

        @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
            return new Element<T>(t, params);
        }
    }

    public static abstract class WrapperFactory<T> extends Factory<T> {

        @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
            Object wrapped = getWrapped(t);
            return new Wrapper<T, Object>(t, params, wrapped == null ? null : createModel(wrapped, full));
        }

        protected abstract Object getWrapped(T t);
    }

    public static abstract class CollectionFactory<T> extends Factory<T> {
        @Override public EditorModel<T> create(T t, ObjectMap<String, Object> params, ObjectMap<Object, ObjectMap<String, Object>> full) {
            Array<EditorModel<Object>> arr = new Array<EditorModel<Object>>();
            for (Object element : getElements(t)) {
                arr.add(createModel(element, full));
            }
            return new Collection<T, Object>(t, params, arr);
        }

        protected abstract Iterable<? extends Object> getElements(T t);
    }

    public abstract static class Stringifier<T> {
        public abstract String toString(T value, Skin skin);
    }
}
