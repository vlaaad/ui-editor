package com.vlaaad.ui;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Created 29.05.14 by vlaaad
 */
public class Generator {
    public static void main(String[] args) {
        TexturePacker.process("loader", "../../core/assets", "loader");
        TexturePacker.process("ui", "../../core/assets", "ui");
    }
}
