var APP_BACKEND_URL = '/tool';
var DELIMITER = '|';

var tfHost;
var tfPort;
var tfTenant;
var tfOpenAmHost;
var tfOpenAmPort;


function getAuthenticationDataFromUI()
{
    tfHost = document.getElementById('tomcat_host');
    tfPort = document.getElementById('tomcat_port');
    tfTenant = document.getElementById('tenant');
    tfOpenAmHost = document.getElementById('authentication_host');
    tfOpenAmPort = document.getElementById('authentication_port');
}


if (typeof String.prototype.startsWith != 'function')
{
    // see below for better implementation!
    String.prototype.startsWith = function (str){
        return this.indexOf(str) == 0;
    };
}
