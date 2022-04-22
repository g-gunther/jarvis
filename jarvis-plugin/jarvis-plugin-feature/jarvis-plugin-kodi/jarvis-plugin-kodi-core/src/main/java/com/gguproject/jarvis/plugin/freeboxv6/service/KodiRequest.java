package com.gguproject.jarvis.plugin.freeboxv6.service;

import java.util.HashMap;
import java.util.Map;

public class KodiRequest {
	
	private static int idCounter = 0;

	private final String jsonrpc = "2.0";
	
	private final String method;
	
	private final int id;
	
	private Map<String, Object> params = new HashMap<>();
	
	public KodiRequest(String method) {
		this.method = method;
		this.id = idCounter++;
	}
	
	public KodiRequest param(String key, Object value) {
		this.params.put(key, value);
		return this;
	}

	@Override
	public String toString() {
		return "KodiRequest [jsonrpc=" + jsonrpc + ", method=" + method + ", id=" + id + ", params=" + params + "]";
	}
}
