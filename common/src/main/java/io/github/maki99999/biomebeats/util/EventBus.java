package io.github.maki99999.biomebeats.util;

import io.github.maki99999.biomebeats.event.Event;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventBus {
    private static final Map<Class<?>, List<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    public static <T extends Event> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public static <T extends Event> void publish(T event) {
        List<Consumer<?>> eventListeners = listeners.getOrDefault(event.getClass(), Collections.emptyList());

        for (Consumer<?> listener : eventListeners) {
            @SuppressWarnings("unchecked")
            Consumer<T> typedListener = (Consumer<T>) listener;  // Safe cast
            typedListener.accept(event);
        }
    }

    public static <T extends Event> void unsubscribe(Class<T> eventType, Consumer<T> listener) {
        List<Consumer<?>> eventListeners = listeners.get(eventType);
        if (eventListeners != null) {
            eventListeners.remove(listener);
            if (eventListeners.isEmpty()) {
                listeners.remove(eventType);
            }
        }
    }
}
