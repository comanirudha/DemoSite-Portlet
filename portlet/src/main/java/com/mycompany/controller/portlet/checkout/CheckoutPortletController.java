package com.mycompany.controller.portlet.checkout;

import org.apache.commons.collections.CollectionUtils;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.service.type.PaymentInfoType;
import org.broadleafcommerce.core.pricing.service.exception.PricingException;
import org.broadleafcommerce.core.web.checkout.model.BillingInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderInfoForm;
import org.broadleafcommerce.core.web.checkout.model.OrderMultishipOptionForm;
import org.broadleafcommerce.core.web.checkout.model.ShippingInfoForm;
import org.broadleafcommerce.core.web.controller.checkout.BroadleafCheckoutController;
import org.broadleafcommerce.core.web.order.CartState;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.PortletRequestDataBinder;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.util.PortalUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import java.beans.PropertyEditorSupport;
import java.util.List;

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

/**
 * 
 * @author Phillip Verheyden
 */
@Controller
@RequestMapping("VIEW")
public class CheckoutPortletController extends BroadleafCheckoutController {

    @RenderMapping
    public String checkout(RenderRequest request, RenderResponse response, Model model,
            @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingForm,
            @ModelAttribute("billingInfoForm") BillingInfoForm billingForm) {
        prepopulateCheckoutForms(CartState.getCart(), orderInfoForm, shippingForm, billingForm);
        return super.checkout(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, null);
    }

    @ActionMapping(value = "savedetails")
    public void saveGlobalOrderDetails(ActionRequest request, Model model,
            @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm, BindingResult result) throws ServiceException {
        super.saveGlobalOrderDetails(PortalUtil.getHttpServletRequest(request), model, orderInfoForm, result);
    }

    @RenderMapping(params = "singleship")
    public String convertToSingleship(RenderRequest request, RenderResponse response, Model model) throws PricingException {
        return super.convertToSingleship(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model);
    }

    @ActionMapping(value = "singleship")
    public void saveSingleShip(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm,
            @ModelAttribute("billingInfoForm") BillingInfoForm billingForm,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingForm,
            BindingResult result) throws PricingException, ServiceException {
        prepopulateOrderInfoForm(CartState.getCart(), orderInfoForm);
        super.saveSingleShip(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, shippingForm, result);
    }

    @RenderMapping(params = "multiship")
    public String showMultiship(RenderRequest request, RenderResponse response, Model model,
            @ModelAttribute("orderMultishipOptionForm") OrderMultishipOptionForm orderMultishipOptionForm,
            BindingResult result) throws PricingException {
        return super.showMultiship(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model);
    }

    @ActionMapping(value = "multiship")
    public void saveMultiship(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("orderMultishipOptionForm") OrderMultishipOptionForm orderMultishipOptionForm,
            BindingResult result) throws PricingException, ServiceException {
        super.saveMultiship(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, orderMultishipOptionForm, result);
    }

    @RenderMapping(params = "add-address")
    public String showMultishipAddAddress(RenderRequest request, RenderResponse response, Model model,
            @ModelAttribute("addressForm") ShippingInfoForm addressForm, BindingResult result) {
        return super.showMultishipAddAddress(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model);
    }

    @ActionMapping(value = "add-address")
    public void saveMultishipAddAddress(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("addressForm") ShippingInfoForm addressForm, BindingResult result) throws ServiceException {
        super.saveMultishipAddAddress(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, addressForm, result);
    }

    @ActionMapping(value = "complete")
    public void completeSecureCreditCardCheckout(ActionRequest request, ActionResponse response, Model model,
            @ModelAttribute("orderInfoForm") OrderInfoForm orderInfoForm,
            @ModelAttribute("shippingInfoForm") ShippingInfoForm shippingForm,
            @ModelAttribute("billingInfoForm") BillingInfoForm billingForm,
            BindingResult result) throws CheckoutException, PricingException, ServiceException {
        prepopulateCheckoutForms(CartState.getCart(), null, shippingForm, billingForm);
        super.completeSecureCreditCardCheckout(PortalUtil.getHttpServletRequest(request), PortalUtil.getHttpServletResponse(response), model, billingForm, result);
    }

    protected void prepopulateOrderInfoForm(Order cart, OrderInfoForm orderInfoForm) {
        if (orderInfoForm != null) {
            orderInfoForm.setEmailAddress(cart.getEmailAddress());
        }
    }

    protected void prepopulateCheckoutForms(Order cart, OrderInfoForm orderInfoForm, ShippingInfoForm shippingForm,
            BillingInfoForm billingForm) {
        List<FulfillmentGroup> groups = cart.getFulfillmentGroups();

        prepopulateOrderInfoForm(cart, orderInfoForm);

        if (CollectionUtils.isNotEmpty(groups) && groups.get(0).getFulfillmentOption() != null) {
            //if the cart has already has fulfillment information
            shippingForm.setAddress(groups.get(0).getAddress());
            shippingForm.setFulfillmentOption(groups.get(0).getFulfillmentOption());
            shippingForm.setFulfillmentOptionId(groups.get(0).getFulfillmentOption().getId());
        } else {
            //check for a default address for the customer
            CustomerAddress defaultAddress = customerAddressService.findDefaultCustomerAddress(CustomerState.getCustomer().getId());
            if (defaultAddress != null) {
                shippingForm.setAddress(defaultAddress.getAddress());
                shippingForm.setAddressName(defaultAddress.getAddressName());
            }
        }

        if (cart.getPaymentInfos() != null) {
            for (PaymentInfo paymentInfo : cart.getPaymentInfos()) {
                if (PaymentInfoType.CREDIT_CARD.equals(paymentInfo.getType())) {
                    billingForm.setAddress(paymentInfo.getAddress());
                }
            }
        }
    }

    @InitBinder
    protected void initBinder(PortletRequest request, PortletRequestDataBinder binder) throws Exception {
        binder.registerCustomEditor(State.class, "address.state", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                State state = stateService.findStateByAbbreviation(text);
                setValue(state);
            }
        });

        binder.registerCustomEditor(Country.class, "address.country", new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {
                Country country = countryService.findCountryByAbbreviation(text);
                setValue(country);
            }
        });
    }

}
