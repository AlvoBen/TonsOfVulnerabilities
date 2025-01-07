/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_LS_HUM
Version          : 1.0
Created Date     : 13/07/2021
Function         : This is service layer class for controller DynamicCustomLinks_LC_HUM
Test Class       : DynamicCustomLinks_LT_HUM
Code Coverage    : 85%
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                                  07/13/2021                   Original Version
***********************************************************************************************************************************************/
public with sharing class DynamicCustomLinks_LS_HUM {
    public static final string FWD_SLASH = '/';
    /*
    * Method Name : setPageName
    * Description : This method is used to get Current page name
    * Return type : Id
    * Parameter   : String
    */
    public static String setPageName(String sParamPageName)
    {
        return sParamPageName == 'DisplayCustomLinks_VF_HUM' ? '' : sParamPageName;
    }

    /*
    * Method Name : getSObjName
    * Description : This method is used to retrieve sObject name for current recID
    * Return type : String
    * Parameter   : ID, String, String
    */
    @AuraEnabled
    public static String getSObjName(Id recordID, String sObjName, String sRecName)
    {
        Schema.SobjectType objType = recordID.getSobjectType();
        sObjName = objType.getDescribe().getName();
        map<Id,Schema.RecordTypeInfo> mapRtById = objType.getDescribe().getRecordTypeInfosById();
        
        if(mapRtById.size() > 1)
        {
            sobject lstquery = DynamicCustomLinks_LD_HUM.getSObjectRecordType(sObjName, recordID);
            
            DynamicCustomLinks_LC_HUM.sRecName = (string)mapRtById.get((String)lstquery.get('RecordTypeId')).getName();
        }
        return sObjName;
    }

    /*
    * Method Name : getLinkDetails
    * Description : This method is used to retrieve link records based on selected object.
    * Return type : void
    * Parameter   : String, String, String, String, String
    */
    @AuraEnabled
    public static void getLinkDetails(String recID, String sObjname, String sRecName, String sPageName, String sectionType)
    {
        try
        {
            map<String,Boolean> maplnkc = new Map<String, Boolean>();
            List<Link_Criteria__c> lstLinkcrt = new List<Link_Criteria__c>();
            DynamicCustomLinks_LC_HUM.mapLinks = DynamicCustomLinks_LD_HUM.getLinkLayoutDetails(sObjname, sRecName, sPageName, sectionType);
            for(Link__c objLnk: DynamicCustomLinks_LC_HUM.mapLinks.values())
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
            DynamicCustomLinks_LC_HUM.maplnktorf = MatchCondition_H_HUM.validateLinkExpression(lstLinkcrt, maplnkc, sObjname, recID, DynamicCustomLinks_LC_HUM.mapLinks);
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'DynamicCustomLinks_LS_HUM', 'getLinkDetails');
        }
    }

    /*
    * Method Name : putInList
    * Description : This method is used to add custom link in a List
    * Return type : list<DynamicLinksWrapper_LH_HUM>
    * Parameter   : Link__c, List<DynamicLinksWrapper_LH_HUM>
    */
    @AuraEnabled
    public static list<DynamicLinksWrapper_LH_HUM> putInList(Link__c objLnk, List<DynamicLinksWrapper_LH_HUM> lstTempLinks)
    {
        DynamicLinksWrapper_LH_HUM objTempLink = new DynamicLinksWrapper_LH_HUM();
        objTempLink.LinkID = objLnk.Id;
        objTempLink.Link = objLnk;
        objTempLink.LinkLabel= objLnk.Link_Label__c;
        lstTempLinks.Add(objTempLink);
        
        return lstTempLinks;
    }

    /*
    * Method Name : includeVFCriteriaLinks
    * Description : This method is used to add custom link after evaluating from VF
    * Return type : list<DynamicCustomLinks_LH_HUM>
    * Parameter   : Map<String, String>
    */
    @AuraEnabled
    public static list<DynamicCustomLinks_LH_HUM> includeVFCriteriaLinks(Map<String, String> mapVFResults, String recordID, String sObjname, Map<Id,Link__c> mapLinks, Map<String, Boolean> maplnktorf)
    {
        List<Link_Criteria__c> lstLinkcrt = new List<Link_Criteria__c>();
        
        if(NULL == mapLinks)    return new list<DynamicCustomLinks_LH_HUM>();
        
        for(String sKey : mapVFResults.keySet())
        {
         if(sKey !=null  && !sKey.split('\\.').isEmpty() && mapLinks.get(sKey.split('\\.')[0]) != null)
            lstLinkcrt.addAll(mapLinks.get(sKey.split('\\.')[0]).Link_Criteria__r);
        }
        
        maplnktorf = MatchCondition_H_HUM.validateLinkExpressionForVF(lstLinkcrt, maplnktorf, sObjname, recordID, mapLinks, mapVFResults);
        
        return DynamicCustomLinks_LC_HUM.prepareFinalLinks();
    }

    /*
    * Method Name : prepareLinkUrl
    * Description : This method is used prepare the final url
    * Return type : Link__c
    * Parameter   : String
    */
    @AuraEnabled
    public static Link__c prepareLinkUrl(String sLinkId, String sRecId, String objName,  Map<Id, Link__c> lnkMap, Boolean isCallBack,  Map<Id,Link__c> mapLinks,  
    Map<String, String> mapParams, List<Link_Parameter__c> lstlinkparam, Boolean isRequiredLinkparam_errorval, Boolean isRequiredLinkparam_error)
    {
        try
        {
            Link__c oLink = NULL;
            String sURL = '';
            
            if(!isCallBack)
            {
                DynamicCustomLinks_LC_HUM.mapLinks = lnkMap;
                DynamicCustomLinks_LC_HUM.lstlinkparam = DynamicCustomLinks_LD_HUM.retrieveLinkParameterFields(sLinkId);
                DynamicCustomLinks_LC_HUM.mapParams = LinkParameterLogic_LH_HUM.getQueryParameters(DynamicCustomLinks_LC_HUM.lstlinkparam, sRecId, objName, false, isRequiredLinkparam_errorval);
                DynamicCustomLinks_LC_HUM.isRequiredLinkparam_error = DynamicCustomLinks_LC_HUM.isRequiredLinkparam_errorval;
            }
            
            Link__c objTmplnk = DynamicCustomLinks_LC_HUM.mapLinks.get(sLinkId);
            
            if(NULL == DynamicCustomLinks_LC_HUM.mapParams)    return NULL;
            
            DynamicCustomLinks_LC_HUM.jsonMap = JSON.serialize(DynamicCustomLinks_LC_HUM.mapParams); 
            
            if(objTmplnk.Link_Action__c.equalsIgnoreCase('Query String'))
            {
                sUrl = objTmplnk.Url__c + '?';
                
                if(!DynamicCustomLinks_LC_HUM.mapParams.isEmpty())
                {
                   for(string obj : DynamicCustomLinks_LC_HUM.mapParams.keyset() )
                   {
                       sUrl += obj +'='+DynamicCustomLinks_LC_HUM.mapParams.get(obj)+'&';
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
            HUMExceptionHelper.logErrors(ex, 'DynamicCustomLinks_LS_HUM', 'prepareLinkUrl');
            return NULL;
        }
    }
     
    /*
    * Method Name : getCurrentPage
    * Description : This method is used to get current page name
    * Return type : String
    * Parameter   : N/A
    */
    @AuraEnabled
    public static String getCurrentPage()
    {
        String sUrl = URL.getCurrentRequestUrl().getPath();
        Integer iPos = sUrl.lastIndexOf(FWD_SLASH) + 1;
        if(!String.isBlank(sURL) && iPos > 0 && iPos < sUrl.length())    return sURL.substring(iPos, sUrl.length());
        return NULL;
    }
}