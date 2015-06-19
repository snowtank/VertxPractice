package me.zcx.vertx.http;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by zcx2001 on 2015-06-19.
 */
public final class IterableQueueCommand<T> {
    public IterableQueueCommand(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public IterableQueueCommand onAction(Consumer<IterableQueueCommand> action) {
        this.action = action;
        return this;
    }

    public IterableQueueCommand onEnd(Consumer<IterableQueueCommand> end) {
        this.end = end;
        return this;
    }

    public void start() {
        if (iterator.hasNext()) {
            value = iterator.next();
            action.accept(this);
        } else {
            end.accept(this);
        }
    }

    public T getValue() {
        return value;
    }

    public void next() {
        start();
    }

    private T value;

    private Consumer<IterableQueueCommand> action;
    private Consumer<IterableQueueCommand> end;
    private Iterator<T> iterator;
}
