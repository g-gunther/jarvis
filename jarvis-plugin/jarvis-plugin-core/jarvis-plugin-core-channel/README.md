# jarvis-plugin-core-channel

This project allow the application to send and receive multicast and TCP data.
It is divided into 2 sub projects:

**utils**: contains only tools, interfaces, abstract classes used for communication. These interfaces and abstract classes needs to be implemented. 
This is done in 2 projects, the **impl** project which is the default implementation (for the main application) and the *jarvis-app-master* (used to remotely control application instances).

## Multicast

### Sender / Manager 

The `MulticastManagerImpl` is the main entry point for multicast events. It exposes 2 methods to publish an event (string or `EventData` object which will be serialized).
The publishing sends first the event on the network and then publish it internally on the bus.

There also 2 classes which are used to write or read datagam packets:

- DatagramOutputStream: used by `MulticastRequestEvent` to write fields in order, each fields has a specific length
- DatagramInputStream: used by `MulticastResponseEvent` to read fields and build an event response

The fields of this datagram packet are the following:

- magic number: ASCII value for "demp" (Distributed Events Management Protocol) - used to check that the event is one of ours
- version: version of the protocol which is supported
- ipv4 address: ip address of the sender (ipv4)
- port: port of the sender which is open to receive tcp events
- timestamp: current timestamp
- uuid: JVM unique identifier
- data: data event serialize as string (can be json)

### Receiver

At startup, it also starts the `MulticastReceiver` in a dedicated thread to listen to broadcasted events across the network.

Because the event is also published internally, when an event is received we need to check if that event has been published by the same JVM or not, else it will be processed twice. 
To avoid that, each event has some metadata including a *uuid* (initialized in `com.gguproject.jarvis.core.events.support.JVMUtils`). This *uuid* is unique and identifies the current application instance. 
Each event sent by this instance will have the same *uuid* value.

## TCP

### Sender / Manager 

Like for multicast, the `TcpManagerImpl` is the main entry point for TCP events. 
It exposes several methods to send data (String or `EventData` object that will be serialized) to a remote application (identified by an ip address and port)
or to answer to a previously received events (example: the application A receives a multicast event from application B, it can then answer to the event. It will then send an event to application B). 

The TCP events is converted to the outputstream with the following fields:

- ipv4 address: ip address of the sender (ipv4)
- port: port of the sender which is open to receive tcp events
- timestamp: current timestamp
- uuid: JVM unique identifier
- data: data event serialize as string (can be json)

### Receiver

At startup, it also starts the `TcpReceiver` in a dedicated thread to wait for events. The events will then be send in the event bus to be processed internally.
It listens to the port configured in `jarvis-app-loader` -> resources/jarvis/configuration.properties, property: `tcp.server.port`.

## Known issue

During startup, an error may occurs because the JVM prefers IPv6 over IPv4. Try using this option: `-Djava.net.preferIPv4Stack=true`