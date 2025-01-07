/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinksHorizontal_C_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is controller class for component 'DynamicCustomLinksHorizontal_CMP_HUM'.This is used to retrieve custom links dynamically.
Test Class       : DynamicCustomLinksHorizontal_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu             27372                   01/20/2016                 Original Version
* Santhi Mandava                                       03/12/2019          Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
* T Prasanna Sai Kumar                                 03/22/2021          Req 1916929 - Dynamic Links Vulnerabilities - Dynamic Custom Link Components - DynamicCustomLinksHorizontal_C_HUM,DynamicCustomLinksHorizontal_T_HUM
* Suraj Patil								   		   04/09/2021		    REQ - 1905175 - PR00094226 - CR AEP readiness - Low Security Issues 
* Lakshmi Madduri                                      05/5/2021            2235996 - Changed the constant class reference
**********************************************************************************************************************************************/

public with sharing class DynamicCustomLinksHorizontal_C_HUM
{
    @testVisible private DynamicCustomLinks_S_HUM objServiceLayer = new DynamicCustomLinks_S_HUM();
    
    /*
    * Method Name : DynamicCustomLinksHorizontal_C_HUM
    * Description : Constructor for the class, Initialize variables
    * Return type : NA
    * Parameter   : NA
    */
    Public DynamicCustomLinksHorizontal_C_HUM()
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
    public String mapLinks {Get; Set;}
    
    /*
    * Method Name : initiate
    * Description : This method Is used to get initialize 1) Selected object record id 2) Record type  3) Links to display on UI
    * Return type : void
    * Parameter   : NA
    */

    Public void initiate()
    {
        recId = (Id)ApexPages.CurrentPage().getParameters().get('id');
        objServiceLayer.setPageName(pageController.getCurrentPage());
        If(recId != null ) sObjname = objServiceLayer.getSObjName((Id)ApexPages.CurrentPage().getParameters().get('id'));
        HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
        
        if(!String.isBlank(String.valueOf(recID)) && !String.isBlank(sObjname)) prepareLinksToDisplay();
    }
    
    /*
    * Method Name : prepareLinksToDisplay
    * Description : This method Is used to initialize Links to display on UI
    * Return type : void
    * Parameter   : NA
    */

    @testVisible
    private void prepareLinksToDisplay()
    {
        objServiceLayer.sectionType = LINKS_CONSTANT_HUM.STRING_HORIZONTAL;                
        objServiceLayer.getLinkDetails();
        lstFinalCustomLinks = objServiceLayer.prepareLinksToDisplay();
        sVFJSON = objServiceLayer.checkVFLink();
        mapLinks = JSON.serialize(objServiceLayer.mapLinks);
    }
   
    /*
    * Method Name : prepareLinkUrl
    * Description :  This method Is used prepare the final url
    * Return type : void
    * Parameter   : NA
    */

    Public void prepareLinkUrl()
    {
        String sLinkId = (ApexPages.currentPage().getParameters().get('slinkId') != null) ? ApexPages.currentPage().getParameters().get('slinkId').escapeHtmL4() : null;
        String sRecID = (ApexPages.currentPage().getParameters().get('sRecId') != null) ? ApexPages.currentPage().getParameters().get('sRecId').escapeHtml4() : null;
        String sObjectName = (ApexPages.currentPage().getParameters().get('sobjname') != null) ? ApexPages.currentPage().getParameters().get('sobjname').escapeHtml4() : null;
        String lnkMap = (ApexPages.currentPage().getParameters().get('lnkMap') != null) ? String.escapeSingleQuotes(ApexPages.currentPage().getParameters().get('lnkMap')) : null;

        objLnk = objServiceLayer.prepareLinkUrl(sLinkId, sRecId, sObjectName, lnkMap, false);
        
        if(!Test.isRunningTest() && objLnk == NULL)    
        {
            return;
        }       
        jsonMap = objServiceLayer.jsonMap;
        
        sVFJSON = objServiceLayer.checkVFParameter();
        
        MAP_URL_LENGTH = objServiceLayer.mapParams.size();
        
        if(MAP_URL_LENGTH == 1)
        {
            HSS_ONECLICK_TARGET = LINKS_CONSTANT_HUM.STRING_TARGET;
        }
        else
        {
            HSS_ONECLICK_TARGET = LINKS_CONSTANT_HUM.STRING_HIDDENTARGET;
        }
    }
    
    /*
    * Method Name : populateVFVariables
    * Description : This method Is used to populate varibles from VF
    * Return type : void
    * Parameter   : NA
    */

    Public void populateVFVariables()
    {
        String sLinksJSONMap = (ApexPages.currentPage().getParameters().get('linksJSONMap') != null) ? String.escapeSingleQuotes(ApexPages.currentPage().getParameters().get('linksJSONMap')) : null;
        try
        {
            sVFJSON = NULL;
            
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
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'DynamicCustomLinksHorizontal_C_HUM', 'logError');
        }
    }
   
    /*
    * Method Name : logError
    * Description : This method Is used to log the error And display user friendly msg
    * Return type : void
    * Parameter   : NA
    */

    Public void logError()
    {
        try
        {
           Throw New HUMCustomException(system.label.CUSTOMLINKS_ERROR_LOG_HUM + objLnk.Link_Label__c);
             
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'DynamicCustomLinksHorizontal_C_HUM', 'logError');
        }
    }
}