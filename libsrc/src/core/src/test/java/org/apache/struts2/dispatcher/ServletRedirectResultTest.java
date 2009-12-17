/*
 * $Id: ServletRedirectResultTest.java 755605 2009-03-18 14:36:33Z musachy $
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

package org.apache.struts2.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.io.StringWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.*;

import ognl.Ognl;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.StrutsTestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.config.StrutsXmlConfigurationProvider;
import org.springframework.mock.web.MockServletContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;

import com.mockobjects.dynamic.C;
import com.mockobjects.dynamic.Mock;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.entities.ResultConfig;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStackFactory;


/**
 */
public class ServletRedirectResultTest extends StrutsTestCase implements StrutsStatics {

    protected ServletRedirectResult view;
    private Mock requestMock;
    private Mock responseMock;
    protected ActionInvocation ai;


    public void testAbsoluteRedirect() {
        view.setLocation("/bar/foo.jsp");
        responseMock.expectAndReturn("encodeRedirectURL", "/context/bar/foo.jsp", "/context/bar/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/bar/foo.jsp")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testAbsoluteRedirect303() {
        view.setLocation("/bar/foo.jsp");
        view.setStatusCode(303);
        responseMock.expectAndReturn("encodeRedirectURL", "/context/bar/foo.jsp", "/context/bar/foo.jsp");
        responseMock.expect("setStatus", C.args(C.eq(SC_SEE_OTHER)));
        responseMock.expect("setHeader", C.args(C.eq("Location"), C.eq("/context/bar/foo.jsp")));
        StringWriter writer = new StringWriter();
        responseMock.matchAndReturn("getWriter", new PrintWriter(writer));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
        assertEquals("/context/bar/foo.jsp", writer.toString());
    }

    public void testPrependServletContextFalse() {
        view.setLocation("/bar/foo.jsp");
        view.setPrependServletContext(false);
        responseMock.expectAndReturn("encodeRedirectURL", "/bar/foo.jsp", "/bar/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("/bar/foo.jsp")));

        try {
            view.execute(ai);
            requestMock.verify();
            responseMock.verify();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    public void testRelativeRedirect() throws Exception {
        view.setLocation("foo.jsp");
        requestMock.expectAndReturn("getParameterMap", new HashMap());
        requestMock.expectAndReturn("getServletPath", "/namespace/some.action");
        requestMock.expectAndReturn("getRequestURI", "/namespace/some.action");
        requestMock.expectAndReturn("getAttribute", C.ANY_ARGS, null);
        responseMock.expectAndReturn("encodeRedirectURL", "/context/namespace/foo.jsp", "/context/namespace/foo.jsp");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/namespace/foo.jsp")));

        view.execute(ai);

        requestMock.verify();
        responseMock.verify();
    }
    
    public void testMultipleParametersRedirect() throws Exception {
        view.setLocation("foo.jsp?foo=bar&amp;baz=jim");
        requestMock.expectAndReturn("getParameterMap", new HashMap());
        requestMock.expectAndReturn("getServletPath", "/namespace/some.action");
        requestMock.expectAndReturn("getRequestURI", "/namespace/some.action");
        requestMock.expectAndReturn("getAttribute", C.ANY_ARGS, null);
        responseMock.expectAndReturn("encodeRedirectURL", "/context/namespace/foo.jsp?foo=bar&amp;baz=jim", "/context/namespace/foo.jsp?foo=bar&baz=jim");
        responseMock.expect("sendRedirect", C.args(C.eq("/context/namespace/foo.jsp?foo=bar&baz=jim")));

        view.execute(ai);

        requestMock.verify();
        responseMock.verify();
    }

    public void testIncludeParameterInResult() throws Exception {

        ResultConfig resultConfig = new ResultConfig.Builder("", "")
            .addParam("namespace", "someNamespace")
            .addParam("encode", "true")
            .addParam("parse", "true")
            .addParam("location", "someLocation")
            .addParam("prependServletContext", "true")
            .addParam("method", "someMethod")
            .addParam("param1", "value 1")
            .addParam("param2", "value 2")
            .addParam("param3", "value 3")
            .build();

        ActionContext context = ActionContext.getContext();
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        context.put(ServletActionContext.HTTP_REQUEST, req);
        context.put(ServletActionContext.HTTP_RESPONSE, res);


        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ServletRedirectResult result = new ServletRedirectResult();
        result.setLocation("/myNamespace/myAction.action");
        result.setParse(false);
        result.setEncode(false);
        result.setPrependServletContext(false);

        IMocksControl control = EasyMock.createControl();
        ActionProxy mockActionProxy = control.createMock(ActionProxy.class);
        ActionInvocation mockInvocation = control.createMock(ActionInvocation.class);
        mockInvocation.getProxy();
        control.andReturn(mockActionProxy);
        mockInvocation.getResultCode();
        control.andReturn("myResult");
        mockActionProxy.getConfig();
        control.andReturn(actionConfig);
        mockInvocation.getInvocationContext();
        control.andReturn(context);

        control.replay();
        result.setActionMapper(container.getInstance(ActionMapper.class));
        result.execute(mockInvocation);
        assertEquals("/myNamespace/myAction.action?param1=value+1&param2=value+2&param3=value+3", res.getRedirectedUrl());

        control.verify();
    }

    protected void setUp() throws Exception {
        super.setUp();
        configurationManager.getConfiguration().
            addPackageConfig("foo", new PackageConfig.Builder("foo").namespace("/namespace").build());

        view = new ServletRedirectResult();
        container.inject(view);

        responseMock = new Mock(HttpServletResponse.class);

        requestMock = new Mock(HttpServletRequest.class);
        requestMock.matchAndReturn("getContextPath", "/context");

         ResultConfig resultConfig = new ResultConfig.Builder("", "").build();

        Map<String, ResultConfig> results=  new HashMap<String, ResultConfig>();
        results.put("myResult", resultConfig);

        ActionConfig actionConfig = new ActionConfig.Builder("", "", "")
                .addResultConfigs(results).build();

        ActionContext ac = new ActionContext(Ognl.createDefaultContext(null));
        ac.put(ServletActionContext.HTTP_REQUEST, requestMock.proxy());
        ac.put(ServletActionContext.HTTP_RESPONSE, responseMock.proxy());
        MockActionInvocation ai = new MockActionInvocation();
        ai.setInvocationContext(ac);
        ai.setResultCode("myResult");
        ActionProxy mockActionProxy = EasyMock.createNiceMock(ActionProxy.class);
        ai.setProxy(mockActionProxy);
        EasyMock.expect(mockActionProxy.getConfig()).andReturn(actionConfig).anyTimes();
        EasyMock.replay(mockActionProxy);
        this.ai = ai;
        ai.setStack(ActionContext.getContext().getValueStack());
    }
}
