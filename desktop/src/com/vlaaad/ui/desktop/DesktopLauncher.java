package com.vlaaad.ui.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.vlaaad.ui.UiEditor;

public class DesktopLauncher {
	public static void main (String[] arg) throws Exception {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 960;
        config.height = 540;
		new LwjglApplication(new UiEditor(), config);
	}
}
