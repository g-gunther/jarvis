/*
 * Copyright (C) 2006 The Android Open Source Project
 * Copyright (C) 2012 ENTERTAILION, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gguproject.jarvis.plugin.androidtv.util;

import com.gguproject.jarvis.plugin.androidtv.client.MessageType;

public final class Message {
    /**
     * User-defined message code so that the recipient can identify 
     * what this message is about. Each {@link Handler} has its own name-space
     * for message codes, so you do not need to worry about yours conflicting
     * with other handlers.
     */
    private MessageType what;

    /**
     * arg1 and arg2 are lower-cost alternatives to using
     * {@link #setData(Bundle) setData()} if you only need to store a
     * few integer values.
     */
    private int arg1; 

    /**
     * arg1 and arg2 are lower-cost alternatives to using
     * {@link #setData(Bundle) setData()} if you only need to store a
     * few integer values.
     */
    private int arg2;

    /**
     * An arbitrary object to send to the recipient.  When using
     * {@link Messenger} to send the message across processes this can only
     * be non-null if it contains a Parcelable of a framework class (not one
     * implemented by the application).   For other data transfer use
     * {@link #setData}.
     * 
     * <p>Note that Parcelable objects here are not supported prior to
     * the {@link android.os.Build.VERSION_CODES#FROYO} release.
     */
    private Object obj;
    
    public Message(MessageType what, Object obj) {
    	this.what = what;
    	this.obj = obj;
    }
    
    public MessageType getWhat() {
		return what;
	}

	public Object getObj() {
		return obj;
	}

	@Override
	public String toString() {
		return "Message [what=" + what + ", arg1=" + arg1 + ", arg2=" + arg2 + ", obj=" + obj + "]";
	}


	public static Message obtain(MessageType what, Object obj) {
        return new Message(what, obj);
    }

}