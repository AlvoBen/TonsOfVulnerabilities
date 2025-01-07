/**************************************************************************************************************************************
Apex Class Name : HUMIntAEHistoryServiceTest 
Version         : 1.0
Created On      : 08/05/2014
Function        : This class mocks the response of HUMIntAEHistoryService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth             17165                    08/05/2014                 Original Version
* Shruthi Karanth             17355                    08/14/2014                 Changes as per updated wsdl
****************************************************************************************************************************************/

@isTest
global class HUMIntAEHistoryServiceTest implements WebServiceMock{
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
               HUMIntAEHistoryResponseHelper.ResponseComplexType respElement = new HUMIntAEHistoryResponseHelper.ResponseComplexType();
               HUMIntAEHistoryResponseHelper.PendHistoryList pendHistoryList = new HUMIntAEHistoryResponseHelper.PendHistoryList();
               HUMIntAEHistoryResponseHelper.PendHistory pendHistory = new HUMIntAEHistoryResponseHelper.PendHistory();
               pendHistory.SSN = '12345678';
               pendHistory.Pend_Key='121';
               pendHistory.First_Name = 'fName';
               pendHistory.Last_Name = 'lName';
               pendHistory.Birth_Date = Date.parse('11/30/1986');
               List<HUMIntAEHistoryResponseHelper.PendHistory> lstPendHistory = new List<HUMIntAEHistoryResponseHelper.PendHistory>();
               lstPendHistory.add(pendHistory);
               pendHistoryList.PendHistory = lstPendHistory;
               respElement.PendHistoryList = pendHistoryList;
               response.put('response_x', respElement); 
   }
}