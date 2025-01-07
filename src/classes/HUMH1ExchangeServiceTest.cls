/**************************************************************************************************************************************
Apex Class Name : HUMH1ExchangeServiceTest
Version         : 1.0
Created On      : 11/19/2014
Function        : This class is used as test class for HUMH1ExchangeService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri                                11/19/2014                 Original Version
* Pradeep Veeravali        19401                       11/24/2014                 Changed to define as mock 
* Pradeep Veeravali        19969                       12/17/2014                 updated to fix Test class failure w.r.t REQ-66104
****************************************************************************************************************************************/

@isTest
global class HUMH1ExchangeServiceTest implements WebServiceMock{
          
    global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType) {
            
               HUMH1ExchangeResponseHelper.EnrollmentDetailResponse respElement = new HUMH1ExchangeResponseHelper.EnrollmentDetailResponse();
               HUMH1ExchangeResponseHelper.EnrollmentDetailList RespInfo = new HUMH1ExchangeResponseHelper.EnrollmentDetailList();
               List<HUMH1ExchangeResponseHelper.EnrollmentDetail> lstResp = new List<HUMH1ExchangeResponseHelper.EnrollmentDetail>();
               HUMH1ExchangeResponseHelper.EnrollmentDetail EnrApp = new HUMH1ExchangeResponseHelper.EnrollmentDetail();
               
               EnrApp.firstName = 'KEREL';
               EnrApp.lastName = 'NEILSEN';
               EnrApp.strDob = '1957-08-23';               
               EnrApp.groupId = '';
               EnrApp.covEffDt = '2014-01-01';
               EnrApp.covTrmDt = '9999-12-31';               
               EnrApp.applnId = '';
               EnrApp.contId = '';
               EnrApp.othSt = '';
               EnrApp.othZip = '';
               EnrApp.exchMbrID = '0000012333';
               EnrApp.strSsn = '123456789';               
               EnrApp.genderCd = 'M';
               EnrApp.othAddr1 = '';
               EnrApp.cntyName = '121';
               EnrApp.othCity = '';
               EnrApp.mailAddr1 = '2870 Peachtree Rd';
               EnrApp.mailCity = 'Atlanta';
               EnrApp.mailSt = 'GA';
               EnrApp.mailZip = '30305';
               EnrApp.homePhone = '';
               EnrApp.workPhone = '';
               EnrApp.pymtOptInd = '';
               EnrApp.csrEffDate = '9999-12-31';
               EnrApp.csrLevel = '0';
               EnrApp.csrEndDate = '9999-12-31';
               EnrApp.aptcAmount = '0.00';
               EnrApp.aptcEffDate = '2014-01-01';
               EnrApp.aptcEndDate = '9999-12-31';
               EnrApp.strBsn = '';
               EnrApp.mktRcptDate = '9999-12-31';
               EnrApp.signDATE = '9999-12-31';
               EnrApp.salesRepName = '';
               EnrApp.salesType = '';
               EnrApp.fileDt = '2013-10-02';
               EnrApp.procDt = '2013-10-02';
               EnrApp.creationTs = '2013-10-02-19.14.36.000008';
               EnrApp.barCodeBase = '';
               EnrApp.oecConfId = '';
               EnrApp.applnChnl = 'EDI';
                              
               lstResp.add(EnrApp);
               RespInfo.EnrollmentDetail = lstResp;
               respElement.EnrollmentDetailList = RespInfo;         
               response.put('response_x', respElement); 
            
          }
           
}