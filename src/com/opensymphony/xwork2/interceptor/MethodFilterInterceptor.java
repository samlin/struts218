/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import java.util.Collections;
import java.util.Set;


/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * MethodFilterInterceptor is an abstract <code>Interceptor</code> used as
 * a base class for interceptors that will filter execution based on method 
 * names according to specified included/excluded method lists.
 * 
 * <p/>
 * 
 * Settable parameters are as follows:
 * 
 * <ul>
 * 		<li>excludeMethods - method names to be excluded from interceptor processing</li>
 * 		<li>includeMethods - method names to be included in interceptor processing</li>
 * </ul>
 * 
 * <p/>
 * 
 * <b>NOTE:</b> If method name are available in both includeMethods and 
 * excludeMethods, it will be considered as an included method: 
 * includeMethods takes precedence over excludeMethods.
 * 
 * <p/>
 * 
 * Interceptors that extends this capability include:
 * 
 * <ul>
 *    <li>TokenInterceptor</li>
 *    <li>TokenSessionStoreInterceptor</li>
 *    <li>DefaultWorkflowInterceptor</li>
 *    <li>ValidationInterceptor</li>
 * </ul>
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @author Rainer Hermanns
 * 
 * @see org.apache.struts2.interceptor.TokenInterceptor
 * @see org.apache.struts2.interceptor.TokenSessionStoreInterceptor
 * @see com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor
 * @see com.opensymphony.xwork2.validator.ValidationInterceptor
 * 
 * @version $Date: 2008-06-21 11:29:39 +0200 (Sa, 21 Jun 2008) $ $Id: MethodFilterInterceptor.java 1833 2008-06-21 09:29:39Z rainerh $
 */
public abstract class MethodFilterInterceptor extends AbstractInterceptor {
    protected transient Logger log = LoggerFactory.getLogger(getClass());
    
    protected Set<String> excludeMethods = Collections.emptySet();
    protected Set<String> includeMethods = Collections.emptySet();

    public void setExcludeMethods(String excludeMethods) {
        this.excludeMethods = TextParseUtil.commaDelimitedStringToSet(excludeMethods);
    }
    
    public Set<String> getExcludeMethodsSet() {
    	return excludeMethods;
    }

    public void setIncludeMethods(String includeMethods) {
        this.includeMethods = TextParseUtil.commaDelimitedStringToSet(includeMethods);
    }
    
    public Set<String> getIncludeMethodsSet() {
    	return includeMethods;
    }
//sss f5 �������غ����Ĺ����� struts2
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (applyInterceptor(invocation)) {
            return doIntercept(invocation);
        } 
        return invocation.invoke();
    }

    protected boolean applyInterceptor(ActionInvocation invocation) {
        String method = invocation.getProxy().getMethod();
        // ValidationInterceptor
        boolean applyMethod = MethodFilterInterceptorUtil.applyMethod(excludeMethods, includeMethods, method);
        if (log.isDebugEnabled()) {
        	if (!applyMethod) {
        		log.debug("Skipping Interceptor... Method [" + method + "] found in exclude list.");
        	}
        }
        return applyMethod;
    }
    
    /**
     * Subclasses must override to implement the interceptor logic.
     * 
     * @param invocation the action invocation
     * @return the result of invocation
     * @throws Exception
     */
    protected abstract String doIntercept(ActionInvocation invocation) throws Exception;
    
}
