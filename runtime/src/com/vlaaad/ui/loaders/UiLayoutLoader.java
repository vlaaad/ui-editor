package com.vlaaad.ui.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.vlaaad.ui.UiLayout;

/**
 * Created 29.05.14 by vlaaad
 */
public class UiLayoutLoader extends AsynchronousAssetLoader<UiLayout, UiLayoutLoader.UiLayoutParameter> {

    public UiLayoutLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override public void loadAsync(AssetManager manager, String fileName, FileHandle file, UiLayoutParameter parameter) {
    }

    @Override public UiLayout loadSync(AssetManager manager, String fileName, FileHandle file, UiLayoutParameter parameter) {
        return new UiLayout(file, manager.get(file.nameWithoutExtension() + ".json", Skin.class));
    }

    @Override public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, UiLayoutParameter parameter) {
        return Array.with(new AssetDescriptor(file.sibling(file.nameWithoutExtension() + ".json"), Skin.class));
    }

    public static class UiLayoutParameter extends AssetLoaderParameters<UiLayout> {

    }
}
