/**************************************************************************** 
Apex Class Name  : AORMockHttpResponse_LTNG_T_HUM
Version          : 1.0  
Created Date     : 11/12/2019 
Description      : This class is used as MockHttpResponse for Agent of Record Service.
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
*  Chetan S                                     11/12/2019            Original Version.
*  Pradeep Dani                                 06/05/2020            Code Coverage related changes.
*****************************************************************************************************************/
@isTest
                        
global class AORMockHttpResponse_LTNG_T_HUM implements HttpCalloutMock {
    // Implement this interface method
    global HTTPResponse respond(HTTPRequest req) 
    {        
        // Create a fake response
        HttpResponse res = new HttpResponse();
        res.setHeader('Content-Type', 'application/xml');
        String strBody='';
        
        strBody = '{"ProducerBusinessResultData": {"ProducerBusinessWithGa": {"AbcProductCode": "PDP","AorName": "CO  HUMANA INSURANCE","AorSan": "1064625","BlockOfBusiness": "MA","MemberId": "",'+
            '"MemberName": "EMMETT EFFIE M","PID": "942192927","Platform": "LV","WaName":"Humana","WaSan":"Insurance"}}}';
                             
        res.setBody(strBody);
        res.setStatusCode(200);

        if(req.getBody().contains('Enterprise_ID__test')){
            res.setBody(null);
        }
        else if(req.getBody().contains('faultcode')){
            res.setBody('faultcode');
        }
        else if(req.getBody().contains('faultcodeXXX')){
            res.setStatusCode(401);
        }
        else if(req.getBody().contains('SuccessResponse')){
            res.setBody('{"ProducerBusinessResultData": {"ProducerBusinessWithGa": {"AbcProductCode": "PDP","AorName": "CO  HUMANA INSURANCE","AorSan": "1064625","BlockOfBusiness": "MA","MemberId": "",'+
            '"MemberName": "EMMETT EFFIE M","PID": "942192927","Platform": "LV","WaName":"","WaSan":""}}}');
            res.setStatusCode(200);
        }

        return res;        
    }
}