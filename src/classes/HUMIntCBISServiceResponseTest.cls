/**************************************************************************************************************************************
Apex Class Name : HUMIntCBISServiceResponseTest
Version         : 1.0
Created On      : 10/24/2014
Function        : This class mocks the response of HUMIntCBISService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Chaitanya Kumar            18890                     10/24/2014                 Original Version
****************************************************************************************************************************************/

@isTest
global class  HUMIntCBISServiceResponseTest implements WebServiceMock{

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
        
        HUMIntCBISService.GetPendHistoryTermsDetailsResponse_element respElement = new HUMIntCBISService.GetPendHistoryTermsDetailsResponse_element ();
           
        HUMIntCBISServiceHelper.Address add = new HUMIntCBISServiceHelper.Address();
        add.Address1 = 'Address1';
        add.Address2 = 'Address2';
        add.City = 'City';
        add.State = 'State';
        add.Zip = '123';
        add.ZipPlus = '000';
       
        HUMIntCBISServiceHelper.Policy pol = new HUMIntCBISServiceHelper.Policy();
        pol.BenId = '123';
        pol.CoverageEndDate = String.valueof(System.today()+1);
        pol.CoverageStartDate = String.valueof(System.today() - 1);
        pol.GroupId = '12';
        pol.PendError = '';
       
        List<HUMIntCBISServiceHelper.ResponseItem> lstRespItem = new list<HUMIntCBISServiceHelper.ResponseItem>();
        HUMIntCBISServiceHelper.ResponseItem respItem = new HUMIntCBISServiceHelper.ResponseItem();
        respItem.Address = add;
        respItem.DOB = '01/01/2000';
        respItem.DependentSSN = '000';
        respItem.FirstName = 'fName';
        respItem.LastName = 'lName';
        respItem.MemberSSN  = '123';
        respItem.MiddleInit = 'init' ;
        respItem.NameSuff = 'Mr';
        respItem.PhoneNumber = '9999999999';
        respItem.Platform = 'p';
        respItem.Policy = pol;
        respItem.RelationType = 'rType';
        respItem.SexType = 'sType';
        lstRespItem.add(respItem);
        
        HUMIntCBISServiceHelper.ArrayOfResponseItem respItems = new HUMIntCBISServiceHelper.ArrayOfResponseItem();
        respItems.ResponseItem = lstRespItem;
        
        HUMIntCBISServiceHelper.Status status = new HUMIntCBISServiceHelper.Status() ;
        status.Error = null;
        status.IsSuccesful = true;
        
        HUMIntCBISServiceHelper.PendHistoryandTermsResponseDTO responseDTO = new HUMIntCBISServiceHelper.PendHistoryandTermsResponseDTO();
        responseDTO.Status = status;
        responseDTO.ResponseItems = respItems;
        
        respElement.GetPendHistoryTermsDetailsResult = responseDTO;
        response.put('response_x', respElement);
   }
}