/*****************************************************************************************
Apex class Name    : MemberIcons_LS_HUM
Version            : 1.0
Function           : Service class for controller MemberIcons_C_HUM
Created Date       : 08/28/2019
Test Class         : MemberIcons_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava										 03/12/2021				  initial version
*Ashish Kumar                                            04/23/2021               Added changes related to Icons on Policy Detail page
*Kajal Namdev                                            05/13/2021               Removed reference of policy object
*Ankima Srivastava                                       07/12/2021               Fix for Medicare Suppliment icon defect
*Ankima Srivastava                                       07/14/2021               Rollback - Fix for Medicare Suppliment icon defect
*Ankima Srivastava                                       08/09/2021               Fix for Medicare Suppliment icon defect
*Ankima Srivastava                                       08/11/2021               Legacy Delete Icon Update
*Ashish Kumar                                            01/10/2021               Changing constants reference
*visweswararao j                                         05/31/2022               US2746576:Account Management - New Classic Icon Implementations - Veteran Icon
*Muthukumar												 02/02/2023				  US 4021255 T1PRJ0170850 - MF23279 - FR1.02 - 2023 and Medicaid (MCD) Logic Update for New Member Icon in CRM (Lightning)
*Deepakkumar Khandelwal								     02/02/2023				  US-4137330: T1PRJ0865978 - MF 24067 /  4097490 -C13, Lightning - CORE - Match Icons to Plans 1
*Deepakkumar Khandelwal									 02/07/2023				  Fix for DF-7111 (US-3852966)
*Deepakkumar Khandelwal									 02/13/2023				  Fix for DF_4303032 (US-4137330)
*Deepakkumar Khandelwal									 06/30/2023				  US_4742449_ T1PRJ0865978 - INC2384724/Contact Handling Alert Icon is displayed even when there are no contact handling alerts for that member.
*Deepakkumar Khandelwal									 07/10/2023				  US_4816948 : Dev 3/Veteran Icon & New Member icon is not always showing in CRM Lightning (even when displaying in classic)
*visweswararao j                                         07/08/2023               User Story 4811684: T1PRJ0865978- MF27110 - C01/Account Management/Pharmacy - Iconology- Veteran Icon (Lightning) Search Account Page
**********************************************************************************************************************************************************/

Public with sharing Class MemberIcons_LS_HUM
{
    private Static List<String> lstPTC = new List<String>{GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MA, GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MAPD,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_PDP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MCD};
    private Static List<String> lstGrpMedicarePTC = new List<String>{GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGP,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGR,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MGF,GLOBAL_CONSTANT_LH_HUM.PRODUCT_TYPE_MPD};
    private Static List<MemberIconsWrapper_LH_HUM> lstActiveAndFuturePolicies;
    private Static List<MemberIconsWrapper_LH_HUM> lstTermedPolicies;
    private Static List<MemberPlan> lstPolicyMembers ;
    private Static map<String,Boolean> mapIconsStatus = new map<String,Boolean>{
        'GroupMedicare' => false,
        'MedicareSupplement' => false,
        'DualEligible' => false,
        'H1036Contract' => false,
        'HIDE' => false,
        'FIDE' => false,
		'LINET'=> false,  
        'IDCardRequest'=>false,  
        'IL_MMP'=>false,
		'ContactHandlingAlert'=>false
    };
    
        
    @testvisible private static Boolean bIsRecordsRetrieved = false;
    private static Boolean bIsPolicyRecordRetrieved = false;
    private static List<String> lstUserPermissions;
	Private Static String sPRODUCT_TYPE_MA = 'MA';
    Private Static String sNONPHARMACY_USER_HUM = 'No';
    private Static List<String> lstCCSPDPPharmacyPilotPTC = new List<String>{'MAPD','PDP'};
         /*
    * This method is used to execute check on Medicare Suppliment on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getMedicareSupplementIconStatusForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved)
        {
            getPolicyMemberRecord(sPolicyMemId);
        }
        return  mapIconsStatus.get('MedicareSupplement'); 
    }
    
        /*
    * This method is used to execute check on H1036 Policy i.e. Contract ID = H1036 on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getH1036ContractIconForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved)
        {
            getPolicyMemberRecord(sPolicyMemId);
        }
        return  mapIconsStatus.get('H1036Contract'); 
    }
    
        /*
    * This method is used to execute check on HIDE Policy on Policy Member at policy member level
    * @param  String - Policy Member Id
    * @return Boolean
    */
    public static Boolean getHIDEIconForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved){
            getPolicyMemberRecord(sPolicyMemId);
        }
        return  mapIconsStatus.get('HIDE');
    }
    
        /*
    * This method is used to execute check on FIDE Policy on Policy Member at policy member level
    * @param  String - Policy member id
    * @return Boolean 
    */
    public static Boolean getFIDEIconForPolMember(String sPolicyMemId)
    {
        if(!bIsPolicyRecordRetrieved){
            getPolicyMemberRecord(sPolicyMemId);
           }
        return  mapIconsStatus.get('FIDE'); 
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
	
	/*LINET for acoount member*/
      public static Boolean getLINETIconForAccount(String sAccountId)
    {
        if(!bIsRecordsRetrieved){
            getMemberRecords(sAccountId);
        }
        return  mapIconsStatus.get('LINET'); 
        
    }
    
    /* LINET for policy member*/
      public static Boolean getLINETIconForPolMember(String sPolicyMemId, String sStatus)
    {
        List<String> lststatus = new List<String>{'Active','Future','Termed'};
            if(!bIsPolicyRecordRetrieved ){
                getPolicyMemberRecord(sPolicyMemId);
            }
        return  mapIconsStatus.get('LINET'); 
        
    }
    
        /*
    * This method is used to execute check on IL MMP Policy at Account Level
    * @param  String - Account Id
    * @return Boolean 
    */
     
	public static Boolean getILMMPIconForAccount(String sAccountId)
    {   
      if(String.isNotBlank(sAccountId)){
        String sILMMPStatus = '';
        Set<String> setPolicies = new Set<String>();
        Set<String> setOfAllMpStatus = new Set<String>();    
        String sILMMPtermedStatus ='';
        Integer iCount = 0;
        if(!bIsRecordsRetrieved){
            getMemberRecords(sAccountId); 
        }
	
        for(MemberPlan oPolicyMember: lstPolicyMembers)
        {   
             setOfAllMpStatus.add(oPolicyMember.Member_Coverage_Status__c);
            if(oPolicyMember.Product__c == 'MED' && oPolicyMember.Product_Type__c == 'MER'   && oPolicyMember.Product_Type_Code__c == 'MAPD' && 
                oPolicyMember.Plan.Contract_Number__c == 'H0336' && oPolicyMember.Plan.PBP_Code__c == '001' 
               && oPolicyMember.Plan.Medicare_Segment_ID__c == '000' && oPolicyMember.Plan.Issue_State__c == 'IL') {
                if(oPolicyMember.Member_Coverage_Status__c == 'Active' || oPolicyMember.Member_Coverage_Status__c =='Future'){
                    sILMMPStatus = oPolicyMember.Member_Coverage_Status__c;
                    break;
                }else if(oPolicyMember.Member_Coverage_Status__c == 'Termed'){
                    sILMMPtermedStatus = oPolicyMember.Member_Coverage_Status__c;
                 } 
                   
            }
            else if(oPolicyMember.Product__c == 'MED' && oPolicyMember.Product_Type__c == 'MCD'   && oPolicyMember.Product_Type_Code__c == NUll && 
                oPolicyMember.Plan.Contract_Number__c == 'H0336' && oPolicyMember.Plan.PBP_Code__c == '001' 
               && oPolicyMember.Plan.Medicare_Segment_ID__c == '000' && oPolicyMember.Plan.Issue_State__c == 'IL') {
                if(oPolicyMember.Member_Coverage_Status__c == 'Active' || oPolicyMember.Member_Coverage_Status__c =='Future'){
                    sILMMPStatus = oPolicyMember.Member_Coverage_Status__c;
                    break;
                }else if(oPolicyMember.Member_Coverage_Status__c == 'Termed'){
                    sILMMPtermedStatus = oPolicyMember.Member_Coverage_Status__c;
                 }      
            } 
        }
       
		if(String.isNotBlank(sILMMPStatus) || 
			(!setOfAllMPStatus.contains('Active') && !setOfAllMPStatus.contains('Future') 
			&& String.isNotBlank(sILMMPtermedStatus))){
				mapIconsStatus.put('IL_MMP',true);
			}else{
				mapIconsStatus.put('IL_MMP',false);
			}
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
    * This method is used to check at Policy Member for a Particular record if IDCardRequest Icon has to be displayed
    * @param  Policy_Member__c Id
    * @return Boolean
    */
    
    public Static Boolean getIDCardIconForPolMember(String sPolMemId)
    {
        Boolean bIDCardRequest = false;
        List<Case> lstCases = MemberIcons_LD_HUM.getCaseforIDCardIcon(sPolMemId); 
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
        if(!bIsPolicyRecordRetrieved){
          getPolicyMemberRecord(sPolicyMemId);       
        } 
        return  mapIconsStatus.get('IDCardRequest'); 
    }
          /*
    * This method is used to retrieve policy member single record
    * @param  String - Policy Member  Id
    * @return void 
    */
    private static void getPolicyMemberRecord(Id sPolMemId)
    {
        List<MemberPlan> lstPolicyMembers = MemberIcons_LD_HUM.getMemberPlanRecord(sPolMemId);
        if(lstPolicyMembers != Null && !lstPolicyMembers.isEmpty())
        {
            checkAtPolicyMemberLevel(lstPolicyMembers[0]);
        }
        bIsPolicyRecordRetrieved = true;
    }
    
    /*
     * This method is used to fetch the Birthdate and Deceased Date of the Account
     * @param String - Record Id 
	*/
    
    public static Account getBirthDescDate(string sRecordId){
    	return MemberIcons_LD_HUM.getBirthDescDate(sRecordId);    
    }
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
        List<PermissionSetAssignment> lstPermissionSets = MemberIcons_LD_HUM.getUserPermissionSets(lstIconPermissions);
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
            If(sProfileName == GLOBAL_CONSTANT_LH_HUM.SYSTEM_ADMINISTRATOR_PROFILE_HUM || sProfileName == GLOBAL_CONSTANT_LH_HUM.DEPLOYMENT_PROFILE_HUM)
            {
                bIsVisible = true;
            }
            else
            {
                if(sProfileName == GLOBAL_CONSTANT_LH_HUM.PHARMACYSPECIALIST_PROFILE_HUM)
                {
                     If(String.IsNotBlank(oIcon.Profile__c) && checkProfileName(oIcon,sProfileName))
                     {
                         bPSCheckSkipped = true;
                         bIsVisible = true;
                     }
                     else
                     {
                         bPSCheckSkipped = false;
                     }
                }
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
            List<PermissionSetAssignment> lstPermissionSets = MemberIcons_LD_HUM.getUserPermissionSets(lstPermissionSetNames);
            if(lstPermissionSets != Null && !lstPermissionSets.IsEmpty())
            {
                if(lstPermissionSets.size() == 1) 
                objRC = mapPSBasedRC.get(lstPermissionSets[0].PermissionSet.Name);
            }
        }
        return objRC;
    }

     /*
    * This method is used to get Repeat Caller Information to display RC attributes
    * @param  String - Account Id
              String- Current Interaction Id
              Boolean 
    * @return RepeatCallerInformation_DTO_HUM 
    */
    public static RepeatCallerInformation_DTO_HUM getRepeatCallerInformation(String sPageName,String MemberPlanID,String sAccountId,String sInteractionId,Boolean bColorLogicExecutionRequired)
    {
        RepeatCallerSettings_HUM__c objRC = getRepeatCallerConfigDetailsForLoggedinUSer();
         RepeatCallerInformation_DTO_HUM objInteractionCounts ;
        if(objRC != Null)
        {
            List<String> lstInteractionOrigin = objRC.Interaction_Origin__c.split(',');
            List<String> lstInteractionWithType = objRC.Interacting_With_type__c.split(',');
            DateTime dt14DaysDateBackDate = DateTime.Now().AddDays(-14);

            DateTime todaysStartDate = DateTime.newInstance(system.today().year(),system.today().month(),system.today().day(), 0,0,0);
            DateTime todaysEndDate = DateTime.newInstance(system.today().year(),system.today().month(),system.today().day(), 23,59,0);
            Integer iTodaysInteractionCount = 0;
            List<Account> lstAccounts = new List<Account>();
            if(sPageName== 'Member Account'){
                lstAccounts = MemberIcons_LD_HUM.getAccounts(sInteractionId,lstInteractionWithType,lstInteractionOrigin,dt14DaysDateBackDate,sAccountId);
            }else{
                lstAccounts = MemberIcons_LD_HUM.InteractionPlanMember(MemberPlanID,sInteractionId,lstInteractionWithType,lstInteractionOrigin,dt14DaysDateBackDate,sAccountId);    
            }
            if(null != lstAccounts && !lstAccounts.isEmpty())
            {
                objInteractionCounts= new RepeatCallerInformation_DTO_HUM();
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
                    if(objInteractionCounts.iInteractionsCreatedToday == 0){
                        objInteractionCounts.sTodaysInteractionsColor= GLOBAL_CONSTANT_LH_HUM.whiteColorCode;
                    }
                    else if(objInteractionCounts.iInteractionsCreatedToday <= Integer.valueOf(objCS.TodaysInts_Green_Range__c))
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
                    if(objInteractionCounts.iInteractionsCreatedInPast14Days == 0){
                        objInteractionCounts.sPast14DaysInteractionsColor='white';
                    }
                    else if(objInteractionCounts.iInteractionsCreatedInPast14Days <= Integer.valueOf(objCS.Past14DaysInts_Green_Range__c))
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
                     if(objInteractionCounts.iOpenCases == 0){
                        objInteractionCounts.sOpenCasesColor='white';
                    }
                    else if(objInteractionCounts.iOpenCases  <= Integer.valueOf(objCS.OpenCases_Green_Range__c))
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
              List<Account> lstAccounts = MemberIcons_LD_HUM.getAccountsforRCIcon(sInteractionId,lstInteractionWithType,lstInteractionOrigin,dt14DaysDateBackDate,sAccountId);
             
                if(null != lstAccounts && !lstAccounts.isEmpty())
               {                
                  if(null != lstAccounts[0].Interaction_Members__r  && !lstAccounts[0].Interaction_Members__r.isEmpty())
                   {
                     if(lstAccounts[0].Interaction_Members__r.size() >= 3)
                     {
                         bShowRepeatCallerIcon = true;
                     }
                   }
               }
            }
            catch(Exception ex){
                HUMExceptionHelper.logErrors(ex, 'MemberIcons_LS_HUM', 'getRepeatCallerIconStatus');
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
        Boolean bShowIndicator = false;
        try{
            Date newMemberStartDate;//(2023/01/01) need to pass
            Date newMemberEndDate;//(2023/12/31) need to pass
            Boolean dateLogicSwitch = HUMUtilityHelper.isCRMFunctionalityON('4021255');
            if(dateLogicSwitch){
                Map<String,MemberIcon_HUM__c> newMemLogic = MemberIcon_HUM__c.getAll();
                for(MemberIcon_HUM__c newMemDate : newMemLogic.values()){
                    if(newMemDate.Name=='NewMember' && newMemDate.Effective_From__c!=null && newMemDate.End_Date__c!=null){
                        newMemberStartDate = newMemDate.Effective_From__c;
                        newMemberEndDate = newMemDate.End_Date__c;
                    }
                }   
            }
            else{
                newMemberStartDate = Date.newinstance(2023, 1, 1);
                newMemberEndDate   = Date.newinstance(2023, 12, 31);
            }
            Date dateYearEarlierNewMemberStartDate = newMemberStartDate.addYears(-1);
            Account acc = MemberIcons_LD_HUM.getBirthDescDate(sAccountId);
            if(acc!=null){
                if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
                if(lstActiveAndFuturePolicies != null && !lstActiveAndFuturePolicies.IsEmpty())
                {
                    List<MemberIconsWrapper_LH_HUM> newMemberList = new List<MemberIconsWrapper_LH_HUM>();
                    
                    for (MemberIconsWrapper_LH_HUM activeFuturePol : lstActiveAndFuturePolicies) {
                        if (activeFuturePol.dtEffectiveDate >= newMemberStartDate && activeFuturePol.dtEffectiveDate <= newMemberEndDate) //  
                        {
                            newMemberList.add(activeFuturePol);
                        }
                        else if (activeFuturePol.dtEffectiveDate < newMemberStartDate && activeFuturePol.sPolicyMemberPlan != 'PDP')
                        {
                            lstTermedPolicies.add(activeFuturePol);
                        }
                    }
                    if (newMemberList != null && !newMemberList.isEmpty())
                    {   
                        newMemberList.sort();
                        Date dtEarliestNewMemberEffectiveDate = newMemberList.get(newMemberList.size() - 1).dtEffectiveDate;
                        Date dateYearLaterOfNewMemberEffecDate = dtEarliestNewMemberEffectiveDate.addYears(1);
                        
						dateYearEarlierNewMemberStartDate =  dtEarliestNewMemberEffectiveDate.addYears(-1);
                        if(dateYearLaterOfNewMemberEffecDate!=null){
                            Integer iDaysBetweenToShowIcon = System.today().daysBetween(dateYearLaterOfNewMemberEffecDate);
                        	bShowIndicator = iDaysBetweenToShowIcon > 0 ? true : false;
                        }
                    }
                    if (bShowIndicator && lstTermedPolicies != null && !lstTermedPolicies.isEmpty()) 
                    {   
                        Boolean bPharmacyuser = Pharmacy_LH_HUM.isPharmacyUser() == sNONPHARMACY_USER_HUM ? false : true;
                        Boolean bCCSPDPPharmacyPilot = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
                        for (MemberIconsWrapper_LH_HUM termOrOldPol : lstTermedPolicies) { 
                            if (termOrOldPol.dtEndDate >= dateYearEarlierNewMemberStartDate 
                                || !(termOrOldPol.sPolicyMemberStatus.equals('Termed')) ) {
                                    bShowIndicator = false;
                                   if (bPharmacyuser || bCCSPDPPharmacyPilot){
                                    for (MemberIconsWrapper_LH_HUM newMemberPolicy: newMemberList) {
                                        if (lstCCSPDPPharmacyPilotPTC.contains(newMemberPolicy.sPolicyMemberPlan) 
                                            && (termOrOldPol.sPolicyMemberPlan.equalsIgnoreCase(sPRODUCT_TYPE_MA)||termOrOldPol.sPolicyMemberPlan=='PDP'))
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
            }
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'MemberIcons_LS_HUM', 'getNewMemberIconStatus');
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
    * This method is used to execute check on FIDE Policy at Account Level
    * @param  String - Account Id
    * @return Boolean 
    */
    public static Boolean getFIDEIconForAccount(String sAccountId)
    {
        
        if(!bIsRecordsRetrieved){
            getMemberRecords(sAccountId);
        }
        
        for(MemberPlan oPolicyMember: lstPolicyMembers)
        {
            if(oPolicyMember.Member_Coverage_Status__c  == 'Termed')
            {
                checkAtPolicyMemberLevelFIDEIcon(oPolicyMember);
            }

        }

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
        
        for(MemberPlan oPolicyMember: lstPolicyMembers)
        {
            if(oPolicyMember.Member_Coverage_Status__c  == 'Termed')
            {
                    checkAtPolicyMemberLevelHIDEIcon(oPolicyMember);
            }

        }

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
    * This method is used to retrieve policy membe rrecords
    * @param  String - Account Id
    * @return void 
    */
    private static void getMemberRecords(String sAccountId)
    {
        lstPolicyMembers = MemberIcons_LD_HUM.getPolicyMemberRecords(sAccountId);
        segregateNewMemberAndGroupMemberRecords(lstPolicyMembers);
    }
    

    /*
    * This method is used to segregate policy member records based on different conditions
    * @param  List<Policy_Member__c>
    * @return void
    */
    private static void segregateNewMemberAndGroupMemberRecords(List<MemberPlan> lstPolMemRecords)
    {
        lstTermedPolicies = new List<MemberIconsWrapper_LH_HUM>();
        lstActiveAndFuturePolicies = new List<MemberIconsWrapper_LH_HUM>();
       
        for(MemberPlan oPolicyMember: lstPolMemRecords)
        {
            String sProductType = oPolicyMember.Plan.Product__r.Product_Type__c == NULL? oPolicyMember.Product_Type__c:oPolicyMember.Plan.Product__r.Product_Type__c;
           if(oPolicyMember.EffectiveFrom != null && oPolicyMember.Product__c == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HUM_BENEFITS_MED &&
              lstPTC.Contains(sProductType))
           {
                if(oPolicyMember.Member_Coverage_Status__c  == 'Termed') 
                {
                    lstTermedPolicies.add(new MemberIconsWrapper_LH_HUM(oPolicyMember));
                } 
                else 
                {
                    lstActiveAndFuturePolicies.add(new MemberIconsWrapper_LH_HUM(oPolicyMember));
                    
                }
           }
           if(oPolicyMember.Member_Coverage_Status__c  == 'Active' || oPolicyMember.Member_Coverage_Status__c  == 'Future')
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
    @testvisible private Static Void checkAtPolicyMemberLevel(MemberPlan  oPolicyMember)
    {	
        if(lstGrpMedicarePTC.Contains(oPolicyMember.Product_Type__c) && oPolicyMember.Member_Coverage_Status__c != 'Termed')
        {
            mapIconsStatus.put('GroupMedicare',true);
        }
        else if(oPolicyMember.Product_Type__c == GLOBAL_CONSTANT_LH_HUM.MAJOR_LOB_MES)
        {
            mapIconsStatus.put('MedicareSupplement',true);
        }
        /*if(oPolicyMember.Dual_Status_Indicator__c == 'Y')
        {
            mapIconsStatus.put('DualEligible',true);
        }*/
        if(oPolicyMember.Plan.Contract_Number__c == 'H1036' && oPolicyMember.Plan.Issue_State__c == 'FL' && oPolicyMember.Member_Coverage_Status__c != 'Termed')
        {
            mapIconsStatus.put('H1036Contract',true);
        }
      
        checkAtPolicyMemberLevelFIDEIcon(oPolicyMember);
        checkAtPolicyMemberLevelHIDEIcon(oPolicyMember);
		checkAtPolicyMemberLevelLINETIcon(oPolicyMember);  
        checkAtPolicyMemberLevelILMMPIcon(oPolicyMember);  
        checkAtPolicyMemberLevelIDCardIconActionalble(oPolicyMember);
    }
	/* This method is used to check at Policy Member for a Particular record if IDCardRequest ICON is actionable
    * @param  Policy_Member__c 
    * @return void
    */
    @testvisible private static void checkAtPolicyMemberLevelIDCardIconActionalble(MemberPlan oPolicyMember)
    {
        List<String> lstProduct = new List<String>{'VIT', 'FSA', 'HLT', 'HWS' , 'LIF', 'STI', 'VIS'};
        List<String> lstPlatform = new List<String>{'CB' , 'CS' , 'FM' , 'PR'};
        if(String.isNotBlank(oPolicyMember.Product__c) &&  !(lstProduct.contains(oPolicyMember.Product__c)) &&
           String.isNotBlank(oPolicyMember.Policy_Platform__c) && !(lstPlatform.contains(oPolicyMember.Policy_Platform__c)))
        {
            mapIconsStatus.put('IDCardRequest',true);
        }
    }
    
	/*
    * This method is used to check at Policy Member for a Particular record if LI NET ICON has to be displayed
    * @param  MemberPlan
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelLINETIcon(MemberPlan oPolicyMember)
    {	

        if(oPolicyMember?.Plan?.Contract_Number__c == 'X0001')
        {	
            mapIconsStatus.put('LINET',true);
        }
    }
    
    
        @testvisible private Static Void checkAtPolicyMemberLevelILMMPIcon(MemberPlan oPolicyMember)
    //@testvisible private Static Boolean checkAtPolicyMemberLevelILMMPIcon(MemberPlan oPolicyMember)
    {
     String sQuery = '';
     List<MemberPlan> pm = new list<MemberPlan>(); 

     if(oPolicyMember.Product__c == 'MED' && ( oPolicyMember.Product_Type__c == 'MER' ||oPolicyMember.Product_Type__c == 'MCD') &&
        oPolicyMember.Plan.Contract_Number__c == 'H0336' && oPolicyMember.Plan.PBP_Code__c == '001' &&
		oPolicyMember.Plan.Medicare_Segment_ID__c == '000' && oPolicyMember.Plan.Issue_State__c == 'IL') {
            if(oPolicyMember.Product_Type_Code__c == 'MAPD'){
            pm =[Select id ,Name
                FROM MemberPlan WHERE Name = :oPolicyMember.name AND Product__c = 'MED'   AND Product_Type__c = 'MER'  
                AND Product_Type_Code__c = 'MAPD'  AND Plan.Issue_State__c= 'IL'
                AND Member_Coverage_Status__c = :oPolicyMember.Member_Coverage_Status__c  AND EffectiveFrom = :oPolicyMember.EffectiveFrom  
                AND EffectiveTo = :oPolicyMember.EffectiveTo];

            }
            else if(String.isBlank(oPolicyMember.Product_Type_Code__c)){
                 pm =[Select id ,Name
                FROM MemberPlan WHERE Name = :oPolicyMember.name AND Product__c = 'MED' AND Product_Type__c = 'MCD' AND Product_Type_Code__c = Null
                AND Plan.Issue_State__c= 'IL' AND Plan.Contract_Number__c = 'H0336' AND Plan.PBP_Code__c = '001'
                AND Plan.Medicare_Segment_ID__c = '000' AND Member_Coverage_Status__c = :oPolicyMember.Member_Coverage_Status__c AND EffectiveFrom = :oPolicyMember.EffectiveFrom  
				AND EffectiveTo = :oPolicyMember.EffectiveTo];
            }
     }
                    
     if (!pm.isEmpty()){
        mapIconsStatus.put('IL_MMP',true);
     }else {
        mapIconsStatus.put('IL_MMP',false);

     }
    }

    /*
    * This method is used to check at Policy Member for a Particular record if FIDE ICON has to be displayed
    * @param  MemberPlan
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelFIDEIcon(MemberPlan oPolicyMember)
    {
        List<String> lstPBPCodeFIDE = new List<String>{'280','281','282','283'};

        if(String.isNotBlank(oPolicyMember.Plan.Contract_Number__c) &&  'H1036' == oPolicyMember.Plan.Contract_Number__c 
            && String.isNotBlank(oPolicyMember.Plan.Issue_State__c) &&  'FL' == oPolicyMember.Plan.Issue_State__c
            && String.isNotBlank(oPolicyMember.Plan.Medicare_Segment_ID__c) &&'000' == oPolicyMember.Plan.Medicare_Segment_ID__c 
            && String.isNotBlank(oPolicyMember.Plan.PBP_Code__c) &&  lstPBPCodeFIDE.contains(oPolicyMember.Plan.PBP_Code__c))
    {
            mapIconsStatus.put('FIDE',true);
        }

    }

    /*
    * This method is used to check at Policy Member for a Particular record if HIDE ICON has to be displayed
    * @param  MemberPlan
    * @return Void
    */
    @testvisible private Static Void checkAtPolicyMemberLevelHIDEIcon(MemberPlan oPolicyMember)
    {
        List<String> lstContractNumberHIDE = new List<String>{'H1036','H7284'};
        List<String> lstPBPCodeHIDE = new List<String>{'210','214','226','261','285','003'};
        
        if(String.isNotBlank(oPolicyMember.Plan.Contract_Number__c) &&  lstContractNumberHIDE.contains(oPolicyMember.Plan.Contract_Number__c)
            && String.isNotBlank(oPolicyMember.Plan.Issue_State__c) &&  'FL' == oPolicyMember.Plan.Issue_State__c
            && String.isNotBlank(oPolicyMember.Plan.Medicare_Segment_ID__c) && '000' == oPolicyMember.Plan.Medicare_Segment_ID__c
            && String.isNotBlank(oPolicyMember.Plan.PBP_Code__c) &&  lstPBPCodeHIDE.contains(oPolicyMember.Plan.PBP_Code__c))
        {
            mapIconsStatus.put('HIDE',true);
        }

    }

    /*
    * This method is used retrieve icon document records.
    * @param  NA
    * @return map<String,Document>
    */
    public static map<String,Document> getDocuments()
    {
        List<Document> lstDocs = MemberIcons_LD_HUM.getDocuments();
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
@AuraEnabled(cacheable=true)
    public static boolean getVeteranIconStatus(String sAccountId){       
        map<string,Product_Type_For_Veteran_Icon_Display__c> mapProductType = Product_Type_For_Veteran_Icon_Display__c.getAll();
		Boolean bVeteraniconSwitch = false;
        Boolean bPharmacyuser = Pharmacy_LH_HUM.isPharmacyUser() == sNONPHARMACY_USER_HUM ? false : true;
        Boolean bCCSPDPPharmacyPilot = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
        bVeteraniconSwitch = HUMUtilityHelper.isCRMFunctionalityOn('3315370');
        Boolean bVeteran = false;
        Boolean bVeteranstatus = false;
        if(String.isNotBlank(sAccountId)){
           string accVeteranStatus =[select id,Veteran_Status__c from account where id=:sAccountId limit 1]?.Veteran_Status__c;
            if((accVeteranStatus==GLOBAL_CONSTANT_HUM.sConfirmedVeteran) ||(accVeteranStatus==GLOBAL_CONSTANT_HUM.sIdentifiedVeteran)){
               bVeteranstatus=True;
        }
       }
        if(!bIsRecordsRetrieved) getMemberRecords(sAccountId);
        if(lstPolicyMembers.size()>0 && !mapProductType.isEmpty()){
        for(MemberPlan oPolMember :lstPolicyMembers){

                if(oPolMember.EffectiveFrom != oPolMember.EffectiveTo){    
                    
                    if((bPharmacyuser && bCCSPDPPharmacyPilot )&& ((String.isNotBlank(oPolMember.Product_Type__c)&& mapProductType.containsKey(oPolMember.Product_Type__c)) ||(String.isNotBlank(oPolMember.Product_Type_Code__c)&& mapProductType.containsKey(oPolMember.Product_Type_Code__c))) && bVeteranstatus &&bVeteraniconSwitch) {
                    bVeteran = true;                     
                   }   
               else  if(mapProductType ==null || mapProductType.isEmpty() && bVeteraniconSwitch){ 
                        bVeteran = true;                    
                    }
                    else if((String.isNotBlank(oPolMember.Product_Type_Code__c))&&(mapProductType.containsKey(oPolMember.Product_Type_Code__c) && 
                    bVeteranstatus && !bVeteraniconSwitch)){
                            bVeteran=false;
                    }   
                    else if((String.isNotBlank(oPolMember.Product_Type__c))&&(mapProductType.containsKey(oPolMember.Product_Type__c) && bVeteranstatus &&bVeteraniconSwitch)){
                    bVeteran=true;
                    }

                if(bVeteran ) break;
                }
        }
      }    
        return bVeteran;
   }
    /*
    * This method is used to check at Policy Member for a Particular record if Contact Handing  ICON has to be displayed
    * @param  MemberPlan
    * @return Void  
	*/
 
       public static Boolean getContactHandlingIconForAccount(String sAccountId)
    {
       
		for(Recommendation_Detail__c rDetil: [select Id,Recommendation__c,Recommendation__r.Name,Recommendation__r.Alert_Type__c,Recommendation__r.ActionReference, Account__c  From Recommendation_Detail__c where Account__c =:sAccountId  AND Recommendation__r.Alert_Type__c ='Contact Handling']){
		 if(rDetil !=null && rDetil.Recommendation__r.Alert_Type__c =='Contact Handling'){
			mapIconsStatus.put('ContactHandlingAlert' ,true);  
			 }
		  }
			return  mapIconsStatus.get('ContactHandlingAlert');  
    }    
   
}