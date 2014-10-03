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


/**
 * Base class for server-side resources. It acts as a wrapper to a given call,
 * including the incoming {@link Request} and the outgoing {@link Response}. <br>
 * <br>
 * It's life cycle is managed by a {@link Finder} created either explicitly or
 * more likely implicitly when your {@link ServerResource} subclass is attached
 * to a {@link Filter} or a {@link Router} via the {@link Filter#setNext(Class)}
 * or {@link Router#attach(String, Class)} methods for example. After
 * instantiation using the default constructor, the final
 * {@link #init(Context, Request, Response)} method is invoked, setting the
 * context, request and response. You can intercept this by overriding the
 * {@link #doInit()} method. Then, if the response status is still a success,
 * the {@link #handle()} method is invoked to actually handle the call. Finally,
 * the final {@link #release()} method is invoked to do the necessary clean-up,
 * which you can intercept by overriding the {@link #doRelease()} method. During
 * this life cycle, if any exception is caught, then the
 * {@link #doCatch(Throwable)} method is invoked.<br>
 * <br>
 * Note that when an annotated method manually sets the response entity, if this
 * entity is available then it will be preserved and the result of the annotated
 * method ignored.<br>
 * <br>
 * In addition, there are two ways to declare representation variants, one is
 * based on the {@link #getVariants()} method and another one on the annotated
 * methods. Both approaches can't however be used at the same time for now.<br>
 * <br>
 * Concurrency note: contrary to the {@link org.restlet.Uniform} class and its
 * main {@link Restlet} subclass where a single instance can handle several
 * calls concurrently, one instance of {@link ServerResource} is created for
 * each call handled and accessed by only one thread at a time.
 * 
 * @author Jerome Louvel
 */
public abstract class ServerResource extends Resource {

    /**
     * Default constructor. Note that the
     * {@link #init(Context, Request, Response)}() method will be invoked right
     * after the creation of the resource.
     */
    public ServerResource() {
    }


}
