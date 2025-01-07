/********************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdateTemplateInsert_LD_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : DemographicUpdateTemplateInsert_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/

public with sharing class DemographicUpdateTemplateInsert_LD_HUM {
    public static string sClassName = 'DemographicUpdateTemplateInsert_LD_HUM';
    public static boolean isServiceSuccess { get; set; }
    /*
    * Method Name: processTemplateData
    * Description: method is responsible to process template data insertion
    * Parameter: caseNumber, tmplateData, templateName, policyMemberId
    * Return Type: Nothing
    */
    @AuraEnabled()
    public static void processTemplateData(string caseNumber, string templateData, string templateName, string policyId, boolean isSuccess) {
        try {
            // Allows AVF call even demographic updates are success or failure
            DemographicUpdateTemplate_DTO_HUM templateDTO = new DemographicUpdateTemplate_DTO_HUM();
            DemographicUpdateTemplate_DTO_HUM templateBaseDTO = (DemographicUpdateTemplate_DTO_HUM)System.JSON.deserialize(templateData,DemographicUpdateTemplate_DTO_HUM.class);
            DemographicUpdateTemplate_DTO_HUM templateMAUDTO = new DemographicUpdateTemplate_DTO_HUM();
            DemographicUpdateTemplate_DTO_HUM.TemplateBase templateMAUBase = new DemographicUpdateTemplate_DTO_HUM.TemplateBase();
            List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateMAUBaselst = new List<DemographicUpdateTemplate_DTO_HUM.TemplateBase>(); 
            if(HUMUtilityHelper.isCRMFunctionalityON('1935446')){
                if(templateBaseDTO.templateBase.size() > 0) {
                    for(DemographicUpdateTemplate_DTO_HUM.TemplateBase base : templateBaseDTO.templateBase) {
                        if(base.data.rsoMAU != null) {
                            templateMAUBaselst.add(base);
                            templateMAUDTO.templateBase = templateMAUBaselst;
                            string templateMAUData = JSON.serialize(templateMAUDTO);
                            DemographicMedicareAddress_LS_HUM.sendMedicareAddressService(caseNumber, templateMAUData, policyId);
                            break;
                        }
                    }
                }
            }
            else{
                if(templateBaseDTO.templateBase[0].data.rsoMAU != null) {
                    DemographicMedicareAddress_LS_HUM.sendMedicareAddressService(caseNumber, templateData, policyId);
                }
            }
            if(templateBaseDTO.templateBase.size() > 0) {
                startTemplating(caseNumber, templateBaseDTO.templateBase, templateName);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'processTemplateData');
        } 
    }

    /*
    * Method Name: startTemplating
    * Description: method is responsible to process template data insertion
    * Parameter: caseNumber, templateBaseDTO, templateName
    * Return Type: Nothing
    */
    @Testvisible
    private static void startTemplating(string caseNumber, 
        List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateBaseDTO, string templateName) {
        map<string, string> templateNameSOIdMap = new map<string, string>();
        map<string, string> templateNameIdMap = new map<string, string>();
        map<string, string> templateNameSubIdMap = new map<string, string>();
        list<Template_Submission_Data__c> templateSubmissionDataList = new list<Template_Submission_Data__c>();
            try{
                templateNameSOIdMap = insertTemplateSubmissionOwner(caseNumber, templateBaseDTO);
                if(templateNameSOIdMap.size() > 0) {
                    templateNameIdMap = fetchTemplateDetails(templateNameSOIdMap);
                    if(templateNameIdMap.size() > 0) {
                        templateNameSubIdMap = insertTemplateSubmission(templateNameSOIdMap, templateNameIdMap);
                        if(templateNameSubIdMap.size() > 0) {
                            templateSubmissionDataList = insertTemplateSubmissionData(templateNameSubIdMap, templateNameIdMap, templateBaseDTO, templateName);
                        }
                    }
                }
            }
            catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName,'startTemplating');
        }
    }

    /*
    * Method Name: insertTemplateSubmissionOwner
    * Description: method is responsible to process template data insertion
    * Parameter: caseNumber, templateBaseDTO, templateName
    * Return Type: map<string, string>
    */
    private static map<string, string> insertTemplateSubmissionOwner(string caseNumber, List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateBaseDTO)
    {
        List<Template_Submission_Owner__c> templateSubOwnerList = new List<Template_Submission_Owner__c>();
        map<string, string> templateSubOwnerMap = new map<string, string>();
        try{
            for(DemographicUpdateTemplate_DTO_HUM.TemplateBase template : templateBaseDTO) {
                Template_Submission_Owner__c objTemplateSubmissionOwner = new Template_Submission_Owner__c();
                objTemplateSubmissionOwner.Object_Owner_ID__c= caseNumber;
                objTemplateSubmissionOwner.Object_Owner_Type__c= 'Case';
                templateSubOwnerList.add(objTemplateSubmissionOwner);
            }
            
            if(templateSubOwnerList.size() > 0) {
                Database.insert(templateSubOwnerList);
                for(DemographicUpdateTemplate_DTO_HUM.TemplateBase template : templateBaseDTO) {
                    for(Template_Submission_Owner__c templateSO : templateSubOwnerList) {
                        templateSubOwnerMap.put(template.templateName, templateSO.Id);
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmissionOwner');
        }
        return templateSubOwnerMap;
    }
    
    /*
    * Method Name: fetchTemplateDetails
    * Description: method is responsible to fetch template details for data insertion
    * Parameter: trmplateNameSubOwnrIdMap
    * Return Type: map<string, string>
    */
    @TestVisible 
    private static map<string, string> fetchTemplateDetails(map<string, string> templateNameSOIdMap)
    {   
        map<string, string> templateNameIdMap = new map<string, string>();
        List<string> templateNameList = new List<string>();
        List<Template__c> templateList = new List<Template__c>();
        try{
            for(string key : templateNameSOIdMap.keySet()) {
                templateNameList.add(key);
            }
            templateList = [SELECT Id, Description__c FROM Template__c where Description__c in :templateNameList];
            if(templateList.size() > 0) {
                for(Template__c template : templateList) {
                    templateNameIdMap.put(template.Description__c, template.Id);
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmissionOwner');
        }    

        return templateNameIdMap;
    }
    
    /*
    * Method Name: insertTemplateSubmission
    * Description: method is responsible to fetch template details for data insertion
    * Parameter: templateNameSubOwnrIdMap
    * Return Type: map<string, string>
    */
    @TestVisible
    private static map<string, string> insertTemplateSubmission(map<string, string> templateNameSOIdMap, map<string, string> templateNameIdMap) {
        List<Template_Submission__c> templateSubmissionList = new List<Template_Submission__c>();
        map<string, string> templateNameSubIdMap = new map<string, string>();
        try{
            for(string key : templateNameIdMap.keySet()) {
                Template_Submission__c objTemplateSubmission = new Template_Submission__c();
                objTemplateSubmission.Submission_Owner__c = templateNameSOIdMap.get(key);
                objTemplateSubmission.Template__c = templateNameIdMap.get(key);
                objTemplateSubmission.Version__c = 1.0;
                templateSubmissionList.add(objTemplateSubmission);
            }
            if(templateSubmissionList.size() > 0) {
                Database.insert(templateSubmissionList);
                for(string key : templateNameIdMap.keySet()) {
                    for(Template_Submission__c templateSub : templateSubmissionList) {
                        if(templateSub.Template__c == templateNameIdMap.get(key)) {
                            templateNameSubIdMap.put(key, templateSub.Id);
                            break;
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmission');
        } 
        return templateNameSubIdMap;
    }
    
    /*
    * Method Name: insertTemplateSubmissionData
    * Description: method is responsible to insert data into template fields
    * Parameter: templateNameSubIdMap, templateNameIdMap, templateBaseDTO, templateName
    * Return Type: list<Template_Submission_Data__c>
    */
    @Testvisible
    private static list<Template_Submission_Data__c> insertTemplateSubmissionData(map<string, string> templateNameSubIdMap, map<string, string> templateNameIdMap,
        List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateBaseDTO, string templateName) {

        list<Template_Submission_Data__c> templateSubmissionDataList = new list<Template_Submission_Data__c>();
        list<Template_Field__c> templatefieldsList = new list<Template_Field__c>();
        map<string, list<Template_Field__c>> templateNameFieldMap = new map<string, list<Template_Field__c>>();
        list<string> templateNameIdList = new list<string>();
            try{
            for(string key : templateNameIdMap.keySet()) {
                templateNameIdList.add(templateNameIdMap.get(key));
            }
            if(templateNameIdList.size() > 0){
                templatefieldsList = [SELECT Id, Name, Template__c, Data_Type__c FROM Template_Field__c where Template__c in :templateNameIdList];    
            }

            if(templatefieldsList.size() > 0) {
                for(DemographicUpdateTemplate_DTO_HUM.TemplateBase tempateBase : templateBaseDTO) {
                    string jsonInstance = System.JSON.serialize(tempateBase.data);
                    map<string, object> unTypedInstance = new map<string, object>();
                    unTypedInstance = (map<string, object>)System.JSON.deserializeUntyped(jsonInstance);
                    string keyName = templateName.toLowerCase() + tempateBase.key.toUpperCase();
                    object fieldDTO = unTypedInstance.get(keyName);
                    string fieldJson = System.JSON.serialize(fieldDTO);
                    map<string, object> unTypedFieldInstance = new map<string, object>();
                    unTypedFieldInstance = (map<string, object>)System.JSON.deserializeUntyped(fieldJson);
                    string sFieldName = 'UserInterfaceData';
                    for(Template_Field__c templateField : templatefieldsList) {
                        if(templateField.Template__c == templateNameIdMap.get(tempateBase.templateName)) {
                                Template_Submission_Data__c objTemplateSubmissionData = new Template_Submission_Data__c();
                                objTemplateSubmissionData.Template_Submission__c = templateNameSubIdMap.get(tempateBase.templateName);
                                objTemplateSubmissionData.Template_Field__c = templateField.Id;
                                objTemplateSubmissionData.Value__c = (string)unTypedFieldInstance.get(templateField.Name);
                                templateSubmissionDataList.add(objTemplateSubmissionData);
                        }
                    }
                }
            }

            if(templateSubmissionDataList.size() > 0) {
                Database.upsert(templateSubmissionDataList);
            }
            }
            catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmissionData');
        }
      
        return templateSubmissionDataList;
    }
    
    /*
    * Method Name: processTemplateDataAfterResponse
    * Description: method is responsible to process template data insertion
    * Parameter: caseNumber, tmplateData, templateName, policyMemberId
    * Return Type: Nothing
    */
    @AuraEnabled()
    public static void processTemplateDataAfterResponse(string caseNumber, string templateData, string templateName, string policyId, boolean isSuccess, string responseStatusMap, string responseStatus) {
        try {
            Map<String, String> finalResponseMap = new Map<String, String>();
            //Set ResponseStatusmap Logic for RSO
             if(templateName.equalsIgnoreCase('RSO') && !String.isBlank(responseStatusMap)){
             finalResponseMap = setResponseMap(responseStatusMap);
             }
            // Allows AVF call even demographic updates are success or failure
            DemographicUpdateTemplate_DTO_HUM templateDTO = new DemographicUpdateTemplate_DTO_HUM();
            DemographicUpdateTemplate_DTO_HUM templateBaseDTO = (DemographicUpdateTemplate_DTO_HUM)System.JSON.deserialize(templateData,DemographicUpdateTemplate_DTO_HUM.class);
            DemographicUpdateTemplate_DTO_HUM templateMAUDTO = new DemographicUpdateTemplate_DTO_HUM();
            DemographicUpdateTemplate_DTO_HUM.TemplateBase templateMAUBase = new DemographicUpdateTemplate_DTO_HUM.TemplateBase();
            List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateMAUBaselst = new List<DemographicUpdateTemplate_DTO_HUM.TemplateBase>(); 
            if(HUMUtilityHelper.isCRMFunctionalityON('1935446')){
                if(templateBaseDTO.templateBase.size() > 0) {
                    for(DemographicUpdateTemplate_DTO_HUM.TemplateBase base : templateBaseDTO.templateBase) {
                        if(base.data.rsoMAU != null) {
                            templateMAUBaselst.add(base);
                            templateMAUDTO.templateBase = templateMAUBaselst;
                            string templateMAUData = JSON.serialize(templateMAUDTO);
                            DemographicMedicareAddress_LS_HUM.sendMedicareAddressService(caseNumber, templateMAUData, policyId);
                            break;
                        }
                    }
                }
            }
            else{
                if(templateBaseDTO.templateBase[0].data.rsoMAU != null) {
                    DemographicMedicareAddress_LS_HUM.sendMedicareAddressService(caseNumber, templateData, policyId);
                }
            }
            if(templateBaseDTO.templateBase.size() > 0) {
                startTemplatingOnResponse(caseNumber, templateBaseDTO.templateBase, templateName, finalResponseMap, responseStatus);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'processTemplateDataAfterResponse');
        } 
    }

    /*
    * Method Name: setResponseMap
    * Description: method is responsible to create map of RequestType and ResponseStatus
    * Parameter: responseStatusMap
    * Return Type: Map of RequestType and ResponseStatus
    */
    @Testvisible
    private static Map<String, String> setResponseMap(string responseStatusMap) {
        
        Map<String, String> updateStatusMap = new Map<String, String>();
        Map<String, String> statusMap = new Map<String, String>();
        String demographicType = Label.Demo_Fields;
        List<String> lstdemographicType = demographicType.split(',');
        String biographicType = Label.Bio_Fields;
        List<String> lstbiographicType = biographicType.split(',');
        String addressType = 'Address';
        Set<String> demoStatusSet = new Set<String>();
        Set<String> bioStatusSet = new Set<String>(); 
        Set<String> addressStatusSet = new Set<String>();

        try{
            UpdateMemberDemographicsResponse_DTO_HUM responseDTO = (UpdateMemberDemographicsResponse_DTO_HUM)System.JSON.deserialize(responseStatusMap, UpdateMemberDemographicsResponse_DTO_HUM.class);
            List<UpdateMemberDemographicsResponse_DTO_HUM.Members> memberList = responseDTO.UpdateMemberResponse.members;
            List<UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion> criterionList = new List<UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion>();
            if(memberList != null && !memberList.isEmpty()){
                for(UpdateMemberDemographicsResponse_DTO_HUM.Members member:memberList){
                    for(UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion membercrit:member.membercriterion)
                    criterionList.add(membercrit);
                }
            }
            if(criterionList != null && !criterionList.isEmpty()){
               for(UpdateMemberDemographicsResponse_DTO_HUM.MemberCriterion membercritValue:criterionList){
                   for(UpdateMemberDemographicsResponse_DTO_HUM.UpdateStatus status:membercritValue.updatestatus){
                     //Separate Response Status for Demographic and Biographic and Address
                    if(lstdemographicType.contains(status.requesttype)){
                        demoStatusSet.add((status.status).toUpperCase());
                    }else if(lstbiographicType.contains(status.requesttype)){
                        bioStatusSet.add((status.status).toUpperCase());
                    }else if(addressType.equalsIgnoreCase(status.requesttype)){
                        addressStatusSet.add((status.status).toUpperCase());
                    }
                   }
               } 
            }
            //Seperate map of RequestType and Response status to Demographic Or Biographic Or Address level
            statusMap = setFinalStatusMap(demoStatusSet,bioStatusSet,addressStatusSet);
            }
            catch(Exception ex) {
                HUMExceptionHelper.logErrors(ex, sClassName,'setResponseMap');
            }
        return statusMap;
    }
    
    /*
    * Method Name: setFinalStatusMap
    * Description: method is responsible to create map of RequestType and ResponseStatus at Demographic Or biographic and Address level
    * Parameter: updateStatusMap
    * Return Type: Final Map of RequestType and ResponseStatus at Demographic Or biographic and Address level
    */

    @Testvisible
    private static Map<String, String> setFinalStatusMap(Set<String> demoStatusSet, Set<String> bioStatusSet, Set<String> addressStatusSet) {
        
        Map<String, String> finalStatusMap = new Map<String, String>();
        
        try{
            //Put Final Response status for Demographic in FinalStatusMap
            if(demoStatusSet != NULL && !demoStatusSet.isEmpty()){
               if(demoStatusSet.contains('FAILED')){
               finalStatusMap.put('cod', 'FAILED');
               }else{
               finalStatusMap.put('cod', 'SUCCESS');   
               }
            }else{
               finalStatusMap.put('cod', 'FAILED');
            }
            //Put Final Response status for Biographic in FinalStatusMap
            if(bioStatusSet != NULL && !bioStatusSet.isEmpty()){
               if(bioStatusSet.contains('FAILED')){
               finalStatusMap.put('crd', 'FAILED');
               }else{
               finalStatusMap.put('crd', 'SUCCESS');   
               }
            }else{
               finalStatusMap.put('crd', 'FAILED'); 
            }
            //Put Final Response status for Address in FinalStatusMap
            if(addressStatusSet != NULL && !addressStatusSet.isEmpty()){
               if(addressStatusSet.contains('FAILED')){
               finalStatusMap.put('mau', 'FAILED');
               finalStatusMap.put('mdu', 'FAILED');
               }else{
               finalStatusMap.put('mau', 'SUCCESS'); 
               finalStatusMap.put('mdu', 'SUCCESS');
               }
            }else{
               finalStatusMap.put('mau', 'FAILED');
               finalStatusMap.put('mdu', 'FAILED'); 
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName,'setFinalStatusMap');
        }
        return finalStatusMap;
    }
    /*
    * Method Name: startTemplatingOnResponse
    * Description: method is responsible to process template data insertion
    * Parameter: caseNumber, templateBaseDTO, templateName, finalResponseMap, responseStatus
    * Return Type: Nothing
    */
    @Testvisible
    private static void startTemplatingOnResponse(string caseNumber, 
        List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateBaseDTO, string templateName, map<String,String> finalResponseMap, string responseStatus) {
        map<string, string> templateNameSOIdMap = new map<string, string>();
        map<string, string> templateNameIdMap = new map<string, string>();
        map<string, string> templateNameSubIdMap = new map<string, string>();
        list<Template_Submission_Data__c> templateSubmissionDataList = new list<Template_Submission_Data__c>();
        try {
            templateNameSOIdMap = insertTemplateSubmissionOwnerOnResponse(caseNumber, templateBaseDTO);
            if(templateNameSOIdMap.size() > 0) {
                templateNameIdMap = fetchTemplateDetailsOnResponse(templateNameSOIdMap);
                if(templateNameIdMap.size() > 0) {
                    templateNameSubIdMap = insertTemplateSubmissionOnResponse(templateNameSOIdMap, templateNameIdMap);
                    if(templateNameSubIdMap.size() > 0) {
                        templateSubmissionDataList = insertTemplateSubmissionDataOnResponse(templateNameSubIdMap, templateNameIdMap, templateBaseDTO, templateName, finalResponseMap, responseStatus);
                    	}
                }
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName,'startTemplatingOnResponse');
        }
    }

    
    
    /*
    * Method Name: insertTemplateSubmissionOwnerOnResponse
    * Description: method is responsible to process TemplateSubOwner data insertion
    * Parameter: caseNumber, templateBaseDTO
    * Return Type: map<string, string>
    */
    private static map<string, string> insertTemplateSubmissionOwnerOnResponse(string caseNumber, List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateBaseDTO)
    {
        List<Template_Submission_Owner__c> templateSubOwnerList = new List<Template_Submission_Owner__c>();
        map<string, string> templateSubOwnerMap = new map<string, string>();
        try{
            for(DemographicUpdateTemplate_DTO_HUM.TemplateBase template : templateBaseDTO) {
                Template_Submission_Owner__c objTemplateSubmissionOwner = new Template_Submission_Owner__c();
                objTemplateSubmissionOwner.Object_Owner_ID__c= caseNumber;
                objTemplateSubmissionOwner.Object_Owner_Type__c= 'Case';
                templateSubOwnerList.add(objTemplateSubmissionOwner);
               
            }
            
            if(templateSubOwnerList.size() > 0) {
                Database.insert(templateSubOwnerList);
                for(DemographicUpdateTemplate_DTO_HUM.TemplateBase template : templateBaseDTO)  {
                    for(Template_Submission_Owner__c templateSO : templateSubOwnerList) {
                         templateSubOwnerMap.put(template.templateName, templateSO.Id);
                       
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmissionOwnerOnResponse');
        }
        return templateSubOwnerMap;
    }
    
    /*
    * Method Name: fetchTemplateDetailsOnResponse
    * Description: method is responsible to create TemplateName and Id map
    * Parameter: trmplateNameSubOwnrIdMap
    * Return Type: map<string, string>
    */
    @TestVisible 
    private static map<string, string> fetchTemplateDetailsOnResponse(map<string, string> templateNameSOIdMap)
    {   
        map<string, string> templateNameIdMap = new map<string, string>();
        List<string> templateNameList = new List<string>();
        List<Template__c> templateList = new List<Template__c>();
        try{
            for(string key : templateNameSOIdMap.keySet()) {
                templateNameList.add(key);
            }
            templateList = [SELECT Id, Description__c FROM Template__c where Description__c in :templateNameList];
            if(templateList.size() > 0) {
                for(Template__c template : templateList) {
                    templateNameIdMap.put(template.Description__c, template.Id);
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'fetchTemplateDetailsOnResponse');
        }
        return templateNameIdMap;
    }
    
    /*
    * Method Name: insertTemplateSubmissionOnResponse
    * Description: method is responsible to fetch template details for data insertion
    * Parameter: templateNameSOIdMap, templateNameIdMap
    * Return Type: map<string, string>
    */
    @TestVisible
    private static map<string, string> insertTemplateSubmissionOnResponse(map<string, string> templateNameSOIdMap, map<string, string> templateNameIdMap) {
        List<Template_Submission__c> templateSubmissionList = new List<Template_Submission__c>();
        map<string, string> templateNameSubIdMap = new map<string, string>();
        try {
            for(string key : templateNameIdMap.keySet()) {
                Template_Submission__c objTemplateSubmission = new Template_Submission__c();
                objTemplateSubmission.Submission_Owner__c = templateNameSOIdMap.get(key);
                objTemplateSubmission.Template__c = templateNameIdMap.get(key);
                objTemplateSubmission.Version__c = 1.0;
                templateSubmissionList.add(objTemplateSubmission);
            }
            if(templateSubmissionList.size() > 0) {
                Database.insert(templateSubmissionList);
                for(string key : templateNameIdMap.keySet()) {
                    for(Template_Submission__c templateSub : templateSubmissionList) {
                        if(templateSub.Template__c == templateNameIdMap.get(key)) {
                            templateNameSubIdMap.put(key, templateSub.Id);
                            break;
                        }
                    }
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmissionOnResponse');
        }
        return templateNameSubIdMap;
    }
    
    /*
    * Method Name: insertTemplateSubmissionDataOnResponse
    * Description: method is responsible to insert data into template fields
    * Parameter: templateNameSubIdMap, templateNameIdMap, templateBaseDTO, templateName
    * Return Type: list<Template_Submission_Data__c>
    */
    @Testvisible
    private static list<Template_Submission_Data__c> insertTemplateSubmissionDataOnResponse(map<string, string> templateNameSubIdMap, map<string, string> templateNameIdMap,
        List<DemographicUpdateTemplate_DTO_HUM.TemplateBase> templateBaseDTO, string templateName, map<String,String> finalResponseMap, string responseStatus) {

        list<Template_Submission_Data__c> templateSubmissionDataList = new list<Template_Submission_Data__c>();
        list<Template_Field__c> templatefieldsList = new list<Template_Field__c>();
        map<string, list<Template_Field__c>> templateNameFieldMap = new map<string, list<Template_Field__c>>();
        list<string> templateNameIdList = new list<string>();
        try{
            for(string key : templateNameIdMap.keySet()) {
                templateNameIdList.add(templateNameIdMap.get(key));
            }
            if(templateNameIdList.size() > 0){
                templatefieldsList = [SELECT Id, Name, Template__c, Data_Type__c FROM Template_Field__c where Template__c in :templateNameIdList];    
            }

            if(templatefieldsList.size() > 0) {
                for(DemographicUpdateTemplate_DTO_HUM.TemplateBase tempateBase : templateBaseDTO) {
                    string jsonInstance = System.JSON.serialize(tempateBase.data);
                    map<string, object> unTypedInstance = new map<string, object>();
                    unTypedInstance = (map<string, object>)System.JSON.deserializeUntyped(jsonInstance);
                    string keyName = templateName.toLowerCase() + tempateBase.key.toUpperCase();
                    object fieldDTO = unTypedInstance.get(keyName);
                    string fieldJson = System.JSON.serialize(fieldDTO);
                    map<string, object> unTypedFieldInstance = new map<string, object>();
                    unTypedFieldInstance = (map<string, object>)System.JSON.deserializeUntyped(fieldJson);
                    string sFieldName = 'UserInterfaceData';
                    for(Template_Field__c templateField : templatefieldsList) {
                        if(templateField.Template__c == templateNameIdMap.get(tempateBase.templateName)) {
                                if(templateField.Name == 'ServiceResponse'){//Set value for ServiceResponse Field
                                Template_Submission_Data__c objTemplateSubmissionData = new Template_Submission_Data__c();
                                objTemplateSubmissionData.Template_Submission__c = templateNameSubIdMap.get(tempateBase.templateName);
                                objTemplateSubmissionData.Template_Field__c = templateField.Id;
                                if(finalResponseMap != NULL && !finalResponseMap.isEmpty() && templateName.equalsIgnoreCase('RSO')){                                           
                                objTemplateSubmissionData.Value__c = finalResponseMap.get(tempateBase.key).toUpperCase();
                                }else{
                                objTemplateSubmissionData.Value__c = responseStatus.toUpperCase();       
                                }
                                templateSubmissionDataList.add(objTemplateSubmissionData);    
                                }else{              
                                Template_Submission_Data__c objTemplateSubmissionData = new Template_Submission_Data__c();
                                objTemplateSubmissionData.Template_Submission__c = templateNameSubIdMap.get(tempateBase.templateName);
                                objTemplateSubmissionData.Template_Field__c = templateField.Id;
                                objTemplateSubmissionData.Value__c = (string)unTypedFieldInstance.get(templateField.Name);
                                templateSubmissionDataList.add(objTemplateSubmissionData);
                                }
                        }
                    }
                }
            }
            if(templateSubmissionDataList.size() > 0) {
                Database.upsert(templateSubmissionDataList);
            }
            
         }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, sClassName,'insertTemplateSubmissionDataOnResponse');
        }
        return templateSubmissionDataList;
        
    }
}