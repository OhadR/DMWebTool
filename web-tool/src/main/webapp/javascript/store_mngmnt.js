var backend_store_url = APP_BACKEND_URL + '/getAllSchemasList';


function onInsertFile()
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
            populateInsertFileResult(response);
        }
    });

}

function populateInsertFileResult(response)
{
    if(response.startsWith('ERROR'))
    {
        alert(response);
    }
    else
    {
        var statusDiv = $('#status')
        statusDiv.val( response );
    }
}
