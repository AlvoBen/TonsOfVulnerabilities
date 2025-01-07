/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_D_HUM
Version          : 1.0
Created Date     : 10/05/2015
Function         : This is data layer class for controller DynamicCustomLinks_C_HUM
Test Class       : DynamicCustomLinks_DT_HUM (As per the standards, full coverage is done from DynamicCustomLinks_T_HUM)
*****************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Sruthi Adivikolanu        21231                    10/05/2015                 Original Version
* Manish Kumar                                       04/18/2016                 Added methods for various functionality(LInk logic)
* Kritika Popat             38698                    09/08/2016                 REQ for Enable/Disable URL encoding
* Muralidhar Kollu                                   01/24/2017                 Horizontal Links section as part of REQ - 290968 - Go365 Details page Floating Navigation Links 
* Anupama Tavva             78689                    03/22/2016                 Added isRequired field for Link paramter for the REQ-304159
* Anupama Tavva             85516                    04/18/2017                 Added DefaultUrl field for Link Object for the REQ-304996
* Prasanthi Kandula         102588                   06/16/2017                 REQ - 318478 Ordering the results returned for link conditional parameters using priority field
* Lakshmi Madduri                                    03/19/2021                 US-1862825 SonarQube Fixes
* Prasanthi Kandula                                  04/21/2021                 US-1895204 Checkmarx low security fixes
* Vinay Lingegowda				                     02/07/2022		            User Story 2977662	T1PRJ0314829 - MF 1 - AHT - Ability to navigate to most commonly used pages in CRM (RF)
************************************************************************************************************************************************/

public with sharing class DynamicCustomLinks_D_HUM
{
    public String sectionType = '';

   /**
    * <p>
    * This method is used to get Link Layout details
    * 
    * @param     sObjname   the String which specifies object name
    * @param     sRecName   the String which specifies record name
    * @param     sPageName   the String which specifies page name
    * @return    Map<Id, Link__c>, map of Links
    * @see       #getLinkLayoutDetails()
    */
    public Map<Id, Link__c> getLinkLayoutDetails(String sObjname, String sRecName, String sPageName)
    {
        Set<Id> setSectionIds = NULL;
        Map<Id,Link_Layout_Section__c> mapLinkLayoutSections = NULL;
        
        if(String.isBlank(sPageName))
        {
            mapLinkLayoutSections = new Map<Id,Link_Layout_Section__c>([select id,Index__c,Section_Name__c,Link_Layout__c from  Link_Layout_Section__c 
                where (Link_Layout__r.Target_Name__c =: sObjname OR Link_Layout__r.Target_Name__c =: sObjname+'.'+sRecName) and  Link_Layout__r.Active__c =true]);
        }
        else
        {
            mapLinkLayoutSections = new Map<Id,Link_Layout_Section__c>([select id,Index__c,Section_Name__c,Link_Layout__c from  Link_Layout_Section__c 
                                                     where (Link_Layout__r.Target_Name__c =: sObjname+'.'+sPageName OR Link_Layout__r.Target_Name__c =: sPageName+'.'+sRecName) and  Link_Layout__r.Active__c =true]);
        }
        setSectionIds = mapLinkLayoutSections.keyset();
        map<Id,Link__c> mapLinks;
        
        if(String.isBlank(sectionType))
        {
             mapLinks  = new map<Id,Link__c>([select id ,DefaultUrl__c, Active__c,Expression__c ,Index__c,Layout_Section__r.Index__c,Layout_Section__C,Link_Action__c,Link_Label__c,Layout_Section__r.Section_Name__c,
                        Hover_Over_Text__c, Display_Hover_Over__c, Icon_Path__c, Target_Type__c, RequireSSO__c, (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r),Url__c from  link__c where Layout_Section__C in : setSectionIds  and  Active__c =true order by Layout_Section__r.Index__c,Index__c ASC]);
        }
        else
        {       
            mapLinks  = new map<Id,Link__c>([select id ,DefaultUrl__c, Active__c,Expression__c ,Index__c,Layout_Section__r.Index__c,Layout_Section__C,Link_Action__c,Link_Label__c,Layout_Section__r.Section_Name__c,
                        Hover_Over_Text__c, Display_Hover_Over__c, Icon_Path__c, Target_Type__c, RequireSSO__c, (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r),Url__c from  link__c where Layout_Section__C in : setSectionIds  and  Active__c =true and Layout_Section__r.Type_Of_Layout__c =: sectionType order by Layout_Section__r.Index__c,Index__c ASC]);        
        }

        return mapLinks;
    }
    
    /**
    * <p>
    * This method is used retrieve Link_Criteria details
    * 
    * @param     sObjname   the String which specifies object name
    * @param     recID   the String which specifies record Id
    * @param     lstlinkcrt   the List<Link_Criteria__c> which contains list of criterias for all link
    * @return    sObject, query results for Criterias for specified link
    * @see       #RetrieveLinkCriteriaDetails()
    */
    public sObject RetrieveLinkCriteriaDetails(String sObjname, String recID, List<Link_Criteria__c> lstlinkcrt)
    {
        String sQuery = 'Select ';
        
        boolean blncrt = false;
        Set<String> fieldsSet = new Set<String>();
        
        for(Link_Criteria__c oLnkcrt : lstlinkcrt)
        {
            if(oLnkcrt.Criteria_Type__c == 'Database Field' && string.isNotBlank(oLnkcrt.Criteria_Source__c))
            {
                oLnkcrt.Criteria_Source__c = oLnkcrt.Criteria_Source__c.toUpperCase('en');
                fieldsSet.add(oLnkcrt.Criteria_Source__c);
            }
        }
        
        if(fieldsSet != null && !fieldsSet.isEmpty())
        {
            for(String flds : fieldsSet)
            {
                if(flds.split('\\.').size() < 7)
                {
                    sQuery += flds + ',';
                    blncrt = true;
                }
            }
            
            sQuery = sQuery.removeEnd(',');
            sQuery += ' from ' + sObjname + ' Where Id =: recID';
        } 
        if(blncrt){
            sQuery = String.escapeSingleQuotes(sQuery);
            return Database.query(sQuery);
        }
            
        else
        return Null;  
    }
    
    /**
    * <p>
    * This method is used retrieve Link parameters
    * 
    * @param     linkid   the String which denotes LinkId
    * @return    List<Link_Parameter__c>, List of retrieved link parameters
    * @see       #retrieveLinkParameterFields()
    */
    public List<Link_Parameter__c> retrieveLinkParameterFields(String linkid)
    {
        return (List<Link_Parameter__c>)[select Link__c,Encode__c, IsRequired__c,IsConditional__c,Variable_Name__c,Variable_Source__c,Variable_Type__c,Key_Name__c,(select Encode__c, Conditional_Check__c,Conditional_Type__c,Conditional_Value__c, Variable_Type__c from Link_Conditional_Parameters__r order by Priority__c ASC) from Link_Parameter__c where Link__c =: String.escapeSingleQuotes(linkid)];
    }
    /**
    * <p>
    * This method is used retrieve Link parameters
    * 
    * @param     linkid   the String which denotes LinkId
    * @return    List<Link_Parameter__c>, List of retrieved link parameters
    */
    public static List<Link_Parameter__c> retrieveLinkParameters(String linkid)
    {
        return (List<Link_Parameter__c>)[select Link__c,Encode__c, IsConditional__c,Variable_Name__c,Variable_Source__c,Variable_Type__c,Key_Name__c,(select Encode__c, Conditional_Check__c,Conditional_Type__c,Conditional_Value__c, Variable_Type__c from Link_Conditional_Parameters__r order by Conditional_Type__c ASC) from Link_Parameter__c where Link__c =: String.escapeSingleQuotes(linkid)];
    }
    
    /**
    * <p>
    * This method is used retrieve records for child objects from Link Parameters
    * 
    * @param     SObjectName   the String for object name
    * @param     oLnkCondParam   the Link_Conditional_Parameter__c which holds conditional record for parameter
    * @param     recordID   the String for record ID
    * @param     sKeyName   the String for key field for child object
    * @param     objResult   the sObject result
    * @return    String Conditional value from child collection
    */
    public String getChildCollection(String SObjectName, Link_Conditional_Parameter__c oLnkCondParam, ID recordID, String sKeyName, Boolean bIsLooseRelation, sObject objResult)
    {
        String sQuery = '';
        if(!String.isBlank(SObjectName) && !String.isBlank(oLnkCondParam.Conditional_Value__c))
        {
            sQuery = 'Select ' + oLnkCondParam.Conditional_Value__c + ' From ' + SObjectName;
            sQuery += ' WHERE ';
            if(bIsLooseRelation)
                        {
                oLnkCondParam.Conditional_Check__c = oLnkCondParam.Conditional_Check__c.replaceFirst('#', '\'' + objResult.get(sKeyName) + '\'');
                sQuery += oLnkCondParam.Conditional_Check__c;
            }
            else
            {
                sQuery += oLnkCondParam.Conditional_Check__c;
                sQuery += ' AND ' + sKeyName + ' =:recordID';
            }
            if(oLnkCondParam.Conditional_Check__c.contains('('))
            {
                sQuery += ')';
            }
            sQuery += ' Limit 1';

            List<SObject> lstResult = Database.Query(sQuery);
            if(!lstResult.isEmpty())    return (String)lstResult[0].get(oLnkCondParam.Conditional_Value__c);
        }
        return NULL;
    }
    
    /**
    * <p>
    * This method is used to retrieve fields from given sObject
    * 
    * @param     lstlinkparam   the List<Link_Parameter__c> which stores link params
    * @param     sObjname   the String which stores object name
    * @param     recID   the String which stores ecord Id
    * @return    List<Link_Parameter__c>, List of retrieved link parameters
    * @see       #getLinkQueryStringData()
    */
    public sObject getLinkQueryStringData(List<Link_Parameter__c> lstlinkparam, String sobjname, String recID)
    {
        String sQuery = 'Select ';
         boolean bIsDBFieldExist = false;
        for(Link_Parameter__c oLnkparam : lstlinkparam)
        {
            if(oLnkparam.Variable_Type__c == 'Database Field')
            {
                if(!sQuery.containsIgnoreCase(oLnkparam.Variable_Source__c))
                sQuery += oLnkparam.Variable_Source__c + ',';
                
                bIsDBFieldExist = true;
            }
            else if(oLnkparam.Variable_Type__c == 'Query By')
            {
                sQuery += oLnkparam.Key_Name__c + ',';
                bIsDBFieldExist = true;
            }
        }
        if(bIsDBFieldExist)
        {
            if(sQuery != Null) sQuery = sQuery.removeEnd(',');
            sQuery += ' From ' + sobjname + ' Where Id =: recID';
            sQuery = String.escapeSingleQuotes(sQuery);
            return Database.query(sQuery);
        }
        else
        {
            return Null;
        }    
    }
    
    /**
    * <p>
    * This method is used get Profile details
    * 
    * @return    String, User name for current logged in user
    * @see       #getUserProfile()
    */
    public String getUserProfile()
    {
        return [Select Name from Profile Where Id =: UserInfo.getProfileId()].Name; 
    }
    
    /**
    * <p>
    * This method is used get Permission set details
    * 
    * @return    List<SObject>, User object for current logged in user
    */
    public List<SObject> getUserPermissionSet(Set<ID> idSet)
    {
        return [Select Id,Network_User_Id__c, (Select Id,PermissionSet.Name From PermissionSetAssignments) From User Where Id IN : idSet];
    }
    
    /**
    * <p>
    * This method is used get Profile details
    * 
    * @param     sObjName the String which stores Object name
    * @param     sRecId the String which stores record ID
    * @return    sObject, Returns dynamic queried results
    * @see       #getSObjectRecordType()
    */
    public sObject getSObjectRecordType(String sObjName, String sRecID)
    {
        String sQuery = 'Select RecordtypeId From ' + sObjName + ' Where Id =: sRecID';
        sQuery = String.escapeSingleQuotes(sQuery);
        return database.query(sQuery);
    }
    
    /**
    * <p>
    * This method is used get Profile details
    * 
    * @param     sRecId the String which stores record ID
    * @return    List<Link_Parameter__c>, Returns dynamic queried results
    * @see       #getLinkParameters()
    */
    public static List<Link_Parameter__c> getLinkParameters(Set<String> setRecID)
    {
        return (List<Link_Parameter__c>)[select Link__c,Encode__c, IsConditional__c,Variable_Name__c,Variable_Source__c,Variable_Type__c,Key_Name__c,(select Encode__c, Conditional_Check__c,Conditional_Type__c,Conditional_Value__c, Variable_Type__c from Link_Conditional_Parameters__r order by Conditional_Type__c ASC) from Link_Parameter__c where Id IN : setRecID];
    }
    
    /**
    * <p>
    * This method is used get Link details having Is dropdown checked
    * @param String : Current Page name    
    * @return    List<Link__c>, Returns links
    * @see       #getDropdownLinks()
    */  
    public Map<id, Link__c> getDropdownLinks(String sCurrentPageName) {
        String sExecludePage = '%' + (String.isNotBlank(sCurrentPageName) ? sCurrentPageName : 'DisplayCustomLinks_VF_HUM');
        Map<id, Link__c> mapDropdownLinks = new map<id,Link__c>([select id ,DefaultUrl__c, Active__c,Expression__c ,Index__c,Layout_Section__r.Index__c,Layout_Section__C,Link_Action__c,Link_Label__c,Layout_Section__r.Section_Name__c,
                                Hover_Over_Text__c, Display_Hover_Over__c, Icon_Path__c, Target_Type__c, RequireSSO__c, 
                                (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r),Url__c,Is_Dropdown_Value__c 
                                from Link__c where Active__c =true and Is_Dropdown_Value__c = true and Layout_Section__r.Link_Layout__r.Target_Name__c = 'Policy_Member__c' and (NOT URL__C LIKE : sExecludePage) 
                                order by link_label__c asc]);
        return mapDropdownLinks;
    }
}