package com.gguproject.jarvis;

import com.gguproject.jarvis.core.repository.JarVersionRepositoryService;
import com.gguproject.jarvis.core.repository.dto.JarDto;
import com.gguproject.jarvis.core.utils.JarUtils;
import com.gguproject.jarvis.core.utils.dto.JarNameProperties;
import com.gguproject.jarvis.plugin.spi.PluginSystemService;
import com.gguproject.jarvis.plugin.spi.data.PluginData;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Main entry point of the application
 */
public class ApplicationStartup {
    private final static Logger LOGGER = Logger.getLogger(ApplicationStartup.class.getName());

    private static Object lock = new Object();

    private static final String pluginLoaderName = "jarvis-plugin-loader";

    public static void main(String[] args) throws IOException {
        InputStream loggerProperties = ApplicationStartup.class.getClassLoader().getResourceAsStream("logger.properties");
        LogManager.getLogManager().readConfiguration(loggerProperties);

        new ApplicationStartup().run();
    }

    private void run() throws IOException {
        LOGGER.info("Starting");

        File jarvisPluginLoaderDirectory = new File("plugins/" + pluginLoaderName);
        File jarvisPluginLoader = null;

        if (!jarvisPluginLoaderDirectory.exists() || !jarvisPluginLoaderDirectory.isDirectory()) {
            LOGGER.info("No jarvis-plugin-loader found - load it from repository");

            if (!JarVersionRepositoryService.checkRepositoryExists()) {
                throw new IllegalStateException("No repository found on network - leave");
            }

            JarDto latestJarDto = JarVersionRepositoryService.getLatestVersion(pluginLoaderName);
            File downloadedJarFile = JarVersionRepositoryService.downloadLatestVersion(pluginLoaderName);
            jarvisPluginLoader = new File(jarvisPluginLoaderDirectory, latestJarDto.getJarName());

            if (downloadedJarFile == null) {
                throw new IllegalStateException("Can't retrieve the " + pluginLoaderName + " jar from repository");
            }

            FileUtils.copyFile(downloadedJarFile, jarvisPluginLoader);

            if (!downloadedJarFile.delete()) {
                LOGGER.log(Level.WARNING, "Can't delete file: " + downloadedJarFile);
            }

        } else {
            File[] appRootFiles = jarvisPluginLoaderDirectory.listFiles(file -> file.isFile() && file.getName().startsWith(pluginLoaderName) && file.getName().endsWith(".jar"));

            if (appRootFiles.length == 0) {
                throw new IllegalStateException("Can't find " + pluginLoaderName + " jar file");
            } else if (appRootFiles.length > 1) {
                throw new IllegalStateException("Several " + pluginLoaderName + " jar files found");
            }

            jarvisPluginLoader = appRootFiles[0];
        }

        JarNameProperties jarProperties = JarUtils.parseFileName(jarvisPluginLoader.getName());
        LOGGER.info("Load jar file: " + jarProperties);
        URL[] existingURLs = new URL[]{jarvisPluginLoader.toURI().toURL()};

        // initialize the main class loader which will be the root of the entire application
        ClassLoader mainClassLoader = new URLClassLoader(jarProperties.getName(), existingURLs, Thread.currentThread().getContextClassLoader());
        ServiceLoader<PluginSystemService> loader = ServiceLoader.load(PluginSystemService.class, mainClassLoader);

        boolean serviceFound = false;
        Iterator<PluginSystemService> it = loader.iterator();
        PluginSystemService service;
        while (it.hasNext()) {
            if (serviceFound) {
                throw new IllegalStateException("An application service has already been loaded.");
            }
            service = it.next();
            serviceFound = true;
            service.initialize(new PluginData(jarProperties.getName(), jarProperties.getVersion()), null);
        }

        if (!serviceFound) {
            throw new IllegalStateException("No application service found.");
        }

        // lock the main thread to avoid ending it
        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error on locking", e);
        }
    }
}
