/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailVNC_Helper_HUM  
* Version          : 1.0
* Created Date     : 25th June, 2021 
* Function         : Common Logic for VNC Integration
* Test Class       : CRMRetailRegisterParticipant_T_HUM & CRMRetailMarkAttendance_T_HUM
**************************************************************************** 
* Modification Log:
* Developer Name                   Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                			  		 				 06/25/2021             Original Version  -2389054
* Vinoth L										  		 06/24/2023		  		User Story 4769775: - T1PRJ0154546 / SF / MF9 Storefront - Carry Forward First Time Visitor to Event Interactions
**********************************************************************************************************************************/
public with sharing class CRMRetailVNC_Helper_HUM 
{
	
    /**
    * getVisitorID Method .
    * <p>
    * This method is to get the visitor id.
    *
	* @param  		 objAccount				Account
    * @return		 visitorId				String
    */
    public static String getVisitorID(Account objAccount) 
    {           
        String visitorId = '';
        List<Policy_Member__c> listOfPolicyMembers = new List<Policy_Member__c>();
        
        if(objAccount.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR)
        {
            visitorId = objAccount.GCM_Visitor_Barcode__c;
			return visitorId;
        }
        else if(objAccount.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
        {
            if(objAccount.Policy_Members__r.size()>0)
                listOfPolicyMembers.addAll(objAccount.Policy_Members__r);	
            else
			{
				visitorId = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_OTHER;
				return visitorId;
			}
        }
        
        if(!listOfPolicyMembers.isEmpty())
        {
            for(Policy_Member__c objPolicyMember : listOfPolicyMembers){
                Date effDate;
                Date endDate;
                String tempEndDate;
                
                if(String.isNotBlank(objPolicyMember.Effective_Date__c))
                	effDate = Date.parse(objPolicyMember.Effective_Date__c); 
                
                if(String.isNotBlank(objPolicyMember.End_Date__c))
                	tempEndDate = objPolicyMember.End_Date__c;
                
                if(String.isNotBlank(tempEndDate)){
                    List<String> splitedDate = tempEndDate.split('/');                    
                    endDate = Date.newInstance(Integer.valueOf(splitedDate[2]),Integer.valueOf(splitedDate[0]),Integer.valueOf(splitedDate[1]));
                }
                
                if(objPolicyMember.Name.startsWith(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_POLICY_NAME_STARTS_WITH) && effDate <= Date.today() && (endDate > Date.today() || endDate == NULL) && objPolicyMember.Status__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE)
                {                           
                    visitorId = objPolicyMember.Name.Substring(0,objPolicyMember.Name.length()-2);
                    break;
                }
                else
                    visitorId = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_OTHER;
            } 
        }
        return visitorId;
    }
	
    /**
    * createInteraction Method .
    * <p>
    * This method is used to create the interactions.
    *
	* @param  		 accountId							Id
	* @param  		 visitorType						String
    * @param  		 category							String
    * @param  		 eventStartTime						DateTime
    * @param  		 eventEndTime						DateTime
    * @param  		 interactionDate					DateTime
    * @param  		 isFirstTime						Boolean
    * @param  		 reasonId							Id
    * @param 	     location							Id
    * @param  		 visitorId							String
    * @return		 Storefront_Interaction__c			instance of interaction		
    */
    public static Storefront_Interaction__c createInteraction( Id accountId, String visitorType, String category, DateTime eventStartTime, DateTime eventEndTime, DateTime interactionDate, Boolean isFirstTime, Id reasonId, Id location, String visitorId, Boolean isAllDayEvent) 
    {
        Storefront_Interaction__c objInteraction = new Storefront_Interaction__c();
        
		objInteraction.Account__c = accountId;
        if (!HUMUtilityHelper.isCRMFunctionalityON('4769775'))
        {
		    objInteraction.First_Time_Visitor__c = isFirstTime;
        }        
        objInteraction.isAllDayEvent__c = isAllDayEvent;
        
        if(String.isNotBlank(visitorType))
        	objInteraction.CRM_Retail_Interaction_Visitor_Type__c = visitorType;
        
        if(eventStartTime != null)
        	objInteraction.Storefront_Event_Starttime__c = eventStartTime;
        
        if(eventEndTime != null)
        	objInteraction.Storefront_Event_Endtime__c = eventEndTime;
        
        if(String.isNotBlank(category))
        	objInteraction.Category__c = category;
        
        if(reasonId != null)
        	objInteraction.Reason__c = reasonId;
        
        if(interactionDate  != null)
        	objInteraction.Interaction_Date__c = interactionDate; 
        
        if(location != null)
        	objInteraction.Location__c = location;
        
        if(String.isNotBlank(visitorId))
        	objInteraction.Visitor_ID__c = visitorId;
        
        return objInteraction;
    }
	/**
    * returnAccount Method .
    * <p>
    * This method is used to return the account associated with the accountid sent in the request.
    *
	* @param  		 accountId								AccountId
	* @return		 listOfAccount							Accounts								
    */
	public static Account returnAccount(Id accountId)
    {
        if (accountId != null )
		{
			List<Account> listOfAccount = [Select Id, (SELECT Id,Name,ETL_Record_Deleted__c,Effective_Date__c,status__c,End_Date__c,Member__c,Product__c FROM Policy_Members__r WHERE Member__r.ETL_Record_Deleted__c = false AND ETL_Record_Deleted__c = false AND Product__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_POLICY_PRODUCT_TYPE ORDER BY Status__c ),GCM_Visitor_Barcode__c,RecordType.Name from Account where Id =: accountId AND (RecordType.Name = :GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER OR RecordType.Name = :GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR) Limit 1];
            
			if(!listOfAccount.isEmpty())
			{
				return listOfAccount[0];
			}
        }
		
		return null;
    }	
	/**
    * returnInteractions Method .
    * <p>
    * This method is used to return list of interactions associated with the accountid sent in the request.
    *
	* @param  		 accountId								AccountId
	* @return		 lstInteraction							List of Interactions								
    */
    public static List<Storefront_Interaction__c> returnInteractions(Id accountId)
    {
        List<Storefront_Interaction__c> lstInteraction = new List<Storefront_Interaction__c>();
		
        if (accountId != null)
        {
			lstInteraction = [Select Id, Visitor_ID__c, isAllDayEvent__c,Reason__c, Storefront_Event_Endtime__c, Category__c, Storefront_Event_Starttime__c, Location__c, Location__r.Name, Reason__r.Name, CRM_Retail_Interaction_Visitor_Type__c, Interaction_Date_Only__c, Interaction_Date__c, Account__c, Account__r.Id, First_Time_Visitor__c  from Storefront_Interaction__c where Account__r.Id =: accountId ORDER BY CreatedDate DESC];
		}
        
        return lstInteraction;
    }	
	
    /**
    * returnReasonId Method .
    * <p>
    * This method is used to return the reason id for which the interaction will be created.
    *
	* @param  		 reasonName						reason name
	* @return		 id								reason id								
    */
    public static Id returnReasonId(String reasonName)
    {
        if(String.isNotBlank(reasonName))        
        {
            List<Storefront_I_Reason__c> listOfReasons = new List<Storefront_I_Reason__c>([Select id, Name from Storefront_I_Reason__c where Name =: reasonName LIMIT 1]);
            
            if(!listOfReasons.isEmpty())
            {
                return listOfReasons[0].id;
            }
        }
        return null;
    }
	/**
    * returnInteractionId Method .
    * <p>
    * This method is used to return the interaction id which needs to be sent in the response instance.
    *
	* @param  		 listOfInteractions						list of recently inserted interactions
	* @return		 id										interaction id								
    */
    public static Id returnInteractionId(List<Storefront_Interaction__c> listOfInteractions)
    {
        if(!listOfInteractions.isEmpty())
        {
            if(listOfInteractions.size() == 1)
            {
                return listOfInteractions[0].Id;
            }
            else
            {
                return listOfInteractions[1].Id;
            }           
        }
        return null;
    }
    /**
    * getError Method.
    * <p>
    * This method is used to get the exact error for an exception.
    *
    * @param  		exceptionError 				exception error string                               
    * @return 		error						error string                 
    */
    public static String getError(String exceptionError)
    {
        String error = exceptionError;
        if(exceptionError.contains(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CUSTOM_VALIDATION))
        {
        	error = exceptionError.substringBetween(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CUSTOM_VALIDATION,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EXCEPTION_END);   
        }
        return error;
    }
}