package org.springframework.security.extensions.portlet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthoritiesContainer;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

public class PortletPreAuthenticatedAuthenticationDetails implements GrantedAuthoritiesContainer {

    private static final long serialVersionUID = 1L;

    protected List<? extends GrantedAuthority> grantedAuthorities;
    protected Map userInfo;

    public PortletPreAuthenticatedAuthenticationDetails(Map userInfo, List<? extends GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
        this.userInfo = userInfo;
    }
    
    @Override
    public List<? extends GrantedAuthority> getGrantedAuthorities() {
        Assert.notNull(grantedAuthorities, "Pre-authenticated granted authorities have not been set");
        return grantedAuthorities;
    }
    
    public Map getUserInfo() {
        return userInfo;
    }
    
}
