/****************************************************************************************************** 
Apex Class Name  : HUMCaseAttachmentInfoToCCPServiceTest
Version          : 1.0  
Created Date     : Aug 8 2014 
Function         : Test class to cover HUMCaseAttachmentInfoToCCPService
****************************************************************************
Modification Log:

* Developer Name                Review Number                  Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vishal Verma                   17844                      06/08/2014                   Original Version
* Santhi Mandava                 20576                      01/28/2015                   Code Modified to fix test class failure Defect # 164139
* Gargey Sharma                                             03/18/2014                   Updated test data for HUMConstants custom setting   
* Santhi Mandava                                            03/12/2019                   Removed "DpaaSWebServiceMockImpl" usage.
*********************************************************************************************************/
@isTest
private class HUMCaseAttachmentInfoToCCPServiceTest
{
    Static HUMattachmentRequestDTO request ;
    Static String CaseNumber ; 
    Static Case cs;
    Static String Type;
    
    private static testMethod void test_method_one()
    {
        HUMTestDataHelperTest.getHUMConstantsData(); 
        
        Type = 'Type';
        createRecords();
        cs = [Select Id , CaseNumber from Case where Id = :cs.Id];
        CaseNumber  = cs.CaseNumber ;
        createRequest();
        Test.StartTest();
            List<HUMAttachmentResponseDTO> lstAttachResptest = HUMCaseAttachmentInfoToCCPService.updateCaseAttachmentInfo(request);
            System.assert(lstAttachResptest.size() > 0);
        Test.StopTest();
    }
   
    private static testMethod void test_method_two()
    {
        HUMTestDataHelperTest.getHUMConstantsData(); 
        Type = 'Type11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111999999999999999999999999999111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111';
        createRecords();
        cs = [Select Id , CaseNumber from Case where Id = :cs.Id];
        CaseNumber  = cs.CaseNumber ;
        createRequest();
        Test.StartTest();
            List<HUMAttachmentResponseDTO> lstAttachResptest = HUMCaseAttachmentInfoToCCPService.updateCaseAttachmentInfo(request);
            System.assert(lstAttachResptest.size() > 0);
        Test.StopTest();
    }
    
    private static testMethod void negative_one()
    {
        HUMTestDataHelperTest.getHUMConstantsData();        
        createRecords();
        CaseNumber  = '';
        createRequest();
        Test.starttest();
            List<HUMAttachmentResponseDTO> lstAttachResptest = HUMCaseAttachmentInfoToCCPService.updateCaseAttachmentInfo(request);
            System.assert(lstAttachResptest[0].Success == false);
        Test.StopTest();
    }
    
    private static testMethod void negative_two()
    {
        HUMTestDataHelperTest.getHUMConstantsData(); 
         createRecords();
         Test.starttest();
            List<HUMAttachmentResponseDTO> lstAttachResptest = HUMCaseAttachmentInfoToCCPService.updateCaseAttachmentInfo(request);
            System.assert(lstAttachResptest[0].Success == false);
         Test.StopTest();
    }
    
    private static testMethod void defaultScenario()
    {
        HUMTestDataHelperTest.getHUMConstantsData(); 
        createRecords();
        createRequest();
        Test.starttest();
       
            List<HUMAttachmentResponseDTO> lstAttachResptest = HUMCaseAttachmentInfoToCCPService.updateCaseAttachmentInfo(request);
            System.assert(lstAttachResptest[0].Success == true);
        Test.StopTest();  
    }
    
    private Static void createRequest()
    {
        HUMTestDataHelperTest.getHUMConstantsData(); 
        request = new HUMattachmentRequestDTO();
        List<HUMAttachmentAttributeRequestDTO> lstAttachAttr = new List<HUMAttachmentAttributeRequestDTO>();
        
        HUMAttachmentRequestDTO attachmentDTO = new HUMAttachmentRequestDTO();
        attachmentDTO.CreatedBy = 'Test';
        attachmentDTO.CaseId = CaseNumber  ;
        attachmentDTO.AttachmentDescription= 'Desc';
        attachmentDTO.AttachmentKey= 'Key';
        attachmentDTO.AttachmentType= Type;
        
        HUMAttachmentAttributeRequestDTO attachmentAttrib = new HUMAttachmentAttributeRequestDTO();
        attachmentAttrib.AttachmentName = 'Test 2';
        attachmentAttrib.AttachmentValue = 'Val 2';
        lstAttachAttr.add(attachmentAttrib);
        attachmentDTO.AttachmentAttribute = lstAttachAttr;
        
        request = attachmentDTO;
    }
   
    private Static  void createRecords()
    {
        HUMTestDataHelperTest.getHUMConstantsData(); 
        cs = HUMTestDataHelperTest.getCase();
        insert cs;
        cs = [Select Id , CaseNumber from Case where Id = :cs.Id];
        CaseNumber  = cs.CaseNumber ;
        system.assert(cs.Id != NULL);
        Interaction__c inte = HUMTestDataHelperTest.getInteraction();
        insert inte;
        Case_Interaction__c caseInt = new Case_Interaction__c();
        caseInt.Case__C = cs.id;
        caseInt.Interaction__c = inte.Id;
        insert caseInt;   
    }
}