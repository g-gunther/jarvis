package com.gguproject.jarvis.plugin.loader.jarloader;

import com.gguproject.jarvis.core.AbstractPluginConfiguration;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.repository.JarRepositoryService;
import com.gguproject.jarvis.core.utils.FileUtils;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarContext;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarContextDto;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarException;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarReport;
import com.gguproject.jarvis.plugin.loader.jarloader.updater.PluginJarReport.Counter;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class used to download and updates jars (plugins / modules)
 * from a repository
 */
@Named
public class PluginJarService {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(PluginJarService.class);

    /**
     * Plugin attributes
     */
    public static class PluginAttributes {

        public static final String directory = AbstractPluginConfiguration.pluginDirectoryName;
        public static final String dataDirectory = AbstractPluginConfiguration.pluginDataDirectoryName;
        public static final String configurationFileName = "plugins.properties";
        private static final Pattern filenamePattern = Pattern.compile("jarvis-plugin-(.*)");

        public static boolean match(String name) {
            return filenamePattern.matcher(name).matches();
        }
    }

    /**
     * All plugin jar contexts indexed by their name
     */
    private final Map<String, PluginJarContext> pluginContexts = new HashMap<>();

    /**
     * Do a full process
     * - remove unused jars (downloaded but not in the configuration file anymore)
     * - download jars that are in the configuration file but not locally
     * - update them if there is a newer version
     *
     * @return Report of the process
     * @throws PluginJarException
     */
    public PluginJarReport process(String... excludedJars) {
        PluginJarReport report = PluginJarReport.get();
        List<String> excludedJarNames = List.of(excludedJars);

        // if the directory does not exists, create it
        if (!FileUtils.isDirectory(PluginAttributes.directory)) {
            new File(PluginAttributes.directory).mkdirs();
        }

        // if the file does not exists, create it
        if (!FileUtils.isFile(PluginAttributes.configurationFileName)) {
            try {
                new File(PluginAttributes.configurationFileName).createNewFile();
            } catch (IOException e) {
                LOGGER.error("Error while creating a new plugin configuration", e);
                return report.error("Can not create file " + PluginAttributes.configurationFileName);
            }
        }

        // empty it before doing a full initialization
        pluginContexts.clear();

        // list all jars that are in the directory
        List<String> availableJarFileNames = Arrays.asList(new File(PluginAttributes.directory).listFiles(f -> f.isDirectory())).stream().map(d -> d.getName()).collect(Collectors.toList());
        LOGGER.info("Available jar files: {}", availableJarFileNames);
        Map<String, PluginJarContext> availableJarContexts = availableJarFileNames
                .stream()
                .filter(jarName -> !excludedJarNames.contains(jarName))
                .collect(Collectors.toMap(Function.identity(),availableJarFileName -> PluginJarContext.get(availableJarFileName)));

        // list all jars that has been configured
        List<String> configuredJarFileNames = FileUtils.readFile(PluginAttributes.configurationFileName);
        LOGGER.info("Configured jar files: {}", configuredJarFileNames);
        Map<String, PluginJarContext> configuredJarContexts = configuredJarFileNames
                .stream().filter(jarName -> !excludedJarNames.contains(jarName))
                .collect(Collectors.toMap(Function.identity(), configuredJarFileName -> PluginJarContext.get(configuredJarFileName)));

        /*
         * jars that are in the directory
         */
        availableJarContexts.forEach((availableJarName, availableJarContext) -> {
            // the jar exists and is in the list of configured jar -> refresh it
            if (availableJarContext.exists() && configuredJarContexts.containsKey(availableJarName)) {
                try {
                    availableJarContext.refresh();
                    availableJarContext.getDataContext().refresh();
                    this.pluginContexts.put(availableJarName, availableJarContext);
                    report.incrementCounter(Counter.REFRESH);
                } catch (PluginJarException e) {
                    LOGGER.error("Error while refreshing jar: {}", availableJarName, e);
                    report.error(e);
                }
            }
            // else remove it
            else {
                try {
                    availableJarContext.remove();
                    report.incrementCounter(Counter.DELETE);
                } catch (PluginJarException e) {
                    LOGGER.error("Error while removing jar: {}", availableJarName, e);
                    report.error(e);
                }
            }
        });

        /*
         * jars that are in the configuration and not in the directory
         * jars in the configuration file does not contains version numbers, so check only the beginning of the available file name
         */
        configuredJarContexts.forEach((configuredJarName, configuredJarContext) -> {
            if (!configuredJarContext.exists()) {
                try {
                    configuredJarContext.download();
                    report.incrementCounter(Counter.DOWNLOAD);
                    this.pluginContexts.put(configuredJarName, configuredJarContext);
                } catch (PluginJarException e) {
                    LOGGER.error("Error while downloading jar: {}", configuredJarName, e);
                    report.error(e);
                }
            }
        });

        return report;
    }

    /**
     * List all available jars in the directory
     *
     * @return list of jars
     */
    public List<PluginJarContextDto> listAvailableJars() {
        return this.pluginContexts.values().stream()
                .map(context -> new PluginJarContextDto(context.getName(), context.getVersion(), context.isLoaded()))
                .collect(Collectors.toList());
    }

    /**
     * List all jars that are in the repository
     *
     * @return list of jars
     */
    public Map<String, String> listRepositoryJars() {
        return JarRepositoryService.listAllJars();
    }

    /**
     * Download a jar from the repository and its associated data if any
     *
     * @param jarName Jar to download (jar name without version)
     * @return Report
     * @throws PluginJarException
     */
    public PluginJarReport add(String jarName) {
        PluginJarReport report = PluginJarReport.get();

        if (!PluginAttributes.match(jarName)) {
            return report.error(String.format("The name: %s does not match plugin pattern", jarName));
        }

        List<String> jars = FileUtils.readFile(PluginAttributes.configurationFileName);

        if (!jars.contains(jarName) && !this.pluginContexts.containsKey(jarName)) {
            LOGGER.debug("Add jar {} to the list", jarName);
            jars.add(jarName);

            // download the jar and data
            try {
                PluginJarContext context = PluginJarContext.get(jarName);
                context.download();
                this.pluginContexts.put(context.getName(), context);
                FileUtils.writeFile(PluginAttributes.configurationFileName, jars);
                report.incrementCounter(Counter.DOWNLOAD);
            } catch (PluginJarException e) {
                LOGGER.error("Error while adding jar: {}", jarName, e);
                report.error(e);
            }
        } else {
            LOGGER.info("The jar {} has already been added", jarName);
        }

        return report;
    }

    /**
     * Force to refresh all jars
     *
     * @return Report
     */
    public PluginJarReport refreshAll() {
        PluginJarReport report = PluginJarReport.get();

        for (Entry<String, PluginJarContext> entry : this.pluginContexts.entrySet()) {
            try {
                if (entry.getValue().refresh(true)) {
                    report.incrementCounter(Counter.REFRESH);
                }
            } catch (PluginJarException e) {
                LOGGER.error("Error while refreshing jar: {}", entry.getKey(), e);
                report.error(e);
            }
        }

        return report;
    }

    /**
     * Try to refresh the jar if needed
     *
     * @param jarName jar to update
     * @return Report
     * @throws PluginJarException
     */
    public PluginJarReport refresh(String jarName) {
        return this.refresh(jarName, false);
    }

    /**
     * Force the refresh of the given jar
     *
     * @param jarName jar to update
     * @param force   True if force to update, false else
     * @return Report
     * @throws PluginJarException
     */
    public PluginJarReport refresh(String jarName, boolean force) {
        PluginJarReport report = PluginJarReport.get();

        if (this.pluginContexts.containsKey(jarName)) {
            try {
                if (this.pluginContexts.get(jarName).refresh(force)) {
                    report.incrementCounter(Counter.REFRESH);
                }
            } catch (PluginJarException e) {
                LOGGER.error("Error while refreshing jar: {}", jarName, e);
                report.error(e);
            }
        } else {
            report.error(String.format("Can't refresh jar: %s because it is not loaded", jarName));
        }

        return report;
    }

    /**
     * Refresh jar data
     *
     * @param jarName jar name to refresh
     * @return Report
     * @throws PluginJarException
     */
    public PluginJarReport refreshData(String jarName, boolean force) {
        PluginJarReport report = PluginJarReport.get();

        if (this.pluginContexts.containsKey(jarName)) {
            try {
                if (this.pluginContexts.get(jarName).getDataContext().refresh(force)) {
                    report.incrementCounter(Counter.REFRESH);
                }
            } catch (PluginJarException e) {
                LOGGER.error("Error while updating data for jar: {}", jarName, e);
                report.error(e);
            }
        } else {
            report.error(String.format("Can't refresh data: %s because it is not loaded", jarName));
        }

        return report;
    }

    /**
     * Remove a given jar
     *
     * @param jarName jar name to remove
     * @throws PluginJarException
     */
    public PluginJarReport remove(String jarName) {
        PluginJarReport report = PluginJarReport.get();

        List<String> jars = FileUtils.readFile(PluginAttributes.configurationFileName);

        if (jars.contains(jarName) && this.pluginContexts.containsKey(jarName)) {
            LOGGER.debug("Remove jar & data {}", jarName);
            try {
                this.pluginContexts.get(jarName).remove();
                jars.remove(jarName);
                FileUtils.writeFile(PluginAttributes.configurationFileName, jars);
                this.pluginContexts.remove(jarName);
                report.incrementCounter(Counter.DELETE);
            } catch (PluginJarException e) {
                LOGGER.error("Error while removing jar: {}", jarName, e);
                report.error(e);
            }
        } else {
            report.error(String.format("The jar %s can't be removed - do not exist in configuration file or in contexts", jarName));
        }

        return report;
    }

    /**
     * Get all the contexts
     *
     * @return list of contexts
     */
    public Collection<PluginJarContext> getContexts() {
        return this.pluginContexts.values();
    }

    /**
     * Return a context by its name
     *
     * @param name
     * @return
     */
    public PluginJarContext getContext(String name) {
        return this.pluginContexts.get(name);
    }
}
