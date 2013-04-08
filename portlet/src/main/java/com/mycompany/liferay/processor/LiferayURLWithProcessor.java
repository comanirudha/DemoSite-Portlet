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

import org.springframework.stereotype.Component;
import org.thymeleaf.Arguments;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.dom.Element;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.processor.attr.AbstractLocalVariableDefinitionAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

import com.liferay.portal.kernel.portlet.LiferayPortletURL;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 *
 * @author Tommi Hannikkala <tommi@hannikkala.com>
 */
@Component
public class LiferayURLWithProcessor extends AbstractLocalVariableDefinitionAttrProcessor {

    public LiferayURLWithProcessor() {
        super("with");
    }

    @Override
    public int getPrecedence() {
        return 13000;
    }

    @Override
    protected Map<String, Object> getNewLocalVariables(Arguments arguments,
            Element element, String attributeName) {
        IWebContext context = (IWebContext) arguments.getContext();
        HttpServletRequest request = context.getHttpServletRequest();

        final String attributeValue = element.getAttributeValue(attributeName);

        final AssignationSequence assignations =
                StandardExpressionProcessor.parseAssignationSequence(
                        arguments, attributeValue, false /* no parameters without value */);
        if (assignations == null) {
            throw new TemplateProcessingException("Could not parse value as attribute assignations: \"" + attributeValue + "\"");
        }

        final Map<String, Object> newLocalVariables = new HashMap<String, Object>(assignations.size() + 1, 1.0f);
        for (final Assignation assignation : assignations) {

            final String varName = assignation.getLeft().getValue();
            final Expression expression = assignation.getRight();
            final Object varValue = StandardExpressionProcessor.executeExpression(arguments, expression);

            newLocalVariables.put(varName, varValue);

        }

        String varName = null;
        if (newLocalVariables.containsKey("var")) {
            varName = newLocalVariables.get("var").toString();
            newLocalVariables.remove("var");
        }

        LiferayURLUtil urlUtil = new LiferayURLUtil("liferay");
        LiferayPortletURL portletURL = urlUtil.createUrl(newLocalVariables, request);

        Map<String, Object> newVariables = new HashMap<String, Object>();
        if (varName != null) {
            newVariables.put(varName, portletURL.toString());
        } else {
            throw new TemplateProcessingException("liferay:with must contain 'var' variable for new variable definition.");
        }

        return newVariables;
    }

}