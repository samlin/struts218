package com.opensymphony.xwork.example;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;

public class PrintToConsoleResult implements Result {
    /** 
     *  
     */ 
    private static final long serialVersionUID = 2324715560445939880L; 
    private String param ; 
    public String getParam() { 
        return param; 
    } 
    public void setParam(String param) { 
        this.param = param; 
    } 
    public void execute(ActionInvocation invocation) throws Exception {
       String result = TextParseUtil.translateVariables(param, invocation.getStack());   
       System.out.println("${test}PrintToConsoleResultÊä³öµÄ:"+result);   
    }
}