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

package com.mycompany.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletModeFactory;
import com.liferay.portal.kernel.portlet.WindowStateFactory;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.util.PortalUtil;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Concepts borrowed heavily from Liferay's ActionUrlTag
 * 
 * @author Phillip Verheyden
 */
@Component
public class LiferayActionUrlProcessor extends AbstractModelVariableModifierProcessor {

    protected final Log LOG = LogFactory.getLog(getClass());

    /**
     * @param elementName
     */
    public LiferayActionUrlProcessor() {
        super("actionurl");
    }

    /**
     * @param string
     */
    public LiferayActionUrlProcessor(String elementName) {
        super(elementName);
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        try {
            IWebContext context = (IWebContext) arguments.getContext();
            HttpServletRequest request = context.getHttpServletRequest();

            String portletName = element.getAttributeValue("portletName");
            if (portletName == null) {
                portletName = getPortletName(request);
            }

            String lifeCycle = getLifeCycle();
            LiferayPortletURL liferayPortletURL = getLiferayPortletURL(request, portletName, lifeCycle);

            if (liferayPortletURL == null) {
                LOG.error("Render response is null because this tag is not being " + "called within the context of a portlet");

                return;
            }

            String windowState = element.getAttributeValue("windowState");
            if (windowState != null) {
                liferayPortletURL.setWindowState(WindowStateFactory.getWindowState(windowState));
            }

            String portletMode = element.getAttributeValue("portletMode");
            if (portletMode != null) {
                liferayPortletURL.setPortletMode(PortletModeFactory.getPortletMode(portletMode));
            }

            boolean secure = Boolean.parseBoolean(element.getAttributeValue("secure"));
            if (secure) {
                liferayPortletURL.setSecure(secure);
            } else {
                liferayPortletURL.setSecure(PortalUtil.isSecure(request));
            }

            String copyCurrentRenderParameters = element.getAttributeValue("copyCurrentRenderParameters");
            if (copyCurrentRenderParameters != null) {
                liferayPortletURL.setCopyCurrentRenderParameters(Boolean.parseBoolean(copyCurrentRenderParameters));
            }

            String escapeXml = element.getAttributeValue("escapeXml");
            if (escapeXml != null) {
                liferayPortletURL.setEscapeXml(Boolean.parseBoolean(escapeXml));
            }

            String name = element.getAttributeValue("name");
            if (lifeCycle.equals(PortletRequest.ACTION_PHASE) && name != null) {
                liferayPortletURL.setParameter(ActionRequest.ACTION_NAME, name);
            }

            String resourceId = element.getAttributeValue("resourceId");
            if (resourceId != null) {
                liferayPortletURL.setResourceID(resourceId);
            }

            String cacheLevel = element.getAttributeValue("cacheLevel");
            if (cacheLevel != null) {
                liferayPortletURL.setCacheability(cacheLevel);
            }

            if (element.getAttributeValue("categoryId") != null) {
                Long categoryId = (Long)StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue("categoryId"));
                liferayPortletURL.setParameter("categoryId", categoryId.toString());
            }
            
            if (element.getAttributeValue("productId") != null) {
                Long productId = (Long)StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue("productId"));
                liferayPortletURL.setParameter("productId", productId.toString());
            }

            
            String portletURLToString = liferayPortletURL.toString();
            String var = element.getAttributeValue("var");
            if (var != null) {
                addToModel(arguments, var, portletURLToString);
            } else {
                addToModel(arguments, "url", portletURLToString);
            }
        } catch (Exception e) {
            LOG.error("There was a problem generating the URL: " + e);
        }
    }

    @Override
    public int getPrecedence() {
        return 0;
    }

    public String getLifeCycle() {
        return PortletRequest.ACTION_PHASE;
    }

    protected static String getPortletName(HttpServletRequest request) {
        PortletRequest portletRequest = (PortletRequest) request.getAttribute(JavaConstants.JAVAX_PORTLET_REQUEST);

        if (portletRequest == null) {
            return null;
        }

        LiferayPortletConfig liferayPortletConfig = (LiferayPortletConfig) request.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);

        return liferayPortletConfig.getPortletId();
    }

    protected static LiferayPortletURL getLiferayPortletURL(HttpServletRequest request, String portletName, String lifecycle) {
        PortletRequest portletRequest = (PortletRequest) request.getAttribute(JavaConstants.JAVAX_PORTLET_REQUEST);

        if (portletRequest == null) {
            return null;
        }

        PortletResponse portletResponse = (PortletResponse) request.getAttribute(JavaConstants.JAVAX_PORTLET_RESPONSE);
        LiferayPortletResponse liferayPortletResponse = PortalUtil.getLiferayPortletResponse(portletResponse);
        return liferayPortletResponse.createLiferayPortletURL(portletName, lifecycle);
    }

}
