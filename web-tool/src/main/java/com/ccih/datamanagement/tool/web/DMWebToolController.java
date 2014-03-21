package com.ccih.datamanagement.tool.web;

import com.ccih.datamanagement.tool.backend.DMWebToolBackend;
import com.ccih.datamanagement.tool.backend.Response;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created with IntelliJ IDEA.
 * User: ohadre
 * Date: 16/03/14
 */
@Controller
public class DMWebToolController
{
    private static Logger log = Logger.getLogger(DMWebToolController.class);

    private static final String TOMCAT_HOST = "tfHost";
    private static final String TOMCAT_PORT = "tfPort";
    private static final String TENANT_NAME = "tfTenant";
    private static final String AUTHENTICATION_HOST = "tfOpenAmHost";
    private static final String AUTHENTICATION_PORT = "tfOpenAmPort";
    private static final String DELIMITER = "|";


    @Autowired
    DMWebToolBackend toolBackend;



    @RequestMapping("/connect")
    protected void onConnect(@RequestParam(TOMCAT_HOST) String tomcatHost,
                             @RequestParam(TOMCAT_PORT ) String tomcatPort,
                             @RequestParam(TENANT_NAME) String tenantName,
                             @RequestParam(AUTHENTICATION_HOST) String authHost,
                             @RequestParam(AUTHENTICATION_PORT) String authPort,
                             HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        response.setContentType("text/plain");

//        log.info("timezone for tomcatHost " + tomcatHost + " tomcatPort: " + tomcatPort);

        Response resp = toolBackend.authenticate(tomcatHost,
                tomcatPort,
                tenantName,
                authHost, authPort);

        if(resp.status.equals(DMWebToolBackend.OK))
        {
            resp = toolBackend.ping();
        }

        response.getWriter().append(resp.status + DELIMITER + resp.value);
    }

    @RequestMapping("/getAllSchemasList")
    protected void onGetAllSchemasList(
            @RequestParam(TOMCAT_HOST) String tomcatHost,
            @RequestParam(TOMCAT_PORT ) String tomcatPort,
            @RequestParam(TENANT_NAME) String tenantName,
            @RequestParam(AUTHENTICATION_HOST) String authHost,
            @RequestParam(AUTHENTICATION_PORT) String authPort,
            HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        log.info("onGetAllSchemasList() started");

        //ohad : re-authentication (coz we are stateless here, serving requests from many web-UIs):
        Response resp = toolBackend.authenticate(tomcatHost,
                tomcatPort,
                tenantName,
                authHost,
                authPort);

//        boolean authenticationSucceeded = SecurityContextUtil.resetSecurityContext();
        if( !resp.status.equals(DMWebToolBackend.OK))
        {
            log.error("onGetAllSchemasList failed: " + resp.value);
            response.getWriter()
                    .append(DMWebToolBackend.ERROR + DELIMITER + resp.value);
        }
        else
        {
            //todo
            resp = toolBackend.getAllSchemasList();
            if(resp.status.equals(DMWebToolBackend.OK))
            {
                response.getWriter()
                        .append(resp.value);
            }
            else
            {
                response.getWriter()
                        .append(DMWebToolBackend.ERROR + DELIMITER + resp.value);
            }
        }
    }


}
