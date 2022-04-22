package com.gguproject.jarvis.core.utils.dto;

/**
 * Contains parsed values of a jar file name
 */
public class JarNameProperties {
	/**
	 * Name without version
	 */
	private String name;
	
	/**
	 * Version of the jar
	 */
	private String version;
	
	public JarNameProperties(String name, String version) {
		this.name = name;
		this.version = version;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return this.version;
	}

	@Override
	public String toString() {
		return "JarNameProperties [name=" + name + ", version=" + version + "]";
	}
}