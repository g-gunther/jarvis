package com.gguproject.jarvis.plugin.core.dev;

import com.gguproject.jarvis.core.bus.EventBusMock;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.utils.DevUtils;
import com.gguproject.jarvis.plugin.core.PluginApplicationContextAware;
import com.gguproject.jarvis.plugin.core.PluginApplicationContextLoader;
import com.gguproject.jarvis.plugin.spi.data.PluginData;

public abstract class AbstractPluginDevStartup {
    private Object lock = new Object();

    /**
     * @param pluginName
     */
    protected static void init(String pluginName) {
        DevUtils.setDevEnvironment();
        AbstractLoggerFactory.init(pluginName);
        PluginApplicationContextLoader context = new PluginApplicationContextLoader();
        context.initialize(new PluginData(pluginName, "0.0.1-SNAPHOST"), new EventBusMock());

        AbstractPluginDevStartup pluginDevStartup = PluginApplicationContextAware.getApplicationContext().getBean(AbstractPluginDevStartup.class);
        pluginDevStartup.process();
        pluginDevStartup.lockProcess();
    }

    /**
     *
     */
    protected void lockProcess() {
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
        }
    }

    /**
     *
     */
    protected abstract void process();
}
