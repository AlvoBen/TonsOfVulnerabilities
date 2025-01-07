/******************************************************************************************************************************       
Apex Class Name  : MemberCache_H_HUM 
Version          : 1.0  
Created Date     : Nov 30, 2015
Function         : "ChangeHasVitality_G_HUM" Helper Class to perform DML operation.
Test Class       : ChangeHasVitality_T_HUM
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               11/30/2015                    Original Version
*************************************************************************************************************************/

public class MemberCache_H_HUM {
    
    public enum triggeredAction { afterDelete }
    
    public MemberCache_H_HUM(Member_Cache__c[] cacheOldRecords, triggeredAction ta) 
    {    
        if (ta == triggeredAction.afterDelete ) 
        {
            AfterDeleteMethod(cacheOldRecords);
        }
    }

 /*
* Method Name   :    afterDeleteMethod() 
* Description   :    Method to Perform AfterDelete Operation.   
* return Type   :    null
* Parameters    :    Array
*/      
    public void afterDeleteMethod(Member_Cache__c[] cacheOldRecords)
    {
        set<id> setAccountId = new set<id>();
        list<Policy_Member__c> lstPolicyMember = new list<Policy_Member__c >();
        Policy_Member__c[] arrPolicyMember ;
        
        try
        {
            for(Member_Cache__c oMemberCache : cacheOldRecords)
                setAccountId.add(oMemberCache.AccountID__c);
            
            if(setAccountId != NULL)
                arrPolicyMember = [select Has_Vitality__c from Policy_Member__c where (Member__c in : setAccountId AND Has_Vitality__c = 'N')];
            
            if(arrPolicyMember != NULL)
            {
                for(Policy_Member__c oPolicyMember : arrPolicyMember )
                {
                    oPolicyMember.Has_Vitality__c = null;
                    lstPolicyMember.add(oPolicyMember);
                }
            }
            
            if(lstPolicyMember != NULL)
                update lstPolicyMember;
        }catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberCache_H_HUM','afterDeleteMethod');
        }
    }
}