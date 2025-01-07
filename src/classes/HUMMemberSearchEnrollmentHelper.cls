/******************************************************************************************************************************************************
Apex Class Name : HUMMemberSearchEnrollmentHelper
Version         : 1.0
Created On      : 08/06/2014
Function        : This class is the wrapper class used for the Search Enrollment functionality.
Test Class       : HUMMemberSearchEnrollmentHelperTest

Modification Log:
* Developer Name             Code Review                Date                       Description

*------------------------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth              17165                   08/06/2014                 Original Version
* Shruthi Karanth              17355                   08/14/2014                 Changes as per updated wsdl for AE History service
* Pranali Choudhari            18890                   10/27/2014                 Updated code for CBIS
* Shreya Choodamani            19131                   11/07/2014                 Added fix for defect 157037.0001
* Pradeep Veeravali            19401                   11/19/2014                 Changes as per updated wsdl for Enrollment (H1Exchange) Service
* Pradeep Veeravali            19401                   11/24/2014                 Changes as per defect in Code Review
* Shruthi Karanth                                      11/27/2014                 Changes to map Effective date and End date for CBIS Service
* Santhi Mandava               19730                   12/05/2014                 Incorporated Req-63682 changes.
* Raja Sekhar Reddy M V        19747                   12/08/2014                 Modified code for REQ-66104
* Pradeep Raju                 19747                   12/08/2014                 Updated the code at line# 284, 285 based on review comments
* Pradeep Raju                 19747                   12/09/2014                 Updated the code for defect id D18304 fix based on review comments
* Raja Sekhar Reddy M V        19882                   12/11/2014                 Updated the code for converting string to date to display in mm/DD/YYYY
* Ranjeeth Nagishetty          19882                   12/12/2014                 Changes in REQ-63682
* Ranjeeth Nagishetty          20103                   12/22/2014                 REQ-95955 adding the smoke indicator
* Raja Sekhar Reddy M V        20103                   12/26/2014                 Added code for checking null values for Date fields.
* Shruthi Karanth              20145                   12/31/2014                 Defect 155928.0001 : AE History service changes to accomodate data type change of elements Cov_Eff_Date,Cov_End_Date from Date to String
* Shruthi Karanth              20274                   01/09/2015                 Defect 155928.0001 : Checking for blank values for Date instaed of null since the data type is string
* Ranjeeth Nagishetty          21021                   02/12/2015                 REQ - 121513:State of Florida Messaging on Search Enrollment Tab
* ChiranjeeviRao Ravuri        21640                   03/17/2015                 REQ - 160866 : Changed the apex upsert logic to use the Member_ID from the service response instead of the salesforce ID for the account_external_id__c
* Raja Sekhar Reddy M V        21724                   03/19/2015                 Code changed for Defect # 168065.0001  
* Ranjeeth Nagishetty          23472                   05/28/2015                 REQ -189913 : Changing the External ID for H1 and AE service
* Ranjeeth Nagishetty          23864                   06/08/2015                 REQ - 191244 :Changing the External Id for the CBIS
* Shreya Choodamani            29217                   01/12/2016                 Added Method HUMMemberSearchEnrollmentHelper(HUMH1SearchResponseHelper.EnrollmentDetail enrollMember), HUMMemberSearchEnrollmentHelper(HUMAEServicesHistoryResponse.PendHistory aeHis), HUMMemberSearchEnrollmentHelper(HUMCBISSearchServiceHelper.ResponseItem oRespItem), HUMMemberSearchEnrollmentHelper(enrollmentTrackResponse.Track trackEnroll)
* Syed Mubarak T 				                       04/30/2019	              REQ - 380470 Application Search in Search Enrollment
* Seema Kaulgi                                         03/15/2019                 REQ - 379048  SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
* Raja Sekhar Reddy MV                                 09/07/2020                 US_1368140: PR00089386 - CR 24 - Add 'End Date' to 'Search Enrollment' tab on the Search Page
* Firoja Begam                                         05/10/2021                 US-2229318 - PR00094232/T1PRJ0002736 - 2021 MF6 - Security - SF - CBIS Specialty Service Migration
* Vardhman Jain                                        08/18/2021                 REQ - US 2098896  - Add Fields to TRR Reply Detail
* Kiran Kumar B					                       07/05/2022		          User Story - 2568652: Billing- Search Enrollment tab- add 'Billing Payment Option Type' field
* Bhumika S                                           10/14/2022                 User Story 3883795: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs-CBIS
* Santhi Mandava                                      11/16/2022                  US3482496_TestClass issue fix
* Nilesh Gadkar					                      12/22/2022	        	  User Story 3883798: T1PRJ0533784 -CR 6/MF 25 - Enhance the search capability Functionality on search enrollment tab within CRMs_CIMTV
* Nilesh Gadkar					                      02/28/2023               	  User Story 4259019: INC2174123 search enrollment screen just released 1/27 is not showing all of the products of a members
* Nilesh Gadkar					      05/18/2023		  User Story 4407442: T1PRJ0191994- MF 128, 129- Display Member Race and Ethnicity in CRM Service- ‘TRR Reply Detail’ page (Classic)	
* Nilesh Gadkar					                      05/18/2023		          User Story 4407442: T1PRJ0191994- MF 128, 129- Display Member Race and Ethnicity in CRM Service- ‘TRR Reply Detail’ page (Classic)	
******************************************************************************************************************************************************/
public with sharing class HUMMemberSearchEnrollmentHelper  
{   
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor
    * Description   :    Constructor for call its methods from other class on instantiate
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public HUMMemberSearchEnrollmentHelper(){}
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor
    * Description   :    Constructor for EES Legacy Search
    * Return Type   :    NA
    * Parameters    :    Map<String,Map<String,String>>,Integer
    */
    public HUMMemberSearchEnrollmentHelper(Map<String,Map<String,String>> mapMemberInfo, Integer i)
    {
        String sKey = '';
        if(mapMemberInfo.containsKey(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember))
        {
            this.sMailaddr = fetchValuesFromMap('Line1' , MapMemberInfo.get('PlatformMember:Address'));
            this.sMailCity = fetchValuesFromMap('City' , MapMemberInfo.get('PlatformMember:Address'));
            this.sMailSt = fetchValuesFromMap('State' , MapMemberInfo.get('PlatformMember:Address'));
            this.sMailZip = fetchValuesFromMap('Zip' , MapMemberInfo.get('PlatformMember:Address'));
            this.sFirstName = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_FirstName ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sLastName = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_LastName ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sDOB = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_DateOfBirth ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sPlatform = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_SrcPlatformCode ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
            this.sExternalID = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_PID ,mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_Family_PlatformMember));
        }
        if(mapMemberInfo.containsKey(system.label.HUMEESLegacyMemberSearchHelper_MemberContract_Group+i))
        {
            this.sGroupId = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_GroupID , mapMemberInfo.get(system.label.HUMEESLegacyMemberSearchHelper_MemberContract_Group+i));
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
            this.sLOB = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_LineOfBusiness , mapMemberInfo.get(sKey));
            this.sProduct = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_CategoryDesciption , mapMemberInfo.get(sKey));
            this.sEffectiveDate = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_MemberCoverageEffectiveDate , mapMemberInfo.get(sKey));
            this.sEndDate = fetchValuesFromMap(system.label.HUMEESLegacyMemberSearchHelper_MemberCoverageEndDate , mapMemberInfo.get(sKey));
        }
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_EES').StringValue__c + this.sExternalID;
        Account account = createAcc( sAccountExternalID );
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor
    * Description   :    Constructor for H1 exchange service
    * Return Type   :    NA
    * Parameters    :    HUMIntH1ExchangeServiceHelper.EnrollmentApplicationDTO
    */
    public HUMMemberSearchEnrollmentHelper(HUMH1ExchangeResponseHelper.EnrollmentDetail enrollMember)
    {
        
        assignValues( enrollMember.firstName, enrollMember.lastName, enrollMember.strDob, null, enrollMember.groupId, null, enrollMember.covEffDt, enrollMember.covTrmDt);
        this.sPend = enrollMember.applnId;
        this.sExternalID = enrollMember.exchMbrId;
        this.sExternalSystemType = system.label.HUMH1Service_label;
        this.sState = enrollMember.othSt;
        this.sZip = enrollMember.othZip;
        this.sExchangeID=enrollMember.exchMbrID;
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
        
        if(!String.isBlank(enrollMember.csrEffDate)) this.sCsrEffDate = convertDate(String.valueOf(Date.valueOf(enrollMember.csrEffDate)));
        if(String.isBlank(enrollMember.csrEffDate)) this.sCsrEffDate = enrollMember.csrEffDate;
        if(!String.isBlank(enrollMember.csrEndDate)) this.sCsrEndDate = convertDate(String.valueOf(Date.valueOf(enrollMember.csrEndDate)));
        if(String.isBlank(enrollMember.csrEndDate)) this.sCsrEndDate = enrollMember.csrEndDate;
        if(!String.isBlank(enrollMember.aptcEffDate)) this.sAptcEffDate = convertDate(String.valueOf(Date.valueOf(enrollMember.aptcEffDate)));
        if(String.isBlank(enrollMember.aptcEffDate)) this.sAptcEffDate = enrollMember.aptcEffDate;
        if(!String.isBlank(enrollMember.aptcEndDate)) this.sAptcEndDate = convertDate(String.valueOf(Date.valueOf(enrollMember.aptcEndDate)));
        if(String.isBlank(enrollMember.aptcEndDate)) this.sAptcEndDate = enrollMember.aptcEndDate;
        if(!String.isBlank(enrollMember.covEffDt)) this.sCovEffDt = convertDate(String.valueOf(Date.valueOf(enrollMember.covEffDt)));
        if(String.isBlank(enrollMember.covEffDt)) this.sCovEffDt = enrollMember.covEffDt;
        if(!String.isBlank(enrollMember.mktRcptDate)) this.sMktRcptDate = convertDate(String.valueOf(Date.valueOf(enrollMember.mktRcptDate)));
        if(String.isBlank(enrollMember.mktRcptDate)) this.sMktRcptDate = enrollMember.mktRcptDate;
        if(!String.isBlank(enrollMember.signDATE)) this.sSignDATE = convertDate(String.valueOf(Date.valueOf(enrollMember.signDATE)));
        if(String.isBlank(enrollMember.signDATE)) this.sSignDATE = enrollMember.signDATE;
        if(!String.isBlank(enrollMember.fileDt)) this.sFileDt = convertDate(String.valueOf(Date.valueOf(enrollMember.fileDt)));
        if(String.isBlank(enrollMember.fileDt)) this.sFileDt = enrollMember.fileDt;
        if(!String.isBlank(enrollMember.procDt)) this.sProcDt = convertDate(String.valueOf(Date.valueOf(enrollMember.procDt)));
        if(String.isBlank(enrollMember.procDt)) this.sProcDt = enrollMember.procDt;
        this.sBarCodeBase = enrollMember.barCodeBase;
        if(enrollMember.smokeInd == 'Y') this.sSmokingIndicator = System.label.HUMSmokingIndicator;
        if(enrollMember.smokeInd == 'N') this.sSmokingIndicator = system.label.HUMsmokindicatorNO;
        if(String.isBlank(enrollMember.smokeInd)) this.sSmokingIndicator = enrollMember.smokeInd;
        this.sOecConfId = enrollMember.oecConfId;
        this.sApplnChnl = enrollMember.applnChnl;
        
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_H1').StringValue__c + this.sExternalID ;
        
        Account account = createAcc( sAccountExternalID );
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor
    * Description   :    Constructor for H1 exchange service
    * Return Type   :    NA
    * Parameters    :    HUMH1SearchResponseHelper.EnrollmentDetail
    */
    public HUMMemberSearchEnrollmentHelper(HUMH1SearchResponseHelper.EnrollmentDetail enrollMember)
    {
        assignValues( enrollMember.firstName, enrollMember.lastName, enrollMember.strDob, null, enrollMember.groupId, null, enrollMember.covEffDt, enrollMember.covTrmDt);
        this.sPend = enrollMember.applnId;
        this.sExternalID = enrollMember.exchMbrId;
        this.sExternalSystemType = system.label.HUMH1Service_label;
        this.sState = enrollMember.othSt;
        this.sZip = enrollMember.othZip;
        this.sExchangeID=enrollMember.exchMbrID;
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
        
        if(!String.isBlank(enrollMember.csrEffDate)) this.sCsrEffDate = convertDate(String.valueOf(Date.valueOf(enrollMember.csrEffDate)));
        if(String.isBlank(enrollMember.csrEffDate)) this.sCsrEffDate = enrollMember.csrEffDate;
        if(!String.isBlank(enrollMember.csrEndDate)) this.sCsrEndDate = convertDate(String.valueOf(Date.valueOf(enrollMember.csrEndDate)));
        if(String.isBlank(enrollMember.csrEndDate)) this.sCsrEndDate = enrollMember.csrEndDate;
        if(!String.isBlank(enrollMember.aptcEffDate)) this.sAptcEffDate = convertDate(String.valueOf(Date.valueOf(enrollMember.aptcEffDate)));
        if(String.isBlank(enrollMember.aptcEffDate)) this.sAptcEffDate = enrollMember.aptcEffDate;
        if(!String.isBlank(enrollMember.aptcEndDate)) this.sAptcEndDate = convertDate(String.valueOf(Date.valueOf(enrollMember.aptcEndDate)));
        if(String.isBlank(enrollMember.aptcEndDate)) this.sAptcEndDate = enrollMember.aptcEndDate;
        if(!String.isBlank(enrollMember.covEffDt)) this.sCovEffDt = convertDate(String.valueOf(Date.valueOf(enrollMember.covEffDt)));
        if(String.isBlank(enrollMember.covEffDt)) this.sCovEffDt = enrollMember.covEffDt;
        if(!String.isBlank(enrollMember.mktRcptDate)) this.sMktRcptDate = convertDate(String.valueOf(Date.valueOf(enrollMember.mktRcptDate)));
        if(String.isBlank(enrollMember.mktRcptDate)) this.sMktRcptDate = enrollMember.mktRcptDate;
        if(!String.isBlank(enrollMember.signDATE)) this.sSignDATE = convertDate(String.valueOf(Date.valueOf(enrollMember.signDATE)));
        if(String.isBlank(enrollMember.signDATE)) this.sSignDATE = enrollMember.signDATE;
        if(!String.isBlank(enrollMember.fileDt)) this.sFileDt = convertDate(String.valueOf(Date.valueOf(enrollMember.fileDt)));
        if(String.isBlank(enrollMember.fileDt)) this.sFileDt = enrollMember.fileDt;
        if(!String.isBlank(enrollMember.procDt)) this.sProcDt = convertDate(String.valueOf(Date.valueOf(enrollMember.procDt)));
        if(String.isBlank(enrollMember.procDt)) this.sProcDt = enrollMember.procDt;
      
        this.sBarCodeBase = enrollMember.barCodeBase;
        if(enrollMember.smokeInd == 'Y') this.sSmokingIndicator = System.label.HUMSmokingIndicator;
        if(enrollMember.smokeInd == 'N') this.sSmokingIndicator = system.label.HUMsmokindicatorNO;
        if(String.isBlank(enrollMember.smokeInd)) this.sSmokingIndicator = enrollMember.smokeInd;
        this.sOecConfId = enrollMember.oecConfId;
        this.sApplnChnl = enrollMember.applnChnl;
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_H1').StringValue__c + this.sExternalID;
        Account account = createAcc( sAccountExternalID );
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for AE History service
    * Return Type   :    NA
    * Parameters    :    HUMIntAEHistoryResponseHelper.Response_element generating hash code
    */
    public HUMMemberSearchEnrollmentHelper(HUMIntAEHistoryResponseHelper.PendHistory aeHis)
    {
            assignValues( aeHis.First_Name, aeHis.Last_Name, String.valueof(aeHis.Birth_Date), aeHis.Platform, aeHis.Group_Id, aeHis.Ben_Id, aeHis.Cov_Eff_Date, aeHis.Cov_End_Date);
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
            String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_AE').StringValue__c + this.sExternalID;
            Account account = createAcc( sAccountExternalID );
            sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor - Async
    * Description   :    Constructor for AE History service
    * Return Type   :    NA
    * Parameters    :    HUMAEServicesHistoryResponse.Response_element
    */
    public HUMMemberSearchEnrollmentHelper(HUMAEServicesHistoryResponse.PendHistory aeHis)
    {
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
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_AE').StringValue__c + this.sExternalID;  
        Account account = createAcc(sAccountExternalID);
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for TrackEnrollment service
    * Return Type   :    NA
    * Parameters    :    HUMIntTrackEnrollmentResponseHelper.Response_element
    */
    public HUMMemberSearchEnrollmentHelper(HUMIntTrackEnrollmentResponseHelper.Track trackEnroll)
    {
        assignValues( trackEnroll.First_Name, trackEnroll.Last_Name, string.valueof(trackEnroll.Birth_Date), null, trackEnroll.Group_Id, trackEnroll.Line_Of_Coverage, null, null);
        this.sPendKey = trackEnroll.Pend_Key;
        this.sExternalID = trackEnroll.Source_Track_Id;
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_TE').StringValue__c + this.sExternalID;
        Account account = createAcc( sAccountExternalID );
        sLegacyMemberJson = Json.serialize(account);
    }
    
     /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for TrackEnrollment service
    * Return Type   :    NA
    * Parameters    :    HUMIntTrackEnrollmentResponseHelper.Response_element
    */
    public HUMMemberSearchEnrollmentHelper(enrollmentTrackResponse.Track trackEnroll)
    {
        assignValues( trackEnroll.First_Name, trackEnroll.Last_Name, string.valueof(trackEnroll.Birth_Date), null, trackEnroll.Group_Id, trackEnroll.Line_Of_Coverage, null, null);
        this.sPendKey = trackEnroll.Pend_Key;
        this.sExternalID = trackEnroll.Source_Track_Id;
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_TE').StringValue__c + this.sExternalID;
        Account account = createAcc( sAccountExternalID );
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for creating Unknown member
    * Return Type   :    NA
    * Parameters    :    Account object
    */
    public HUMMemberSearchEnrollmentHelper(Account account)
    {
        assignValues( account.FirstName, account.LastName, null, account.Source_Platform_Code__c, account.Group_Number__c, null, null, null );
        this.sDOB = account.Birthdate__c;
        this.sPendKey = '';
        this.iAccntId = account.id;        
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :   HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for OHBP Service
    * Return Type   :    NA
    * Parameters    :    Map<String,String>
    */
    public HUMMemberSearchEnrollmentHelper(Map<String,String> mapAccount)
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
            this.sDOB  = convertDate(String.valueOf(Date.valueOf(mapAccount.get(system.label.HUMOHBPSearchBirthDt))));
        }
        if(mapAccount.containsKey(system.label.HUMOHBPSearchProd))
        {
            this.sProduct = mapAccount.get(system.label.HUMOHBPSearchProd);
        }
        if(mapAccount.containsKey(system.label.HUMOHBPSearchIDPlan))
        {
            this.sGroupId = mapAccount.get(system.label.HUMOHBPSearchIDPlan);
        }
        this.sLOB = '';
        if(mapAccount.containsKey(system.label.HUMOHBPSearchEfectDate))
        {
            this.sEffectiveDate = convertDate(String.valueOf(Date.valueOf(mapAccount.get(system.label.HUMOHBPSearchEfectDate))));
        }
        this.sZip  = mapAccount.get('PersonMailingPostalCode');
        this.sEndDate = '';
        this.sPendKey = '';
        
        string sextIDohbp = this.sFirstName+this.sLastName+this.sDOB+this.sZip ;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(sextIDohbp );
        String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_OHBP').StringValue__c +sExternalID;
        
        //String sAccountExternalID = HUMConstants__c.getInstance('ENROLLMENT_OHBP').StringValue__c + mapAccount.get('externalID');
        
        Account account = createAcc( sAccountExternalID );
        sLegacyMemberJson = Json.serialize(account);
    }
    
    /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for CBIS Service
    * Return Type   :    NA
    * Parameters    :    HUMCbisSpecialityHelper.ResponseItem
    */
    public HUMMemberSearchEnrollmentHelper(HUMIntCbisServiceHelper.ResponseItem oRespItem)
    {
        bdisplayInteractbutton =false;
        
        List<HUMRestrictedGroupIDs__c> lstsRestrictGroupID = HUMRestrictedGroupIDs__c.getall().values();
        this.sDOB = oRespItem.DOB;
        
        if(oRespItem.policy==null)
        {
            assignValues( oRespItem.FirstName, oRespItem.LastName, null, oRespItem.Platform, null, null, null, null );
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
            this.sEffectiveDate = oRespItem.policy.CoverageStartDate;
            this.sEndDate =  oRespItem.policy.CoverageEndDate;
            this.sExternalSystemType= System.Label.HUMCBISCode;
       }
       
        String ExternalID =  oRespItem.FirstName+ oRespItem.LastName+String.valueof(oRespItem.DOB)+oRespItem.Address.Zip;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(ExternalID );
        String sAccountExternalID = HUMConstants__c.getInstance('CBIS_EXTID').StringValue__c + this.sExternalID;
        Account account = createAcc(sAccountExternalID);
        sLegacyMemberJson = Json.serialize(account);
     }
     
     /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for CBIS Service Async
    * Return Type   :    NA
    * Parameters    :    HUMCbisSpecialityHelper.ResponseItem
    */
    public HUMMemberSearchEnrollmentHelper(HUMCBISSearchServiceHelper.ResponseItem oRespItem)
    {
        bdisplayInteractbutton =false;
        
        List<HUMRestrictedGroupIDs__c> lstsRestrictGroupID = HUMRestrictedGroupIDs__c.getall().values();
        this.sDOB = oRespItem.DOB;
        this.sExternalID = oRespItem.MemberId;
        
        if(oRespItem.policy==null)
        {
            assignValues( oRespItem.FirstName, oRespItem.LastName, null, oRespItem.Platform, null, null, null, null );
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
            this.sEffectiveDate = oRespItem.policy.CoverageStartDate;
            this.sEndDate =  oRespItem.policy.CoverageEndDate;
            this.sExternalSystemType= System.Label.HUMCBISCode;
       }
        String ExternalID =  oRespItem.FirstName+ oRespItem.LastName+String.valueof(oRespItem.DOB)+oRespItem.Address.Zip;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(ExternalID );
        String sAccountExternalID = HUMConstants__c.getInstance('CBIS_EXTID').StringValue__c + this.sExternalID;
        Account account = createAcc(sAccountExternalID);
        sLegacyMemberJson = Json.serialize(account);
     }
	 

     /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for CBIS Service REST
    * Return Type   :    NA
    * Parameters    :    HUMCbisSpecialityHelper.ResponseItem
    */
    public HUMMemberSearchEnrollmentHelper(HUMCBISSearchResponse_H_HUM.ResponseItem oRespItem)
    {
        bdisplayInteractbutton =false;
        
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
            this.sDateTimePend=oRespItem.policy.PendDate;
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
            this.sDateTimePend=oRespItem.policy.PendDate;
            this.sExternalSystemType= System.Label.HUMCBISCode;
            this.sMailaddr = oRespItem.Address.Address1 +' '+ oRespItem.Address.Address2;
            this.sMailCity = oRespItem.Address.City;
            this.sMailSt = oRespItem.Address.State;
            this.sMailZip = oRespItem.Address.Zip;
            this.sGroupId = oRespItem.policy.GroupId;
       }
        String ExternalID =  oRespItem.FirstName+ oRespItem.LastName+String.valueof(oRespItem.DOB)+oRespItem.Address.Zip;
        this.sExternalID = HUMUtilityHelper.getMD5Hash(ExternalID );
        String sAccountExternalID = HUMConstants__c.getInstance('CBIS_EXTID').StringValue__c + this.sExternalID;
        Account account = createAcc(sAccountExternalID);
        sLegacyMemberJson = Json.serialize(account);
    }


 /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor -
    * Description   :    Constructor for Application Search
    * Return Type   :    NA
    * Parameters    :    ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetails,Integer amSearchCounter
    */
	
	public HUMMemberSearchEnrollmentHelper(ApplicationSearch_Response_DTO_HUM.enrollmentDetailWrapper eDetails,Integer amSearchCounter)
     {
        this.sMedicareId = eDetails.MedicareId;
        this.sSolSecurity =  eDetails.SSN;
        this.sMedicaidId = eDetails.MedicaidId;
        this.sFirstName = eDetails.FirstName;
        this.sLastName = eDetails.LastName;
        this.sDOB = eDetails.DOB;
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
        this.sHomePh = eDetails.HomePhone;
        this.sworkPh = eDetails.WorkPhone;
        this.sPOAFirstName = eDetails.POAFirstName;
        this.sPOALastName = eDetails.POALastName;
        this.sPOARelationType = eDetails.POARelationType;
        this.sPOAPhone = eDetails.POAPhone;
        this.sContractId = eDetails.ContractId;
        this.sPlanBenefitPackage = eDetails.PlanBenefitPackage;
        this.sOSBPackage= eDetails.OSBPackage;
        this.sBSN = eDetails.BSN;
        this.sGroupId = eDetails.GroupId;
        this.sSegmentId = eDetails.SegmentId;
        this.sCovEffDt = eDetails.CoverageEffectiveDate;
		this.sCovTermDt = eDetails.CoverageTermDate;
        this.sReceivedDate = eDetails.MarketReceiptDate;
        this.sEmployerId = eDetails.EmployerId;
        this.sSignatureType = eDetails.SignatureType;
        this.sSignatureDate = eDetails.SignatureDate;
        this.sPymtOpt = eDetails.PaymentOptIndicator;
	    this.sPymtOptType = eDetails.PaymentOptType;
        this.sSalesRepName = eDetails.SalesRepName;
        this.sSalesRepSSN = eDetails.SalesRepSSN;
        this.sSalesType = eDetails.SalesType;
        this.sPCPNumber = eDetails.PCPNumber;
        this.sPCPName= eDetails.PCPName;
        this.sSmokingIndicator = eDetails.SmokeIndicator;
        this.sESRDIndicator = eDetails.ESRDIndicator;
        this.sDisabledIndicator = eDetails.DisabledIndicator;
        this.sElectionTypeCd = eDetails.ElectionTypeCd;
        this.sSEPDate = eDetails.SEPDate;
        this.sSepReasonCode = eDetails.SepReasonCode;
        this.sPartAEligibleDate = eDetails.PartAEligibleDate;
        this.sPartBEligibleDate = eDetails.PartBEligibleDate;
        this.sFileDt = eDetails.FileDate;
        this.sProcDt = eDetails.ProcessedDate;
        this.sApplicationChannel = eDetails.ApplicationChannel;
        this.sOECConfirmationId = eDetails.OECConfirmationId;
        this.sCreationTs = eDetails.CreationTs;
        this.sBarCodeBase = eDetails.BarCodeBase;
        this.sRawOECYear = eDetails.PlanYear;
        this.sWebUniqueId = eDetails.ApplicationId;
        this.raceCd = eDetails.RaceCd;
        this.ethinicityCd = eDetails.EthinicityCd;
        
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
     /*
    * Method Name   :    HUMMemberSearchEnrollmentHelper constructor -TRRServiceRestResponse_DTO_HUM.TRRDetail
    * Description   :    Constructor for TRR Service Async
    * Return Type   :    NA
    * Parameters    :    TRRServiceRestResponse_DTO_HUM.TRRResponseDTO
    */
  
    public HUMMemberSearchEnrollmentHelper(TRRServiceRestResponse_DTO_HUM.TRRDetail TRREnroll,Integer TRRSearchCounter)
    {
           
        this.sMedicareClaim =  TRREnroll.MedicareId;                                
        this.sLastName= TRREnroll.LastName;           
        this.sFirstName= TRREnroll.FirstName;
        this.sDOB =TRREnroll.DateOfBirth;                                               
        this.sMiddleInitial=TRREnroll.MiddleInitial;                
        this.sGender =TRREnroll.GenderCode;
        this.sState =TRREnroll.StateCode;
        this.sCountyName =TRREnroll.CountyCode;
        this.sContract=TRREnroll.Contract;
        this.sPBP=TRREnroll.PBP;
        this.sEffectiveDate=TRREnroll.EffectiveDate;
        this.sEndDate=TRREnroll.EndDate;
        this.sReplyCode=TRREnroll.ReplyCode;
        this.sTransactionCode=TRREnroll.TransactionCode;
        this.sElectionTypeCode=TRREnroll.ElectionTypeCode;
        this.sDedupIndicator=TRREnroll.DedupIndicator;
        this.sUIInitialChangeFlag=TRREnroll.UIInitialChangeFlag;
        this.sUIOrganization=TRREnroll.UIOrganization;
        this.sErrorMessage=TRREnroll.ErrorMessage;
        this.sEnrollmentSourceCode=TRREnroll.EnrollmentSourceCode;
        this.sVariable=TRREnroll.Variable;
        this.sApplicationDate=TRREnroll.ApplicationDate;
        this.sSourceId=TRREnroll.SourceId;
        this.sPriorPBP=TRREnroll.PriorPBP;
        this.sHumanaReceivedDate=TRREnroll.HumanaReceivedDate;
        this.sTransactionDate=TRREnroll.TransactionDate;
        this.sCMSProcessingTimestamp=TRREnroll.CMSProcessingTimestamp;
        this.sApplicationDateIndicator=TRREnroll.ApplicationDateIndicator;
        this.sTRCShortName=TRREnroll.TRCShortName;
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
        this.sESRDIndicator=TRREnroll.ESRDIndicator;
        this.sPartDOptOutFlag=TRREnroll.PartDOptOutFlag;
        this.sOutOfAreaFlag=TRREnroll.OutOfAreaFlag;
        this.sPremiumWithholdOptionCode=TRREnroll.PremiumWithholdOptionCode;
        this.sPartCPremiumAmount=TRREnroll.PartCPremiumAmount;
        this.sPartDPremiumAmount=TRREnroll.PartDPremiumAmount;
        this.sMSPStatusFlag=TRREnroll.MSPStatusFlag;
        this.sWAIndicator=TRREnroll.WAIndicator;
        this.sEntitlementTypeCode=TRREnroll.EntitlementTypeCode;
        this.sInstitutionalIndicator=TRREnroll.InstitutionalIndicator;
        this.sDisabilityIndicator=TRREnroll.DisabilityIndicator;
        this.sHospiceIndicator=TRREnroll.HospiceIndicator;
        this.sDistrictOfficeCode=TRREnroll.DistrictOfficeCode;
        this.sDeMinimisDifferentialAmount=TRREnroll.DeMinimisDifferentialAmount;
        this.sPrevTROOPPBP=TRREnroll.PrevTROOPPBP;
        this.sSegmentNumber=TRREnroll.SegmentNumber;
        this.sSystemAssignedTransTrackingId=TRREnroll.SystemAssignedTransTrackingId;
        this.sPlanAssignedTransTrackingId=TRREnroll.PlanAssignedTransTrackingId;
        this.sDisenrollmentReasonCode=TRREnroll.DisenrollmentReasonCode;
        this.sFADemoOptOut=TRREnroll.FADemoOptOut;         
        String uniqueNumber = append + String.valueOf(TRRSearchCounter);
        uniqueNumber = uniqueNumber.substring(uniqueNumber.length() - 3);
        
        this.TRRresultdetails = 'TRR-'+uniqueNumber;
        //US 2098896-- TRR Detail change
        this.sPrefLangcode=TRREnroll.PrefLangcode; 
        this.sAccessibleFmtCode=TRREnroll.AccessibleFmtCode; 
        this.sSEPReasCode=TRREnroll.SEPReasCode; 
        //US - 4407442 -- Race and Ethinicity code
        this.raceCd = TRREnroll.RaceCd;
        this.ethinicityCd = TRREnroll.EthinicityCd; 
          
    }
     
            
    
    public ID iRecordID {get;set;}
    public String sFirstName {get;set;}
    public String sLastName {get;set;}
    public String sDOB {get;set;}
    public String sPlatform {get;set;}
    public String sGroupId {get;set;}
    public String sProduct {get;set;}
    public String sLOB {get;set;}
    public String sEffectiveDate {get;set;}
    public String sEndDate {get;set;}
    public String sPend {get;set;}
    public String sPendKey {get;set;}
    public String sDateTimePend {get;set;}
    public String sLegacyMemberJson {get;set;}
    public Id iAccntId {get;set;}
    public String sExternalID {get;set;}
    public string sExternalSystemType {get;set;}
    public string sState {get;set;}
    public string sExchangeID {get;set;}
    public string sZip {get;set;}
    public string sSolSecurity {get;set;}
    public string sGender {get;set;}
    public string sPermanentAddress {get;set;}
    public string sCountyName {get;set;}
    public string sCity {get;set;}
    public string sMailaddr {get;set;}
    public string sMailCity {get;set;}
    public string sMailSt {get;set;}
    public string sMailZip {get;set;}
    public string sHomePh {get;set;}
    public String sworkPh {get;set;}
    Public String sPymtOpt {get;set;}
    Public String sPymtOptType {get;set;}
    Public String sCsrEffDate {get;set;}
    Public String sCsrLevel{get;set;}
    Public String sCsrEndDate {get;set;}
    Public String sAptcAmount {get;set;}
    Public String sAptcEffDate {get;set;}
    Public String sAptcEndDate {get;set;}
    Public String sStrBsn{get;set;}
    Public String sCovEffDt{get;set;}
    Public String sMktRcptDate{get;set;}
    Public String sSignDATE{get;set;}
    public String sSalesRepName{get;set;}
    public string sSalesType{get;set;}
    Public String sFileDt{get;set;}
    Public String sProcDt{get;set;}
    Public String sCreationTs{get;set;}
    Public String sApplnChnl{get;set;}
    Public String sBarCodeBase{get;set;}
    Public String sOecConfId{get;set;}
    Public String sSmokingIndicator{get;set;}
    public boolean bdisplayInteractbutton {get;set;}
    Public String sAmPm {get; set;} // To set AM or PM
    Public String stimeStamp {get; set;} // To check for timestamp is null or not
    Public String sDate {get; set;} // To Split date & time and format date 
    Public String sTime {get; set;} // To Split date & time
    Public String sTimeFormat {get; set;}  // To format date 
    Public String sTimes {get; set;}  // To Get Hour
	
	public String SysAssnNumber {get;set;}
    Public String sMedicareId{get;set;} //
    public String sMedicaidId {get;set;}
    public string sMemberLangPreference{get;set;}
    public String sPOAFirstName {get;set;}
    public String sPOALastName {get;set;}
    public String sPOARelationType {get;set;}
    public String sPOAPhone {get;set;}
    Public String sContractId{get;set;}
    Public String sPlanBenefitPackage{get;set;}
    public String sOSBPackage {get;set;}
    public String sEmployerId {get;set;}
    public String sSignatureType {get;set;}
    public String sSignatureDate {get;set;}
    public String sSalesRepSSN {get;set;}
    public String sPCPNumber {get;set;}
    public String sPCPName {get;set;}
    public String sESRDIndicator {get;set;}
    public String sDisabledIndicator {get;set;}
    public String sElectionTypeCd {get;set;}
    public String sSEPDate {get;set;}
    public String sSepReasonCode {get;set;}
    public String sPartAEligibleDate {get;set;}
    public String sPartBEligibleDate {get;set;}
    Public String sBSN{get;set;}
    Public String sSegmentId{get;set;}
    Public String sApplicationChannel{get;set;}
    Public String sOECConfirmationId{get;set;}
    Public String sRawOECYear{get;set;}
    Public String sReceivedDate{get;set;}
    public String sWebUniqueId{get;set;}
    public String details{get;set;}
    public String application{get;set;}
	public String applicationURL{get;set;}
    public String append = '000';
    
    
    
    
    public string sMedicareClaim{Get; Set;}   
    public string sMiddleInitial {Get;Set;}    
    public string sReplyCode {Get;Set;}
    public string sTransactionCode {Get;Set;}
    public string sElectionTypeCode {Get;Set;}
    public string sDedupIndicator {Get;Set;}
    public string sUIInitialChangeFlag {Get;Set;}
    public string sUIOrganization {Get;Set;}
    public string sErrorMessage {Get;Set;}    
    public string sContract {Get;Set;}
    public string sPBP {Get;Set;}
    public string sEnrollmentSourceCode {Get;Set;}
    public string sVariable {Get;Set;}
    public string sApplicationDate {Get;Set;}
    public string sSourceId {Get;Set;}
    public string sPriorPBP {Get;Set;}
    public string sHumanaReceivedDate {Get;Set;}
    public string sTransactionDate {Get;Set;}
    public string sCMSProcessingTimestamp {Get;Set;}
    public string sApplicationDateIndicator {Get;Set;}
    public string sTRCShortName {Get;Set;}
    public string sCreditableCoverageFlag {Get;Set;}
    public string sCumulativeNumberofUncoveredMonths {Get;Set;}
    public string sSubmittedNumberofUncoveredMonths {Get;Set;}
    public string sPartDPenaltyAmount {Get;Set;}
    public string sPartDPenaltyWaivedAmount {Get;Set;}
    public string sPartDPenaltySubAmount {Get;set;}
    public string sPartDRxBIN {get;set;}
    public string sPartDRxPCN {get;set;}
    public string sPartDRxGroup {get;set;}
    public string sPartDRxId {get;set;}
    public string sLISPremiumLevel {get;set;}
    public string sLISCopayCategory {get;set;}
    public string sLISPeriodEffectiveDate {get;set;}
    public string sLISPremAmount {get;set;}
    public string sLISPeriodEndDate {get;set;}
    public string sLISSourceCode {get;set;}
    public string sLISEnrolleeTypeCode {get;set;}
    public string sSecondaryInsDrugFlag {get;set;}
    public string sSecondaryRxId {get;set;}
    public string sSecondaryRxGroup {get;set;}
    public string sSecondaryRxBIN {get;set;}
    public string sSecondaryRxPCN {get;set;}
    public string sEmployerSubsidyOverrideFlag {get;set;}
    public string sEGHPIndicator {get;set;} 
    public string sPartDOptOutFlag {get;set;}
    public string sOutOfAreaFlag {get;set;}
    public string sPremiumWithholdOptionCode {get;set;}
    public string sPartCPremiumAmount {get;set;}
    public string sPartDPremiumAmount {get;set;}
    public string sMSPStatusFlag {get;set;}
    public string sWAIndicator {get;set;}
    public string sEntitlementTypeCode {get;set;}
    public string sInstitutionalIndicator {get;set;}
    public string sDisabilityIndicator {get;set;}
    public string sHospiceIndicator {get;set;}
    public string sDistrictOfficeCode {get;set;}
    public string sDeMinimisDifferentialAmount {get;set;}
    public string sPrevTROOPPBP {get;set;}
    public string sSegmentNumber {get;set;}
    public string sSystemAssignedTransTrackingId {get;set;}
    public string sPlanAssignedTransTrackingId {get;set;}
    public string sDisenrollmentReasonCode {get;set;}
    public string sFADemoOptOut {get;set;}  
    public string TRRresultdetails {get;set;} 
    public string sCovTermDt {get;set;}
	//US 2098896-- TRR Detail change
    public string sPrefLangcode {get;set;}
    public string sAccessibleFmtCode {get;set;}
    public string sSEPReasCode {get;set;}
    public integer FirstNameSKey {get;set;} 

    public string raceCd {get;set;} 
    public string ethinicityCd {get;set;} 
    /*
    * Method Name : fetchValuesFromMap
    * Description : This function is used to get values from Map
    * Return type : String
    * Paramater   : String, Map<String,String>
    */
    public String fetchValuesFromMap( String sKey, Map<String,String> mapMemberInfo )
    {
        if(FirstNameSKey==null) FirstNameSKey = 0;
        if(sKey=='FirstName') FirstNameSKey++;
        String sReturnVal = '';
        if(mapMemberInfo.containsKey(sKey)) {sReturnVal = mapMemberInfo.get(sKey);  }
        return sReturnVal;
    }
    
    /*
    * Method Name : assignValues
    * Description : This function is used to assign values from Response based on Constructor from which it invoked
    * Return type : Void
    * Paramater   : String, String, String, String, String, String, String, String
    */
    public void assignValues( String firstName, String lastName, String DOB, String platform, string groupId, String LOB, String effectiveDate, String endDate ){
        this.sFirstName = firstName;
        this.sLastName = lastName;
        if(!String.isBlank(DOB))
        {
            this.sDOB = convertDate(String.valueOf(Date.valueOf(DOB)));
        }
        this.sPlatform = platform;
        this.sGroupId = groupId;
        this.sLOB = LOB;
        if(!String.isBlank(effectiveDate))
        {
            this.sEffectiveDate = convertDate(String.valueOf(Date.valueOf(effectiveDate)));
        }
        if(!String.isBlank(endDate))
        {
            this.sEndDate= convertDate(String.valueOf(Date.valueOf(endDate)));
        }
    }
    
    /*
    * Method Name : convertDate
    * Description : This function is used to convert Date to a specified format
    * Return type : String
    * Paramater   : String
    */
    public String convertDate( String sDate)
    {
        if(String.isNotBlank(sDate)){
            return sDate.split('-')[1]+'/'+sDate.split('-')[2]+'/'+sDate.split('-')[0];
        }else return '';
    }
    
    /*
    * Method Name : createAcc
    * Description : This function is used to create Account record to pass parameter to json serialise
    * Return type : Account
    * Paramater   : String
    */
    public Account createAcc( String AccExternalId )
    {
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
        
        Account accountRecord = new Account(FirstName = sfirstName, LastName = slastName ,
        Birthdate__c = this.sDOB , recordtypeid = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(), HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c),
        Source_Platform_Code__c = this.sPlatform,
        Group_Number__c = this.sGroupId,
        Account_External_ID__c = AccExternalId,
        Tenant_Id__c = system.label.HUMExtMemAccountTenantID,
        General_Account__c = true,
        Individual_First_Name__c = this.sFirstName,
        Individual_Last_Name__c = this.sLastName);
        return accountRecord;
    }
}