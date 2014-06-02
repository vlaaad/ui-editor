package com.vlaaad.ui.util;

/**
 * Created 18.05.14 by vlaaad
 */
public interface IStateDispatcher<T> {

    void addListener(boolean notifyImmediately, Listener<T> listener);

    void removeListener(Listener<T> listener);

    void clearListeners();

    T getState();

    interface Listener<T> {
        void onChangedState(T newState);
    }
}
