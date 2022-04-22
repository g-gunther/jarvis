package com.gguproject.jarvis.core.events.support;

import java.util.UUID;

public class JVMUtils {

	/**
	 * Unique identifier of this JVM instance. Each packet must have a kind of "origin" field, which is
	 * automatically set by the manager when an event is published. When the receiver receives an event, it
	 * checks this origin field. If it is equal to the manager's origin, then the event is discarded, because
	 * it was sent from the same JVM. The origin field of the manager must be a random UUID, which is
	 * guaranteed to be unique.
	 */
	private static UUID uuid = UUID.randomUUID();
	
	public static UUID getUUID(){
		return uuid;
	}
}
