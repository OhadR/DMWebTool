package com.ccih.datamanagement.tool;


import com.ccih.base.service.AuthenticationService;
import com.ccih.base.service.AuthenticationServiceImpl;
import com.ccih.base.service.AuthenticationServiceMock;
import com.ccih.base.service.RemotingUtils;
import com.ccih.common.ui.UIToolHelper;
import com.ccih.common.util.PlatformException;
import com.ccih.common.util.security.SecurityContextUtil;
import com.ccih.datamanagement.client.RawSchemaClient;
import com.ccih.datamanagement.client.SchemaClientFactory;
import com.ccih.datamanagement.client.remoting.DataManagementRemoteClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ohadre
 * Date: 11/03/14
 * Time: 14:43
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class DMWebTool
{
    private static Logger log = Logger.getLogger(DMWebTool.class);

    private static final String TOMCAT_HOST = "tfHost";
    private static final String TOMCAT_PORT = "tfPort";
    private static final String TENANT_NAME = "tfTenant";
    private static final String AUTHENTICATION_HOST = "tfOpenAmHost";
    private static final String AUTHENTICATION_PORT = "tfOpenAmPort";
    private static final String DELIMITER = "|";
    private static final String OK = "OK";
    private static final String ERROR = "ERROR";

    @Autowired
    AuthenticationService authenticationService;


    private String authenticate(String tomcatHost,
                                String tomcatPort,
                                String tenantName,
                                String authHost,
                                String authPort)
    {
        if (tomcatHost.equals("")|| tomcatPort.equals(""))
        {
            log.error("please insert both tomcat's IP and port");
            return ERROR + DELIMITER + "please insert both tomcat's IP and port";
        }

//  ohad (comment out):      System.setProperty(BaseConstants.TENANT_NAME_TOMCAT_PARAM, tenantName);

        //Overwrite AuthenticationServiceImpl attribute in util-beans with those from the UI.
        AuthenticationServiceImpl.setOpenAMHost(authHost);
        AuthenticationServiceImpl.setOpenAMPort(authPort);
        AuthenticationServiceImpl.setServiceUserRealm(tenantName);

/*
        //Overwrite AuthenticationServiceMock attribute in util-beans with those from the UI.
        AuthenticationServiceMock.setOpenAMHost(authHost);
        AuthenticationServiceMock.setOpenAMPort(authPort);
        AuthenticationServiceMock.setServiceUserRealm(tenantName);
*/


        try {
            //Authenticate (Get Service Token)
            boolean authenticationSucceeded = SecurityContextUtil.resetSecurityContext();
            if (! authenticationSucceeded){
                String errorMsg = MessageFormat.format(
                        "Please verify that OpenAM host and port are correct. Follow this check list: " +
                                "{3}1) You have ping to {0}.   " +
                                "{3}2) You can do telnet {0} {1}.   " +
                                "{3}3) Copy the next link to your browser an verify you get a token.id : http://{0}/opensso/identity/authenticate?username=serviceuser&password=qwe123%21%40%23&uri=realm={2}.     " +
                                "{3}4) If all you still can't find the problem, please run again the Data Management Tool from command line, try to authenticate, copy the log, and send to zachb@nice.com.",
                        authHost, authPort, tenantName,
                        System.getProperty("line.separator"));
                log.error(errorMsg);
                return errorMsg;
            }

            log.info("authentication Succeeded!");

            //Overwrite RemotingUtils attribute in util-beans with those from the UI.
            RemotingUtils.setRemoteHost("http://" + tomcatHost + ":" + tomcatPort);

            UIToolHelper.saveProperties();

            return OK;
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            return e1.getMessage() + "\n" + e1.getStackTrace()[0];
        }
    }

    private void ping() throws PlatformException
    {
        DataManagementRemoteClient client = new DataManagementRemoteClient();
        client.pingService();
    }


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

        String authResult = authenticate(tomcatHost,
                tomcatPort,
                tenantName,
                authHost, authPort);

        if(authResult.equals(OK))
        {
            ping();
        }

        response.getWriter().append(authResult);
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
        RawSchemaClient client = createClient();
        List<String> schemaNames=null;

        //ohad : re-authentication (coz we are stateless here, serving requests from many web-UIs):
        String retMsg = authenticate(tomcatHost,
                tomcatPort,
                tenantName,
                authHost,
                authPort);

//        boolean authenticationSucceeded = SecurityContextUtil.resetSecurityContext();
        if( !retMsg.equals(OK))
        {
            log.error("onGetAllSchemasList failed: " + retMsg);
            response.getWriter()
                    .append(ERROR + DELIMITER + retMsg);
        }

        try
        {
            schemaNames = client.getSchemasList();

            StringBuffer retVal = new StringBuffer();

            for( String entityName : schemaNames )
            {
                retVal.append(entityName + "\n");
            }
            log.debug("onGetAllSchemasList() response: " + retVal);
            response.getWriter()
                    .append(retVal);
        }
        catch (PlatformException platEx)
        {
            log.error("could not get schemas list", platEx);
            response.getWriter()
                    .append(ERROR + DELIMITER + platEx.getMessage());
        }
    }

    private RawSchemaClient createClient(){
        RawSchemaClient client = createSchemaFactory().createRawClient();
        return client;

    }

    protected SchemaClientFactory createSchemaFactory(){
        SchemaClientFactory factory = new SchemaClientFactory();
        return factory;
    }




}
