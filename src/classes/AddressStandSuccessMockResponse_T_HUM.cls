/****************************************************************************************
Apex Class Name  : AddressStandSuccessMockResponse_T_HUM
Version          : 1.0 
Created Date     : 12-05-2020
Function         : Test class to AddressStandardizeList_D_HUM Apex class 
Modification Log
*      Developer               Code Review            Date                 Description
* --------------------------------------------------------------------------------------------------------        
*   Ajay Chakradhar                                 12/05/2020              Original Version     
***********************************************************************************************************/
@istest
global class AddressStandSuccessMockResponse_T_HUM implements HttpCalloutMock 
{
    global HTTPResponse respond(HTTPRequest req) 
    {
        // Creating a fake success response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        res.setBody('{ "StandardizeAddressResponse":{ "SuccessFlag":"Y", "AddressLine1":"9022 SHELBYVILLE RD APT 2", "City":"LOUISVILLE", "StateCode":"KY", "ZipCode":"40222", "StateID":"21", "ZipCodePlus":"5106", "CountyName":"JEFFERSON", "CountyID":"111", "Longitude":"-85.583009", "Latitude":"38.2468857", "Valid":"true", "ChangeIndicator":"Change" } }');
        res.setStatusCode(200);
        return res;
    }
}