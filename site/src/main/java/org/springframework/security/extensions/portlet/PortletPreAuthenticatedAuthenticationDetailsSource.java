package org.springframework.security.extensions.portlet;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.portlet.PortletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Used by the PortletAuthenticationProcessingInterceptor in order to build a user details with the correct authorities
 * as well as put in the userinfo map from the portlet request
 * 
 * @author Phillip Verheyden
 */
public class PortletPreAuthenticatedAuthenticationDetailsSource implements AuthenticationDetailsSource<PortletRequest, GrantedAuthoritiesContainer> {

    /**
     * Used in order to map specific Liferay roles to internal authorities
     */
    protected Map<String, String[]>  authoritiesMap;

    @Override
    public GrantedAuthoritiesContainer buildDetails(PortletRequest context) {
        //loop through the liferay roles
        List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        for (Map.Entry<String, String[]> entry : authoritiesMap.entrySet()) {
            String liferayRole = entry.getKey();
            String[] broadleafRoles = entry.getValue();
            if (context.isUserInRole(liferayRole)) {
                for (String role : broadleafRoles) {
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }
        }
        
        return new PortletPreAuthenticatedAuthenticationDetails((Map)context.getAttribute(PortletRequest.USER_INFO), authorities);
    }
    
    public Map<String, String[]> getAuthoritiesMap() {
        return authoritiesMap;
    }
    
    public void setAuthoritiesMap(Map<String, String[]> authoritiesMap) {
        this.authoritiesMap = authoritiesMap;
    }
}
