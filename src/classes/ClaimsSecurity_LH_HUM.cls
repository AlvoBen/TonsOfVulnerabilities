/*****************************************************************************************
Apex class Name    : ClaimsSecurity_LH_HUM
Version            : 1.0
Function           : This is the Security Classs, to filter out the Claims which user is having the access.
Created Date       : 06/10/2022
Test Class         : Claims_Security_HUM

Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
*   Suraj Patil                                      	06/10/2022         Original Version
*   Anuradha Gajbhe                                     11/02/2022         US#3786555 - Claims System Integration: Claims: Medical Claims: Dental Claims: Security Home Office Differentiation
*   Anuradha Gajbhe                                     09/27/2023         US 4785171: INC2508785 - Claims: Medical Claims: Dental Claims: Security Home Office Filtering based on user access
*********************************************************************************************************************************/

public with sharing class ClaimsSecurity_LH_HUM 
{

    public Set<String> vuniquePlanid=new Set<String>();
    public map<String, boolean> planAccessMap = new map<String, boolean>();
    public map<String, boolean> planHOMap = new map<String, boolean>();

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
            return ClaimSecurityDataLogic_LD_HUM.getMemberAccountID(iPolicyMemberID);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'ClaimsSecurity_LH_HUM','getMemberAccountID');
            return NULL;
        }
    }
    

    /**
     * Method Name   :  getCCSUserAccessPolicyNumbers
     * Description   :  Get list of policies which user is having the access.
     * Return Type   :  SET<String>
     * Parameters    :  ID, String 
     */                                
    public SET<String> getCCSUserAccessPolicyNumbers(ID vMemberAccountID, String userType)
    {
    
        List<MemberPlan> vMemberPolicies=new List<MemberPlan>();
        Set<String> vuniquePolicies=new Set<String>();
        Boolean vHomeOffice;
        Integer counter=0;
        try 
        {
            List<MemberPlan> vList2 = ClaimSecurityDataLogic_LD_HUM.getPolicyMembersBasedonProduct(vMemberAccountID);
            if(vList2 != null && (vList2.size() > 0))
            {
                getPlanaccessandHODetails(vList2);
                for(MemberPlan vList: vList2 )
                {     
                    vHomeOffice = checkForHomeOffice(vList);
                    String smemmberID=getPolicyMemberID(vList.id);
                    if((!vuniquePolicies.contains(smemmberID)) &&( (!vHomeOffice) || (vList.Product__c.equalsIgnoreCase(userType))))
                    {
                        vuniquePolicies.add(smemmberID);
                    }
                    counter++;
                }
            }
            return vuniquePolicies;
        }
        catch (Exception e)
        {
            return vuniquePolicies;
        }
    }

    /*
    * Method Name   :  getPlanaccessandHODetails
    * Description   :  Validating the user has access to plan along with validating if plan is Home office or not
    * Return Type   :  Maps
    * Parameters    :  memberplan list
    */  
    public void getPlanaccessandHODetails(List<MemberPlan> vList2)
    {
        for(MemberPlan vList: vList2)
        {
            vuniquePlanid.add(vList.planId);
            planHOMap.put(vList.planId, vList.Plan.Home_Office_Account__c);			
        }

        for (UserRecordAccess indUserPlanAccess : [SELECT RecordId, HasReadAccess FROM UserRecordAccess WHERE UserId=:UserInfo.getUserId() AND RecordId IN:vuniquePlanid WITH SECURITY_ENFORCED LIMIT 200])
        {
            planAccessMap.put(indUserPlanAccess.RecordId , indUserPlanAccess.HasReadAccess);
        }    
    }

    public boolean checkForHomeOffice(MemberPlan vList)
    {
        
        Boolean vHomeOffice = true;
        boolean userhasplanaccess;
        boolean bHOplan;

        if(vList.planId != Null){
            userhasplanaccess = planAccessMap.get(vList.planid);
            bHOplan = planHOMap.get(vList.planid);
            vHomeOffice = (userhasplanaccess == true) ? bHOplan : true;
        }
        return vHomeOffice;   
    }

    /*
    * Method Name   :  isMemberHaveHomeOfficePolicies
    * Description   :  Validating the is member having any home office policies 
    * Return Type   :  Boolean
    * Parameters    :  ID 
    */                                    
    public Boolean isMemberHaveHomeOfficePolicies(ID vMemberAccountID)
    {
        if(ClaimSecurityDataLogic_LD_HUM.isHomeOfficeMember(vMemberAccountID)){
            return true;
        }
        else 
        {   
            boolean isMemberHaveHomeOfficePolicies;             
            List <MemberPlan> vPolicyMemberLst = ClaimSecurityDataLogic_LD_HUM.getHomeOfficeMembers(vMemberAccountID);
            isMemberHaveHomeOfficePolicies = (vPolicyMemberLst != NULL && !vPolicyMemberLst.isEmpty()) ? true : false;
            return isMemberHaveHomeOfficePolicies;
        }
    }
    
    
    /*
    * Method Name   :  getFilteredClaims
    * Description   :  Filtering the Claims which user is having the access
    * Return Type   :  List<ClaimsSummary_Wrapper_LDTO_HUM>
    * Parameters    :  List<ClaimsSummary_Wrapper_LDTO_HUM>, ID , ID
    */                                    
    public List<ClaimsSummary_Wrapper_LDTO_HUM> getFilteredClaims(List<ClaimsSummary_Wrapper_LDTO_HUM> vClaimsList, ID vMemberAccountID, Boolean bIsMemberAHomeOfficeAcct, Boolean bisHomeOfficeAllUser, Boolean bisHomeOfficeMedicalUser, Boolean bisHomeOfficeDentalUser)
    {
        List<ClaimsSummary_Wrapper_LDTO_HUM> vFiletrClaims=new List<ClaimsSummary_Wrapper_LDTO_HUM>();
    
        if((!Test.isRunningTest() && !bIsMemberAHomeOfficeAcct) || (bisHomeOfficeAllUser)){
            return vClaimsList;
        } 
        List<MemberPlan> ccsUserAccessMemberPolicies= NULL;
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
        String tempCmemberID='';
        String cmemberID='';
        try 
        {
            tempCmemberID= ClaimSecurityDataLogic_LD_HUM.getMemberIdName(policyRecordID);
            cmemberID = tempCmemberID.substring(0, 9);
        }
        catch(Exception e)
        {
            cmemberID='';
        }                
        return cmemberID;
    }
}