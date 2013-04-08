/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.interceptor.security;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.BroadleafRequestProcessor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.portlet.context.PortletWebRequest;

import com.liferay.portal.util.PortalUtil;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;


/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class LiferayRequestProcessor extends BroadleafRequestProcessor {

    @Override
    public void process(WebRequest request) {
        super.process(request);
        //grab the HTTPServlet request from Liferay
        PortletRequest portletRequest = (PortletRequest)((PortletWebRequest) request).getNativeRequest();
        HttpServletRequest servletRequest = PortalUtil.getHttpServletRequest(portletRequest);
        BroadleafRequestContext.getBroadleafRequestContext().setRequest(servletRequest);
    }
}
