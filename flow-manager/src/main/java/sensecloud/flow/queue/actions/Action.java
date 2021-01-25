package sensecloud.flow.queue.actions;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public abstract class Action<R, P> {

    @Getter
    protected ActionHandler<R, P> handler;

    @Getter @Setter
    protected String name;

    protected Action(String name) {
        this.name = name;
    }

    public abstract R act(P p);

}
