/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_C_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is controller class for component 'DynamicCustomLinks_CMP_HUM'.This is used to retrieve custom links dynamically.
Test Class       : DynamicCustomLinks_T_HUM
*****************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu         27372                   10/05/2015                 Original Version
* Manish Kumar               27372                   10/22/2015                 Restructured entire class to implement SOC for Service layer
* Manish Kumar               20725                   11/13/2015                 Added few variables required for SSO method in Javascript
* Manish Kumar               25216                   01/27/2016                 Added ability for Links to read content from Visualforce
* Anupama Tavva              78689                   03/22/2016                 Added isRequired field for Link paramter for the REQ-304159
* Albin Arul                 86332                   04/20/2017                 Added OneClickURL in prepareLinkUrl method Req - 296697 
* Anupama Tavva              85516                   04/18/2017                 Added DefaultUrl field for Link Object for the REQ-304996
* Prasanthi Kandula          102588                  06/15/2017                 REQ - 318478 - CAS Link - Added error message in populateVFVariables() method 
* Santhi Mandava                                     03/12/2019                 Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* Kapil Arora                                        03/30/2020                 US 967881 Quality - PR00091574 - MF 1 - AHT - Automatically launch PCP/PCD tab on clicking ID Card Management link for HMO plans (RF)
* Kapil Arora										 03/30/2020					US 976383 - PR00091574 - MF 1 - AHT - Ability to launch multiple links together from left hand panel on any page (RF)
* Suresh Kokkeragadda                                05/25/2020                 User Story 1110902: PR00091574 - MF 1 - AHT - Ability to launch multiple links together from left hand panel on any page (CRM)
* Suresh Kokkeragadda                                06/24/2020                 User Story 1203671: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 07/07 and 07/17 releases (CRM)
* Lakshmi Madduri                                    02/26/2021                 SonarQube Fixes
* Suraj Patil								   		 04/09/2021				    REQ - 1905175 - PR00094226 - CR AEP readiness - Low Security Issues 
* Vinay Lingegowda				                     02/07/2022		            User Story 2977662	T1PRJ0314829 - MF 1 - AHT - Ability to navigate to most commonly used pages in CRM (RF)
**********************************************************************************************************************************************/

public with sharing class DynamicCustomLinks_C_HUM
{
    @testVisible private DynamicCustomLinks_S_HUM objServiceLayer = new DynamicCustomLinks_S_HUM();
    
    /**
    * <p>
    * Constructor for the class, Initialize variables
    *
    * @see       #DynamicCustomLinks_C_HUM
    */
    public DynamicCustomLinks_C_HUM()
    {
         lstFinalCustomLinks = new List<DynamicCustomLinks_H_HUM>();
		
    }
    
    public string sObjname {get; set;}
    public Link__c objLnk {get; private set;}
    public List<DynamicCustomLinks_H_HUM> lstFinalCustomLinks {get; private set;}
    public ID recID {get; set;}
    public SObjectDescriptorImpl_H_HUM pageController {get; set;}
    public String jsonMap {get; set;}
    public string HSS_ONECLICK_URL {get; set;}
    public string HSS_ONECLICK_TARGET {get; set;}
    public Integer MAP_URL_LENGTH {get; set;}
    public String sVFJSON {get; set;}
    public String mapLinks {get; set;}
    public boolean isRequiredLinkparam_error{get;set;}
    public String mapLinksDropDown{get;set;}
    public list<SelectOption> lstDropDownOption{get; set;}
    public Boolean bSwitch {
        get{
            return HUMUtilityHelper.isCRMFunctionalityON('2977662');
        }
        set;
    }   
    /**
    * <p>
    * This method is used to get initialize 
    *                1) Selected object record id 
    *                2) Record type
    *                3) Links to display on UI
    * 
    * @see       #init()
    */
    public void initiate()
    {
        recId = (Id)ApexPages.CurrentPage().getParameters().get('id');
        objServiceLayer.setPageName(pageController.getCurrentPage());
        If(recId != null ) sObjname = objServiceLayer.getSObjName((Id)ApexPages.CurrentPage().getParameters().get('id'));
        HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;

        if(!String.isBlank(String.valueOf(recID)) && !String.isBlank(sObjname)) prepareLinksToDisplay();
    }
    
    /**
    * <p>
    * This method is used to initialize Links to display on UI
    * 
    * @see       #prepareLinksToDisplay()
    */
    @testVisible
    private void prepareLinksToDisplay()
    {
        objServiceLayer.getLinkDetails();
        lstFinalCustomLinks = objServiceLayer.prepareLinksToDisplay();
        sVFJSON = objServiceLayer.checkVFLink();
        mapLinks = JSON.serialize(objServiceLayer.mapLinks);
        // common drop down link
        if(bSwitch && UtilityHelper_H_AHT_HUM.isRapidForceUser() && objServiceLayer.toDisplayMostCommonlyUsedDropdown()){
            Map<String, id> mapDropDownLink = objServiceLayer.getDropdownLinks();
            this.mapLinksDropDown = JSON.serialize(objServiceLayer.mapLinksDropDown);
            lstDropDownOption = new list<SelectOption>();
            this.lstDropDownOption.add(new SelectOption('', 'Open New Tab'));
            for(String sKey : mapDropDownLink.keySet()) {          
                this.lstDropDownOption.add(new SelectOption(mapDropDownLink.get(sKey), sKey));
            }
        }
    }
    
    /**
    * <p>
    * This method is used prepare the final url
    * 
    * @see       #prepareLinkUrl()
    */
    public void prepareLinkUrl()
    {   
        isRequiredLinkparam_error = false;
        String sLinkId = (ApexPages.currentPage().getParameters().get('slinkId') != null) ? ApexPages.currentPage().getParameters().get('slinkId').escapeHtmL4() : null;
        String sRecID = (ApexPages.currentPage().getParameters().get('sRecId') != null) ? ApexPages.currentPage().getParameters().get('sRecId').escapeHtml4() : null;
        String sObjectName = (ApexPages.currentPage().getParameters().get('sobjname') != null) ? ApexPages.currentPage().getParameters().get('sobjname').escapeHtml4() : null;
        String lnkMap = (ApexPages.currentPage().getParameters().get('lnkMap') != null) ? String.escapeSingleQuotes(ApexPages.currentPage().getParameters().get('lnkMap')) : null;

                objLnk = objServiceLayer.prepareLinkUrl(sLinkId, sRecId, sObjectName, lnkMap, false);
        
        isRequiredLinkparam_error = objServiceLayer.isRequiredLinkparam_error;
        if(objLnk != null && String.isBlank(objLnk.DefaultUrl__c) && isRequiredLinkparam_error == true)
        {
          ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error,Label.IsRequired_LinkParameter));
        }
        if(!Test.isRunningTest() && objLnk == NULL) {   
            return;
        }  
        jsonMap = objServiceLayer.jsonMap;
        
        sVFJSON = objServiceLayer.checkVFParameter();
        
        MAP_URL_LENGTH = objServiceLayer.mapParams.size();
        
         if(MAP_URL_LENGTH == 1)
                                    {
            HSS_ONECLICK_TARGET = 'TARGET';
        }
        else
        {
            HSS_ONECLICK_TARGET = 'HIDDENTARGET';
        }
        
        HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
    }
    
    /**
    * <p>
    * This method is used to populate varibles from VF
    * 
    * @see       #populateVFVariables()
    */
    public void populateVFVariables()
    {        
        String sLinksJSONMap = (ApexPages.currentPage().getParameters().get('linksJSONMap') != null) ? String.escapeSingleQuotes(ApexPages.currentPage().getParameters().get('linksJSONMap')) : null;
        try
        {
            sVFJSON = NULL;
            if(objLnk != null && String.isBlank(objLnk.DefaultUrl__c) && isRequiredLinkparam_error == true)
            {
                 ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error,Label.IsRequired_LinkParameter));
            }
            String actionType = ApexPages.currentPage().getParameters().get('actionType');
            Map<String, String> mapVFResults = (Map<String, String>)JSON.deserialize(sLinksJSONMap, Map<String, String>.class);
            
            if(actionType.equalsIgnoreCase('LinkCriteria'))
            {
                lstFinalCustomLinks = objServiceLayer.includeVFCriteriaLinks(mapVFResults);
            }
            else if(actionType.equalsIgnoreCase('LinkParam'))
            {
                objLnk = objServiceLayer.includeVFParameterValues(mapVFResults);
                jsonMap = objServiceLayer.jsonMap;
            }
            sVFJSON = NULL;
      }
        catch(Exception ex)
       {
            HUMExceptionHelper.logErrors(ex, 'CustomLinksManager_C_HUM', 'logError');
       }
    }
    
    /**
    * <p>
    * This method is used to log the error and display user friendly msg
    * 
    * @see       #logError()
    */
    public void logError()
    {
        try
        {
           throw new HUMCustomException('There is some problem with your Link : ' + objLnk.Link_Label__c);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CustomLinksManager_C_HUM', 'logError');
        }
    }
}