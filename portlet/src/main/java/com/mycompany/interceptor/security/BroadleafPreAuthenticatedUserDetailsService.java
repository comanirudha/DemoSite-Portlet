/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.interceptor.security;

import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.extensions.portlet.PortletAuthenticationProcessingInterceptor;
import org.springframework.security.extensions.portlet.PortletPreAuthenticatedAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesUserDetailsService;

import com.liferay.portlet.UserAttributes;

import java.util.Collection;
import java.util.Map;


/**
 * Used by the Spring Security authentication provider. This will return a UserDetails that has the necessary info to
 * be used in the CustomerStateInterceptor to look up or populate the current Customer in relation to the Portal user.
 * 
 * @author Phillip Verheyden
 * @see {@link PortletAuthenticationProcessingInterceptor}
 * @see {@link CustomerStateInterceptor}
 * @see applicationContext-security.xml
 * 
 */
public class BroadleafPreAuthenticatedUserDetailsService extends PreAuthenticatedGrantedAuthoritiesUserDetailsService {

    @Override
    protected UserDetails createuserDetails(Authentication token, Collection<? extends GrantedAuthority> authorities) {
        Map userInfo = ((PortletPreAuthenticatedAuthenticationDetails) token.getDetails()).getUserInfo();
        
        //The attributes available to the portlet are defined in the global portlet.xml
        String firstName = (String) userInfo.get(UserAttributes.USER_NAME_GIVEN);
        String lastName = (String) userInfo.get(UserAttributes.USER_NAME_FAMILY);
        String email = (String) userInfo.get(UserAttributes.USER_HOME_INFO_ONLINE_EMAIL);

        BroadleafExternalAuthenticationUserDetails user = new BroadleafExternalAuthenticationUserDetails(email, "N/A", authorities);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
