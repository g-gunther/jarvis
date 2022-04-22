package com.gguproject.jarvis.plugin.loader.jarloader.updater;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.repository.JarDataRepositoryService;
import com.gguproject.jarvis.core.utils.FileUtils;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Context of data of a jar
 */
public class PluginJarDataContext {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(PluginJarDataContext.class);

    /**
     * Data directory
     */
    private File dataDirectory;

    /**
     * Parent jar context
     */
    private PluginJarContext pluginJarContext;

    private static Pattern propertyLinePattern = Pattern.compile("^[\\w\\.]+=(.*)$");

    /**
     * Get an instance of jar data context
     *
     * @param pluginJarContext jar context
     * @return data context instance
     */
    public static PluginJarDataContext get(PluginJarContext pluginJarContext) {
        return new PluginJarDataContext(pluginJarContext);
    }

    /**
     * Constructor
     *
     * @param pluginJarContext jar context
     */
    private PluginJarDataContext(PluginJarContext pluginJarContext) {
        this.pluginJarContext = pluginJarContext;
    }

    /**
     * set the parent jar directory
     *
     * @param jarDirectory
     */
    public void setJarDirectory(File jarDirectory) {
        File[] dataFolders = jarDirectory.listFiles(f -> f.getName().equals(PluginJarService.PluginAttributes.dataDirectory) && f.isDirectory());
        if (dataFolders.length == 0) {
            LOGGER.debug("No data folder for {}", this.pluginJarContext.getName());
        } else {
            this.dataDirectory = dataFolders[0];
        }
    }

    /**
     * Indicates if the data directory exists or not
     *
     * @return true if exists, false else
     */
    public boolean exists() {
        return this.dataDirectory != null && this.dataDirectory.exists();
    }

    /**
     * Refresh the data directory
     *
     * @throws PluginJarException
     */
    public boolean refresh(boolean force) throws PluginJarException {
        return this.download(force);
    }

    public boolean refresh() throws PluginJarException {
        return this.download(false);
    }

    /**
     * Download data
     *
     * @return
     * @throws PluginJarException
     */
    public boolean download() throws PluginJarException {
        return this.download(false);
    }

    /**
     * Download the data directory
     *
     * @throws PluginJarException
     */
    private boolean download(boolean force) throws PluginJarException {
        if (this.exists() && !force) {
            throw new PluginJarException("Data directory already exists - call refresh instead");
        }

        File targetDataDirectory = new File(PluginJarService.PluginAttributes.directory + File.separator + this.pluginJarContext.getName() + File.separator + PluginJarService.PluginAttributes.dataDirectory);

        LOGGER.info("Download data for: {} in: {}", this.pluginJarContext.getName(), targetDataDirectory);

        // download data file related to the current jar version
        Optional<File> downloadedDataZipFile = JarDataRepositoryService.downloadVersion(this.pluginJarContext.getName(), this.pluginJarContext.getVersion());

        if (downloadedDataZipFile.isEmpty()) {
            LOGGER.info("No data folder for {} {}", this.pluginJarContext.getName(), this.pluginJarContext.getVersion());
            return false;
        }

        if (targetDataDirectory.exists()) {
            this.processNewDownloadedZipDataFolder(targetDataDirectory, downloadedDataZipFile.get());
        } else {
            try {
                FileUtils.unzipFile(downloadedDataZipFile.get(), targetDataDirectory.getParentFile());
            } catch (IOException e) {
                throw new PluginJarException(String.format("Not able to unzip data file %s to %s", downloadedDataZipFile, targetDataDirectory), e);
            }
        }

        FileUtils.deleteAndLogFile(downloadedDataZipFile.get());
        File newDataDirectory = new File(PluginJarService.PluginAttributes.directory + File.separator + this.pluginJarContext.getName() + File.separator + PluginJarService.PluginAttributes.dataDirectory);
        if (!newDataDirectory.exists()) {
            throw new PluginJarException("Data folder has been unzipped but no data folder found");
        }
        this.dataDirectory = newDataDirectory;

        return true;
    }

    /**
     * unzip the downloaded data folder and merge with the existing one if any
     * @param targetDataDirectory
     * @param downloadedDataZipFile
     */
    private void processNewDownloadedZipDataFolder(File targetDataDirectory, File downloadedDataZipFile){
        LOGGER.debug("Existing data folder: {}", targetDataDirectory);
        try {
            File temporaryUnzipFolder = new File(targetDataDirectory.getParentFile(), "tmp");
            if(temporaryUnzipFolder.exists()){
                FileUtils.deleteDirectory(temporaryUnzipFolder);
            }

            // unzip in a tmp folder
            FileUtils.unzipFile(downloadedDataZipFile, temporaryUnzipFolder);

            // merge the temporary downloaded data folder with the existing one
            // for each existing files in the newly downloaded data folder, look for the same file in the existing data folder
            // if found and .properties file -> merge properties, else keep the existing one
            // if not found, keep the new file
            Files.walk(temporaryUnzipFolder.toPath())
                    .map(p -> p.toFile())
                    .filter(File::isFile)
                    .forEach(newDataFile -> this.updateNewDataFolderWithOldOne(targetDataDirectory, temporaryUnzipFolder, newDataFile));

            // move temporary unzip folder to new location
            FileUtils.deleteDirectory(targetDataDirectory);
            Files.move(new File(temporaryUnzipFolder, "data").toPath(), targetDataDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
            FileUtils.deleteDirectory(temporaryUnzipFolder);
            LOGGER.debug("Delete existing data directory: {}", targetDataDirectory);
        } catch (IOException e) {
            throw new PluginJarException(String.format("Not able to delete data directory %s ", targetDataDirectory), e);
        }
    }

    /**
     * Merge new downloaded file with existing ones
     * - if property file, try to merge property by property
     * - else keep the existing files
     * @param targetDataDirectory
     * @param temporaryUnzipFolder
     * @param newDataFile
     */
    private void updateNewDataFolderWithOldOne(File targetDataDirectory, File temporaryUnzipFolder, File newDataFile){
        try {
            // try to find the same file in the old data folder
            String dataFilePathFromDataRoot = newDataFile.getCanonicalPath().replace(temporaryUnzipFolder.getCanonicalPath(), "");
            File oldDataFile = new File(targetDataDirectory.getParent(), dataFilePathFromDataRoot);

            if(oldDataFile.exists()){
                boolean isNewDataFilePropertyFile = Files.lines(newDataFile.toPath()).allMatch(line -> propertyLinePattern.matcher(line).matches());
                boolean isOldDataFilePropertyFile = Files.lines(oldDataFile.toPath()).allMatch(line -> propertyLinePattern.matcher(line).matches());

                // if the 2 files are of type property try to merge the properties from the old to the new
                if(isNewDataFilePropertyFile && isOldDataFilePropertyFile) {
                    Properties oldDataFileProperties = this.loadProperties(oldDataFile);
                    Properties newDataFileProperties = this.loadProperties(newDataFile);

                    oldDataFileProperties.entrySet().forEach(newProperty -> {
                        newDataFileProperties.merge(newProperty.getKey(), newProperty.getValue(), (oldProp, newProp) -> oldProp);
                    });
                    newDataFileProperties.store(new FileOutputStream(newDataFile), null);

                } else {
                    // else copy the old file to replace the new
                    Files.move(oldDataFile.toPath(), newDataFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        } catch (IOException e){
            LOGGER.error("Error while processing file: {}", newDataFile, e);
        }
    }

    /**
     *
     * @param file
     * @return
     */
    private Properties loadProperties(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new PluginJarException("Not able to load property file: " + file + " for merge");
        }
        return properties;
    }

    /**
     * Remove all the data directory
     *
     * @throws PluginJarException
     */
    public void remove() throws PluginJarException {
        if (this.exists()) {
            try {
                FileUtils.deleteDirectory(this.dataDirectory);
                this.dataDirectory = null;
            } catch (IOException e) {
                throw new PluginJarException("Error while deleting data directory for: " + this.pluginJarContext.getName(), e);
            }
        }
    }
}
