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

import org.deviceconnect.android.localoauth.ScopeUtil;
import org.deviceconnect.android.localoauth.temp.RedirectRepresentation;
import org.restlet.ext.oauth.internal.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Reference;
import org.restlet.ext.oauth.internal.AuthSession;
import org.restlet.ext.oauth.internal.Scope;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

//import freemarker.template.Configuration;
//import org.restlet.data.CacheDirective;
import org.restlet.ext.oauth.internal.Token;

/**
 * Helper class to the AuhorizationResource Handles Authorization requests. By
 * default it will accept all scopes requested.
 * 
 * AuhorizationResourceするためのヘルパークラスには、認証要求を処理します。
 * デフォルトでは、要求されたすべてのスコープを受け入れます。
 * 
 * To intercept and allow a user to control authorization you should set the
 * OAuthHelper.setAuthPageTemplate parameter. It should contain a static HTML
 * page or a FreeMarker page that will be loaded with the CLAP protocol straight
 * from root.
 * 
 * あなたはOAuthHelper.setAuthPageTemplateパラメータを設定する必要があり、
 * 許可を制御するためのユーザをインターセプトできるようにします。
 * これは、静的なHTMLページやストレートルートからCLAPプロトコルでロードされます
 * FreeMarkerのページが含まれている必要があります。
 * 
 * Example. Add an AuthPageResource to your inbound root.
 * 
 * インバウンドルートにAuthPageResourceを追加します。
 * 
 * <pre>
 * {
 *      &#064;code
 *      public Restlet createInboundRoot(){
 *              ...
 *              root.attach(OAuthHelper.getAuthPage(getContext()), AuthPageServerResource.class);
 *              //Set Template for AuthPage:
 *              OAuthHelper.setAuthPageTemplate(&quot;authorize.html&quot;, getContext());
 *              //Dont ask for approval if previously approved
 *              OAuthHelper.setAuthSkipApproved(true, getContext());
 *              ...
 *      }
 *      
 * }
 * </pre>
 * 
 * The FreeMarker data model looks like the following
 * 
 * FreeMarkerのデータモデルは次のようになります。
 * 
 * <pre>
 * {
 *     &#064;code
 *     HashMap&lt;String, Object&gt; data = new HashMap&lt;String, Object&gt;();
 *     data.put(&quot;target&quot;, &quot;/oauth/auth_page&quot;);
 *     data.put(&quot;clientId&quot;, clientId);
 *     data.put(&quot;clientDescription&quot;, client.toString());
 *     data.put(&quot;clientCallback&quot;, client.getRedirectUri());
 *     data.put(&quot;clientName&quot;, client.getApplicationName());
 *     data.put(&quot;requestingScopes&quot;, scopes);
 *     data.put(&quot;grantedScopes&quot;, previousScopes);
 * }
 * </pre>
 * 
 * Below is an example of a simple FreeMarker page for authorization
 * 
 * 下記の承認のための簡単なFreeMarkerのページの例である
 * 
 * <pre>
 * {@code
 * <html>
 * <head>
 * <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
 * <link rel="stylesheet" href="resources/style.css" type="text/css" media="screen"
 *   charset="utf-8">
 * <title>OAuth2 Authorization Server</title>
 * </head>
 * <body>
 *   <div id="container">
 *    <div id="header">
 *      <h2>OAuth authorization page</h2>
 *      <section id="intro">
 *         <h2>Application requesting scope</h2>
 *         <p>Client ClientId = ${clientId} CB = ${clientDescription} wants to get access to your information.</p>
 *       </section>
 *     </div>
 *     <aside>
 *     <form action="${target}" method="get">
 *    <h4>The following private info is requested</h4>
 * 
 *     <#list requestingScopes as r> <input type="checkbox" name="scope" value="${r}" checked />
 *       <b>${r}</b><br/>
 *     </#list> 
 *     <#if grantedScopes?has_content>
 *       <hr />
 *       <h4>Previously approved scopes</h4>
 *       <#list grantedScopes as g> <input type="checkbox" name="scope" value="${g}" checked />
 *         <b>${g}</b><br/>
 *       </#list>
 *     </#if>
 *     <br/>
 *     <input type="submit" name="action" value="Reject"/>
 *     <input type="submit" name="action" value="Accept" />
 *     </form>
 *     </aside>
 *     <footer>
 *       <p class="copyright">Copyright &copy; 2010 Ericsson Inc. All rights reserved.</p>
 *     </footer>
 *   </div>
 * </body>
 * </html>
 * }
 * </pre>
 * 
 * 
 * should be set in the attributes. It should contain a static HTML page or a
 * FreeMarker page that will be loaded with the CLAP protocol straight from
 * root.
 * 
 * 属性を設定する必要があります。
 * これは、静的なHTMLページやストレートルートから拍手プロトコルでロードされます
 * FreeMarkerのページが含まれている必要があります。
 * 
 * @author Kristoffer Gronowski
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */

public class AuthPageServerResource extends AuthorizationBaseServerResource {

    public static final String ACTION_ACCEPT = "Accept";

    public static final String ACTION_REJECT = "Reject";

    public static final String GRANTED_SCOPE = "granted_scope";

	public static final String ACTION = "action";
    
    /**
     * Entry point to the AuthPageResource. The AuthorizationResource dispatches
     * the call to this method. Should also be invoked by an eventual HTML page
     * FORM. In the from HTTP GET should be used and a result parameter: action
     * = Accept results in approving requested scope while action = Reject
     * results in a rejection error back to the requestor.
     * 
     * AuthPageResourceへのエントリポイント。 
	 * AuthorizationResourceは、このメソッドへの呼び出しをディスパッチします。 
	 * また、最終的なHTMLページ形式で起動する必要があります。 
	 * HTTPからのGETを使用して、結果パラメータれるべきである。 
	 * 
	 * ：アクション 
	 * = 要求されたスコープのアクションしばらく承認に結果を受け入れる = 
	 * 戻って要求元に拒否通知エラーで結果を拒否します。
     * 
     * @return HTML page with the graphical policy page
     */
    public static Representation showPage() throws OAuthException {
    	
    	/*
			上記htmlを表示、またはsubmitされた タイミングで実行される
    	*/

        String action = getQuery().get("action").get(0);
        
        // Came back after user interacted with the page
        if (action != null) {										// submitされたときはここを通る
            Map<String, ArrayList<String>> query = getQuery();
            ArrayList<String> strscopes = query.get("scope");
            
            /* Scope.toString()で変換した文字列配列をScope[]に変換する */
            String[] strScopes = strscopes.toArray(new String[0]);
            Scope[] scopes = ScopeUtil.stringToScope(strScopes);
            
            /* アプリケーション名を取得 */
            String applicationName = null;
            ArrayList<String> strApplicationNames = query.get(APPLICATION_NAME);
            if (strApplicationNames != null && strApplicationNames.size() > 0) {
                applicationName = strApplicationNames.get(0);
            }

            RedirectRepresentation redirectRepresentation = handleAction(action, scopes, applicationName);	// この中でセッションを保存し認可コードを取得する
            return redirectRepresentation;
        }
        return new EmptyRepresentation(); // Will redirect
    }

    /**
     * locationにパラメータをつけてredirectTemporary(location)を実行する。</p>
     * FORMの応答を処理するヘルパーメソッド。<br>
     * Locationヘッダを307に設定して返します。<br>
     * 「トークンの流れは「要求されたか、コードが含まれている場合にはトークンです。<br>
     * <br>
     * 
     * Helper method to handle a FORM response. Returns with setting a 307 with
     * the location header. Token if the token flow was requested or code is
     * included.
     * 
     * @param action
     *            as interacted by the user.
     * @param grantedScope
     *            the scopes that was approved.
     * @param applicationName アプリケーション名
     */
    protected static RedirectRepresentation handleAction(String action, Scope[] grantedScope, String applicationName)
            throws OAuthException {
        AuthSession session = getAuthSession();
        session.setGrantedScope(grantedScope);
        session.setApplicationName(applicationName);

        if (action.equals(ACTION_REJECT)) {
            getLogger().fine("Rejected.");
            throw new OAuthException(OAuthError.access_denied, "Rejected.",
                    null);
        }
        getLogger().fine("Accepting scopes - in handleAction");
        Client client = clients.findById(session.getClientId());
        String scopeOwner = session.getScopeOwner();

        // Create redirection
        final Reference location = new Reference(session.getRedirectionURI()		// locationに、session.getRedirectionURI()設定。
                .getURI());

        String state = session.getState();
        if (state != null && !state.isEmpty()) {
            // Setting state information back.
            location.addQueryParameter(STATE, state);
        }

        // Add query parameters for each flow.
        Map<String, Object> options = new HashMap<String, Object>(); 
        ResponseType flow = session.getAuthFlow();
        if (flow.equals(ResponseType.token)) {
            Token token = tokens
                    .generateToken(client, scopeOwner, grantedScope, applicationName);
            location.addQueryParameter(TOKEN_TYPE, token.getTokenType());
            location.addQueryParameter(ACCESS_TOKEN, token.getAccessToken());
            Scope[] scopes = token.getScope();
            String[] strScopeNames = ScopeUtil.scopeToScopeNames(scopes);
            if (!Scopes.isIdentical(strScopeNames, session.getRequestedScope())) {
                // OPTIONAL, if identical to the scope requested by the client,
                // otherwise REQUIRED. (4.2.2. Access Token Response)
                location.addQueryParameter(SCOPE, Scopes.toString(scopes));
            }
        } else if (flow.equals(ResponseType.code)) { // 認可コードグラントの場合はここを通る
            String code = tokens.storeSession(session); // セッションをTokenManager.sessionsに格納して認可コード(セッションに割り当てた番号)を取得する
            location.addQueryParameter(CODE, code); // URIに添付する
            /* optionsに認可コードを格納して返す */
            options.put(CODE, code);
        }

        // Reset the state
        session.setState(null);

        /*
         * We might don't need to do this. // Sets the no-store Cache-Control
         * header addCacheDirective(getResponse(), CacheDirective.noStore()); //
         * TODO: Set Pragma: no-cache
         */

        if (flow.equals(ResponseType.token)) {
            // Use fragment for Implicit Grant
            location.setFragment(location.getQuery());
            location.setQuery("");
        }

        RedirectRepresentation redirectRepresentation = new RedirectRepresentation(RedirectRepresentation.RedirectProc.nothing, options);

        return redirectRepresentation;
    }
}
