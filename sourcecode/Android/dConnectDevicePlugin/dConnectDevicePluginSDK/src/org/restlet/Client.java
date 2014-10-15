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

import java.util.Arrays;
import java.util.List;

import org.restlet.data.Protocol;

/**
 * Connector acting as a generic client. It internally uses one of the available
 * connector helpers registered with the Restlet engine.<br>
 * <br>
 * Concurrency note: instances of this class or its subclasses can be invoked by
 * several threads at the same time and therefore must be thread-safe. You
 * should be especially careful when storing state in member variables.<br>
 * <br>
 * For advanced cases, it is possible to obtained the wrapped
 * {@link RestletHelper} instance that is used by this client to handle the
 * calls via the "org.restlet.engine.helper" attribute stored in the
 * {@link Context} object.
 * 
 * @author Jerome Louvel
 */
public class Client {

    /**
     * Constructor.
     * 
     * @param context
     *            The context.
     * @param protocols
     *            The connector protocols.
     */
    public Client(Context context, List<Protocol> protocols) {
        this(context, protocols, null);
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The context.
     * @param protocols
     *            The connector protocols.
     * @param helperClass
     *            Optional helper class name.
     */
    public Client(Context context, List<Protocol> protocols, String helperClass) {
    }

    /**
     * Constructor.
     * 
     * @param context
     *            The context.
     * @param protocol
     *            The connector protocol.
     */
    public Client(Context context, Protocol protocol) {
        this(context, (protocol == null) ? null : Arrays.asList(protocol), null);
    }

    /**
     * Constructor.
     * 
     * @param protocols
     *            The connector protocols.
     */
    public Client(List<Protocol> protocols) {
        this(null, protocols, null);
    }

    /**
     * Constructor.
     * 
     * @param protocol
     *            The connector protocol.
     */
    public Client(Protocol protocol) {
        this(null, protocol);
    }

    /**
     * Constructor.
     * 
     * @param protocolName
     *            The connector protocol.
     */
    public Client(String protocolName) {
        this(Protocol.valueOf(protocolName));
    }


    public void handle(Request request, Response response) {

    }

    /**
     * Indicates the underlying connector helper is available.
     * 
     * @return True if the underlying connector helper is available.
     */
    public boolean isAvailable() {
    	return false;
    }

}
