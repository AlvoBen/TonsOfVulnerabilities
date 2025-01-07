/**************************************************************************** 
Apex Class Name  : HUMPolicyMemberRelatedListController 
Version          : 1.0  
Created Date     : July 21 2014 
Function         : This class is used to display the policy data by overriding the security.
Complete Policy data is displayed  to the user where he has full access. Only limited
data like policy Group Name, Group Number and Product details are display for which user 
does not have access to. The class is made as 'without sharing' since we are conditionally 
overriding security to retrieve the policy details, but displaying necessary information 
based on security.
Test Class      :  HUMPolicyMemberRelatedListControllerTest


* Developer Name           Code Review                  Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Anil Pilka                17169                      07/21/2014                  Original Version
* Shruthi Karanth           17169                      07/25/2014                  Call transfer functionality is added
* Shruthi Karanth           17169                      08/09/2014                  Fixed review defect D14870.
* Anil Pilka                17169                      08/15/2014                  Changes the class as without sharing
* Shruthi Karanth           18234                      09/27/2014                  Added code to display the list sorted on status on page.
* Shruthi Karanth           18234                      09/29/2014                  Removed inner class
* Shreya Choodamani		 27303					    10/22/2015 			        Modified the SOQL on Line#49 - 81 for the Soft Delete(REQ - 233770) US changes.
* Shikhar Mehta			 24223  				    3/31/2016				    Defect#221159
* Samridh Manucha           40994                      9/26/2016                   Defect#242220.0001
* Amar Gadewar				 73794						03/03/2017					REQ - 301397 Display last Update date for Policy Member  on account page (RF)
* Sharan Shanmugam								 		05/02/2017			 		REQ - 303395 - Restrict policy members for Humana Pharmacy user
* Sai Ayyagari              146398                     11/09/2017                  REQ - 335296 - Null Check for account ID
* Vishal Verma   			 150268                     11/22/2017				    REQ - 348197 : Added a check to determine Policy member access based on logged in user 
* Mithra Bharadwaj			 321316						1/3/2019					REQ - 350473 Member Details Page Policies Section SOQL Limit 
* Mithra Bharadwaj                                     09/16/2019                  REQ - 404577 Adding Plan Name to Member search results.
* Praveen Kumar Parimi                                 10/31/2019                  REQ - 415243 Fetching IAB plan name from Policy Member or Policy.
* Santhosh Ganji                                       02/01/2020                  Userstory 750125 Incident 8213853 Fix.
* Yogesh Gupta											13/01/2020					REQ - 769549 Hover Over Policy Page
* Vinay Lingegowda					                    02/10/2020		            User story- 791019 PR00091574 - MF 1 - AHT - Display Termed policies in the chronological order on the Member Account page (RF)
* Amar Gadewar                                         03/09/2020                  User Story 915384 PR00091574 - MF 1 - AHT - Display Termed policies in the chronological order on the Member Account page (CRM)
* Amar Gadewar                                         04/03/2020                  US 995046 PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup - 4/10 and 4/24 Release (CRM)
* Keerti Durbha                                        04/23/2020                  738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness- Added OSB Indicator to Policy Member query
* Keerti Durbha                                        05/12/2020                  738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness- OSB on/off functioanlity
* Sagar MN												08/06/2020				    USER STORY 1329433 EDW Reporting changes 
* Asish Behera										    09/04/2020				    PR00090588 - CR1727 - Display Medicare PDP Plan Name - Policy Section (Person Account Page)
* Firoja Begam										    12/30/2021				    PR00094226 - Incident: INC0669657: SF - TECH - CRM - Policies section on Account page is getting collapsed when fields are logged
* Shreya Agrawal/Isha Gupta							    01/30/2021			        PhoneBook Implementation
* Ankit Avula                                           03/28/2021                  User Story 1987365 Contract Protected Data Implementation
* Ajay Chakradhar                           		    05/05/2021	                REQ - 2184294 - Replacing constants with Consumer Management Constants
* Akash Surwase                                         08/17/2022                  USER STORY 2819667 : T1PRJ0100059- MF 3 FR 10 - Case Creation (HP)
********************************************************************************************/ 

public without Sharing class HUMPolicyMemberRelatedListController 
{
    public String sAccId{get;set;}  
    public String sInteractionId{get; set;}
    public String sPolicyMemberId{get; set;}
    public String sPolicyId{get; set;}
    public Boolean bSwitch1279870 {get;set;}
    public Integration_Mapping__c intMapping;
    public list<HUMPolicyAccessDetailsWrapperHelper> lstPolicyAccessWrapperBackup;
    public Integer rowNumber{get;set;}
    public Boolean bSwitch1746305 {get;set;}
    public Boolean contractprotecteddataonoff {get;set;}
    public Boolean agnosticOnOff {get;set;}
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    public Boolean bagnosticOnOffswitch {get;set;}
    public String msgValue{get;set;}
    public Map<String,String> PolicyDataMap{get;set;} 
    Private String sHUM_FIELDVALUE_PRODUCTNAME  = 'VIS'; 
    
    //To check if a user is a genesys user or not
    public Boolean isGenesysUser{get; set;}

    public HUMPolicyMemberRelatedListController()
    {
        isGenesysUser = PhoneBook_H_HUM.isGenesysUser();
        agnosticOnOff = false;
        agnosticOnOff = [SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == PHARMACYSPECIALIST_PROFILE_HUM ? true : false;
        bagnosticOnOffswitch=HUMUtilityHelper.isCRMFunctionalityON('2860677');
    }

    public Boolean phoneBookSwitch1726016 //new change
    {
        get
        {
            return HUMUtilityHelper.isCRMFunctionalityON('1726016_CallTransferToPhoneBook');
        }
        set;
    }
    
    //Policy Member Wrapper List
    public list<HUMPolicyAccessDetailsWrapperHelper> lstPolicyAccessWrapper 
    {
        get{ 
            if(lstPolicyAccessWrapper ==null)
            {
                lstPolicyAccessWrapper = new List<HUMPolicyAccessDetailsWrapperHelper>();
                determinePolicyAccess();
            }  
            return lstPolicyAccessWrapper;
        } 
        
        set;
    }
    
    //Standard Set Controller for policy members
    public ApexPages.StandardSetController setConPolicies 
    {
        get {
            bSwitch1279870 = HUMUtilityHelper.isCRMFunctionalityON('1279870');
            bSwitch1746305 = HUMUtilityHelper.isCRMFunctionalityON('1746305');
            contractprotecteddataonoff = HUMUtilityHelper.isCRMFunctionalityON('2037674');
            if(setConPolicies == null && sAccId != NULL && !String.isblank(sAccId)) 
            { 
                List<AccountPolicyWrapper_H_HUM> lstAccountPolicyMemberWrapper = new List<AccountPolicyWrapper_H_HUM>();
                List<Policy_Member__c> lstAccPolicyMember = new List<Policy_Member__c>();
                for(policy_member__c polMemb : [SELECT id,Name,Member__r.RecordType.Name,OSB_Indicator__c,Product__c,iab_description__c,policy__r.iab_description__c,policy__r.Product_Type__c,policy__r.Product__r.Product_Line_Description__c,Product_Type__c,ASO__c,Relationship__c,  policy__r.Contract_Number__c,policy__r.PBP_Code__c,policy__r.Medicare_Segment_ID__c,policy__r.Source_Cust_Cov_Key__c,policy__r.Benefit_Coverage__c,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Platform__c,(SELECT Id,Subject FROM cases__r limit 1),Group_Name__c,Group_Number__c,Effective_Date__c,End_Date__c,Status__c,EHB_Term_Date__c, ETL_Record_Deleted__c,LastModifiedDate
                                                FROM Policy_Member__c where Member__c = :sAccId order by Status__c asc,Product_Order__c asc])                
                {
                    if(polMemb.ETL_Record_Deleted__c)
                    {
                        List<Case> lstCase = new List<case>();
                        lstCase = polMemb.Cases__R;
                        if(!lstCase.isEmpty())
                        {
                            lstAccountPolicyMemberWrapper.add(new AccountPolicyWrapper_H_HUM(polMemb));
                        }
                    }
                    else
                    {
                        lstAccountPolicyMemberWrapper.add(new AccountPolicyWrapper_H_HUM(polMemb)); 
                    }
                }
                
                lstAccountPolicyMemberWrapper.sort(); 
                for(AccountPolicyWrapper_H_HUM accPolicyWrap:lstAccountPolicyMemberWrapper){
                    lstAccPolicyMember.add(accPolicyWrap.polyMemb);
                }
                setConPolicies = new ApexPages.StandardSetController(lstAccPolicyMember);
                
            }
            setConPolicies.setPagesize(5);
            return setConPolicies ;
        }
        set;
    }
    public Boolean getAgnosticAccess(String sAccId){
        Account account = [SELECT Id, Name, RecordType.Name FROM Account WHERE ID =: sAccId LIMIT 1];
        if(account.RecordType.Name == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER){
            return isPharmacyUserforAgnostic();
        }
        return false;
    }
    public Boolean isPharmacyUserforAgnostic(){
        if([SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == PHARMACYSPECIALIST_PROFILE_HUM){
            RETURN true;
        }
        RETURN false;
    }
    /*  
* Method name : saveDataForCallTransfer
* Description : This method is used to save the data for call transfer. Call transfer requires data from policy and product.  
* Return Type : void
* Parameter :   N/A
*/ 
    public void saveDataForCallTransfer()
    {
        try
        {
            //PhoneBook Implementation
            if(isGenesysUser && phoneBookSwitch1726016)
            {
                PolicyDataMap = new  Map<String,String>();
                String InteractionId='';
                String InteractingFor ='';
                if(!String.isBlank(sInteractionId))
                {   
                    Integer sStartPos = sInteractionId.indexOf('&interactionId=');
                    if ((sStartPos != -1)  && (String.isNotBlank(sInteractionId.substring(sStartPos + 15))) )
                    {
                        InteractionId = sInteractionId.substring(sStartPos + 15, sStartPos + 33);
                    }
                    List<Interaction__c> interaction = [Select Interacting_With__r.Id,UUID__c from Interaction__c where id=:InteractionId limit 1];
                    if(interaction.size()>0){
                        InteractingFor = interaction[0].Interacting_With__r.Id;
                    }
                }
                PolicyDataMap.put(Label.HUMSPhoneMappingObjInteraction,InteractionId );
                PolicyDataMap.put(Label.HUMSPhoneMappingObjInqFor, InteractingFor);
                PolicyDataMap.put(Label.HUMSPhoneMappingObjInqAb, sAccId);
                PolicyDataMap.put(Label.HUMSPhoneMappingObjPolicyMember, sPolicyMemberId);
                msgValue =String.ValueOf(JSON.serialize(PolicyDataMap));
            }
            else{
                intMapping = new Integration_Mapping__c();
                String iInteraction ='';
                
                //Fetch the interaction Id from the console URL. 
                if(!String.isBlank(sInteractionId))
                {   
                    Integer sStartPos = sInteractionId.indexOf('&interactionId=');
                    if (sStartPos != -1) 
                    {
                        String intId = sInteractionId.substring(sStartPos + 15, sStartPos + 33);
                        iInteraction = intId;
                    }
                }
                //Query the interaction details. UUID is needed for call transfer, which is stored on interaction
                String sQry = 'Select Interacting_With__r.Id,UUID__c from Interaction__c where id=:iInteraction';
                //Query the interaction details 
                Interaction__c inter = Database.query(sQry);
                
                Id InteractingWith = inter.Interacting_With__r.Id;
                
                //Prepare the string to update the INTEGRATION MAPPING object that is used for soft phone call transfer data
                String sMapping = Label.HUMSPhoneMappingObjInteraction + ':' + String.valueOf(iInteraction) + ';' + Label.HUMSPhoneMappingObjInqFor + ':';
                sMapping += String.valueOf(InteractingWith) + ';' + Label.HUMSPhoneMappingObjInqAb + ':' + sAccId + ';'; 
                sMapping += Label.HUMSPhoneMappingObjPolicyMember + ':' + sPolicyMemberId;
                
                //Select the Integration record where the name is Soft Phone routing   and create the integration mapping for call transfer data
                String sIntQry = 'Select Name,id from Integration__c where Name= \''+String.escapeSingleQuotes(System.Label.HUMIntegrationTypeSoftPhone)+'\'';
                Integration__c softPhone = Database.query(sIntQry);
                intMapping.Integration_ID__c=softPhone.id;
                intMapping.Mapping__c = sMapping;
                intMapping.Record_ID__c = inter.UUID__c;
                insert intMapping;
                //Display message to the user about subsequent action to click REFRESH on soft phonr 
                ApexPages.Message msg=new ApexPages.Message(ApexPages.Severity.INFO,Label.HUMSFCallTransfer);
                ApexPages.addMessage(msg); 
            }  
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'HUMPolicyMemberRelatedListController','saveDataForCallTransfer');
        }
    }
    
    /*  
* Method name : determinePolicyAccess
* Description : This  method determines the policy level of access of the logged in user. 
* Return Type : void
* Parameter :   N/A
*/
    public void determinePolicyAccess()
    {
        List<Policy_Member__c> lstMemberPolicies = new List<Policy_Member__c>();
        List<Id> lstPolicyId = new List<Id>();
        String sPharmacyUser = '';
        sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
        
        
        //Get the policy member records from the set controller
        lstMemberPolicies = (List<Policy_Member__c>) setConPolicies.getRecords();
        
        lstPolicyAccessWrapper.clear();    
        
        for(Policy_Member__c plcyMb : lstMemberPolicies)
        { 
            lstPolicyId.add(plcyMb.id);
        }    
        map<Id,boolean> mapAccessPolicyMember = new map<Id,boolean>();
        //Loop through the policy member records to determine the access level of the policy for logged in user    
        for(UserRecordAccess userRecAccess:  [SELECT RecordId, HasReadAccess
                                              FROM UserRecordAccess
                                              WHERE UserId = :userinfo.getUserId() AND RecordId IN:  lstPolicyId]){
                                                  mapAccessPolicyMember.put(userRecAccess.RecordId,userRecAccess.HasReadAccess);
                                              }   
        rowNumber = 0;
        for(Policy_Member__c policyMember : lstMemberPolicies)
        {
            if(Pharmacy_H_HUM.determineAccountPageMemberAccess(policyMember.Product__c,sPharmacyUser))
            {
                if(policyMember.Product__c == sHUM_FIELDVALUE_PRODUCTNAME)
                {
                    HUMPolicyAccessDetailsWrapperHelper wrpperObj = new HUMPolicyAccessDetailsWrapperHelper(policyMember,mapAccessPolicyMember.get(policyMember.id),policyMember.policy__r.Product_Type__c);
                    wrpperObj.rowNumber =rowNumber;
                    if(policyMember.Member__r.RecordType.Name == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER && bagnosticOnOffswitch)
                        wrpperObj.nPolicyLink = !agnosticOnOff;                    
                    lstPolicyAccessWrapper.add(wrpperObj);
                }
                else{
                    Boolean OSBONOFF= HUMUtilityHelper.isCRMFunctionalityON('738134');
                    Boolean canProceed = HUMUtilityHelper.deduceOSB(OSBONOFF, PolicyMember.OSB_Indicator__c);
                    if(canProceed) {
                        HUMPolicyAccessDetailsWrapperHelper wrpperObj = new HUMPolicyAccessDetailsWrapperHelper(policyMember,mapAccessPolicyMember.get(policyMember.id),policyMember.Product_Type__c);
                        wrpperObj.rowNumber = rowNumber;
                        if(policyMember.Member__r.RecordType.Name == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER && bagnosticOnOffswitch)
                            wrpperObj.nPolicyLink = !agnosticOnOff;
                        lstPolicyAccessWrapper.add(wrpperObj);
                    } 
                }
                rowNumber++;
            }
        }
        if(!lstPolicyAccessWrapper.isEmpty()){
            lstPolicyAccessWrapperBackup = new list<HUMPolicyAccessDetailsWrapperHelper>();
            lstPolicyAccessWrapperBackup.addAll(lstPolicyAccessWrapper);
        }
        
    }
    
    /*  
* Method name : next
* Description : This method iterates through the next set of policy member records in the set controller      
* Return Type : void
* Parameter :   N/A
*/
    public void next()
    {
        setConPolicies.next();
        determinePolicyAccess();
    }
    
    /*  
* Method name : previous
* Description : This method iterates through the previous set of policy member records in the set controller      
* Return Type : void
* Parameter :   N/A
*/
    public void previous()
    {
        setConPolicies.previous();
        determinePolicyAccess();
    }
    public void showrelatedTable()
    {
        list<Policy_Plan_Details__c> listOfPlanDetails;
        list<HUMPolicyAccessDetailsWrapperHelper> lstPolicyWraptemp = new list<HUMPolicyAccessDetailsWrapperHelper>();
        HUMPolicyAccessDetailsWrapperHelper policyWrap;        
        try{
            if(String.isNotBlank(sPolicyId)){
                listOfPlanDetails = [SELECT iab_description__c,Coverage_Plan_Effective_Date__c,Coverage_Plan_End_Date__c
                                     FROM Policy_Plan_Details__c
                                     WHERE Policy__c = :sPolicyId AND iab_description__c !=null AND Coverage_Plan_End_Date__c !=null 
                                     AND Coverage_Plan_Effective_Date__c !=null AND ETL_Record_Deleted__c = false order by createdDate ASC];
                
                string sPlanType = '';
                Date effectiveDate;
                Date endDate;
                if(!listOfPlanDetails.isEmpty()){
                    
                    for(Integer i=0; i<=lstPolicyAccessWrapper.size()-1;i++){
                        if(rowNumber == i){                           
                            lstPolicyAccessWrapper[i].IsPDPPolicy = 'false';
                            lstPolicyWraptemp.add(lstPolicyAccessWrapper[i]);
                            policyWrap = new HUMPolicyAccessDetailsWrapperHelper();
                            policyWrap.sPlanName = Label.HUMPlanNameHeaderValueLabel;
                            policyWrap.sEffectiveDate = Label.HUMEffDateHeaderValueLabel;
                            policyWrap.sEndDate = Label.HUMEndDtHeaderValueLabel;
                            lstPolicyWraptemp.add(policyWrap);                            
                            Integer count=0;
                            
                            for(Integer j=listOfPlanDetails.size()-1;j>=0; j--){
                                policyWrap = new HUMPolicyAccessDetailsWrapperHelper();
                                effectiveDate = Date.parse(listOfPlanDetails[j].Coverage_Plan_Effective_Date__c);
                                try{
                                    endDate = Date.parse(listOfPlanDetails[j].Coverage_Plan_End_Date__c);
                                }catch(Exception e) {
                                    List<String> lstStr = listOfPlanDetails[j].Coverage_Plan_End_Date__c.split('/');
                                    endDate = Date.newInstance(Integer.valueOf(lstStr.get(2)), Integer.valueOf(lstStr.get(0)), Integer.valueOf(lstStr.get(1)));
                                }
                                
                                if(count>=3) break;
                                if(sPlanType != 'Past' && endDate < system.today() && effectiveDate < system.today()){
                                    
                                    sPlanType = 'Past';
                                    policyWrap.sPlanName = listOfPlanDetails[j].iab_description__c;
                                    policyWrap.sEffectiveDate = listOfPlanDetails[j].Coverage_Plan_Effective_Date__c;
                                    policyWrap.sEndDate = listOfPlanDetails[j].Coverage_Plan_End_Date__c;
                                    lstPolicyWraptemp.add(policyWrap);
                                    count++;
                                }else if(sPlanType != 'Current' && effectiveDate <= system.today() && endDate >= system.today()){                                     
                                    sPlanType = 'Current';
                                    policyWrap.sPlanName = listOfPlanDetails[j].iab_description__c;
                                    policyWrap.sEffectiveDate = listOfPlanDetails[j].Coverage_Plan_Effective_Date__c;
                                    policyWrap.sEndDate = listOfPlanDetails[j].Coverage_Plan_End_Date__c;
                                    lstPolicyWraptemp.add(policyWrap);
                                    count++;
                                }else if(sPlanType != 'Future' && effectiveDate > system.today() && endDate > system.today()){
                                    
                                    sPlanType = 'Future';
                                    policyWrap.sPlanName = listOfPlanDetails[j].iab_description__c;
                                    policyWrap.sEffectiveDate = listOfPlanDetails[j].Coverage_Plan_Effective_Date__c;
                                    policyWrap.sEndDate = listOfPlanDetails[j].Coverage_Plan_End_Date__c;
                                    lstPolicyWraptemp.add(policyWrap);
                                    count++;
                                }                          
                                
                            }
                            
                        }else{
                            lstPolicyWraptemp.add(lstPolicyAccessWrapper[i]);
                        }
                        
                    }
                    
                    lstPolicyAccessWrapper.clear();                    
                    lstPolicyAccessWrapper.addAll(lstPolicyWraptemp); 
                    
                    
                }        
                
            }
            
        }catch(Exception e) {
            
            HUMExceptionHelper.logErrors(e, 'HUMPolicyMemberRelatedListController', 'showrelatedTable');
        } 
        
        
    }
    public void removerelatedTable(){
        try{
            if(!lstPolicyAccessWrapperBackup.isEmpty()){
                lstPolicyAccessWrapperBackup[rowNumber].IsPDPPolicy = lstPolicyAccessWrapperBackup[rowNumber].IsPDPPolicy=='false'?'true':'';
                lstPolicyAccessWrapper.clear();
                lstPolicyAccessWrapper.addAll(lstPolicyAccessWrapperBackup);
            }
        }catch(Exception e) {
            
            HUMExceptionHelper.logErrors(e, 'HUMPolicyMemberRelatedListController', 'removerelatedTable');
        } 
   
    }
    
}