package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import com.opensymphony.xwork2.util.profiling.UtilTimerStack;

import java.io.Serializable;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;


/**
 * The Default ActionProxy implementation
 *
 * @author Rainer Hermanns
 * @author Revised by <a href="mailto:hu_pengfei@yahoo.com.cn">Henry Hu</a>
 * @author tmjee
 * 
 * @version $Date: 2009-05-08 05:54:47 +0200 (Fr, 08 Mai 2009) $ $Id: DefaultActionProxy.java 1991 2009-05-08 03:54:47Z musachy $
 * @since 2005-8-6
 */
public class DefaultActionProxy implements ActionProxy, Serializable {
	
	private static final long serialVersionUID = 3293074152487468527L;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultActionProxy.class);

    protected Configuration configuration;
    protected ActionConfig config;
    protected ActionInvocation invocation;
    protected UnknownHandlerManager unknownHandlerManager;
    protected String actionName;
    protected String namespace;
    protected String method;
    protected boolean executeResult;
    protected boolean cleanupContext;

    protected ObjectFactory objectFactory;

    protected ActionEventListener actionEventListener;

    /**
     * This constructor is private so the builder methods (create*) should be used to create an DefaultActionProxy.
     * <p/>
     * The reason for the builder methods is so that you can use a subclass to create your own DefaultActionProxy instance
     * (like a RMIActionProxy).
     */
    protected DefaultActionProxy(ActionInvocation inv, String namespace, String actionName, String methodName, boolean executeResult, boolean cleanupContext) {
        
        this.invocation = inv;
		this.cleanupContext = cleanupContext;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Creating an DefaultActionProxy for namespace " + namespace + " and action name " + actionName);
		}

		this.actionName = actionName;
		this.namespace = namespace;
		this.executeResult = executeResult;
        this.method = methodName;
    }
    
    @Inject
    public void setObjectFactory(ObjectFactory factory) {
        this.objectFactory = factory;
    }
    
    @Inject
    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
    
    @Inject
    public void setUnknownHandler(UnknownHandlerManager unknownHandlerManager) {
        this.unknownHandlerManager = unknownHandlerManager;
    }
    
    @Inject(required=false) 
    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }

    public Object getAction() {
        return invocation.getAction();
    }

    public String getActionName() {
        return actionName;
    }

    public ActionConfig getConfig() {
        return config;
    }
    
    public void setExecuteResult(boolean executeResult) {
        this.executeResult = executeResult;
    }

    public boolean getExecuteResult() {
        return executeResult;
    }

    public ActionInvocation getInvocation() {
        return invocation;
    }

    public String getNamespace() {
        return namespace;
    }

    public String execute() throws Exception {
        ActionContext nestedContext = ActionContext.getContext();
        ActionContext.setContext(invocation.getInvocationContext());

        String retCode = null;

        String profileKey = "execute: ";
        try {
        	UtilTimerStack.push(profileKey);
        	
            retCode = invocation.invoke();
        } finally {
            if (cleanupContext) {
                ActionContext.setContext(nestedContext);
            }
            UtilTimerStack.pop(profileKey);
        }

        return retCode;
    }


    public String getMethod() {
        return method;
    }

    private void resolveMethod() {
        // if the method is set to null, use the one from the configuration
        // if the one from the configuration is also null, use "execute"
        if (StringUtils.isEmpty(this.method)) {
            this.method = config.getMethodName();
            if (StringUtils.isEmpty(this.method)) {
                this.method = "execute";
            }
        }
    }

    protected void prepare()  {
        String profileKey = "create DefaultActionProxy: ";
        try {
            UtilTimerStack.push(profileKey);
            config = configuration.getRuntimeConfiguration().getActionConfig(namespace, actionName);
    
            if (config == null && unknownHandlerManager.hasUnknownHandlers()) {
                config = unknownHandlerManager.handleUnknownAction(namespace, actionName);
            }
            if (config == null) {
                String message;
    
                if ((namespace != null) && (namespace.trim().length() > 0)) {
                    message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_PACKAGE_ACTION_EXCEPTION, Locale.getDefault(), new String[]{
                        namespace, actionName
                    });
                } else {
                    message = LocalizedTextUtil.findDefaultText(XWorkMessages.MISSING_ACTION_EXCEPTION, Locale.getDefault(), new String[]{
                        actionName
                    });
                }
                throw new ConfigurationException(message);
            }

            resolveMethod();
            
            if (!config.isAllowedMethod(method)) {
                throw new ConfigurationException("Invalid method: "+method+" for action "+actionName);
            }

            invocation.init(this);

        } finally {
            UtilTimerStack.pop(profileKey);
        }
    }
}
