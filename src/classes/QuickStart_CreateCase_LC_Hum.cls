/*****************************************************************************************************************************************************************************
@Apex Class Name  : QuickStart_CreateCase_LC_Hum
@Version          : 1.0
@Created Date     : 07/12/2022
@Test class       : QuickStart_CreateCase_LT_Hum
******************************************************************************************************************************************************************************
Modification Log :  

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                   07/12/2022                Original Version, Code to created case from Quick Start
* Prasuna Pattabhi										08/17/2022					3272650 - Associate Interaction to Case
* Prasuna Pattabhi										09/02/2022					3723454 - CI Error Message Display
* Prasuna Pattabhi										09/19/2022					3723454 - Fix for HP Cases Creation
* Prasuna Pattabhi										09/30/2022					Removed the CCS 205 check as per discussion with Jasmeen
* Disha Dole 											20/01/2023 				US:4085171 - T1PRJ0865978 - C06, Lightning-Case Management- Quick Start-Associate to Policy, alignment & display issue fixes
* Pooja Kumbhar	                                    06/01/2023                US:4583426 - T1PRJ0865978 - C06- Case Management - MF 26447 - Provider QuickStart- Implement Callback Number, G&A, Duplicated C/I logic
******************************************************************************************************************************************************************************/

Public With sharing Class QuickStart_CreateCase_LC_Hum {   

    /**
    * To check the user has record access
    * @return boolean
    **/
    private static boolean verifyRecordAccess(String recordId,Id userId){
        
        List<UserRecordAccess> userRecordAccess = [SELECT RecordId, HasReadAccess 
            FROM UserRecordAccess WHERE UserId =:userId AND RecordId =: recordId LIMIT 1];
        if(!userRecordAccess.isEmpty()){
            return userRecordAccess[0].HasReadAccess;
        }else{
            return false;
        }

    } 

        
    /**
    * To check the account is activie and not etl deleted
    * @return Account
    **/
    @TestVisible
    private static Account queryActiveAccount(String accountId){         
        
        List<Account> lstActiveAcct = [SELECT Id, RecordTypeId, RecordType.Name, NPI_ID__c,ETL_Record_Deleted__c, 
                        (SELECT Consumer_ID__c FROM ConsumerIDs__r LIMIT 1)  
				   FROM Account  WHERE Id = :accountId LIMIT 1];         
        if(!lstActiveAcct.isEmpty()){
            if(lstActiveAcct[0].ETL_Record_Deleted__c == true){
                return null;
            }else{                
                return lstActiveAcct[0];
            }
        }else{
            return null;
        }        
    }

    /**
    * To check the MemberPlan is activie and not etl deleted
    * @return MemberPlan
    **/
    @TestVisible
    private static MemberPlan queryActiveMemberPlan(string recordId){
        List<MemberPlan> lstPolMem = [SELECT Id, Name, MemberId, ETL_Record_Deleted__c FROM MemberPlan WHERE Id = :recordId LIMIT 1];
        if(!lstPolMem.isEmpty()){
            if(lstPolMem[0].ETL_Record_Deleted__c == true){
                return null;
            }else{
                return lstPolMem[0];
            }
        }
        return null;
    }

    /**
    * To check the case record type
    * @return Id
    **/
    @TestVisible
    private static Id getCaseRecordType(String recordTypeName,Boolean isHpCase,String profileName,String currentUserQueue){
        Map<String, HUMAccountCaseRecordTypes__c> accTypesMap = HUMAccountCaseRecordTypes__c.getAll();        
        Id sCaseRecordType=null ;
        Boolean pharmacyFlag = false;
        String sCaseRecName = '';        
        if(isHpCase){
            pharmacyFlag=true;
        }else{            
            if(profileName == 'Humana Pharmacy Specialist' || currentUserQueue == 'PDP Pilot Humana Pharmacy Calls'){
                pharmacyFlag=true;
            }
        }
        for (String accCaseRec : accTypesMap.keySet()) {
            if(accTypesMap.get(accCaseRec).Account_RecordType__c == recordTypeName && 
            accTypesMap.get(accCaseRec).Account_RecordType__c != NULL && 
            accTypesMap.get(accCaseRec).Humana_Pharmacy__c == pharmacyFlag) {
                sCaseRecName = accTypesMap.get(accCaseRec).Case_RecordType__c;
                sCaseRecName =  sCaseRecName.trim();
                sCaseRecordType = Schema.SObjectType.Case.getRecordTypeInfosByName().get(String.valueOf(sCaseRecName)).getRecordTypeId();                
            }
        }
        return sCaseRecordType;
    }

    /**
    * To return the active interaction associated with the case and assign the values to the case.
    * @return Case
    **/
    private static Case getActiveInteractionMember(Case neweCase, string interactionId){        
        
        List<Interaction_Member__c> intMemLst = [SELECT Interaction__r.Interacting_With_Type__c, 
            Interaction__r.Interacting_With__c, Interaction__r.Caller_Name__c, 
            Interaction__r.Interaction_Origin__c, Interacting_About_Type__c 
            FROM Interaction_Member__c WHERE Interacting_About__c = :neweCase.AccountId AND 
            Interaction__c = :interactionId ORDER BY CreatedDate DESC LIMIT 1];
        if(!intMemLst.isEmpty()){
            neweCase.Interaction_ID__c = interactionId;
            neweCase.Origin = intMemLst[0].Interaction__r.Interaction_Origin__c;
            neweCase.Interacting_With_Type__c = intMemLst[0].Interaction__r.Interacting_With_Type__c;
            neweCase.Interacting_With__c = intMemLst[0].Interaction__r.Interacting_With__c;
            neweCase.Interacting_With_Name__c = intMemLst[0].Interaction__r.Caller_Name__c;
            neweCase.Interacting_About_Type__c = intMemLst[0].Interacting_About_Type__c;         
        }  
        return neweCase; 
    }
    
    /**
    *Check if there any case available with the member plan id,  interaction Id, account Id and CTCI list.
    * @return Boolean
    **/
    @TestVisible
    private static Boolean verifyCasesByActiveInteraction(Case oNewCase){
        if(oNewCase.Interaction_ID__c!=null && oNewCase.Interaction_ID__c!=''){
            
            List<Case> lstExistingCases = [SELECT Id, CTCI_List__c  FROM Case  WHERE 
            Member_Plan_Id__c = :oNewCase.Member_Plan_Id__c AND  Interaction_ID__c = :oNewCase.Interaction_ID__c AND 
            AccountId = :oNewCase.AccountId AND CTCI_List__c =: oNewCase.CTCI_List__c];
            
            return !lstExistingCases.isEmpty();        
        }else{
            return false;
        }
    }      
    
    /**
        *Queries the policy page to check Product Code and Product type code
        * @return Boolean
    **/
    @AuraEnabled
    public static Boolean queryPolicyByClassification(String policyId,String complaintVal){
        Boolean isPolicyProductType = true;
        Map<String,String> productType = new Map<String,String>();
        productType.put('MEF','MEF');
        productType.put('MER','MER');
        productType.put('MEP','MEP');
        productType.put('MES','MES');
        productType.put('MGP','MGP');
        productType.put('MGR','MGR');
        productType.put('MPD','MPD');
        productType.put('MRO','MRO');
        productType.put('MRP','MRP');
        productType.put('PDP','PDP');
        
            List<MemberPlan> lstMemberPlan =[SELECT id, Product_Type__c,Product_Type_Code__c  FROM MemberPlan WHERE id = : policyId];            
            if(!lstMemberPlan.isEmpty() && String.isNotBlank(complaintVal)){
                for(MemberPlan p : lstMemberPlan){            
                    if(
                        (p.Product_Type_Code__c == 'PDP' && (complaintVal.equals('Yes - Medicare Part C') || complaintVal.equals('Yes - Medicaid')))
                        || (p.Product_Type_Code__c == 'MAPD' &&  complaintVal.equals('Yes - Medicaid')) 
                        || (p.Product_Type_Code__c == 'MA' &&  (complaintVal.equals('Yes - Medicare Part D') || complaintVal.equals('Yes - Medicaid')))
                        || ((p.Product_Type_Code__c =='' || p.Product_Type_Code__c == null) && productType.containsKey(p.Product_Type__c)  && complaintVal.equals('Yes - Medicaid'))
                        || (p.Product_Type__c == 'MCD' && (complaintVal.equals('Yes - Medicare Part D') ||(complaintVal.equals('Yes - Medicare Part C'))))
                        || ((complaintVal.equals('Yes - Medicare Part D') ||complaintVal.equals('Yes - Medicare Part C') || complaintVal.equals('Yes - Medicaid')) &&(!productType.containsKey(p.Product_Type__c))) 
                    ){
                        isPolicyProductType = true;
                    }else{
                        isPolicyProductType = false;
                    }
                }
            }
        
        return isPolicyProductType;   
    }    
    
    /**
        *Queries the Cases to check there any case avilable with the same details created in last 14 days
        * @return Boolean
    **/
    private static Boolean VerifyCasesByClassification(Case oNewCase){
        
        Boolean dupClassification = false;
        
        List<Case> lstCases = [SELECT Id, CTCI_List__c FROM Case
                WHERE Member_Plan_Id__c = :oNewCase.Member_Plan_Id__c AND AccountId = :oNewCase.AccountId AND 
                CTCI_List__r.Classification__c =: oNewCase.Classification_Id__c AND 
                status != 'Cancelled' AND createddate = LAST_N_DAYS:14 LIMIT 1]; 

        if(!lstCases.isEmpty()) {            
            dupClassification = true;
        }
        return dupClassification;         
    }

    /**
        * All the required checks performed and proceeded with the case creation
        * @return Map
    **/
    @AuraEnabled
    public static Map<String,String> validateCreateCase(String caseData,String additionalInfo){

        Map<String,String> result  =  new Map<String,String>();  
        try{            
            Case oNewCase = (Case)JSON.deserialize(caseData,Case.class); 
            Map<String,Object> addlInfo =(Map<String,Object>)JSON.deserializeUntyped(additionalInfo);        
            Boolean executeNextStep = true;
            String ctciId;
            Boolean bIsHPCase = false;
            
            Boolean bProviderUser = ''+addlInfo.get('bProviderUser')=='true'?true:false;
            String recordId = ''+addlInfo.get('recordId');
            String userId = ''+addlInfo.get('userId');
            String profileName = ''+addlInfo.get('profileName');
            String classificationtype = ''+addlInfo.get('classificationTypeId');
            Boolean bIsAccount = ''+addlInfo.get('bIsAccount')=='true'?true:false;
            String interactionId = ''+addlInfo.get('interactionId');
            Boolean bAllowMultipleCase = ''+addlInfo.get('bAllowMultipleCase')=='true'?true:false;
            String classificationLabel = ''+addlInfo.get('classificationLabel');
            String intentLabel = ''+addlInfo.get('intentLabel');
			Id classificationId = ''+addlInfo.get('classificationId');
            Id intentId = ''+addlInfo.get('intentId');

            Boolean hasAccess = verifyRecordAccess(recordId,userId);
            if(!hasAccess){
                executeNextStep = false;
                result.put('errorOnSave','true');
                result.put('messageDetails','You do not have proper permissions to create a case for this Member.');
                result.put('messageType','Error'); 
            }
            if(executeNextStep){
                if(classificationLabel =='' || classificationLabel== null){
                    executeNextStep = false;
                    result.put('errorOnSave','true');
                    result.put('messageType','Error'); 
                }
            }
            if(executeNextStep){
                oNewCase.Status = 'In Progress';
                oNewCase.Type = 'Customer Inquiry';
                oNewCase.Origin = 'Inbound Call';                
                oNewCase.Call_Benefit_Category__c = null;

                Boolean bHasHPPermSet = HUMUtilityHelper.hasUserPharmacy206Access(); 
                if(bHasHPPermSet){             
                    bIsHPCase = QuickStart_LH_Hum.isHPClassificationIntent(classificationId,intentId,classificationtype );
                }else{
                    bIsHPCase = false;
                }
                result.put('bIsHPCase',''+bIsHPCase);

                if(bIsAccount){
                    Account account = queryActiveAccount(recordId);
                    if(account != null){
                        oNewCase.AccountId = account.Id;
                        oNewCase.RecordTypeId = getCaseRecordType(account.RecordType.Name,bIsHPCase,profileName,oNewCase.Owner_Queue__c);
                        oNewCase.NPI_ID__c = account.NPI_ID__c;
                        if(!account.ConsumerIDs__r.isEmpty()) {
                            oNewCase.Tax_ID__c = account.ConsumerIDs__r[0].Consumer_ID__c;
                        }
                        Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Case.getRecordTypeInfosById();        
                        if(null != mapRecordType && null != mapRecordType.get(oNewCase.RecordTypeId) && 
                        String.isNotBlank(mapRecordType.get(oNewCase.RecordTypeId).getName())
                        && mapRecordType.get(oNewCase.RecordTypeId).getName().containsIgnoreCase('Unknown Case')){
                            if(String.isBlank(oNewCase.G_A_Rights_Given__c)){
                                oNewCase.G_A_Rights_Given__c = 'No';
                            }
                            if(String.isBlank(oNewCase.Complaint__c)){
                                oNewCase.Complaint__c = 'No';
                            }
                        }
                    }else{
                        executeNextStep = false;
                        result.put('errorOnSave','true');
                        result.put('messageDetails','Cases cannot be created for a legacy deleted Account / Policy Member. Please select a different Account / Policy Member.');
                        result.put('messageType','Error');
                    }                
                }else{
                    String policyId ='';
                    String membeAccount;
                    MemberPlan memberPlan = queryActiveMemberPlan(recordId);
                    if(memberPlan!=null){
                        policyId = memberPlan.Id;
                        membeAccount = memberPlan.MemberId;
                        oNewCase.Member_Plan_Id__c = memberPlan.Id;
                    }
                    if(policyId != ''){                        
                        Account account = queryActiveAccount(membeAccount);
                        if(account != null){
                            oNewCase.AccountId = account.Id;
                            oNewCase.RecordTypeId = getCaseRecordType(account.RecordType.Name,bIsHPCase,profileName,oNewCase.Owner_Queue__c);
                        }else{
                            executeNextStep = false;
                            result.put('errorOnSave','true');
                            result.put('messageDetails','Cases cannot be created for a legacy deleted Account / Policy Member. Please select a different Account / Policy Member.');
                            result.put('messageType','Error');
                        }
                    }else{
                        executeNextStep = false;
                        result.put('errorOnSave','true');
                        result.put('messageDetails','Cases cannot be created for a legacy deleted Account / Policy Member. Please select a different Account / Policy Member.');
                        result.put('messageType','Error');
                    }
                }
            }
            
            if(interactionId!='' && executeNextStep){
                oNewCase = getActiveInteractionMember(oNewCase, interactionId);
                if(oNewCase.Interaction_ID__c=='' || oNewCase.Interaction_ID__c==null){
                    executeNextStep = false;
                    result.put('errorOnSave','true');
                    result.put('messageDetails','Interaction is not assocaited with the Account. Please provide Valid Interaction Id');
                    result.put('messageType','Error');
                }
            }

            if(executeNextStep && !bProviderUser){            
                Boolean bisClaimsCall = classificationLabel.equalsIgnoreCase('Claims');
                if(!bisClaimsCall && !bAllowMultipleCase) {
                    Boolean bErrorDuplicClassIntent = verifyCasesByActiveInteraction(oNewCase);
                    executeNextStep = !bErrorDuplicClassIntent;
                    if(bErrorDuplicClassIntent){                        
                        result.put('errorOnSave','true');                        
                        result.put('messageDetails','A case with the selected Classification and Intent has already been created for this interaction.');
                        result.put('messageType','Error');
                    }
                }
            }
            
            if(executeNextStep && !bProviderUser && 
                (oNewCase.Interacting_With_Type__c == 'Member' || 
                oNewCase.Interacting_With_Type__c == 'Member Representative or Caregiver')){

                Boolean dupClassification = VerifyCasesByClassification(oNewCase);
                if(dupClassification){
                    executeNextStep = false;
                    result.put('errorOnSave','true');
                    result.put('messageDetails','A Case(s) with the same Classification was created within the past 14 days. Please review the Member&apos;s Case history. Do you still want to create a new case?');
                    result.put('messageType','Confirmation');
                    result.put('caseData',JSON.serialize(oNewCase));
                }
            }
            if(executeNextStep){  
                result = saveCase(oNewCase,result);
            }            
        }catch (Exception ex) {
            result.put('errorOnSave','true'); 
            result.put('messageDetails',ex.getMessage()+' *** '+ex.getLineNumber()+' *** '+ex.getCause());     
        }   
        return result;
    }

    /**
        * All the required checks performed and proceeded with the case creation
        * @return Map
    **/
    @AuraEnabled
    public static Map<String,String> insertCase(String caseData,String bIsHPCase){
                 
        Map<String,String> result =new Map<String,String>();
        try{
            Case oNewCase = (Case)JSON.deserialize(caseData,Case.class);
            result.put('bIsHPCase',bIsHPCase);
            result = saveCase(oNewCase,result);
        }catch (Exception ex) { 
            result.put('errorOnSave','true'); 
            result.put('messageDetails',ex.getMessage()+' *** '+ex.getLineNumber()+' *** '+ex.getCause());
        }
        return result;
    }

    /**
        * Inserts the case to the db and keeps track of the result in a map
        * @return Map
    **/
    private static Map<String,String> saveCase(Case oNewCase,Map<String,String> result){

            String sHasSaveError = '';        
            Boolean success = false;
            database.SaveResult oSaveSuccess = database.insert(oNewCase, false);            
            if(!oSaveSuccess.isSuccess()) {
                for(Database.Error err : oSaveSuccess.getErrors()) {                    
                    String sErroFieldLabels = '';
                    for(String sErrorField : err.getFields()) {
                        Schema.SObjectField sTemplabel = Constants_C_AHT_HUM.MAP_CASE_FIELDS.get(sErrorField);
                        if(sTemplabel != null) {
                            sErroFieldLabels += sTemplabel.getDescribe().getLabel() +', ';  
                        }
                    }
                    if(sErroFieldLabels.length() > 1) {
                        sErroFieldLabels = sErroFieldLabels.substring(0, sErroFieldLabels.trim().length() -1);
                    }
                    if(String.isNotBlank(sErroFieldLabels)) {
                        sHasSaveError += sErroFieldLabels +': '+ err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';
                    }else{
                        sHasSaveError += err.getMessage().unescapeHtml4().escapeHtml4() + '<br>';   
                    }
                }
                result.put('errorOnSave','true');
                result.put('messageDetails','Quick Start could not associate to a record. Please try again or contact your administrator.'+sHasSaveError);                    
                result.put('messageType','Error');                    
            }else{
                result.put('caseId',oNewCase.Id);
                result.put('errorOnSave','false'); 
                success = true;
            }
            if(!success){result.put('caseId','');}        
        return result;
    }
    
    /**
        * returns the case number for the given case id
        * @return String
    **/
    @AuraEnabled
    public static String getCaseNumber(String caseId){        
        String caseNumber = ''; 
        Case savedCase = [SELECT CaseNumber FROM Case WHERE id =: caseId];
        if(savedCase!=null){
            caseNumber = savedCase.CaseNumber;
        }
        return caseNumber;
    }   
}