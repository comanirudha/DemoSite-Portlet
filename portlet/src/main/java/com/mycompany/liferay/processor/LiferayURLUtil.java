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

package com.mycompany.liferay.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.PortletURLFactoryUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

/**
 * Util class to create Liferay portlet URLs.
 * 
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 */
public class LiferayURLUtil {

    private static final Log log = LogFactory.getLog(LiferayURLUtil.class);

    protected static final PortletMode[] PORTLET_MODES = {
            LiferayPortletMode.VIEW,
            LiferayPortletMode.EDIT,
            LiferayPortletMode.HELP,
            LiferayPortletMode.ABOUT,
            LiferayPortletMode.CONFIG,
            LiferayPortletMode.EDIT_DEFAULTS,
            LiferayPortletMode.EDIT_GUEST,
            LiferayPortletMode.PREVIEW,
            LiferayPortletMode.PRINT
    };
    protected static final WindowState[] WINDOW_STATES = {
            LiferayWindowState.NORMAL,
            LiferayWindowState.MAXIMIZED,
            LiferayWindowState.MINIMIZED,
            LiferayWindowState.EXCLUSIVE,
            LiferayWindowState.POP_UP
    };

    private final String prefix;

    /**
     * 
     */
    public LiferayURLUtil() {
        this.prefix = "liferay";
    }

    /**
     * Different attribute prefix other than 'liferay' in your Thymeleaf HTML
     * @param prefix Attribute prefix in Thymeleafed HTML.
     */
    public LiferayURLUtil(String prefix) {
        this.prefix = prefix != null ? prefix + ":" : "";
    }

    public LiferayPortletURL createUrl(Map<String, Object> params, HttpServletRequest request) {
        long plid = getPlid(params.get("plid"), request);
        String portletName = getPortletName((String) params.get("portletname"), request);
        String lifecycle = getLifecycle((String) params.get("lifecycle"));

        LiferayPortletURL portletURL = PortletURLFactoryUtil.create(request,
                portletName, plid, lifecycle);

        try {
            portletURL.setWindowState(getWindowState((String) params.get("windowState")));
        } catch (WindowStateException e) {
            //do nothing
        }

        try {
            portletURL.setPortletMode(getPortletMode((String) params.get("portletMode")));
        } catch (PortletModeException e) {
            //do nothing
        }
        
        if (params.containsKey("action")) {
            String value = params.get("action") != null ? params.get("action").toString() : "";
            portletURL.setParameter("javax.portlet.action", value, false);
            portletURL.setLifecycle(PortletRequest.ACTION_PHASE);
        }

        params.remove("plid");
        params.remove("portletname");
        params.remove("lifecycle");
        params.remove("windowState");
        params.remove("portletMode");
        params.remove("action");

        addParameters(params, portletURL);

        return portletURL;
    }

    protected WindowState getWindowState(String windowState) {
        if (windowState != null) {
            windowState = windowState.toLowerCase();

            for (WindowState state : WINDOW_STATES) {
                if (windowState.equalsIgnoreCase(state.toString())) {
                    return state;
                }
            }
        }
        return WindowState.NORMAL;
    }

    protected PortletMode getPortletMode(String portletMode) {
        if (portletMode != null) {
            portletMode = portletMode.toLowerCase();

            for (PortletMode mode : PORTLET_MODES) {
                if (portletMode.equalsIgnoreCase(mode.toString())) {
                    return mode;
                }
            }
        }

        return PortletMode.VIEW;
    }

    protected String getAttributeValue(Element element, String attributeName) {
        Attribute attribute = element.getAttributeFromNormalizedName(this.prefix + attributeName);
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    protected String getLifecycle(String lifecycle) {
        if (lifecycle != null) {
            return lifecycle;
        }
        return PortletRequest.RENDER_PHASE;
    }

    protected String getPortletName(String portletName, HttpServletRequest request) {
        if (portletName != null) {
            return portletName;
        }
        return (String) request.getAttribute(WebKeys.PORTLET_ID);
    }

    protected long getPlid(Object plid, HttpServletRequest request) {
        if (plid != null) {
            try {
                return Long.parseLong(plid.toString());
            } catch (NumberFormatException e) {
                log.error("Couldn't parse plid value '"
                        + plid
                        + "' too long. Returning default.");
            }
        }
        ThemeDisplay themeDisplay = (ThemeDisplay) request
                .getAttribute(com.liferay.portal.kernel.util.WebKeys.THEME_DISPLAY);
        if (themeDisplay == null) {
            themeDisplay = (ThemeDisplay) request.getAttribute("LIFERAY_SHARED_THEME_DISPLAY");
        }
        Layout layout = themeDisplay.getLayout();
        return layout.getPlid();
    }

    protected void addParameters(Map<String, Object> params, LiferayPortletURL portletURL) {
        for (Entry<String, Object> entry : params.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            portletURL.setParameter(name, value, true);
        }
    }

    protected Map<String, Object> parseParams(Arguments arguments, String attributeValue) {
        final AssignationSequence assignments =
                StandardExpressionProcessor.parseAssignationSequence(
                        arguments, attributeValue, false /* no parameters without value */);
        if (assignments == null) {
            throw new TemplateProcessingException(
                    "Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        final Map<String, Object> newLocalVariables = new HashMap<String, Object>(assignments.size() + 1, 1.0f);
        for (final Assignation assignation : assignments) {

            final String varName = assignation.getLeft().getValue();
            final Expression expression = assignation.getRight();
            final Object varValue = StandardExpressionProcessor.executeExpression(arguments, expression);

            newLocalVariables.put(varName, varValue);

        }
        return newLocalVariables;
    }

}