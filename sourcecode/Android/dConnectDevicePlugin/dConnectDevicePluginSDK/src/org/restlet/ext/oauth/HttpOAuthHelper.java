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

import org.restlet.Context;

/**
 * Implementation of OAuth2 Authentication. If this helper is not automatically
 * added to your Engine add it with:
 * 
 * <pre>
 * {
 *     &#064;code
 *     List&lt;AuthenticatorHelper&gt; authenticators = Engine.getInstance()
 *             .getRegisteredAuthenticators();
 *     authenticators.add(new OAuthAuthenticationHelper());
 * }
 * </pre>
 * 
 * Here is the list of parameters that are supported. They should be set before
 * an OAuth2Server or Client is started:
 * <table>
 * <tr>
 * <th>Parameter name</th>
 * <th>Value type</th>
 * <th>Default value</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>authPage</td>
 * <td>String</td>
 * <td>auth_page</td>
 * <td>Specifies where an AuthorizationServerResource should redirect
 * authorization requests for user interaction. This resource will be accessed
 * using riap, i.e. riap://application/+authPage</td>
 * </tr>
 * <tr>
 * <td>authPageTemplate</td>
 * <td>String</td>
 * <td>null</td>
 * <td>Specifies an html file or freemarker template for a GUI. If none is
 * provided Roles (scopes) will automatically granted. Accessed using clap, i.e.
 * clap:///+authPageTemplate</td>
 * </tr>
 * <td>authPageTemplate</td>
 * <td>String</td>
 * <td>null</td>
 * <td>Specifies an html file or freemarker template for a GUI. If none is
 * provided Roles (scopes) will automatically granted. Used by
 * AuthPageServerResource</td>
 * </tr>
 * <tr>
 * <td>authSkipApproved</td>
 * <td>boolean</td>
 * <td>false</td>
 * <td>If true no authorization page will be shown if the Roles (scopes) have
 * been previously approved by the user</td>
 * </tr>
 * <tr>
 * <td>loginPage</td>
 * <td>String</td>
 * <td>login</td>
 * <td>Specifing a login resource location relative to the Application root.
 * Defaults to "login". This resource will be accessed using riap, i.e.
 * riap://application/+loginPage</td>
 * </tr>
 * 
 * </td>
 * </table>
 * 
 * @author Kristoffer Gronowski
 */
public class HttpOAuthHelper  {

    /**
     * Returns the value of the "authPage" parameter.
     * 
     * @param c
     *            The context where to find the parameter.
     * @return The value of the "authPage" parameter.
     */
    public static String getAuthPage(Context c) {
        return c.getParameters().getFirstValue("authPage", "/auth_page");
    }
    public static String getErrorPageTemplate(Context c) {
        return c.getParameters().getFirstValue("errorPageTemplate");
    }

    /**
     * Constructor. Use the {@link ChallengeScheme#HTTP_OAUTH} authentication
     * scheme.
     */
    public HttpOAuthHelper() {
    }

}
