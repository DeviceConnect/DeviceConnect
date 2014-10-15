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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.deviceconnect.android.localoauth.temp.RedirectRepresentation;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.data.Form;
import org.restlet.data.Reference;
import org.restlet.ext.oauth.internal.AuthSession;
import org.restlet.ext.oauth.internal.RedirectionURI;
import org.restlet.ext.oauth.internal.Scope;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.ext.oauth.internal.ServerToken;
import org.restlet.representation.Representation;
import org.restlet.security.User;

/**
 * 
 * Restletの実装クラスAuthorizationService。OAuthの2.0許可要求を開始するために使用。
 * Restlet implementation class AuthorizationService. Used for initiating an
 * OAuth 2.0 authorization request.
 * 
 * このリソースは、コンテキスト属性パラメータにによって制御されている
 * This Resource is controlled by to Context Attribute Parameters
 * 
 * Implements OAuth 2.0 (RFC6749)
 * 
 * 次の例は、単純な認証サービスを設定する方法を示しています。
 * The following example shows how to set up a simple Authorization Service.
 * 
 * <pre>
 * {
 *      &#064;code
 *      public Restlet createInboundRoot(){
 *      ...
 *      ChallengeAuthenticator au = new ChallengeAuthenticator(getContext(),
 *              ChallengeScheme.HTTP_BASIC, &quot;OAuth Test Server&quot;);
 *      au.setVerifier(new MyVerifier());
 *      au.setNext(AuthorizationServerResource.class);
 *      root.attach(&quot;/authorize&quot;, au);
 *      ...
 * }
 * </pre>
 * ------------------------------------------------------------------------
 *  {
 *    @code
 *    public Restlet createInboundRoot(){
 *    ...
 *    ChallengeAuthenticator au = new ChallengeAuthenticator(getContext(),
 *            ChallengeScheme.HTTP_BASIC, "OAuth Test Server");
 *    au.setVerifier(new MyVerifier());
 *    au.setNext(AuthorizationServerResource.class);
 *    root.attach("/authorize", au);
 *    ...
 *	}
 * ------------------------------------------------------------------------
 * 
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 * @author Martin Svensson
 * 
 * @see <a href="http://tools.ietf.org/html/rfc6749#section-3.1">OAuth 2.0</a>
 */
public class AuthorizationServerResource extends
        AuthorizationBaseServerResource {

	/*
     * The authorization server MUST support the use of the HTTP "GET" method
     * [RFC2616] for the authorization endpoint and MAY support the use of the
     * "POST" method as well. (3.1. Authorization Endpoint)
     */
    public static final String PARAMETER_SUPPORT_POST = "supportPost";

    /**
     * Checks that all incoming requests have a type parameter. Requires
     * response_type, client_id and redirect_uri parameters. For the code flow
     * client_secret is also mandatory.
     */
    public static Representation requestAuthorization(Form params)
            throws OAuthException {

    	AuthSession session = getAuthSession();						// <- Cookie[ClientCookieID]をキーにgetContext().getAttributes()の配列から取得したセッションデータ
        if (session != null) {
            return doPostAuthorization(session,								// 認可要求を処理します。
                    clients.findById(session.getClientId()));
        }

        final Client client;
        final RedirectionURI redirectURI;
        try {
            client = getClient(params);								// <- Form[CLIENT_ID]とclients(ClientManager)から、clientを取得
            redirectURI = getRedirectionURI(params, client);				// <- Form[REDIR_URI]から、redirectURIを取得。redirectURIはclient.getRedirectURIs()に存在しなければ例外
        } catch (OAuthException ex) {
        	getLogger().warning("requestAuthorization() - OAuthException() - ex.getErrorDescription():" + ex.getErrorDescription());
            /*
             * MUST NOT automatically redirect the user-agent to the invalid
             * redirection URI. (see 3.1.2.4. Invalid Endpoint)
             */
            return getErrorPage(
                    HttpOAuthHelper.getErrorPageTemplate(getResourceContext()), ex);
        } catch (Exception ex) {
            // All other exception should be caught as server_error.
            OAuthException oex = new OAuthException(OAuthError.server_error,
                    ex.getMessage(), null);
    		getLogger().warning("requestAuthorization() - OAuthException() - ex.getMessage():" + ex.getMessage());
            return getErrorPage(
                    HttpOAuthHelper.getErrorPageTemplate(getResourceContext()), oex);
        }

        // Start Session
		getLogger().warning("requestAuthorization() - Start Session");
        session = setupAuthSession(redirectURI);							// <- セッション開始

        // Setup session attributes
		getLogger().warning("requestAuthorization() - Setup session attributes");
        try {
            ResponseType[] responseTypes = getResponseType(params);		// <- Form[RESPONSE_TYPE]からレスポンスタイプ取得
            if (responseTypes.length != 1) {									// <- 1件以外ならエラー
        		getLogger().warning("requestAuthorization() - Extension response types are not supported.");
                throw new OAuthException(OAuthError.unsupported_response_type,
                        "Extension response types are not supported.", null);
            }
            if (!client.isResponseTypeAllowed(responseTypes[0])) {				// <- responseTypeに認識できない値がきたらエラー
        		getLogger().warning("requestAuthorization() - Unauthorized response type.");
                throw new OAuthException(OAuthError.unauthorized_client,
                        "Unauthorized response type.", null);
            }
            session.setAuthFlow(responseTypes[0]);								// <- セッションにresponseType設定
            session.setClientId(client.getClientId());							// <- セッションにクライアントID設定
            String[] scope = getScope(params);									// <- Formからscopeを取得
            session.setRequestedScope(scope);									// <- セッションに要求するscope設定
            String state = getState(params);									// <- Formからstate値を取得
            if (state != null && !state.isEmpty()) {
                session.setState(state);										// <- state値が設定されていたらセッションに設定
            }
        } catch (OAuthException ex) {
    		getLogger().warning("requestAuthorization() - OAuthException message:" + ex.getMessage() + " description:" + ex.getErrorDescription());
            ungetAuthSession();
            throw ex;
        }

        User scopeOwner = getRequest().getClientInfo().getUser();
        if (scopeOwner != null) {
            // If user information is present, use as scope owner.
            session.setScopeOwner(scopeOwner.getIdentifier());					// セッションにownerを設定(ユーザーIDを保存する)
        }
        getLogger().warning("requestAuthorization() - session.getScopeOwner():" + session.getScopeOwner());

        if (session.getScopeOwner() == null) {									// "owner"が設定されなかった場合

        	// Redirect to login page.
            getLogger().warning("requestAuthorization() - redirectTemporary()");
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(RedirectRepresentation.SESSION_ID, session.getId());
            return new RedirectRepresentation(RedirectRepresentation.RedirectProc.loginPage, options);
        }

        getLogger().warning("requestAuthorization() - doPostAuthorization()");
        return doPostAuthorization(session, client);							// 認可要求を処理します。
    }

    /**
     * 認可要求を処理します。<br>
     * Handle the authorization request.
     * 
     * @param session
     *            The OAuth session.
     * 
     * @return The result as a {@link Representation}.
     */
    protected static Representation doPostAuthorization(AuthSession session,
            Client client) {
    	
    	// URL生成 : "riap://application" + AuthPage + "client=<clientid>" + "scope=<scope>&scope=...(scopeの数分)" + "grantedScope=<grandScope>&grantedScope=...(grandScopeの数分)"
    	Reference ref = new Reference("riap://application"  
                + HttpOAuthHelper.getAuthPage(getResourceContext()));
        ref.addQueryParameter("client", session.getClientId());

        // Requested scope should not be null.
        String[] scopes = session.getRequestedScope();
        for (String s : scopes) {
            ref.addQueryParameter("scope", s);						// sessionに格納されていたRequestedScopeをURLに追加する。"scope"
        }
        
        ServerToken token = (ServerToken) tokens.findToken(client,	// session.getScopeOwner() のtokenをTokenManagerの中から検索 
                session.getScopeOwner());
        if (token != null && !token.isExpired()) {
            for (Scope s : token.getScope()) {
                ref.addQueryParameter("grantedScope", s.toString());			// tokenが存在すれば、URLに追加する。"grantedScope"
            }
        }

        Map<String, Object> options = new HashMap<String, Object>();
        ArrayList<String> scopes2 = new ArrayList<String>(); 
        for (String s : scopes) {
        	scopes2.add(s);
        }
        ArrayList<String> grantedScopes = new ArrayList<String>(); 
        if (token != null && !token.isExpired()) {
            for (Scope s : token.getScope()) {
            	grantedScopes.add(s.toString());
            }
        }
        options.put("scope", scopes2);
        options.put("grantedScope", grantedScopes);
        return new RedirectRepresentation(RedirectRepresentation.RedirectProc.authPage, options);
    }

    /**
     * Get request parameter "response_type".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static ResponseType[] getResponseType(Form params) throws OAuthException {
        String responseType = params.getFirstValue(RESPONSE_TYPE);
        if (responseType == null || responseType.isEmpty()) {
            throw new OAuthException(OAuthError.invalid_request,
                    "No response_type parameter found.", null);
        }
        /*
         * Extension response types MAY contain a space-delimited (%x20) list of
         * values (3.1.1. Response Type)
         */
        String[] typesString = Scopes.parseScope(responseType); // The same
                                                                // format as
                                                                // scope.
        ResponseType[] types = new ResponseType[typesString.length];

        for (int i = 0; i < typesString.length; i++) {
            try {
                ResponseType type = Enum.valueOf(ResponseType.class,
                        typesString[i]);
                getLogger().fine("Found flow - " + type);
                types[i] = type;
            } catch (IllegalArgumentException iae) {
                throw new OAuthException(OAuthError.unsupported_response_type,
                        "Unsupported flow", null);
            }
        }

        return types;
    }

    /**
     * Get request parameter "redirect_uri". (See 3.1.2.3. Dynamic
     * Configuration)
     * 
     * @param params
     * @param client
     * @return
     * @throws OAuthException
     */
    protected static RedirectionURI getRedirectionURI(Form params, Client client)
            throws OAuthException {
        String redirectURI = params.getFirstValue(REDIR_URI);
        String[] redirectURIs = client.getRedirectURIs();
        
        /*
         * If multiple redirection URIs have been registered, if only part of
         * the redirection URI has been registered, or if no redirection URI has
         * been registered, the client MUST include a redirection URI with the
         * authorization request using the "redirect_uri" request parameter.
         * (See 3.1.2.3. Dynamic Configuration)
         */
        if (redirectURIs == null || redirectURIs.length != 1) {
            if (redirectURI == null || redirectURI.isEmpty()) {
                throw new OAuthException(OAuthError.invalid_request,
                        "Client MUST include a redirection URI.", null);
            }
        } else {
            if (redirectURI == null || redirectURI.isEmpty()) {
                // If the optional parameter redirect_uri is not provided,
                // we use the one provided during client registration.
                return new RedirectionURI(redirectURIs[0]);
            }
        }

        /*
			リダイレクトURIは、許可要求に含まれている場合、 
			認可サーバーは受信した値を比較し、一致しなければならない 
			少なくとも登録リダイレクトURIの1（またはURIに対して 
			[RFC3986]セクション6で定義されているコンポーネント）、もしあれば、リダイレクト 
			URIは、登録された。 (See 3.1.2.3. Dynamic Configuration)
			
         * When a redirection URI is included in an authorization request, the
         * authorization server MUST compare and match the value received
         * against at least one of the registered redirection URIs (or URI
         * components) as defined in [RFC3986] Section 6, if any redirection
         * URIs were registered. (See 3.1.2.3. Dynamic Configuration)
         */
        for (String uri : redirectURIs) {
            if (redirectURI.startsWith(uri)) {
                return new RedirectionURI(redirectURI, true);
            }
        }

        // The provided uri is no based on the uri with the client registration.
        throw new OAuthException(OAuthError.invalid_request,
                "Callback URI does not match.", null);
    }
}
