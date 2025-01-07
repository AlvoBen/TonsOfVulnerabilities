/*
 * Apex class Name    : AuthReferral_Security_H_HUM  
 * Version            : 1.0
 * Function           : This class apply security filter on results fetched from Service
                        Class is having without sharing as part of security setting to filter Authorizations,
                        so that CCS Associate should be able to get values for those records(GroupNumber) which he don't have access to.
 * Created Date       : 12/08/2015
 * Test Class         : AuthReferral_Security_T_HUM
 * Author            : Manish Kumar 
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar               29042                 12/08/2015                 Original Version
 * Sruthi Adivikolanu         29042                 01/07/2016                 Included Encrypted call out for Group Number field on Account
 * Sruthi Adivikolanu         29646                 01/27/2016                 Renamed AuthRefferal_Wrapper_H_HUM to AuthRefferal_Wrapper_DTO_HUM
 * Santhi Mandava                                   03/21/2018                 REQ - 355953 Removed group number encryption
 *****************************************************************************************************************************************/

public without sharing class AuthReferral_Security_H_HUM
{
    private AuthReferral_Query_D_HUM oDataLayer = new AuthReferral_Query_D_HUM();
    private Set<ID> setPolicy = new Set<ID>();
   /*
    * Method Name : applySecurityMeasures
    * Description : This method will be called from main controller class to apply 
    *               security features on data fetched from service.
    * Parameters : List<AuthRefferal_Wrapper_DTO_HUM> which holds data from wrapper
    *              Id which holds data for current Account instance
    * Return Type : List<AuthRefferal_Wrapper_DTO_HUM>
    */
    public List<AuthRefferal_Wrapper_DTO_HUM> applySecurityMeasures(List<AuthRefferal_Wrapper_DTO_HUM> lstAuthRef, Id recId)
    {
        if(checkHOSecurityGroup(recId))
        {
            return lstAuthRef;
        }
        else
        {
            if(checkHOAccountMember(recId))
            {
                return filterHOAccountResults(lstAuthRef);
            }
            else
            {
                return lstAuthRef;
            }
        }
    }
    
   /*
    * Method Name : checkHumanaAll
    * Description : This method will be called internal to this class to fetch all Groups 
    *               which current logged in User is part of.
    * Parameters : Id
    * Return Type : Boolean
    */
    @testVisible
    private Boolean checkHumanaAll(Id recId)
    {
        for(Policy_Member__c oPM : oDataLayer.getRelatedPolicies(recId))
        {
            setPolicy.add(oPM.Policy__c);
        }
        List <UserRecordAccess> lstUserRecordAcess = oDataLayer.getUserRecordAccess(setPolicy);
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            if(!userAccess.HASREADACCESS)   
            return false;
        }
        
        return true;
    }
    
   /*
    * Method Name : checkHOSecurityGroup
    * Description : This method will be called internally to for this class and 
    *               will check if the User is part of HO security group.
    * Parameters : Id
    * Return Type : Boolean
    */
    @testVisible
    private Boolean checkHOSecurityGroup(Id recId)
    {
        return checkHumanaAll(recId);
    }
    
   /*
    * Method Name : checkHOAccountMember
    * Description : This method will query current instance of Account to get Home_Office_Account field
    * Parameters : Id
    * Return Type : Boolean
    */
    @testVisible
    private Boolean checkHOAccountMember(Id recId)
    {
        return oDataLayer.checkHOAccountMember(recId);
    }
    
   /*
    * Method Name : filterHOAccountResults
    * Description : This method will be called internally to for this class and 
    *               will check if the User is part of HO Account security group.
    * Parameters : lstAuthRef a List<AuthRefferal_Wrapper_DTO_HUM> which holds data from wrapper
    * Return Type : List<AuthRefferal_Wrapper_DTO_HUM>
    */
    @testVisible
    private List<AuthRefferal_Wrapper_DTO_HUM> filterHOAccountResults(List<AuthRefferal_Wrapper_DTO_HUM> lstAuthRef)
    {
        Set<String> setGroupIds = new Set<String>();
        map<String, Boolean> mapGroupFlag = new Map<String, Boolean>();
        map<String, AuthRefferal_Wrapper_DTO_HUM> mapAuthRef = new Map<String, AuthRefferal_Wrapper_DTO_HUM>();
        for(AuthRefferal_Wrapper_DTO_HUM oWrp : lstAuthRef)
        {
            setGroupIds.add(oWrp.sGroupId);
            mapAuthRef.put(oWrp.sAuthorizationOrReferralNumber, oWrp);
            mapGroupFlag.put(oWrp.sGroupId, true);
        }
        if(!setGroupIds.isEmpty())
        {
            mapGroupFlag = filterGroupId(setGroupIds,mapGroupFlag);
        }
        return getFinalAuthList(mapAuthRef,mapGroupFlag);
    }
    
   /*
    * Method Name : filterGroupId
    * Description : This method will be called internally to get GroupId which can't be accessible. 
    * Parameters : Set<String> setGroupIds,GroupId related to Auths
                   map<String, Boolean> mapGroupFlag,GroupId with true/false.
    * Return Type : map<String, Boolean> mapGroupFlag,GroupId with true/false.
    */
    private map<String, Boolean> filterGroupId(Set<String> setGroupIds,map<String, Boolean> mapGroupFlag)
    {
       for(Account oAcc : oDataLayer.getAccountGroupRecords(setGroupIds))
       {
            if(oAcc.Home_Office_Account__c){
               for(string obj : setGroupIds){
                   if(obj == oAcc.Group_Number__c){
                      mapGroupFlag.put(obj, false);
                   }
               }    
            }    
        }   
     return mapGroupFlag;
    }
    
   /*
    * Method Name : getFinalAuthList
    * Description : This method will be called internally to get final List of filtered Auths. 
    * Parameters :  map<String, AuthRefferal_Wrapper_DTO_HUM> mapAuthRef,Map of Auth Number and Obj Instance.
                    map<String, Boolean> mapGroupFlag,GroupId with true/false.
    * Return Type : List<AuthRefferal_Wrapper_DTO_HUM>.
    */
    private List<AuthRefferal_Wrapper_DTO_HUM> getFinalAuthList(map<String, AuthRefferal_Wrapper_DTO_HUM> mapAuthRef,map<String, Boolean> mapGroupFlag){
        List<AuthRefferal_Wrapper_DTO_HUM> lstFinalAuthRef = new List<AuthRefferal_Wrapper_DTO_HUM>();
        AuthRefferal_Wrapper_DTO_HUM oAuthRef = new AuthRefferal_Wrapper_DTO_HUM();
        for(String sAuthRef : mapAuthRef.keySet()){
            oAuthRef = mapAuthRef.get(sAuthRef);
            if(!mapGroupFlag.get(oAuthRef.sGroupId)){
                oAuthRef.bIsAccessible = false;
            }    
            lstFinalAuthRef.add(oAuthRef);
        }
        return lstFinalAuthRef;
    }
}