package com.vlaaad.ui.util.inputs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created 03.06.14 by vlaaad
 */
public abstract class TextInput<T> extends EditorInput<T> {

    private final TextField textField;
    private final boolean required;
    private String prev = "";

    protected TextInput(boolean required, boolean isDefault, T initialValue, Skin editorSkin) {
        super(initialValue);
        this.required = required;

        textField = new TextField(isDefault || dispatcher.getState() == null ? "" : String.valueOf(dispatcher.getState()), editorSkin, required ? "required" : "default");
        if (isDefault)
            textField.setMessageText(dispatcher.getState() == null ? "" : String.valueOf(dispatcher.getState()));

        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override public void keyTyped(TextField textField, char c) {
                if (!acceptInput(textField.getText())) {
                    textField.setText(prev);
                } else {
                    prev = textField.getText();
                }
                if (textField.getText().length() == 0) {
                    dispatcher.setState(null);
                } else {
                    dispatcher.setState(toValue(textField.getText()));
                }
            }
        });
    }

    protected boolean acceptInput(String currentText) {
        if (required && currentText.length() == 0)
            return false;
        if (currentText.length() == 0)
            return true;
        try {
            toValue(currentText);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected abstract T toValue(String text);

    @Override public Actor getActor() {
        return textField;
    }
}
