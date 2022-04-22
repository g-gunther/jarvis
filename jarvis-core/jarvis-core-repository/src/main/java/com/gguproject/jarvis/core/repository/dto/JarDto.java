package com.gguproject.jarvis.core.repository.dto;

public class JarDto {

	private String name;
	
	private String version;
	
	private String jarName;
	
	public JarDto() {
	}
	
	public void copyValues(JarDto jarDto) {
		this.name = jarDto.getName();
		this.version = jarDto.getVersion();
		this.jarName = jarDto.getJarName();
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getJarName() {
		return jarName;
	}
	
	public boolean exists() {
		return this.jarName != null;
	}
}
