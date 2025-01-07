/*******************************************************************************************************************************
Apex Class Name : HUMCustomLinksController 
Version         : 1.0
Created On      : 04/01/2015
Function        : This class is used to display custom Links
Test Class      : HUMCustomLinksControllerTest 
           
Modification Log: 
* Developer Name           Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Asha Deepika Koyyana       22136                   04/03/2015                  Original Version
*Saurabh Lamba              114940                  07/28/2017                  REQ - 323535 - Ability of a system to land on Humana Group Medicare listing Page directly
*Subhamay Mandal                                    01/30/2018                  REQ - 351097 - Ability in CRM Console to display Medicare Medicaid Job Aid link under additional resources section at CRM footer (RF)
*Subhamay Mandal            		                03/29/2018                  REQ - 357681 - Ability in CRM Console to display Medicare Medicaid Job Aid link under additional resources section at CRM footer (CRM)
*Darshan Sharma                                     04/20/2018                  REQ - 361047 - Ability to update Mentor Links in QuickStart via Business Configuration (RF) 
*Kavya Bondada                                      08/10/2021                  User Story 2551570: T1PRJ0001827 - MF 1 - Quality - Add 'Medicaid Related Resources' section under Additional Resources (CRM)
*Swetha Thuti                                      09/08/2023                   User Story 4853184: T1PRJ1117588 CarePlus - Classic CRM - Create a CarePlus section under Additional Resource Widget
*******************************************************************************************************************************/
public with sharing class HUMCustomLinksController {
    
    public List<CustomLink__c> lstCustomLink{get;private set;}
    public List<CustomLink__c> lstCommercialResource{get;private set;}
    public List<CustomLink__c> lstGeneralResource{get;private set;}
    public List<CustomLink__c> lstMedicareResource{get;private set;}
    public List<CustomLink__c> lstMedicaidResource{get;private set;}
    public List<CustomLink__c> lstCareplusResource{get;private set;}
    public Boolean bShowCarePlusResources{get;set;}
    
    
    /* Method Name  :  populateSubSections
    * Description   :  Populates  the Medicare, Commercial and General Resource list
    * Parameter     :  NA
    * Return Type   :  void
    */
    public void populateSubSections()
    {
        
        try
        {
            bShowCarePlusResources = HUMUtilityHelper.isCRMFunctionalityON('4853184');
            
            lstCommercialResource = new  List<CustomLink__c>();
            lstGeneralResource = new  List<CustomLink__c>();
            lstMedicareResource = new  List<CustomLink__c>();
            lstMedicaidResource = new List<CustomLink__c>();
            lstCareplusResource = new List<CustomLink__c>();
            Integer iValue = Integer.Valueof(HUMConstants__c.getInstance('HUMConstantHundered').IntegerValue__c); 
            lstCustomLink = [Select Name,Subsection__c,URL__c from CustomLink__c where RecordType.Name = 'Custom Link' order by Name Limit : iValue ];
            for(CustomLink__c oCustomLink : lstCustomLink)
            {
             if(String.isNotBlank(oCustomLink.Subsection__c)){
                if(oCustomLink.Subsection__c.equals(System.Label.HUMMedicareResource)) lstMedicareResource.add(oCustomLink);
                if(oCustomLink.Subsection__c.equals(System.Label.HUMCommercialResource)) lstCommercialResource.add(oCustomLink);
                if(oCustomLink.Subsection__c.equals(System.Label.HUMGeneralResource)) lstGeneralResource.add(oCustomLink);
                if(oCustomLink.Subsection__c.equals(System.Label.HUMMedicaidResource)) lstMedicaidResource.add(oCustomLink);
                if(oCustomLink.Subsection__c.equals(System.Label.HUMCareplusResource))lstCareplusResource.add(oCustomLink);
                       
                }
            }
      	}


          catch(exception ex)
	      {
	          HUMExceptionHelper.logErrors(ex, 'HUMCustomLinksController ', 'populateSubSections ');      
	      }  
    }
  }