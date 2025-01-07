/*******************************************************************************************************************************
Apex Class Name : MemberSearchEnrollmentHelper_LC_HUM
Function        : This class is used for take the all Enrollment search data to UI 

Modification Log: 
* Version          Developer Name             Code Review               Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        10/08/2020               Original Version
*    2.0           Muthukumar                                           07/29/2022               US-3255798
*    3.0           Visweswararao j                                      07/29/2022               User Story 3256122: T1PRJ0078574 - MF 19331- Lightning - - Search Enrollment - Application Electons Tab fields population     
*    4.0           visweswararao j                                      07/29/2022               User Story 3332979: T1PRJ0170850 - MF 21024- Lightning - Search Enrollment - Medicare Entitlement & Misc Fields population
*    5.0           visweswararao j                                      07/29/2022               User Story 3332969: T1PRJ0170850 - MF 21024- Lightning - Search Enrollment - POA & Agent Information Fields population
*    6.0           visweswararao j                                      07/29/2022               User Story 3331631: T1PRJ0170850 - MF 21024- Lightning - - Search Enrollment - Demographic Tab fields population
*    7.0           Bhakti Vispute                                       10/14/2022               User Story 3495763: T1PRJ0170850- MF 20034 - Enrollment Search / TRR Details Tab - Reply Code Tab fields population
*    8.0           Bhakti Vispute                                       10/14/2022               User Story 3495778: T1PRJ0170850- MF 20034 - Enrollment Search / TRR Details Tab - Creditable Coverage / LEP & 4Rx Tab fields population
*    9.0           Visweswararao Jayavarapu                             30/08/2022               User Story 3481400: Enrollment Search / H1 Market Place Tab - Detail Page Creation and Field Population
*    10.0          Visweswararao Jayavarapu                             30/08/2022               User Story 3483233: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Applications Misc Tab fields population
*    11.0          Visweswararao Jayavarapu                             30/08/2022               User Story 3481578: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Billing Information Tab fields population
*    12.0          Visweswararao Jayavarapu                             30/08/2022               User Story 3482496: T1PRJ0170850- MF 20035- Lightning - - Enrollment Search / H1 Market Place - Benefit
*    13.0          Nilanjana Sanyal                                     02/17/2023               User Story 4003126: FirstName and Lastname link implementation in Enrollment Search Tab 
                                                                                               (for CBIS, H1 marketplace, Customer Interface/Metavance and Automated Enrollment History)
*    14.0          Visweswararao Jayavarapu                             04/07/2023               User Story 4404841: T1PRJ0865978 - MF24206 - Add SSN field in UI and enhance the search capability for CBIS in enrollment search
*    15.0          Vardhman Jain                                        05/18/2023               User Story 4407458:T1PRJ0191994- MF 128, 129- Display Member Race and Ethnicity in CRM Service- ‘TRR Reply Detail’ page (Lightning)                                    
******************************************************************************************************************************************************************************************************************************************/
public without sharing class MemberSearchEnrollmentHelper_LC_HUM
{
    /*these variables are for TRR search */
    @AuraEnabled public String sMedicareClaim;                                
    @AuraEnabled public String sLastName;           
    @AuraEnabled public String sFirstName;
    @AuraEnabled public String sContract;
    @AuraEnabled public String sPBP;
    @AuraEnabled public String sEffectiveDate;
    @AuraEnabled public String sReplyCode;
    @AuraEnabled public String sSegmentNumber;
    @AuraEnabled public String sHumanaReceivedDate;
    @AuraEnabled public String TRRresultdetails;
    @AuraEnabled public String sErrorMessage;
	@AuraEnabled public String sCreditableCoverageFlag;
    @AuraEnabled public String sCumulativeNumberofUncoveredMonths;
    @AuraEnabled public String sSubmittedNumberofUncoveredMonths;
    @AuraEnabled public String sPartDPenaltyAmount;
    @AuraEnabled public String sPartDPenaltyWaivedAmount;
    @AuraEnabled public String sPartDPenaltySubAmount;
    @AuraEnabled public String sPartDRxBIN;
    @AuraEnabled public String sPartDRxPCN;
    @AuraEnabled public String sPartDRxGroup;
    @AuraEnabled public String sPartDRxId;
    @AuraEnabled public String sTransactionCode;
    @AuraEnabled public String sUIOrganization;
    @AuraEnabled public String sEnrollmentSourceCode;
    @AuraEnabled public String sVariable;
    @AuraEnabled public String sApplicationDate;
    @AuraEnabled public String sTransactionDate;
    @AuraEnabled public String sCMSProcessingTimestamp;
    @AuraEnabled public String sApplicationDateIndicator;
    @AuraEnabled public String sTRCShortName;
    @AuraEnabled public String sSourceId;
    @AuraEnabled public String sElectionTypeCode;
    @AuraEnabled public String sPriorPBP;
    @AuraEnabled public String sUIInitialChangeFlag; 
	@AuraEnabled public String sSEPReasCode;
    @AuraEnabled public String sDedupIndicator;	
    @AuraEnabled public String sMiddleInitial;
    @AuraEnabled public string sLISPremiumLevel {get;set;}
    @AuraEnabled public string sLISCopayCategory {get;set;}
    @AuraEnabled public string sLISPeriodEffectiveDate {get;set;}
    @AuraEnabled public string sLISPremAmount {get;set;}
    @AuraEnabled public string sLISPeriodEndDate {get;set;}
    @AuraEnabled public string sLISSourceCode {get;set;}
    @AuraEnabled public string sLISEnrolleeTypeCode {get;set;}
    @AuraEnabled public string sSecondaryInsDrugFlag {get;set;}
    @AuraEnabled public string sSecondaryRxId {get;set;}
    @AuraEnabled public string sSecondaryRxGroup {get;set;}
    @AuraEnabled public string sSecondaryRxBIN {get;set;}
    @AuraEnabled public string sSecondaryRxPCN {get;set;}
    
    @AuraEnabled public string sEmployerSubsidyOverrideFlag {get;set;}
    @AuraEnabled public string sEGHPIndicator {get;set;} 
    @AuraEnabled public string sPlanAssignedTransTrackingId {get;set;}
    @AuraEnabled public string sDistrictOfficeCode {get;set;}
    @AuraEnabled public string sPartDOptOutFlag {get;set;}
    @AuraEnabled public string sOutOfAreaFlag {get;set;}
    @AuraEnabled public string sDisenrollmentReasonCode {get;set;}
    @AuraEnabled public string sPartCPremiumAmount {get;set;}
    @AuraEnabled public string sPartDPremiumAmount {get;set;}
   
    @AuraEnabled public string sAccessibleFmtCode {get;set;}
    @AuraEnabled public string sWAIndicator {get;set;}
    @AuraEnabled public string sPrefLangcode {get;set;}
    @AuraEnabled public string sPremiumWithholdOptionCode {get;set;}
    @AuraEnabled public string sDisabilityIndicator {get;set;}
    @AuraEnabled public string sHospiceIndicator {get;set;}
    @AuraEnabled public string sMSPStatusFlag {get;set;}
    @AuraEnabled public string sDeMinimisDifferentialAmount {get;set;}
    @AuraEnabled public string sPrevTROOPPBP {get;set;}
    @AuraEnabled public string sInstitutionalIndicator {get;set;}
    @AuraEnabled public string sEntitlementTypeCode {get;set;}
    @AuraEnabled public boolean bdisplayInteractbutton;
    
	public String append ='000'; 
    
    /*these variables are for CBI search */
    @AuraEnabled public String sExternalID;
    @AuraEnabled public String sProduct;
    @AuraEnabled public String sLOB;
    @AuraEnabled public String sEndDate;
    @AuraEnabled public String sPend;
    @AuraEnabled public String sExternalSystemType;
    @AuraEnabled public String sPlatform;
    @AuraEnabled public String sGroupId;
    @AuraEnabled public String sDOB; 
    
    /*these variables are for Application search */
    @AuraEnabled public String sMedicareId;
    @AuraEnabled public String sMedicaidId;
    @AuraEnabled public String sBSN;
    @AuraEnabled public String sCovEffDt;
    @AuraEnabled public String sReceivedDate;
    @AuraEnabled public String sApplicationChannel;
    @AuraEnabled public String sOECConfirmationId;
    @AuraEnabled public String sRawOECYear;
    @AuraEnabled public String sWebUniqueId;
    @AuraEnabled public String details;
    @AuraEnabled public String application;
    @AuraEnabled public String applicationURL;
	@AuraEnabled public String sBarCodeBase;
    @AuraEnabled public String sProcDt;
    @AuraEnabled public String sSalesRepSSN;
    @AuraEnabled public String sSalesType;
    @AuraEnabled public String sSalesRepName;
    @AuraEnabled public String sPOAPhone;
    @AuraEnabled public String sPOARelationType;
    @AuraEnabled public String sElectionTypeCd;
    @AuraEnabled public String sPymtOptType; 
    @AuraEnabled public String sCreationTs;
    @AuraEnabled public String sSEPDate;
    @AuraEnabled public String sSepReasonCode;
    @AuraEnabled public String sPartAEligibleDate;
    @AuraEnabled public String sPartBEligibleDate;
	@AuraEnabled public String sContractId;
    @AuraEnabled public String sFileDt;
    @AuraEnabled public String sSolSecurity;
    @AuraEnabled public String sGender;
    @AuraEnabled public String sPermanentAddress;
    @AuraEnabled public String sCity;
    @AuraEnabled public String sCountyName;
    @AuraEnabled public String sMailaddr;
    @AuraEnabled public String sMailCity;
    @AuraEnabled public String sMailSt;
    @AuraEnabled public String sMailZip;
    @AuraEnabled public String sworkPh;
    @AuraEnabled public String sMemberLangPreference;
    @AuraEnabled public String sContact;
    @AuraEnabled public String sEmployerId;
    @AuraEnabled public String sOSBPackage;
    @AuraEnabled public String sPymtOpt;
    @AuraEnabled public String sSmokingIndicator;
    @AuraEnabled public String sDisabledIndicator;
    @AuraEnabled public String sPCPName;
    @AuraEnabled public String sPCPNumber;
    @AuraEnabled public String sESRDIndicator;
	@AuraEnabled public String sPOAFirstName;
    @AuraEnabled public String sPOALastName;
    @AuraEnabled public String sHomePh;
	@AuraEnabled public String sSignatureDate;
	@AuraEnabled public String sSignatureType;	
	@AuraEnabled public String sSegmentId;
	@AuraEnabled public String sRace;
    @AuraEnabled public String sEthinicity;	
    
    /* these variables are used for Automated enrollment search */
    @AuraEnabled public String sPendKey;
    @AuraEnabled public String sDateTime;
    
    /* these variables are used for H1Market palce Enroll Search */
    @AuraEnabled public String sExchangeID;
    @AuraEnabled public String sState;
    @AuraEnabled public String sZip;
	@AuraEnabled public String sCsrLevel;
    @AuraEnabled public String sAptcAmount;
    @AuraEnabled public String sStrBsn;
	@AuraEnabled public String sOecConfId;
    @AuraEnabled public String sApplnChnl;
	@AuraEnabled public String sCsrEffDate;
    @AuraEnabled public String sAptcEffDate;
    @AuraEnabled public String sCsrEndDate;
    @AuraEnabled public String sSignDATE;
    @AuraEnabled public String sMktRcptDate;
    @AuraEnabled public String sAptcEndDate;
    @AuraEnabled public String sLegacyMemberJson;

	/* sAccountId is to store salesforce account record id for all systems */
    @AuraEnabled public String sAccountId;
	
    /* this constructor is initialised so that we can use method of HUMMemberSearchEnrollmentHelper */
    HUMMemberSearchEnrollmentHelper memSearch = new HUMMemberSearchEnrollmentHelper();
    
    /* this is constructor is used for H1MarketPlace data pasing */
    public MemberSearchEnrollmentHelper_LC_HUM(HUMH1SearchResponseHelper.EnrollmentDetail enrollMember)
    {
        assignValues( enrollMember.firstName, enrollMember.lastName, enrollMember.strDob, null, null, null, enrollMember.covEffDt, enrollMember.covTrmDt);
        this.sExchangeID = enrollMember.exchMbrID;
		this.sPend = enrollMember.applnId;
        this.sExternalID = enrollMember.exchMbrId;
        this.sExternalSystemType = system.label.HUMH1Service_label;
        this.sState = enrollMember.othSt;
		this.sZip = enrollMember.othZip;
		this.sSolSecurity = enrollMember.strSsn;
        this.sGender = enrollMember.genderCd;
        this.sPermanentAddress = enrollMember.othAddr1;
        this.sCountyName = enrollMember.cntyName;
        this.sCity = enrollMember.othCity;
        this.sMailaddr = enrollMember.mailAddr1;
        this.sMailCity = enrollMember.mailCity;
        this.sMailSt = enrollMember.mailSt;
        this.sMailZip = enrollMember.mailZip;
        this.sHomePh = enrollMember.homePhone;
        this.sWorkPh = enrollMember.workPhone;
        this.sPymtOpt = enrollMember.pymtOptInd;
        this.sCsrLevel = enrollMember.csrLevel;
        this.sAptcAmount = enrollMember.aptcAmount;
        this.sStrBsn = enrollMember.strBsn;
        this.sSalesRepName = enrollMember.salesRepName;
        this.sSalesType = enrollMember.salesType;
		this.sCreationTs = enrollMember.CreationTs;
         this.sCsrEffDate = memSearch.convertDate(enrollMember.csrEffDate);
		 this.sCsrEndDate = memSearch.convertDate(enrollMember.csrEndDate);
         this.sAptcEffDate = memSearch.convertDate(enrollMember.aptcEffDate);
         this.sAptcEndDate = memSearch.convertDate(enrollMember.aptcEndDate);
         this.scovEffDt = memSearch.convertDate(enrollMember.covEffDt);
         this.sMktRcptDate = memSearch.convertDate(enrollMember.mktRcptDate);
         this.sSignDATE = memSearch.convertDate(enrollMember.signDATE);
         this.sFileDt = memSearch.convertDate(enrollMember.fileDt);
         this.sProcDt = memSearch.convertDate(enrollMember.procDt);		 
         this.sBarCodeBase = enrollMember.barCodeBase;
        if(enrollMember.smokeInd == 'Y') this.sSmokingIndicator = System.label.HUMSmokingIndicator;
        if(enrollMember.smokeInd == 'N') this.sSmokingIndicator = system.label.HUMsmokindicatorNO;
        if(String.isBlank(enrollMember.smokeInd)) this.sSmokingIndicator = enrollMember.smokeInd;
        this.sOecConfId = enrollMember.oecConfId;
        this.sApplnChnl = enrollMember.applnChnl;
		if(String.isNotBlank(this.sExternalID)){
           String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_H1').StringValue__c + this.sExternalID;    
           this.sLegacyMemberJson = Json.serialize(createAcc(sAccountExternalID));
           this.sExternalID = sAccountExternalID;
        }
    }
    
    /* this constructor is use for parse data from OHBP system */
    public MemberSearchEnrollmentHelper_LC_HUM(Map<String,String> mapAccount)
    {
        if(mapAccount.containsKey(system.label.HUMOHBPSearchFstName))
        {
            this.sFirstName=  mapAccount.get(system.label.HUMOHBPSearchFstName) ;
        }
        if(mapAccount.containsKey(system.label.HUMOHBPSearchLstName))
        {
            this.sLastName=mapAccount.get(system.label.HUMOHBPSearchLstName);
        }
        if(mapAccount.containsKey(system.label.HUMOHBPSearchBirthDt))
        {
            this.sDOB  = memSearch.convertDate(String.valueOf(Date.valueOf(mapAccount.get(system.label.HUMOHBPSearchBirthDt))));
        }
        if(mapAccount.containsKey(system.label.HUMOHBPSearchEfectDate))
        {
            this.sEffectiveDate = memSearch.convertDate(String.valueOf(Date.valueOf(mapAccount.get(system.label.HUMOHBPSearchEfectDate))));
        }
        this.sZip  = mapAccount.get('PersonMailingPostalCode');
        this.sEndDate = '';
        this.sState = '';
        string sextIDohbp = this.sFirstName+this.sLastName+this.sDOB+this.sZip ;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(sextIDohbp );
    }
    
    /* this constructor is used for Automated Enrollment search */
    public MemberSearchEnrollmentHelper_LC_HUM(HUMAEServicesHistoryResponse.PendHistory aeHis){
        assignValues(aeHis.First_Name, aeHis.Last_Name, String.valueof(aeHis.Birth_Date), aeHis.Platform, aeHis.Group_Id, aeHis.Ben_Id, String.valueof(aeHis.Cov_Eff_Date), String.valueof(aeHis.Cov_End_Date));
        this.sPendKey = aeHis.Pend_Key;
        if(!String.IsBlank( aeHis.First_Name)) 
        {
            aeHis.First_Name = aeHis.First_Name.trim();
        }
        if(!String.IsBlank( aeHis.Last_Name)) 
        {
            aeHis.Last_Name= aeHis.Last_Name.trim();
        }
		String SExtd=  aeHis.First_Name + aeHis.Last_Name+String.valueof(aeHis.Birth_Date)+aeHis.Zip;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(SExtd) ;
        if(String.isNotBlank(this.sExternalID)){
            String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_AE').StringValue__c + this.sExternalID; 
            this.sLegacyMemberJson = Json.serialize(createAcc(sAccountExternalID));  
            this.sExternalID = sAccountExternalID;        
        }
    }
    
    /* this constructor is used for Metavance search */
    public MemberSearchEnrollmentHelper_LC_HUM(Map<String,Map<String,String>> mapMemberInfo, Integer i){
        String sKey = '';
        if(mapMemberInfo.containsKey(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember))
        {
            this.sFirstName = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_FirstName ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sLastName = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_LastName ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sDOB = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_DateOfBirth ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sPlatform = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_SrcPlatformCode ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sExternalID = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_PID ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
        }
        if(mapMemberInfo.containsKey(system.label.HUMEESLegacyMemberSearchHelper_MemberContract_Group))
        {
            this.sGroupId = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_GroupID , mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_MemberContract_Group));
        }
        if(i != null)
        {
            sKey = system.label.HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage+i;
        }
        else
        {
            sKey = system.label.HUMEESLegacyMemberSearchHelper_Coverages_PlatformMemberCoverage;
        }
        if(mapMemberInfo.containsKey(sKey))
        {
            this.sLOB = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_LineOfBusiness , mapMemberInfo.get(sKey));
            this.sProduct = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_CategoryDesciption , mapMemberInfo.get(sKey));
            this.sEffectiveDate = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_MemberCoverageEffectiveDate , mapMemberInfo.get(sKey));
            this.sEndDate = memSearch.fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_MemberCoverageEndDate , mapMemberInfo.get(sKey));
        }
		if(String.isNotBlank(this.sExternalID)){
			String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_EES').StringValue__c + this.sExternalID;
			this.sLegacyMemberJson = Json.serialize(createAcc(sAccountExternalID));  
            this.sExternalID = sAccountExternalID;  
        } 
    }
    
    /* this constructor is used for TRR search */
    public MemberSearchEnrollmentHelper_LC_HUM(TRRServiceRestResponse_DTO_HUM.TRRDetail TRREnroll,Integer TRRSearchCounter){
        this.sMedicareClaim =  TRREnroll.MedicareId;                                
        this.sLastName= TRREnroll.LastName;           
        this.sFirstName= TRREnroll.FirstName;
        this.sContract=TRREnroll.Contract;
        this.sPBP=TRREnroll.PBP;
        this.sEffectiveDate=TRREnroll.EffectiveDate;
        this.sReplyCode=TRREnroll.ReplyCode;
        this.sErrorMessage=TRREnroll.ErrorMessage;
        this.sHumanaReceivedDate=TRREnroll.HumanaReceivedDate;
        this.sSegmentNumber=TRREnroll.SegmentNumber;
		this.sDOB =TRREnroll.DateOfBirth; 
        this.sGender =TRREnroll.GenderCode;
        this.sMiddleInitial=TRREnroll.MiddleInitial;
        this.sState =TRREnroll.StateCode;
        this.sCountyName =TRREnroll.CountyCode;
        this.sEndDate=TRREnroll.EndDate;
        this.sLISPremiumLevel=TRREnroll.LISPremiumLevel;
        this.sLISCopayCategory=TRREnroll.LISCopayCategory;
        this.sLISPeriodEffectiveDate=TRREnroll.LISPeriodEffectiveDate;
        this.sLISPremAmount=TRREnroll.LISPremAmount;
        this.sLISPeriodEndDate=TRREnroll.LISPeriodEndDate;
        this.sLISSourceCode=TRREnroll.LISSourceCode;
        this.sLISEnrolleeTypeCode=TRREnroll.LISEnrolleeTypeCode;
        this.sSecondaryInsDrugFlag=TRREnroll.SecondaryInsDrugFlag;
        this.sSecondaryRxId=TRREnroll.SecondaryRxId;
        this.sSecondaryRxGroup=TRREnroll.SecondaryRxGroup;
        this.sSecondaryRxBIN=TRREnroll.SecondaryRxBIN;
        this.sSecondaryRxPCN=TRREnroll.SecondaryRxPCN;
        
        this.sEmployerSubsidyOverrideFlag=TRREnroll.EmployerSubsidyOverrideFlag;
        this.sEGHPIndicator=TRREnroll.EGHPIndicator;
        this.sPlanAssignedTransTrackingId=TRREnroll.PlanAssignedTransTrackingId;
        this.sDistrictOfficeCode=TRREnroll.DistrictOfficeCode;
        this.sPartDOptOutFlag=TRREnroll.PartDOptOutFlag;
        this.sOutOfAreaFlag=TRREnroll.OutOfAreaFlag;
        this.sDisenrollmentReasonCode=TRREnroll.DisenrollmentReasonCode;
        this.sPartCPremiumAmount=TRREnroll.PartCPremiumAmount;
        this.sPartDPremiumAmount=TRREnroll.PartDPremiumAmount;
        this.sESRDIndicator = TRREnroll.ESRDIndicator;
        this.sAccessibleFmtCode=TRREnroll.AccessibleFmtCode; 
        this.sWAIndicator=TRREnroll.WAIndicator;
        this.sPrefLangcode=TRREnroll.PrefLangcode; 
        this.sPremiumWithholdOptionCode=TRREnroll.PremiumWithholdOptionCode;
        this.sDisabilityIndicator=TRREnroll.DisabilityIndicator;
        this.sHospiceIndicator=TRREnroll.HospiceIndicator;
        this.sMSPStatusFlag=TRREnroll.MSPStatusFlag;
        this.sDeMinimisDifferentialAmount=TRREnroll.DeMinimisDifferentialAmount;
        this.sPrevTROOPPBP=TRREnroll.PrevTROOPPBP;
        this.sInstitutionalIndicator=TRREnroll.InstitutionalIndicator;
        this.sEntitlementTypeCode=TRREnroll.EntitlementTypeCode;     
        
        this.sCreditableCoverageFlag=TRREnroll.CreditableCoverageFlag;
        this.sCumulativeNumberofUncoveredMonths=TRREnroll.CumulativeNumberofUncoveredMonths;
        this.sSubmittedNumberofUncoveredMonths=TRREnroll.SubmittedNumberofUncoveredMonths;
        this.sPartDPenaltyAmount=TRREnroll.PartDPenaltyAmount;
        this.sPartDPenaltyWaivedAmount=TRREnroll.PartDPenaltyWaivedAmount;
        this.sPartDPenaltySubAmount=TRREnroll.PartDPenaltySubAmount;
        this.sPartDRxBIN=TRREnroll.PartDRxBIN;
        this.sPartDRxPCN=TRREnroll.PartDRxPCN;
        this.sPartDRxGroup=TRREnroll.PartDRxGroup;
        this.sPartDRxId=TRREnroll.PartDRxId;
        this.sTransactionCode=TRREnroll.TransactionCode;
        this.sUIOrganization=TRREnroll.UIOrganization;
        this.sEnrollmentSourceCode=TRREnroll.EnrollmentSourceCode;
        this.sVariable=TRREnroll.Variable;
        this.sApplicationDate=TRREnroll.ApplicationDate;
        this.sTransactionDate=TRREnroll.TransactionDate;
        this.sCMSProcessingTimestamp=TRREnroll.CMSProcessingTimestamp;
        this.sApplicationDateIndicator=TRREnroll.ApplicationDateIndicator;
        this.sTRCShortName=TRREnroll.TRCShortName;
        this.sSourceId=TRREnroll.SourceId;
        this.sElectionTypeCode=TRREnroll.ElectionTypeCode;
        this.sPriorPBP=TRREnroll.PriorPBP;
        this.sUIInitialChangeFlag=TRREnroll.UIInitialChangeFlag;
		this.sSEPReasCode=TRREnroll.SEPReasCode;
		this.sDedupIndicator=TRREnroll.DedupIndicator;
		this.sRace = TRREnroll.RaceCd;
        this.sEthinicity = TRREnroll.EthinicityCd;
		
        String uniqueNumber = this.append + String.valueOf(TRRSearchCounter);
        uniqueNumber = uniqueNumber.substring(uniqueNumber.length() - 3);
        this.TRRresultdetails = 'TRR-'+uniqueNumber;
    }
    
    /*this constructor is used for Application search response parsing*/
    public MemberSearchEnrollmentHelper_LC_HUM(ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetails,Integer amSearchCounter)
    {
        this.sEndDate = eDetails.CoverageTermDate; 
        this.sMedicareId = eDetails.MedicareId;
        this.sFirstName = eDetails.FirstName;
        this.sLastName = eDetails.LastName;
        this.sDOB = eDetails.DOB;
        this.sContract = eDetails.ContractId;
        this.sPBP = eDetails.PlanBenefitPackage;
        this.sBSN = eDetails.BSN;
        this.sGroupId = eDetails.GroupId;
        this.sSegmentNumber = eDetails.SegmentId;
        this.sCovEffDt = eDetails.CoverageEffectiveDate;
        this.sReceivedDate = eDetails.MarketReceiptDate;
        this.sApplicationChannel = eDetails.ApplicationChannel;
        this.sOECConfirmationId = eDetails.OECConfirmationId;
        this.sRawOECYear = eDetails.PlanYear;
        this.sWebUniqueId = eDetails.ApplicationId;
		this.sPymtOptType = eDetails.PaymentOptType;
        this.sGender = eDetails.GenderCode;
        this.sMemberLangPreference = eDetails.MemberLangPreference;
        this.sPermanentAddress = eDetails.OtherAddress1 +' '+eDetails.OtherAddress2;
        this.sCity = eDetails.OtherCity;
        this.sState = eDetails.OtherState;
        this.sZip =eDetails.OtherZipCode;
        this.sCountyName = eDetails.CountyName;
        this.sMailaddr = eDetails.MailAddress1 +' '+eDetails.MailAddress2;
        this.sMailCity = eDetails.MailCity;
        this.sMailSt = eDetails.MailState;
        this.sMailZip = eDetails.MailZipCode;
        this.sworkPh = eDetails.WorkPhone;
		this.sHomePh = eDetails.homePhone;
		this.sSignatureDate = eDetails.SignatureDate;
		this.sSignatureType = eDetails.SignatureType;
		this.sSegmentId = eDetails.SegmentId;
        this.sSolSecurity =  eDetails.SSN;
        this.sContact = eDetails.HomePhone;
        this.sEmployerId = eDetails.EmployerId;
        this.sOSBPackage = eDetails.OSBPackage;
        this.sPymtOpt = eDetails.PaymentOptIndicator;
        this.sSmokingIndicator = eDetails.SmokeIndicator;
        this.sDisabledIndicator = eDetails.DisabledIndicator;
        this.sPCPName= eDetails.PCPName;
        this.sPCPNumber = eDetails.PCPNumber;
        this.sESRDIndicator = eDetails.ESRDIndicator;
        this.sBarCodeBase = eDetails.BarCodeBase;
        this.sSEPDate = eDetails.SEPDate;
        this.sSepReasonCode = eDetails.SepReasonCode;
        this.sPartAEligibleDate = eDetails.PartAEligibleDate;
        this.sPartBEligibleDate = eDetails.PartBEligibleDate;
        this.sFileDt = eDetails.FileDate;
        this.sProcDt = eDetails.ProcessedDate;
        this.sSalesRepSSN = eDetails.SalesRepSSN;
        this.sSalesType = eDetails.SalesType;
        this.sSalesRepName = eDetails.salesRepName;
        this.sPOAPhone = eDetails.POAPhone;
        this.sPOARelationType = eDetails.POARelationType;
		this.sPOAFirstName = eDetails.POAFirstName;
        this.sPOALastName = eDetails.POALastName;
        this.sCreationTs = eDetails.CreationTs;
        this.sElectionTypeCd = eDetails.ElectionTypeCd;
		this.sMedicaidId = eDetails.MedicaidId;
        this.sContractId = eDetails.ContractId;
		this.sRace = eDetails.RaceCd;
        this.sEthinicity = eDetails.EthinicityCd;
        
        String ssoURL = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL').Remote_Url__c;
        String uniqueNumber = append + String.valueOf(amSearchCounter);
        uniqueNumber = uniqueNumber.substring(uniqueNumber.length() - 3);
        this.details = 'AS-'+uniqueNumber;
        
        if(String.isNotBlank(eDetails.ApplicationId))
        {
            this.application = eDetails.ApplicationId;
            if(Endpoint_Url_HUM__c.getInstance('FAST_APP_URL') != null && String.isNotBlank(Endpoint_Url_HUM__c.getInstance('FAST_APP_URL').Remote_Url__c))
            {
                this.applicationURL = ssoURL + '?HIDDENTARGET=' + EncodingUtil.base64Encode(blob.valueOf(Endpoint_Url_HUM__c.getInstance('FAST_APP_URL').Remote_Url__c)); 
            }            
        }
        else if(String.isNotBlank(eDetails.DCNNumber))
        {
            this.application = eDetails.DCNNumber;
            
            if(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL') != null && String.isNotBlank(Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c) && (eDetails.DCNNumber).startsWithIgnoreCase('AC'))
            {
                String dcnURL = Endpoint_Url_HUM__c.getInstance('DCNSpider2URL').Remote_Url__c ;           
                this.applicationURL =  dcnURL + eDetails.DCNNumber;                        
            }
        }
    }
    
    /*This constructor is used for CBI search Response parsing*/
    public MemberSearchEnrollmentHelper_LC_HUM(HUMCBISSearchServiceHelper.ResponseItem oRespItem,Integer count)
    {
        Boolean  bdisplayInteractbutton =false;
        
        List<HUMRestrictedGroupIDs__c> lstsRestrictGroupID = HUMRestrictedGroupIDs__c.getall().values();
        this.sDOB = oRespItem.DOB;
        String uniqueNumber = '000' + String.valueOf(count);
        uniqueNumber = uniqueNumber.substring(uniqueNumber.length() - 3);
        this.details = 'AS-'+uniqueNumber;
        this.sDateTime = '';
        if(oRespItem.policy==null)
        {
            assignValues(oRespItem.FirstName, oRespItem.LastName, '', oRespItem.Platform, '', '', '', '' );
            this.sProduct=oRespItem.policy.benID;
            this.sPend=oRespItem.policy.pendError;
            this.sEffectiveDate = oRespItem.policy.CoverageStartDate;
            this.sEndDate =  oRespItem.policy.CoverageEndDate;
			this.sExternalSystemType= System.Label.HUMCBISCode;
        }
        else
        {
            for (integer i=0; i<lstsRestrictGroupID.size();i++)
            {
                if(oRespItem.policy!=null && lstsRestrictGroupID[i].name ==oRespItem.policy.GroupId)
                {
                    bdisplayInteractbutton=true; 
                }
            }
        }
        if(bdisplayInteractbutton)
        {
            this.sFirstName = oRespItem.FirstName;
            this.sLastName = oRespItem.LastName;
        }
        else if(!bdisplayInteractbutton && oRespItem.policy!=null)
        {
            assignValues( oRespItem.FirstName, oRespItem.LastName, '', oRespItem.Platform, oRespItem.policy.GroupId, '', '', '' );
            this.sProduct=oRespItem.policy.benID;
            this.sPend=oRespItem.policy.pendError;
            this.sEffectiveDate = oRespItem.policy.CoverageStartDate;
            this.sEndDate =  oRespItem.policy.CoverageEndDate;
            this.sGroupId = oRespItem.policy.PendKey;
            this.sPendKey = oRespItem.policy.GroupId;
			this.sExternalSystemType= System.Label.HUMCBISCode; 
        } 
        String ExternalID =  oRespItem.FirstName+ oRespItem.LastName+String.valueof(oRespItem.DOB)+oRespItem.Address.Zip;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(ExternalID );
        if(String.isNotBlank(this.sExternalID)){
            String sAccountExternalID = HUMConstants__c.getInstance('CBIS_EXTID').StringValue__c + this.sExternalID;
            this.sLegacyMemberJson = Json.serialize(createAcc(sAccountExternalID));  
            this.sExternalID = sAccountExternalID;             
        }
    }
    
     /*
    * Method Name   :    MemberSearchEnrollmentHelper_LC_HUM constructor -
    * Description   :    New Constructor for CBIS Service REST
    * Return Type   :    NA
    * Parameters    :    HUMCBISSearchResponse_H_HUM.ResponseItem
    */
    
    public MemberSearchEnrollmentHelper_LC_HUM(HUMCBISSearchResponse_H_HUM.ResponseItem oRespItem)
    {
        system.debug('##--New CBIS constructor is executing');
        Boolean  bdisplayInteractbutton =false;
        
        List<HUMRestrictedGroupIDs__c> lstsRestrictGroupID = HUMRestrictedGroupIDs__c.getall().values();
        this.sDOB = oRespItem.DOB;
        this.sExternalID = oRespItem.MemberId;
        
        if(oRespItem.policy==null)
        {
            assignValues( oRespItem.FirstName, oRespItem.LastName, null, oRespItem.Platform, null, null, null, null );
            this.sProduct=oRespItem.policy.benID;
            this.sPend=oRespItem.policy.pendError;
            this.sPendKey=oRespItem.policy.PendKey;
            this.sEffectiveDate = oRespItem.policy.CoverageStartDate;
            this.sEndDate =  oRespItem.policy.CoverageEndDate;
            //this.sDateTimePend=oRespItem.policy.PendDate;
            this.sExternalSystemType= System.Label.HUMCBISCode;	
            this.sMailaddr = oRespItem.Address.Address1 +' '+ oRespItem.Address.Address2;
            this.sMailCity = oRespItem.Address.City;
            this.sMailSt = oRespItem.Address.State;
            this.sMailZip = oRespItem.Address.Zip;
            this.sGroupId = oRespItem.policy.GroupId;
        }
        else
        {
            for (integer i=0; i<lstsRestrictGroupID.size();i++)
            {
                if(oRespItem.policy!=null && lstsRestrictGroupID[i].name ==oRespItem.policy.GroupId)
                {
                    this.bdisplayInteractbutton=true; 
                }
           }
       }
       if(bdisplayInteractbutton)
       {
            this.sFirstName = oRespItem.FirstName;
            this.sLastName = oRespItem.LastName;
       }
       else if(!bdisplayInteractbutton && oRespItem.policy!=null)
       {
            assignValues( oRespItem.FirstName, oRespItem.LastName, null, oRespItem.Platform, oRespItem.policy.GroupId, null, null, null );
            this.sProduct=oRespItem.policy.benID;
            this.sPend=oRespItem.policy.pendError;
            this.sPendKey=oRespItem.policy.PendKey;
            this.sEffectiveDate = oRespItem.policy.CoverageStartDate;
            this.sEndDate =  oRespItem.policy.CoverageEndDate;
            //this.sDateTimePend=oRespItem.policy.PendDate;
            this.sExternalSystemType= System.Label.HUMCBISCode;
            this.sMailaddr = oRespItem.Address.Address1 +' '+ oRespItem.Address.Address2;
            this.sMailCity = oRespItem.Address.City;
            this.sMailSt = oRespItem.Address.State;
            this.sMailZip = oRespItem.Address.Zip;
            this.sGroupId = oRespItem.policy.GroupId;
       }
        String ExternalID =  oRespItem.FirstName+ oRespItem.LastName+String.valueof(oRespItem.DOB)+oRespItem.Address.Zip;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(ExternalID );
        if(String.isNotBlank(this.sExternalID)){
            String sAccountExternalID = HUMConstants__c.getInstance('CBIS_EXTID').StringValue__c + this.sExternalID;
            this.sLegacyMemberJson = Json.serialize(createAcc(sAccountExternalID));  
            this.sExternalID = sAccountExternalID;       
        }
    }
    
    /* 
    * Method name : assignValues
    * Description : This method will store the values in variables 
    * Parameter   : String variables
    */
    void assignValues(String firstName, String lastName, String DOB, String platform, string groupId, String LOB, String effectiveDate, String endDate)
    {
        this.sFirstName = firstName;
        this.sLastName = lastName;
        if(!String.isBlank(DOB))
        {
            this.sDOB = memSearch.convertDate(String.valueOf(Date.valueOf(DOB)));
        }
        this.sPlatform = platform;
        this.sGroupId = groupId;
        this.sLOB = LOB;
        if(!String.isBlank(effectiveDate))
        {
            this.sEffectiveDate = memSearch.convertDate(String.valueOf(Date.valueOf(effectiveDate)));
        }
        if(!String.isBlank(endDate))
        {
            this.sEndDate= memSearch.convertDate(String.valueOf(Date.valueOf(endDate)));
        }
    }
	
/*
    * Method Name : createAcc
    * Description : This function is used for creating unknown accounts record in CRM based on external Id
    * Return type : Account
    * Paramater   : String AccExternalId
    */
  public Account createAcc( String AccExternalId )
    {
        Account accountRecord;
        try{
            string sfirstName=this.sFirstName;
            Integer ilengthFName = Integer.valueof(System.label.HUMFirstNameLength);
            if(this.sFirstName != null && this.sFirstName.length() > ilengthFName )
            {
                sfirstName=this.sFirstName.substring(0,ilengthFName );
            }
            
            string slastName=this.sLastName;
            Integer ilengthLName = Integer.valueof(System.label.HUMLastNamelength);
            if(this.sLastName != null && this.sLastName.length() > ilengthLName )
            {
                sLastName =this.sLastName.substring(0,ilengthLName );
            }
            
            accountRecord = new Account(FirstName = sfirstName, LastName = slastName ,
            Birthdate__c = this.sDOB , recordtypeid = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(), HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c),
            Source_Platform_Code__c = this.sPlatform,
            Group_Number__c = this.sGroupId,
            Account_External_ID__c = AccExternalId,
            Tenant_Id__c = system.label.HUMExtMemAccountTenantID,
            General_Account__c = true,
            Individual_First_Name__c = this.sFirstName,
            Individual_Last_Name__c = this.sLastName);
        }catch (Exception e) 
        {   
            HUMExceptionHelper.logErrors(e,'MemberSearchEnrollmentHelper_LC_HUM', 'createAcc');
        }
        return accountRecord;
    }

    @AuraEnabled
    public static string insertMemberAccount(String accountJson,String externalId){
        String sId = '';
        try 
        {   
            Account newAcc = (Account) JSON.Deserialize(accountJson, Account.Class);        
            //Conditional Logic that checks the Group Record Type and Insert or Just return the ID          
            if (newAcc != null)
            {            
                List < Account > upsertAccounts = new List < Account >();               
                Database.upsertResult[] upsertResults = Database.upsert(new List<Account>{newAcc}, Account.Fields.Account_External_ID__c, false);
                boolean isError = HUMExceptionHelper.processUpsertResults(upsertResults, new List<Account>{newAcc}, 
                            'MemberSearchEnrollmentHelper_LC_HUM', 'insertMemberAccount', 'Account');
                if (!isError) sId = newAcc.Id;
            }
        } 
        catch (Exception e) 
        {   
            HUMExceptionHelper.logErrors(e,'MemberSearchEnrollmentHelper_LC_HUM', 'insertMemberAccount');
            sId = '';
        }
        return sId;
    }
}