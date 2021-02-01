package sensecloud.event;

@FunctionalInterface
public interface EventHandler {

    void handle(Event e) ;

}
