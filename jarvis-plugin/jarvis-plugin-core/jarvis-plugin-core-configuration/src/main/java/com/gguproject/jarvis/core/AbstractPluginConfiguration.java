package com.gguproject.jarvis.core;

import com.gguproject.jarvis.core.exception.TechnicalException;
import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.utils.DevUtils;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractPluginConfiguration extends AbstractConfiguration {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(AbstractPluginConfiguration.class);

    public static final String pluginDataDirectoryName = "data";
    public static final String pluginSecretDataDirectoryName = "secret-data";
    public static final String pluginDirectoryName = "plugins";

    private String jarName;

    private Optional<File> dataDirectory;

    private Optional<File> secretDataDirectory;

    public AbstractPluginConfiguration(String jarName) {
        super();
        this.jarName = jarName;

        this.secretDataDirectory = this.loadDirectoryAndProperties(pluginSecretDataDirectoryName, pluginSecretDataDirectoryName + File.separator + jarName + File.separator);
        this.dataDirectory = this.loadDirectoryAndProperties(pluginDataDirectoryName, pluginDirectoryName + File.separator + jarName + File.separator + pluginDataDirectoryName + File.separator);
    }

    /**
     *
     * @param rootDirectory
     * @param prodConfigurationDirectory
     * @return
     */
    private Optional<File> loadDirectoryAndProperties(String rootDirectory, String prodConfigurationDirectory){
        File dataDirectory = Optional.of(DevUtils.isDevEnvironment())
                .filter(isDev -> isDev)
                .map(isDev -> new File(rootDirectory + File.separator))
                .orElse(new File(prodConfigurationDirectory));

        Optional<File> optionalDataDirectory = Optional.of(dataDirectory)
                .filter(directory -> directory.exists() && directory.isDirectory());

        if (optionalDataDirectory.isPresent()) {
            Stream.of(optionalDataDirectory.get()
                    .listFiles((file, name) -> name.endsWith(".properties")))
                  .forEach(configurationFile -> this.loadProperties(configurationFile));
        } else {
            LOGGER.info("No directory found for plugin: {} in {}", this.jarName, dataDirectory);
        }

        return optionalDataDirectory;
    }

    /**
     *
     * @param directoryPath
     * @return
     */
    public Optional<File> getConfigurationDirectory(String directoryPath) {
        return this.dataDirectory
            .map(dataDirectory -> new File(dataDirectory, directoryPath))
            .filter(directory -> directory.exists() && directory.isDirectory());
    }

    /**
     * Get a configuration file
     *
     * @param filename
     * @return
     */
    public Optional<File> getConfigurationFile(String filename) {
        return this.dataDirectory
                .map(dataDirectory -> new File(dataDirectory, filename))
                .filter(file -> file.exists() && file.isFile());
    }

    /**
     * Get a configuration file
     *
     * @param filename
     * @return
     */
    public File getConfigurationFileOrCreate(String filename) {
        File dataDirectory = this.dataDirectory
                .orElseThrow(() -> TechnicalException.get().message("Can't get configuration or create {0} because data directory does not exist", filename).build());
        return this.getConfigurationFile(filename)
                .orElse(new File(dataDirectory, filename));
    }

    /**
     *
     * @param filename
     * @return
     */
    public String getDataFilePath(String filename) {
        return this.dataDirectory
                .map(directory -> new StringBuilder(directory.getPath()).append(File.separator).append(filename).toString())
                .orElseThrow(() -> TechnicalException.get().message("Can't get data file path for file: {0} because the dataDirectory does not exist", filename).build());
    }

    /**
     *
     * @param filename
     * @return
     */
    public Optional<File> getSecretDataFile(String filename){
        return this.secretDataDirectory
                .map(directory -> new File(directory, filename))
                .filter(file -> file.exists() && file.isFile());
    }

    /**
     * Get a configuration file
     *
     * @param filename
     * @return
     */
    public File getSecretDataFileOrCreate(String filename) {
        File secretDataDirectory = this.secretDataDirectory
                .orElseThrow(() -> TechnicalException.get().message("Can't get configuration or create {0} because secret data directory does not exist", filename).build());
        return this.getSecretDataFile(filename)
                .orElse(new File(secretDataDirectory, filename));
    }

    /**
     *
     * @param filename
     * @return
     */
    public String getSecretDataFilePath(String filename) {
        return this.secretDataDirectory
                .map(directory -> new StringBuilder(directory.getPath()).append(File.separator).append(filename).toString())
                .orElseThrow(() -> TechnicalException.get().message("Can't get data file path for file: {0} because the secret dataDirectory does not exist", filename).build());
    }

    /**
     *
     * @param filename
     */
    public void loadAdditionalPropertyFile(String filename){
        this.getConfigurationFile(filename)
                .ifPresentOrElse(
                        propertyFile -> this.loadProperties(propertyFile),
                        () -> LOGGER.warn("Not able to load additional property file {} because not found", filename)
                );
    }

    /**
     *
     * @return
     */
    public String getJarName() {
        return this.jarName;
    }
}
