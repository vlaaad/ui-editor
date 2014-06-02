package com.vlaaad.ui.util.inputs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

/**
 * Created 03.06.14 by vlaaad
 */
public abstract class TextInput<T> extends EditorInput<T> {

    private final TextField textField;

    protected TextInput(T initialValue, Skin editorSkin) {
        super(initialValue);
        textField = new TextField(dispatcher.getState() == null ? "" : String.valueOf(dispatcher.getState()), editorSkin);
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override public boolean acceptChar(TextField textField, char c) {
                return acceptInput(textField.getText(), c);
            }
        });
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override public void keyTyped(TextField textField, char c) {
                if (textField.getText().length() == 0) {
                    dispatcher.setState(null);
                } else {
                    dispatcher.setState(toValue(textField.getText()));
                }
            }
        });
    }

    protected boolean acceptInput(String currentText, char c) {
        try {
            toValue(currentText + c);
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
