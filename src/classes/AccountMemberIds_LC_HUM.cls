/*******************************************************************************************************************************
Apex Class Name : AccountMemberIds_LC_HUM
Version         : 1.0
Created On      : 10/09/2021
Function        : This class is used for sending the Account fields data to AccountMemberIdsCardHum LWC 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ajay Chakradhar                                   09/27/2021               Original Version
**************************************************************************************************************************************************/

public class AccountMemberIds_LC_HUM { 
    
    /*
    * Method name : fetchAccountMemberIdData
    * Description : This method will return the Account object
    * Parameter   : acctid(id of current Account)
    */
    @AuraEnabled
    public Static Object fetchAccountMemberIdData(String acctid){
        Map<String,String> mpOfFieldApiVsLabel = new Map<String,String>();
        String accountQuery = 'Select id';
        ObjectVSFieldNameMapping__mdt[] fldMappingsData = MemberIdsCard_LD_HUM.queryAccountMemberIdCardData();
        for(ObjectVSFieldNameMapping__mdt objFldmaping : fldMappingsData){
            if(objFldmaping.ObjectTypeName__c==GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.ACCOUNT_OBJ){
                accountQuery += ', '+objFldmaping.FieldAPIName__c; 
                mpOfFieldApiVsLabel.put(objFldmaping.MasterLabel,objFldmaping.FieldAPIName__c);  
            }
        }
        accountQuery += ' From Account where id=:acctid';
        Account accnt = Database.query(accountQuery);
        List<MemberIdsCard_LS_HUM.MemberIdCardData> obj = (List<MemberIdsCard_LS_HUM.MemberIdCardData>) MemberIdsCard_LS_HUM.createMemberIdData(mpOfFieldApiVsLabel,accnt , fldMappingsData);
        if(obj==null){  return null;    }
        return MemberIdsCard_LC_HUM.changeSSN((List<MemberIdsCard_LS_HUM.MemberIdCardData>) MemberIdsCard_LS_HUM.createMemberIdData(mpOfFieldApiVsLabel,accnt,fldMappingsData)); 
    }
  
}