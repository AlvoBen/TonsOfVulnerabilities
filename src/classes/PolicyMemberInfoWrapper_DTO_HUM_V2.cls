/************************************************************************************************************************************************
Apex Class Name  : PolicyMemberInfoWrapper_DTO_HUM_V2
Version          : 1.0
Created Date     : FEB 25 2015
Function         : Wrapper class for current and Benefits History table to send the state to the callback function.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani                                   02/25/2016                    Original Version
* Avinash Choubey            31077                 04/18/2016                    Design Change in All Accums Req ( 237142, 246888 etc)
* Avinash Choubey            28561(VS)             05/28/2016                    Changes to fix Defect# ( 225237, 226287)
* Avinash Choubey            28899                 06/09/2016                    Changes to fix Defect# 228875
* Avinash Choubey            32583                 07/12/2016                    Unused properties removed REQ - 269975
* Muralidhar Kollu                                 18/08/2016                    REQ - 326297 - change of AsOfDate Previous section logic - add dateofservice field
* Shailesh Mali									   010/20/2020                   Version - 2, REQ - 791947
*************************************************************************************************************************************************/
public class PolicyMemberInfoWrapper_DTO_HUM_V2 {
    public Set<ID> setRemainingPolicyMember =new Set<ID>();
    public List<String> lstLabels = new List<String> ();
    public Policy_Member__c objCurrentPolicyMember = new Policy_Member__c();
    public List<Policy_Member__c> lstPolicyMember = new List<Policy_Member__c>();
    public List<VobBenefitsCurrent_DTO_HUM> listOfDTOData = new List<VobBenefitsCurrent_DTO_HUM>();
    public String accumsType ='';
    public Integer startCounter = 0;
    public date dtOfService = null;
    public Member_Id__c memberIdObj = new Member_Id__c();
    
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  subscriberGenKey in string format.
    * @param  policy_member__c record
    * @param  Boolean; yes if memgenkey is vaialable in cache else false.
    * @param  Boolean; yes if account is already sent for webActivity service else false.
    * @param  date dtDateOfService
    * @return  None
    * @see
    */
    public PolicyMemberInfoWrapper_DTO_HUM_V2(Member_Id__c tmemberIdObj,Policy_Member__c tobjCurrentPolicyMember,List<Policy_Member__c> tlstPolicyMember,Set<ID> tsetRemainingPolicyMember, List<String> tlstLabels,List<VobBenefitsCurrent_DTO_HUM> tlistOfDTOData,String  sAccumsType,Integer iStartCounter, date dtDateOfService)
    {
        objCurrentPolicyMember = tobjCurrentPolicyMember;
        lstPolicyMember = tlstPolicyMember;
        setRemainingPolicyMember = tsetRemainingPolicyMember;
        lstLabels = tlstLabels;
        listOfDTOData =tlistOfDTOData;
        accumsType = sAccumsType;
        startCounter = iStartCounter;
        dtOfService  = dtDateOfService;
        memberIdObj = tmemberIdObj;
    }

}