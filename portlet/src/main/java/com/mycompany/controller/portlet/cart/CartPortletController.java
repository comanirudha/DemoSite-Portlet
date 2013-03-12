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

package com.mycompany.controller.portlet.cart;

import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.order.service.exception.RemoveFromCartException;
import org.broadleafcommerce.core.order.service.exception.UpdateCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.controller.cart.BroadleafCartController;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
import org.broadleafcommerce.core.web.service.UpdateCartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.util.PortalUtil;

import javax.annotation.Resource;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.io.IOException;


/**
 * 
 * @author Phillip Verheyden
 */
@Controller
@RequestMapping("VIEW")
public class CartPortletController extends BroadleafCartController {
    
    @Resource(name="blOrderService")
    protected OrderService orderService;
    
    @Resource(name="blUpdateCartService")
    protected UpdateCartService updateCartService;
    
    @RenderMapping
    public String viewCart(RenderRequest request, RenderResponse response, Model model) throws PricingException {
        return super.cart(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model);
    }
    
    @ActionMapping("add")
    public void add(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("addToCartItem") AddToCartItem addToCartItem) throws IOException, PricingException, AddToCartException {

        super.add(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, addToCartItem);
    }

    @ActionMapping("updateQuantity")
    public void updateQuantity(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("addToCartItem") AddToCartItem addToCartItem) throws IOException, PricingException, UpdateCartException, RemoveFromCartException {
        super.updateQuantity(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, addToCartItem);
    }

    @ActionMapping("remove")
    public void remove(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("addToCartItem") AddToCartItem addToCartItem) throws IOException, PricingException, RemoveFromCartException {
        super.remove(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, addToCartItem);
    }

    @ActionMapping("addPromo")
    public void addPromo(ActionRequest request, ActionResponse response, Model model,
            @RequestParam("promoCode") String customerOffer) throws IOException, PricingException {
        super.addPromo(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, customerOffer);
    }

    @ActionMapping("removePromo")
    public void removePromo(ActionRequest request, ActionResponse response, Model model,
            @RequestParam("offerCodeId") Long offerCodeId) throws IOException, PricingException {
        super.removePromo(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, offerCodeId);
    }
}
