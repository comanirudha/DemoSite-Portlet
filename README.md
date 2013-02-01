# Heat Clinic in Portlets
This is an example of running the Heat Clinic application in a Portlet environment while still using Spring MVC. Some of the included code is specific to the Liferay environment (like ActionURLProcessor) but with minimal effort you should theoretically be able to run this in any Portal (WebSphere, Pluto, etc). If you are unexperienced with using Spring MVC in a portlet environment, I _highly_ recommend you read the Javadocs for [Spring's DispatcherPortler](http://static.springsource.org/spring/docs/3.1.x/javadoc-api/index.html?org/springframework/web/portlet/DispatcherPortlet.html). This does a good job of explaining some of the differences and idiosyncrisies between the Portlet and Servlet environment.

This demo provides the following functionality:

1.	Some example _frontend_ portlets (cart, catalog, checkout (WIP). Currently no admin portlet. Instead, you can just deploy the admin war that gets built and run as a normal web application (pointed to the same database, of course).
2.	Thymeleaf Integration
3.	Spring Security integration (via interceptors; more on this in a second)
4.	Some Liferay-specific code (see ActionURLProcessor)
5.	Full integration with Broadleaf - Broadleaf jars are included in the application

##Spring Security
This project relies on the currently unmaintained Spring Portlet Security extension (GitHub project is https://github.com/SpringSource/spring-security-portlet). While this project has not been updated in quite some time (2 years at the time of this writing), most of the paradigms presented still apply rather nicely.  This extension is currently included in this project wholesale, until a proper pull request can be made to SpringSource for them to bring this module up to date. The changes we have made to the extension are slight such as removing deprecated references and provide a way to map the Portal user's roles onto Spring ones (if defined).

The relevant files to look at when tracing through Spring Security are in [applicationContext-security.xml](portlet/src/main/webapp/WEB-INF/applicationContext-security.xml) and [applicationContext-interceptor.xml](portlet/src/main/webapp/WEB-INF/applicationContext-interceptor.xml). The interceptors are where Spring Security is invoked (check the ordering in applicationContext-interceptor) and ensure that the Portal user is on Spring's SecurityContext.

One other note: the mechanism for Spring Security here relies on a PreAuthenticatedAuthenticationToken/Provider. These are essentially just pass-through mechanisms that trust the fact that the user has already logged in via the Portal (meaning request.getUserPrincipal() is non-null) and goes ahead and logs that user in to the application.

##Filters vs Interceptors
A quick note about these. JSR-286 (Portlet 2.0) provides the Filter functionality to mimic Servlet Filters. There are a few reasons why we decided to use interceptors over the Portlet 2.0 filters:

1.	They serve the same purpose - this is mostly true; technically Filters are invoked prior to hitting Spring's ```DispatcherPortlet```
2.	There is already support for utilizing Spring Security via interceptors that works perfectly fine - albeit with some modification to remove deprecations
3.	Spring does not have any native support for JSR-286 Portlet Filters - there is nothing like ```DelegatingFilterProxy``` offered on the Servlet side so you cannot do dependency injection without creating this functionality yourself (although this is probably not terribly hard to do)


##Broadleaf Version
This currently relies on Broadleaf 2.2.0-SNAPSHOT as this is utilizing a refactoring of some of our Servlet filters that we use in Broadleaf so that the same logic can be reused as interceptors. This is currently located in a [pull request](https://github.com/BroadleafCommerce/BroadleafCommerce/pull/31). You can locally install this version by checking out the ```filter-refactor``` branch and doing a ```mvn install``` there.

##Liferay Version
This is currently tested on Liferay 6.1 CE GA2 and 6.2.0 M3. However, 6.1 GA2 comes with a small caveat.  Because of the bug outlined here: http://issues.liferay.com/browse/LPS-29103, the Portlet context starts _before_ the Spring context (as defined in MergeContextLoaderListener which is analagous to ContextLoaderListener in web.xml). This means that you would be unable to inject any of the Broadleaf dependencies into the Portlet contexts and would lose out on most (if not all) of the benefits of Broadleaf. This has been fully resolved in the 6.2 CE version, and we have heard reports of this already being addressed in the enterprise edition of Liferay.  The fix for 6.1 is also pretty simple. After Liferay autodeploys the Broadleaf application, you can simply edit the web.xml in Tomcat's webapps directory and ensure that the Broadleaf ```MergeContextLoaderListener``` comes before any of the other declared listeners in the web.xml.

#Environment Setup
You will need to have a few properties defined in your local Maven environment for this to install properly on your machine. These are ```liferay.dir``` and ```liferay.tomcat```. You can define both of these properties in your local Maven settings.xml. An example configuration would look like:

```xml
<profiles>
	<profile>
		<id>blc-liferay</id>
		<properties>
			<liferay.dir>/path/to/liferay-portal</liferay.dir>
			<liferay.tomcat>${liferay.dir}/tomcat-7.0.27</liferay.tomcat>
		</properties>
	</profile>
</profiles>
<activeProfiles>
	<activeProfile>blc-liferay</activeProfile>
</activeProfiles>
```

These properties are used for the Liferay Maven plugin defined as the first plugin the ```portlet/pom.xml```.

You can install this just like you would any other Maven application from the root directory:
```bash
mvn clean install
```

Which will build 2 wars: admin/target/admin.war (non-portlet admin) and portlet/target/broadleaf.war which is what you will be deploying onto your Liferay instance. To utilize Maven for this:

```bash
cd portlet
mvn clean install liferay:deploy
```

The ```liferay:deploy``` goal will deploy broadleaf.war to the autodeploy directory that you have set via ```liferay.dir```.

#Tomcat
You will need to ensure that you have set your JNDI database resources properly. Specifically, Broadleaf will look for ```jdbc/web```, ```jdbc/webSecure``` and ```jdbc/webStorage```. We have provided examples of what you would need in Tomcat's conf/context.xml via the [included context.xml](portlet/src/main/webapp/META-INF/context.xml). You should be able to put the contents of this file inside of Tomcat's context.xml. Theoretically this should work by being a context file within the war, but Liferay has been known to modify the contents of this file. To save some headaches it is recommended to declare these as global Tomcat JNDI resources.