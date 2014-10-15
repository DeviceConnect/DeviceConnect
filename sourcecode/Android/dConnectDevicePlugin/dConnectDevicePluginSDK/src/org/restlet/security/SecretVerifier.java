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

package org.restlet.security;

/**
 * Verifier of identifier/secret couples. By default, it extracts the identifier
 * and the secret from the {@link ChallengeResponse}. If the verification is
 * successful, it automatically adds a new {@link User} for the given
 * identifier.
 * 
 * @author Jerome Louvel
 */
public abstract class SecretVerifier implements Verifier {

    /**
     * Compares that two secrets are equal and not null.
     * 
     * @param secret1
     *            The input secret.
     * @param secret2
     *            The output secret.
     * @return True if both are equal.
     */
    public static boolean compare(char[] secret1, char[] secret2) {
        boolean result = false;

        if ((secret1 != null) && (secret2 != null)) {
            // None is null
            if (secret1.length == secret2.length) {
                boolean equals = true;

                for (int i = 0; (i < secret1.length) && equals; i++) {
                    equals = (secret1[i] == secret2[i]);
                }

                result = equals;
            }
        }

        return result;
    }

}
