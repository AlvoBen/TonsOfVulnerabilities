/*****************************************************************************************
Apex class Name    : MemberIcons_S_HUM
Version            : 1.0
Function           : Service class for controller MemberIcons_C_HUM
Created Date       : 08/28/2019
Test Class         : MemberIcons_ST_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      08/28/2019         Original Version
 *   Santhi Mandava                                      09/17/2019         REQ - 409718 - Implemented medicare supplement icon logic
 *   Amar Gadewar                                        10/29/2019         REQ - 411303 Quality - 2020 New Member Indicator (CRM)
 *   Amar Gadewar                                        01/28/2020         USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
 *   Amit Agarwal                                        06/25/2020         User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *   Amar Gadewar                                        07/15/2020         DF-1086: Repeat Caller Icon is not shown on Member Search Page & Member Account page 
 *   Amar Gadewar                                        08/08/2020         DF-1336: Repeat Caller Attributes are displayed on Member Account Page for the User with inactive CRMS_240_GBO_Segment_Service_Access PS
 *   Amit Agarwal                                        08/19/2020         User Story 1368823: PR00091574 - MF 1 - Iconology - H1036 Icon logic update (CRM)
 *   Samantha Tennyson                                   09/08/2020         User Story 1449973: PR00091574 - MF 1 - Iconology - FIDE Icon (CRM)
 *   Atia Uzma                                           09/12/2020         User Story 1449938: PR00091574 - MF 1 - Iconology - HIDE Icon (CRM)
  *  Raja Sekhar Reddy M V                               11/13/2020         User Story 1660597: PR00096346 - MF 1 - DEV - New Member Indicator
 *   Samantha Tennyson                                   02/08/2021         Removing unused existing logic for Dual Eligible Icon
 *   Y Sunil Kumar Reddy                                 02/12/2021         User Story 1874757: PR00091574 -MF 1 - Quality - Relabel "Open Cases" Repeat Caller Attributes And Interaction Count Issue Fix (CRM) 
 *   Anjani Vaddadi                                      02/26/2021         User Story#-1260069 -T1PRJ0003372 - MF 1 - FR1.1 Veteran Member Icon
 *   Ajay Chakradhar                           		     05/05/2021	        REQ - 2184294 - Replacing constants with Consumer Management Constants
 *   Ajay Chakradhar                                     05/31/2021         REQ - 1755482 - Replacing constants to improve performance
 *   Atia Uzma                                           08/27/2021         User Story 2578189: T1PRJ0001827 - MF 1 - RESEARCH - Iconology - ID Card Request Icon (RF)
 *   Amar Gadewar                                        10/22/2021         User Story 2607022: T1PRJ0001827 - MF 1 - 2022 New Member Indicator (CRM)
 *   Atia Uzma                                           01/18/2022         User Story 2977635: T1PRJ0314829 - MF 1 - Iconology - LI NET Icon (RF)
 *   Anil Parvathaneni                                   07/11/2022         User Story 3315370: T1PRJ0527119 - MF 1 - Expand Population Criteria for Icon (CRM)
 *   Kiran Kumar B									 	 07/05/2022			User Story 3253058: T1PRJ0325139 - MF 1 - Iconology - IL MMP Icon (CRM)
 *   Anil Parvathaneni                                   11/08/2022         User Story 3501670: T1PR0325139 - FR1.02 - Medicaid (MCD) New Member Icon in CRM (Classic)
 *   Kinal Mangukiya                                     05/05/2023         User Story 4534222: T1PRJ0891339 - 2023 Arch Remediation - TECH - CRM Service Error Trending(SFDC)
 *   Nilesh Gadkar					                     06/30/2023	        User Story 4330043: T1PRJ0891339 - MF 1.1 - Limit Population Criteria for Veteran Icon (Classic)
 ***********************************************************************************************************************************************************/

Public with sharing Class MemberIcons_S_HUM
{
    private Static List<String> lstPTC = new List<String>{'MA','MAPD','PDP','MCD'}; 
    private Static List<String> lstCCSPDPPharmacyPilotPTC = new List<String>{'MAPD','PDP'};
  
    private Static List<String> lstGrpMedicarePTC = new List<String>{'MGP','MGR','MGF','MPD'};
    private Static List<MemberIconsWrapper_H_HUM> lstActiveAndFuturePolicies;
    private Static List<MemberIconsWrapper_H_HUM> lstTermedPolicies;
    private Static List<Policy_Member__c> lstPolicyMembers ;
    private Static map<String,Boolean> mapIconsStatus = new map<String,Boolean>{
        'GroupMedicare' => false,
        'MedicareSupplement' => false,
        'H1036Contract' => false,
        'HIDE' => false,
        'FIDE' => false,
        'IDCardRequest' => false,
        'LINET' => false
    };
    
    @testvisible private static Boolean bIsRecordsRetrieved = false;
    private static Boolean bIsPolicyRecordRetrieved = false;
    private static List<String> lstUserPermissions;
    Private Static String sMAJOR_LOB_MES = 'MES';
    Private Static String sPRODUCT_TYPE_MAPD = 'MAPD';
    Private Static String sPRODUCT_TYPE_MA = 'MA';
    Private Static String sHUM_BENEFITS_MED = 'MED';
    Private Static String sNONPHARMACY_USER_HUM = 'No';
    //ILMMP
    private Static Boolean bIsILMMPplan = false;
    private Static Boolean bIsnonILMMPplanActOrFutPM = false;
    private Static Boolean bIsILMMPActOrFutPolMem = false;
    
    /*
    * This method is used to fetch icons from custom setting object for a given page
    * @param  String - Page name
    * @return MemberIcons_DTO_HUM - Page icons
    */
    public Static MemberIcons_DTO_HUM getPageIcons(String sPageName)
    {
        Map<String,MemberIcon_HUM__c> mapMemberIcons = MemberIcon_HUM__c.getAll();
        List<MemberIcon_HUM__c> lstAllIcons = mapMemberIcons.values();
        List<MemberIcon_HUM__c> lstCurrentPageIconObj = new List<MemberIcon_HUM__c>();

        List<String> lstIconPermissions = new List<String>();
        
        for(Integer i = 0 ; i < lstAllIcons.size(); i++)
        {
            if(lstAllIcons[i].IsVisible__c && String.IsNotBlank(lstAllIcons[i].PageName__c))
            {
                lstAllIcons[i].PageName__c = lstAllIcons[i].PageName__c.replaceAll(', ',',');
                List<String> lstPageNames = lstAllIcons[i].PageName__c.split(',');
                
                If(lstPageNames.Contains(sPageName)) 
                {
                    lstCurrentPageIconObj.Add(lstAllIcons[i]);
                    if(String.IsNotBlank(lstAllIcons[i].PermissionSet__c))
                    {
                        lstIconPermissions.AddAll(lstAllIcons[i].PermissionSet__c.replaceAll(', ',',').split(','));
                    }
                }
            }
        }
        return validatePermissionsAndGetIconsList(lstCurrentPageIconObj,lstIconPermissions);
    } 

    /*
    * This method is used to check permission set and profile access for logged in user.
    * @param  List<MemberIcon_HUM__c> - List of icons
              List<String> -  List of configured permission sets
    * @return MemberIcons_DTO_HUM - Page icons
    */
    private static MemberIcons_DTO_HUM validatePermissionsAndGetIconsList(List<MemberIcon_HUM__c> lstCurrentPageIconObj,List<String> lstIconPermissions)
    {
        MemberIcons_DTO_HUM objIcons = new MemberIcons_DTO_HUM();
        objIcons.lstMemberIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
        List<PermissionSetAssignment> lstPermissionSets = MemberIcons_D_HUM.getUserPermissionSets(lstIconPermissions);
        lstUserPermissions = new List<String>();
        Boolean bHasPermissionSetAccess;
        Boolean bHasProfileAccess; 
        String sProfileName = [Select Name from Profile Where Id =: UserInfo.getProfileId()].Name; 
        map<String,Document> mapIconDocuments = getDocuments();
        for(PermissionSetAssignment objPermission : lstPermissionSets )
        {
            lstUserPermissions.Add(objPermission.PermissionSet.Name);
        }
        MemberIcons_DTO_HUM.Icon_DTO_HUM oIconObject ;
        for(MemberIcon_HUM__c oIcon : lstCurrentPageIconObj)
        {
            oIconObject = new MemberIcons_DTO_HUM.Icon_DTO_HUM();
            Boolean bPSCheckSkipped = false;
            Boolean bIsVisible = false;
            If(sProfileName == ConsumerManagementConstants_HUM.SYSTEM_ADMINISTRATOR_PROFILE_HUM || sProfileName == ConsumerManagementConstants_HUM.DEPLOYMENT_PROFILE_HUM)
            {
                bIsVisible = true;
            }
            else
            {
                if(!bPSCheckSkipped && checkProfileName(oIcon,sProfileName) && checkPermissionSets(oIcon))
                {
                    bIsVisible = true;
                }
            }
            if(bIsVisible)
            {
                Document objDoc = mapIconDocuments.get(oIcon.DocumentName__c);
                if(objDoc != Null) oIconObject.sDocumentId= objDoc.Id;
                oIconObject.sIconLabel = oIcon.DisplayText__c;
                oIconObject.bIconVisible = true;
                oIconObject.sIconName = oIcon.Name;
                oIconObject.sOrder = oIcon.Order__c;
                objIcons.lstMemberIcons.add(oIconObject);
            }
        }
        return objIcons;
    } 
    
    /*
    * This method is used to check profile access for logged in user.
    * @param  MemberIcon_HUM__c - Icon custom setting object
              String -  Loggedin user profile
    * @return Boolean 
    */
    private Static Boolean checkProfileName(MemberIcon_HUM__c oIcon,String sProfileName )
    {
        Boolean hasPermission = false;
        if(String.IsNotBlank(oIcon.Profile__c))
        {
            oIcon.Profile__c = oIcon.Profile__c.replaceAll(', ',',');
            List<String> lstProfiles = oIcon.Profile__c.split(',');
            If(lstProfiles.Contains(sProfileName))
            {
                hasPermission = true;
            }
        }
        else
        {
            hasPermission = true;
        }
        return hasPermission;
    }

    /*
    * This method is used to check permission set access for logged in user.
    * @param  MemberIcon_HUM__c - Icon custom setting object
    * @return Boolean 
    */
    private Static Boolean checkPermissionSets(MemberIcon_HUM__c oIcon)
    {
        Boolean hasPermission = false;
        if(String.IsNotBlank(oIcon.PermissionSet__c))
        {
            oIcon.PermissionSet__c = oIcon.PermissionSet__c.replaceAll(', ',',');
            List<String> lstPermissions = oIcon.PermissionSet__c.split(',');
            for(Integer i =0 ; i < lstPermissions.size(); i++)
            {
                If(String.IsNotBlank(lstPermissions[i]))
                {
                    IF(lstUserPermissions.Contains(lstPermissions[i]))
                    {
                        hasPermission = true;
                        break;
                    }
                }
            }
        }
        else
        {
            hasPermission = true;
        }
        return hasPermission;
    }

     /*
    * This method is used to get custom settings values and check permission set access for logged in user.
    * @return RepeatCallerSettings_HUM__c 
    */
    @TestVisible
    private static RepeatCallerSettings_HUM__c getRepeatCallerConfigDetailsForLoggedinUSer()
    {
        Map<String,RepeatCallerSettings_HUM__c> mapRC = RepeatCallerSettings_HUM__c.getAll();
        Map<String,RepeatCallerSettings_HUM__c> mapPSBasedRC = New Map<String,RepeatCallerSettings_HUM__c>();
        List<String> lstPermissionSetNames = new List<String>();
        RepeatCallerSettings_HUM__c objRC = null;
        String sProfileName = [Select Name from Profile Where Id =: UserInfo.getProfileId()].Name;
        For(RepeatCallerSettings_HUM__c objRCSetting : mapRC.values()) 
        {
            if(String.IsNotBlank(objRCSetting.PermissionSet__c) && objRCSetting.IsActive__c)
            {
                lstPermissionSetNames.Add(objRCSetting.PermissionSet__c);
                mapPSBasedRC.put(objRCSetting.PermissionSet__c, objRCSetting );
            }
        }
        if(sProfileName == 'Humana Pharmacy Specialist')
        {
            objRC = mapPSBasedRC.get('Humana Pharmacy Specialist');
        }
        else
        {
            List<PermissionSetAssignment> lstPermissionSets = MemberIcons_D_HUM.getUserPermissionSets(lstPermissionSetNames);
            if(lstPermissionSets != Null && !lstPermissionSets.IsEmpty())
            {
                if(lstPermissionSets.size() == 1) 
                objRC = mapPSBasedRC.get(lstPermissionSets[0].PermissionSet.Name);
            }
        }
        return objRC;
    }

    private Static List<String> splitAndTrim(String s1){
        List<String> sList = new List<String>();
        if(s1!=null){
            for(String str : s1.split(',')){
                sList.add(str.trim());   
            }
        }
        return sList;
    }
     /*
    * This method is used to get Repeat Caller Information to display RC attributes
    * @param  String - Account Id
              String- Current Interaction Id
              Boolean 
    * @return RepeatCallerInformation_DTO_HUM 
    */
    public static RepeatCallerInformation_DTO_HUM getRepeatCallerInformation(String sAccountId,String sInteractionId,Boolean bColorLogicExecutionRequired)
    {
        RepeatCallerSettings_HUM__c objRC = getRepeatCallerConfigDetailsForLoggedinUSer();
        RepeatCallerInformation_DTO_HUM objInteractionCounts = new RepeatCallerInformation_DTO_HUM();
        if(objRC != Null)
        {
            List<String> lstInteractionOrigin = splitAndTrim(objRC.Interaction_Origin__c);
            List<String> lstInteractionWithType = splitAndTrim(objRC.Interacting_With_type__c);
            DateTime dt14DaysDateBackDate = DateTime.Now().AddDays(-14);

            DateTime todaysStartDate = DateTime.newInstance(system.today().year(),system.today().month(),system.today().day(), 0,0,0);
            DateTime todaysEndDate = DateTime.newInstance(system.today().year(),system.today().month(),system.today().day(), 23,59,0);
            Integer iTodaysInteractionCount = 0;

            List<Account> lstAccounts = MemberIcons_D_HUM.getAccounts(sInteractionId,lstInteractionWithType,lstInteractionOrigin,dt14DaysDateBackDate,sAccountId);
            if(null != lstAccounts && !lstAccounts.isEmpty())
            {
                if(null != lstAccounts[0].Interaction_Members__r && !lstAccounts[0].Interaction_Members__r.isEmpty())
                {
                    For(integer i= 0; i<lstAccounts[0].Interaction_Members__r.size();i++)
                    {
                        if(lstAccounts[0].Interaction_Members__r[i].CreatedDate > = todaysStartDate && lstAccounts[0].Interaction_Members__r[i].CreatedDate <= todaysEndDate)
                        {
                            iTodaysInteractionCount = iTodaysInteractionCount + 1;
                        }
                    }
                    objInteractionCounts.iInteractionsCreatedToday = iTodaysInteractionCount;
                    objInteractionCounts.iInteractionsCreatedInPast14Days = lstAccounts[0].Interaction_Members__r.size();
                }
                if(lstAccounts[0].Cases != Null && !lstAccounts[0].Cases.isEmpty())
                {
                    objInteractionCounts.iOpenCases = lstAccounts[0].Cases.size();
                }
            }
            if(bColorLogicExecutionRequired)
            {
                objInteractionCounts = executeUIDisplayLogicForRepeatCaller(objInteractionCounts,objRC);
            }
        }
        return objInteractionCounts;
    }
  
    /*
    * This method is used to get Repeat Caller Color code Information to display RC attributes
    * @param  RepeatCallerInformation_DTO_HUM - DTO Object
              RepeatCallerSettings_HUM__c- Custom Settings Object
    * @return RepeatCallerInformation_DTO_HUM 
    */
    @TestVisible
    private static RepeatCallerInformation_DTO_HUM executeUIDisplayLogicForRepeatCaller(RepeatCallerInformation_DTO_HUM objInteractionCounts,RepeatCallerSettings_HUM__c objCS)
    {
        if(objCS != null)
        {
            if(objInteractionCounts != Null) 
            {
                if(objCS.IsTodaysIntsVisible__c)
                {
                    if(objInteractionCounts.iInteractionsCreatedToday <= Integer.valueOf(objCS.TodaysInts_Green_Range__c))
                    {
                        objInteractionCounts.sTodaysInteractionsColor = objCS.GreenColorCode__c;                        
                    }
                    else if(objInteractionCounts.iInteractionsCreatedToday > Integer.valueOf(objCS.TodaysInts_Green_Range__c) 
                    && objInteractionCounts.iInteractionsCreatedToday <= Integer.valueOf(objCS.TodaysInts_Amber_Range__c)){
                        objInteractionCounts.sTodaysInteractionsColor = objCS.AmberColorCode__c;
                    }
                    else if(objInteractionCounts.iInteractionsCreatedToday >= Integer.valueOf(objCS.TodaysInts_RedRange__c)){
                        objInteractionCounts.sTodaysInteractionsColor = objCS.RedColorCode__c;
                    }
                }

                if(objCS.Is14DaysIntsVisible__c)
                {
                    if(objInteractionCounts.iInteractionsCreatedInPast14Days <= Integer.valueOf(objCS.Past14DaysInts_Green_Range__c))
                    {
                        objInteractionCounts.sPast14DaysInteractionsColor = objCS.GreenColorCode__c;
                    }
                    else if(objInteractionCounts.iInteractionsCreatedInPast14Days > Integer.valueOf(objCS.Past14DaysInts_Green_Range__c)
                    && objInteractionCounts.iInteractionsCreatedInPast14Days <= Integer.valueOf(objCS.Past14DaysInts_Amber_Range__c))
                    {
                        objInteractionCounts.sPast14DaysInteractionsColor = objCS.AmberColorCode__c;
                    }
                    else if(objInteractionCounts.iInteractionsCreatedInPast14Days >= Integer.valueOf(objCS.Past14DaysInts_RedRange__c)){
                        objInteractionCounts.sPast14DaysInteractionsColor = objCS.RedColorCode__c;
                    }
                }

                if(objCS.IsOpenCasesVisible__c)
                {
                    if(objInteractionCounts.iOpenCases  <= Integer.valueOf(objCS.OpenCases_Green_Range__c))
                    {
                        objInteractionCounts.sOpenCasesColor = objCS.GreenColorCode__c;
                    }
                    else if(objInteractionCounts.iOpenCases > Integer.valueOf(objCS.OpenCases_Green_Range__c)
                    && objInteractionCounts.iOpenCases <= Integer.valueOf(objCS.OpenCases_Amber_Range__c)){
                        objInteractionCounts.sOpenCasesColor = objCS.AmberColorCode__c;
                    }
                    else if(objInteractionCounts.iOpenCases >= Integer.valueOf(objCS.OpenCases_RedRange__c)){
                        objInteractionCounts.sOpenCasesColor = objCS.RedColorCode__c;
                    }
                }
                
                objInteractionCounts.bIsTodaysIntsVisible = objCS.IsTodaysIntsVisible__c;
                objInteractionCounts.bIsPast14DaysIntsVisible = objCS.Is14DaysIntsVisible__c;
                objInteractionCounts.bIsOpenCasesVisible = objCS.IsOpenCasesVisible__c;
            }
        }
        return objInteractionCounts;
    }

     /*
    * This method is used to execute repeat caller icon logic
    * @param  String - Account Id
              String- Current Interaction Id
    * @return Boolean 
    */
    public static Boolean getRepeatCallerIconStatus(String sAccountId,String sInteractionId)
    {
            List<String> lstInteractionOrigin = new List<String>{'Inbound Call','Web Chat','Outbound Call'};
            List<String> lstInteractionWithType = new List<String>{'Member','Member Representative or Caregiver'};
            DateTime dt14DaysDateBackDate = DateTime.Now().AddDays(-14);
            Boolean bShowRepeatCallerIcon = false;
            try{
              List<Account> lstAccounts = MemberIcons_D_HUM.getAccountsforRCIcon(sInteractionId,lstInteractionWithType,lstInteractionOrigin,dt14DaysDateBackDate,sAccountId);
              if(null != lstAccounts && !lstAccounts.isEmpty())
               {                
                  if(null != lstAccounts[0].Interaction_Members__r  && !lstAccounts[0].Interaction_Members__r.isEmpty())
                   {
                     if(lstAccounts[0].Interaction_Members__r.size() >= 3)
                     bShowRepeatCallerIcon = true;
                   }
               }
            }
            catch(Exception ex){
                HUMExceptionHelper.logErrors(ex, 'MemberIcons_S_HUM', 'getRepeatCallerIconStatus');
              }
            return bShowRepeatCallerIcon;
    }
    /*
    * This method is used to execute new member logic
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getNewMemberIconStatus(String sAccountId)
    {
        Date newMemberStartDate = Date.newinstance(2023, 1, 1);
        Date newMemberEndDate = Date.newinstance(2023, 12, 31);
        Date dateYearEarlierNewMemberStartDate = newMemberStartDate.addYears(-1);
        
        Boolean bShowIndicator = false;
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        
        if(lstActiveAndFuturePolicies != null && !lstActiveAndFuturePolicies.IsEmpty())
        {
            List<MemberIconsWrapper_H_HUM> newMemberList = new List<MemberIconsWrapper_H_HUM>();
            
            for (MemberIconsWrapper_H_HUM activeFuturePol : lstActiveAndFuturePolicies) {
                if (activeFuturePol.dtEffectiveDate >= newMemberStartDate && activeFuturePol.dtEffectiveDate <= newMemberEndDate)
                {
                    newMemberList.add(activeFuturePol);
                }
                else if (activeFuturePol.dtEffectiveDate < newMemberStartDate && activeFuturePol.sPolicyMemberPlan != 'PDP')
                {
                   
                    lstTermedPolicies.add(activeFuturePol);
                }
            }

            if (newMemberList != null && !newMemberList.isEmpty())
            {   newMemberList.sort();
                Date dtEarliestNewMemberEffectiveDate = newMemberList.get(newMemberList.size() - 1).dtEffectiveDate;
                Date dateYearLaterOfNewMemberEffecDate = dtEarliestNewMemberEffectiveDate.addYears(1);
                dateYearEarlierNewMemberStartDate =  dtEarliestNewMemberEffectiveDate.addYears(-1);
                Integer iDaysBetweenToShowIcon = System.today().daysBetween(dateYearLaterOfNewMemberEffecDate);
                bShowIndicator = iDaysBetweenToShowIcon > 0 ? true : false;
            }
            if (bShowIndicator && lstTermedPolicies != null && !lstTermedPolicies.isEmpty()) 
            {   
                Boolean bPharmacyuser = Pharmacy_H_HUM.isPharmacyUser() == sNONPHARMACY_USER_HUM ? false : true;
                Boolean bCCSPDPPharmacyPilot = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
                for (MemberIconsWrapper_H_HUM termOrOldPol : lstTermedPolicies) { 
                    if (termOrOldPol.dtEndDate >= dateYearEarlierNewMemberStartDate 
                        || !(termOrOldPol.sPolicyMemberStatus.equals(ConsumerManagementConstants_HUM.POLICYSTATUS_TERMED)) ) {

                            bShowIndicator = false;
                            if (bPharmacyuser || bCCSPDPPharmacyPilot){
                                for (MemberIconsWrapper_H_HUM newMemberPolicy: newMemberList) {
                                    if (lstCCSPDPPharmacyPilotPTC.contains(newMemberPolicy.sPolicyMemberPlan) 
                                            && termOrOldPol.sPolicyMemberPlan.equalsIgnoreCase(sPRODUCT_TYPE_MA))
                                    {
                                        bShowIndicator = true;
                                        break;
                                    }
                                }
                            }
                    }
                    if (!bShowIndicator) break;
                }
            }
        }
        return bShowIndicator;
    }


    
    /*
    * This method is used to execute medicare group member logic at account level
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getGroupMedicareIconStatusForAccount(String sAccountId)
    {
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        return  mapIconsStatus.get('GroupMedicare'); 
    }
    
    /*
    * This method is used to execute medicare group member logic at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getGroupMedicareIconStatusForPolMember(String sPolicyMemId)
    {
        
        if(!bIsPolicyRecordRetrieved) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('GroupMedicare'); 
    }
    
     /*
    * This method is used to execute medicare supplement logic at account level
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getMedicareSupplementIconStatusForAccount(String sAccountId)
    {
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        return  mapIconsStatus.get('MedicareSupplement');
    }
    
    /*
    * This method is used to execute medicare supplement logic at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getMedicareSupplementIconStatusForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('MedicareSupplement');
    }


     /*
    * This method is used to execute check on H1036 Policy i.e. Contract ID = H1036 on Policy Member associated at account level
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getH1036ContractIconForAccount(String sAccountId)
    {
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        return  mapIconsStatus.get('H1036Contract'); 
    }
    
    /*
    * This method is used to execute check on H1036 Policy i.e. Contract ID = H1036 on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getH1036ContractIconForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('H1036Contract'); 
    }

    /*
    * This method is used to execute check on IL MMP Policy at Account Level
    * @param  String - Account Id
    * @return Boolean 
    */
    
    public static Boolean getILMMPIconForAccount(String sAccountId)
    {   
        String sILMMPStatus = '';
        Set<String> setPolicies = new Set<String>();
        Integer iCount = 0;
        if(!bIsRecordsRetrieved){
            getMemberRecords(sAccountId);
        }

        for(Policy_Member__c oPolicyMember: lstPolicyMembers)
        {
            if(oPolicyMember.Product__c == 'MED' && oPolicyMember.Product_Type__c == 'MER' && oPolicyMember.Product_Type_Code__c == 'MAPD' && 
                oPolicyMember.Policy__r.Contract_Number__c == 'H0336' && oPolicyMember.Policy__r.PBP_Code__c == '001' && oPolicyMember.Policy__r.Medicare_Segment_ID__c == '000' && oPolicyMember.Policy__r.Issue_State__c == 'IL') {
        
                for(Integer i = 0; i < lstPolicyMembers.size(); i++) {            
                    if(iCount == i) {
                        continue;
                    }
                    Policy_Member__c oPM = lstPolicyMembers.get(i);
                    if(oPM.Product__c == 'MED' && oPM.Product_Type__c == 'MCD' && String.isBlank(oPM.Product_Type_Code__c) && oPM.Policy__r.Issue_State__c== 'IL' 
                        && oPolicyMember.status__c == oPM.status__c && oPolicyMember.Effective_Date__c == oPM.Effective_Date__c && oPolicyMember.End_Date__c == oPM.End_Date__c) {
                        sILMMPStatus = oPolicyMember.Status__c;
                        setPolicies.add(oPolicyMember.id);
                        setPolicies.add(oPM.id);
                        break;
                    }
                }
            }
            if(String.isNotBlank(sILMMPStatus)) {
                break;
            }
            iCount++;
        }
        if(String.isNotBlank(sILMMPStatus) && sILMMPStatus.equalsIgnoreCase('Termed')) {
            for(Policy_Member__c oPolicyMember : lstPolicyMembers) {
                if(!setPolicies.contains(oPolicyMember.id) && oPolicyMember.Status__c != 'Termed') {
                    sILMMPStatus = '';
                    break;
                }
            }
        }
        if(String.isNotBlank(sILMMPStatus)){
            mapIconsStatus.put('IL_MMP',true);
        }else{
            mapIconsStatus.put('IL_MMP',false);
        }
        return  mapIconsStatus.get('IL_MMP'); 
    }
   
    /*
    * This method is used to execute check on IL MMP Policy on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    
    public static Boolean getILMMPIconForPolMember(String sPolicyMemId)
    {
     if(!bIsRecordsRetrieved) { 
         getPolicyMemberRecord(sPolicyMemId);
     }
        return  mapIconsStatus.get('IL_MMP'); 
    }


    /*
    * This method is used to execute check on LI NET Policy at Account Level
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getLINETIconForAccount(String sAccountId)
    {
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        return  mapIconsStatus.get('LINET'); 
    }
    
    /*
    * This method is used to execute check on LI NET Policy on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getLINETIconForPolMember(String sPolicyMemId,String sStatus)
    {
        List<String> lststatus = new List<String>{'Active','Future','Termed'};
        
        if(!bIsPolicyRecordRetrieved && lststatus.contains(sStatus)) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('LINET'); 
    }
    
    
    /*
    * This method is used to execute check on FIDE Policy at Account Level
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getFIDEIconForAccount(String sAccountId)
    {
        
        if(!bIsRecordsRetrieved){
            getMemberRecords(sAccountId);
        }
        
        for(Policy_Member__c oPolicyMember: lstPolicyMembers)
        {
            if(oPolicyMember.Status__c == 'Termed')
            {
                checkAtPolicyMemberLevelFIDEIcon(oPolicyMember);
            }

        }

        return  mapIconsStatus.get('FIDE'); 
    }
    
    /*
    * This method is used to execute check on FIDE Policy on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getFIDEIconForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('FIDE'); 
    }
    
    /* 
    * This method is used to execute check on HIDE Policy at Account Level
    * @param  String - Account Id
    * @return Boolean
    */
    public static Boolean getHIDEIconForAccount(String sAccountId)
    {
        
        if(!bIsRecordsRetrieved){
            getMemberRecords(sAccountId);
        }
        
        for(Policy_Member__c oPolicyMember: lstPolicyMembers)
        {
            if(oPolicyMember.Status__c == 'Termed')
            {
                    checkAtPolicyMemberLevelHIDEIcon(oPolicyMember);
            }

        }

        return  mapIconsStatus.get('HIDE');
    }
    
    /*
    * This method is used to execute check on HIDE Policy on Policy Member at policy member level
    * @param  String - Policy Member Id
    * @return Boolean
    */
    public static Boolean getHIDEIconForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('HIDE');
    }
    
    
    /*
    * This method is used to execute birthday logic
    * @param  String - Birthdate
    * @return Boolean 
    */
    public static Boolean getBirthdayIconStatus(String sBirthDate)
    {
        Boolean bShowIndicator = false;
        Date dBirthDate;
        Date dPastDate;
        Date dFutureDate;
        
        if(String.IsNotBlank(sBirthDate))
        {
            try
            {
                List<String> lstVals = sBirthDate.split('/');
                sBirthDate = lstVals[0]+'/'+lstVals[1]+'/' + Date.Today().year();
                dBirthDate = Date.parse(sBirthDate);
                dPastDate = dBirthDate.addDays(-14);
                dFutureDate = dBirthDate.addDays(7);
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
            }
            catch(Exception Ex)
            {
                List<String> lstStr = sBirthDate.split('/');
                dBirthDate = Date.newInstance(Integer.valueOf(Date.Today().year()), Integer.valueOf(lstStr.get(0)), Integer.valueOf(lstStr.get(1)));
                dPastDate = dBirthDate.addDays(-14);
                dFutureDate = dBirthDate.addDays(7);
            }
            if(Date.Today() >= dPastDate  && Date.Today() <= dFutureDate )
            {
                bShowIndicator = true;
            }
        }
        return bShowIndicator;
    }
       
    /*
    * This method is used to execute deceased member logic
    * @param  String - Deceased Date
    * @return Boolean 
    */
    public static Boolean getDeceasedIconStatus(String sDeceasedDate)
    {
        Boolean bShowIndicator = false;
        if(String.IsNotBlank(sDeceasedDate))
        {
            bShowIndicator = true;
        }
        return bShowIndicator;
    }
    
    /*
    * This method is used to retrieve policy member single record
    * @param  String - Policy Member  Id
    * @return void 
    */
    private static void getPolicyMemberRecord(Id sPolMemId)
    {
        List<Policy_Member__c> lstPolicyMembers = MemberIcons_D_HUM.getPolicyMemberDetails(sPolMemId);
        if(lstPolicyMembers != Null && !lstPolicyMembers.isEmpty())
        {
            checkAtPolicyMemberLevel(lstPolicyMembers[0]);
        }
        bIsPolicyRecordRetrieved = true;
    }

    /*
    * This method is used to retrieve policy membe rrecords
    * @param  String - Account Id
    * @return void 
    */
    private static void getMemberRecords(String sAccountId)
    {
        lstPolicyMembers = MemberIcons_D_HUM.getPolicyMemberRecords(sAccountId);
        segregateNewMemberAndGroupMemberRecords(lstPolicyMembers);
    }
    
    /*
    * This method is used to segregate policy member records based on different conditions
    * @param  List<Policy_Member__c>
    * @return void
    */
    private static void segregateNewMemberAndGroupMemberRecords(List<Policy_Member__c> lstPolMemRecords)
    {
        lstTermedPolicies = new List<MemberIconsWrapper_H_HUM>();
        lstActiveAndFuturePolicies = new List<MemberIconsWrapper_H_HUM>();
        
        for(Policy_Member__c oPolicyMember: lstPolMemRecords)
        {
             string productType = oPolicyMember.Policy__r.Product__r.Product_Type__c==NULL?oPolicyMember.Product_Type__c:oPolicyMember.Policy__r.Product__r.Product_Type__c;
           
           if(oPolicyMember.Effective_Date__c != null && oPolicyMember.Product__c == sHUM_BENEFITS_MED && lstPTC.Contains(productType))
           {
                if(oPolicyMember.Status__c == 'Termed') 
                {
                    lstTermedPolicies.add(new MemberIconsWrapper_H_HUM(oPolicyMember));
                } 
                else 
                {
                    lstActiveAndFuturePolicies.add(new MemberIconsWrapper_H_HUM(oPolicyMember));
                    
                }
           }
           if(oPolicyMember.Status__c == 'Active' || oPolicyMember.Status__c == 'Future')
           {
               checkAtPolicyMemberLevel(oPolicyMember);
           }
        }
        bIsRecordsRetrieved = true;
    }
    
    /*
    * This method is used to check at Policy Member for a Particular record about various values on Policy Member to display a Icon
    * @param  Policy_Member__c
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevel(Policy_Member__c oPolicyMember)
    {
        if(lstGrpMedicarePTC.Contains(oPolicyMember.Product_Type__c))
        {
            mapIconsStatus.put('GroupMedicare',true);
        }
        else if(oPolicyMember.Product_Type__c == sMAJOR_LOB_MES)
        {
            mapIconsStatus.put('MedicareSupplement',true);
        }
        if(oPolicyMember.Policy__r.Contract_Number__c == 'H1036' && oPolicyMember.Policy__r.Issue_State__c == 'FL')
        {
            mapIconsStatus.put('H1036Contract',true);
        }
      
        checkAtPolicyMemberLevelFIDEIcon(oPolicyMember);
        checkAtPolicyMemberLevelHIDEIcon(oPolicyMember);
        checkAtPolicyMemberLevelIDCardIconActionalble(oPolicyMember);
        checkAtPolicyMemberLevelLINETIcon(oPolicyMember);
        checkAtPolicyMemberLevelILMMPIcon(oPolicyMember);
    }

    /*
    * This method is used to check at Policy Member for a Particular record if FIDE ICON has to be displayed
    * @param  Policy_Member__c
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelFIDEIcon(Policy_Member__c oPolicyMember)
    {
        List<String> lstPBPCodeFIDE = new List<String>{'280','281','282','283'};

        if(String.isNotBlank(oPolicyMember.Policy__r.Contract_Number__c) &&  'H1036' == oPolicyMember.Policy__r.Contract_Number__c 
            && String.isNotBlank(oPolicyMember.Policy__r.Issue_State__c) &&  'FL' == oPolicyMember.Policy__r.Issue_State__c
            && String.isNotBlank(oPolicyMember.Policy__r.Medicare_Segment_ID__c) &&'000' == oPolicyMember.Policy__r.Medicare_Segment_ID__c 
            && String.isNotBlank(oPolicyMember.Policy__r.PBP_Code__c) &&  lstPBPCodeFIDE.contains(oPolicyMember.Policy__r.PBP_Code__c))
    {
            mapIconsStatus.put('FIDE',true);
        }
    }

    /*
    * This method is used to check at Policy Member for a Particular record if LI NET ICON has to be displayed
    * @param  Policy_Member__c
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelLINETIcon(Policy_Member__c oPolicyMember)
    {	
        if(oPolicyMember.Policy__r.Contract_Number__c == 'X0001')
        {	
            mapIconsStatus.put('LINET',true);
        }
    }
    
    /*
    * This method is used to check at Policy Member for a Particular record if IL MMP ICON has to be displayed
    * @param  Policy_Member__c
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelILMMPIconVny(List<Policy_Member__c> lstPolicyMembers){
        for(Policy_Member__c oPolicyMember: lstPolicyMembers){
            mapIconsStatus.put('IL_MMP',true);
        }
           
    }



    @testvisible private Static Void checkAtPolicyMemberLevelILMMPIcon(Policy_Member__c oPolicyMember)
    //@testvisible private Static Boolean checkAtPolicyMemberLevelILMMPIcon(Policy_Member__c oPolicyMember)
    {
     String sQuery = '';
     List<Policy_Member__c> pm = new list<Policy_Member__c>();
     if(oPolicyMember.Product__c == 'MED' && oPolicyMember.Product_Type__c == 'MER' && oPolicyMember.Product_Type_Code__c == 'MAPD' && 
        oPolicyMember.Policy__r.Contract_Number__c == 'H0336' && oPolicyMember.Policy__r.PBP_Code__c == '001' && oPolicyMember.Policy__r.Medicare_Segment_ID__c == '000' && oPolicyMember.Policy__r.Issue_State__c == 'IL') {
             pm =[Select id ,Name
                FROM Policy_Member__c WHERE Name = :oPolicyMember.name AND Product__c = 'MED'   AND Product_Type__c = 'MCD'  AND Product_Type_Code__c = null  AND Policy__r.Issue_State__c= 'IL'
                AND status__c = :oPolicyMember.status__c  AND Effective_Date__c = :oPolicyMember.Effective_Date__c   AND End_Date__c = :oPolicyMember.End_Date__c];

     }else if(oPolicyMember.Product__c == 'MED' && oPolicyMember.Product_Type__c == 'MCD' && String.isBlank(oPolicyMember.Product_Type_Code__c) && oPolicyMember.Policy__r.Issue_State__c== 'IL' ) {
        pm =[Select id ,Name
                FROM Policy_Member__c WHERE Name = :oPolicyMember.name AND Product__c = 'MED' AND Product_Type__c = 'MER' AND Product_Type_Code__c = 'MAPD'
                AND Policy__r.Issue_State__c= 'IL' AND Policy__r.Contract_Number__c = 'H0336' AND Policy__r.PBP_Code__c = '001'
                AND Policy__r.Medicare_Segment_ID__c = '000' AND status__c = :oPolicyMember.status__c AND Effective_Date__c = :oPolicyMember.Effective_Date__c  AND End_Date__c = :oPolicyMember.End_Date__c];
     }
     
     if (!pm.isEmpty()){
        mapIconsStatus.put('IL_MMP',true);
     }else {
        mapIconsStatus.put('IL_MMP',false);
     }
     
    }

    /*
    * This method is used to check at Policy Member for a Particular record if HIDE ICON has to be displayed
    * @param  Policy_Member__c
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelHIDEIcon(Policy_Member__c oPolicyMember)
    {
        List<String> lstContractNumberHIDE = new List<String>{'H1036','H7284'};
        List<String> lstPBPCodeHIDE = new List<String>{'210','214','226','261','285','003'};
        
        if(String.isNotBlank(oPolicyMember.Policy__r.Contract_Number__c) &&  lstContractNumberHIDE.contains(oPolicyMember.Policy__r.Contract_Number__c)
            && String.isNotBlank(oPolicyMember.Policy__r.Issue_State__c) &&  'FL' == oPolicyMember.Policy__r.Issue_State__c
            && String.isNotBlank(oPolicyMember.Policy__r.Medicare_Segment_ID__c) && '000' == oPolicyMember.Policy__r.Medicare_Segment_ID__c
            && String.isNotBlank(oPolicyMember.Policy__r.PBP_Code__c) &&  lstPBPCodeHIDE.contains(oPolicyMember.Policy__r.PBP_Code__c))
        {
            mapIconsStatus.put('HIDE',true);
        }

    }


    /*
    * This method is used to check at Account record for a Particular record if Veteran ICON has to be displayed
    * @param  Account Id
    * @return Boolean
    */
    public static boolean getVeteranIconStatus(String sAccountId){       
        map<string,Product_Type_For_Veteran_Icon_Display__c> mapProductType = Product_Type_For_Veteran_Icon_Display__c.getAll();
        Boolean bVeteran = false;
         Boolean bVeteraniconSwitch = false;
      bVeteraniconSwitch = HUMUtilityHelper.isCRMFunctionalityOn('3315370');
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        if(lstPolicyMembers != NULL && !lstPolicyMembers.isEmpty()){
            for(Policy_Member__c oPolMember :lstPolicyMembers){
                if(oPolMember.Effective_Date__c != oPolMember.End_Date__c){
                    if(mapProductType ==null || mapProductType.isEmpty()){ 
                       bVeteran = true;
                       
                    }
                    /*else{
                       bVeteran =  mapProductType.containsKey(oPolMember.Product_Type__c)?true :false; 
                    }  */ 
                    else if(mapProductType.containsKey(oPolMember.Product_Type_Code__c) && !bVeteraniconSwitch){
                        bVeteran = true;
                    }
                    else if(mapProductType.containsKey(oPolMember.Product_Type__c) && bVeteraniconSwitch){
                        bVeteran = true;
                    }                 
                }			
                if(bVeteran) break;
                }
            }
                
        return bVeteran;
        }  

    /*
    * This method is used retrieve icon document records.
    * @param  NA
    * @return map<String,Document>
    */
    public static map<String,Document> getDocuments()
    {
        List<Document> lstDocs = MemberIcons_D_HUM.getDocuments();
        map<String,Document> mapIconDocuments = new map<String,Document>();
        For(Document objDoc : lstDocs )
        {
            mapIconDocuments.put(objDoc.Name,objDoc);
        }
        return mapIconDocuments;
    }
    
    /*
    * This method is used sort icons based on configured order.
    * @param  List<MemberIcons_DTO_HUM.Icon_DTO_HUM> - List of icons
    * @return List<MemberIcons_DTO_HUM.Icon_DTO_HUM> - Sorted list of icons
    */
    public static List<MemberIcons_DTO_HUM.Icon_DTO_HUM> sortIcons(List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstIcons)
    {
        for(Integer i = 0; i < lstIcons.size(); i++)
        {
            for(integer j = i+1; j <= lstIcons.size()-1; j++)
            {
                MemberIcons_DTO_HUM.Icon_DTO_HUM objIcon;
                if(String.IsNotBlank(lstIcons[i].sOrder) && String.IsNotBlank(lstIcons[j].sOrder))
                {
                    if(Integer.ValueOf(lstIcons[i].sOrder) > Integer.ValueOf(lstIcons[j].sOrder))
                    {
                        objIcon = lstIcons[i];
                        lstIcons[i] = lstIcons[j];
                        lstIcons[j]= objIcon;
                    }
               }
            }
        }
        return lstIcons;
    }
    
    /*
    * This method is used to check at Policy Member for a Particular record if IDCardRequest Icon has to be displayed
    * @param  Policy_Member__c Id
    * @return Boolean
    */
    public Static Boolean getIDCardIconForPolMember(String sPolMemId)
    {
        Boolean bIDCardRequest = false;
        List<Case> lstCases = MemberIcons_D_HUM.getCaseforIDCardIcon(sPolMemId);        
        if(lstCases != Null && !lstCases.isEmpty())
        {
            bIDCardRequest = true;    
        } 
        return  bIDCardRequest;        
    }
    
    
    /*
    * This method is used to check at Policy Member for a Particular record if IDCardRequest Icon is actionable 
    * @param  Policy_Member__c 
    * @return Boolean
    */
    public Static Boolean checkIDCardIconActionalble(String sPolicyMemId){
        if(!bIsPolicyRecordRetrieved) getPolicyMemberRecord(sPolicyMemId);
        return  mapIconsStatus.get('IDCardRequest'); 
    }
    
    /*
    * This method is used to check at Policy Member for a Particular record if IDCardRequest ICON is actionable
    * @param  Policy_Member__c 
    * @return void
    */
    @testvisible private static void checkAtPolicyMemberLevelIDCardIconActionalble(Policy_Member__c oPolicyMember)
    {
        List<String> lstProduct = new List<String>{'VIT', 'FSA', 'HLT', 'HWS' , 'LIF', 'STI', 'VIS'};
        List<String> lstPlatform = new List<String>{'CB' , 'CS' , 'FM' , 'PR'};
            
        if(String.isNotBlank(oPolicyMember.Product__c) &&  !(lstProduct.contains(oPolicyMember.Product__c)) && String.isNotBlank(oPolicyMember.Policy_Platform__c) && !(lstPlatform.contains(oPolicyMember.Policy_Platform__c)))
        {
            mapIconsStatus.put('IDCardRequest',true);
        }

    }
    

}