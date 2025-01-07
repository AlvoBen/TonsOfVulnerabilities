/*******************************************************************************************************************************************************
Apex Class Name  : PhoneBook_LD_HUM
Version          : 1.0
Created Date     : 03/09/2021
Function         : This data layer class contains methods to get policy and Interaction details
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            		 Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                              03/09/2021                 Original Version
* Arpit Jain                                             06/25/2021                 Methods added to get policy and Interaction Details to be sent to savaCallData webservice
* Arpit Jain                                             02/15/2022                 US-2564501 - Check IVR interaction for inbound calls and create if not found
* Harshada Kamble                                        08/16/2022                 US-3701398 - T1PRJ0272347: Dev for IOP-2022-5960542 & INC1860844
* Harshada Kamble                                        01/05/2023                 US 3849706,3944555 : updating interaction origin as Inbound call in case of Watson or System interaction clone. 
* Harshada Kamble                                        01/05/2023                 US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)
* Harshada Kamble                                        02/03/2023                 US 3980680: T1PRJ0036776: RxE - Save Campaign name and keycode to CRM interactions
* Anil Pavithran                                         02/03/2023                 US 3979946: T1PRJ0036776: PCC VOC Survey Transfer Identification 
* Santhi Mandava                                         02/06/2013                 User Story 4082261,4084543: Display interaction information on search page and account detail page.
* Santhi Mandava                                         04/14/2013                 User Story 4404843: Consumer/Create interaction onclick of "With" & "About" button separately then directly clicks on FN/LN Genesys Known member
* Harshada Kamble/Anil Pavithran                         05/05/2023                 US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
* Kiran Kumar Kotni                                      05/10/2023                 User Story 4420291: T1PRJ0036776: Change 2 Field Names - Correct RxE implementation & Provider Survey Checkbox
* Mayur Pardeshi                                         06/16/2023				    User Story 4734084: T1PRJ0036776: PCC Lightning - Second Member Information Is Not Being Updated on 'Interacting About' field from Watson in a Multimember auth call (Genesys)
* Harshada Kamble                                        06/22/2023                 User Story 4707880: T1PRJ0036776: Lightning - Multi-member transfer call handling - Provider (Genesys)
* Mayur Pardeshi                                         07/05/2023				    User Story 4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
* Kiran Kotni                                            08/04/2023                 User Story 4863232: T1PRJ0036776: RxE - Lightning - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
* Harshada Kamble                                        01/25/2024                 User Story 5602717: T1PRJ0036776: Pass NPI & Tax ID from Watson IVR to Genesys to CRM Salesforce Service
* Robert Crispen										 02/12/2024                 Copado Hyperforce Testing
**********************************************************************************************************************************************/
public without sharing class PhoneBook_LD_HUM {
    
    private static set<string> setMemberIDTypes = new set<string>{PhoneBookConstants_H_HUM.MEDICAREID_HUM,PhoneBookConstants_H_HUM.SSN_HUM,PhoneBookConstants_H_HUM.MEMBERIDBASE_HUM,PhoneBookConstants_H_HUM.MEDICADEID_HUM,PhoneBookConstants_H_HUM.UNKNOWN_MEMBER_MEDICAIDID, PhoneBookConstants_H_HUM.CBIS_ALT_ID};
    /**
    * Returns void
    * Method to insert record into Integration_Mapping__c object when call transfer button is clicked
    *
    * @param  Object callTransferData
    * @return void
    */
    @AuraEnabled
    public static void insertIntegrationmapping(Object callTransferData)
    {
        Map<String,String> PolicyCaseDataMap = new Map<String,String>();
		String searchDataString = JSON.serialize(callTransferData);
        Map<String, object> searchDataMap =   (Map<String, object>) JSON.deserializeUntyped(searchDataString);
		String accId = (String)searchDataMap.get('accId') != '' ? (String)searchDataMap.get('accId') : null;
        String memberPlanID = (String)searchDataMap.get('MemberPlanId') != '' ? (String)searchDataMap.get('MemberPlanId') : null;
        String uuid = (String)searchDataMap.get('uuid');
        Integration_Mapping__c intMapping = new Integration_Mapping__c();
        String sMapping = Label.HUMSPhoneMappingObjInteraction + ':' + null + ';' + Label.HUMSPhoneMappingObjInqFor + ':';
        sMapping += null + ';' + Label.HUMSPhoneMappingObjInqAb + ':' + accId + ';'; 
        sMapping += Label.HUMSPhoneMappingObjPolicyMember + ':' + memberPlanID;
		
        if(Schema.sObjectType.Integration_Mapping__c.fields.Mapping__c.isCreateable()){intMapping.Mapping__c = sMapping;}
        if(Schema.sObjectType.Integration_Mapping__c.fields.Record_ID__c.isCreateable()){intMapping.Record_ID__c = uuid;}
        insert intMapping;
    }

    /**
    * Returns Map of the interaction details
    * Method to get the latest interactions details(Interacting With and Interacting About) from the UUID
    *
    * @param  String UUID
    * @return Map < String, String >
    */
    public static Map < String, String > getInteractionSaveCallDataFields(String UUID) 
    {
        Map < String, String > interactionsavecallmapping = new Map < String, String > ();

        List<Interaction__c> lstInteractionRec = new List<Interaction__c>();
        Account intWithAcc = null;
        Account intAboutAcc = null;
        string sIntWithMemberId;
        string intAbtId;
        Map<String,String> mapSoftPhone = new Map<String,String>();

        try
        {
            /* US4707880: added below code for making member gen key 0 by default in case of call transfer
            and get interacting about details on click of call transfer buton from account detail page
            */
            if(HUMUtilityHelper.isCRMFunctionalityON('4707880'))
            {
                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY,'0');
                mapSoftPhone = HUMWithoutSharingQueryHelper.getMapping(UUID);
                
                if(mapSoftPhone != null)
                {
                    if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInqAb) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb).containsIgnoreCase(Label.HUMNullValue))
                    {
                        intAboutAcc = HUMWithoutSharingQueryHelper.getAccount(mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb));
                        intAbtId = intAboutAcc.Id;
                    }
                }
            }
            // US4707880: added below code for taking only selcted interacting about interaction details in case of call transfer   
            if(HUMUtilityHelper.isCRMFunctionalityON('4707880'))
            {
                if(intAbtId != null  && intAbtId !='')
                {
                    lstInteractionRec= HUMWithoutSharingQueryHelper.fetchCurrSelectedIntAbt(uuid, intAbtId);
                }
                else{
                    lstInteractionRec= HUMWithoutSharingQueryHelper.fetchInteractionwithItsMembers(UUID);
                }
            }
            else
            {
                lstInteractionRec= HUMWithoutSharingQueryHelper.fetchInteractionwithItsMembers(UUID);
            }
            if (lstInteractionRec.size() > 0) 
            {
                String sinteractionWithId = lstInteractionRec[0].Interacting_With__c;
                String sInteractingWithtyp = lstInteractionRec[0].Interacting_With_type__c;
                // US4707880: removed below code for adding Auth_IND based on Interacting with in case of call transfer
                if(!HUMUtilityHelper.isCRMFunctionalityON('4707880'))
                {
                    if(lstInteractionRec[0].Authenticated__c == true)
                    {
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.AUTHENTICATION_IND, 'Y');
                    }
                    else
                    {
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.AUTHENTICATION_IND, 'N');
                    }
                }

                if (!String.isEmpty(lstInteractionRec[0].Interacting_With__c)) 
                {
                    switch on sInteractingWithtyp {
                        when 'Member' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, lstInteractionRec[0].Interacting_With__r.FirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.LastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, lstInteractionRec[0].Interacting_With__r.Birthdate__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, PhoneBookConstants_H_HUM.M_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, '');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.M_VAL);
                            // US4707880: Added below code for adding INQR_ID based on Interacting with in case of call transfer
                            if(HUMUtilityHelper.isCRMFunctionalityON('4707880'))
                            {
                                intWithAcc =HUMWithoutSharingQueryHelper.getAccount(lstInteractionRec[0].Interacting_With__c);
                                sIntWithMemberId =  setInquiryId(intWithAcc);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,sIntWithMemberId);
                            }
                        }
                        when 'Group' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, lstInteractionRec[0].Interacting_With__r.Group_Number__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, PhoneBookConstants_H_HUM.E_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, '');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.E_VAL);
                        }
                        when 'Provider' {
                            List < ConsumerID__c > taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sinteractionWithId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, (taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, PhoneBookConstants_H_HUM.P_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.P_VAL);
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,lstInteractionRec[0].Interacting_With__r.NPI_ID__c);
                            }
                        }
                        when 'Agent' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, lstInteractionRec[0].Interacting_With__r.agent_ID__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, PhoneBookConstants_H_HUM.B_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, '');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.B_VAL);
                        }
                        when 'Unknown-Member' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, lstInteractionRec[0].Interacting_With__r.FirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.LastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, lstInteractionRec[0].Interacting_With__r.Birthdate__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, 'U');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, '');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                        }
                        when 'Unknown-Group' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, lstInteractionRec[0].Interacting_With__r.Group_Number__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, 'U');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, '');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                        }
                        when 'Unknown-Provider' {
                            List < ConsumerID__c > taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sinteractionWithId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, (taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, 'U');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, '');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                        }
                        when 'Unknown-Agent' {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Interacting_With__r.name);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID, lstInteractionRec[0].Interacting_With__r.agent_ID__c);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, 'U');
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST, '');
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB, '');
                                //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                                if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                                {
                                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                                }
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                        }
                        when 'Other' {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                            }
                        when else {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.M_VAL);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.M_VAL);
                            }
                    }
                } 
                else {
                    if (!String.isEmpty(lstInteractionRec[0].Caller_Name__c) && lstInteractionRec[0].Caller_Name__c != null) 
                    {
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST, lstInteractionRec[0].Caller_Name__c);
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,'');
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                        if(sInteractingWithtyp != '' && sInteractingWithtyp !=null)
                        {
                            switch on sInteractingWithtyp
                            {
                                    when 'Other' {                                    
                                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                                    }
                                    when else {                                    
                                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.M_VAL);
                                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.M_VAL);
                                    }
                            }
                        }
                    }
                }

                if (lstInteractionRec[0].Interaction_Members__r.size() > 0) 
                {
                    String sIntAboutMemberId;
                    boolean sIntAboutAuth;
                    String sInteractingAboutId = lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__c;
                    String sInteractingAboutType = lstInteractionRec[0].Interaction_Members__r[0].Interacting_About_Type__c;

                    // US4707880: Added below code for adding AUTH_IND based on Interacting about in case of call transfer
                    if(HUMUtilityHelper.isCRMFunctionalityON('4707880'))
                    {
                        sIntAboutAuth = lstInteractionRec[0].Interaction_Members__r[0].Authenticated__c;

                        if(sIntAboutAuth)
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'Y');
                        }
                        else
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'N');
                        }
                    }
                    
                    switch on sInteractingAboutType {
                        when 'Member' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.FirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.LastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Birthdate__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, PhoneBookConstants_H_HUM.M_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,'');
                            // US4696940: Added below code for adding INQA_ID and gen key based on Interacting about in case of call transfer
                            if(HUMUtilityHelper.isCRMFunctionalityON('4707880'))
                            { 
                                if(intAboutAcc != null && (intAboutAcc.Id == sInteractingAboutId))
                                {
                                    sIntAboutMemberId =  setInquiryId(intAboutAcc);
                                }
                                else
                                {
                                    intAboutAcc =HUMWithoutSharingQueryHelper.getAccount(sInteractingAboutId);
                                    sIntAboutMemberId =  setInquiryId(intAboutAcc);
                                }

                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,sIntAboutMemberId);
                                if(intAboutAcc != null && intAboutAcc.Mbr_Gen_Key__c != null)
                                {
                                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY,intAboutAcc.Mbr_Gen_Key__c);  
                                }
                            }
                        }
                        when 'Group' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Group_Number__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, PhoneBookConstants_H_HUM.E_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                        }
                        when 'Provider' {
                            List < ConsumerID__c > taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sInteractingAboutId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, (taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, PhoneBookConstants_H_HUM.P_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                        }
                        when 'Agent' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.agent_ID__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, PhoneBookConstants_H_HUM.B_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                        }
                        when 'Unknown-Member' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.FirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.LastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Birthdate__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, 'U');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,'');
                        }
                        when 'Unknown-Group' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Group_Number__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, 'U');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                        }
                        when 'Unknown-Provider' {
                            List < ConsumerID__c > taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sInteractingAboutId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, (taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, 'U');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                        }
                        when 'Unknown-Agent' {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.name);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.agent_ID__c);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, 'U');
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                        }
                    }
                }
                else{
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST, '');
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID, '');
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST, '');
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, '');
                    
                    // US4707880: Added below code for adding AUTH_IND based on Interacting with in case of call transfer if about not added
                    if(HUMUtilityHelper.isCRMFunctionalityON('4707880'))
                    {
                        if(lstInteractionRec[0].Authenticated__c == true){
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.AUTHENTICATION_IND, 'Y');
                        }
                        else{
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.AUTHENTICATION_IND, 'N');
                        }
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY,'0');
                    }
                }
            }
        
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LD_HUM', 'getInteractionSaveCallDataFields');
        }
        return interactionsavecallmapping;
    }

    /**
    * Returns Map of the policy details 
    * Method to get the policy data to be sent to saveCallData webservice on Consult/Blind transfer button click
    *
    * @param  String UUID
    * @return Map < String, String >
    */
    @AuraEnabled
    public static Map < String, String > getCallTransferData(String UUID)
    {
    Map < String, String > callDataListMap = new Map < String, String > ();
        SoftPhoneCallTransfer_LH_HUM getCallTransferData;
        Map < String, String > mapSoftPhone = new Map < String, String > ();

        if (!String.isEmpty(UUID)) {
            List < Integration_Mapping__c > lstsoftphoneCallRouting = new List < Integration_Mapping__c > ();
            String sQry = 'SELECT Mapping__c FROM Integration_Mapping__c WHERE Record_ID__c = :UUID ORDER BY CreatedDate DESC Limit 1';
            lstsoftphoneCallRouting = Database.query(sQry);
            if (!lstsoftphoneCallRouting.isEmpty()) {
                if (lstsoftphoneCallRouting[0] != null) {
                    if (lstsoftphoneCallRouting[0].Mapping__c != null) {
                        if (lstsoftphoneCallRouting[0].Mapping__c.contains(system.label.HUMSPhoneMappingObj_semicolon) && lstsoftphoneCallRouting[0].Mapping__c.contains(system.label.HUMSPhoneMappingObj_colon)) {
                            for (String s: lstsoftphoneCallRouting[0].Mapping__c.split(system.label.HUMSPhoneMappingObj_semicolon)) {
                                if (s.contains(system.label.HUMSPhoneMappingObj_colon)) {
                                    List < String > lstTemp = s.split(system.label.HUMSPhoneMappingObj_colon);
                                    if (lstTemp.size() > 0) {
                                        mapSoftPhone.put(lstTemp[0], lstTemp[1] != null ? lstTemp[1] : '');
                                    }
                                }
                            }
                        }
                    }
                }
                if (mapSoftPhone.size() > 0) {
                    getCallTransferData = SoftPhoneCallTransfer_LS_HUM.createResponse(mapSoftPhone);
                    if (getCallTransferData.ASOIndicator != null) {
                        callDataListMap.put(PhoneBookConstants_H_HUM.ASO_IND_VAL, getCallTransferData.ASOIndicator);
                    }
                    if (getCallTransferData.BusinessSegmentIndicator != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.BUS_SEGMENT_IND_VAL, getCallTransferData.BusinessSegmentIndicator);
                    }
                    if (getCallTransferData.HdphIndicator != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.HDHP_IND_VAL, getCallTransferData.HdphIndicator);
                    }
                    if (getCallTransferData.InquiredAboutDOB != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, getCallTransferData.InquiredAboutDOB);
                    }
                    if (getCallTransferData.InquiredAboutGroupId != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL, getCallTransferData.InquiredAboutGroupId);
                    }
                    if (getCallTransferData.InquiredAboutPlatformCode != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD_VAL, getCallTransferData.InquiredAboutPlatformCode);
                    }
                    if (getCallTransferData.MajorLineOfBusiness != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.MAJOR_LOB_VAL, getCallTransferData.MajorLineOfBusiness);
                    }
                    if (getCallTransferData.MemberId != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.MEMBER_ID_VAL, getCallTransferData.MemberId);
                    }
                    if (getCallTransferData.MtvBusinessLevel5 != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.MTV_BL5_VAL, getCallTransferData.MtvBusinessLevel5);
                    }
                    if (getCallTransferData.MtvBusinessLevel7 != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.MTV_BL7_VAL, getCallTransferData.MtvBusinessLevel7);
                    }
                    if (getCallTransferData.ProductTypeCode != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.PROD_TYPE_CODE_VAL, getCallTransferData.ProductTypeCode);
                    }
                    if (getCallTransferData.SellingLedger != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.SELLING_LEDGER_VAL, getCallTransferData.SellingLedger);
                    }
                    if (getCallTransferData.StateOfIssue != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.STATE_OF_ISSUE_VAL, getCallTransferData.StateOfIssue);
                    }
                    if (getCallTransferData.VENDOR_ASOFDATE != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.VENDOR_ASOFDATE_VAL, getCallTransferData.VENDOR_ASOFDATE);
                    }
                    if (getCallTransferData.VENDOR_SUB_GROUPID != null) {

                        callDataListMap.put(PhoneBookConstants_H_HUM.VENDOR_SUB_GROUPID_VAL, getCallTransferData.VENDOR_SUB_GROUPID);
                    }
                }
            }
        }
    return callDataListMap;
}
    /**
    * Returns Interacting About Account Id 
    * <p>
    * Method to get the Interacting About Account Id when transfered call in received
    *
    * @param  String UUID
    * @return String
    */
    @AuraEnabled
    public static Map<String,String> getAboutAccountId(String UUID,Boolean isrefresh,String authIndicator,String genesysInteractionId,Object searchModal, String calllogattributes){
        Map<String,String> mapIntAccountInfo = new Map<String,String>();
        Map<String,String> mapIntAccountInfoRes = new Map<String,String>();
        
        String aboutId = '';
        Interaction__c clonedInteraction;
        List<Interaction_Member__c> intMembersLst = new List<Interaction_Member__c>();
        List<Interaction__c> lstInteractionRec = new List<Interaction__c>();
        if(fetchSwitchs().get('Switch_4461937') != false){
            lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c FROM Interaction_Members__r order by Name ASC) FROM Interaction__c where UUID__c =: UUID order by Name desc limit 1];
        }
        else{
            lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c FROM Interaction_Members__r order by CreatedDate) FROM Interaction__c where UUID__c =: UUID order by Name desc limit 1];
        }
        String searchDataString = JSON.serialize(searchModal);
        if (lstInteractionRec.size() > 0)
        {
            Interaction__c oInt = lstInteractionRec[0];
             if(isrefresh){
                mapIntAccountInfo.put('sIntId',oInt.Id);
            }
            if(!isrefresh)
            {
                clonedInteraction = oInt.clone(false,true,false,false);
                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){clonedInteraction.Authenticated__c = authIndicator == 'Y'?true:false;}
                if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){clonedInteraction.Genesys_Interaction_ID__c = genesysInteractionId;}
                //US 3849706,3944555 : updating interaction origin as Inbound call in case of Watson or System interaction clone.  
                if(fetchSwitchs().get('Switch_3849706') != false)
                {
                    if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){clonedInteraction.Interaction_Origin__c = PhoneBookConstants_H_HUM.Inbound_Call;}
                    if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){clonedInteraction.Genesys_Interaction_ID__c = genesysInteractionId;}                
                }
                if(fetchSwitchs().get(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitchLD) != false)
                {
                    if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes)) {
                        if(Schema.sObjectType.Interaction__c.fields.Genesys_Data__c.isCreateable()){
                            clonedInteraction.Genesys_Data__c = calllogattributes;
                        }
                    }
                }
                //US 4863232 - T1PRJ0036776: RxE - Lightning - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
                if(fetchSwitchs().get('Switch_4863232')!=false){
                    if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes)) {
                        if(Schema.sObjectType.Interaction__c.fields.Campaign_name_and_Keycode__c.isCreateable()){
                            clonedInteraction.Campaign_name_and_Keycode__c = getKeycodeCampaignNameFormat(calllogattributes);
                        }
                    }
                }
                if(fetchSwitchs().get('Switch_2704639') != false)
                {
                    if(Schema.sObjectType.Interaction__c.fields.Call_data__c.isCreateable()){
                    	clonedInteraction.Call_data__c = (searchDataString != null && searchDataString.length() > 2500) ? '' : searchDataString;
                    }
                }
                //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
                if(fetchSwitchs().get('Switch_4461937') != true){
                    insert clonedInteraction;
                    mapIntAccountInfo.put('sIntId',clonedInteraction.Id);
                }
            }   
            //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
            if(fetchSwitchs().get('Switch_4461937') != false)
            {
                mapIntAccountInfoRes = getCloneInteraction(clonedInteraction, isrefresh, searchModal);  
                mapIntAccountInfo = mapIntAccountInfoRes;
            }
            else
            {
                if (oInt.Interaction_Members__r.size() > 0)
                {
                    if(!isrefresh)
                    {
                        for(Interaction_Member__c eachMember:oInt.Interaction_Members__r )
                        {
                            Interaction_Member__c clonedIntMember = eachMember.clone(false,true,false,false);
                            clonedIntMember.Interaction__c = clonedInteraction.id;
                            intMembersLst.add(clonedIntMember);
                        }
                        insert intMembersLst;
                    }
                    aboutId = oInt.Interaction_Members__r[0].Interacting_About__c;
                }
            }

        }
        //US 3944531 : UCID not mapping to UUID field in Salesforce CRM (INC2009416)- if interaction creation failed for Agent 1 then added below logic for handling interaction creation for transferred call 
        else if(fetchSwitchs().get('Switch_3944531') != false){
                String sIntId = createAutoInteraction(searchModal, UUID, isrefresh, genesysInteractionId,calllogattributes);
                mapIntAccountInfo.put('sIntId',sIntId);
        }
        if(fetchSwitchs().get('Switch_4461937') != true)
        {
            mapIntAccountInfo.put('aboutId',aboutId);
        }

        return mapIntAccountInfo;
    }
    /**
    * Returns Switches information 
    * <p>
    * Method to get the Switches information
    *
    * @return Map<String,Boolean>
    */
    @AuraEnabled
    public static Map<String,Boolean> fetchSwitchs() 
    {
        Map<String,Boolean> switchMap = new Map<String,Boolean>();
		List<CRMFunctionality_ONOFF_Switch__c> switchList = new List<CRMFunctionality_ONOFF_Switch__c>();
            for(CRMFunctionality_ONOFF_Switch__c OnOffswitch : [SELECT Id, Name, IsON__c FROM CRMFunctionality_ONOFF_Switch__c WHERE Name IN: PhoneBookConstants_H_HUM.SWITCH_LIST])
            {
                switchMap.put('Switch_'+OnOffswitch.Name, OnOffswitch.IsOn__c);
            }
        return switchMap;
    }
    /**
    * Returns map of Secure Payment type and secure Flow id from custom settings
    * <p>
    * Method to get the secure Payment Types
    *
    * @return Map<String,String>
    */
    public static Map<String,String> getSecurePaymentsDetails()
    {
        Map<String,String> securePaymentsMap = new Map<String,String>();
            List<SecurePayments_HUM__c> allSecurePayment = SecurePayments_HUM__c.getall().values();
            for(SecurePayments_HUM__c sp : allSecurePayment){
                if(sp.IsLightning__c == true){
                    securePaymentsMap.put(sp.SecurePaymentName__c , sp.FlowId__c);
                }
            }
        return securePaymentsMap;
    }
    /**
    * Returns MemberPlan details of the focused MemberPlan page
    * <p>
    * Method to get the MemberPlan details
    *
    * @return MemberPlan
    */
    public static MemberPlan getMemberPlanRecord( String recId)
    {
        MemberPlan oMemPlan;
        List<MemberPlan> lstMemberPlan = [SELECT Id, Name,MemberId, Member_Dependent_Code__c,Member.Enterprise_Id__c, Member_Id_Base__c, SubscriberPlanId__c,SubscriberPlanId__r.Member_Id_Base__c,SubscriberPlanId__r.Name,Policy_Platform__c FROM MemberPlan where Id=:recId];
        if(lstMemberPlan.size() > 0) 
        {
            oMemPlan = lstMemberPlan[0];
        } 
        return oMemPlan;
    }
    
    /**
    * Returns interaction Id of the auto created interaction
    * <p>
    * Method to create auto interaction
    *
    * @return interaction Id
    */
    @AuraEnabled
    public static String createAutoInteraction(Object searchModal,String UUID,Boolean isrefresh,String genesysInteractionId, String calllogattributes)
    {
        String interactionId = '';
        Interaction__c clonedInteraction;
        List<Interaction_Member__c> intMembersLst = new List<Interaction_Member__c>();
        Interaction__c inc = new Interaction__c();
        Interaction__c clonedAgentInteraction = new Interaction__c();
        List<Interaction__c> interactionLst = new List<Interaction__c>();
        Interaction_Member__c incMember = new Interaction_Member__c();
        String searchDataString = JSON.serialize(searchModal);
        Map<String, object> searchDataMap =   (Map<String, object>) JSON.deserializeUntyped(searchDataString);
        String INQR_TYP = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) : '';
        String INQR_ID = '';
        Boolean isWithAboutSame = false;
        Boolean isCallerTypehandled = false;
        Boolean InteractingWithAccountIdNotfound =false;
        Boolean isAboutId = false;
        Map<String,String> mapIntAccountInfo = new Map<String,String>();

        if((String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_ID) != null && (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_ID) !='' )
        {
        INQR_ID = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_ID);
        }
        else if((String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL)==PhoneBookConstants_H_HUM.G_VAL || (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL)==PhoneBookConstants_H_HUM.E_VAL )
        {
        if((String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL) !=null && (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL) !='')
        INQR_ID = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL);
        }
        String INQR_FIRST = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_FIRST) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_FIRST) : '';
        String INQR_LAST = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_LAST) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_LAST) : '';
        String INQR_DOB = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_DOB) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_DOB) : '';
        String INQA_TYP = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) : '';
        String INQA_ID = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_ID) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_ID) : '';
        String INQA_FIRST = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_FIRST) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_FIRST) : '';
        String INQA_LAST = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_LAST) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_LAST) : '';
        String INQA_DOB = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL) : '';
        Boolean AUTHENTICATION_IND = searchDataMap.get(PhoneBookConstants_H_HUM.Authentication_IND) !=null ? (searchDataMap.get(PhoneBookConstants_H_HUM.Authentication_IND) == 'Y'?true:false) : false;
        String SURVEY_ID = (String)searchDataMap.get(PhoneBookConstants_H_HUM.SURVEY_ID) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.SURVEY_ID) : '';
        String Call_Catg_Desc = (String)searchDataMap.get(PhoneBookConstants_H_HUM.Call_Catg_Desc) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.Call_Catg_Desc) : '';
        String MEMBER_PERS_GEN_KEY = (String)searchDataMap.get(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY) : '';
        String DependentCode = (String)searchDataMap.get(PhoneBookConstants_H_HUM.DependentCode) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.DependentCode) : '';
        String INQA_PLTFRM_CD = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD) : '';
        String INQR_PLTFRM_CD = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_PLTFRM_CD) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_PLTFRM_CD) : '';
        String INQR_GEN_KEY = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_GEN_KEY) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_GEN_KEY) : '';
        String INQR_NPI = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_NPI) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_NPI) : '';
        

        //US4461937- For Authenticated Member call, if only “With” specified and "About" not specified or "About Type" is specified as "M/S" then set the “About” to the same as the With 
        if(fetchSwitchs().get('Switch_4461937') != false)
        {  
            if(AUTHENTICATION_IND)
            {
                if(string.isNotBlank(INQR_TYP) && string.isNotBlank(INQR_ID) && string.isBlank(INQA_TYP)){
                    String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.INTERACTING_ABOUT_CALLER_TYP_NOT_FOUND_LOG, new string[]{UUID, INQR_TYP}), 'PhoneBook_C_HUM', 'createAutoInteraction','CustomException','Integration Error');
                }
                if(string.isNotBlank(INQR_TYP) && (INQR_TYP.equalsIgnoreCase(PhoneBookConstants_H_HUM.M_VAL) || INQR_TYP.equalsIgnoreCase(PhoneBookConstants_H_HUM.S_VAL)))
                {
                    if((string.isBlank(INQA_TYP)||(string.isNotBlank(INQA_TYP) && (INQA_TYP.equalsIgnoreCase(PhoneBookConstants_H_HUM.M_VAL) 
                    || INQA_TYP.equalsIgnoreCase(PhoneBookConstants_H_HUM.S_VAL)))) 
                    && string.isBlank(INQA_ID) && string.isBlank(INQA_FIRST) && string.isBlank(INQA_LAST) && string.isBlank(INQA_DOB))
                    {
                        if(string.isBlank(INQA_TYP))
                            INQA_TYP='M';

                        INQR_GEN_KEY = MEMBER_PERS_GEN_KEY;
                        isWithAboutSame =true; 
                    }
                }
            }
        }

        //US 3944531 : UCID not mapping to UUID field in Salesforce CRM (INC2009416)
        if(fetchSwitchs().get('Switch_3944531') != false)
        {
            //If InteractingAbout and InteractingWith are same then assign boolean flag to avoid duplicate calls to query
            if(AUTHENTICATION_IND && (string.isNotBlank(INQA_TYP) && string.isNotBlank(INQR_TYP) && (INQR_TYP.equalsIgnoreCase(INQA_TYP) || INQR_TYP.equalsIgnoreCase(PhoneBookConstants_H_HUM.M_VAL) || 
            INQR_TYP.equalsIgnoreCase(PhoneBookConstants_H_HUM.S_VAL))) && 
                (string.isNotBlank(INQA_ID) && string.isNotBlank(INQR_ID) && INQR_ID.equalsIgnoreCase(INQA_ID)) &&
                (string.isNotBlank(INQA_FIRST) && string.isNotBlank(INQR_FIRST) && INQR_FIRST.equalsIgnoreCase(INQA_FIRST)) &&
                (string.isNotBlank(INQA_LAST) && string.isNotBlank(INQR_LAST) && INQR_LAST.equalsIgnoreCase(INQA_LAST)) &&
                (string.isNotBlank(INQA_DOB) && string.isNotBlank(INQR_DOB) && INQR_DOB.equalsIgnoreCase(INQA_DOB)))
            {
                    INQR_GEN_KEY = MEMBER_PERS_GEN_KEY;
                    isWithAboutSame =true;
            }
        }
        
        List<Interaction__c> lstInteractionRec = new List<Interaction__c>();
        if(fetchSwitchs().get('Switch_4734084') == false){
            if(fetchSwitchs().get('Switch_4461937') != false){
            lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c FROM Interaction_Members__r order by Name ASC) FROM Interaction__c where UUID__c =: UUID order by Name desc limit 1];
        }
        else{
            lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c,Authenticated__c, Interaction__c  FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where UUID__c =: UUID order by Name desc limit 1];
        }
        }
        if (lstInteractionRec.size() > 0) {
            Interaction__c oInt = lstInteractionRec[0];
            //US 3849706,3944555 : updating interaction origin as Inbound call in case of Watson or System interaction clone.  
            if(fetchSwitchs().get('Switch_3849706') != false)
            {
                if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){oInt.Interaction_Origin__c = PhoneBookConstants_H_HUM.Inbound_Call;}
                if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){oInt.Genesys_Interaction_ID__c = genesysInteractionId;}
            }
            if(fetchSwitchs().get(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitchLD) != false)
            {
                if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes))
                {
                    if(Schema.sObjectType.Interaction__c.fields.Genesys_Data__c.isCreateable()){oInt.Genesys_Data__c = calllogattributes;}
                }
                    
            }
            //US 4863232 : T1PRJ0036776: RxE - Lightning - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
            if(fetchSwitchs().get('Switch_4863232')!=false){
                if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes)) {
                    if(Schema.sObjectType.Interaction__c.fields.Campaign_name_and_Keycode__c.isCreateable()){
                        oInt.Campaign_name_and_Keycode__c = getKeycodeCampaignNameFormat(calllogattributes);
                    }
                }  
            }
            if(!isrefresh)
            {
                //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
                if(fetchSwitchs().get('Switch_4461937') != false)
                {
                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){oInt.Authenticated__c = AUTHENTICATION_IND;}
                    mapIntAccountInfo = getCloneInteraction(oInt, isrefresh, searchModal);  
                    interactionId = mapIntAccountInfo.get('sIntId');
                }
                else{
                    clonedInteraction = oInt.clone(false,true,false,false);
                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){clonedInteraction.Authenticated__c = AUTHENTICATION_IND;}
                    insert clonedInteraction;
                    interactionId = (String)clonedInteraction.Id;
                    if (oInt.Interaction_Members__r.size() > 0) 
                    {
                        for(Interaction_Member__c eachMember:oInt.Interaction_Members__r )
                        {
                            Interaction_Member__c clonedIntMember = eachMember.clone(false,true,false,false);
                            clonedIntMember.Interaction__c = clonedInteraction.id;
                            intMembersLst.add(clonedIntMember);
                        }
                        insert intMembersLst;
                    }
                }
            }
            else{
                interactionId = oInt.Id;
            }
        }
        else{
            if(!isrefresh)
            {
                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = AUTHENTICATION_IND;}
                if(Schema.sObjectType.Interaction__c.fields.Survey_ID__c.isCreateable()){inc.Survey_ID__c = SURVEY_ID;}
                if(Schema.sObjectType.Interaction__c.fields.Call_Category_Description__c.isCreateable()){inc.Call_Category_Description__c = Call_Catg_Desc;}
                if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){inc.Genesys_Interaction_ID__c = genesysInteractionId;}
                
                if(Schema.sObjectType.Interaction__c.fields.uuid__c.isCreateable()){inc.uuid__c = UUID;}
                if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){inc.Interaction_Origin__c = 'Inbound Call';}
                if(fetchSwitchs().get(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitchLD) != false)
                {
                    if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes))
                        if(Schema.sObjectType.Interaction__c.fields.Genesys_Data__c.isCreateable()){inc.Genesys_Data__c = calllogattributes;}
                }
                //US 4863232 - T1PRJ0036776: RxE - Lightning - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
                if(fetchSwitchs().get('Switch_4863232')!=false){
                    if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes))
                        if(Schema.sObjectType.Interaction__c.fields.Campaign_name_and_Keycode__c.isCreateable()){inc.Campaign_name_and_Keycode__c = getKeycodeCampaignNameFormat(calllogattributes);}
                }
                if(fetchSwitchs().get('Switch_2704639') != false) {
                        if(Schema.sObjectType.Interaction__c.fields.Call_data__c.isCreateable()){inc.Call_data__c = (searchDataString != null && searchDataString.length() > 2500) ? '' : searchDataString;}
                }
                                
                if (INQR_TYP != '') {
                    Boolean isAccountId = false;
                    switch on INQR_TYP {
                        when 'M','S' {
                            isCallerTypehandled =true;
                            String AccountId = '';
                            if(fetchSwitchs().get('Switch_3701398') != false) 
                                AccountId = getCallMemberAccountDetails(INQR_ID, DependentCode, INQR_GEN_KEY, INQR_FIRST, INQR_LAST, INQR_PLTFRM_CD, AUTHENTICATION_IND);
                            else
                                AccountId = getMemberWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);

                            if (AccountId != '') {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Member';}
                                isAccountId = true;
                            }
                        }
                        when 'P','PPI' {
                            isCallerTypehandled =true;
                            String AccountId = getProviderWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB, INQR_NPI);
                            if (AccountId !='') {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Provider';}
                                isAccountId = true;
                            }
                        }
                        when 'A','B'{
                            isCallerTypehandled =true;
                           //US4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
                            if(fetchSwitchs().get('Switch_4599393') == true){
                            String AccountId =getAgentAccountId(INQR_ID);
							if (AccountId !=  '') {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Agent';}
                                isAccountId = true;	
                                    }
                       		 }  
                            else{
                            String AccountId = getAgentWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);  
                            if (AccountId !=  '') {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Agent';}
                                isAccountId = true;
                            }
                            }
                        }
                        when 'G','E' {
                            isCallerTypehandled =true;
                            String AccountId = getGroupWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                            if (AccountId !='') {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Group';}
                                isAccountId = true;
                            }
                        }
                    }
                    if(!isAccountId){
                        if(fetchSwitchs().get('Switch_3944531') != false)
                        {
                            InteractingWithAccountIdNotfound = true;
                            if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                            isAccountId= true;
                        }
                        else{
                            if(fetchSwitchs().get('Switch_2895841') != false){
                                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                            }
                            if(fetchSwitchs().get('Switch_3701398') != false) 
                            {
                                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                                isAccountId = true;  
                            }
                            else
                            {
                                if((INQR_FIRST != '' || INQR_LAST != '')) {
                                    if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQR_FIRST +' '+ INQR_LAST;}
                                    isAccountId = true;
                                }
                                else if(INQA_TYP == INQR_TYP  && (INQA_FIRST != '' || INQA_LAST != '')) {
                                    if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQA_FIRST +' '+ INQA_LAST;}
                                    isAccountId = true;
                                }
                                else if(AUTHENTICATION_IND == false){
                                    isAccountId = true;                            
                                }
                            }
                        }
                    }
                    //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
                    if(INQA_TYP !=''){
                        switch on INQA_TYP {
                            when 'M','S' {

                                String AboutId = '';
                                if(fetchSwitchs().get('Switch_3701398') != false){
                                    if (isWithAboutSame && inc.Interacting_With__c != null) {
                                        if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){AboutId = inc.Interacting_With__c;}
                                    }
                                    else
                                        AboutId = getCallMemberAccountDetails(INQA_ID, DependentCode, MEMBER_PERS_GEN_KEY, INQA_FIRST, INQA_LAST, INQA_PLTFRM_CD, AUTHENTICATION_IND);
                                }
                                else
                                    AboutId = getMemberAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);

                                if (AboutId != '') {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = AboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Member';}
                                    isAboutId = true;
                                }
                            }
                            when 'P','PPI' {
                                String aboutId = getProviderAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                                if (aboutId !='') {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = aboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Provider';}
                                    isAboutId = true;
                                }
                            }
                            when 'A','B'{
                                String aboutId= getAgentAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                                if (aboutId !='') {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = aboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Agent';}
                                    isAboutId = true;
                                }
                            }
                            when 'G','E' {
                                String aboutId=getGroupAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                                if (aboutId !='') {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = aboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Group';}
                                    isAboutId = true;
                                }
                            }
                        }
                    }
                    if(isAccountId){
                        try{
                            if(fetchSwitchs().get('Switch_2876026') != false){
                                interactionLst.add(inc);
                                clonedAgentInteraction = inc.clone(false,true,false,false);
                                interactionLst.add(clonedAgentInteraction);
                                insert interactionLst;
                                interactionId = interactionLst.get(1).Id;
                            }
                            else {
                                if(fetchSwitchs().get('Switch_4461937') != false)
                                {
                                    if(inc.Authenticated__c ==true && (inc.Interacting_with__C == null || incMember.interacting_about__C == null))
                                    {
                                        if(Schema.sObjectType.Interaction__c.fields.Authenticated__C.isCreateable()){inc.Authenticated__C =false;}
                                    }
                                    insert inc;
                                    // if(Schema.sObjectType.Interaction__c.fields.Id.isCreateable()){interactionId = inc.Id;}
                                    interactionId = inc.Id;
                                }
                                else{
                                    insert inc;
                                    // if(Schema.sObjectType.Interaction__c.fields.Id.isCreateable()){interactionId = inc.Id;}
                                    interactionId = inc.Id;
                                }
                            }
                            if(fetchSwitchs().get('Switch_3944531') != false && AUTHENTICATION_IND == true)
                            {
                                if(!isCallerTypehandled)
                                    String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.CALLER_TYPE_NOT_HANDLED_LOG, new string[]{UUID, INQR_TYP}), 'PhoneBook_LD_HUM', 'createAutoInteraction','CustomException','Integration Error');

                                if(InteractingWithAccountIdNotfound && isCallerTypehandled)
                                    String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.INTERACTING_WITH_ACCOUNT_ID_NOT_FOUND_LOG, new string[]{UUID, INQR_TYP}), 'PhoneBook_LD_HUM', 'createAutoInteraction','CustomException','Integration Error');
                            }
                        }
                        catch(Exception e){
                            if(fetchSwitchs().get('Switch_3944531') != false)
                            {
                                e.setMessage(string.format(PhoneBookConstants_H_HUM.CREATE_AUTO_INTERACTION_FAIL_LOG, new string[]{UUID, e.getMessage()}));
                                HUMExceptionHelper.logErrors(e,'Phonebook_LD_HUM', 'createAutoInteraction');
                                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = null;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = '';}
                            }
                            else 
                            {
                                HUMExceptionHelper.logErrors(e,'Phonebook_LD_HUM', 'createAutoInteraction');
                                if(fetchSwitchs().get('Switch_2895841') != false)
                                {
                                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                                }
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = null;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = '';}
                               
                                if(fetchSwitchs().get('Switch_3701398') != true)
                                {
                                    if(INQR_FIRST != '' || INQR_LAST != '') 
                                    {
                                        if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQR_FIRST +' '+ INQR_LAST;}
                                    }
                                    else if(INQA_TYP == INQR_TYP  && (INQA_FIRST != '' || INQA_LAST != '')) 
                                    {
                                        if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQR_FIRST +' '+ INQR_LAST;}
                                    }
                                }
                                else{
                                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__C.isCreateable()){inc.Authenticated__C =false;}
                                }
                            }
                            if(fetchSwitchs().get('Switch_2876026') != false)
                            {
                                interactionLst = new List<Interaction__c>();
                                interactionLst.add(inc);
                                clonedAgentInteraction = inc.clone(false,true,false,false);
                                interactionLst.add(clonedAgentInteraction);
                                List<Database.saveresult> srList = Database.insert(interactionLst);
                                if(srList[1].isSuccess())
                                {
                                    interactionId = srList[1].getId();
                                }
                            }
                            else
                            {
                                //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
                                if(fetchSwitchs().get('Switch_4461937') != false)
                                {
                                    if(inc.Authenticated__c ==true && (inc.Interacting_with__C == null || incMember.interacting_about__C == null))
                                    {
                                        if(Schema.sObjectType.Interaction__c.fields.Authenticated__C.isCreateable()){inc.Authenticated__C =false;}
                                    }
                                    Database.saveresult sr  = Database.insert(inc);
                                    if(sr.isSuccess()) 
                                    {
                                        interactionId = sr.getId();
                                    }
                                }
                                else{
                                    Database.saveresult sr  = Database.insert(inc);
                                    if(sr.isSuccess()) 
                                    {
                                        interactionId = sr.getId();
                                    }
                                }
                            }
                        }
                    }
                    if(interactionId != ''){
                        if(isAboutId){
                            try{
                                if(fetchSwitchs().get('Switch_2876026') != false)
                                {
                                    incMember.Interaction__c = interactionLst.get(0).Id;
                                    intMembersLst.add(incMember);
                                    Interaction_Member__c clonedAgentIntMember = incMember.clone(false,true,false,false);
                                    clonedAgentIntMember.Interaction__c = interactionLst.get(1).Id;
                                    intMembersLst.add(clonedAgentIntMember);
                                    insert intMembersLst;
                                }
                                else{
                                    //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
                                    if(fetchSwitchs().get('Switch_4461937') != false)
                                    {
                                        if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){incMember.Authenticated__C =true;}
                                        if(incMember.Authenticated__c ==true && (inc.Interacting_with__C == null || incMember.interacting_about__C == null))
                                        {
                                            if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){incMember.Authenticated__C =false;}
                                        }
                                        if(Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()){incMember.Interaction__c = inc.Id;}
                                        insert incMember; 
                                    }
                                    else
                                    {
                                        if(Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()){incMember.Interaction__c = inc.Id;}
                                        insert incMember;
                                    }
                                }
                            }
                            Catch(Exception e)
                            {
                                if(fetchSwitchs().get('Switch_3944531') != false)
                                {
                                    e.setMessage(string.format(PhoneBookConstants_H_HUM.CREATE_AUTO_INTERACTION_MEMBER_FAIL_LOG, new string[]{UUID, e.getMessage()}));
                                    HUMExceptionHelper.logErrors(e,'Phonebook_LD_HUM', 'createAutoInteraction');
                                }
                                else{
                                    HUMExceptionHelper.logErrors(e,'Phonebook_LD_HUM', 'createAutoInteraction');
                                }
                            }
                        }
                        if(fetchSwitchs().get('Switch_3944531') != false)
                        {
                            if(AUTHENTICATION_IND == true && !isAboutId)
                                String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.INTERACTING_ABOUT_ACCOUNT_ID_NOT_FOUND_LOG, new string[]{UUID, INQA_TYP}), 'PhoneBook_LD_HUM', 'createAutoInteraction','CustomException','Integration Error');
                        }
                    }
                }
                else{
                    try{
                        if(fetchSwitchs().get('Switch_2876026') != false)
                        {
                            interactionLst.add(inc);
                            clonedAgentInteraction = inc.clone(false,true,false,false);
                            interactionLst.add(clonedAgentInteraction);
                            insert interactionLst;
                            interactionId = interactionLst.get(1).Id;
                        }
                        else{
                            //US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
                            if(fetchSwitchs().get('Switch_4461937') != false)
                            {
                                if(inc.Authenticated__c ==true && (inc.Interacting_with__C == null || incMember.interacting_about__C == null))
                                {
                                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__C.isCreateable()){inc.Authenticated__C =false;}
                                }
                                
                                insert inc;
                                interactionId = inc.Id;
                            }
                            else{
                                insert inc;
                                interactionId = inc.Id;
                            }
                        }
                    }
                    Catch(Exception e)
                    {
                        if(fetchSwitchs().get('Switch_3944531') != false)
                        {
                            e.setMessage(string.format(PhoneBookConstants_H_HUM.CREATE_AUTO_INTERACTION_FAIL_LOG, new string[]{UUID, e.getMessage()}));
                            HUMExceptionHelper.logErrors(e,'Phonebook_LD_HUM', 'createAutoInteraction');
                        }
                        else {
                            HUMExceptionHelper.logErrors(e,'Phonebook_LD_HUM', 'createAutoInteraction');
                        }
                    }
                }
            }
        }
        return interactionId;
    }
    public static String getMemberWithAccountId(String INQR_ID,String INQR_FIRST,String INQR_LAST,String INQR_DOB,String INQA_TYP,String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String AccountId = '';
        String sFirstName ='' ;
        String sLastName ='';
        String sBirthdate='';
        String tempAccountId='';
        
        if ( INQR_ID != '') {
            List<List<sObject>> allAccountIdx = [FIND :INQR_ID IN ALL FIELDS RETURNING MemberPlan(Member_Id_Base__c,MemberId,Member.FirstName,Member.LastName where ETL_Record_Deleted__c = false)];
            MemberPlan[] AccountIdx = (MemberPlan[])allAccountIdx[0];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AccountId = AccountIdx[0].MemberId;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(MemberPlan memPlan : AccountIdx){
                        if(INQR_FIRST != '' && INQR_LAST != '' && memPlan.Member.FirstName == INQR_FIRST && memPlan.Member.LastName == INQR_LAST && memPlan.Member_Id_Base__c == INQR_ID)
                        {
                            AccountId = memPlan.MemberId;
                            break;
                        }
                        else if(memPlan.Member_Id_Base__c == INQR_ID){
                            tempAccountId = memPlan.MemberId;
                            accountIds.add(memPlan.MemberId);
                        }
                    }
                    if(AccountId == '' && accountIds.size() == 1){
                        AccountId = tempAccountId;
                    }
                }
            }
        }
        else if(INQR_FIRST != '' || INQR_LAST != '' ) {
            sFirstName = INQR_FIRST ;
            sLastName = INQR_LAST ;
            sBirthdate = (INQR_DOB).replaceAll('-', '/');
            String Name = sFirstName +' '+ sLastName;
            List<List<sObject>> searchList = [FIND :Name IN Name FIELDS RETURNING Account(Name,Birthdate__c)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
            else{
                for (Account a : searchAccounts) {
                    if((a.Birthdate__c).equals(sBirthdate)) AccountId = a.Id;
                }
            }
        }
        else if (INQA_TYP == 'M') {
            AccountId = getMemberAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        }
        return AccountId;
    }
    
    public static String getProviderWithAccountId(String INQR_ID,String INQR_FIRST,String INQR_LAST,String INQR_DOB,String INQA_TYP,String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB, string INQR_NPI){ 
        String taxId ='';
        String AccountId='';
        String sFirstName='';
        String sLastName='';

        //US-5602717 Added below changes to find interacting with record id based on tax id and npi combination if available in provider call data.
        if(HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
        {
            if(INQR_NPI !='' && INQR_ID !='')
            {
                List < ConsumerID__c > acclist = [SELECT Id, Account__r.RecordType.Name, Account__c, Consumer_ID__c FROM ConsumerID__c where Consumer_ID__c =: INQR_ID AND Account__r.NPI_ID__c =: INQR_NPI];
                if(acclist.size() > 0 && acclist.size() == 1)
                {
                    AccountId = acclist[0].Account__c;
                }
                else
                {
                    List < ConsumerID__c > acc = [SELECT Id, Account__r.RecordType.Name, Account__c, Consumer_ID__c FROM ConsumerID__c where Account__r.NPI_ID__c =: INQR_NPI];
                    if(acc.size() > 0 && acc.size() == 1)
                    {
                        AccountId = acc[0].Account__c;
                    }
                }
            }
        }
        
        if ( INQR_ID != '' && AccountId == '') {
            List < ConsumerID__c > AccountIdx = [SELECT Id, Account__r.RecordType.Name, Account__c, Consumer_ID__c FROM ConsumerID__c where Consumer_ID__c =: INQR_ID];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AccountId = AccountIdx[0].Account__c;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(ConsumerID__c con : AccountIdx){
                        accountIds.add(con.Account__c);
                    }
                    if(accountIds.size() == 1){
                        AccountId = AccountIdx[0].Account__c;
                    }
                    else if(INQR_LAST != ''){
                        sFirstName = INQR_FIRST ;
                        sLastName = INQR_LAST ;
                        List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id where Id IN :accountIds)];
                        Account[] searchAccounts = (Account[])searchList[0];
                        if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
                    }
                    else if(fetchSwitchs().get('Switch_4734084') == true){
                        //US-5602717 if call data yields multiple Results in CRM, then the "Interacting With" will be blank. 
                        if(!HUMUtilityHelper.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                        {
                            AccountId = getProviderAccountId(AccountIdx);
                        }
                    }
                }
            }
        } 
        else if(  INQR_LAST != '' ) {
            sFirstName = INQR_FIRST ;
            sLastName = INQR_LAST ;
            List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
        }
        else if (INQA_TYP == 'P') {
            AccountId = getProviderAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        }
        return AccountId;
    }
    public static String getAgentWithAccountId(String INQR_ID,String INQR_FIRST,String INQR_LAST,String INQR_DOB,String INQA_TYP,String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String agentId ='';
        String AccountId='';
        String sFirstName='';
        String sLastName='';
        if (INQR_ID != '') {
            List < Account > AccountIdx = [SELECT Id FROM Account where agent_ID__c =: INQR_ID];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AccountId = AccountIdx[0].Id;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(Account con : AccountIdx){
                        accountIds.add(con.Id);
                    }
                    if(accountIds.size() == 1){
                        AccountId = AccountIdx[0].Id;
                    }
                    else if(INQR_LAST != ''){
                        sFirstName = INQR_FIRST ;
                        sLastName = INQR_LAST ;
                        List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id where Id IN :accountIds)];
                        Account[] searchAccounts = (Account[])searchList[0];
                        if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
                    }
                }
            }
        } 
        else if(INQR_LAST != ''){
            sFirstName = INQR_FIRST ;
            sLastName = INQR_LAST ;
            List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
        }
        else if (INQA_TYP == 'A' || INQA_TYP == 'B') {
            AccountId = getAgentAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        } 
        return AccountId;
    }
    public static String getGroupWithAccountId(String INQR_ID,String INQR_FIRST,String INQR_LAST,String INQR_DOB,String INQA_TYP,String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String groupId='';
        String AccountId='';
        String sFirstName='';
        String sLastName='';
        
        if ( INQR_ID != '') {
            List < Account > AccountIdx = [SELECT Id FROM Account where Group_Number__c =: INQR_ID];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AccountId = AccountIdx[0].Id;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(Account con : AccountIdx){
                        accountIds.add(con.Id);
                    }
                    if(accountIds.size() == 1){
                        AccountId = AccountIdx[0].Id;
                    }
                    else if(INQR_LAST != ''){
                        sFirstName = INQR_FIRST ;
                        sLastName = INQR_LAST ;
                        List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id where Id IN :accountIds)];
                        Account[] searchAccounts = (Account[])searchList[0];
                        if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
                    }
                }
            }
        } 
        else if(INQR_LAST !=''){
            sFirstName = INQR_FIRST ;
            sLastName = INQR_LAST ;
            List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) AccountId = searchAccounts[0].Id;
        }
        else if (INQA_TYP == 'G' || INQA_TYP == 'E') {
            AccountId = getGroupAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        }
        return AccountId;
    }
    public static String getMemberAboutAccountId(String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String AboutId='';
        String sFirstName='' ;
        String sLastName='' ;
        String sBirthdate='';
        String tempAboutId='';
        if (INQA_ID != '') {
            List<List<sObject>> allAccountIdx = [FIND :INQA_ID IN ALL FIELDS RETURNING MemberPlan(Member_Id_Base__c,MemberId,Member.FirstName,Member.LastName where ETL_Record_Deleted__c = false)];
            MemberPlan[] AccountIdx = (MemberPlan[])allAccountIdx[0];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AboutId = AccountIdx[0].MemberId;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(MemberPlan memPlan : AccountIdx){
                        if(INQA_FIRST != '' && INQA_LAST != '' && memPlan.Member.FirstName == INQA_FIRST && memPlan.Member.LastName == INQA_LAST && memPlan.Member_Id_Base__c == INQA_ID)
                        {
                            AboutId = memPlan.MemberId;
                            break;
                        }
                        else if(memPlan.Member_Id_Base__c == INQA_ID){
                            tempAboutId = memPlan.MemberId;
                            accountIds.add(memPlan.MemberId);
                        }
                    }
                    if(AboutId == '' && accountIds.size() == 1){
                        AboutId = tempAboutId;
                    }
                }
            }
        } else {
            if(INQA_LAST !='' || INQA_FIRST !='')
            {
                sFirstName = INQA_FIRST ;
                sLastName = INQA_LAST ;
                sBirthdate = (INQA_DOB).replaceAll('-', '/');
                String Name = sFirstName +' '+ sLastName;
                List<List<sObject>> searchList = [FIND :Name IN Name FIELDS RETURNING Account(Name,Birthdate__c)];
                Account[] searchAccounts = (Account[])searchList[0];
                if(searchAccounts.size() == 1) AboutId = searchAccounts[0].Id;
                else 
                {
                    for (Account a : searchAccounts) {
                        if((a.Birthdate__c).equals(sBirthdate)) AboutId = a.Id;
                    }
                }
            }
        }
        return AboutId;
    }
    
    public static String getProviderAboutAccountId(String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String taxId ='';
        String aboutId='';
        String sFirstName='';
        String sLastName='';
        if( INQA_ID != ''){
            List < ConsumerID__c > AccountIdx = [SELECT Id, Account__r.RecordType.Name, Account__c, Consumer_ID__c FROM ConsumerID__c where Consumer_ID__c =: INQA_ID];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    aboutId = AccountIdx[0].Account__c;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(ConsumerID__c con : AccountIdx){
                        accountIds.add(con.Account__c);
                    }
                    if(accountIds.size() == 1){
                        aboutId = AccountIdx[0].Account__c;
                    }
                    else if(INQA_LAST != ''){
                        sFirstName = INQA_FIRST ;
                        sLastName = INQA_LAST ;
                        List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id where Id IN :accountIds)];
                        Account[] searchAccounts = (Account[])searchList[0];
                        if(searchAccounts.size() == 1) aboutId = searchAccounts[0].Id;
                    }
                    else if(fetchSwitchs().get('Switch_4734084') == true){
                        aboutId = getProviderAccountId(AccountIdx);
                    }
                }
            }
        }
        else {
            if(INQA_LAST !='')
            {
            sFirstName = INQA_FIRST ;
            sLastName = INQA_LAST ;
            List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) aboutId = searchAccounts[0].Id;
            }
        }
        return aboutId;
    }
    public static String getAgentAboutAccountId(String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String agentId ='';
        String aboutId='';
        String sFirstName='';
        String sLastName='';
        if(INQA_ID !=''){
            List < Account > AccountIdx = [SELECT Id FROM Account where agent_ID__c =: INQA_ID];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    aboutId = AccountIdx[0].Id;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(Account con : AccountIdx){
                        accountIds.add(con.Id);
                    }
                    if(accountIds.size() == 1){
                        aboutId = AccountIdx[0].Id;
                    }
                    else if(INQA_LAST != ''){
                        sFirstName = INQA_FIRST ;
                        sLastName = INQA_LAST ;
                        List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id where Id IN :accountIds)];
                        Account[] searchAccounts = (Account[])searchList[0];
                        if(searchAccounts.size() == 1) aboutId = searchAccounts[0].Id;
                    }
                }
            }
        }
        else {
            if(INQA_LAST !='')
            {
            sFirstName = INQA_FIRST ;
            sLastName = INQA_LAST ;
            List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) aboutId = searchAccounts[0].Id;
            }
        }
        return aboutId;
    }
    public static String getGroupAboutAccountId(String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String groupId='';
        String aboutId='';
        String sFirstName='';
        String sLastName='';
        if(INQA_ID  !=''){
            List < Account > AccountIdx = [SELECT Id FROM Account where Group_Number__c =: INQA_ID];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    aboutId = AccountIdx[0].Id;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(Account con : AccountIdx){
                        accountIds.add(con.Id);
                    }
                    if(accountIds.size() == 1){
                        aboutId = AccountIdx[0].Id;
                    }
                    else if(INQA_LAST != ''){
                        sFirstName = INQA_FIRST ;
                        sLastName = INQA_LAST ;
                        List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id where Id IN :accountIds)];
                        Account[] searchAccounts = (Account[])searchList[0];
                        if(searchAccounts.size() == 1) aboutId = searchAccounts[0].Id;
                    }
                }
            }
        }
        else {
            if(INQA_LAST !='')
            {
            sFirstName = INQA_FIRST ;
            sLastName = INQA_LAST ;
            List<List<sObject>> searchList = [FIND :sLastName IN Name FIELDS RETURNING Account(Name,Id)];
            Account[] searchAccounts = (Account[])searchList[0];
            if(searchAccounts.size() == 1) aboutId = searchAccounts[0].Id;
            }
        }
        return aboutId;
    }

    /**
    * Returns Map of AccountId and RecordType
    * <p>
    * Method to get AccountId and RecordType
    *
    * @return Map<String,String>
    */
    @AuraEnabled
    public static Map<String,String> getaccountWithRecordType( String recId , String obj)
    {
        Map<String,String> accIdwithrecordType = new Map<String,String>();
        String accId = '';
        String accRecordType = '';
        if(obj == 'Account'){
            accId = recId;
        }
        else if(obj == 'MemberPlan'){
            List<MemberPlan> lstMemberPlan = [SELECT Id, Name,MemberId, Member_Dependent_Code__c,Member.Enterprise_Id__c, Member_Id_Base__c, SubscriberPlanId__c,SubscriberPlanId__r.Member_Id_Base__c,SubscriberPlanId__r.Name,Policy_Platform__c FROM MemberPlan where Id=:recId];
            if(lstMemberPlan.size() > 0) 
            {
                accId = lstMemberPlan[0].MemberId;
            } 
        }
        if(accId != ''){
            List<Account> accList = [SELECT Id, LastName, FirstName, RecordType.Name FROM Account where Id = :accId];
            accIdwithrecordType.put(accId,accList[0].RecordType.Name);
        }
        return accIdwithrecordType;
    }
    
    /**
    * Returns InteractionId of the interaction created for OutBoundCall
    * <p>
    * Method to create the interaction on OutBoundCall
    *
    * @return InteractionId
    */
    public static String createInteraction(String uuid,String interactingWith,String genInteractionId,String interactingWithType,String origin)
    {
        Interaction__c oInt = new Interaction__c();
        if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){oInt.Interacting_With__c = interactingWith;}
        if(Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()){oInt.Interacting_With_type__c = interactingWithType;}
        if(Schema.sObjectType.Interaction__c.fields.UUID__c.isCreateable()){oInt.UUID__c = uuid;}
        if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){oInt.Genesys_Interaction_ID__c=genInteractionId;}
        if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){oInt.Interaction_Origin__c = origin;}
        insert oInt;
        return oInt.Id;
    }
    
    public static String createInteractionMember(Interaction_Member__c oIntmem)
    {
        insert oIntmem;
        return oIntmem.Id; 
    }

    /**
    * Returns String
    * <p>
    * Method to get Cloned Interactionn AboutId
    *
    * @return String
    */
    @AuraEnabled
    public static Map<String,String> getClonedInteractionAboutId(String UUID)
    {
        String aboutId = '';
        Map<String,String> mapIntAccountInfo = new Map<String,String>();
        List < Interaction__c > lstInteractionRec = [SELECT Id, Caller_Name__c,Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Interaction__c FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where UUID__c =: UUID AND createdById =: UserInfo.getUserId() order by Name desc limit 1];
        if(!lstInteractionRec.isEmpty())  mapIntAccountInfo.put('sIntId',lstInteractionRec[0].Id);
        if (lstInteractionRec[0].Interaction_Members__r.size() > 0) 
        {
            aboutId = lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__c;
             mapIntAccountInfo.put('aboutId',aboutId);
        }
    return mapIntAccountInfo;
    }

    /*
    * getCallMemberAccountDetails
    * <p>
    * This method is used to get member Account details for InteractingWith and Interacting About for Interaction 
    * @returntype string
    * @param    accountId, memberGenKey, accountFirstName, accountLastName, IsAuthenticate
    */
    @AuraEnabled
    public static string getCallMemberAccountDetails(string accountId, string acctDepCode, string memberGenKey, string accountFirstName, string accountLastName, string platformCd, boolean IsAuthenticate)
    {
        map<string,string> mapinteraction = new map<string,string>();
        boolean isMemberFound = false;
        string memberAccountId='';
        
        //If the call is authenticated then GenKey will be passed and value will be retrieved based on Genkey
        if(isAuthenticate && string.isNotBlank(memberGenKey) && long.valueOf(memberGenKey) !=0)
        {
            List<Account> lstAcct = [SELECT Id, Mbr_Gen_Key__c,RecordTypeId, RecordType.Name , LastName, FirstName from Account where Mbr_Gen_Key__c = :memberGenKey and ETL_Record_Deleted__c = false limit 1];
            if(null != lstAcct && !lstAcct.isEmpty())
            {
                isMemberFound = true;
                memberAccountId = lstAcct[0].Id;
            }
        }

        //If Member is not found based on Genkey the Member record will be retrieved based on MemberId 
        if(isAuthenticate && !isMemberFound)
        {
            if(string.isNotBlank(accountId))
            { 
                memberAccountId = getCallMemberRecords(accountId, acctDepCode, accountFirstName, accountLastName, platformCd, IsAuthenticate);
            }
        }
        return memberAccountId;
    }

    /*
    * getCallMemberRecords
    * <p>
    * This method is used to get Member records based on MemberId from SOSL query for InteractingWith and Interacting About for Interaction 
    * @returntype string memAccountId
    * @param    accountId, accountFirstName, accountLastName
    */
    public static String getCallMemberRecords(string accountId, string acctDepCode, string accountFirstName, string accountLastName, string platformCd, boolean IsAuthenticate)
    {
        list<Member_Id__c> lstMemberRecs = new list<Member_Id__c>(); 
        string memDependCode;
        string memberID;
        integer iSearchLimit = 20;
        String memAccountId ='';
        boolean bSwitch_2854267 =  HUMUtilityHelper.isCRMFunctionalityON('2854267');
        if(bSwitch_2854267)
        {
            if(string.isNotBlank(platformCd) && platformCd.equalsIgnoreCase(PhoneBookConstants_H_HUM.CB_PLATFORM_CD))
            {
                memDependCode  = acctDepCode;
                memberID = accountId;
            }
            else
            {
                if(!IsAuthenticate)
                {
                    memberID = accountId;
                    memDependCode = acctDepCode;
                }
                else
                {
                    if(accountId.length() > 9 && accountId.length() < 13 && (accountId.substring(0, 1).equalsIgnoreCase('H') || accountId.isNumeric()))
                    {
                        memDependCode = accountId.substring(9,accountId.length());
                        memberID = accountId.substring(0,(accountId.length()-2));
                    }
                    else
                    {
                        memberID = accountId;
                        memDependCode = acctDepCode;
                    }
                }
            }
        }
        else
        {
            //If AccountId passed is more than 10 then it consists of MemberId base and dependent code so need to retrieve seperate to hit query
            if(accountId.length() > 10)
            {
                memDependCode = accountId.substring(9,accountId.length());
                memberID = accountId.substring(0,(accountId.length()-2));
            }
            else
            {
                memberID = accountId;
            }
        }

        string sMemIdQuery = 'FIND \'' + string.escapeSingleQuotes(memberID) + '\' IN NAME FIELDS RETURNING Member_Id__c(Name,Member_Dependent_Code__c ,'+
                            'Policy_Member__r.Member__r.Id, Policy_Member__r.ETL_Record_Deleted__c,Policy_Member__r.Member__c, Policy_Member__r.Member__r.FirstName, '+
                            'Policy_Member__r.Member__r.LastName, Policy_Member__r.Member__r.RecordTypeId, Policy_Member__r.Member__r.RecordType.Name  Where Type__c in :setMemberIDTypes and Policy_Member__r.ETL_Record_Deleted__c = false ';
    

        if(String.isNotBlank(memDependCode))
        {
            sMemIdQuery += ' AND Member_Dependent_Code__c = \'' + string.escapeSingleQuotes(memDependCode) + '\' Limit ' + iSearchLimit+')';
        }
        else
        {
            sMemIdQuery += ' Limit ' + iSearchLimit+')';
        }
        //Retrieve the Member ID records    
        list<list<Member_Id__c>> searchList = search.query(sMemIdQuery);
        list<Member_Id__c> lstMembers = ((List<Member_Id__c>)searchList[0]);
        if(lstMembers != null && !lstMembers.isEmpty())
        {
            for(Member_Id__c obj : lstMembers)
            {
                if(memberID.startsWithIgnoreCase(obj.Name))
                {
                    //If FirstName and LastName is passed the value is matched to find correct record
                    if(string.isNotBlank(accountFirstName) && string.isNotBlank(accountLastName))
                    {
                        if(accountFirstName.trim().equalsIgnoreCase((obj.Policy_Member__r.Member__r.FirstName).trim()) &&
                            accountLastName.trim().equalsIgnoreCase((obj.Policy_Member__r.Member__r.LastName).trim()))
                        {
                            lstMemberRecs.add(obj);
                            break;      
                        }
                    }
                    else
                    {
                        lstMemberRecs.add(obj);
                    }
                }
            }
        }

        if(null != lstMemberRecs && !lstMemberRecs.isEmpty() && lstMemberRecs.size() == 1)
        {
            memAccountId = lstMemberRecs[0].Policy_Member__r.Member__c; 
        }

        return memAccountId;
    }

    /*
    * updateInteractionAttribute
    * <p>
    * This method used to save voc survey transfer value in intraction object
    * @returntype Boolean
    * @param  uuid
    */
    @AuraEnabled
    public Static Boolean updateInteractionAttribute(String uuid)
    {
        Boolean IntAttribute = false;
        try 
        {
            if(fetchSwitchs().get('Switch_3979946') != false){
                List<Interaction__c> lstInteractionReclatest = [SELECT Id, Transferred_to_Survey__c  FROM Interaction__c where UUID__c=: uuid AND Createdby.Id =: UserInfo.getUserId() order by Name desc Limit 1];
                if(lstInteractionReclatest.size()>0){
                  IntAttribute = true;
                  lstInteractionReclatest[0].Transferred_to_Survey__c = IntAttribute;
                  update lstInteractionReclatest;
                }
              }
              else{
                List<Interaction__c> lstInteractionReclatest = [SELECT Id,Interaction_Attributes__c  FROM Interaction__c where UUID__c=: uuid AND Createdby.Id =: UserInfo.getUserId() order by Name desc Limit 1];
                if(lstInteractionReclatest.size()>0){
                    IntAttribute = true;
                    lstInteractionReclatest[0].Interaction_Attributes__c = IntAttribute;
                    update lstInteractionReclatest;
                }
              }
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LD_HUM', 'updateInteractionAttribute');
        }
        return IntAttribute;
    }
    
    /*
    * cloneInteraction
    * <p>
    * US4461937- For cloning system interaction and transfer call interaction 
    * 1. If Inbound call is marked authenticated then both Interacting With and About are mandatory. If not, create unauth inetraction with provided data.
    * 2. For Authenticated Member call, if only “With” specified or "About Type" is specified as "M" then set the “About” to the same as the With 
    * 3. For Authenticated calls except Member call, if only “With” specified create an interaction and set just the Interacting With info. Set the Authenticated checkbox as false
    * @returntype Map<String,String> 
    * @param  Interaction__c,isRefreshedval, searchModal
    */
    public static Map<String,String>  getCloneInteraction(Interaction__c oInt, Boolean isRefreshedval, object searchModal)
    {
        String intId; 
        Boolean isTransferredCall= false;
        Map<String,String> mapIntAccountInfo = new Map<String,String>();

        String aboutId = '';
        string aboutIdVal ='';
        Interaction_Member__c incMember = new Interaction_Member__c();
        List<Interaction_Member__c> intMembersLst = new List<Interaction_Member__c>();
        Interaction__c clonedInteraction = new Interaction__c();
        String searchDataString = JSON.serialize(searchModal);
        Map<String, object> DataModal =   (Map<String, object>) JSON.deserializeUntyped(searchDataString);
        try
        {
            if(oInt.Interaction_Members__r.size() > 0)
            {
                //Added condition for legacy to Genesys transfer making 0th member as authenticated.
                if(DataModal.containsKey(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS) && (DataModal.get(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS)!= ''))
                {
                    if(oInt.Authenticated__c == true && oInt.Interacting_with__C != null &&  oInt.Interaction_Members__r[0].interacting_about__C != null){
                        oInt.Interaction_Members__r[0].Authenticated__C =true;
                    }
                }
                clonedInteraction = oInt.clone(false,true,false,false);
                for(Interaction_Member__c eachMember:oInt.Interaction_Members__r )
                {
                    if(!isRefreshedval)
                    {
                        Interaction_Member__c clonedIntMember = eachMember.clone(false,true,false,false);

                        if(clonedInteraction.Authenticated__c ==true && (clonedInteraction.Interacting_with__C == null || clonedIntMember.interacting_about__C == null))
                        {
                            if(Schema.sObjectType.Interaction__c.fields.Authenticated__C.isCreateable()){clonedInteraction.Authenticated__C =false;}
                            if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){clonedIntMember.Authenticated__C = false;}                           
                        }
                        if(DataModal != null){
                            //String INQR_TYP = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) : '';
                            if((DataModal.containsKey(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER) || (DataModal.containsKey(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS) && (DataModal.get(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS)!= ''))) )
                            {
                                isTransferredCall = true; 
                            }
                        }
                        if(!isTransferredCall && clonedInteraction.Authenticated__c == true && clonedInteraction.Interacting_with__C != null && clonedIntMember.interacting_about__C != null)
                        {
                            if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){clonedIntMember.Authenticated__C = false;}
                        }

                        intId = PhoneBook_D_HUM.insertInteraction(clonedInteraction);
                        mapIntAccountInfo.put('sIntId',intId);
                        
                        clonedIntMember.Interaction__c = clonedInteraction.id;
                        intMembersLst.add(clonedIntMember);
                    }
                    if(eachMember.Interacting_About__r.BirthDate__c == DataModal.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL)
                    && eachMember.Interacting_About__r.LastName == DataModal.get(PhoneBookConstants_H_HUM.INQA_LAST)
                    && eachMember.Interacting_About__r.FirstName == DataModal.get(PhoneBookConstants_H_HUM.INQA_FIRST)
                    && eachMember.Interacting_About_Type__c.subString(0,1) == DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL))
                    { 
                        aboutIdVal = eachMember.Interacting_About__c; 
                        mapIntAccountInfo.put('aboutId', aboutIdVal);
                    }
                    if(string.isEmpty(aboutIdVal) && string.isBlank(aboutIdVal))
                    {
                        aboutIdVal = eachMember.Interacting_About__c; 
                        mapIntAccountInfo.put('aboutId', aboutIdVal);
                    }
                }
                if(!isRefreshedval)
                {
                    PhoneBook_D_HUM.insertInteractionMember(intMembersLst);
                }
            }
            else{
                clonedInteraction = oInt.clone(false,true,false,false);

                if(clonedInteraction.Authenticated__c ==true && clonedInteraction.Interacting_with__C != null && 
                clonedInteraction.Interacting_With_Type__c =='Member' && incMember.interacting_about__C == null &&
                (incMember.interacting_about_type__C =='Member' || incMember.interacting_about_type__C == null))
                {
                    if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){incMember.Authenticated__C= false;}
                    if(Schema.sObjectType.Interaction_Member__c.fields.interacting_about__C.isCreateable()){incMember.interacting_about__C = clonedInteraction.Interacting_with__C;}
                    if(Schema.sObjectType.Interaction_Member__c.fields.interacting_about_type__C.isCreateable()){incMember.interacting_about_type__C = clonedInteraction.Interacting_With_Type__c;}
                    aboutIdVal = incMember.interacting_about__C; 
                    mapIntAccountInfo.put('aboutId',aboutIdVal);
                
                }
                else{
                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__C.isCreateable()){clonedInteraction.Authenticated__c =false;} 
                    if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){incMember.Authenticated__C= false;}
                }

                intId = PhoneBook_D_HUM.insertInteraction(clonedInteraction);
                mapIntAccountInfo.put('sIntId',intId);
                incMember.Interaction__c = clonedInteraction.id;
                intMembersLst.add(incMember);
                PhoneBook_D_HUM.insertInteractionMember(intMembersLst);
            }
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LD_HUM', 'getCloneInteraction');
        }
        return mapIntAccountInfo;
    }

    /*
     * Method Name : setInquiryId
     * Description : This Method is used to set Interacting With and About Ids.  
     * Return type : Id 
     * Paramater   : ID (Account)
     */
    public static String setInquiryId(Account oAcc)
    {
        String memberID;
        String sInquiredAboutId = (oAcc.policy_members__r.size()>0)? oAcc.policy_members__r[0].Name:null;
        if(sInquiredAboutId.length() > 9 && sInquiredAboutId.length() < 13 && (sInquiredAboutId.substring(0, 1).equalsIgnoreCase('H') || sInquiredAboutId.isNumeric()))
        {
            memberID = sInquiredAboutId.substring(0,(sInquiredAboutId.length()-2));
        }
        return memberID;
    }
    /*
     * Method Name : getProviderAccountId
     * Description : This Method is used to get the account id for the provider record.  
     * Return type : String 
     * Paramater   : List<ConsumerID__c>
    */
    public static String getProviderAccountId(List<ConsumerID__c> AccountIdx){
    String AccountId='';
        if(AccountIdx.size() >0 ){
            for(ConsumerID__c conID:AccountIdx){
                if(conID.Account__r.RecordType.Name =='Provider'){
                AccountId = conID.Account__c;
                break;     
                    }
            }
        }
    return AccountId;
    }
    /*
     * Method Name : getAgentAccountId
     * Description : This Method is used to upsert the account from Producer Service and return the accountID.  
     * Return type : String 
     * Paramater   : String
     * US4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
    */
    public static String getAgentAccountId(String INQR_ID){
        //if INQR_ID is blank or null then we are returning the blank.
        if(String.isBlank(INQR_ID) || INQR_ID==null){
           return '';
       }else {
        Map<string,Account> mapCallAccountToUpsert = new Map<string,Account>();
        Map<string,Account> mapCallFinalAccount = new Map<string,Account>();
        Map<String,Account> mapAgentDetails = new Map<String,Account>();
        List<Account> lstIntWithAccounts = new List<Account>();
        
            CreateAgentInteraction_H_HUM searchObj = new CreateAgentInteraction_H_HUM();
            searchObj.PrepareCallAgentAccountMap(INQR_ID);
            mapCallAccountToUpsert=CreateAgentInteraction_H_HUM.mapAccountToUpsert;
                    
            //If upsert record is stored in map then upsert and send the information
            if(null != mapCallAccountToUpsert && !mapCallAccountToUpsert.isEmpty())
            {
                List<Account> lstAccounts = mapCallAccountToUpsert.values();
                lstIntWithAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstAccounts);
                if(null!= lstIntWithAccounts && !lstIntWithAccounts.isEmpty())
                {
                    for(Account acct :lstIntWithAccounts)
                    {
                        mapCallFinalAccount.put(acct.Agent_ID__c,acct);
                    }
                }    
                mapAgentDetails.putAll(mapCallFinalAccount);    
            }
            String AccountId='';
            if(null != mapAgentDetails && mapAgentDetails.containsKey(INQR_ID))
            {
                Account oWithAccount = mapAgentDetails.get(INQR_ID);
                if(null != oWithAccount){
                    AccountId=oWithAccount.Id;   
                }  
            }
             return AccountId;
          }  
        }

    /*
     * Method Name   :    getKeycodeCampaignNameFormat
     * Description   :    US 4863232/4878631 -  Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
     *                    This method fetches the keycode and campaigname from callLogAttributes and return in keycode - campaignname format.
     * return Type   :    String
     * Parameters    :    String
     */
    public static String getKeycodeCampaignNameFormat(string callAttributes){
        string keycodeHypenCampaignNameFormat = '';
        try{
            Map<String,Object> callAttributesMap = (Map<String,Object>) JSON.deserializeUntyped(callAttributes);
            Boolean hasCampaignName = callAttributesMap.containsKey('campaignname');
            Boolean hasKeyCode = callAttributesMap.containsKey('keycode');
    
            if(hasCampaignName && hasKeyCode){
                keycodeHypenCampaignNameFormat = callAttributesMap.get('keycode') +' - '+callAttributesMap.get('campaignname');
            }else if(hasCampaignName){
                keycodeHypenCampaignNameFormat = String.valueOf(callAttributesMap.get('campaignname'));
            }else if(hasKeyCode){
                keycodeHypenCampaignNameFormat = String.valueOf(callAttributesMap.get('keycode'));
            }
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'PhoneBook_LD_HUM', 'getKeycodeCampaignNameFormat');
        }
        return keycodeHypenCampaignNameFormat;
    }
}