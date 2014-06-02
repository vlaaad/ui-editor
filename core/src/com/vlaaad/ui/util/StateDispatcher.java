package com.vlaaad.ui.util;

import com.badlogic.gdx.utils.SnapshotArray;

/**
 * Created 18.05.14 by vlaaad
 */
public class StateDispatcher<T> implements IStateDispatcher<T> {

    private T state;
    private SnapshotArray<Listener<T>> listeners = new SnapshotArray<Listener<T>>(Listener.class);

    public StateDispatcher(T initialState) {
        state = initialState;
    }

    @Override public void addListener(boolean notifyImmediately, Listener<T> listener) {
        if (!listeners.contains(listener, true))
            listeners.add(listener);
        if (notifyImmediately) listener.onChangedState(state);
    }

    @Override public void removeListener(Listener<T> listener) {
        listeners.removeValue(listener, true);
    }

    @Override public void clearListeners() {
        listeners.clear();
    }

    @Override public T getState() {
        return state;
    }

    @SuppressWarnings("unchecked")
    public boolean setState(T t) {
        if (state == t)
            return false;
        state = t;
        Listener[] items = listeners.begin();
        for (int i = 0, n = listeners.size; i < n; i++) {
            items[i].onChangedState(t);
        }
        listeners.end();
        return true;
    }

}
