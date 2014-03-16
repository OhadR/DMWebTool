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
    alert('got response ' + response);
}
