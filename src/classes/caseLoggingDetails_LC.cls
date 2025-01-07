/*****************************************************************************************************************************************************************************
    @Apex Class Name  : caseLoggingDetails_LC
    @Version          : 1.0
    @Created Date     : 06/01/2022
    @Function         : Conroller Class for caseLoggingDetailsHum
    @test Class : caseLoggingDetails_LT
******************************************************************************************************************************************************************************
Modification Log : 

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                 06/01/2022                    Original Version  
* Prasuna Pattabhi                                08/04/2022                   Added field to display the user as link
******************************************************************************************************************************************************************************/

Public With sharing Class caseLoggingDetails_LC {    
    /*
    * @return Boolean - this indicates whethere the user has access to deleted the attachment log record Or Not.   
    */
    @AuraEnabled(cacheable=true)
    public static Boolean hasDeleteAccess(String caseId){        
		
        Boolean hasDelAccess = false;
        Boolean bhideTheLink = false;

        Set<String> wqueues = GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.WQUEUES;
		Set<String> chatQueues = GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CHATQUEUES;
        
        String userProfile = [SELECT Name FROM Profile WHERE Id = :UserInfo.getProfileId() limit 1].Name;

        String pharmacyUser = GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.NONPHARMACY_USER_HUM;        
        
        if(userProfile == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.HUM_PHARMACY_SPECIALIST) {
            pharmacyUser = GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.YES_SPECIALIST;
        	List<String> listCSHPSupervisor = new List<String>();        	
        	for(String cs : Pharmacy_Supervisor_HUM__c.getAll().keyset()){
        		listCSHPSupervisor.add(cs);
        	}
        	if(listCSHPSupervisor != null && !listCSHPSupervisor.isEmpty()){
		        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name IN : listCSHPSupervisor];
		        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0){
		            pharmacyUser = GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.YES_SUPERVISOR;
		        }
        	}
        }

        Case oCase = [SELECT Id, Policy_Member__c,Member_Plan_Id__c,RecordType.name, 
        Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin ,Member_Plan_Id__r.Product__c
        FROM Case WHERE id = :caseId];
        
        if(oCase !=null &&(oCase.Recordtype.name.containsIgnoreCase(GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.HUM_HP_REC_TYPE) 
			|| (oCase.Policy_Member__c !=null && oCase.Policy_Member__r.Product__c == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.MEDICARE_POLICY)
            || (oCase.Member_Plan_Id__c !=null && oCase.Member_Plan_Id__r.Product__c == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.MEDICARE_POLICY) 
			|| (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.WEB_CHAT_ORIGIN))){
            if(oCase.Recordtype.name.containsIgnoreCase(GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.HUM_HP_REC_TYPE) 
            || wqueues.contains(oCase.Owner_Queue__c) || wqueues.contains(oCase.Created_By_Queue__c) || 
            (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.WEB_CHAT_ORIGIN)){
                bhideTheLink = true;
            }
        }

        if (userProfile == GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CUSTOMER_CARE_SUPERVISOR || 
            (GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.YES_SUPERVISOR.equalsIgnoreCase(pharmacyUser) && bhideTheLink)){				
            hasDelAccess = true;
        }

        return hasDelAccess;
    }
    
    /*
    * @return Map - It contains the no of records count And the case logged info details   
    */
    
    @AuraEnabled
    public static Map<String,String> getCaseLoggedDetails(String caseId,Integer noOfRows){
        
        Map<String,String> result  =  new Map<String,String>();

        List<AggregateResult> ar = [SELECT Count(Id) cnt FROM Attachment_Log__c WHERE IsDeleted__c = false  AND Case__c = :caseId 
        AND Source_System__c  in (:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM,:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM_LIGHTNING)  AND 
        Attachment_Type__c !=:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CLAIMS_MANUAL_ENTRY];
        String str = String.valueOf(ar[0].get('cnt')) ;
        Integer cnt = Integer.valueOf(str) ;
        result.put('Count',cnt+'');

        List<CaseLoggedInfo_LDTO_HUM> caseLoggedInfo = new list<CaseLoggedInfo_LDTO_HUM>();
        list<Attachment_Log__c> attachmentInfoList;
        if(noOfRows>0){
            attachmentInfoList = [SELECT Id, Attachment_Sub_type__c, Created_BY__c,
            CreatedBy.Name, CreatedDate, CreatedBy.Current_Queue__c,CreatedByQueue__c,Source_System__c,CreatedBy.Id
            FROM Attachment_Log__c WHERE IsDeleted__c = false 
            AND Case__c = :caseId AND Source_System__c  in (:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM,:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM_LIGHTNING)
            AND Attachment_Type__c !=:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CLAIMS_MANUAL_ENTRY
            ORDER BY Id DESC LIMIT :noOfRows];
        }else{
            attachmentInfoList = [SELECT Id, Attachment_Sub_type__c, Created_BY__c,
            CreatedBy.Name, CreatedDate, CreatedBy.Current_Queue__c,CreatedByQueue__c,Source_System__c,CreatedBy.Id
            FROM Attachment_Log__c WHERE IsDeleted__c = false 
            AND Case__c = :caseId AND Source_System__c  in (:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM,:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CRM_LIGHTNING)
            AND Attachment_Type__c !=:GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM.CLAIMS_MANUAL_ENTRY 
            ORDER BY Id DESC];
        } 
        
        for(Attachment_Log__c ai : attachmentInfoList){			
            CaseLoggedInfo_LDTO_HUM caseInfoDto = new CaseLoggedInfo_LDTO_HUM();            
			caseInfoDto.id = ai.id;
            caseInfoDto.sourceSystem = ai.Source_System__c;
			caseInfoDto.subType = ai.Attachment_Sub_type__c;
			caseInfoDto.createdDate = ai.CreatedDate.format('MM/dd/yyyy hh:mm a');
			caseInfoDto.createdBy = ai.CreatedBy.Name;
			caseInfoDto.createdByQueue = ai.CreatedByQueue__c;
            caseInfoDto.userId = '/lightning/r/User/'+ai.CreatedBy.Id+'/view';
			caseLoggedInfo.add(caseInfoDto);
		}
        result.put('data',JSON.serialize(caseLoggedInfo));

        return result;
    }

    /*
    * @return String - the case number assocaited with the given caseId  
    */
    @AuraEnabled(cacheable=true)
    public static String getCaseNumber(String caseId){
        Case oCase = [SELECT caseNumber FROM Case WHERE Id = :caseId limit 1];
        return oCase.caseNumber;
    }
    
    /*
    * This function deletes the attachment log assocaited with the given attachment Id
    *@return viod  
    */
    @AuraEnabled
    public static void deleteAttachment(String attachmentId){
        Attachment_Log__c attachment = [SELECT Id, IsDeleted__c FROM Attachment_Log__c WHERE id = :attachmentId];
        attachment.IsDeleted__c = true;
        update attachment;
    }
}