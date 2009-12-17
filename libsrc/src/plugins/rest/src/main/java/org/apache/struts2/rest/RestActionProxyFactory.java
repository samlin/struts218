/*
 * $Id: RestActionProxyFactory.java 666756 2008-06-11 18:11:00Z hermanns $
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

package org.apache.struts2.rest;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.DefaultActionProxyFactory;

import java.util.Map;


/**
 * Factory that creates the {@link RestActionInvocation}
 */
public class RestActionProxyFactory extends DefaultActionProxyFactory {

    @Override
    public ActionProxy createActionProxy(String namespace, String actionName, String methodName, Map extraContext, boolean executeResult, boolean cleanupContext) {
        ActionInvocation inv = new RestActionInvocation(extraContext, true);
        container.inject(inv);
        return createActionProxy(inv, namespace, actionName, methodName, executeResult, cleanupContext);
    }

}
