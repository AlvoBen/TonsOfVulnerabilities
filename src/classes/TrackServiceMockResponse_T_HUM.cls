/**************************************************************************** 
Apex Class Name  : TrackServiceMockResponse_T_HUM 
Version          : 1.0  
Created Date     : July 04, 2017
Function         : Mock Response Class for Track service
Test Class       : TrackService_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Lalit Jain		        27449             07/04/2017                    Original Version
***************************************************************************************************/
@isTest
global class TrackServiceMockResponse_T_HUM implements HttpCalloutMock 
{
	/**
	* Interface method to form the mock response
	*
	* @param    req      Request from test class
	* @return   res		 Mock response
	*/
    global HTTPResponse respond(HTTPRequest req) 
	{        
        // Create a mock response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/json');
        String strjson='';        
        String sBodyContect = req.getBody(); 
        if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('TrackService_WriteTrack').End_Point_URL__c)
        {
        	strjson = '{"WriteTrackResponse": {"Status": "Success","RecipientTrackingId": "10392266761","SourceTrackingId": "10392266756","CaseId": "259853502906","MilestoneId": "876"}}';  
        }
        res.setBody(strjson);
        res.setStatusCode(200);
        return res;        
    }
}