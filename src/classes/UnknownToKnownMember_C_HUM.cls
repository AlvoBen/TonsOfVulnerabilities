/*****************************************************************************************************************************************************************************************************************************************************
Apex Class Name  : UnknownToKnownMember_C_HUM
Version          : 1.0
Function         : Class to move from unknown to known member
Version                   Developer Name                  Date                       Description
1.0                        Bhumika S                       2/1/2022           User Story 3010498: T1PRJ0314829 - MF 1 - Quality - Ability to move cases from an unknown to a known Member (RF)
**************************************************************************************************************************************************/
public with sharing class UnknownToKnownMember_C_HUM {
    private String sId ='';
    public Case oCase{get;set;}
    public String  IntWithName{get;set;}
    public Boolean bMemberFlag{ get; set;}
    public boolean bIntAboutFlag{get;set;}
    public boolean bIntWithFlag{get;set;}
    public boolean bIsSuccess{get;set;}
    public boolean viewAccountLookup{get;set;}
    public Interaction__c interactionRec{get;set;}
    public String sAccountId{get;set;}    
    public String sAccountName{get; set;}
    public String sCaseNumber{get; set;}

    /*
    * Method name  : UnknownToKnownMember_C_HUM
    * Description  : Constructor to get the case id from the URL parameters
    * Return Type  : NA
    * Parameter    : NA
    */
    public UnknownToKnownMember_C_HUM() {
        this.sAccountName = null;
        this.interactionRec = new Interaction__c(Interacting_With_type__c = 'Member');
        this.oCase = new Case();
        this.sId = ApexPages.Currentpage().getParameters().get('id'); 
        this.viewAccountLookup = false; 
    }

    /*
    * This method helps in opening/closing the Modal window on for Account Lookup
    * @param  NA
    * @return NA
    */
    public void getopenAccountLookup() {
        viewAccountLookup = !viewAccountLookup;
    }

    /*
    * This method helps in editing and moving case to known member
    * @param  NA
    * @return PgRef
    */
    public Pagereference editAndMove() { 
        Pagereference pgRef = null;     
        this.sAccountName = null;
        try {
            List<Interaction__c> lstInteraction = new List<Interaction__c>();
            Account InteractingAboutAcc = null;
            Savepoint sp = Database.setSavepoint();
            if(String.isNotBlank(sId) && sId.startsWith('500')) {
                List<Case> lstCase = [select id, CaseNumber, Interacting_With__c, Interacting_with_type__c, Interacting_About__c, Interacting_About_Type__c, Status, (Select id, Interaction__c from Case_Interaction__r) 
                                            From Case Where Id =: sId limit 1];             
                this.sCaseNumber = lstCase.get(0).CaseNumber;
                Set<String> setIdInteraction = new Set<String>();
                for(Case_Interaction__c oCaseInteraction : lstCase.get(0).Case_Interaction__r) {
                    setIdInteraction.add(oCaseInteraction.Interaction__c);
                }
            
                if(String.isNotBlank(oCase.AccountId)){
                    String sInteractingAbout = oCase.AccountId;
                    InteractingAboutAcc = [select ID, Name, RecordType.Name from Account where id =: sInteractingAbout limit 1]; 
                    sAccountId = InteractingAboutAcc.ID;
                    sAccountName = InteractingAboutAcc.Name;
                }
                if(InteractingAboutAcc.RecordType.Name != 'Member') {
                    ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR,'Interacting About must be a Member.'));
                    return null;
                }
                lstInteraction = [select id,Interacting_With__c, Interacting_With_type__c, Caller_name__c from Interaction__c where Id in :setIdInteraction];
                
                for(Interaction__c oInteraction : lstInteraction) {
                    oInteraction.Interacting_With_type__c = interactionRec.Interacting_With_type__c;
                    oInteraction.Interacting_With__c = interactionRec.Interacting_With__c;
                    oInteraction.Caller_Name__c= interactionRec.Caller_Name__c;
                }

                List<Database.SaveResult> savelstInteraction = Database.update(lstInteraction, false);
                Boolean isError = HUMExceptionHelper.processSaveResults(savelstInteraction ,lstInteraction ,'UnknownToKnownMember_C_HUM','editAndMove: Update Interaction','Interaction__c');
                if(!ApexPages.getMessages().isEmpty()) {
                    Database.RollBack(sp);
                    return null;
                }
           
                List<Interaction_Member__c> lstIM = [select id, Interacting_About__c, Interacting_About_Type__c from Interaction_Member__c where Interaction__c in : setIdInteraction];
               
                if(!lstIM.isEmpty()) {
                    for(Interaction_Member__c oIM : lstIM) {
                        oIM.Interacting_About__c = InteractingAboutAcc.Id;
                        oIM.Interacting_About_Type__c = InteractingAboutAcc.RecordType.Name;
                    }                    
                } else {
                    lstIM = new List<Interaction_Member__c>();
                    for(Interaction__c oInteraction : lstInteraction) {
                        lstIM.add(new Interaction_Member__c(Interaction__c = oInteraction.Id, Interacting_About__c = InteractingAboutAcc.Id, Interacting_About_Type__c = InteractingAboutAcc.RecordType.Name));
                    }
                }
                
                List<Database.UpsertResult> upsertResults = Database.upsert(lstIM,false);
                HUMExceptionHelper.processUpsertResults(upsertResults ,lstIM ,'UnknownToKnownMember_C_HUM','editAndMove: Upsert Iteraction Member','Interaction_Member__c');
               
                if(!ApexPages.getMessages().isEmpty()) {
                    Database.RollBack(sp);
                    return null;
                }
                for(Case oCaseRecord : lstCase) {
                    oCaseRecord.AccountId = InteractingAboutAcc.Id;
                    oCaseRecord.Interacting_About_Type__c = InteractingAboutAcc.RecordType.Name;
                    oCaseRecord.RecordTypeId = HUMUtilityHelper.getCaseRecordType(InteractingAboutAcc.RecordType.Name); 

                    oCaseRecord.Interacting_with_type__c = interactionRec.Interacting_With_type__c;
                    if(String.isBlank(interactionRec.Caller_Name__c)){
                   // if(interactionRec.Interacting_With_type__c=='Member Representative or Caregiver' && String.isBlank(interactionRec.Caller_Name__c)){
                        oCaseRecord.Interacting_With__c = interactionRec.Interacting_With__c;
                       // oCaseRecord.Interacting_With_Name__c = ' ';                       
                    } else {
                        oCaseRecord.Interacting_With_Name__c = interactionRec.Caller_Name__c;
                      //  oCaseRecord.Interacting_With__c = null;
                    }
                    
                    if(oCaseRecord.status == CaseEditConstants_HUM.CASEEDIT_CLOSED_HUM){
                        oCaseRecord.status = CaseEditConstants_HUM.CASESTATUS_HUM;            
                    } 
                }
				List<Database.SaveResult> saveCaseInt = Database.update(lstCase,false);
                HUMExceptionHelper.processSaveResults(saveCaseInt ,lstCase ,'UnknownToKnownMember_C_HUM','editAndMove: Update Case','case');
                if(!ApexPages.getMessages().isEmpty()) {
                    Database.RollBack(sp);
                    return null;
                }
                this.bIsSuccess = true;
            }
        } catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'UnknownToKnownMember_C_HUM','editAndMove');
            bIsSuccess = false;
        }
        return pgRef;
    }
}