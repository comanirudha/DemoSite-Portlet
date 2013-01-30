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

package com.mycompany.filter.security;

import org.broadleafcommerce.common.security.BroadleafExternalAuthenticationUserDetails;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.web.core.CustomerState;
import org.broadleafcommerce.profile.web.core.security.CustomerStateRequestProcessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.extensions.portlet.PortletAuthenticationProcessingInterceptor;
import org.springframework.web.context.request.WebRequest;


/**
 * Processor that attempts to convert the principal setup by {@link PortletAuthenticationProcessingInterceptor} to the BLC
 * Customer and set on the current request. If a Customer cannot be found for the Portal user then this will create a
 * Customer and use that.
 * 
 * @author Phillip Verheyden
 * @see {@link CustomerStateInterceptor}
 */
public class PortletCustomerStateProcessor extends CustomerStateRequestProcessor {
    
    @Override
    public void process(WebRequest request) {
        //This should be instantiated in BroadleafPreAuthenticatedUserDetailsService
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            BroadleafExternalAuthenticationUserDetails user = (BroadleafExternalAuthenticationUserDetails)authentication.getPrincipal();
            Customer customer = customerService.readCustomerByUsername(user.getEmail());
            //no user instantiated for this Portal user yet, do that
            if (customer == null) {
                customer = customerService.createCustomer();
                customer.setEmailAddress(user.getEmail());
                customer.setUsername(customer.getEmailAddress());
                customer.setFirstName(user.getFirstName());
                customer.setLastName(user.getLastName());
                customer = customerService.saveCustomer(customer);
            }
            
            CustomerState.setCustomer(customer);
        } else {
            super.process(request);
        }
    }

}
