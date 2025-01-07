/*****************************************************************************************
Apex class Name    : MemberAncillaryRequest_D_HUM_V2
Version            : 1.0
Function           : DataLayer Class for the Member Ancillary request
Created Date       : 11/04/2019
Test Class         : MemberAncillaryRequest_D_T_HUM_V2
Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------
 *  Manikanta                       356372               05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
 *  Yogesh Gupta                                         11/04/2019         V2 Version - REQ-403709 Ancillary Request for Medicaid Id
*********************************************************************************************************************************/
public with sharing class MemberAncillaryRequest_D_HUM_V2
{             
    @testvisible private static HUMConstants__c objEncryption = HUMConstants__c.getInstance('ENCRYPTIONTYPE');
    
    /*
    *  This method  is used to return details of a policy member
    *  @name   getMemberName
    *  @param  ID - Record ID of Policy Member for which claims are to be displayed
    *  @return List<Policy_Member__c> -  List of Policy member records
    *  @throws getMemberName
    */
    public static List<Policy_Member__c> getMemberName(ID sRecId)
    {   
        List<Policy_Member__c> lstpolmem = new List<Policy_Member__c>();
        try
        {
            if(sRecId != null && String.isNotBlank(sRecId))
            {
				//Adding new field in SOQL REQ-403709 
                lstpolmem = [SELECT Id, Policy__r.Client_Number__c, Group_Number__c, Effective_Date__c, Policy_Platform__c, Member__r.Name, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Product_Type__c, Issue_State__c FROM Policy_Member__c WHERE ID=:sRecId];
            }
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberAncillaryRequest_D_HUM_V2', 'getMemberName');
        }
        return lstpolmem;
    } 
    
  
    /*
    *  This method  is used to return Member Id details for a policy member
    *  @name   getMemberIDObjDetails
    *  @param  ID - Record ID of Policy Member which has related Member_ID_c records
    *  @return list<Member_ID__c> - List of Member_ID_c objects related to given policy member
    *  @throws getMemberIDObjDetails
    */    
     public static List<Member_ID__c> getMemberIDObjDetails(ID sRecId)
     { 
        List<Member_ID__C> lstmemid = new List<Member_ID__C>();
        try
        {
            if(sRecId != null && String.isNotBlank(sRecId))
            {
                lstmemid =[SELECT Id, Name, Member_Dependent_Code__c, Policy_Member__r.Subscriber__c, Type__c FROM Member_ID__c WHERE Policy_Member__c=:sRecId AND Type__c = 'Member-Id-Base'];                                  
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberAncillaryRequest_D_HUM_V2', 'getMemberIDObjDetails');
        }
        return lstmemid;
     }
   
    /*
    *  This method is used to get enterprise ID for a particular Member
    *  @name   getEnterpriseId
    *  @param  String sObjId - Policy Member Id for which enterprise ID is needed
    *  @return String - returns enterprise ID for a particular Member
    *  @throws NA
    */      
    public static String getEnterpriseId(String sObjId)
    {
        String sEnterpriseId;
        if(sObjId != null && String.isNotBlank(sObjId))
        {
            Policy_Member__c objPolMember = [Select Member__r.Enterprise_ID__c FROM Policy_Member__c WHERE Id =:sObjId] ;    
            if(objPolMember.Member__r.Enterprise_ID__c != null)  
            {
                sEnterpriseId = objPolMember.Member__r.Enterprise_ID__c;
            }            
        }
        return sEnterpriseId;
    }         
}