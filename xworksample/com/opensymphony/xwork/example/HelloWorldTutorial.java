package com.opensymphony.xwork.example;
 
 import java.util.LinkedHashMap;
 
 import com.opensymphony.xwork2.ActionProxy;
 import com.opensymphony.xwork2.ActionProxyFactory;
 import com.opensymphony.xwork2.config.Configuration;
 import com.opensymphony.xwork2.config.ConfigurationManager;
 import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
 
 /**
  * 
  * @author tm_jee
  * @version $Date$ $Id$
  */
 public class HelloWorldTutorial {
 
 	public static void main(String[] args) throws Exception {
 
 		ConfigurationManager confManager = new ConfigurationManager();
 		confManager.addConfigurationProvider(
 				new XmlConfigurationProvider(
 						"xwork.xml", 
 						true));
         
         Configuration conf = confManager.getConfiguration();
 		ActionProxyFactory actionProxyFactory = conf.getContainer().getInstance(ActionProxyFactory.class);
 		ActionProxy actionProxy = actionProxyFactory.createActionProxy(
 				"/helloWorld", "helloWorld", new LinkedHashMap());
 		
 		
 		actionProxy.execute();
 	}
 }