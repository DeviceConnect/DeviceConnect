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

package org.restlet.ext.oauth;

import java.util.concurrent.ConcurrentMap;

import org.restlet.data.CookieSetting;
//import org.restlet.ext.freemarker.ContextTemplateLoader;
//import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.oauth.internal.AuthSession;
import org.restlet.ext.oauth.internal.AuthSessionTimeoutException;
import org.restlet.ext.oauth.internal.RedirectionURI;
import org.restlet.representation.Representation;


/**
 * Base Restlet resource class for Authorization service resource. Handle errors
 * according to OAuth2.0 specification, and manage AuthSession. Authorization
 * Endndpoint, Authorization pages, and Login pages should extends this class.
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class AuthorizationBaseServerResource extends OAuthServerResource {

    public static final String ClientCookieID = "_cid";

    /**
     * Sets up a new authorization session.
     * 
     * @param redirectUri
     *            The redirection URI.
     */
    protected static AuthSession setupAuthSession(RedirectionURI redirectUri) {
        
    	getLogger().fine("Base ref = " + getReference().getParentRef());

        AuthSession session = AuthSession.newAuthSession();
        session.setRedirectionURI(redirectUri);

        CookieSetting cs = new CookieSetting(ClientCookieID, session.getId());
        // TODO create a secure mode setting, update all cookies
        // cs.setAccessRestricted(true);
        // cs.setSecure(true);
        getResourceCookieSettings().add(cs);
        getLogger().fine("Setting cookie in SetupSession - " + session.getId());

        getResourceContext().getAttributes().put(session.getId(), session);

        return session;
    }

    /**
     * Returns the current authorization session.
     * 
     * @return The current {@link AuthSession} instance.
     */
    public static AuthSession getAuthSession() throws OAuthException {
        // Get some basic information
        String sessionId = getResourceCookies().getFirstValue(ClientCookieID);
        getLogger().fine("sessionId = " + sessionId);

        AuthSession session = (sessionId == null) ? null
                : (AuthSession) getResourceContext().getAttributes().get(sessionId);

        if (session == null) {
            return null;
        }

        try {
            session.updateActivity();
        } catch (AuthSessionTimeoutException ex) {
            // Remove timeout session
            getResourceContext().getAttributes().remove(sessionId);
            throw new OAuthException(OAuthError.server_error,
                    "Session timeout", null);
        }

        return session;
    }

    /**
     * Unget current authorization session.
     */
    protected static void ungetAuthSession() {
        String sessionId = getResourceCookies().getFirstValue(ClientCookieID);
        // cleanup cookie.
        if (sessionId != null && sessionId.length() > 0) {
            ConcurrentMap<String, Object> attribs = getResourceContext()
                    .getAttributes();
            attribs.remove(sessionId);
        }
    }

    /**
     * Helper method to format error responses according to OAuth2 spec. (Non
     * Redirect)
     * 
     * @param errPage
     *            errorPage template name
     * @param ex
     *            Any OAuthException with error
     */
    protected static Representation getErrorPage(String errPage, OAuthException ex) {
		getLogger().warning("[DELETE]getErrorPage() - errPage:" + errPage + " ex.getMessage():" + ex.getMessage());

    	return null;
    }

    public static String getSessionId() {
    	String sessionId = getResourceCookieSettings().getValues(ClientCookieID);
    	return sessionId;
    }

    public static void setSessionId(String sessionId) {
    	getResourceCookieSettings().set(ClientCookieID, sessionId);
    }
}
