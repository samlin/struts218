/*
 * $Id: TemplatePageFilter.java 651946 2008-04-27 13:41:38Z apetrelli $
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.struts2.sitemesh;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.dispatcher.Dispatcher;

import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.filter.PageFilter;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;

/**
 *  An abstract template page filter that sets up the proper contexts for
 *  template processing.
 *
 */
public abstract class TemplatePageFilter extends PageFilter {

    private FilterConfig filterConfig;
    
    private static String customEncoding;
    
    @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
    public static void setCustomEncoding(String enc) {
        customEncoding = enc;
    }

    public void init(FilterConfig filterConfig) {
        super.init(filterConfig);
        this.filterConfig = filterConfig;
    }

    /**
     *  Applies the decorator, using the relevent contexts
     *
     * @param page The page
     * @param decorator The decorator
     * @param req The servlet request
     * @param res The servlet response
     * @param servletContext The servlet context
     * @param ctx The action context for this request, populated with the server state
     */
    protected abstract void applyDecorator(Page page, Decorator decorator,
                                  HttpServletRequest req, HttpServletResponse res,
                                  ServletContext servletContext, ActionContext ctx)
            throws ServletException, IOException;

    /**
     *  Applies the decorator, creating the relevent contexts and delegating to
     *  the extended applyDecorator().
     *
     * @param page The page
     * @param decorator The decorator
     * @param req The servlet request
     * @param res The servlet response
     */
    protected void applyDecorator(Page page, Decorator decorator,
                                  HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {

        ServletContext servletContext = filterConfig.getServletContext();
        ActionContext ctx = ServletActionContext.getActionContext(req);
        if (ctx == null) {
            // ok, one isn't associated with the request, so let's create one using the current Dispatcher
            ValueStack vs = Dispatcher.getInstance().getContainer().getInstance(ValueStackFactory.class).createValueStack();
            vs.getContext().putAll(Dispatcher.getInstance().createContextMap(req, res, null, servletContext));
            ctx = new ActionContext(vs.getContext());
            if (ctx.getActionInvocation() == null) {
                // put in a dummy ActionSupport so basic functionality still works
                ActionSupport action = new ActionSupport();
                vs.push(action);
                ctx.setActionInvocation(new DummyActionInvocation(action));
            }
        }

        // delegate to the actual page decorator
        applyDecorator(page, decorator, req, res, servletContext, ctx);
    }


    /**
     *  Gets the L18N encoding of the system.  The default is UTF-8.
     */
    protected String getEncoding() {
        String encoding = customEncoding;
        if (encoding == null) {
            encoding = System.getProperty("file.encoding");
        }
        if (encoding == null) {
            encoding = "UTF-8";
        }
        return encoding;
    }

    static class DummyActionInvocation implements ActionInvocation {

        private static final long serialVersionUID = -4808072199157363028L;

        ActionSupport action;

        public DummyActionInvocation(ActionSupport action) {
            this.action = action;
        }

        public Object getAction() {
            return action;
        }

        public boolean isExecuted() {
            return false;
        }

        public ActionContext getInvocationContext() {
            return null;
        }

        public ActionProxy getProxy() {
            return null;
        }

        public Result getResult() throws Exception {
            return null;
        }

        public String getResultCode() {
            return null;
        }

        public void setResultCode(String resultCode) {
        }

        public ValueStack getStack() {
            return null;
        }

        public void addPreResultListener(PreResultListener listener) {
        }

        public String invoke() throws Exception {
            return null;
        }

        public String invokeActionOnly() throws Exception {
            return null;
        }

        public void setActionEventListener(ActionEventListener listener) {
        }

        public void init(ActionProxy proxy) {
        }
    }
}
