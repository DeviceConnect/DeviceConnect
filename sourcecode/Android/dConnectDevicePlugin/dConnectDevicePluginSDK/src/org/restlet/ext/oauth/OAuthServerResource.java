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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.oauth.internal.Client;
import org.restlet.ext.oauth.internal.ClientManager;
import org.restlet.ext.oauth.internal.Scopes;
import org.restlet.ext.oauth.internal.TokenManager;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ServerResource;

/**
 * Base class for common resources used by the OAuth server side. Implements
 * OAuth 2.0 (RFC6749)
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 * @author Kristoffer Gronowski
 */
public abstract class OAuthServerResource extends ServerResource implements
        OAuthResourceDefs {

    public static final String PARAMETER_DEFAULT_SCOPE = "defaultScope";

    protected static ClientManager clients;

    protected static TokenManager tokens;

    /**
     * 処理の戻り値を格納する変数(Representationの戻り値が分からないので、別の仕組みを作って戻り値を返すようにした)
     */
    protected static Map<String, String> resultMap = null;    
    
    /**
     * Default constructor.
     */
    public OAuthServerResource() {
        super();
    }

    public static void init(Request request, Response response, ClientManager clients_, TokenManager tokens_) {
    	init(request, response);
        clients = clients_;
        tokens = tokens_;
    }
    
    /**
     * Get request parameter "client_id".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static Client getClient(Form params) throws OAuthException {
        // check clientId:
        String clientId = params.getFirstValue(CLIENT_ID);
        if (clientId == null || clientId.isEmpty()) {
            getLogger().warning("Could not find client ID");
            throw new OAuthException(OAuthError.invalid_request,
                    "No client_id parameter found.", null);
        }
        Client client = clients.findById(clientId);
        getLogger().fine("Client = " + client);
        if (client == null) {
        	getLogger().warning("Need to register the client : " + clientId);
            throw new OAuthException(OAuthError.invalid_request,
                    "Need to register the client : " + clientId, null);
        }

        return client;
    }

    /**
     * Get request parameter "scope".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static String[] getScope(Form params) throws OAuthException {
        String scope = params.getFirstValue(SCOPE);
        if (scope == null || scope.isEmpty()) {
            
        	/*
				要求するときにクライアントがスコープパラメータを省略した場合は 
				承認、認可サーバーは処理しなければならないのどちらか 
				事前に定義されたデフォルト値を使用して要求するか、要求を失敗 
				無効な範囲を示す...（ドラフト-IETF-OAuthの-V2-303.3。）
        	 */
        	/*
             * If the client omits the scope parameter when requesting
             * authorization, the authorization server MUST either process the
             * request using a pre-defined default value, or fail the request
             * indicating an invalid scope... (draft-ietf-oauth-v2-30 3.3.)
             */
            Object defaultScope = getResourceContext().getAttributes().get(
                    PARAMETER_DEFAULT_SCOPE);
            if (defaultScope == null || defaultScope.toString().isEmpty()) {
                throw new OAuthException(OAuthError.invalid_scope,
                        "Scope has not provided.", null);
            }
            scope = defaultScope.toString();
        }
        return Scopes.parseScope(scope);
    }

    /**
     * Get request parameter "state".
     * 
     * @param params
     * @return
     * @throws OAuthException
     */
    protected static String getState(Form params) {
        return params.getFirstValue(STATE);
    }

    /**
     * Returns the representation of the given error. The format of the JSON
     * document is according to 5.2. Error Response.
     * 
     * @param ex
     *            Any OAuthException with error
     * @return The representation of the given error.
     */
    public static Representation responseErrorRepresentation(OAuthException ex) {
        try {
            return new JsonRepresentation(ex.createErrorDocument());
        } catch (JSONException e) {
            StringRepresentation r = new StringRepresentation(
                    "{\"error\":\"server_error\",\"error_description:\":\""
                            + e.getLocalizedMessage() + "\"}");
            r.setMediaType(MediaType.APPLICATION_JSON);
            return r;
        }
    }

    public static void initResult() {
    	resultMap = new HashMap<String, String>();
    }
    
    public static Map<String, String> getResult() {
    	return resultMap;
    }
    
    public static void addResultValue(String key, String value) {
    	resultMap.put(key, value);
    }
}
