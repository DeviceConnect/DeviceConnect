/**
 * Copyright 2005-2014 Restlet
 * 
 * The contents of this file are subject to the terms of one of the following
 * open source licenses: Apache 2.0 or LGPL 3.0 or LGPL 2.1 or CDDL 1.0 or EPL
 * 1.0 (the "Licenses"). You can select the license that you prefer but you may
 * not use this file except in compliance with one of these Licenses.
 * 
 * You can obtain a copy of the Apache 2.0 license at
 * http://www.opensource.org/licenses/apache-2.0
 * 
 * You can obtain a copy of the LGPL 3.0 license at
 * http://www.opensource.org/licenses/lgpl-3.0
 * 
 * You can obtain a copy of the LGPL 2.1 license at
 * http://www.opensource.org/licenses/lgpl-2.1
 * 
 * You can obtain a copy of the CDDL 1.0 license at
 * http://www.opensource.org/licenses/cddl1
 * 
 * You can obtain a copy of the EPL 1.0 license at
 * http://www.opensource.org/licenses/eclipse-1.0
 * 
 * See the Licenses for the specific language governing permissions and
 * limitations under the Licenses.
 * 
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly at
 * http://www.restlet.com/products/restlet-framework
 * 
 * Restlet is a registered trademark of Restlet
 */

package org.restlet;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.restlet.data.MediaType;
import org.restlet.data.RecipientInfo;
import org.restlet.data.Warning;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

/**
 * Generic message exchanged between components.
 * 
 * @author Jerome Louvel
 */
public abstract class Message {
    /** The modifiable attributes map. */
    private volatile ConcurrentMap<String, Object> attributes;

    /** The date and time at which the message was originated. */
    private volatile Date date;

    /** The payload of the message. */
    private volatile Representation entity;

    /** The optional cached text. */
    private volatile String entityText;

    /** Callback invoked when an error occurs when sending the message. */
    private volatile Uniform onError;

    /** Callback invoked after sending the message. */
    private volatile Uniform onSent;

    /** The intermediary recipients info. */
    private volatile List<RecipientInfo> recipientsInfo;

    /** The additional warnings information. */
    private volatile List<Warning> warnings;

    /**
     * Constructor.
     */
    public Message() {
        this((Representation) null);
    }

    /**
     * Constructor.
     * 
     * @param entity
     *            The payload of the message.
     */
    public Message(Representation entity) {
        this.attributes = null;
//        this.cacheDirectives = null;
        this.date = null;
        this.entity = entity;
        this.entityText = null;
        this.onSent = null;
        this.recipientsInfo = null;
        this.warnings = null;
    }

    /**
     * Returns the entity representation.
     * 
     * @return The entity representation.
     */
    public Representation getEntity() {
        return this.entity;
    }

    /**
     * Returns the callback invoked when an error occurs when sending the
     * message.
     * 
     * @return The callback invoked when an error occurs when sending the
     *         message.
     */
    public Uniform getOnError() {
        return onError;
    }

    /**
     * Returns the callback invoked after sending the message.
     * 
     * @return The callback invoked after sending the message.
     */
    public Uniform getOnSent() {
        return onSent;
    }

    /**
     * Returns the intermediary recipient information.<br>
     * <br>
     * Note that when used with HTTP connectors, this property maps to the "Via"
     * headers.
     * 
     * @return The intermediary recipient information.
     */
    public List<RecipientInfo> getRecipientsInfo() {
        // Lazy initialization with double-check.
        List<RecipientInfo> r = this.recipientsInfo;
        if (r == null) {
            synchronized (this) {
                r = this.recipientsInfo;
                if (r == null) {
                    this.recipientsInfo = r = new CopyOnWriteArrayList<RecipientInfo>();
                }
            }
        }
        return r;
    }

    /**
     * Returns the additional warnings information.<br>
     * <br>
     * Note that when used with HTTP connectors, this property maps to the
     * "Warning" headers.
     * 
     * @return The additional warnings information.
     */
    public List<Warning> getWarnings() {
        // Lazy initialization with double-check.
        List<Warning> r = this.warnings;
        if (r == null) {
            synchronized (this) {
                r = this.warnings;
                if (r == null) {
                    this.warnings = r = new CopyOnWriteArrayList<Warning>();
                }
            }
        }
        return r;
    }

    /**
     * Indicates if a content is available and can be sent or received. Several
     * conditions must be met: the content must exists and have some available
     * data.
     * 
     * @return True if a content is available and can be sent.
     */
    public boolean isEntityAvailable() {
        // The declaration of the "result" variable is a workaround for the GWT
        // platform. Please keep it!
        boolean result = (getEntity() != null) && getEntity().isAvailable();
        return result;
    }

    /**
     * Releases the message's entity if present.
     * 
     * @see org.restlet.representation.Representation#release()
     */
    public void release() {
        if (getEntity() != null) {
            getEntity().release();
        }
    }

    /**
     * Sets the date and time at which the message was originated.
     * 
     * @param date
     *            The date and time at which the message was originated.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets the entity representation.
     * 
     * @param entity
     *            The entity representation.
     */
    public void setEntity(Representation entity) {
        this.entity = entity;
    }

    /**
     * Sets a textual entity.
     * 
     * @param value
     *            The represented string.
     * @param mediaType
     *            The representation's media type.
     */
    public void setEntity(String value, MediaType mediaType) {
        setEntity(new StringRepresentation(value, mediaType));
    }

    /**
     * Sets the callback invoked when an error occurs when sending the message.
     * 
     * @param onError
     *            The callback invoked when an error occurs when sending the
     *            message.
     */
    public void setOnError(Uniform onError) {
        this.onError = onError;
    }

    /**
     * Sets the callback invoked after sending the message.
     * 
     * @param onSentCallback
     *            The callback invoked after sending the message.
     */
    public void setOnSent(Uniform onSentCallback) {
        this.onSent = onSentCallback;
    }

    /**
     * Sets the modifiable list of intermediary recipients. Note that when used
     * with HTTP connectors, this property maps to the "Via" headers. This
     * method clears the current list and adds all entries in the parameter
     * list.
     * 
     * @param recipientsInfo
     *            A list of intermediary recipients.
     */
    public void setRecipientsInfo(List<RecipientInfo> recipientsInfo) {
        synchronized (getRecipientsInfo()) {
            if (recipientsInfo != getRecipientsInfo()) {
                getRecipientsInfo().clear();

                if (recipientsInfo != null) {
                    getRecipientsInfo().addAll(recipientsInfo);
                }
            }
        }
    }

    /**
     * Sets the additional warnings information. Note that when used with HTTP
     * connectors, this property maps to the "Warning" headers. This method
     * clears the current list and adds all entries in the parameter list.
     * 
     * @param warnings
     *            The warnings.
     */
    public void setWarnings(List<Warning> warnings) {
        synchronized (getWarnings()) {
            if (warnings != getWarnings()) {
                getWarnings().clear();

                if (warnings != null) {
                    getWarnings().addAll(warnings);
                }
            }
        }
    }

}
