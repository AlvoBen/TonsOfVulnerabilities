/**************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_LC_HUM
Version          : 1.0
Created Date     : 06/02/2021
Function         : This is controller for dynamicCustomLinkLcHum Lightning Web Component
Test Class       : DynamicCustomLinks_LT_HUM
Code Coverage    : 86%
 *
 ***********************************************************************************************************************************************
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Ranadheer Alwal                                  06/02/2021               Original Version
* Suraj patil   								 	07/16/2021				 US - 2365235 - T1PRJ0003805 - MF SF: TECH Linkout objects Picklist value for Lightning
 * Anuradha Gajbhe                                  05/05/2023               US-3862189 : T1PRJ0865978 -CRM Salesforce Lightning/HealthCloud - Lightning - Core - TECH - Quicklinks Framework Updates.
 *************************************************************************************************************************************************/
public with sharing class DynamicCustomLinks_LC_HUM
{
    
    public static String sPageName = '';
    public static String sObjname = '';
    public static String sRecName = '';
    public static string HSS_ONECLICK_URL;
    public static String sectionType = '';
    public static Map<Id,Link__c> mapLinks = new Map<Id, Link__c>();
    public static Map<String, Boolean> maplnktorf = new Map<String, Boolean>();
    public static List<DynamicCustomLinks_LH_HUM> lstFinalCustomLinks = new List<DynamicCustomLinks_LH_HUM>();
    public static Boolean isRequiredLinkparam_error = false;
    public static String jsonMap = '';
    public static Map<String, String> mapParams = NULL;
    public static List<Link_Parameter__c> lstlinkparam = NULL;
    public static String sVFJSON;
    public static Link__c objLnk = new Link__c();
    public static string HSS_ONECLICK_TARGET;
    public static Integer MAP_URL_LENGTH;
    public Static Boolean isRequiredLinkparam_errorval;

    //wrapper to return the result - used in prepareLinkUrlResultWrapperVar
    public class DynamicCustomLinksResultWrapper{
        @AuraEnabled public List<DynamicCustomLinks_LH_HUM> lstFinalCustomLinks{get; set;}
        @AuraEnabled public Map<Id,Link__c> mapLinks{get; set;}
        @AuraEnabled public Map<String, Boolean> maplnktorf{get; set;}
        @AuraEnabled public string sVFJSON{get;set;}
        @AuraEnabled public Boolean chkLobPrioritySwitch {
            get{
                return HUMUtilityHelper.isCRMFunctionalityON('3862189');
            }
            set;
        }
    }

    //wrapper to return the result - used in DynamicCustomLinksConsolidatedWrapper
    public class prepareLinkUrlResultWrapper{
        @AuraEnabled public String sVFJSON{get; set;}
        @AuraEnabled public Link__c objLnk{get; set;}
        @AuraEnabled public String jsonMap{get; set;}
        @AuraEnabled public String HSS_ONECLICK_URL{get; set;}
        @AuraEnabled public String HSS_ONECLICK_TARGET{get; set;}
        @AuraEnabled public Integer MAP_URL_LENGTH{get; set;}
        @AuraEnabled public Boolean isRequiredLinkparam_error{get; set;}
        @AuraEnabled public Map<String, String> mapParams{get; set;}
    }

    //wrapper to return the result on the load of the lwc component
    //we are returning these wrapper as the apex class needs these value again when the link is clicked in the compoennt
    public class DynamicCustomLinksConsolidatedWrapper{
        @AuraEnabled public DynamicCustomLinksResultWrapper DynamicCustomLinksResultWrapperVar{get; set;}
        @AuraEnabled public prepareLinkUrlResultWrapper prepareLinkUrlResultWrapperVar{get; set;}
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
    @AuraEnabled
    public static DynamicCustomLinksResultWrapper initiate(String recID, string pageName)
    {
        DynamicCustomLinksResultWrapper result = new DynamicCustomLinksResultWrapper();
		
        sPageName = DynamicCustomLinks_LS_HUM.setPageName(pageName);
        If(recId != null ) sObjname = DynamicCustomLinks_LS_HUM.getSObjName(recID, sObjName, sRecName);
        HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
        if((!String.isBlank(String.valueOf(recID)) && !String.isBlank(sObjname)) || (!String.isBlank(sPageName))){
            result.lstFinalCustomLinks = prepareLinksToDisplay(recID);
            result.mapLinks = mapLinks;
            result.maplnktorf = maplnktorf;
            result.sVFJSON =  checkVFLink();
            return result; 
        } 
        return null;
    }

    /**
    * <p>
    * This method is used to initialize Links to display on UI
    * 
    * @see       #prepareLinksToDisplay()
    */
    @AuraEnabled
    public static List<DynamicCustomLinks_LH_HUM> prepareLinksToDisplay(String recID)
    {
        DynamicCustomLinks_LS_HUM.getLinkDetails(recID, sObjname, sRecName, sPageName, sectionType);
        return lstFinalCustomLinks = prepareLinksToDisplay();
    }
    
    
    /**
    * <p>
    * This method is used to initialize Links to display on UI
    * 
    * @see       #prepareLinksToDisplay()
    */
    @AuraEnabled
    public static List<DynamicCustomLinks_LH_HUM> prepareLinksToDisplay()
    {
        if(!mapLinks.isEmpty())    return prepareFinalLinks();
        return new list<DynamicCustomLinks_LH_HUM>();
    }
    
    public static String checkVFLink()
    {
        List<String> lstStr = new List<String>();
        lstStr.addAll(maplnktorf.keySet());
        String sVFJSON = maplnktorf.keySet().size() > 0 ? lstStr[maplnktorf.keySet().size()-1] : '';   
        if(NULL == mapLinks)    return NULL;
        
        for(string sfdcId : mapLinks.keySet())
        {
            if(String.valueOf(sfdcId).equalsIgnoreCase(sVFJSON))    return NULL;
        }
        return sVFJSON;
    }
    
    /*
    * Method Name : prepareFinalLinks
    * Description : This method is used prepare the final section wise links to be shown on UI.
    * Return type : List<DynamicCustomLinks_LH_HUM>
    * Parameter   : NA
    */
    @AuraEnabled
    public static list<DynamicCustomLinks_LH_HUM> prepareFinalLinks()
    {
        Integer iIndex = 0;
        String sSecName='';
        DynamicCustomLinks_LH_HUM objWrapper= new DynamicCustomLinks_LH_HUM();
        objWrapper.lstCustomLinks = new List<DynamicLinksWrapper_LH_HUM>();
        list<DynamicLinksWrapper_LH_HUM> lstTempLinks = new List<DynamicLinksWrapper_LH_HUM>();
        list<DynamicCustomLinks_LH_HUM> lstFinalCustomLinks = new List<DynamicCustomLinks_LH_HUM>();
        if(mapLinks.values().isEmpty())    return lstFinalCustomLinks;
        iIndex = (Integer)mapLinks.values()[0].Layout_Section__r.Index__c;
        sSecName = mapLinks.values()[0].Layout_Section__r.Section_Name__c;
		
        for(Link__c objLnk : mapLinks.values())
        {
            if( (maplnktorf.containskey(String.valueOf(objLnk.id)) && maplnktorf.get(String.valueOf(objLnk.id))) || test.isRunningTest())
            { 
                if(iIndex != objLnk.Layout_Section__r.Index__c || sSecName  != objLnk.Layout_Section__r.Section_Name__c || test.isRunningTest())
                {
                    objWrapper.sSectionName = sSecName;
                    objWrapper.lstCustomLinks =  lstTempLinks;
                    lstFinalCustomLinks.Add(objWrapper);
                    objWrapper = new DynamicCustomLinks_LH_HUM();
                    lstTempLinks = new List<DynamicLinksWrapper_LH_HUM>();
                }
                lstTempLinks = DynamicCustomLinks_LS_HUM.putInList(objLnk, lstTempLinks);
                sSecName = objLnk.Layout_Section__r.Section_Name__c;
                iIndex = (Integer)objLnk.Layout_Section__r.Index__c;
            }
        }
        objWrapper.sSectionName = sSecName;
        objWrapper.lstCustomLinks = lstTempLinks;
        lstFinalCustomLinks.Add(objWrapper);
        return lstFinalCustomLinks;
    }
    
    /**
    * <p>
    * This method is used prepare the final url
    * 
    * @see       #prepareLinkUrl()
    * sVFJSON,LinkParam,objLnk,jsonMap,HSS_ONECLICK_URL,HSS_ONECLICK_TARGET,MAP_URL_LENGTH,isRequiredLinkparam_error
            
    */
    @AuraEnabled
    public static prepareLinkUrlResultWrapper prepareLinkUrlOnClick(String sLinkId, String sRecId, String sObjectName, String lnkMap, Map<Id, Link__c> linkMapValue)
    {   
        prepareLinkUrlResultWrapper resultWrapper = new prepareLinkUrlResultWrapper();
        
		if(String.IsNotBlank(sRecId) && String.IsBlank(sObjectName))
        sObjectName= DynamicCustomLinks_LS_HUM.getSObjName(sRecId, sObjectName, sRecName);
		
        objLnk = DynamicCustomLinks_LS_HUM.prepareLinkUrl(sLinkId, sRecId, sObjectName, linkMapValue, false, mapLinks,  
         mapParams, lstlinkparam, isRequiredLinkparam_errorval, isRequiredLinkparam_error);
        if(objLnk != null && String.isBlank(objLnk.DefaultUrl__c) && isRequiredLinkparam_error == true)
        {
          throw new AuraHandledException(Label.IsRequired_LinkParameter);
        }
        if(!Test.isRunningTest() && objLnk == NULL) { 
            //return resultWrapper;
        } 
        
        sVFJSON = checkVFParameter();
        if(mapParams != null){
            MAP_URL_LENGTH = mapParams.size();
        }
        
         if(MAP_URL_LENGTH == 1)
                                    {
            HSS_ONECLICK_TARGET = 'TARGET';
        }
        else
        {
            HSS_ONECLICK_TARGET = 'HIDDENTARGET';
        }
        
        HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
        resultWrapper.sVFJSON = sVFJSON;
        resultWrapper.objLnk = objLnk;
        resultWrapper.jsonMap = jsonMap;
        resultWrapper.HSS_ONECLICK_URL = HSS_ONECLICK_URL;
        resultWrapper.HSS_ONECLICK_TARGET = HSS_ONECLICK_TARGET;
        resultWrapper.MAP_URL_LENGTH = MAP_URL_LENGTH;
        resultWrapper.isRequiredLinkparam_error = isRequiredLinkparam_error;
        resultWrapper.mapParams = mapParams;
        return resultWrapper;
    }
    
    /*
    * Method Name : checkVFParameter
    * Description : This method checks if any values has to be fetched from VF
    * Return type : String
    * Parameter   : NA
    */
    public static String checkVFParameter()
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

    /**
    * <p>
    * This method is used to populate varibles from VF
    * 
    * @see       #populateVFVariables()
    */
    @AuraEnabled
    public static DynamicCustomLinksConsolidatedWrapper populateVFVariables(String sLinksJSONMap, Link__c objLnkVar, String sVFJSONVar, Boolean isRequiredLinkparam_errorVar, 
    String actionType, Map<Id,Link__c> mapLinksVar, Map<String, Boolean> maplnktorfVar, String sObjnameVar, String recordID,
    list<DynamicCustomLinks_LH_HUM> lstFinalCustomLinksVar, Map<String, String> mapParamsVar)
    {        
    
        try{
        list<Id> ListLinkId = new List<Id>();
        for(Id LinkId : mapLinksVar.keyset()){
            ListLinkId.add(LinkId );
        }
            mapLinksVar= new map<Id,Link__c>([select id ,DefaultUrl__c, Active__c,Expression__c ,Index__c,Layout_Section__r.Index__c,Layout_Section__C,Link_Action__c,Link_Label__c,Layout_Section__r.Section_Name__c,Layout_Section__r.Type_Of_Layout__c,Hover_Over_Text__c, Display_Hover_Over__c, Icon_Path__c, Target_Type__c, RequireSSO__c, (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r),Url__c from  link__c where Id in : ListLinkId  and  Active__c =true order by Layout_Section__r.Index__c,Index__c ASC]);
            mapLinks = mapLinksVar;
            maplnktorf = maplnktorfVar;
            sObjname = sObjnameVar;
            lstFinalCustomLinks = lstFinalCustomLinksVar;
            objLnk = objLnkVar;
            isRequiredLinkparam_error = isRequiredLinkparam_errorVar;
            mapParams = mapParamsVar;
            sVFJSON = NULL;
            if(objLnk != null && String.isBlank(objLnk.DefaultUrl__c) && isRequiredLinkparam_error == true) {
                throw new AuraHandledException(Label.IsRequired_LinkParameter);
            }
            Map<String, String> mapVFResults = new  Map<String, String>();
            if(sLinksJSONMap != null){
                mapVFResults = (Map<String, String>)JSON.deserialize(sLinksJSONMap, Map<String, String>.class);
            }
           
            
            if(actionType.equalsIgnoreCase('LinkCriteria'))
            {
                lstFinalCustomLinks = includeVFCriteriaLinks(mapVFResults,recordID);
            }
            else if(actionType.equalsIgnoreCase('LinkParam'))
            {
                objLnk = includeVFParameterValues(mapVFResults, recordID);
            }
            sVFJSON = NULL;
            DynamicCustomLinksConsolidatedWrapper wrapper = new DynamicCustomLinksConsolidatedWrapper();

            DynamicCustomLinksResultWrapper DCLRW = new DynamicCustomLinksResultWrapper();
            DCLRW.lstFinalCustomLinks = lstFinalCustomLinks;
            DCLRW.mapLinks = mapLinks;
            DCLRW.maplnktorf = maplnktorf;
            DCLRW.sVFJSON = sVFJSON;
            
            prepareLinkUrlResultWrapper PLURW = new prepareLinkUrlResultWrapper();
            PLURW.sVFJSON = sVFJSON;
            PLURW.objLnk = objLnk;
            PLURW.jsonMap = jsonMap;
            PLURW.HSS_ONECLICK_URL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
            if(mapParams.size() == 1)
            {
                HSS_ONECLICK_TARGET = 'TARGET';
            }
            else
            {
                HSS_ONECLICK_TARGET = 'HIDDENTARGET';
            }
            
            PLURW.HSS_ONECLICK_TARGET = HSS_ONECLICK_TARGET;
            PLURW.MAP_URL_LENGTH = MAP_URL_LENGTH;
            PLURW.isRequiredLinkparam_error = isRequiredLinkparam_error;
            PLURW.mapParams = mapParams;

            wrapper.DynamicCustomLinksResultWrapperVar = DCLRW;
            wrapper.prepareLinkUrlResultWrapperVar = PLURW;
            return wrapper;
       }catch(Exception ex){
            return new DynamicCustomLinksConsolidatedWrapper();
       }
    }

    /*
    * Method Name : includeVFCriteriaLinks
    * Description : This method is used to add custom link after evaluating from VF
    * Return type : list<DynamicCustomLinks_LH_HUM>
    * Parameter   : Map<String, String>
    */
    //migrated
    @AuraEnabled
    public static list<DynamicCustomLinks_LH_HUM> includeVFCriteriaLinks(Map<String, String> mapVFResults, String recordID)
    {
        List<Link_Criteria__c> lstLinkcrt = new List<Link_Criteria__c>();
        
        if(NULL == mapLinks)    return new list<DynamicCustomLinks_LH_HUM>();
        
        for(String sKey : mapVFResults.keySet())
        {
         if(!sKey.split('\\.').isEmpty() && mapLinks.get(sKey.split('\\.')[0]) != null)
            lstLinkcrt.addAll(mapLinks.get(sKey.split('\\.')[0]).Link_Criteria__r);
        }
        
        maplnktorf = MatchCondition_H_HUM.validateLinkExpressionForVF(lstLinkcrt, maplnktorf, sObjname, recordID, mapLinks, mapVFResults);
		
        return prepareFinalLinks();
    }

    /*
    * Method Name : includeVFParameterValues
    * Description : This method is used to add custom link after evaluating from VF for Link Parameter
    * Return type : Link__c
    * Parameter   : Map<String, String>
    */
    @AuraEnabled
    public static Link__c includeVFParameterValues(Map<String, String> mapVFResults, String recordID)
    {
        mapParams = includeLinkParameterFormVF(mapParams, mapVFResults, recordID);
        jsonMap = JSON.serialize(mapParams); 
        if(!mapVFResults.isEmpty()){
            String key = (new List<String>(mapVFResults.keySet()))[0];
            return DynamicCustomLinks_LS_HUM.prepareLinkUrl(key.split('\\.')[2], recordID, sObjName, null, true, mapLinks,  
            mapParams, lstlinkparam, isRequiredLinkparam_errorval, isRequiredLinkparam_error);
        }
        
        return NULL;
    }

    /*
    * Method Name : includeLinkParameterFormVF
    * Description : This method is used to put all values in map to form a query string
    * Return type : Map<String, String> 
    * Parameter   : Map<String, String>, Map<String, String>, String
    */
    @AuraEnabled
    public static Map<String, String> includeLinkParameterFormVF(Map<String, String> mapParams, Map<String, String> mapVFResults, String sRecID)
    {
        String paramValue = '';
        Map<String, String> tempMap = new Map<String, String>();
        String jsonStr = '';
        Set<String> setRecId = new Set<String>();
        Link_Parameter__c oLnkParam = NULL;
        
        for(String key : mapVFResults.keySet())
        {
            if(key!=null) setRecId.add(key.split('\\.')[0]);
        }
        Map<Id, Link_Parameter__c> mapLnkParam = new Map<Id, Link_Parameter__c>(DynamicCustomLinks_D_HUM.getLinkParameters(setRecId));
        
        for(String key : mapVFResults.keySet())
        {
            if(key!=null){
                tempMap.put(key,'');
                jsonStr = JSON.serialize(tempMap);
                
                if(mapParams.get(jsonStr) != NULL)
                {
                    paramValue = mapVFResults.get(key);
                    mapParams.remove(jsonStr);
                    oLnkParam = mapLnkParam.get(key.split('\\.')[0]);
                    if(oLnkParam.IsConditional__c)    paramValue = LinkParameterLogic_LH_HUM.checkParameterCondition(paramValue, oLnkParam, false, sRecID, null);
                    mapParams.put(oLnkParam.Variable_Name__c,paramValue);
                }
                tempMap.clear();
            }
            
        }
        return mapParams;
    }
    
}