package com.ccih.datamanagement.tool.backend;


import com.ccih.base.service.AuthenticationServiceImpl;
import com.ccih.base.service.RemotingUtils;
import com.ccih.common.ui.UIToolHelper;
import com.ccih.common.util.PlatformException;
import com.ccih.common.util.security.SecurityContextUtil;
import com.ccih.datamanagement.client.RawSchemaClient;
import com.ccih.datamanagement.client.SchemaClientFactory;
import com.ccih.datamanagement.client.remoting.DataManagementRemoteClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ohadre
 * Date: 11/03/14
 */
@Controller
public class DMWebToolBackend
{
    private static Logger log = Logger.getLogger(DMWebToolBackend.class);

    public static final String OK = "OK";
    public static final String ERROR = "ERROR";

//    @Autowired
//    AuthenticationService authenticationService;


    public Response authenticate(String tomcatHost,
                                String tomcatPort,
                                String tenantName,
                                String authHost,
                                String authPort)
    {
        Response response = new Response();
        if (tomcatHost.equals("")|| tomcatPort.equals(""))
        {
            log.error("please insert both tomcat's IP and port");
            response.status = ERROR;
            response.value = "please insert both tomcat's IP and port";
            return response;
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

                response.status = ERROR;
                response.value = errorMsg;
                return response;
            }

            log.info("authentication Succeeded!");

            //Overwrite RemotingUtils attribute in util-beans with those from the UI.
            RemotingUtils.setRemoteHost("http://" + tomcatHost + ":" + tomcatPort);

            UIToolHelper.saveProperties();

            response.status = OK;
            return response;
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
            response.status = ERROR;
            response.value = e1.getMessage() + "\n" + e1.getStackTrace()[0];
            return response;
        }
    }

    public Response ping()
    {
        Response response = new Response();

        DataManagementRemoteClient client = new DataManagementRemoteClient();
        try {
            client.pingService();
            response.status = OK;
        }
        catch (PlatformException platEx)
        {
            log.error("could not ping", platEx);
            response.status = ERROR;
            response.value = platEx.getMessage();
        }
        return response;
    }


    public Response getAllSchemasList()
    {
        Response response = new Response();
        RawSchemaClient client = createClient();
        List<String> schemaNames=null;

        try {
            schemaNames = client.getSchemasList();
        }
        catch (PlatformException platEx)
        {
            log.error("could not get schemas list", platEx);
            response.status = ERROR;
            response.value = platEx.getMessage();
            return response;
        }

        StringBuffer retVal = new StringBuffer();

        for( String entityName : schemaNames )
        {
            retVal.append(entityName + "\n");
        }
        log.debug("onGetAllSchemasList() response: " + retVal);

        response.status = OK;
        response.value = retVal.toString();
        return response;
    }





    private RawSchemaClient createClient(){
        RawSchemaClient client = createSchemaFactory().createRawClient();
        return client;

    }

    private SchemaClientFactory createSchemaFactory(){
        SchemaClientFactory factory = new SchemaClientFactory();
        return factory;
    }


}
