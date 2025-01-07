/*****************************************************************************************************
Apex Class Name  	: CaseCompassExtension_C_HUM
Version   			: 1.0 
Created Date  		: 07/23/2017
Function  			: Controller to fetch compass related to cases
Test Class 			:  CompassExtension_T_HUM

        Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Sumit Pharande            149213             09/12/2017                   Original Version
* Pradeepkumar Dani			149213		       11/17/2017				    re-factoring
* Prudhvi Pamarthi                             02/06/2019                 REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Santhosh Ganji                               04/19/2019                 REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
* Santhosh Ganji                               05/24/2019                 REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
* Moshitha Gunasekaran                         01/28/2021                 US#1921246 - Sync CPU Time Limit Analysis - Optimized the code to improve the performance
* Gowthami Thota							   07/29/2021				  US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page
*******************************************************************************************************/
public with sharing class CompassExtension_C_HUM
{
    public string columnName {get;set;}
    public string columnHeader {get;set;}  
    public Boolean bdisableButton {get; set;}
    public Boolean blegacyDeleteCheck;
    public Id toGetCaseId;
	public boolean hideTheButton{ get; set;}
	public boolean sPharmacyUser { get; set;}
	public string compassButtonLabel{get;set;}
	public boolean bInsideRapidForce{get;set;}
    public Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES);
	
    /**
	*  Constructor for the class
	*  @name CompassExtension_C_HUM
	*  @param NA
	*  @return void
	*  @throws NA
	*/    
    public CompassExtension_C_HUM()
    {
    	bInsideRapidForce = HUMUtilityHelper.IsRapidForceAPP();
    	compassButtonLabel = bInsideRapidForce?CaseDetailConstants_HUM.HUMCompassButtonRF:CaseDetailConstants_HUM.HUMCompassButtonNonRF;
        columnName = json.serialize(new List<String>{'Id','MentorDocumentName' ,'DocumentURL', 'CreatedDate' , 'CreatedBy'  });     
        columnHeader = json.serialize(new List<String>{'Id','Mentor Document Name ' ,'Document URL', 'Created Date ' , 'Created By' });     
        toGetCaseId =  ApexPages.currentPage().getParameters().get('Id');
        bdisableButton = verifyLegacyDelete();
		hideTheButton = verifyMedicareCase();
    }
	
    /**
	*  Gets list of Case Comments for given case using wrapper class list
	*  @name getwrapperclass
	*  @param NA
	*  @return void
	*  @throws NA
	*/ 
    @RemoteAction
    public static list <Compass_DTO_HUM> getCaseCompassData(String sCaseRecordId) 
    {
        return CompassExtension_C_HUM.getCaseCompassDTOList(sCaseRecordId);
    }
    
    /**
	*  Gets list of Case Comments for given case using wrapper class list
	*  @name getwrapperclass
	*  @param NA
	*  @return void
	*  @throws NA
	*/
    public static list<Compass_DTO_HUM> getCaseCompassDTOList(String caseRecId)
    {
        list<Compass_DTO_HUM>  lstCompass =new list<Compass_DTO_HUM>(); 
                
        List<Compass__c> cmpsList = [SELECT Id,CreatedDate, CreatedById, CreatedBy.Name, Document_Name__c, Document_URL__c, Case__c, Case__r.Owner_Queue__c FROM Compass__c WHERE Case__c =: caseRecId ORDER BY Id DESC] ; 
        for(Compass__c oCp:cmpsList)
        {
            Compass_DTO_HUM Ocompass = new  Compass_DTO_HUM();
            Ocompass.Id = oCp.Id;
            Ocompass.MentorDocumentName = oCp.Document_Name__c; 
            Ocompass.DocumentURL = oCp.Document_URL__c + CaseDetailConstants_HUM.sCompassOpenURL + oCp.Document_URL__c;
            String sCreatedDate = oCp.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
            Ocompass.CreatedDate = sCreatedDate;
            Ocompass.CreatedBy = oCp.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + oCp.CreatedBy.Id;
            lstcompass.add(Ocompass);    
        }
        return lstCompass ;
    }

    /**  
    * This method is to check whether search compass button should be disabled.
    * 
    * @return   Boolean
    */
    public boolean verifyLegacyDelete()
    {
       bdisableButton = false;
       blegacyDeleteCheck = false;
       Case caseRec = new case();
       if(toGetCaseId!=null)caseRec = [SELECT Id, AccountId, Policy_Member__c, Status FROM Case where id= :toGetCaseId];
       if (caseRec.AccountId != Null)
       {
           Account oAcctRT = [SELECT id,ETL_Record_Deleted__c from Account where Id = :caseRec.AccountId];
           if(oAcctRT.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
           if (caseRec.Policy_Member__c != Null)
           {
               Policy_Member__c oPolMem = [SELECT id,ETL_Record_Deleted__c from Policy_Member__c where Id = :caseRec.Policy_Member__c];
               if(oPolMem.ETL_Record_Deleted__c == true) blegacyDeleteCheck = true;
           }
        }
        if(blegacyDeleteCheck && caseRec.Status=='Closed') bdisableButton = true;
        return bdisableButton;
     }
	 
	 public boolean verifyMedicareCase()
     {
       hideTheButton = false;
       Case objCase = new case();
       sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser() == CaseDetailConstants_HUM.NONPHARMACY_USER_HUM ? false : true;
       if(toGetCaseId!=null)objCase= [SELECT Id, Recordtype.name, Policy_Member__c, Policy_Member__r.Product__c,Owner_Queue__c,Created_By_Queue__c,Origin FROM Case where id= :toGetCaseId];
        if(!Test.isRunningTest()){
          if(objCase != null && (objCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) ||(objCase.Policy_Member__c != null && objCase.Policy_Member__r.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY) || (chatQueues.contains(objCase.Owner_Queue__c) && objCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN)))
          {
            if(objCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || wqueues.contains(objCase.Owner_Queue__c) || wqueues.contains(objCase.Created_By_Queue__c) || (chatQueues.contains(objCase.Owner_Queue__c) && objCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
            {
              hideTheButton = true;
            }
          }
	    }
       
       return hideTheButton;
     }
}