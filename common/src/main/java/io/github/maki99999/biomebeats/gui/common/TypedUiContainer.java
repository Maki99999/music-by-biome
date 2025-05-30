package io.github.maki99999.biomebeats.gui.common;

import io.github.maki99999.biomebeats.gui.util.Rect;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class TypedUiContainer<T extends UiElement> extends UiElement {
    private final List<T> typedChildren = new ArrayList<>();

    protected TypedUiContainer(Component name) {
        super(name);
    }

    protected TypedUiContainer(Component name, Rect bounds) {
        super(name, bounds);
    }

    public List<T> getTypedChildren() {
        return typedChildren;
    }

    public void addTypedChild(T child) {
        typedChildren.add(child);
        addChild(child);
    }

    public void removeTypedChild(T child) {
        typedChildren.remove(child);
        removeChild(child);
    }
}
