/*****************************************************************************************
Apex class Name    : VerifyDemographics_LD_HUM
Version            : 1.0
Function           : Data layer for VerifyDemographics_LS_HUM
Created Date       : 03/16/2021
Test Class         : VerifyDemographics_LT_HUM
Modification Log:
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava										 03/16/2021				  initial version
*Ankima Srivastava                                       06/07/2021               Checkmarx fix
*Kajal Namdev                                        	 08/18/2021               Removed limit from single record query
*Kajal Namdev                                            08/20/2021          	  Added limitOne in query
****************************************************************************************************************************/
public with sharing class VerifyDemographics_LD_HUM {
    public static final Integer iLimitOne = GLOBAL_CONSTANT_LH_HUM.iLimitONe;
    /*
     * This method is used to fetch User Name and Network User Id based on user Id
     * @param String - User Id 
     * return List of User
	*/
    public static List<User> getUserNetwork(String userId){
    	 List<User> lstUser = [Select Name,Network_User_Id__c from User where Id =: userID LIMIT :iLimitOne];
         return lstUser;
    }
    
     /*
     * This method is used to fetch Enterprise_ID__c of Account
     * @param String - Account Id 
     * return List of Account
	*/
    public static  List<Account> getAccount(String sAccId){
         List<Account> lstAccount = [Select id,Enterprise_ID__c from Account where id =:sAccId LIMIT :iLimitOne];
         return lstAccount;
    }
    
     /*
     * This method is used to fetch Enterprise_ID__c and Member Generation Key of Account
     * @param String - Account Id 
     * return Account
	*/
    public static Account getMemAccount(String AccountId){
        Account acc = [Select Enterprise_ID__c ,Mbr_Gen_Key__c from Account where id =: AccountId LIMIT :iLimitOne];
        return acc;
    }
    
    /*
     * This method is used to fetch Name of User based on Network User Id 
     * @param String - NetworkUser Id 
     * return User
	*/
    public static User getUser(String sNetworkUserId){
       User oUser = [Select Name from User where Network_User_Id__c  =: sNetworkUserId Limit 1];
       return oUser;
    }

}