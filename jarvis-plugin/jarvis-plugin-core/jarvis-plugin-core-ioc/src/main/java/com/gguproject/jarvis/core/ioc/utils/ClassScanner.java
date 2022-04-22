package com.gguproject.jarvis.core.ioc.utils;

import com.gguproject.jarvis.core.logger.AbstractLoggerFactory;
import com.gguproject.jarvis.core.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Tools used to scan a class loader
 * to find all classes in a given package and matching a list of custom filters
 */
public class ClassScanner {
    private static final Logger LOGGER = AbstractLoggerFactory.getLogger(ClassScanner.class);

    /**
     * Get an instance of Class scanner
     *
     * @return instance of class scanner
     * @throws ClassScannerException
     */
    public static ClassScanner get() throws ClassScannerException {
        return new ClassScanner();
    }

    /**
     * Class loader to scan
     */
    private ClassLoader classLoader;

    /**
     * List of found classes
     */
    private List<Class<?>> foundClasses = new ArrayList<>();

    /**
     * Filter to apply on found classes
     */
    private ClassScannerFilter filter;

    /**
     * Default package name
     */
    private String packageName = "/";

    /**
     * Constructor
     */
    private ClassScanner() {
        this.classLoader = this.getClass().getClassLoader();
    }

    /**
     * Set a filter
     *
     * @param filter Filter to apply on found classes
     * @return Instance of class loader
     */
    public ClassScanner setFilter(ClassScannerFilter filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Set the package to scan
     *
     * @param packageName Package name
     * @return Instance of class loader
     */
    public ClassScanner setPackage(String packageName) {
        this.packageName = packageName;
        return this;
    }

    /**
     * Set the class loader to scan
     *
     * @param classLoader Class loader
     * @return Instance of class loader
     */
    public ClassScanner setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    /**
     * Starts the scan
     *
     * @return Instance of class loader
     * @throws ClassScannerException
     */
    public ClassScanner scanClassLoader() throws ClassScannerException {
        LOGGER.debug("Start scan on package: {}", packageName);
        String path = this.packageName.replace('.', '/');

        // get all the resources of the class loader that are in the resource name (package)
        Enumeration<URL> resources = null;
        try {
            resources = this.classLoader.getResources(path);
        } catch (IOException e) {
            throw new ClassScannerException(e);
        }

        // for each resources retrieve all the files
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }

        // for each files, if it contains "jar" then we need to scan a jar file, else it's a local directory (for development)
        for (File directory : dirs) {
            if (directory.getPath().contains(".jar!")) {
                String jarFilePath = directory.getPath().substring(0, directory.getPath().indexOf("!"));
                if (jarFilePath.startsWith("file:")) {
                    jarFilePath = jarFilePath.substring("file:".length());
                }
                File jarFile = new File(jarFilePath);
                if (jarFile.exists()) {
                    this.scanJarFile(new File(jarFilePath));
                } else {
                    LOGGER.error("Not able to found file: {} from initial directory: {}", jarFilePath, directory.getPath());
                }
            } else {
                this.scanClassesInDirectory(directory, this.packageName);
            }
        }


        // remove duplicates of foundClasses when exactly the same class
        // it can happen when loading an external jar since this jar depends on libraries that are
        // already loaded in the main class loader.
        this.foundClasses = this.foundClasses.stream().distinct().collect(Collectors.toList());

        return this;
    }

    /**
     * Scan a directory to find all classes
     *
     * @param directory   Directory to scan
     * @param packageName Package of classes to find
     * @throws ClassScannerException
     */
    private void scanClassesInDirectory(File directory, String packageName) throws ClassScannerException {
        if (!directory.exists()) {
            return;
        }

        LOGGER.debug("Scan directory: {}", directory);

        File[] files = directory.listFiles();
        for (File file : files) {
            // recursive call
            if (file.isDirectory()) {
                this.scanClassesInDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                try {
                    // load the class and apply the filter if any
                    Class<?> foundClass = this.classLoader.loadClass(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    if (this.filter == null || this.filter.filter(foundClass)) {
                        LOGGER.debug("Found class: {}", foundClass);
                        this.foundClasses.add(foundClass);
                    }
                } catch (ClassNotFoundException e) {
                    throw new ClassScannerException(e);
                }
            }
        }
    }

    /**
     * Scan a jar file
     *
     * @param jarFile Jar file
     * @return Current class loader instance
     */
    public ClassScanner scanJarFile(File jarFile) {
        LOGGER.debug("Scan jar file: {}", jarFile);
        try (JarFile jar = new JarFile(jarFile);) {
            String path = this.packageName.replace('.', '/');

            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String file = entry.getName();

                // if .class file in the package we're looking for which is not an inner class
                if (file.endsWith(".class") && file.startsWith(path) && !file.contains("$")) {
                    String className = file.replace('/', '.').substring(0, file.length() - 6).split("\\$")[0];
                    try {
                        Class<?> foundClass = this.classLoader.loadClass(className);
                        if (this.filter == null || this.filter.filter(foundClass)) {
                            LOGGER.debug("Found class: {}", foundClass);
                            this.foundClasses.add(foundClass);
                        }
                    } catch (ClassNotFoundException e) {
                        LOGGER.error("Fails to instantiate {} from jarFile {}", className, jarFile);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Fails to load jar file {}", jarFile);
        }

        return this;
    }

    /**
     * Process all found classes during the scan
     *
     * @param processor Processor which will process the classes
     */
    public void process(ClassScannerProcessor processor) {
        processor.process(this.foundClasses);
    }

    /**
     * Get the list of found classes
     *
     * @return list of classes
     */
    public List<Class<?>> getClasses() {
        return this.foundClasses;
    }
}
