/*
 * Copyright 2013 the original author or authors.
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

package com.mycompany.controller.portlet.catalog;

import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.catalog.CategoryHandlerMapping;
import org.broadleafcommerce.core.web.controller.catalog.BroadleafCategoryController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.servlet.ModelAndView;

import com.liferay.portal.util.PortalUtil;

import javax.annotation.Resource;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;


/**
 * 
 * @author Phillip Verheyden
 */
@RequestMapping("VIEW")
@Controller
public class CategoryPortletController extends BroadleafCategoryController {

    @Resource(name = "blCatalogService")
    private CatalogService catalogService;

    @RenderMapping(params = { "categoryId" })
    public String viewCategory(RenderRequest baseRequest, RenderResponse baseResponse, Model model, @RequestParam Long categoryId) throws Exception {
        BroadleafRequestContext context = BroadleafRequestContext.getBroadleafRequestContext();
        Category category = catalogService.findCategoryById(categoryId);
        if (category != null) {
            context.getRequest().setAttribute(CategoryHandlerMapping.CURRENT_CATEGORY_ATTRIBUTE_NAME, category);
            ModelAndView result = super.handleRequest(PortalUtil.getHttpServletRequest(baseRequest), PortalUtil.getHttpServletResponse(baseResponse));
            model.addAllAttributes(result.getModel());
            return result.getViewName();
        }

        return null;
    }

}
