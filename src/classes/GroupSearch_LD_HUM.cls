/*******************************************************************************************************************************
Apex Class Name : GroupSearch_LD_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is data class for GroupSearch_LC_HUM
Test Class      : GroupSearch_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	10/19/2020                	Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
*    3.0           Vardhman Jain                                    09/27/2022                  US3429402 - Home Office/ Contract Protected Data Changes
*    4.0           Santhi Mandava                                   03/06/2024                  User Story 5312358: T1PRJ0891742 - C01- Consumer Mgt - Ltng- VIT Remediation Search pages and unknown Group Creation    
*******************************************************************************************************************************************************************/
public without sharing class GroupSearch_LD_HUM 
{
    public static String sQry = '';
    public static List<Account> lstAccountList;
    public static List<Account> requestGroupAccountListBySOSL(GroupSearchInputWrapper_LH_HUM oGroupSearchInputWraper, String sGroupNumberCondition)
    {	
        sQry='';
        lstAccountList = new List<Account>();
        String sGroup_Name = oGroupSearchInputWraper.sGroupName + '*';
       
        sQry = 'FIND \'' + string.escapeSingleQuotes(sGroup_Name) + '\' IN NAME FIELDS RETURNING Account(Id, FirstName,Home_Office_Account__c,ETL_Record_Deleted__c, RecordType.Name, Phone, BillingStreet, BillingCity, BillingState,BillingCountry,BillingPostalCode, Group_Number__c,Source_Customer_Key__c,Source_Platform_Code__c, Name where ( RecordType.DeveloperName ='+'\''+ GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_UnknownGroup_Account_Record_Type+'\'';
        if(!oGroupSearchInputWraper.sUnknownGroupCheck)   sQry+=  ' OR RecordType.DeveloperName ='+'\''+GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_Group_Account_Record_Type+'\'';
        sQry+= ' ) ';
        if(!(String.isBlank(oGroupSearchInputWraper.sGroupNumber))){sQry += ' AND ' +sGroupNumberCondition+'='+'\''+ string.escapeSingleQuotes(oGroupSearchInputWraper.sGroupNumber)+'\'';}
        if(!(String.isBlank(oGroupSearchInputWraper.sState)) && oGroupSearchInputWraper.sState != GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue)
        {
            sQry += ' AND BillingStateCode = \'' +string.escapeSingleQuotes(oGroupSearchInputWraper.sState) + '\'';
        }
        sQry +=  ' ORDER BY RecordType.Name ASC Limit 250)';
        
        List<List<sObject>> searchList=search.query(sQry);
        for(Account oAcc :(List<Account>) searchList[0])
        {
            if(String.IsBlank(oAcc.Name) || (String.IsNotBlank(oAcc.Name) && oAcc.Name.startsWithIgnoreCase(oGroupSearchInputWraper.sGroupName)))
            {   
                lstAccountList.add(oAcc);
            }
            if(lstAccountList.size() == (Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c) -1 )) break;
        }
        return lstAccountList;
    }
    
    public static List<Account> requestGroupAccountListBySOQL(GroupSearchInputWrapper_LH_HUM oGroupSearchInputWraper, String sGroupSourceCustomerKeyCondition)
    {	
        sQry='';
        lstAccountList = new List<Account>();
        String unknownGroup = GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_UnknownGroup_Account_Record_Type;
        String groupAccount = GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_Group_Account_Record_Type;
        
        sQry = 'Select Id, FirstName,Home_Office_Account__c,Source_Customer_Key__c, RecordType.Name, Phone, BillingStreet, BillingCity,ETL_Record_Deleted__c,BillingState, BillingCountry,BillingPostalCode, Group_Number__c,'; 
        sQry += 'Source_Platform_Code__c,UserRecordAccess.HasReadAccess, Name from Account where (RecordType.DeveloperName =:unknownGroup';
        if(!oGroupSearchInputWraper.sUnknownGroupCheck) sQry+=  ' OR RecordType.DeveloperName =:groupAccount';
        sQry+= ' ) ';
        if(!(String.isBlank(oGroupSearchInputWraper.sGroupNumber))){
            String sGroupNumber = string.escapeSingleQuotes(oGroupSearchInputWraper.sGroupNumber)+'%';
            sQry += ' AND '+ sGroupSourceCustomerKeyCondition+'  LIKE :sGroupNumber';
        }
        
        if(!(String.isBlank(oGroupSearchInputWraper.sState)) && oGroupSearchInputWraper.sState != GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue)
        {
            String sBillingStateCode = string.escapeSingleQuotes(oGroupSearchInputWraper.sState);
            sQry += ' AND BillingStateCode = :sBillingStateCode';
        }
        Integer searchLimit = Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c);
        sQry += ' ORDER BY RecordType.Name ASC Limit :searchLimit';  
        lstAccountList = Database.Query(sQry);
        return lstAccountList;
    }
	 /*
    * Method Name   :    dataSecurity
    * Description   :    This Method is for Security purpose. Used to display fewer records to External users
    * Return Type   :    void
    * Parameters    :    List<Account>
    */

    public static List<Account> dataSecurity(List<Account> lstGroupAccount)
    {
        List <ID> lstAccountList = new List<ID>();
        Map<Id, Boolean> mapRecordHaveAccess = new Map<Id, Boolean>();
        for(Integer i = 0 ; i < lstGroupAccount.size() ; i++)
        {
            lstAccountList.add(lstGroupAccount.get(i).Id);
        }
        List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : lstAccountList];
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            mapRecordHaveAccess.put(userAccess.RECORDID, userAccess.HASREADACCESS);
        }    

        GroupSearch_LC_HUM.objGrpSerResponse.mapRecordAccess =  mapRecordHaveAccess;
        List<Account> lstFinalSearchResult = new List<Account>();
        lstFinalSearchResult.addAll(lstGroupAccount); 
        
		return lstFinalSearchResult;
	} 
}