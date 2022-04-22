package com.gguproject.jarvis.plugin.spi.data;

public class PluginData {

	private String name;
	
	private String version;
	
	public PluginData(String name, String version) {
		this.name = name;
		this.version = version;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getVersion() {
		return this.version;
	}
}

