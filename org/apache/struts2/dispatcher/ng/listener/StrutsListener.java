/*
 * $Id$
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
package org.apache.struts2.dispatcher.ng.listener;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.ng.InitOperations;
import org.apache.struts2.dispatcher.ng.PrepareOperations;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Servlet listener for Struts.  The preferred way to use Struts is as a filter via the
 * {@link org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter} and its variants.  This servlet dispatcher
 * is only for those that really know what they are doing as it may not support every feature of Struts, particularly
 * static resource serving.
 */
public class StrutsListener implements ServletContextListener {
    private PrepareOperations prepare;

    public void contextInitialized(ServletContextEvent sce) {
       InitOperations init = new InitOperations();
        try {
            ListenerHostConfig config = new ListenerHostConfig(sce.getServletContext());
            init.initLogging(config);
            Dispatcher dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(config.getServletContext(), dispatcher);
        } finally {
            init.cleanup();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        prepare.cleanupDispatcher();
    }
}
