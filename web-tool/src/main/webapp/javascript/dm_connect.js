var backend_connect_url = '/tool/connect';


function connect()
{
    getAuthenticationDataFromUI();


//    alert('trying to connect...' + tfHost.value + ":" + tomcat_port.value);

    $.ajax({
        url : backend_connect_url,
        type: 'GET',
//        data: 'tfHost=aaa',
		data:
		{
            tfHost: tfHost.value,
            tfPort: tfPort.value,
            tfTenant: tfTenant.value,
            tfOpenAmHost: tfOpenAmHost.value,
            tfOpenAmPort: tfOpenAmPort.value
        },
//        dataType: "json",
        success: function(response)
        {
            populateConnectionResult(response);
        }
    });
}

function populateConnectionResult(response)
{
    if(response.startsWith(ERROR))
    {
        var res = response.split(DELIMITER);
        alert(ERROR + ": " + res[1]);
    }
    else
    {
        //TODO: status div 'connected'?
        alert(res[1]);
    }
}
