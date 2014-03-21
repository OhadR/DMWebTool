var backend_url = APP_BACKEND_URL + '/getAllSchemasList';

function getAllSchemaList()
{
    getAuthenticationDataFromUI();

    $.ajax({
        url : backend_url,
        type: 'GET',

        data:
        {
            tfHost: tfHost.value,
            tfPort: tfPort.value,
            tfTenant: tfTenant.value,
            tfOpenAmHost: tfOpenAmHost.value,
            tfOpenAmPort: tfOpenAmPort.value
        },

        success: function(response)
        {
            populateRawSchemaListResult(response);
        }
    });
}


function populateRawSchemaListResult(response)
{
//    alert('got response ' + tzResponse);

    if(response.startsWith('ERROR'))
    {
        var res = response.split(DELIMITER);
        alert(ERROR + ": " + res[1]);
    }
    else
    {
        var allSchemasResultDiv = $('#allSchemasResult');
        allSchemasResultDiv.val( response );
    }
}
