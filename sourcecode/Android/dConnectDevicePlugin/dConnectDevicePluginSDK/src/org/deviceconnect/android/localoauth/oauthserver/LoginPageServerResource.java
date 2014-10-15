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

package org.deviceconnect.android.localoauth.oauthserver;

//import freemarker.template.Configuration;
import java.util.HashMap;

import org.deviceconnect.android.localoauth.LocalOAuth2Main;
import org.deviceconnect.android.localoauth.temp.ResultRepresentation;
//import org.restlet.ext.freemarker.ContextTemplateLoader;
//import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.oauth.AuthorizationBaseServerResource;
import org.restlet.ext.oauth.OAuthException;
import org.restlet.representation.Representation;
import org.restlet.security.SecretVerifier;

/**
 * Simple login authentication resource.
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class LoginPageServerResource extends AuthorizationBaseServerResource {

    /** QueryValueキー. */
	public static final String RESULT = "result";
    /** QueryValueキー. */
	public static final String USER_ID = "user_id";
    /** QueryValueキー. */
    public static final String PASSWORD = "password";
    /** QueryValueキー. */
    public static final String CONTINUE = "continue";
    /** QueryValueキー. */
    public static final String REDIRECT_URL = "redirect_uri";

    /**
     * 成功したらredirectTemporary(uri)でリダイレクト、"login.html".
     * @return 戻り値(ResultRepresentation)
     * @throws OAuthException OAuth処理の例外
     */
    public static Representation getPage() throws OAuthException {
    	getLogger().info("Get Login");
        
    	ResultRepresentation resultRepresentation = new ResultRepresentation();
        resultRepresentation.setResult(false);
    	
    	String userId = getQueryValue(USER_ID);
    	HashMap<String, Object> data = new HashMap<String, Object>();
    	if (userId != null && !userId.isEmpty()) {
        	String password = getQueryValue(PASSWORD);
        	getLogger().info("User=" + userId + ", Pass=" + password);
        	SampleUser sampleUser = LocalOAuth2Main.getSampleUserManager()
        			.findUserById(userId);
        	if (sampleUser == null) {
        		data.put("error", "Authentication failed.");
        		data.put("error_description", "ID is invalid.");
                resultRepresentation.setError("Authentication failed.", "ID is invalid.");
        	} else {
            	boolean result = SecretVerifier.compare(password.toCharArray(),
            			sampleUser.getPassword());
            	if (result) {
                	getAuthSession().setScopeOwner(userId);
                	String uri = getQueryValue(CONTINUE);
                	getLogger().info("URI: " + uri);

                	addResultValue(RESULT, "true");
                	addResultValue(REDIRECT_URL, uri);
                	
                	getLogger().info("redirectTemporary(uri)=" + uri);
                	
                    resultRepresentation.setResult(true);
                    return resultRepresentation;

            	} else {
            		data.put("error", "Authentication failed.");
            		data.put("error_description", "Password is invalid.");
                    resultRepresentation.setError("Authentication failed.", "Password is invalid.");
            	}
        	}
    	}

        return resultRepresentation;
    }
}
