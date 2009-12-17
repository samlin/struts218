/*
 * $Id: RestActionMapperTest.java 680686 2008-07-29 12:53:18Z mrdon $
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

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import junit.framework.TestCase;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.springframework.mock.web.MockHttpServletRequest;

public class RestActionMapperTest extends TestCase {

    private RestActionMapper mapper;
    private ConfigurationManager configManager;
    private Configuration config;
    private MockHttpServletRequest req;

    protected void setUp() throws Exception {
        super.setUp();
        req = new MockHttpServletRequest();
        req.setContextPath("/myapp");
        req.setMethod("GET");

        mapper = new RestActionMapper();

        config = new DefaultConfiguration();
        PackageConfig pkg = new PackageConfig.Builder("myns").namespace("/animals").build();
        PackageConfig pkg2 = new PackageConfig.Builder("my").namespace("/my").build();
        config.addPackageConfig("mvns", pkg);
        config.addPackageConfig("my", pkg2);
        configManager = new ConfigurationManager() {
            public Configuration getConfiguration() {
                return config;
            }
        };
    }

    public void testGetMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog");
        req.setServletPath("/animals/dog");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("index", mapping.getMethod());
    }

    public void testPostMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog");
        req.setServletPath("/animals/dog");
        req.setMethod("POST");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("create", mapping.getMethod());
    }

    public void testDeleteMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("DELETE");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("destroy", mapping.getMethod());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
    }

    public void testPutMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("PUT");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("update", mapping.getMethod());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
    }

    public void testGetIdMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("show", mapping.getMethod());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
    }

    public void testNewMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/new");
        req.setServletPath("/animals/dog/new");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("editNew", mapping.getMethod());
    }

    public void testEditMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido/edit");
        req.setServletPath("/animals/dog/fido/edit");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("edit", mapping.getMethod());
    }

    public void testEditSemicolonMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido;edit");
        req.setServletPath("/animals/dog/fido;edit");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("edit", mapping.getMethod());
    }

    public void testGetJsessionIdSemicolonMapping() throws Exception {
        req.setRequestURI("/myapp/animals/dog/fido;jsessionid=29fefpv23do1g");
        req.setServletPath("/animals/dog/fido");
        req.setMethod("GET");

        ActionMapping mapping = mapper.getMapping(req, configManager);

        assertEquals("/animals", mapping.getNamespace());
        assertEquals("dog", mapping.getName());
        assertEquals("fido", ((String[])mapping.getParams().get("id"))[0]);
        assertEquals("show", mapping.getMethod());
    }

    public void testParseNameAndNamespace() {
        tryUri("/foo/23", "", "foo/23");
        tryUri("/foo/", "", "foo/");
        tryUri("foo", "", "foo");
        tryUri("/", "/", "");
    }
    
    public void testParseNameAndNamespaceWithNamespaces() {
        tryUri("/my/foo/23", "/my", "foo/23");
        tryUri("/my/foo/", "/my", "foo/");
    }
    
    public void testParseNameAndNamespaceWithEdit() {
        tryUri("/my/foo/23;edit", "/my", "foo/23;edit");
    }
    
    private void tryUri(String uri, String expectedNamespace, String expectedName) {
        ActionMapping mapping = new ActionMapping();
        mapper.parseNameAndNamespace(uri, mapping, configManager);
        assertEquals(expectedName, mapping.getName());
        assertEquals(expectedNamespace, mapping.getNamespace());
    }

}
