/*
 * Copyright 2002-2005 the original author or authors.
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

package info.jtrac.wicket;

import info.jtrac.Jtrac;
import info.jtrac.acegi.JtracCasProxyTicketValidator;
import info.jtrac.domain.Space;
import info.jtrac.domain.User;
import info.jtrac.util.WebUtils;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.resource.loader.IStringResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Locale;

//import info.jtrac.wicket.yui.TestPage;
//import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;

/**
 * main wicket application for jtrac
 * holds singleton service layer instance pulled from spring
 */
public class JtracApplication extends WebApplication {

    private final static Logger logger = LoggerFactory.getLogger(JtracApplication.class);

    private Jtrac jtrac;
    private ApplicationContext applicationContext;
    private JtracCasProxyTicketValidator jtracCasProxyTicketValidator;

    public Jtrac getJtrac() {
        return jtrac;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // used only by CasLoginPage
    public String getCasLoginUrl() {
        if (jtracCasProxyTicketValidator == null) {
            return null;
        }
        return jtracCasProxyTicketValidator.getLoginUrl();
    }

    // used only by logout link in HeaderPanel
    public String getCasLogoutUrl() {
        if (jtracCasProxyTicketValidator == null) {
            return null;
        }
        return jtracCasProxyTicketValidator.getLogoutUrl();
    }

    @Override
    public void init() {

        super.init();

        // get hold of spring managed service layer (see BasePage, BasePanel etc for how it is used)
        ServletContext sc = getServletContext();
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(sc);
        jtrac = (Jtrac) applicationContext.getBean("jtrac");

        // check if acegi-cas authentication is being used, get reference to object to be used
        // by wicket authentication to redirect to right pages for login / logout        
        try {
            jtracCasProxyTicketValidator = (JtracCasProxyTicketValidator) applicationContext.getBean("casProxyTicketValidator");
            logger.info("casProxyTicketValidator retrieved from application context: " + jtracCasProxyTicketValidator);
        } catch (NoSuchBeanDefinitionException nsbde) {
            logger.info("casProxyTicketValidator not found in application context, CAS single-sign-on is not being used");
        }

        // delegate wicket i18n support to spring i18n
        getResourceSettings().getStringResourceLoaders().add(new IStringResourceLoader() {
            @Override
            public String loadStringResource(Class<?> aClass, String s, Locale locale, String s1, String s2) {
                return null;
            }

            @Override
            public String loadStringResource(Component component, String s, Locale locale, String s1, String s2) {
                return null;
            }

            public String loadStringResource(Class clazz, String key, Locale locale, String style) {
                try {
                    return applicationContext.getMessage(key, null, locale);
                } catch (Exception e) {
                    // have to return null so that wicket can try to resolve again
                    // e.g. without prefixing component id etc.
                    if (logger.isDebugEnabled()) {
                        logger.debug("i18n failed for key: '" + key + "', Class: "
                                + clazz + ", Style: " + style + ", Exception: " + e);
                    }
                    return null;
                }
            }

            public String loadStringResource(Component component, String key) {
                Class clazz = component == null ? null : component.getClass();
                Locale locale = component == null ? Session.get().getLocale() : component.getLocale();
                return loadStringResource(clazz, key, locale, null);
            }
        });

        getSecuritySettings().setAuthorizationStrategy(new IAuthorizationStrategy() {
            public boolean isActionAuthorized(Component c, Action a) {
                return true;
            }

            @Override
            public boolean isResourceAuthorized(IResource iResource, PageParameters pageParameters) {
                return true;
            }

            public boolean isInstantiationAuthorized(Class clazz) {
                if (BasePage.class.isAssignableFrom(clazz)) {
                    if (((JtracSession) Session.get()).isAuthenticated()) {
                        return true;
                    }
                    if (jtracCasProxyTicketValidator != null) {
                        // attempt CAS authentication ==========================
                        logger.debug("checking if context contains CAS authentication");
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.isAuthenticated()) {
                            logger.debug("security context contains CAS authentication, initializing session");
                            ((JtracSession) Session.get()).setUser((User) authentication.getPrincipal());
                            return true;
                        }
                    }
                    // attempt remember-me auto login ==========================
                    if (attemptRememberMeAutoLogin()) {
                        return true;
                    }
                    // attempt guest access if there are "public" spaces =======
                    List<Space> spaces = getJtrac().findSpacesWhereGuestAllowed();
                    if (spaces.size() > 0) {
                        logger.debug(spaces.size() + " public space(s) available, initializing guest user");
                        User guestUser = new User();
                        guestUser.setLoginName("guest");
                        guestUser.setName("Guest");
                        guestUser.addSpaceWithRole(null, "ROLE_GUEST");
                        for (Space space : spaces) {
                            guestUser.addSpaceWithRole(space, "ROLE_GUEST");
                        }
                        ((JtracSession) Session.get()).setUser(guestUser);
                        // and proceed
                        return true;
                    }
                    // not authenticated, go to login page
                    logger.debug("not authenticated, forcing login, page requested was " + clazz.getName());
                    if (jtracCasProxyTicketValidator != null) {
                        /*
                        String serviceUrl = jtracCasProxyTicketValidator.getServiceProperties().getService();
                        String loginUrl = jtracCasProxyTicketValidator.getLoginUrl();
                        logger.debug("cas authentication: service URL: " + serviceUrl);
                        String redirectUrl = loginUrl + "?service=" + serviceUrl;
                        logger.debug("attempting to redirect to: " + redirectUrl);
                         */
                        String redirectUrl = "nothing";
                        throw new RestartResponseAtInterceptPageException(new RedirectPage(redirectUrl));
                    } else {
                        //throw new RestartResponseAtInterceptPageException(LoginPage.class);
                    }
                }
                return true;
            }
        });

        // friendly urls for selected pages
        if (jtracCasProxyTicketValidator != null) {
            // mountPage("/login", CasLoginPage.class);
        } else {
            // mountPage("/login", LoginPage.class);
        }
        // mountPage("/logout", LogoutPage.class);
        //mountPage("/svn", SvnStatsPage.class);
        //mountPage("/test", TestPage.class);
        //mountPage("/casError", CasLoginErrorPage.class);
        // bookmarkable url for viewing items
        // mount(new IndexedParamUrlCodingStrategy("/item", ItemViewPage.class));
    }

    @Override
    public Session newSession(Request request, Response response) {
        return new JtracSession(JtracApplication.this, request);
    }

    public Class getHomePage() {
        //return DashboardPage.class;
        return BasePage.class;
    }

    public User authenticate(String loginName, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginName, password);
        AuthenticationManager am = (AuthenticationManager) applicationContext.getBean("authenticationManager");
        try {
            Authentication authentication = am.authenticate(token);
            return (User) authentication.getPrincipal();
        } catch (AuthenticationException ae) {
            logger.debug("acegi authentication failed: " + ae);
            return null;
        }
    }

    private boolean attemptRememberMeAutoLogin() {
        logger.debug("checking cookies for remember-me auto login");
        List<Cookie> cookies = ((WebRequest) RequestCycle.get().getRequest()).getCookies();
        if (cookies == null) {
            logger.debug("no cookies found");
            return false;
        }
        for (Cookie c : cookies) {
            if (logger.isDebugEnabled()) {
                logger.debug("examining cookie: " + WebUtils.getDebugStringForCookie(c));
            }
            if (!c.getName().equals("jtrac")) {
                continue;
            }
            String value = c.getValue();
            logger.debug("found jtrac cookie: " + value);
            if (value == null) {
                continue;
            }
            int index = value.indexOf(':');
            if (index == -1) {
                continue;
            }
            String loginName = value.substring(0, index);
            String encodedPassword = value.substring(index + 1);
            logger.debug("valid cookie, attempting authentication");
            User user = (User) getJtrac().loadUserByUsername(loginName);
            if (encodedPassword.equals(user.getPassword())) {
                ((JtracSession) Session.get()).setUser(user);
                logger.debug("remember me login success");
                return true;
            }
        }
        // no valid cookies were found
        return false;
    }

}
