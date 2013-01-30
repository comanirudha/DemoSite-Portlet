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

import org.broadleafcommerce.core.order.domain.NullOrderImpl;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.order.service.exception.AddToCartException;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.order.model.AddToCartItem;
import org.broadleafcommerce.core.web.order.security.CartStateRequestProcessor;
import org.broadleafcommerce.core.web.service.UpdateCartService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;

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
public class CartPortletController {
    
    @Resource(name="blOrderService")
    protected OrderService orderService;
    
    @Resource(name="blUpdateCartService")
    protected UpdateCartService updateCartService;
    
    @RequestMapping
    public String viewCart(RenderRequest request, RenderResponse response, Model model) {
        return "portlet/cart";
    }
    
//    @RequestMapping(value = "/add", produces = "application/json")
//    @ResourceMapping("addToCart")
    @ActionMapping
    public void addJson(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("addToCartItem") AddToCartItem addToCartItem) throws IOException, PricingException, AddToCartException {

        Order cart = (Order)request.getAttribute(CartStateRequestProcessor.getCartRequestAttributeName());
        
        // If the cart is currently empty, it will be the shared, "null" cart. We must detect this
        // and provision a fresh cart for the current customer upon the first cart add
        if (cart == null || cart instanceof NullOrderImpl) {
            cart = orderService.createNewCartForCustomer((Customer)request.getAttribute(CustomerStateRequestProcessor.getCustomerRequestAttributeName()));
        }
        
        updateCartService.validateCart(cart);

        cart = orderService.addItem(cart.getId(), addToCartItem, false);
        cart = orderService.save(cart,  true);
        //CartState.setCart(cart);
        request.setAttribute(CartStateRequestProcessor.getCartRequestAttributeName(), cart);

        
        //return new HashMap<String, Object>();
    }
}
