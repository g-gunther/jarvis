package com.gguproject.jarvis.master;

import com.gguproject.jarvis.core.ioc.ApplicationContextManager;
import com.gguproject.jarvis.core.ioc.context.ApplicationContext;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.JarvisLoggerConfigurationFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.master.channel.TcpCommandReceiver;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

import java.io.IOException;

public class MasterApplicationStartup {
    static {
        System.setProperty(ConfigurationFactory.CONFIGURATION_FACTORY_PROPERTY, JarvisLoggerConfigurationFactory.class.getName());
    }

    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(MasterApplicationStartup.class);

    private static Object lock = new Object();

    public static void main(String[] args) throws IOException {
        AbstractLoggerFactory.init("master");
        new MasterApplicationStartup().run();
    }

    private MasterApplicationStartup() {
    }

    private void run() {
        ApplicationContext context = ApplicationContextManager.get()
                .scan("com.gguproject.jarvis")
                .getContext();

        context.getBean(TcpCommandReceiver.class).start();
        context.getBean(SystemCommandReceiver.class).start();

        // lock the main thread to avoid ending it
        /*try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Error on locking", e);
        }*/
    }
}
