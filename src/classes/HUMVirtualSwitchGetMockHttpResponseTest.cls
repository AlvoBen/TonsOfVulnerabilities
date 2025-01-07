/**************************************************************************** 
Apex Class Name  : HUMVirtualSwitchGetMockHttpResponseTest
Version          : 1.0  
Created Date     : May 27 2015 
Description      : This class is used as MockHttpResponse for Get Virtual Switch in HUMServiceTester
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* Gargey Sharma              23473                  05/27/2015        Original Version  
* Vishal Verma               23473                  06/02/2015        Fixing Code Review Comments for Review # 23473	
*****************************************************************************************************************/
@isTest
                        
global class HUMVirtualSwitchGetMockHttpResponseTest implements HttpCalloutMock {
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) {        
        // Create a fake response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/xml');
        String strXml='';        
        String sBodyContect = req.getBody(); 
        if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('VirtualSwitch_Get').End_Point_URL__c)
        {
        	strXml = '<Result><Status>Success</Status><Message>Virtual config details for GCSGroupService</Message><VirtualConfigDetails>'+
        		 '<Operations>1. GetGroupInfo 2. GetHumanGroupInfo 3. GetGBEInfo 4. GetGBEGroupBenefitInfo 5. GetMasteredGroupBenefit 6. GetMasteredGroup 7. Search </Operations>'+
        		 '<VirtualFlag>Y</VirtualFlag><VirtualURL>http://LOUXDWDEVB0495.humad.com:8313/GroupService.svc</VirtualURL></VirtualConfigDetails>'+
        		 '<VirtualConfigDetails><Operations>1. GroupSearch </Operations><VirtualFlag>N</VirtualFlag><VirtualURL>http://LOUXDWDEVB0495.humad.com:8313/GroupService.svc</VirtualURL>'+
        		 '</VirtualConfigDetails></Result>';  
        }
        else if(req.getEndpoint() == HUM_Webservice_Callout__c.getValues('VirtualSwitch_Update').End_Point_URL__c)
        {
        	 strXml = '<Result><Status>Success</Status><Message><VirtualFlag>Virtual Flag for operation(s) 1. getHandleForUserid'+
                 '2. createRequest 3. login 4. logout  of service CAServiceDesk is updated to Y</VirtualFlag>'+
                 '<VirtualURL>Virtual URL for operation(s) 1. getHandleForUserid 2. createRequest 3. login 4. logout'+  
                 'of service CAServiceDesk is updated to http://LOUXDWDEVB0495.humad.com:8314/CAService</VirtualURL></Message></Result>';  
        }                   
        res.setBody(strXml);
        res.setStatusCode(200);
        return res;        
    }
}