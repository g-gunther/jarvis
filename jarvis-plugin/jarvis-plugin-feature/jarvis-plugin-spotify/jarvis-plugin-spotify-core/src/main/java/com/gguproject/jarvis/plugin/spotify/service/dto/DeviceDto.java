package com.gguproject.jarvis.plugin.spotify.service.dto;

import com.wrapper.spotify.model_objects.miscellaneous.Device;

public class DeviceDto {
	private String name;
	
	private String id;

	public DeviceDto(Device device) {
		this.name = device.getName();
		this.id = device.getId();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "DeviceDto{" +
				"name='" + name + '\'' +
				", id='" + id + '\'' +
				'}';
	}
}
