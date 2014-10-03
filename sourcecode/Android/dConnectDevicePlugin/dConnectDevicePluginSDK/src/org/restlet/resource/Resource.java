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

package org.restlet.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Reference;
import org.restlet.representation.Representation;
import org.restlet.util.Series;

/**
 * Base resource class exposing the uniform REST interface. Intended conceptual
 * target of a hypertext reference. An uniform resource encapsulates a
 * {@link Context}, a {@link Request} and a {@link Response}, corresponding to a
 * specific target resource.<br>
 * <br>
 * It also defines a precise life cycle. First, the instance is created and the
 * {@link #init(Context, Request, Response)} method is invoked. If you need to
 * do some additional initialization, you should just override the
 * {@link #doInit()} method.<br>
 * <br>
 * Then, the abstract {@link #handle()} method can be invoked. For concrete
 * behavior, see the {@link ClientResource} and {@link ServerResource}
 * subclasses. Note that the state of the resource can be changed several times
 * and the {@link #handle()} method called more than once, but always by the
 * same thread.<br>
 * <br>
 * Finally, the final {@link #release()} method can be called to clean-up the
 * resource, with a chance for the developer to do some additional clean-up by
 * overriding the {@link #doRelease()} method.<br>
 * <br>
 * Note also that throwable raised such as {@link Error} and {@link Exception}
 * can be caught in a single point by overriding the {@link #doCatch(Throwable)}
 * method.<br>
 * <br>
 * "The central feature that distinguishes the REST architectural style from
 * other network-based styles is its emphasis on a uniform interface between
 * components. By applying the software engineering principle of generality to
 * the component interface, the overall system architecture is simplified and
 * the visibility of interactions is improved. Implementations are decoupled
 * from the services they provide, which encourages independent evolvability."
 * Roy T. Fielding<br>
 * <br>
 * Concurrency note: contrary to the {@link org.restlet.Uniform} class and its
 * main {@link Restlet} subclass where a single instance can handle several
 * calls concurrently, one instance of {@link Resource} is created for each call
 * handled and accessed by only one thread at a time.
 * 
 * @see <a
 *      href="http://roy.gbiv.com/pubs/dissertation/rest_arch_style.htm#sec_5_1_5">Source
 *      dissertation</a>
 * @author Jerome Louvel
 */
public abstract class Resource {

    /** The parent context. */
    private static Context context;

    /** The handled request. */
    private static Request request;

    /** The handled response. */
    private static Response response;

    /** getQuery()の処理が複雑なので、代わりにここに格納する. */
    private static Map<String, ArrayList<String>> query = new HashMap<String, ArrayList<String>>();

    /**
     * Returns the current context.
     * 
     * @return The current context.
     */
    public static Context getResourceContext() {
        return context;
    }

    /**
     * Returns the modifiable series of cookies provided by the client. Creates
     * a new instance if no one has been set.
     * 
     * @return The cookies provided by the client.
     * @see Request#getCookies()
     */
    public static Series<Cookie> getResourceCookies() {
        return getRequest() == null ? null : getRequest().getCookies();
    }

    /**
     * Returns the modifiable series of cookie settings provided by the server.
     * Creates a new instance if no one has been set.
     * 
     * @return The cookie settings provided by the server.
     * @see Response#getCookieSettings()
     */
    public static Series<CookieSetting> getResourceCookieSettings() {
        return getResourceResponse() == null ? null : getResourceResponse().getCookieSettings();
    }
    
    /**
     * Returns the logger.
     * 
     * @return The logger.
     */
    public static Logger getLogger() {
        return getResourceContext() != null ? getResourceContext().getLogger() : Context
                .getCurrentLogger();
    }

    /**
     * Returns the original reference as requested by the client. Note that this
     * property is not used during request routing.
     * 
     * @return The original reference.
     * @see Request#getOriginalRef()
     */
    public static Reference getOriginalRef() {
        return getRequest() == null ? null : getRequest().getOriginalRef();
    }

    /**
     * Returns the resource reference's optional query. Note that modifications
     * to the returned {@link Form} object aren't reported to the underlying
     * reference.
     * 
     * @return The resource reference's optional query.
     * @see Reference#getQueryAsForm()
     */
    public static Map<String, ArrayList<String>> getQuery() {
    	
    	return query;
	}

    /**
     * Returns the first value of the query parameter given its name if
     * existing, or null.
     * 
     * @param name
     *            The query parameter name.
     * @return The first value of the query parameter.
     */
    public static String getQueryValue(String name) {
        String result = null;
        Map<String, ArrayList<String>> query = getQuery();

        if (query != null) {
            result = query.get(name).get(0);
        }

        return result;
    }

    /**
     * Returns the URI reference.
     * 
     * @return The URI reference.
     */
    public static Reference getReference() {
        return getRequest() == null ? null : getRequest().getResourceRef();
    }

    /**
     * Returns the handled request.
     * 
     * @return The handled request.
     */
    public static Request getRequest() {
        return request;
    }

    /**
     * Returns the handled response.
     * 
     * @return The handled response.
     */
    public static Response getResourceResponse() {
        return response;
    }

    /**
     * Returns the response entity representation.
     * 
     * @return The response entity representation.
     */
    public static Representation getResourceResponseEntity() {
        return getResourceResponse() == null ? null : getResourceResponse().getEntity();
    }

    public static void init(Context context_) {
        context = context_;
    }
    
    public static void init(Request request_, Response response_) {
        request = request_;
        response = response_;
    }
}
