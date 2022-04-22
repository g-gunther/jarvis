package com.gguproject.jarvis.core.bus.support;

import java.lang.reflect.ParameterizedType;

import com.gguproject.jarvis.core.bus.AbstractEventListener;

public class EventTypeUtils {
	
	/**
	 * Find the type of event handled by the given event listener
	 * @param eventListener Event listener to analyse
	 * @return Type of event
	 */
    @SuppressWarnings("unchecked")
	public static Class<EventData> getType(AbstractEventListener<?> eventListener) {
    		Class<?> eventType = findType(eventListener.getClass());
    	
    		if(eventType == null) {
    			throw new IllegalArgumentException("Can not find event type for event listener " + eventListener.getClass().toString());
    		}
    		
    		return (Class<EventData>) eventType;
    }
    
    /**
     * Find the type of data handled by the EventListener
     * @param clazz class to analyse
     * @return type of event
     */
    private static Class<?> findType(Class<?> clazz) {
    		if(clazz.getGenericSuperclass() instanceof ParameterizedType) {
    			ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
    			if(parameterizedType.getRawType().getTypeName().equals(AbstractEventListener.class.getCanonicalName())){
    				String eventTypeName = parameterizedType.getActualTypeArguments()[0].getTypeName();
    				try {
    					return Class.forName(eventTypeName);
    				} catch (ClassNotFoundException e) {
    					throw new IllegalStateException(String.format("Can't find class with name: %s", eventTypeName), e);
    				}	
    			}
    		}
    		
    		if(clazz.getSuperclass().equals(Object.class)) {
    			return null;
    		}
    		
    		return findType(clazz.getSuperclass());
    }
}
