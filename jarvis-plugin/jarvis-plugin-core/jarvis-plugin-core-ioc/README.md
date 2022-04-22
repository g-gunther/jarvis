# jarvis-plugin-core-ioc

This module enables the dependency injection features. 
Basically it scans packages starting by a given package root name, and look for all classes annotated with `@Named`.
This is done by using the class `ApplicationContextManager` to start the process.

Each of the found classes is then associated to a `BeanContext` which is responsible for instantiating the class and resolves its dependencies.
There are 2 kinds of bean context for now:

- singleton: which is the one by default, used when the class is only annotated by `@Named`. It will create only one instance of the bean and will always return it.
- prototype: used when the class is also annotated with the `@Prototype` annotation. It will create as many instances as injected. Every time the bean is injected, a new one is created.

Additionally, the class can also be annotated with:

- `@Qualifer(value)`: used during injection to specify which bean to inject by its name
- `@Order(value)`: to specify a position in the list in case a list of beans is injected

## Dependencies

The bean context resolves the dependencies by looking for a constructor annotated with `@Inject` (which is optional if there is only 1 constructor) and resolve the constructor parameters as dependencies. 
Each found property type should by found as a bean in the context, else an exception is thrown (unless the property is an Optional, in that case the bean might be an Optional.empty()). 

The `@Qualifer(value)` can be used with the `@Inject` annotation to specify the bean to inject in can of several interface implementation. 

It is also possible to inject a list of beans. Every bean that implements/extends the provided type will be injected in that list.

To interact directly with the context, it is possible to inject the bean `ApplicationContext`. It provides methods to retrieve a bean for example.

## Lifecycle

### PostConstruct

The bean might implemented the `OnPostConstruct` interface which expose only one method: `postConstruct`.
This method is executed after the bean initialization (which is quite similar to a call from the constructor directly).