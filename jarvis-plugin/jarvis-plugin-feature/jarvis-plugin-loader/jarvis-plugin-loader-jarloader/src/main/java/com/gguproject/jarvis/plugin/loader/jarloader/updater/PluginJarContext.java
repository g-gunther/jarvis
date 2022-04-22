package com.gguproject.jarvis.plugin.loader.jarloader.updater;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;
import com.gguproject.jarvis.core.repository.JarVersionRepositoryService;
import com.gguproject.jarvis.core.repository.dto.JarDto;
import com.gguproject.jarvis.core.utils.FileUtils;
import com.gguproject.jarvis.core.utils.JarUtils;
import com.gguproject.jarvis.core.utils.StringUtils;
import com.gguproject.jarvis.core.utils.dto.JarNameProperties;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarLoaderInitializer;
import com.gguproject.jarvis.plugin.loader.jarloader.PluginJarService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Context of a jar to load
 */
public class PluginJarContext {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(PluginJarContext.class);

    public static final PluginJarContext EMPTY = new PluginJarContext();

    /**
     * Name of the jar
     */
    private String name;

    /**
     * Version of the jar
     */
    private String version;

    /**
     * Full name of the jar (name + version)
     */
    private String fullName;

    /**
     * Jar file
     */
    private File jarfile;

    /**
     * Context of data associated to the jar
     */
    private PluginJarDataContext dataContext;

    /**
     * Class loader of the jar
     */
    private ClassLoader classLoader;

    private PluginJarContext() {
    }

    /**
     * Get an instance of the jar context
     *
     * @param name Jar name
     * @return Instance of jar context
     */
    public static PluginJarContext get(String name) {
        PluginJarContext context = new PluginJarContext();
        context.name = name;
        context.dataContext = PluginJarDataContext.get(context);

        // check if there is a jar file that matches the given name
        // if the context directory is empty, get the current working directory (may be the application jar)
        File jarDirectory;
        if (StringUtils.isEmpty(PluginJarService.PluginAttributes.directory)) {
            jarDirectory = new File(System.getProperty("user.dir"));
        } else {
            jarDirectory = new File(PluginJarService.PluginAttributes.directory + File.separator + name);
        }

        if (jarDirectory.exists() && jarDirectory.isDirectory()) {
            context.dataContext.setJarDirectory(jarDirectory);

            // list all jar files of this directory
            File[] jarFiles = jarDirectory.listFiles((File file) -> file.getPath().toLowerCase().endsWith(".jar"));
            if (jarFiles.length == 0) {
                LOGGER.debug("No jar found in : {}", jarDirectory);
            } else if (jarFiles.length > 1) {
                LOGGER.warn("Several jars found in: {}", jarDirectory);
            } else {
                // a jar has been found
                context.jarfile = jarFiles[0];
                context.parseFileName();
            }
        } else {
            LOGGER.debug("No jar folder found: {}", name);
        }

        return context;
    }

    /**
     * Parse the file name of the jar to extract the name and version
     */
    private void parseFileName() {
        JarNameProperties jarNameProperties = JarUtils.parseFileName(this.jarfile.getName());
        this.name = jarNameProperties.getName();
        this.version = jarNameProperties.getVersion();
        this.fullName = this.jarfile.getName();
    }

    /**
     * Is the jar exists or not downloaded yet
     *
     * @return true if exists, false else
     */
    public boolean exists() {
        return this.jarfile != null && this.jarfile.exists();
    }

    /**
     * Is the jar loaded
     *
     * @return true if loaded, false else
     */
    public boolean isLoaded() {
        return this.classLoader != null;
    }

    /**
     * Return the jar class loader
     *
     * @return Class loader
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * Set a class loader for an externally loaded jar
     *
     * @param classLoader
     */
    public void setClassLoader(ClassLoader classLoader) {
        if (this.classLoader != null) {
            throw new IllegalStateException("The classLoader has already been set for jar context " + this.getName());
        }
        this.classLoader = classLoader;
    }

    /**
     * Load the jar in a dedicated class loader
     *
     * @param initializer Initializer which initializes the loaded jar
     * @throws PluginJarException
     */
    public void load(PluginJarLoaderInitializer initializer) throws PluginJarException {
        if (this.isLoaded()) {
            throw new PluginJarException(String.format("The jar %s is already loaded", this.name));
        }
        if (!this.exists()) {
            throw new PluginJarException(String.format("The jar %s can't be loaded because it does not exist", this.name));
        }

        try {
            this.classLoader = new URLClassLoader(this.name, new URL[]{this.jarfile.toURI().toURL()}, Thread.currentThread().getContextClassLoader());
            initializer.initialize(this);
        } catch (IOException e) {
            throw new PluginJarException(String.format("Can't load class loader for jar: %s", this.name), e);
        }
    }

    /**
     * Unload the jar and its custom classloader
     */
    public void unload() throws PluginJarException {
        if (this.classLoader instanceof URLClassLoader) {
            try {
                ((URLClassLoader) this.classLoader).close();
                this.classLoader = null;
            } catch (IOException e) {
                throw new PluginJarException(String.format("Can't close class loader for jar: %s", this.name), e);
            }
        }
    }

    /**
     * Download the jar if it does not exists
     * It takes the lastest version in the remote repository
     *
     * @throws PluginJarException
     */
    public void download() throws PluginJarException {
        LOGGER.debug("Download jar: {}", this.name);
        if (this.exists()) {
            throw new PluginJarException("Jar already exists - call refresh instead");
        }

        JarDto latestJarDto = JarVersionRepositoryService.getLatestVersion(this.name);
        File downloadedJarFile = JarVersionRepositoryService.downloadLatestVersion(this.name);

        if (downloadedJarFile != null) {
            this.copyDownloadedJarFileToTarget(latestJarDto.getJarName(), downloadedJarFile);
            this.getDataContext().download();
        } else {
            throw new PluginJarException(String.format("Not able to find plugin to download: %s", this.name));
        }
    }

    /**
     * Copy the downloaded jar file to the target
     *
     * @param downloadedJarFile File to download
     */
    private void copyDownloadedJarFileToTarget(String jarName, File downloadedJarFile) throws PluginJarException {
        try {
            File targetFile;
            // If there is no directory for jar, download it in the current directory
            // may be the main app jar
            if (StringUtils.isEmpty(PluginJarService.PluginAttributes.directory)) {
                targetFile = new File(jarName);
            } else {
                targetFile = new File(PluginJarService.PluginAttributes.directory + File.separator + this.name + File.separator + jarName);
            }

            FileUtils.copyFile(downloadedJarFile, targetFile);

            // try to delete the downloaded jar file
            FileUtils.deleteAndLogFile(downloadedJarFile);

            this.jarfile = targetFile;
            this.parseFileName();

            LOGGER.debug("Jar {} has been downloaded", downloadedJarFile.getName());
        } catch (IOException e) {
            LOGGER.error("Not able to download jar: {}", downloadedJarFile, e);
            throw new PluginJarException(String.format("Error while downloading file: %s - %s", downloadedJarFile, e.getMessage()));
        }
    }

    /**
     * Refresh the existing jar file by downloading a newer version if any
     *
     * @throws PluginJarException
     */
    public void refresh() throws PluginJarException {
        this.refresh(false);
    }

    /**
     * Refresh the existing jar file by download a newer version if any
     *
     * @param force If true, then it will download a newer version or the same one
     * @return {true} if updated, false else
     * @throws PluginJarException
     */
    public boolean refresh(boolean force) throws PluginJarException {
        JarDto latestJarDto = JarVersionRepositoryService.getLatestVersion(this.name);

        if (latestJarDto != null && latestJarDto.exists()) {
            LOGGER.info("Try to update jar: {} - current version: {}, last version: {} - force mode: {}", this.name, this.version, latestJarDto.getVersion(), force);

            // check the name of the repository file & the name of the current one
            // if more recent -> download it
            if (force || latestJarDto.getJarName().compareTo(this.fullName) > 0) {
                LOGGER.debug("Update current file: {} with file: {}", this.fullName, latestJarDto.getJarName());

                File downloadedJarFile = JarVersionRepositoryService.downloadLatestVersion(this.name);

                if (downloadedJarFile == null) {
                    throw new PluginJarException(String.format("Not able to download file: %s from repository", this.name));
                }

                // if names were different, delete the old one, else it might be a force update
                if (!latestJarDto.getJarName().equals(this.fullName)) {
                    this.removeFile(this.jarfile);
                }

                // copy the file
                this.copyDownloadedJarFileToTarget(latestJarDto.getJarName(), downloadedJarFile);

                return true;
            } else {
                LOGGER.debug("Jar {} hasn't been updated", this.name);
            }
        } else {
            throw new PluginJarException(String.format("Not able to find file: %s in repository", this.name));
        }

        return false;
    }

    /**
     * Remove the jar file and the associated data
     */
    public void remove() throws PluginJarException {
        if (this.exists()) {
            File parentDirectory = this.jarfile.getParentFile();
            this.removeFile(this.jarfile);
            this.getDataContext().remove();

            if (parentDirectory.delete()) {
                LOGGER.info("Parent directory of {} has been removed", this.name);
            } else {
                LOGGER.warn("Not able to remove parent directory of {}", this.name);
            }
        }
    }

    /**
     * Remove some files
     *
     * @param file fle to remove (full file name with version)
     */
    private void removeFile(File file) throws PluginJarException {
        if (FileUtils.deleteAndLogFile(file)) {
            // reset all data
            this.jarfile = null;
            this.fullName = null;
            this.version = null;
        } else {
            throw new PluginJarException(new StringBuilder("Not able to delete file: ").append(file).toString());
        }
    }

    /**
     * Return the data context of this jar
     *
     * @return Data context
     */
    public PluginJarDataContext getDataContext() {
        return this.dataContext;
    }

    /**
     * Returns the jar name
     *
     * @return Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the jar version
     *
     * @return Jar version
     */
    public String getVersion() {
        return this.version;
    }

    @Override
    public String toString() {
        return "JarContext: " + this.getName() + " " + this.getVersion();
    }
}
