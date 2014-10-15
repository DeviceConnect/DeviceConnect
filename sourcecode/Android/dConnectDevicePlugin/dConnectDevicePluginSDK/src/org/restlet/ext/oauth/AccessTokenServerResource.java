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

import org.deviceconnect.android.localoauth.temp.ResultRepresentation;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.oauth.internal.AuthSession;
import org.restlet.ext.oauth.internal.AuthSessionTimeoutException;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.Scope;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.ext.oauth.internal.Token;
import org.restlet.representation.Representation;
import org.restlet.security.User;

import android.util.Base64;

/**
 * Server resource used to acquire an OAuth token. A code, or refresh token can
 * be exchanged for a working token.
 * 
 * Implements OAuth 2.0 (RFC6749)
 * 
 * Example. Attach an AccessTokenServerResource
 * 
 * <pre>
 * {
 *      &#064;code
 *      public Restlet createInboundRoot(){
 *              ...
 *              root.attach(&quot;/token&quot;, AccessTokenServerResource.class);
 *              ...
 *      }
 * }
 * </pre>
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 * @author Kristoffer Gronowski
 * 
 * @see <a href="http://tools.ietf.org/html/rfc6749#section-3.2">OAuth 2.0 (3.2.
 *      Token Endpoint)</a>
 */
public class AccessTokenServerResource extends OAuthServerResource {

    protected static Client getAuthenticatedClient() throws OAuthException {
        User authenticatedClient = getRequest().getClientInfo().getUser();
        if (authenticatedClient == null) {
            getLogger().warning("Authenticated client_id is missing.");
            return null;
        }
        // XXX: We 'know' the client was authenticated before, 'client' should
        // not be null.
        Client client = clients.findById(authenticatedClient.getIdentifier());
        getLogger().fine(
                "Requested by authenticated client " + client.getClientId());
        return client;
    }

    protected static void ensureGrantTypeAllowed(Client client, GrantType grantType)
            throws OAuthException {
        if (!client.isGrantTypeAllowed(grantType)) {
            throw new OAuthException(OAuthError.unauthorized_client,
                    "Unauthorized grant type.", null);
        }
    }

    /**
     * Handles the {@link Post} request. The client MUST use the HTTP "POST"
     * method when making access token requests. (3.2. Token Endpoint)
     * 
     * @param input
     *            HTML form formated token request per oauth-v2 spec.
     * @return JSON response with token or error.<br>
     * 			※Local OAuth ではResultRepresentation型のポインタを返す。<br>
     * 			getResult()=trueならアクセストークンが含まれている。アクセストークンはgetText()で取得する。<br>
     * 			getResult()=falseならエラー。アクセストークンは取得できない。<br>
     */
    public static Representation requestToken(Representation input)
            throws OAuthException, JSONException {
        getLogger().fine("Grant request");
        final Form params = new Form(input);

        final GrantType grantType = getGrantType(params);
        switch (grantType) {
        case authorization_code:
            getLogger().info("Authorization Code Grant");
            return doAuthCodeFlow(params);
//        case password:
//        	getLogger().info("Resource Owner Password Credentials Grant");
//            return doPasswordFlow(params);
//        case client_credentials:
//        	getLogger().info("Client Credentials Grantt");
//            return doClientFlow(params);
//        case refresh_token:
//        	getLogger().info("Refreshing an Access Token");
//            return doRefreshFlow(params);
        default:
            getLogger().warning("Unsupported flow: " + grantType);
            throw new OAuthException(OAuthError.unsupported_grant_type,
                    "Flow not supported", null);
        }
    }

    /**
     * Get request parameter "grant_type".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static GrantType getGrantType(Form params) throws OAuthException {
        String typeString = params.getFirstValue(GRANT_TYPE);
        getLogger().info("Type: " + typeString);
        try {
            GrantType type = Enum.valueOf(GrantType.class, typeString);
            getLogger().fine("Found flow - " + type);
            return type;
        } catch (IllegalArgumentException iae) {
            throw new OAuthException(OAuthError.unsupported_grant_type,
                    "Unsupported flow", null);
        } catch (NullPointerException npe) {
            throw new OAuthException(OAuthError.invalid_request,
                    "No grant_type parameter found.", null);
        }
    }

    /**
     * Get request parameter "code".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static String getCode(Form params) throws OAuthException {
        String code = params.getFirstValue(CODE);
        if (code == null || code.isEmpty()) {
            throw new OAuthException(OAuthError.invalid_request,
                    "Mandatory parameter code is missing", null);
        }
        return code;
    }

    // XXX [MEMO]Restletコードをstatic化。
    /**
     * Get request parameter "redirect_uri".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static String getRedirectURI(Form params) throws OAuthException {
        String redirUri = params.getFirstValue(REDIR_URI);
        if (redirUri == null || redirUri.isEmpty()) {
            throw new OAuthException(OAuthError.invalid_request,
                    "Mandatory parameter redirect_uri is missing", null);
        }
        return redirUri;
    }
    
    // XXX [MEMO]追加。
    protected static String getApplicationName(Form params) throws OAuthException {
        String base64ApplicationName = params.getFirstValue(APPLICATION_NAME);
        String applicationName = new String(Base64.decode(base64ApplicationName, Base64.DEFAULT));
        if (applicationName == null || applicationName.isEmpty()) {
            throw new OAuthException(OAuthError.invalid_request,
                    "Mandatory parameter application_name is missing", null);
        }
        return applicationName;
    }

    /**
     * Response JSON document with valid token. The format of the JSON document
     * is according to 5.1. Successful Response.
     * 
     * @param token
     *            The token generated by the client.
     * @param requestedScope
     *            The scope originally requested by the client.
     * @return The token representation as described in RFC6749 5.1. <br>
     * 			※Local OAuth ではResultRepresentation型のポインタを返し、result=trueならアクセストークンを設定して返す。<br>
     * 			  アクセストークンは、getText()で返す。
     * @throws ResourceException
     */
    protected static Representation responseTokenRepresentation(Token token,
            String[] requestedScope) throws JSONException {
        JSONObject response = new JSONObject();

        response.put(TOKEN_TYPE, token.getTokenType());
        response.put(ACCESS_TOKEN, token.getAccessToken());
//        response.put(EXPIRES_IN, token.getExpirePeriod());
        String refreshToken = token.getRefreshToken();
        if (refreshToken != null && !refreshToken.isEmpty()) {
            response.put(REFRESH_TOKEN, refreshToken);
        }
        Scope[] scope = token.getScope();
        if (!Scopes.isIdentical(Scope.toScopeStringArray(scope), requestedScope)) {
            /*
             * OPTIONAL, if identical to the scope requested by the client,
             * otherwise REQUIRED. (5.1. Successful Response)
             */
            response.put(SCOPE, Scopes.toString(scope));
        }

        return new JsonRepresentation(response);
    }

    /**
     * Executes the 'authorization_code' flow. (4.1.3. Access Token Request)
     * 
     * @param params
     * @return
     * @throws OAuthException
     * @throws JSONException
     */
    private static Representation doAuthCodeFlow(Form params) throws OAuthException,
            JSONException {
        // The flow require authenticated client.
        Client client = getAuthenticatedClient();
        if (client == null) {
            // Use the public client. (4.1.3. Access Token Request)
            client = getClient(params);
        }

        ensureGrantTypeAllowed(client, GrantType.authorization_code);

        String code = getCode(params);

        /*
         * ensure that the authorization code was issued to the authenticated
         * confidential client, or if the client is public, ensure that the code
         * was issued to "client_id" in the request, (4.1.3. Access Token
         * Request)
         */
        AuthSession session = tokens.restoreSession(code);
        if (!client.getClientId().equals(session.getClientId())) {
            throw new OAuthException(OAuthError.invalid_grant,
                    "The code was not issued to the client.", null);
        }

        try {
            // Ensure that the session is not timeout.
            session.updateActivity();
        } catch (AuthSessionTimeoutException ex) {
            throw new OAuthException(OAuthError.invalid_grant, "Code expired.",
                    null);
        }

        /*
         * ensure that the "redirect_uri" parameter is present if the
         * "redirect_uri" parameter was included in the initial authorization
         * request as described in Section 4.1.1, and if included ensure that
         * their values are identical. (4.1.3. Access Token Request)
         */
        if (session.getRedirectionURI().isDynamicConfigured()) {
            String redirectURI = getRedirectURI(params);
            if (!redirectURI.equals(session.getRedirectionURI().getURI())) {
                throw new OAuthException(
                        OAuthError.invalid_grant,
                        "The redirect_uri is not identical to the one included in the initial authorization request.",
                        null);
            }
        }
        
        String applicationName = getApplicationName(params);
        
        Token token = tokens.generateToken(client, session.getScopeOwner(),
                session.getGrantedScope(), applicationName);

        String accessToken = token.getAccessToken();
        ResultRepresentation resultRepresentation = new ResultRepresentation();
        resultRepresentation.setResult(true);
        resultRepresentation.setText(accessToken);
        return resultRepresentation;
    }
}
