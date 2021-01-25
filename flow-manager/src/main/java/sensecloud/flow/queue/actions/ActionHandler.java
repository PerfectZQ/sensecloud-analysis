package sensecloud.flow.queue.actions;

@FunctionalInterface
public interface ActionHandler<P, R> {

    R handle(P params);
}
