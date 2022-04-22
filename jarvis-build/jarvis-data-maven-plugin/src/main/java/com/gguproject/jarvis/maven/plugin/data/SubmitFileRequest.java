package com.gguproject.jarvis.maven.plugin.data;

import java.io.File;

public class SubmitFileRequest {
	
	private String address;

	private File file;
	
	private String path;
	
	private String targetFilename;
	
	public static SubmitFileRequest get() {
		return new SubmitFileRequest();
	}
	
	public SubmitFileRequest address(String address) {
		this.address = address;
		return this;
	}
	
	public SubmitFileRequest file(File file) {
		this.file = file;
		return this;
	}
	
	public SubmitFileRequest path(String path) {
		this.path = path;
		return this;
	}
	
	public SubmitFileRequest targetFilename(String targetFilename) {
		this.targetFilename = targetFilename;
		return this;
	}

	public String getAddress() {
		return this.address;
	}

	public File getFile() {
		return this.file;
	}

	public String getPath() {
		return this.path;
	}

	public String getTargetFilename() {
		return this.targetFilename == null ? this.getFile().getName() : this.targetFilename;
	}

	@Override
	public String toString() {
		return "SubmitFileRequest [address=" + address + ", file="
				+ file + ", path=" + path + ", targetFilename=" + targetFilename + "]";
	}
}
