/*****************************************************************************************
Apex class Name    : ClaimsSecurity_H_HUM
Version            : 1.0
Function           : This is the Security Classs, to filter out the Claims which user is having the access.
Created Date       : 15 Apr 2016
Test Class         : Claims_Security_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Rajaram Korivi              25239                  04/15/2016           Original Version
 *   Harikrishna M               25239                  04/15/2016           Added security related changes
 *   Kritika Popat               27335                  05/12/2016           Defect 225869.0001 Modified in getFilteredClaims()
 *   Gargey Sharma               27522                  05/16/2016           Defect 225876.0001 Modified in getFilteredClaims()
 *   Harika Devi Kalidindi       30521                  06/17/2016           Defect 230083.0001# fix
 *   Santhi Mandava                                     12/26/2018           Removed dpaas call
 *   Prasanthi Kandula                                  07/02/2021           US - 2356070 Claims Summary performance improvement 
 *********************************************************************************************************************************/

public with sharing class ClaimsSecurity_H_HUM 
{
    /*
    * Method Name   :  getMemberAccountID
    * Description   :  Getting AccountID for that member.
    * Return Type   :  ID (Member Account SFDC ID)
    * Parameters    :  ID  (Policy_Member__C record id)
    */
    public ID getMemberAccountID(ID iPolicyMemberID)
    {
         try
         {
               return ClaimSecurityDataLogic_D_HUM.getMemberAccountID(iPolicyMemberID);
         }
         catch(Exception ex)
         {
              HUMExceptionHelper.logErrors(ex,'ClaimsSecurity_H_HUM','getMemberAccountID');
              return NULL;
         }
    }
    
  
    /**
    * Method Name   :  getCCSUserAccessPolicyNumbers
    * Description   :  Get list of policies which user is having the access.
    * Return Type   :  SET<String>
    * Parameters    :  ID, String 
    */                                
    public SET<String> getCCSUserAccessPolicyNumbers(ID vMemberAccountID, String userType){
    
        List<Policy_Member__c> vMemberPolicies=new List<Policy_Member__c>();
        Set<String> vuniquePolicies=new Set<String>();
        Integer counter=0;
        try {
        List<Policy_Member__c> vList2 = ClaimSecurityDataLogic_D_HUM.getPolicyMembersBasedonProduct(vMemberAccountID);
        For(Policy_Member__c vList: vList2 )
        {     
            Account vgroupAccount=(Account)vList.getSObject('policy__r').getSobject('Group_Name__r');
            Boolean vHomeOffice=vgroupAccount.Home_Office_Account__c;
            String smemmberID=getPolicyMemberID(vList.id);
            if((!vuniquePolicies.contains(smemmberID)) &&( (!vHomeOffice) || (vList.Product__c.equalsIgnoreCase(userType))))
            {

                vuniquePolicies.add(smemmberID);
            }
            counter++;
         }
         return vuniquePolicies;
        }
        catch (Exception e){
           return vuniquePolicies;
        }
    }

    
    /*
    * Method Name   :  isMemberHaveHomeOfficePolicies
    * Description   :  Validating the is member having any home office policies 
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */                                    
    public Boolean isMemberHaveHomeOfficePolicies(ID vMemberAccountID)
    {
        if(ClaimSecurityDataLogic_D_HUM.isHomeOfficeMember(vMemberAccountID)){
            return true;
        }
        else {                     
        List <Policy_Member__c> vPolicyMemberLst = ClaimSecurityDataLogic_D_HUM.getHomeOfficeMembers(vMemberAccountID);
        if(vPolicyMemberLst != NULL && !vPolicyMemberLst.isEmpty()) return true;
        else return false;
        }
    }
    
    
    /*
    * Method Name   :  getFilteredClaims
    * Description   :  Filtering the Claims which user is having the access
    * Return Type   :  List<ClaimsSummary_Wrapper_DTO_HUM>
    * Parameters    :  List<ClaimsSummary_Wrapper_DTO_HUM>, ID , ID
    */                                    
    public List<ClaimsSummary_Wrapper_DTO_HUM> getFilteredClaims(List<ClaimsSummary_Wrapper_DTO_HUM> vClaimsList, ID vMemberAccountID, Boolean bIsMemberAHomeOfficeAcct, Boolean bisHomeOfficeAllUser, Boolean bisHomeOfficeMedicalUser, Boolean bisHomeOfficeDentalUser)
    {
        List<ClaimsSummary_Wrapper_DTO_HUM> vFiletrClaims=new List<ClaimsSummary_Wrapper_DTO_HUM>();
        if(!Test.isRunningTest() && !bIsMemberAHomeOfficeAcct) return vClaimsList;
        if(bisHomeOfficeAllUser) return vClaimsList;
        List<Policy_Member__c> ccsUserAccessMemberPolicies= NULL;
        SET<String> vmemberUniquePolicies=null;
        if(bisHomeOfficeMedicalUser)
        {
            //Show only Medical Claims for Home Office and General Claims
            vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID,'MED');    
        }
        else if(bisHomeOfficeDentalUser)
        {
            //Show only Dental Claims for Home Office and General Claims
            vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID,'DEN');
        }
        else
        {
           //Show Only Genearl claims
           vmemberUniquePolicies=getCCSUserAccessPolicyNumbers(vMemberAccountID,'None');
        }
        for(integer i=0;i<vClaimsList.size();i++)
        {
            String sMemberID = vClaimsList.get(i).sMemberId;
            if(vmemberUniquePolicies!= null && vClaimsList.get(i).sMemberId != null && vmemberUniquePolicies.contains(vClaimsList.get(i).sMemberId.substringBefore('-'))) {
                vFiletrClaims.add(vClaimsList.get(i));
            }
        }
        return vFiletrClaims;
    }
    
     /*
    * Method Name   :  getPolicyMemberID
    * Description   :  Get the Member ID from Policy
    * Return Type   :  String
    * Parameters    :  ID 
    */ 
    @TestVisible
    private String getPolicyMemberID(ID policyRecordID)
    {
        String cmemberID='';
        try 
        {
            cmemberID= ClaimSecurityDataLogic_D_HUM.getMemberIdName(policyRecordID);
        }
        catch(Exception e)
        {
             cmemberID='';
        }                
        return cmemberID;
    }
}