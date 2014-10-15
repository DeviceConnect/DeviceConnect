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

package org.restlet.ext.oauth.internal;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Utility methods for converting between OAuth Scopes and Restlet Roles
 * 
 * @author Martin Svensson
 */
public class Scopes {

    public static String toString(Scope[] scopes) {
        StringBuilder sb = new StringBuilder();
        for (Scope scope : scopes) {
            sb.append(' ');
            sb.append(scope.getScope());
        }
        return sb.substring(1);
    }

    public static String[] parseScope(String scopes) {
        if (scopes != null && scopes.length() > 0) {
            StringTokenizer st = new StringTokenizer(scopes, " ");
            String[] scope = new String[st.countTokens()];
            for (int i = 0; st.hasMoreTokens(); i++)
                scope[i] = st.nextToken();
            return scope;
        }
        return new String[0];
    }

    public static boolean isIdentical(String[] a, String[] b) {
        List<String> al = Arrays.asList(a);
        List<String> bl = Arrays.asList(b);
        return al.containsAll(bl) && bl.containsAll(al);
    }
}
