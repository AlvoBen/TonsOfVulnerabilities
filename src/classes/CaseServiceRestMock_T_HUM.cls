/*******************************************************************************************************************************
Apex Class Name :  CaseServiceRestMock_T_HUM.cls
Version: 1.0
Created On      : 10/17/2018 
Function        :Test class to generate Mock response
Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Charan konakandla         314128                     12/15/2018                   Original Version
********************************************************************************************************************************/
@isTest
public with sharing class CaseServiceRestMock_T_HUM implements HTTPCalloutMock 
{
	 private String  strMockResponseBody;
     private Integer intMockResponseCode;
    
    /*
    * Method Name   :    CaseServiceRestMock_T_HUM(Parameterised Constructor)
    * Description   :    This is Parameterised Constructor. 
    * Return Type   :    N/A
    * Parameters    :    String strRespnseBody, Integer intResponseCode
    */ 
    public CaseServiceRestMock_T_HUM(String strRespnseBody, Integer intResponseCode)
    {
        this.strMockResponseBody = strRespnseBody;
        this.intMockResponseCode = intResponseCode;
    }
    
     /*
    * Method Name   :    respond
    * Description   :    This method is used to create mock service response
    * Return Type   :    HTTPResponse
    * Parameters    :    HTTPRequest req
    */ 
    public HTTPResponse respond(HTTPRequest req )
    {
      HttpResponse res = new HttpResponse();
      res.setHeader('Content-Type', 'application/json');
      res.setBody(strMockResponseBody);
      res.setStatusCode(intMockResponseCode);
      return res;  
    }    
}