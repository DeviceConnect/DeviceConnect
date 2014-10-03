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

package org.restlet.data;

/**
 * Client specific data related to a call. When extracted from a request, most
 * of these data are directly taken from the underlying headers. There are some
 * exceptions: agentAttributes and mainAgentProduct which are taken from the
 * agent name (for example the "user-agent" header for HTTP requests).<br>
 * <br>
 * As described by the HTTP specification, the "user-agent" can be seen as a
 * ordered list of products name (ie a name and a version) and/or comments.<br>
 * <br>
 * Each HTTP client (mainly browsers and web crawlers) defines its own
 * "user-agent" header which can be seen as the "signature" of the client.
 * Unfortunately, there is no rule to identify clearly a kind a client and its
 * version (let's say Firefox 2.x, Internet Explorer IE 7.0, Opera, etc)
 * according to its signature. Each signature follow its own rules which may
 * vary according to the version of the client.<br>
 * <br>
 * In order to help retrieving interesting data such as product name (Firefox,
 * IE, etc), version, operating system, Restlet users has the ability to define
 * their own way to extract data from the "user-agent" header. It is based on a
 * list of templates declared in a file called "agent.properties" and located in
 * the classpath in the sub directory "org/restlet/data". Each template
 * describes a typical user-agent string and allows to use predefined variables
 * that help to retrieve the content of the agent name, version, operating
 * system.<br>
 * <br>
 * The "user-agent" string is confronted to the each template from the beginning
 * of the property file to the end. The loop stops at the first matched
 * template.<br>
 * <br>
 * Here is a sample of such template:<br>
 * 
 * <pre>
 * #Firefox for Windows
 *  Mozilla/{mozillaVersion} (Windows; U; {agentOs}; {osData}; rv:{releaseVersion}) Gecko/{geckoReleaseDate} {agentName}/{agentVersion}
 * </pre>
 * 
 * This template matches the "user-agent" string of the Firefox client for
 * windows:
 * 
 * <pre>
 *  Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1) Gecko/20060918 Firefox/2.0
 * </pre>
 * 
 * At this time, six predefined variables are used:<br>
 * <table>
 * <tr>
 * <th>Name</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>agentName</td>
 * <td>Name of the user agent (i.e.: Firefox)</td>
 * </tr>
 * <tr>
 * <td>agentVersion</td>
 * <td>Version of the user agent</td>
 * </tr>
 * <tr>
 * <td>agentOs</td>
 * <td>Operating system of the user agent</td>
 * </tr>
 * <tr>
 * <td>agentComment</td>
 * <td>Comment string, that is to say a sequence of characters enclosed "(", or
 * ")"</td>
 * </tr>
 * <tr>
 * <td>commentAttribute</td>
 * <td>A sequence of characters enclosed by ";", "(", or ")"</td>
 * </tr>
 * <tr>
 * <td>facultativeData</td>
 * <td>A sequence of characters that can be empty</td>
 * </tr>
 * </table>
 * <br>
 * <br>
 * These variables are used to generate a {@link Product} instance with the main
 * data (name, version, comment). This instance is accessible via the
 * {@link ClientInfo#getMainAgentProduct()} method. All other variables used in
 * the template aims at catching a sequence of characters and are accessible via
 * the {@link ClientInfo#getAgentAttributes()} method.
 * 
 * @author Jerome Louvel
 */
public final class ClientInfo {

    /** Authenticated user. */
    private volatile org.restlet.security.User user;

    /**
     * Constructor.
     */
    public ClientInfo() {
        this.user = null;
    }

    /**
     * Returns the authenticated user.
     * 
     * @return The authenticated user.
     */
    public org.restlet.security.User getUser() {
        return user;
    }

    /**
     * Sets the authenticated user.
     * 
     * @param user
     *            The authenticated user.
     */
    public void setUser(org.restlet.security.User user) {
        this.user = user;
    }

}
