/************************************************************************************************************************************************
Apex Class Name  : Accums_ContinuationStateWrapper_DTO_HUM
Version          : 1.0
Created Date     : Apr 18 2016
Function         : Wrapper class for current and Benefits History table to send the state to the callback function.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey                                  04/18/2016                    Original Version
* Avinash Choubey            28561	               05/28/2016                	 Changes to fix Defect# ( 225237, 226287)
* Avinash Choubey            29988         		   06/09/2016                	 Changes to fix Defect# 228875
*************************************************************************************************************************************************/

public class Accums_ContinuationStateWrapper_DTO_HUM
{
	public List<policy_Member__c> policyMembers = new List<policy_Member__c>();
	public Map<id,String> mapSubscriberAccountIDAndGenKey = new Map<id,String>();
	public List<string> labelsRequest = new List<string>();
	public String memberGenKey =null;
	public Policy_Member__C currentPolicyMember= new Policy_Member__C();
	public Set<id> SubscriberPoliciesids = new set<ID>();
	public Set<id> setRemainingPolicyMembers = new set<id>();
	public String accumsType = '';
	public Integer startCounter = 0;
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  List<policy_Member__c> list of Policy Members to get Accums Data 
    * @param  Map<id,GenKey> map of Subscriber id and respective membergenkey
    * @param  list<string>
    * @param  string membergenkey
    * @return  None
    */
	public Accums_ContinuationStateWrapper_DTO_HUM(Policy_Member__C objcurrentPolicyMember ,List<policy_Member__c> lPolicyMembers,Map<id,String> mMapSubscriberAccountIDAndGenKey,String sMemberGenKey,List<string> lstLabelsRequest,Set<id> setSubscriberPoliciesids,Set<id> tsetRemainingPolicyMembers,String saccumsType,Integer iStartCounter)
	{
		policyMembers = lPolicyMembers;
		mapSubscriberAccountIDAndGenKey = mMapSubscriberAccountIDAndGenKey;
		memberGenKey = sMemberGenKey;
		labelsRequest = lstLabelsRequest;
		currentPolicyMember= objcurrentPolicyMember;
		SubscriberPoliciesids = setSubscriberPoliciesids;
		setRemainingPolicyMembers =tsetRemainingPolicyMembers;
		accumsType = saccumsType;
		StartCounter = iStartCounter;
	}
}