package com.gguproject.jarvis.plugin.freeboxv6.event.analyser.processor;

import com.gguproject.jarvis.plugin.freeboxv6.event.event.InfraredCommand;

/**
 * Factory interface to build a {@link CommandProcessor}
 */
public interface CommandProcessorFactory<T extends CommandProcessor> {

    public boolean match(String context, String action);

    /**
     * Build a {@link CommandProcessor} based on the json object
     *
     * @param object
     * @return
     */
    public T build(InfraredCommand command);
}
