/*******************************************************************************************************************************************************
Apex Class Name  : MatchCondition_H_HUM
Version          : 1.0
Created Date     : 1/12/2016
Function         : This is helper class to match the conditions
Test Class       : MatchCondition_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar              20725                    1/12/2016                 Original Version
* Manish Kumar                                       1/27/2016                 Added ability for Links to read content from Visualforce
* Anurag Shah                                        03/13/2018				   REQ - 354546:: SF - USER - Research/Attachments - Claim Detail Links Panel.
* Prerna Prasad             222286					 03/28/2016                REQ - 357293 - Link criteria for "Not Contains" option
* Pradeep Dani				246226					 06/20/2016                Adding "Not In" functionality to add multple values in expected value.
* Sagar MN											 06/26/2019				   REQ - 366925 - Link criteria for "Not Contains"  Permission set option
* Anuradha Gajbhe   								 07/02/2021				   US - 2373122 - MF 6 - SF - Tech - Performance Tuning - Claims Detail Pages and IE and Edge Browsers.
* Suraj patil   								 	 07/16/2021				   US - 2365235 - T1PRJ0003805 - MF SF: TECH Linkout objects Picklist value for Lightning
* Prashant Moghe									 01/31/2022					Build Issue Fix 
* Tharun Madishetti                                  06/06/2022                US-2690713:T1DMND0013600 -4.1 Ability to Not show the claims link for any products assigned to the Dominion Network
***********************************************************************************************************************************************/

public class MatchCondition_H_HUM
{
	private static final string LINKCRITERIA_NOTIN_HUM = 'Not In';
    private Static final String LINKCRITERIA_NOTCONTAINS_HUM = 'Not Contains';
    private Static final String EQUALS = 'Equals';
    private Static final String NOT_EQUALS = 'Not Equals';
    private Static final String CONTAINS = 'Contains';
    private Static final String STARTS_WITH = 'Starts With';
    private Static final String DOES_NOT_START_WITH = 'Does Not Start With';
    private Static final String ENDS_WITH = 'Ends With';
    private Static final String SFDCQUERYEXCEPTION = 'Criteria source cannot query foreign key relationships more than 5 levels away from the root SObject';
    private static List<String> lstPermissionSet = new List<String>();
	/*
    * Method Name : matchCondition
    * Description : This method is used to match Condition
    * Return type : Map<string,boolean>
    * Parameter   : Link__c, sObject, String, String
    */
    @testVisible
    private static Map<string,boolean> matchCondition(Link__c objLnk, sObject objCriteriaFieldsData, String sObjname, String sUserProfile, Map<String, String> mapVFLink, Boolean bIsCallBack)
    {
        Map<string,boolean> mapLnkCrtev = new Map<string,boolean>();
        				
        for(Link_Criteria__c oLnkCrt : objLnk.Link_Criteria__r)
        {
            if((oLnkCrt.Criteria_Type__c.equals('Database Field')))
            {
                mapLnkCrtev.put(oLnkCrt.Name, matchConditionalValue(getDatabaseFieldCriteriaTypeValue(oLnkCrt, objCriteriaFieldsData), oLnkCrt.Expected_Value__c, oLnkCrt.Condition__c));
                continue;
            }
            else if(oLnkCrt.Criteria_Type__c.equals('VisualForce Page') || oLnkCrt.Criteria_Type__c.equals('Lightning Web Component'))
            {
                processVisualForceCriteriaTypeValue(objLnk, oLnkCrt, mapLnkCrtev, mapVFLink, bIsCallBack);
                continue;
            }
            else if((oLnkCrt.Criteria_Type__c.equals('User Profile') &&  matchConditionalValue(sUserProfile, oLnkCrt.Expected_Value__c, oLnkCrt.Condition__c))
                 || (oLnkCrt.Criteria_Type__c.equals('Permission Set') &&  checkUserPermissionSet(oLnkCrt.Expected_Value__c, oLnkCrt.Condition__c, getUserPermissionSet(lstPermissionSet)))
				 || (oLnkCrt.Criteria_Type__c.equals('App Name')) && matchConditionalValue(HUMUtilityHelper.GetSelectedAppName(), oLnkCrt.Expected_Value__c,oLnkCrt.Condition__c)
				  || (oLnkCrt.Criteria_Type__c.equals('Permission Set Group') &&  checkUserGroupPermissionSet(oLnkCrt.Expected_Value__c, oLnkCrt.Condition__c, getUserPermissionSet(lstPermissionSet))))
            {
                mapLnkCrtev.put(oLnkCrt.Name, true);
                continue;
            }
            mapLnkCrtev.put(oLnkCrt.Name, false);
        }
        
        return mapLnkCrtev;
    }
    
    /*
    * Method Name : processVisualForceCriteriaTypeValue
    * Description : This method is used to get value for VisualForce criteria type.
    * Return type : void
    * Parameter   : Link__c, Link_Criteria__c, Map<string, Boolean>, Map<String, String>, Boolean
    */
    @testVisible
    private static void processVisualForceCriteriaTypeValue(Link__c objLnk, Link_Criteria__c oLnkCrt, Map<string, Boolean> mapLnkCrtev, Map<String, String> mapVFLink, Boolean bIsCallBack)
    {
        if(!bIsCallBack)
        {
            populateMapsForVisualForce(objLnk.Id, oLnkCrt.Criteria_Source__c, mapLnkCrtev, mapVFLink);
        }
        else
        {
            mapLnkCrtev.put(oLnkCrt.Name, matchConditionalValue(mapVFLink.get(objLnk.Id + '.' + oLnkCrt.Criteria_Source__c), oLnkCrt.Expected_Value__c, oLnkCrt.Condition__c));
        }
    }
    
    /*
    * Method Name : populateMapsForVisualForce
    * Description : This method will populate maps for Visualforce Criteria type
    * Return type : void
    * Parameter   : ID, String, Map<string,boolean>, Map<String, String>
    */
    @testVisible
    private static void populateMapsForVisualForce(ID linkId, String sCriteria, Map<string,boolean> mapLnkCrtev, Map<String, String> mapVFLink)
    {
        mapVFLink.put(String.valueOf(linkId) + '.' + sCriteria, '');
        mapLnkCrtev.put('VisualForce Content', false);
    }
    
    /*
    * Method Name : getDatabaseFieldCriteriaTypeValue
    * Description : This method is used to get value for Database field type.
                    Note: Criteria source cannot query foreign key relationships more than 5 levels away from the root SObject
    * Return type : String
    * Parameter   : Link__c, sObject
    */
    public static String getDatabaseFieldCriteriaTypeValue(Link_Criteria__c oLnkCrt, sObject objCriteriaFieldsData)
    {
        SObject sobj = NULL;
        String sDBValue = '';
        
        list<String> lstObj = oLnkCrt.Criteria_Source__c.split('\\.');
        
        if(lstObj.size() == 1)
        {
            sDBValue = String.ValueOf(objCriteriaFieldsData.get(oLnkCrt.Criteria_Source__c));
        }
        else if(lstObj.size() >= 7)
        {
            throw new HUMCustomException(MatchCondition_H_HUM.SFDCQUERYEXCEPTION);
            //return '';
        }
        else
        {
            try {
                for(Integer i = 0 ; i < lstObj.size() - 1 ; i++)
                {
                    if(i == 0)
                    {
                        sobj = objCriteriaFieldsData.getSObject(lstObj[0]);
                        continue;
                    }
                    sobj = sobj.getSObject(lstObj[i]);
                }
                
                sDBValue = String.ValueOf(sobj.get(lstObj[lstObj.size()-1]));
            } catch (Exception ex) {
                return '';
            }
        }
        return sDBValue;
    }
    
    /*
    * Method Name : validateLinkExpression
    * Description : This method is used to validate the link expression.
    * Return type : Map<String,Boolean>
    * Parameter   : List<Link_Criteria__c>, Map<String, Boolean>, String, String, Map<Id, Link__c>
    */
    public static Map<String, Boolean> validateLinkExpression(List<Link_Criteria__c> lstLinkcrt, Map<String, Boolean> mapTempLnks, String sObjname, String recordID, Map<Id, Link__c> mapLinks)
    {
        DynamicCustomLinks_D_HUM objDataLayer = new DynamicCustomLinks_D_HUM();
        Map<String, String> mapVFLink = new Map<String, String>();
        map<string,boolean> mapLnkCrtev = NULL;
        Boolean bIsCriteriaMatch = false;
        
        if(!lstLinkcrt.isEmpty())
        {
            sObject objCriteriaFieldsData = objDataLayer.retrieveLinkCriteriaDetails(sObjname, recordID, lstLinkcrt);
            String sUserProfile = objDataLayer.getUserProfile();
            
            for(Link__c objLnk: mapLinks.values())
            {
                mapLnkCrtev = new Map<string,boolean>();
                if(objLnk.Expression__c != Null && objLnk.Link_Criteria__r != null && !objLnk.Link_Criteria__r.isEmpty())
                {
                    mapLnkCrtev = matchCondition(objLnk, objCriteriaFieldsData, sObjname, sUserProfile, mapVFLink, false);
                    
                    if(mapLnkCrtev.containsKey('VisualForce Content'))
                    {
                        mapTempLnks.Put(String.valueOf(objLnk.Id), false);
                        continue;
                    }
                    putInMap(objLnk, mapLnkCrtev, mapTempLnks);
                }
            }
            if(!mapVFLink.isEmpty())
            {
                String sVFJSON = JSON.serialize(mapVFLink);
                mapTempLnks.put(sVFJSON, false);
            }
        }
        return mapTempLnks;
    }
    
    /*
    * Method Name : validateLinkExpressionForVF
    * Description : This method is used to validate the link expression.
    * Return type : Map<String, Boolean>
    * Parameter   : List<Link_Criteria__c>, Map<ID, Boolean>, String, String, Map<Id, Link__c>, Map<String, String>
    */
    public static Map<String, Boolean> validateLinkExpressionForVF(List<Link_Criteria__c> lstLinkcrt, Map<String, Boolean> mapTempLnks, String sObjname, String recordID, Map<Id, Link__c> mapLinks, Map<String, String> mapVFResults)
    {
        DynamicCustomLinks_D_HUM objDataLayer = new DynamicCustomLinks_D_HUM();
        map<string,boolean> mapLnkCrtev = NULL;
        Link__c objLnk = NULL;
        
        if(!lstLinkcrt.isEmpty())
        {
            SObject objCriteriaFieldsData = objDataLayer.retrieveLinkCriteriaDetails(sObjname, recordID, lstLinkcrt);
            String sUserProfile = objDataLayer.getUserProfile();
            
            for(String sKey : mapVFResults.keySet())
            {
                objLnk = mapLinks.get(sKey.split('\\.')[0]);
                mapLnkCrtev = matchCondition(objLnk, objCriteriaFieldsData, sObjname, sUserProfile, mapVFResults, true);
                putInMap(objLnk, mapLnkCrtev, mapTempLnks);
            }
        }
        return mapTempLnks;
    }
    
    /*
    * Method Name : getUserPermissionSet
    * Description : This method is used to get list of User permission set.
    * Return type : List<String>
    * Parameter   : List<String>
    */
    @testVisible
    private static void putInMap(Link__c objLnk, Map<string,boolean> mapLnkCrtev, Map<String, Boolean> mapTempLnks)
    {
        if(mapLnkCrtev.size() > 1)
        {
            ExpressionEvaluate_H_HUM oExpEval = new ExpressionEvaluate_H_HUM();
            mapTempLnks.putall(oExpEval.evaluateExpression(objLnk, mapLnkCrtev));
        }
        else if(objLnk.Link_Criteria__r.size() == 1 && mapLnkCrtev.get(objLnk.Link_Criteria__r[0].Name)) 
        {
            mapTempLnks.Put(String.valueOf(objLnk.Link_Criteria__r[0].Link__c), true);
        }
    }
    
    /*
    * Method Name : getUserPermissionSet
    * Description : This method is used to get list of User permission set.
    * Return type : List<String>
    * Parameter   : List<String>
    */
    @testVisible
    private static List<String> getUserPermissionSet(List<String> lstPermissionSet)
    {
        if(!lstPermissionSet.isEmpty())    return lstPermissionSet;
        
        DynamicCustomLinks_D_HUM objDataLayer = new DynamicCustomLinks_D_HUM();
        User oUser = (User)objDataLayer.getUserPermissionSet(new Set<ID>{UserInfo.getUserId()})[0];
        
        for(PermissionSetAssignment oPSA : oUser.PermissionSetAssignments)
        {
            lstPermissionSet.add(oPSA.PermissionSet.Name);
        }
        return lstPermissionSet;
    }
    
    /*
    * Method Name : checkUserPermissionSet
    * Description : This method is used to validate the User permission set.
    * Return type : Boolean
    * Parameter   : String, String
    */
    @testVisible
    private static boolean checkUserPermissionSet(String sExpected_Value, String sCondition, List<String> lstPermissionSet)
    {
       set<String> setPermissionSet = new set<string>(lstPermissionSet);
       if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.EQUALS))
       {
         if(setPermissionSet.contains(sExpected_Value)) return true;
       }
       else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.NOT_EQUALS))
       {              
         if(!setPermissionSet.contains(sExpected_Value)) return true;
       }
          
       return false;
    }
    
	/*
    * Method Name : matchConditionalValue
    * Description : This method is used to match two string based on condition
    * Return type : Boolean
    * Parameter   : String, String, String
    */
    public static boolean matchConditionalValue(String sValue, String sExpected_Value, String sCondition)
    {
        If (String.isBlank(sValue) && !String.isBlank(sCondition)){
            // Allows the link framework to evaluate an empty sting as Not EQUAL to the expected value
            If (sCondition.equalsIgnoreCase(MatchCondition_H_HUM.NOT_EQUALS) && !String.isBlank(sExpected_Value)){
                Return True;
            } else{
                Return False;
            }
        } else if(String.isBlank(sValue) || String.isBlank(sCondition)) {
            Return False;
        }
        
        sCondition = sCondition.trim();
        sValue = sValue.trim();
        
        if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.EQUALS))
        {
            if(sValue.equalsIgnoreCase(sExpected_Value))    return true;
        }
        else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.NOT_EQUALS))
        {
            if(!sValue.equalsIgnoreCase(sExpected_Value))    return true;
        }
        else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.CONTAINS))
        {
            if(sValue.containsIgnoreCase(sExpected_Value))    return true;
        }
        else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.STARTS_WITH))
        {
            if(sValue.startsWithIgnoreCase(sExpected_Value))    return true;
        }
        else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.DOES_NOT_START_WITH))
        {
            if(!sValue.startsWithIgnoreCase(sExpected_Value))    return true;
        }
        else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.ENDS_WITH))
        {
            if(sValue.endsWithIgnoreCase(sExpected_Value))    return true;
        }
		else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.LINKCRITERIA_NOTCONTAINS_HUM))
        {
               if(!sValue.containsIgnoreCase(sExpected_Value))    return true;
        }
		else if (sCondition.equalsIgnoreCase(MatchCondition_H_HUM.LINKCRITERIA_NOTIN_HUM))
		{
			if (!(new set<String>(sExpected_Value.split('\\,'))).contains(sValue))    return true;
		}
        return false;
    }
	
	 /*
    * Method Name : checkUserGroupPermissionSet
    * Description : This method is used to validate the multiple User permission set assigned to user
    * Return type : Boolean
    * Parameter   : String, String, string
    */
    @testVisible
    private static boolean checkUserGroupPermissionSet(String sExpected_Value, String sCondition, List<String> lstPermissionSet)
    {
       set<String> setPermissionSet = new set<string>(lstPermissionSet);

       if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.EQUALS))
       {
		   List<String> lstExpectedPermissionSet;
           if(string.isnotblank(sExpected_Value)) lstExpectedPermissionSet = sExpected_Value.split(',');
           Boolean UserHasPermission = false;
           for(string ostr : lstExpectedPermissionSet){
               if(setPermissionSet.contains(ostr)) UserHasPermission = true;
               else return false;
           }
           return UserHasPermission;
       }
       else if(sCondition.equalsIgnoreCase(MatchCondition_H_HUM.NOT_EQUALS))
       {              
           List<String> lstExpectedPermissionSet;
		   if(string.isnotblank(sExpected_Value))lstExpectedPermissionSet = sExpected_Value.split(',');
           Boolean UserHasNoPermission = false;
           for(string ostr : lstExpectedPermissionSet){
               if(!setPermissionSet.contains(ostr)) UserHasNoPermission = true;
               else return false;
           }
           return UserHasNoPermission;
       }
       return false;
    }
}