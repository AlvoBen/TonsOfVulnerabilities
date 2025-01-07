/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_S_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is service layer class for controller DynamicCustomLinks_C_HUM
Test Class       : DynamicCustomLinks_ST_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        27372                    10/05/2015                 Original Version
* Manish Kumar              27372                    10/22/2015                 Restructured entire class to implement SOC but have not changed core logic
* Santhi Mandava            27484                    11/02/2015                 Fixed link query sting issue. Updated method 'linkActionTypeQueryString'
* Santhi Mandava            27574                    11/04/2015                 Fixed link issue for CCS profile users.
* Sruthi Adivikolanu        27608                    11/05/2015                 Fixed issue regarding Static Parameters for Query String.If Parameter is Blank it is passing Null as Value in URl.
                                                                                So modified Code from 348 to 350
* Manish Kumar              27773                    11/13/2015                 Removed the method calling Single sign on Class from Code and implemented that Encoding of url through javascript.
* Manish Kumar              20725                    01/13/2016                 Worked on REQ-237273 to extend framework functionality. Code modified throughout the class.
* Manish Kumar              25216                    01/27/2016                 Added ability for Links to read content from Visualforce
* Anupama Tavva                                      01/23/2017                 Worked on REQ-296918 to remove null pointer exception in prod environment
* Muralidhar Kollu                                   01/24/2017                 Horizontal Links section as part of REQ - 290968 - Go365 Details page Floating Navigation Links 
* Anupama Tavva             78689                    03/22/2016                 Added isRequired field for Link paramter for the REQ-304159
* Pradeepkumar Dani			88534        			 04/28/2017					REQ - 311469 - Rx Lookup PROD issue - Framework updates for null pointer exception
* Lakshmi Madduri                                    03/19/2021                 US-1916924 SonarQube Fixes
* Lakshmi Madduri                                    05/05/2021                 2235996 - Separate constant class for link framework
* Vinay Lingegowda				                     02/07/2022		            User Story 2977662	T1PRJ0314829 - MF 1 - AHT - Ability to navigate to most commonly used pages in CRM (RF)
***********************************************************************************************************************************************/

public with sharing class DynamicCustomLinks_S_HUM
{
    @testVisible private DynamicCustomLinks_D_HUM objDataLayer = new DynamicCustomLinks_D_HUM();
    @testVisible List<Link_Parameter__c> lstlinkparam = NULL;
    public Map<Id,Link__c> mapLinks = new Map<Id, Link__c>();
    public Map<Id,Link__c> mapLinksDropDown = new Map<Id, Link__c>(); // common drop down link
    @testVisible private String sPageName = '';
    @testVisible private String sRecName = '';
    @testVisible private String sObjName = '';
    @testVisible private ID recordID = NULL;
    @testVisible private Map<String, Boolean> maplnktorf = new Map<String, Boolean>();
    @testVisible private Map<String, Boolean> mapdropdwnlnktorf = new Map<String, Boolean>(); // common drop down link
    public Map<String, String> mapParams = NULL;
    public String jsonMap = '';
    public String sectionType = '';
    public boolean isRequiredLinkparam_error;
    
   
    /*
    * Method Name : setPageName
    * Description : This method is used to get Current page name
    * Return type : Id
    * Parameter   : String
    */
    public void setPageName(String sParamPageName)
    {
        sPageName = sParamPageName == LINKS_CONSTANT_HUM.STRING_DISPLAYCUSTOMLINKSPAGENAME ? '' : sParamPageName;
    }
    
    /*
    * Method Name : getSObjName
    * Description : This method is used to retrieve sObject name for current recID
    * Return type : String
    * Parameter   : ID
    */
    public String getSObjName(Id recID)
    {
        this.recordID = recID;
        Schema.SobjectType objType = recordID.getSobjectType();
        sObjName = objType.getDescribe().getName();
        map<Id,Schema.RecordTypeInfo> mapRtById = objType.getDescribe().getRecordTypeInfosById();
        
        if(mapRtById.size() > 1)
        {
            sobject lstquery = objDataLayer.getSObjectRecordType(sObjName, recordID);
            
            sRecName = (string)mapRtById.get((String)lstquery.get('RecordTypeId')).getName();
        }
        return sObjName;
    }
    
    /*
    * Method Name : toDisplayMostCommonlyUsedDropdown
    * Description : This method is used to validate the dropdown display 
    * Return type : Boolean
    * Parameter   : None
    */
    public Boolean toDisplayMostCommonlyUsedDropdown() {
        Boolean bDisplayCommonLink = false;
        if(sObjName == 'Policy_Member__c' && (String.isEmpty(sPageName) || LINKS_CONSTANT_HUM.lstLinksdisplayCommonly.contains(sPageName))) {
            bDisplayCommonLink= true;
        }
        return bDisplayCommonLink;
    }

    /*
    * Method Name : getDropdownLinks
    * Description : This method is used to get the dropDown links and its rec Ids 
    * @param String : Current page name
    * Return type : Map<String,Id>
    * Parameter   : None
    */
    public Map<String,Id>  getDropdownLinks() {
        Map<String,Boolean> maplnkc = new Map<String, Boolean>();
        List<Link_Criteria__c> lstLinkcrt = new List<Link_Criteria__c>();
        mapLinksDropDown = objDataLayer.getDropdownLinks(sPageName);
        for(Link__c objLnk: mapLinksDropDown.values()) {
            if(objLnk.Expression__c == Null) {
                maplnkc.put(String.valueOf(objLnk.id), true);
            } else if(objLnk.Expression__c != Null && objLnk.Link_Criteria__r != null && !objLnk.Link_Criteria__r.isEmpty()) {
                lstLinkcrt.addAll(objLnk.Link_Criteria__r);
            }
        }
        mapdropdwnlnktorf = MatchCondition_H_HUM.validateLinkExpression(lstLinkcrt, maplnkc, sObjname, recordID, mapLinksDropDown);
        Map<String,Id> dropdownLinkMap = new Map<String,Id>();
        for(String sKey : mapLinksDropDown.keySet()){
            if(mapdropdwnlnktorf.containsKey(sKey) && mapdropdwnlnktorf.get(sKey)) {
                dropdownLinkMap.put(mapLinksDropDown.get(sKey).link_label__c , sKey);
            }            
        }
        return dropdownLinkMap;
    }
    
    /*
    * Method Name : getLinkDetails
    * Description : This method is used to retrieve link records based on selected object.
    * Return type : void
    * Parameter   : String, String
    */
    public void getLinkDetails()
    {
        try
        {
            map<String,Boolean> maplnkc = new Map<String, Boolean>();
            List<Link_Criteria__c> lstLinkcrt = new List<Link_Criteria__c>();
            
            objDataLayer.sectionType = sectionType;
            mapLinks = objDataLayer.getLinkLayoutDetails(sObjname, sRecName, sPageName);
            
            for(Link__c objLnk: mapLinks.values())
            {
                if(objLnk.Expression__c == Null)
                {
                    maplnkc.Put(String.valueOf(objLnk.id), true);
                }
                else if(objLnk.Expression__c != Null && objLnk.Link_Criteria__r != null && !objLnk.Link_Criteria__r.isEmpty())
                {
                    lstLinkcrt.addAll(objLnk.Link_Criteria__r);
                }
            }
            maplnktorf = MatchCondition_H_HUM.validateLinkExpression(lstLinkcrt, maplnkc, sObjname, recordID, mapLinks);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BuildCustomLinks_S_HUM', 'getLinkDetails');
        }
    }
    
    /*
    * Method Name : prepareLinksToDisplay
    * Description : This method is used prepare the final section wise links to be shown on UI.
    * Return type : List<DynamicCustomLinks_H_HUM>
    * Parameter   : NA
    */
    public List<DynamicCustomLinks_H_HUM> prepareLinksToDisplay()
    {
        if(!mapLinks.isEmpty())    return prepareFinalLinks();
        return new list<DynamicCustomLinks_H_HUM>();
    }
    
    /*
    * Method Name : checkVFLink
    * Description : This method checks if any VF links has to be rendered on UI
    * Return type : String
    * Parameter   : NA
    */
    public String checkVFLink()
    {
        List<String> lstStr = new List<String>();
        lstStr.addAll(maplnktorf.keySet());
        String sVFJSON = maplnktorf.keySet().size() > 0 ? lstStr[maplnktorf.keySet().size()-1] : '';   
        if(NULL == mapLinks)    return NULL;
        
        for(Id sfdcId : mapLinks.keySet())
        {
            if(String.valueOf(sfdcId).equalsIgnoreCase(sVFJSON))    return NULL;
        }
        
        return sVFJSON;
    }
    
    /*
    * Method Name : prepareFinalLinks
    * Description : This method is used prepare the final section wise links to be shown on UI.
    * Return type : List<DynamicCustomLinks_H_HUM>
    * Parameter   : NA
    */
    @testVisible
    private list<DynamicCustomLinks_H_HUM> prepareFinalLinks()
    {
        Integer iIndex = 0;
        String sSecName='';
        DynamicCustomLinks_H_HUM objWrapper= new DynamicCustomLinks_H_HUM();
        objWrapper.lstCustomLinks = new List<DynamicLinksWrapper_H_HUM>();
        list<DynamicLinksWrapper_H_HUM> lstTempLinks = new List<DynamicLinksWrapper_H_HUM>();
        list<DynamicCustomLinks_H_HUM> lstFinalCustomLinks = new List<DynamicCustomLinks_H_HUM>();
        if(mapLinks.values().isEmpty())    return lstFinalCustomLinks;
        iIndex = (Integer)mapLinks.values()[0].Layout_Section__r.Index__c;
        sSecName = mapLinks.values()[0].Layout_Section__r.Section_Name__c;
        
        for(Link__c objLnk : mapLinks.values())
        {
            if(maplnktorf.containskey(String.valueOf(objLnk.id)) && maplnktorf.get(String.valueOf(objLnk.id)))
            {
                if(iIndex != objLnk.Layout_Section__r.Index__c || sSecName  != objLnk.Layout_Section__r.Section_Name__c)
                {
                    objWrapper.sSectionName = sSecName;
                    objWrapper.lstCustomLinks =  lstTempLinks;
                    lstFinalCustomLinks.Add(objWrapper);
                    objWrapper = new DynamicCustomLinks_H_HUM();
                    lstTempLinks = new List<DynamicLinksWrapper_H_HUM>();
                }
                lstTempLinks = putInList(objLnk, lstTempLinks);
                sSecName = objLnk.Layout_Section__r.Section_Name__c;
                iIndex = (Integer)objLnk.Layout_Section__r.Index__c;
            }
        }
        objWrapper.sSectionName = sSecName;
        objWrapper.lstCustomLinks = lstTempLinks;
        lstFinalCustomLinks.Add(objWrapper);
        
        return lstFinalCustomLinks;
    }
    
    /*
    * Method Name : putInList
    * Description : This method is used to add custom link in a List
    * Return type : list<DynamicLinksWrapper_H_HUM>
    * Parameter   : Link__c, List<DynamicLinksWrapper_H_HUM>
    */
    @testVisible
    private list<DynamicLinksWrapper_H_HUM> putInList(Link__c objLnk, List<DynamicLinksWrapper_H_HUM> lstTempLinks)
    {
        DynamicLinksWrapper_H_HUM objTempLink = new DynamicLinksWrapper_H_HUM();
        objTempLink.LinkID = objLnk.Id;
        objTempLink.Link = objLnk;
        objTempLink.LinkLabel= objLnk.Link_Label__c;
        lstTempLinks.Add(objTempLink);
        
        return lstTempLinks;
    }
    
    /*
    * Method Name : includeVFCriteriaLinks
    * Description : This method is used to add custom link after evaluating from VF
    * Return type : list<DynamicCustomLinks_H_HUM>
    * Parameter   : Map<String, String>
    */
    public list<DynamicCustomLinks_H_HUM> includeVFCriteriaLinks(Map<String, String> mapVFResults)
    {
        List<Link_Criteria__c> lstLinkcrt = new List<Link_Criteria__c>();
        
        if(NULL == mapLinks)    return new list<DynamicCustomLinks_H_HUM>();
        
        for(String sKey : mapVFResults.keySet())
        {
         if(!sKey.split('\\.').isEmpty() && mapLinks.get(sKey.split('\\.')[0]) != null)
            lstLinkcrt.addAll(mapLinks.get(sKey.split('\\.')[0]).Link_Criteria__r);
        }
        
        maplnktorf = MatchCondition_H_HUM.validateLinkExpressionForVF(lstLinkcrt, maplnktorf, sObjname, recordID, mapLinks, mapVFResults);
        
        return prepareFinalLinks();
    }
    
    /*
    * Method Name : prepareLinkUrl
    * Description : This method is used prepare the final url
    * Return type : Link__c
    * Parameter   : String
    */
    public Link__c prepareLinkUrl(String sLinkId, String sRecId, String objName, String lnkMap, Boolean isCallBack)
    {
      isRequiredLinkparam_error=false;
        try
        {
            recordID = sRecId;
            sObjName = objName;
            Link__c oLink = NULL;
            String sURL = '';
            
            if(!isCallBack)
            {
                mapLinks = ( Map<Id, Link__c>)JSON.deserialize(lnkMap, Map<Id, Link__c>.class);
                lstlinkparam = objDataLayer.retrieveLinkParameterFields(sLinkId);
                mapParams = LinkParameterLogic_H_HUM.getQueryParameters(lstlinkparam, sRecId, objName, false);
                isRequiredLinkparam_error = LinkParameterLogic_H_HUM.isRequiredLinkparam_errorval;
            }
            
            Link__c objTmplnk = mapLinks.get(sLinkId);
            
            if(NULL == mapParams)    return NULL;
            
            jsonMap = JSON.serialize(mapParams); 
            
            if(objTmplnk.Link_Action__c.equalsIgnoreCase('Query String'))
            {
                sUrl = objTmplnk.Url__c + '?';
                
                if(!mapParams.isEmpty())
                {
                   for(string obj : mapParams.keyset() )
                   {
                       sUrl += obj +'='+mapParams.get(obj)+'&';
                   }
                   sUrl = sUrl.removeEnd('&');
                }
            }
            else if(objTmplnk.Link_Action__c.equalsIgnoreCase('Basic'))
            {
                sURL = objTmplnk.Url__c;
            }
            else if(objTmplnk.Link_Action__c.equalsIgnoreCase('Post'))
            {
                sURL = objTmplnk.Url__c;
            }
            
            oLink = objTmplnk.clone();
            oLink.Url__c = sURL;
            return oLink;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'BuildCustomLinks_S_HUM', 'prepareLinkUrl');
            return NULL;
        }
    }
    
    /*
    * Method Name : checkVFParameter
    * Description : This method checks if any values has to be fetched from VF
    * Return type : String
    * Parameter   : NA
    */
    public String checkVFParameter()
    {
        if(NULL == mapParams)    return NULL;
        
        Map<String, String> mapTemp = new Map<String, String>();
        
        for(String key : mapParams.keySet())
        {
            if(mapParams.get(key) != null && mapParams.get(key).equalsIgnoreCase('__LinkFraework__VisualForce__'))
            {
                mapTemp.putAll((Map<String, String>)JSON.deserialize(key, Map<String, String>.class));
            }
        }
        if(!mapTemp.isEmpty())    return JSON.serialize(mapTemp);
        return NULL;
    }
    
    /*
    * Method Name : includeVFParameterValues
    * Description : This method is used to add custom link after evaluating from VF for Link Parameter
    * Return type : Link__c
    * Parameter   : Map<String, String>
    */
    public Link__c includeVFParameterValues(Map<String, String> mapVFResults)
    {
        mapParams = LinkParameterLogic_H_HUM.includeLinkParameterFormVF(mapParams, mapVFResults, recordID);
        jsonMap = JSON.serialize(mapParams); 
        if(!mapVFResults.isEmpty()){
            String key = (new List<String>(mapVFResults.keySet()))[0];
            return prepareLinkUrl(key.split('\\.')[2], recordID, sObjName, null, true);
        }
        
        return NULL;
    }
}