# jarvis-plugin-core-bus

This project contains classes and services to emit events that can be dispatched 

- internally to the current application instance and processed by installed plugins
- externally to the other application instances

The objective is to enable the communication between plugins without letting them know that a plugin is installed and where.

## External

Several instances of the application with various plugins may run across the house and all connected to the same network.
To avoid maintaining a registry of all the running instances, the list of their plugins... they communicate by broadcasting events using multicast.
It basically means when an instance needs to send an event to other instances, this event is serialized, then send over the network on ip address `224.0.0.1` on port `9999`
and every other application listening on that port (if any) will receive and, deserialize it and process it. 

**Note**: when emitting an event externally, it will also be send internally to be processed by all plugins.

A received event implements the interface `com.gguproject.jarvis.core.bus.support.DistributedEvent` which is an envelope containing the following information:

- timestamp: send date of this event
- uuid: unique sender identifier - used to check if the received event has been sent by the same application to avoid processing it internally twice. 
- eventSender: ip address and port that can be used to reply directly to the sender.
- data: serialized content of the event.

Since a DistributedEvent contains the sender connection information, it is possible to answer directly without having to broadcast to all other instances. 
It can be done using the `eventSender` property. By default, every instance listen to TCP messages on port `9997` and then propagate the event as multicast events.

Each received DistributedEvent are then propagated to all plugins of the instance using the `EventBus` described below.

## Internal

Since every plugin is independent of the others but still need to interact with the other plugins or systems, an event bus is created (in the plugin `jarvis-plugin-loader` which loads all the other plugins) and shared by all the plugins. 
This event bus allows us to register or unregister event listeners and emit events.

An event has to extend the class `com.gguprojec.jarvis.core.bus.support.EventData` and is identified by its type and a unique name. 
It can then have attributes that characterized it.

```java
public class ExampleEventData extends EventData {
    private static final long serialVersionUID = -6841671964817624557L;
    public static final String eventType = "EXAMPLE_EVENT";

    /** Value to transmit */
    private String value;

    /** Empty constructor needed to deserialize the event */
    public ExampleEventData() {
        super(eventType, ExampleEventData.class);
    }

    public ExampleEventData(String value) {
        this();
        this.value = value;
    }
}
```

Then a plugin which has to listen to some particular events has to create listener beans by implementing the interface `com.gguproject.jarvis.core.bus.EventListener`.
There is an abstract class implementing it to make things easier `com.gguproject.jarvis.core.bus.AbstractEventListener`.
Those event listeners need to specify which type of event they are looking at.

```java
@Named
public class ExampleEventListener extends AbstractEventListener<ExampleEventData> {

    @Inject
    private SomeService someService;

    public ExampleEventListener() {
        super(ExampleEventData.eventType, ExampleEventData.class);
    }

    @Override
    public void onEvent(DistributedEvent event, ExampleEventData data) {
        this.someService.process(data.getValue());
    }
}
```

Event listeners are automatically registered in the event bus (because they implement the EventListener interface).
When an event has to be processed, the EventBus looks for all registered listeners for this particular event type and call each of them.

## Event emission

To emit an event in this internal bus, the `EventBusService` can be used. It provides useful methods:

- emit(DistributedEvent event): to emit a raw event - this shouldn't be used, prefer the `emit(EventData data)` method instead. This is the method called when receiving an external event. It deserializes the raw data to an `EventData` object.
- emit(EventData data): to emit an event internally to plugin listeners of this application instance.
- externalEmit(EventData data): to emit an event using multicast to all other application instances. It also emits the event internally.
- externalEmit(EventData, EventSender target): to emit an event to a specific target identified by an IP address and port.
- emitAndWait(DistributedEvent event): to emit a raw event and wait for the response of listeners - this shouldn't be used, prefer the `emitAndWait(EventData data)` method instead. 
- emitAndWait(EventData data): to emit an event internally to plugin listeners of this application instance and wait for their responses.

## Implementation details

There are 3 projects:

- **javis-plugin-core-bus-api**: contains only the bus and listener interfaces and related dto. It is used as the API of this feature.
- **javis-plugin-core-bus-base**: contains the services, mock and listener abstract implementations that helps the use of this feature inside plugins. Basically each plugin depends on this project.
- **javis-plugin-core-bus-impl**: contains the implementation of the `EventBus`. This is used to create the bus and inject it to all plugins. The creation is only done once by the `jarvis-plugin-loader` plugin. 

---
**Note**: all plugins has to share the same version of `jarvis-plugin-core-bus-api` else the plugin loader won't be able to inject the implementation of the `EventBus`.   
---

An event has to extend the class `com.gguprojec.jarvis.core.bus.support.EventData` which provides 2 methods: 

- serialize: which serialize the current event following the format: `[event type]@[event data]` where the *event type* is the unique name that defines this type of event and *event data* is the object serialize as JSON. 
- deserialize: deserialize the *event data* from JSON format to an instance of the object itself.

The `EventBusService` injects itself the list of `AbstractEventListener` that has been found in the current context (which is equivalent to the plugin context)
and then registers all of those listeners in the `EventBus`. 

When emitting an event on the bus, it will 

1. look for all listeners `EventListener` that can handle the provided *event type*.
2. call each of those found listeners using a pool of threads to process them in parallel (the current implementation creates a pool of 2 threads only). 
3. since all listeners are called asynchronously, it returns a list of `Future` of `Optional`. Each listener may or may not choose to return a value after the event processing.

There is a particular event `ExternalEventData` used when an event has to be broadcasted to all application instances. Its usage is hidden by methods *externalEmit* of the `EventBusService`.
The associated listener is implemented in `jarvis-plugin-loader`. 





 