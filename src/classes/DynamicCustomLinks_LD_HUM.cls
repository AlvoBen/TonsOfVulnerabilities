/*******************************************************************************************************************************************************
Apex Class Name  : DynamicCustomLinks_LD_HUM
Version          : 1.0
Created Date     : 13/07/2021
Function         : This is data layer class for controller DynamicCustomLinks_LC_HUM
Test Class       : DynamicCustomLinks_LT_HUM
Code Coverage    : 85%
*****************************************************************************************************************************************************

Modification Log: 

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Ranadheer Alwal                                  07/13/2021                   Original Version
* Anuradha Gajbhe                                  05/05/2023                   US-3862189 : T1PRJ0865978 -CRM Salesforce Lightning/HealthCloud - Lightning - Core - TECH - Quicklinks Framework Updates.
************************************************************************************************************************************************/
public with sharing class DynamicCustomLinks_LD_HUM {
    /*
    * This method is used get Profile details
    * 
    * @param     sObjName the String which stores Object name
    * @param     sRecId the String which stores record ID
    * @return    sObject, Returns dynamic queried results
    * @see       #getSObjectRecordType()
    */
    @AuraEnabled
    public static sObject getSObjectRecordType(String sObjName, String sRecID)
    {
        String sQuery = 'Select RecordtypeId From ' + sObjName + ' Where Id =: sRecID';
		sQuery = String.escapeSingleQuotes(sQuery);
		return database.query(sQuery);
    }

    /*
    * Method Name : getLinkLayoutDetails
    * Description : This method is used to get link layout details based on selected object.
    * Return type :  Map<Id, Link__c>
    * Parameter   : String, String, String
    */
    public static Map<Id, Link__c> getLinkLayoutDetails(String sObjname, String sRecName, String sPageName, String sectionType)
    {		
        integer start = Limits.getCpuTime();
        Set<Id> setSectionIds = NULL;
        Map<Id,Link_Layout_Section__c> mapLinkLayoutSections = NULL;
        
        if(String.isBlank(sPageName))
        {			
            mapLinkLayoutSections = new Map<Id,Link_Layout_Section__c>([select id,Index__c,Section_Name__c,Link_Layout__c,Type_Of_Layout__c from  Link_Layout_Section__c 
                where (Link_Layout__r.Target_Name__c =: sObjname OR Link_Layout__r.Target_Name__c =: sObjname+'.'+sRecName) and  Link_Layout__r.Active__c =true]);
        }
        else
        {		
            mapLinkLayoutSections = new Map<Id,Link_Layout_Section__c>([select id,Index__c,Section_Name__c,Link_Layout__c,Type_Of_Layout__c from  Link_Layout_Section__c 
                                                     where (Link_Layout__r.Target_Name__c =: sObjname+'.'+sPageName OR Link_Layout__r.Target_Name__c =: sPageName OR Link_Layout__r.Target_Name__c =: sPageName+'.'+sRecName) and  Link_Layout__r.Active__c =true]);
        }
        setSectionIds = mapLinkLayoutSections.keyset();
        map<Id,Link__c> mapLinks;
        
        if(String.isBlank(sectionType))
        {
            mapLinks  = new map<Id,Link__c>([select id ,DefaultUrl__c, Active__c,Expression__c ,Index__c,Layout_Section__r.Index__c,Layout_Section__C,Link_Action__c,Link_Label__c,Layout_Section__r.Section_Name__c,Layout_Section__r.Type_Of_Layout__c,
                        Hover_Over_Text__c, Display_Hover_Over__c, Icon_Path__c, Target_Type__c, RCC_Link_Priority__c, PCC_Link_Priority__c, GBO_Link_Priority__c, CWP_Link_Priority__c, RequireSSO__c, (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r),Url__c from  link__c where Layout_Section__C in : setSectionIds  and  Active__c =true order by Layout_Section__r.Index__c,Index__c ASC]);				
        }
        else
        {    	
            mapLinks  = new map<Id,Link__c>([select id ,DefaultUrl__c, Active__c,Expression__c ,Index__c,Layout_Section__r.Index__c,Layout_Section__C,Link_Action__c,Link_Label__c,Layout_Section__r.Section_Name__c,Layout_Section__r.Type_Of_Layout__c,
                        Hover_Over_Text__c, Display_Hover_Over__c, Icon_Path__c, Target_Type__c, RCC_Link_Priority__c, PCC_Link_Priority__c, GBO_Link_Priority__c, CWP_Link_Priority__c, RequireSSO__c, (select Name,Link__c,Expected_Value__c,Criteria_Type__c,Criteria_Source__c, Condition__c from Link_Criteria__r),Url__c from  link__c where Layout_Section__C in : setSectionIds  and  Active__c =true and Layout_Section__r.Type_Of_Layout__c =: sectionType order by Layout_Section__r.Index__c,Index__c ASC]);        				
        }
        return mapLinks;
    }

    /**
    * <p>
    * This method is used retrieve Link parameters
    * 
    * @param     linkid   the String which denotes LinkId
    * @return    List<Link_Parameter__c>, List of retrieved link parameters
    * @see       #retrieveLinkParameterFields()
    */
    @AuraEnabled
    public static List<Link_Parameter__c> retrieveLinkParameterFields(String linkid)
    {
        return (List<Link_Parameter__c>)[select Link__c,Encode__c, IsRequired__c,IsConditional__c,Variable_Name__c,Variable_Source__c,Variable_Type__c,Key_Name__c,(select Encode__c, Conditional_Check__c,Conditional_Type__c,Conditional_Value__c, Variable_Type__c from Link_Conditional_Parameters__r order by Priority__c ASC) from Link_Parameter__c where Link__c =: String.escapeSingleQuotes(linkid)];

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
    @AuraEnabled
    public static sObject getLinkQueryStringData(List<Link_Parameter__c> lstlinkparam, String sobjname, String recID)
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
            return Database.query(String.escapeSingleQuotes(sQuery));
        }
        else
        {
            return Null;
        }    
    }

    /**
    * <p>
    * This method is used get Permission set details
    * 
    * @return    List<SObject>, User object for current logged in user
    */
    public static List<SObject> getUserPermissionSet(Set<ID> idSet)
    {
        return [Select Id,Network_User_Id__c, (Select Id,PermissionSet.Name From PermissionSetAssignments) From User Where Id IN : idSet];
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
    public static String getChildCollection(String SObjectName, Link_Conditional_Parameter__c oLnkCondParam, ID recordID, String sKeyName, Boolean bIsLooseRelation, sObject objResult)
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
                sQuery += ' AND ' + sKeyName + ' = ' + '\'' + recordID + '\'';
            }
            if(oLnkCondParam.Conditional_Check__c.contains('('))
            {
                sQuery += ')';
            }
            sQuery += ' Limit 1';
            List<SObject> lstResult = Database.Query(sQuery);
           if(!lstResult.isEmpty()){
                if(oLnkCondParam.Conditional_Value__c.contains('.')){
                    string[] params = (oLnkCondParam.Conditional_Value__c).split('\\.');
                    if(params != null && params.size() >= 2)
                    	return (String)lstResult[0].getSObject(params[0]).get(params[1]);
                    else
                        return '';
                }else{
                    return (String)lstResult[0].get(oLnkCondParam.Conditional_Value__c);
                }
                
            }
        }
        return NULL;
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
}