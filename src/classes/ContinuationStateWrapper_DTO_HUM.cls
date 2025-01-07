/************************************************************************************************************************************************
Apex Class Name  : ContinuationStateWrapper_DTO_HUM
Version          : 1.0
Created Date     : FEB 25 2015
Function         : Wrapper class for current and Benefits History table to send the state to the callback function.
Test Class       : 
************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Rajesh Keswani                                   02/25/2016                    Original Version
*************************************************************************************************************************************************/

public class ContinuationStateWrapper_DTO_HUM
{
	public List<PolicyMemberInfoWrapper_DTO_HUM> listOfNonWebActivityPolicymemWrapper = new List<PolicyMemberInfoWrapper_DTO_HUM>();
	public list<ComponentValues>ListOfinnerWrapperOfRequestedData = new List<ComponentValues>();
	public List<string> labelsRequest = new List<string>();
	public Map<string,string> sSubcIdInCache = new  Map<string,string>();
	Public policy_Member__c currentPolicy = NULL;
	
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  policy_Member__c record of current policy
    * @param  list<PolicyMemberInfoWrapper_DTO_HUM>
    * @param  list<ComponentValues>
    * @param  list<string>
    * @param  Map<string,string>
    * @return  None
    */
	public ContinuationStateWrapper_DTO_HUM(policy_Member__c currentPol,list<PolicyMemberInfoWrapper_DTO_HUM>listOfNonWebActPolicymemWrapper,list<ComponentValues>ListOfinnerWrapperOfReqData,list<string> labelRequest,Map<string,string> sSubcIdInCacheData)
	{
		currentPolicy = currentPol;
		listOfNonWebActivityPolicymemWrapper = listOfNonWebActPolicymemWrapper;
		ListOfinnerWrapperOfRequestedData = ListOfinnerWrapperOfReqData;
		labelsRequest = labelRequest;
		sSubcIdInCache = sSubcIdInCacheData;
	}
}