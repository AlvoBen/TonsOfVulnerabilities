/****************************************************************************************************************************************
Apex Class Name  : HUMMentorToSFDCHelperTest
Version          : 1.0
Created Date     : September 30 2014
Function         : Test class for HUMMentorToSFDCHelper
 *****************************************************************************************************************************************
 Modification Log: 
* Developer Name              Code Review                Date                       Description 
*---------------------------------------------------------------------------------------------------------------------------------------- 
* Palak Papneja                                        09/30/2014                 Original Version
* Asha Deepika Koyyana        20572                    01/28/2015                 Fixed test class failure for Defect#164139  
* Santhi Mandava                                       03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage.            
* Melkisan Selvaraj                                    08/26/2019                 Launch Compass Changes- Quick Start
*****************************************************************************************************************************************/
@isTest
private class HUMMentorToSFDCHelperTest
{    
  /*
   * Method Name : testAttachToCase
   * Description : Test method to successfully insert article for a case which is not closed or cancelled     
   * Return type : void     
   * Parameter   : NA     
   */    
   static testMethod void testAttachToCase()    
   {        
     Case oCase = HUMTestDataHelperTest.getCase();        
     oCase.Status = 'New';          
     insert oCase;        
     PageReference pageRef = Page.HUMMentorToSFDC;        
     Test.setCurrentPage(pageRef);               
     ApexPages.CurrentPage().getParameters().put('caseid',oCase.Id);        
     ApexPages.CurrentPage().getParameters().put('casename','test name');        
     Compass__c objCompass = HUMTestDataHelperTest.getCompassRecord();        
     ApexPages.StandardController stdCntrl = new ApexPages.StandardController(objCompass);        
     HUMMentorToSFDCController objMentorClass = new HUMMentorToSFDCController(stdCntrl);        
     //Test for a article for a case which is not closed 
     test.startTest();      
     HUMMentorToSFDCHelper compPayload = new HUMMentorToSFDCHelper('test', '09000929806ec765', 'www.salesforce.com' );                     
     String sPayload = JSON.serialize(compPayload)+'"'+oCase.Id+'"';                
     String sResult = HUMMentorToSFDCController.attachToCase(sPayload,oCase.id);        
     test.stopTest();        
     System.assertEquals(System.Label.HUMCanvasAlertInserted, sResult); 
   }
}