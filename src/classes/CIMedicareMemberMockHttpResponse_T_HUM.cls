/**************************************************************************** 
Apex Class Name  : CIMedicareMemberMockHttpResponse_T_HUM
Version          : 1.0  
Created Date     : July 07 2017 
Description      : This class is used as MockHttpResponse for CIMedicareMultipleMemberDetailsService
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* Amar Gadewar                        				 07/30/2017        Original Version
*****************************************************************************************************************/
@isTest
global class CIMedicareMemberMockHttpResponse_T_HUM implements HttpCalloutMock  
{
	// Implement this interface method
    global HTTPResponse respond(HTTPRequest req) {        
    
    // Create a fake response
    HttpResponse res = new HttpResponse();
    res.setHeader('Content-Type', 'application/json');
    String strJSON='';        
    String sBodyContent = req.getBody(); 
    if(sBodyContent.contains('123456789012'))
     {
    	 strJSON = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"", "POALastName":"", "POAFirstName":"", "POAPhoneNumber":"", "POAType":"C", "POAApplDate":"", "Errors":{ "Error":{  } } } },' +  
    	 															   '{ "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"Y", "POALastName":"ELORME", "POAFirstName":"WILFORD", "POAPhoneNumber":"754-368-4561", "POAType":"C", "POAApplDate":"07/14/2015", "Errors":{ "Error":{  } } } },' +
    	 															   '{ "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"Y", "POALastName":"ELORME", "POAFirstName":"WILFORD", "POAPhoneNumber":"754-368-4561", "POAType":"H", "POAApplDate":"07/14/2015", "Errors":{ "Error":{  } } } },' +
    	 															   '{ "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"Y", "POALastName":"ELORME", "POAFirstName":"WILFORD", "POAPhoneNumber":"754-368-4561", "POAType":"O", "POAApplDate":"07/14/2015", "Errors":{ "Error":{  } } } },' + 
    	 															   '{ "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"Y", "POALastName":"ELORME", "POAFirstName":"WILFORD", "POAPhoneNumber":"754-368-4561", "POAType":"U", "POAApplDate":"07/14/2015", "Errors":{ "Error":{  } } } } ] } }';
    	 res.setBody(strJSON );
      	 res.setStatusCode(200);
	 }
	   else if(sBodyContent.contains('2222222222'))
    {
    	strJSON = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[] } }';
    	 res.setBody(strJSON );
      	 res.setStatusCode(200);
    
    }
    else if(sBodyContent.contains('33333333'))
    {
    	strJSON = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ } ] } }';
    	 res.setBody(strJSON );
      	 res.setStatusCode(200);
    
    }
     else if(sBodyContent.contains('44444444'))
    {
    	strJSON = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ } ] } }';
    	 res.setBody(strJSON );
      	 res.setStatusCode(404);
    
    }
    else
    {
    	strJSON = '{ "CIMedicareResponse":{ "GetMedicareMemberInfo":[ { "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"", "POALastName":"", "POAFirstName":"", "POAPhoneNumber":"", "POAType":"A", "POAApplDate":"", "Errors":{ "Error":{  } } } }, { "MedicareMemberLISSNPInfoResponse":{ "MedicareID":"", "LISIndicator":"", "SubsidyEffectDate":"", "SubsidyEndDate":"", "LISSubsidyLevel":"", "LISCopayCategory":"", "LTSSIndicator":"", "ESRDIndicator":"", "SPAPIndicator":"", "SNPIndicator":"", "SNPTypeCode":"", "POAIndicator":"Y", "POALastName":"ELORME", "POAFirstName":"WILFORD", "POAPhoneNumber":"754-368-4561", "POAType":"U", "POAApplDate":"07/14/2015", "Errors":{ "Error":{  } } } } ] } }';
    	res.setBody(strJSON );
      	res.setStatusCode(200);
    }                     
     
      return res;        
    }
    
}