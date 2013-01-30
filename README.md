## Heat Clinic in Portlets
This is an example of running the Heat Clinic application in a Portlet environment while still using Spring MVC. Some of the included code is specific to the Liferay environment (like ActionURLProcessor) but with minimal effort you should theoretically be able to run this in any Portal (WebSphere, Pluto, etc). If you are unexperienced with using Spring MVC in a portlet environment, I _highly_ recommend you read the Javadocs for [Spring's DispatcherPortler](http://static.springsource.org/spring/docs/3.1.x/javadoc-api/index.html?org/springframework/web/portlet/DispatcherPortlet.html). This does a good job of explaining some of the differences and idiosyncrisies between the Portlet and Servlet environment.

This demo provides the following functionality:
1. Some example _frontend_ portlets (cart, catalog, checkout (WIP); no admin portlet)
2. Thymeleaf Integration
3. Spring Security integration (via interceptors; more on this in a second)
4. Some Liferay-specific code (see ActionURLProcessor)
5. Full integration with Broadleaf - Broadleaf jars are included in the application

## Environment Setup
You will need to have a few properties defined