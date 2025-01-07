/**************************************************************************** 
Apex Class Name  : MemberActivePolicies_C_HUM 
Version          : 1.0  
Created Date     : Jan 14, 2018
Function         : This class is used to display the policy data on Search Page. Refer class 'HUMPolicyMemberRelatedListController'
Test Class      :  MemberActivePolicies_T_HUM


 * Developer Name           Code Review                  Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Amar Gadewar                                          01/14/2017                 Original Version
 * Asish Behera                                         05/30/2018                 REQ - 363572 Preselect Policy from the Search Page (RF)
 * Suresh Gottipati                                      05/31/2018                 REQ - 362027 - Added Member_Id.Member_Id_Value field in code which Is replacement of Name Field
 * Darshan Sharma				                        06/26/2018		            REQ-367008 - AHT - Preselect Policy from the Search Page (RF)
 * Asish   Behera				                        01/03/2019		            REQ -382779 - AHT - Preselect Policy from the Search Page (CRM)
 * Asish Behera                                         1/23/2019                   REQ - 383628 - PCC - Assign functionalities access depending on Quick Start 650 Permission to RSO Medicare Only 684 Permission (CRM)
 * Santhi Mandava                                       04/15/2019                 Replaced "Member_Id_Value__c" with Name
 * Rajasekhar Chintapalli                               05/06/2019                 REQ - 396410 - Non PHI Home Office Policy Info on Member Search Screen (GBORF)
 * Mithra Bharadwaj                                     09/16/2019                 REQ - 404577 Adding Plan Name to Member search results.
 * Praveen Kumar Parimi                                 10/31/2019                 REQ - 415243 Fetching IAB plan name from Policy Member Or Policy.
 * Kiran Kumar											04/27/2020					REQ - 850087: Preselect Policy in 'Active and Future Policies' Section of Search Page (RxRF)
 * Keerti Durbha                                        04/27/2020                 738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness- Added OSB Indicator to Policy Member query
 * Keerti Durbha                                        05/12/2020                 738134- PR00091318 - MF 9 - Medicare OSB Vision And Fitness- on/off functionality
 * Kiran Kumar											05/08/2020					REQ - 937903: Preselect Policy in 'Active and Future Policies' Section of Search Page (CRMS)
 * Sagar MN								                08/06/2020				    USER STORY 1329433 EDW Reporting changes
 * Jayesh Solanki										03/17/2021					User Story 2056116: Ability to display Termed Policies information on the Member Search tab within the Search page (RF)
 * Ankit Avula                                          04/01/2021                  User Story 1987365 Contract Protected Data and Home Office
 * Jayesh Solanki                                       05/25/2021                  User Story 2305573: T1PRJ0001827 - MF 1 - AHT - Ability to display Termed Policies information on the Member Search tab within the Search page (CRM)
 * Ankit Avula											06/04/2021					User Story 2275855 T1PRJ0002980 - Protected Group - Policy auto selection logic update
 * Samantha Tennyson       						 		06/23/2021           	    User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM) 
 * Ajay Chakradhar										10/13/2021			        US 2712261 : Global Const and HumConst files changes for Performance improvement 
***********************************************************************************************************************************************************************/
Public without sharing Class MemberActivePolicies_C_HUM {
    public Boolean contractprotecteddataonoff {get;set;}
    public Boolean radioButtonOnOff {get;set;}
    Public String sAccId{get; set{
            sAccId = value;           
            determinePolicyAccess();            
        }
    }  
    Public String NONPHARMACY_USER_HUM = 'No';
    Public String VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public String VOB_FUTURE_HUM = 'Future';
    Public String MEDICARE_POLICY = 'MED';
    Public String HUMMedicare = 'MedicareID';

    //Policy Member Wrapper List
    public list<HUMPolicyAccessDetailsWrapperHelper> lstPolicyAccessWrapper {get;set;}    
 
    /*  
     * Method name : determinePolicyAccess
     * Description : This  method determines the policy level of access of the logged in user. 
     * Return Type : void
     * Parameter :   N/A
     */
    public void determinePolicyAccess() {
        list<Id> lstPolicyId = new List<Id>();
        Integer iNoOfMEDpolicy = 0;
        Integer iNoOfFuturePolicy  = 0; 
        Integer iNonHumanaPolicy  = 0;       
        contractprotecteddataonoff = HUMUtilityHelper.isCRMFunctionalityON('2037674');
        //US 2275855 On Off Switch
        radioButtonOnOff = HUMUtilityHelper.isCRMFunctionalityON('2275855');
        try {     
            String profileName = [SELECT Id,Name FROM Profile WHERE Id =: userInfo.getProfileId()].Name;       
        	String sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
            this.lstPolicyAccessWrapper = new List<HUMPolicyAccessDetailsWrapperHelper>();
			Boolean bPharmacyuser = Pharmacy_H_HUM.isPharmacyUser() == NONPHARMACY_USER_HUM ? false : true;
            Boolean bCCSPDPPharmacyPilot = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess('CRMS_205_CCSPDPPharmacyPilot');
                        
            if(String.isNotBlank(sAccId)) {

                List<Policy_Member__c> listMemberPolicies = new List<Policy_Member__c>();
					
                    List<Policy_Member__c> listMemberRFPolicies = [SELECT id,policy__r.Coverage_Plan_Effective_Date__c,policy__r.Benefit_Coverage__c,Policy__r.Platform__c,
                                                                   Policy__r.Source_Cust_Cov_Key__c, Name, Product__c,OSB_Indicator__c,iab_description__c,policy__r.iab_description__c,
                                                                   policy__r.Product_Type__c, Product_Type__c, End_Date__c, Effective_Date__c, ASO__c, Relationship__c, Group_Name__c, Group_Number__c,Member__r.RecordType.Name,EHB_Term_Date__c, Status__c, Policy__r.Group_Name__r.Security_Groups__c,
                                                                   (Select Name From Member_Ids__r Where Type__c =: HUMMedicare) 
                                                               FROM Policy_Member__c where Member__c = :this.sAccId and ETL_Record_Deleted__c = false order by Status__c asc,Product_Order__c asc LIMIT 20];
                    
                    List<AccountPolicyWrapper_H_HUM> lstAccountPolicyMemberWrapper = new List<AccountPolicyWrapper_H_HUM>();
                    for(Policy_Member__c pm : listMemberRFPolicies){
                        lstAccountPolicyMemberWrapper.add(new AccountPolicyWrapper_H_HUM(pm));
                    }
                    
                    lstAccountPolicyMemberWrapper.sort();

                    Integer termedCounter=0;
                    for(AccountPolicyWrapper_H_HUM accPolicyWrap : lstAccountPolicyMemberWrapper){
                        if((accPolicyWrap.polyMemb.Status__c == VOB_ACTIVEMEMBERSTATUS_HUM) || (accPolicyWrap.polyMemb.Status__c == VOB_FUTURE_HUM))
                        {
                            listMemberPolicies.add(accPolicyWrap.polyMemb);
                        }  else if(accPolicyWrap.polyMemb.Status__c == 'Termed' && termedCounter <= 2){
							
                            termedCounter++;
                           listMemberPolicies.add(accPolicyWrap.polyMemb); 
                    }
                

                }
			
               
                Set<Id> policyMemberIds = new Set<Id>();
                for(Policy_Member__c policyMember : listMemberPolicies) {
                    policyMemberIds.add(policyMember.Id);
                    if(policyMember.Status__c == VOB_ACTIVEMEMBERSTATUS_HUM && policyMember.Product__c == MEDICARE_POLICY)
                    	iNoOfMEDpolicy++;
                    else if(policyMember.Status__c == VOB_FUTURE_HUM  && policyMember.Product__c == MEDICARE_POLICY)
                        iNoOfFuturePolicy++;
                    else if(policyMember.Product__c == 'TRA')
                        iNonHumanaPolicy++;
                    
                }
                
                if(null != policyMemberIds && !policyMemberIds.isEmpty()) {
                    //Adding a map to get the recordIds and their access for the user
                    map<Id,boolean> mapAccessPolicyMember = new map<Id,boolean>();
                    //Loop through the policy member records to determine the access level of the policy for logged in user
	                List<UserRecordAccess> userRecordAccessList = [SELECT RecordId, HasReadAccess FROM UserRecordAccess WHERE UserId = :userinfo.getUserId() AND RecordId IN: policyMemberIds];
	                for(UserRecordAccess usrRecordAccess : userRecordAccessList){
                        if(contractprotecteddataonoff){
                        	mapAccessPolicyMember.put(usrRecordAccess.RecordId,usrRecordAccess.HasReadAccess);
                        }
                        else{
	                    	if(!usrRecordAccess.HasReadAccess){
	                        	policyMemberIds.remove(usrRecordAccess.RecordId);       
	                    	}
                        }
	                }
	                
	                for(Policy_Member__c policyMember: listMemberPolicies){
	                    if(policyMemberIds.contains(policyMember.Id) && Pharmacy_H_HUM.determinePolicyMemberAccess(policyMember.Product__c,sPharmacyUser)) {
                            if(contractprotecteddataonoff){
                                //US1987365 Using the map to see if the user has access or not to the policies when switch is on
	                        	HUMPolicyAccessDetailsWrapperHelper policywrap = new HUMPolicyAccessDetailsWrapperHelper(policyMember, mapAccessPolicyMember.get(policyMember.id), policyMember.Product_Type__c); // changes related moving 650 to 684 permissionset
	                            if(iNoOfMEDpolicy == 1 && policyMember.Status__c == VOB_ACTIVEMEMBERSTATUS_HUM && policyMember.Product__c == MEDICARE_POLICY && (UtilityHelper_H_AHT_HUM.isUserHavingMedicareCustomerServiceAccess()|| bCCSPDPPharmacyPilot)){
                            	    policywrap.sPreselect = 'selectMEDpolicy';     
                                }
							// For REQ 937903 
                                else if(((iNoOfMEDpolicy == 1) || (iNoOfMEDpolicy == 0 && iNoOfFuturePolicy == 1)) && policyMember.Product__c == MEDICARE_POLICY && (bPharmacyUser)){
                                    policywrap.sPreselect = 'selectMEDpolicy'; 
                                }
                                //Agnostic
                                else if(iNonHumanaPolicy == 1){
                                    policywrap.sPreselect = 'NonHumanaPolicy';
                                }
                                //Agnostic                                
                                else{
                                    policywrap.sPreselect = 'Policies';
                                }   
                                policywrap.hasHumanaProfileAccess = true;
                                if(policyMember.Member__r.recordType.Name == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER)
                                    policywrap.hasHumanaProfileAccess = (profileName == ConsumerManagementConstants_HUM.PHARMACYSPECIALIST_PROFILE_HUM) ? true : false;                             
                                Boolean OSBONOFF= HUMUtilityHelper.isCRMFunctionalityON('738134');
                                Boolean canProceed = HUMUtilityHelper.deduceOSB(OSBONOFF, PolicyMember.OSB_Indicator__c);
                                if(canProceed){
                                  lstPolicyAccessWrapper.add(policywrap);
	                            }
                            }
                            //the below else block will run when contractprotecteddataonoff is in off condition
                            else{
	                            HUMPolicyAccessDetailsWrapperHelper policywrap = new HUMPolicyAccessDetailsWrapperHelper(policyMember, true, policyMember.Product_Type__c);
	                            // changes related moving 650 to 684 permissionset
	                            if(iNoOfMEDpolicy == 1 && policyMember.Status__c == VOB_ACTIVEMEMBERSTATUS_HUM && policyMember.Product__c == MEDICARE_POLICY && (UtilityHelper_H_AHT_HUM.isUserHavingMedicareCustomerServiceAccess()|| bCCSPDPPharmacyPilot)){
                            	    policywrap.sPreselect = 'selectMEDpolicy';
                                 
                                }
							    // For REQ 937903 
                                else if(((iNoOfMEDpolicy == 1) || (iNoOfMEDpolicy == 0 && iNoOfFuturePolicy == 1)) && policyMember.Product__c == MEDICARE_POLICY && (bPharmacyUser)){
                                    policywrap.sPreselect = 'selectMEDpolicy';
                                 
                                }
                                else{
                                    policywrap.sPreselect = 'Policies';
                                }  
                                policywrap.hasHumanaProfileAccess = true;
                                if(policyMember.Member__r.recordType.Name == ConsumerManagementConstants_HUM.AGNOSTICCUSTOMER)
                                    policywrap.hasHumanaProfileAccess = (profileName == ConsumerManagementConstants_HUM.PHARMACYSPECIALIST_PROFILE_HUM) ? true : false;                              
                                Boolean OSBONOFF= HUMUtilityHelper.isCRMFunctionalityON('738134');
                                Boolean canProceed = HUMUtilityHelper.deduceOSB(OSBONOFF, PolicyMember.OSB_Indicator__c);
                                if(canProceed){
                                    lstPolicyAccessWrapper.add(policywrap);
	                            }
                            }
                        }
                    }
                }
            }             
        } catch(Exception e) {
            HUMExceptionHelper.logErrors(e, 'MemberActivePolicies_C_HUM', 'determinePolicyAccess');
        }    
    }
}