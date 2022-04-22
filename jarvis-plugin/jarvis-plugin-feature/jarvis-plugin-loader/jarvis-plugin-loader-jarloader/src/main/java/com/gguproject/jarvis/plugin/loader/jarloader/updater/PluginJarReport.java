package com.gguproject.jarvis.plugin.loader.jarloader.updater;

import java.util.*;

/**
 * Report of the jar service processes
 */
public class PluginJarReport {

    /**
     * Get a report instance
     *
     * @return report instance
     */
    public static PluginJarReport get() {
        return new PluginJarReport();
    }

    /**
     * Type of counter
     */
    public enum Counter {
        /**
         * Downloaded jar files
         */
        DOWNLOAD("download"),

        /**
         * Jar loaded (in classloader)
         */
        LOADED("loaded"),

        /**
         * Deleted jar files
         */
        DELETE("delete"),

        /**
         * jar unloaded (classloader removed)
         */
        UNLOAD("unload"),

        /**
         * Refreshed jar files
         */
        REFRESH("refresh");

        private String name;

        private Counter(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    /**
     * List of errors
     */
    private List<String> errors = new ArrayList<>();

    /**
     * Counters
     */
    private Map<Counter, Integer> counters = new HashMap<>();

    private PluginJarReport() {
    }

    /**
     * Merge another report with this one
     *
     * @param pluginJarReport
     */
    public void merge(PluginJarReport pluginJarReport) {
        pluginJarReport.counters.forEach((counter, value) -> {
            this.incrementCounter(counter, value);
        });

        this.errors.addAll(pluginJarReport.errors);
    }

    /**
     * Increment a given counter by 1
     *
     * @param counter Counter to increment
     * @return instance of report
     */
    public PluginJarReport incrementCounter(Counter counter) {
        this.incrementCounter(counter, 1);
        return this;
    }

    /**
     * Increment a counter
     *
     * @param counter Counter to increment
     * @param nb      Value to add
     * @return Current instance of the report
     */
    public PluginJarReport incrementCounter(Counter counter, int nb) {
        if (!this.counters.containsKey(counter)) {
            this.counters.put(counter, 0);
        }
        this.counters.put(counter, this.counters.get(counter) + nb);
        return this;
    }

    /**
     * Add an error
     *
     * @param e Exception to add
     * @return Current instance of the report
     */
    public PluginJarReport error(Exception e) {
        this.errors.add(e.getMessage());
        return this;
    }

    /**
     * Add an error
     *
     * @param error Value of the error
     * @return Current instance of the report
     */
    public PluginJarReport error(String error) {
        this.errors.add(error);
        return this;
    }

    /**
     * Get a counter value
     *
     * @param counter Counter
     * @return value of the counter
     */
    public int getCounter(Counter counter) {
        return this.counters.containsKey(counter) ? this.counters.get(counter) : 0;
    }

    /**
     * Indicates if there is an error in the report
     *
     * @return true if error
     */
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    /**
     * Get the list of errors
     *
     * @return list of errors
     */
    public List<String> getErrors() {
        return Collections.unmodifiableList(this.errors);
    }
}
