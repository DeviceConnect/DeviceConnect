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

import org.restlet.data.ClientInfo;
import org.restlet.data.Cookie;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.util.Series;

/**
 * Generic request sent by client connectors. It is then received by server
 * connectors and processed by {@link Restlet}s. This request can also be
 * processed by a chain of Restlets, on both client and server sides. Requests
 * are uniform across all types of connectors, protocols and components.
 * 
 * @see org.restlet.Response
 * @see org.restlet.Uniform
 * @author Jerome Louvel
 */
public class Request extends Message {

    /** The client-specific information. */
    private volatile ClientInfo clientInfo;

    /** The cookies provided by the client. */
    private volatile Series<Cookie> cookies;

    /** The original reference. */
    private volatile Reference originalRef;

    /** The resource reference. */
    private volatile Reference resourceRef;

    /**
     * Constructor.
     */
    public Request() {
        this((Method) null, (Reference) null, (Representation) null);
    }

    /**
     * Constructor.
     * 
     * @param method
     *            The call's method.
     * @param resourceRef
     *            The resource reference.
     * @param entity
     *            The entity.
     */
    public Request(Method method, Reference resourceRef, Representation entity) {
        super(entity);
        this.clientInfo = null;
        this.cookies = null;
        this.originalRef = null;
        this.resourceRef = resourceRef;
    }

    /**
     * Returns the client-specific information. Creates a new instance if no one
     * has been set.
     * 
     * @return The client-specific information.
     */
    public ClientInfo getClientInfo() {
        // Lazy initialization with double-check.
        ClientInfo c = this.clientInfo;
        if (c == null) {
            synchronized (this) {
                c = this.clientInfo;
                if (c == null) {
                    this.clientInfo = c = new ClientInfo();
                }
            }
        }
        return c;
    }

    /**
     * Returns the modifiable series of cookies provided by the client. Creates
     * a new instance if no one has been set.<br>
     * <br>
     * Note that when used with HTTP connectors, this property maps to the
     * "Cookie" header.
     * 
     * @return The cookies provided by the client.
     */
    public Series<Cookie> getCookies() {
        // Lazy initialization with double-check.
        Series<Cookie> c = this.cookies;
        if (c == null) {
            synchronized (this) {
                c = this.cookies;
                if (c == null) {
                    this.cookies = c = new Series<Cookie>(Cookie.class);
                }
            }
        }
        return c;
    }
    /**
     * Returns the original reference as requested by the client. Note that this
     * property is not used during request routing. See the
     * {@link #getResourceRef()} method for details.
     * 
     * @return The original reference.
     * @see #getResourceRef()
     */
    public Reference getOriginalRef() {
        return this.originalRef;
    }

    /**
     * Returns the reference of the target resource. This reference is
     * especially important during routing, dispatching and resource finding.
     * During such processing, its base reference is constantly updated to
     * reflect the reference of the parent Restlet or resource and the remaining
     * part of the URI that must be routed or analyzed.
     * 
     * If you need to get the URI reference originally requested by the client,
     * then you should use the {@link #getOriginalRef()} method instead. Also,
     * note that beside the update of its base property, the resource reference
     * can be modified during the request processing.
     * 
     * For example, the {@link org.restlet.service.TunnelService} associated to
     * an application can extract some special extensions or query parameters
     * and replace them by semantically equivalent properties on the request
     * object. Therefore, the resource reference can become different from the
     * original reference.
     * 
     * Finally, when sending out requests via a dispatcher such as
     * {@link Context#getClientDispatcher()} or
     * {@link Context#getServerDispatcher()}, if the reference contains URI
     * template variables, those variables are automatically resolved using the
     * request's attributes.
     * 
     * @return The reference of the target resource.
     * @see #getOriginalRef()
     * @see #getHostRef()
     */
    public Reference getResourceRef() {
        return this.resourceRef;
    }

    /**
     * Sets the client-specific information.
     * 
     * @param clientInfo
     *            The client-specific information.
     */
    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    /**
     * Sets the modifiable series of cookies provided by the client. Note that
     * when used with HTTP connectors, this property maps to the "Cookie"
     * header. This method clears the current series and adds all entries in the
     * parameter series.
     * 
     * @param cookies
     *            A series of cookies provided by the client.
     */
    public void setCookies(Series<Cookie> cookies) {
        synchronized (getCookies()) {
            if (cookies != getCookies()) {
                if (getCookies() != null) {
                    getCookies().clear();
                }

                if (cookies != null) {
                    getCookies().addAll(cookies);
                }
            }
        }
    }

    /**
     * Sets the original reference requested by the client.
     * 
     * @param originalRef
     *            The original reference.
     * @see #getOriginalRef()
     */
    public void setOriginalRef(Reference originalRef) {
        this.originalRef = originalRef;
    }

    /**
     * Sets the target resource reference. If the reference is relative, it will
     * be resolved as an absolute reference. Also, the context's base reference
     * will be reset. Finally, the reference will be normalized to ensure a
     * consistent handling of the call.
     * 
     * @param resourceRef
     *            The resource reference.
     * @see #getResourceRef()
     */
    public void setResourceRef(Reference resourceRef) {
        this.resourceRef = resourceRef;
    }

}
