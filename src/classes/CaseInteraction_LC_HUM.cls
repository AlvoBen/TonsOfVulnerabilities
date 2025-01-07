/***********************************************************************************************************
Apex Class Name  : CaseInteraction_LC_HUM
Version          : 1.0
Function         : This class is for displaying case related Information data in lightning
Test Class       : CaseInteraction_LT_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gowthami Thota                       			02/08/2023                 Original Version
* Prasuna Pattabhi                                        03/02/2023       US_4290170 and US_4286921
* Prasuna Pattabhi                                        03/09/2023       Defect Fix 
************************************************************************************************************/
public with sharing class CaseInteraction_LC_HUM {

    /**
    * Function: method called to return lsit of Case Interaction records
    * @param  String
    * @return String
    */
    @AuraEnabled
    public static string getInteractions(String sCaseRecordId) 
	{
        return JSON.serialize(getInteractionsList(sCaseRecordId));
    }
	/**
    * Function: This method fetches Case Interaction data in wrapper.
    * @param  String, Boolean, Boolean
    * @return List<String>
    */
    
  	Private Static List<CaseInteractionDTO_LC_HUM> getInteractionsList(String caseRecId){
       
  	 	List<CaseInteractionDTO_LC_HUM> lstCaseint = new List<CaseInteractionDTO_LC_HUM>();
        List<Case_Interaction__c> intList = [SELECT CreatedDate,CreatedBy.Name,Interaction__r.Id,Interaction__r.Created_By_Queue__c,Interaction__r.Name,Interaction__r.CreatedDate, Interaction__r.Interaction_Origin__c,Interaction__r.Interacting_With_type__c,Interaction__r.Interacting_With__r.Name,Case__r.CaseNumber,Id FROM Case_Interaction__c WHERE Case__c =:caseRecId ORDER BY CreatedDate DESC];
        
        for(Case_Interaction__c cInt : intList )
        {
            CaseInteractionDTO_LC_HUM oInt = new  CaseInteractionDTO_LC_HUM ();
            oInt.sInteractionNumber = cInt.Interaction__r.Name+ CONSTANTS_CASE_HUM.sSrcUp + CONSTANTS_CASE_HUM.sForwardSlash + cInt.Interaction__r.Id;
            oInt.sInteractionId = cInt.Interaction__r.Id;
            oInt.sInteractionOrigin = cInt.Interaction__r.Interaction_Origin__c;
            oInt.sInteractionWith = cInt.Interaction__r.Interacting_With__r.Name;
            oInt.sInteractionWithType = cInt.Interaction__r.Interacting_With_type__c;
            oInt.sCreatedDate = cInt.Interaction__r.CreatedDate.format(CONSTANTS_CASE_HUM.HUMDateTimeFormat);
            oInt.sCreatedBy = cInt.CreatedBy.Name + CONSTANTS_CASE_HUM.sSrcUp + cInt.CreatedBy.Id   ;
            oInt.sCreatedByQueue = cInt.Interaction__r.Created_By_Queue__c;
            oInt.sAssociatedtoCaseDate= cInt.CreatedDate.format(CONSTANTS_CASE_HUM.HUMDateTimeFormat); 
            lstCaseint.add(oInt);
        }
        return lstCaseint;
    
    }
    /**  
    * This method is to check whether new case interaction and create new interaction buttons should be disabled.
    * 
    * @return   Boolean
    */
    @AuraEnabled(cacheable=true)
    public static boolean verifyLegacyDelete(String recordId)
    {
       boolean bdisableButton = false;
       boolean blegacyDeleteCheck = false;
       Id caseId =recordId;
           if(caseId!=null){
            Case caseRec = [SELECT Id,CaseNumber, Account.ETL_Record_Deleted__c,RecordType.name,Member_Plan_Id__c,Member_Plan_Id__r.ETL_Record_Deleted__c, Member_Plan_Id__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin, AccountId, Status,Type  FROM Case where id= :caseId];
                
            if(caseRec.Account.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
            else if (caseRec.Member_Plan_Id__c != null && caseRec.Member_Plan_Id__r.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
            if(blegacyDeleteCheck && caseRec.Status=='Closed') bdisableButton = true;
            
         }
        return bdisableButton;
    }
    
    public with sharing class CaseInteractionDTO_LC_HUM 
    {
        @AuraEnabled
        public String sInteractionNumber{get; set;}
        @AuraEnabled
        public String sInteractionId{get; set;}
        @AuraEnabled
        public String sInteractionOrigin{get; set;}    
        @AuraEnabled
        public String sInteractionWith{get; set;}
        @AuraEnabled
        public String sInteractionWithType{get; set;}
        @AuraEnabled
        public String sCreatedDate{get; set;}
        @AuraEnabled
        public String sCreatedBy{get; set;}
        @AuraEnabled
        public String sCreatedByQueue{get; set;}
        @AuraEnabled
        public String sAssociatedtoCaseDate{get; set;}
    }
    
	public static final set < StatusCode > setValidExceptionCodes = new set < StatusCode > {
  StatusCode.FIELD_CUSTOM_VALIDATION_EXCEPTION
};
/*
* Method name  : saveInteraction
* Description  : Method to create new  interaction along with related records once use clicks on save
* Return Type  : Map
* Parameter    : interactionData, caseId
*/
@AuraEnabled
public static Map<String,String> saveInteraction(String interactionData,Id caseId){
  Map<String,String> result = new Map<String,String>();
  try{
  Interaction__c interactionRec = (Interaction__c)JSON.deserialize(interactionData,Interaction__c.class);         
  String errorMsg = null;

  List<Interaction__c> lstInteraction = new List<Interaction__c>{interactionRec};
  List<Database.SaveResult> savResults = Database.insert(lstInteraction,false);
  
  if(savResults[0].isSuccess()){ 
	  List<Case> caseRecord = [select Account.Id,Account.RecordType.Name from case where id=:caseId];
      if(!caseRecord.isEmpty() && caseRecord[0].Account.Id!=null){
            Interaction_Member__c interactionMember = new Interaction_Member__c();
            interactionMember.Interacting_About__c = caseRecord[0].Account.Id;
            interactionMember.Interacting_About_Type__c = caseRecord[0].Account.RecordType.Name;
            interactionMember.Interaction__c = interactionRec.id;
            List<Interaction_Member__c> lstInteractionMember = new List<Interaction_Member__c>{interactionMember};
            List<Database.SaveResult> saveIntMem = Database.insert(lstInteractionMember,false);
            if(saveIntMem[0].isSuccess() == false){
                errorMsg = processSaveResults(saveIntMem);          
            }
        }
      Case_Interaction__c caseInteraction = new  Case_Interaction__c();
      caseInteraction.Interaction__c = interactionRec.Id;
      caseInteraction.Case__c = caseId;
      result.put('interactionId',interactionRec.Id);
      List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>{caseInteraction};
      List<Database.SaveResult> saveCaseInt = Database.insert(lstCaseInteraction,false);
      if(saveCaseInt[0].isSuccess() == false){
          errorMsg = processSaveResults(saveCaseInt);
      }            
  }else{
      errorMsg = processSaveResults(savResults);
  }
  result.put('isSuccess',''+savResults[0].isSuccess());
  result.put('errorMsg',errorMsg);
}catch(Exception e){
  result.put('isSuccess','false');
  result.put('errorMsg',e.getMessage());
}
  return result;
}

/*
* Method name  : processSaveResults
* Description  : Method to check if there are errors and get the error messages
* Return Type  : String
* Parameter    : Database.SaveResult
*/

private static string processSaveResults(List <Database.SaveResult> listSaveResults){
  string errMsg='';
  if(listSaveResults != null){
      Database.Error error;
      for(Integer count = 0; count < listSaveResults.size(); count++){
          if(!listSaveResults[count].isSuccess()){
              error = listSaveResults[count].getErrors()[0];
              if(setValidExceptionCodes.contains(error.getStatusCode())) {
                  errMsg =  error.getMessage();
              }else {                        
                  errMsg = error.getMessage();
              }
          }
      }
  }
  return errMsg;
} 

/*
* Method name  : getCaseData
* Description  : Method to get the origin, Interacting With Type and Interacting With
* Return Type  : Map
* Parameter    : caseId
*/
@AuraEnabled
public static Map<String,String> getCaseData(Id caseId){
  
  Map<String,String> result = new Map<String,String>();
  List<Case> lstCase = [select origin,Interacting_With_Type__c,Interacting_With__c from Case where Id =: caseId];
  
  if(!lstCase.isEmpty()){
      result.put('origin',lstCase[0].origin);
      result.put('interactingWithType',lstCase[0].Interacting_With_Type__c);
      result.put('interactingWith',lstCase[0].Interacting_With__c);
  }
  
  return result;
}
}