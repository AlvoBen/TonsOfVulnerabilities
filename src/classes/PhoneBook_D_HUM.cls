/***********************************************************************************************************************************
* Apex Class Name  : PhoneBook_D_HUM 
* Version          : 1.0
* Created Date     : 23rd June, 2021
* Function         : Data Layer class for PhoneBook development
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                                                     06/23/2021            Original Version
* Shreya Agrawal													07/13/2021            MF2- US-2357061  IVR End Call or Transfer – IVR Call Data End Agent Session
* Isha Gupta														07/16/2021            MF5- US-2230090  IVR Call Transfer passing Interaction Data
* Isha Gupta												        08/03/2021            US- 2484206  T1PRJ0036776_Phonebook Secure Payment UI Controls (E-post, Pharmacy)MF1
* Isha Gupta												        08/03/2021            US-2484212  T1PRJ0036776_Phonebook Secure Payment UI Controls(CBIS) MF1
* Shreya Agrawal/ Sahil Verma                                       02/18/2022            Modified code to fix defect#4469
* Harshada Kamble/Anil Pavithran                                    03/09/2023            US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
* Harshada Kamble                                                   06/22/2023            User Story 4696940: T1PRJ0036776: Classic - Multi-member transfer call handling
* Harshada Kamble                                                   01/25/2024            User Story 5602717: T1PRJ0036776: Pass NPI & Tax ID from Watson IVR to Genesys to CRM Salesforce Service
**********************************************************************************************************************************/
public with sharing class PhoneBook_D_HUM 
{
    /**
    * <p>
    * Method to create soft Phone Logs for events
    *
	* @param String callAction,String callInfoLog,String callLogType,String SPExtrnlId,String accountId,String uuidvalue
    * @return Void
    */
    public static void createSoftPhoneLog(String callAction,String callInfoLog,String callLogType,String SPExtrnlId,String accountId,String uuidvalue)
    {
        SoftPhone_Call_Log__c sphn = new SoftPhone_Call_Log__c();
        if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.Call_Action__c.isCreateable()){sphn.Call_Action__c = callAction;}
        if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.Call_Information_Log__c.isCreateable()){sphn.Call_Information_Log__c = callInfoLog;}
        if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.Call_Log_Type__c.isCreateable()){sphn.Call_Log_Type__c = callLogType;}
        if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.SP_External_Id__c.isCreateable()){sphn.SP_External_Id__c = SPExtrnlId;}
        if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.Agent_Id__c.isCreateable()){sphn.Agent_Id__c = UserInfo.getUserId();}
        if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.IVR_UUID__c.isCreateable()){sphn.IVR_UUID__c=uuidvalue;}
        if(accountId != '')
        {
            if(Schema.sObjectType.SoftPhone_Call_Log__c.fields.Account_Id__c.isCreateable()){sphn.Account_Id__c = accountId;}
        }        
        try
        {
            insert sphn;
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_D_HUM', 'createSoftPhoneLog');
        }
    }
    
    /**
    * <p>
    *  Method to Insert Interaction Mapping record
    * 
    * @param  Map<String,String> PolicyCaseDataMap
    */ 
    public static void insertIntegrationmapping(Map<String,String> PolicyCaseDataMap)
    {
        Integration_Mapping__c intMapping = new Integration_Mapping__c();
        try
        {
            if(PolicyCaseDataMap.size()>0){
                String sInteractionId = ((PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjInteraction)!=null)? PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjInteraction):'');
                String sInteractingWith = ((PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjInqFor)!=null)?PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjInqFor):'');
                String sInteractingAbt = ((PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjInqAb)!=null)?PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjInqAb):'');
                String sPolicyMemberId = ((PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjPolicyMember)!=null)?PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjPolicyMember):'');
                
            
                String sMapping = Label.HUMSPhoneMappingObjInteraction + ':' + sInteractionId + ';' + Label.HUMSPhoneMappingObjInqFor + ':';
                sMapping += sInteractingWith + ';' + Label.HUMSPhoneMappingObjInqAb + ':' + sInteractingAbt + ';'; 
                sMapping += Label.HUMSPhoneMappingObjPolicyMember + ':' + sPolicyMemberId ;
                
                List<Interaction__c> lstinter = [Select id,Interacting_With__r.Id,UUID__c from Interaction__c where id=:sInteractionId limit 1];
                
                if(!lstinter.isEmpty())
                {
                    if(Schema.sObjectType.Integration_Mapping__c.fields.Mapping__c.isCreateable()){intMapping.Mapping__c = sMapping;}
                    if(Schema.sObjectType.Integration_Mapping__c.fields.Record_ID__c.isCreateable()){intMapping.Record_ID__c = lstinter[0].UUID__c;}
                    insert intMapping;
                } 
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_D_HUM', 'insertIntegrationmapping');
        }
    }

    /**
* <p>
*  Method to extract all the interaction related information to be sent out to SaveCalldata service
* 
* @param  String uuidataValue 
* @return map<String,String>
*/ 
    public static map<String,String> getInteractionInfoForSaveCallData(String uuidataValue,String intAbtId)
    {
        Map<String,String> interactionsavecallmapping = new Map<String,String>();

        List<Interaction__c> lstInteractionRec = new List<Interaction__c>();
        Account intWithAcc = null;
        Account intAboutAcc = null;
        string sIntWithMemberId;
        Map<String,String> mapSoftPhone = new Map<String,String>();

        try
        {
            /* US4696940: added below code for making member gen key 0 by default in case of call transfer
            and get interacting about details on click of call transfer buton from account detail page
            */
            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
            {
                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY,'0');
                mapSoftPhone = HUMWithoutSharingQueryHelper.getMapping(uuidataValue);

                if(mapSoftPhone != null)
                {
                    if(mapSoftPhone.containsKey(Label.HUMSPhoneMappingObjInqAb) && !mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb).containsIgnoreCase(Label.HUMNullValue))
                    {
                        intAboutAcc = HUMWithoutSharingQueryHelper.getAccount(mapSoftPhone.get(Label.HUMSPhoneMappingObjInqAb));
                        intAbtId = intAboutAcc.Id;
                    }
                }
            }

            // US4696940: added below code for taking only selcted interacting about interaction details in case of call transfer
            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
            {
                if(intAbtId!=Null && intAbtId !='')
                {
                    lstInteractionRec=  HUMWithoutSharingQueryHelper.fetchCurrSelectedIntAbt(uuidataValue, intAbtId);
                }
                else{
                    lstInteractionRec= HUMWithoutSharingQueryHelper.fetchInteractionwithItsMembers(uuidataValue);
                }
            }
            else{
                lstInteractionRec= HUMWithoutSharingQueryHelper.fetchInteractionwithItsMembers(uuidataValue);
            }

            if (lstInteractionRec.size() > 0) 
            {
                String sinteractionWithId = lstInteractionRec[0].Interacting_With__c;
                String sInteractingWithtyp = lstInteractionRec[0].Interacting_With_type__c;

                // US4696940: removed below code for adding Auth_IND based on Interacting with in case of call transfer
                if(!PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
                {
                    if(lstInteractionRec[0].Authenticated__c == true)
                    {
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'Y');
                    }
                    else
                    {
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'N');
                    }
                }
                
                if (!String.isEmpty(lstInteractionRec[0].Interacting_With__c)) 
                {                    
                    switch on sInteractingWithtyp 
                    {
                        when 'Member' 
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,lstInteractionRec[0].Interacting_With__r.FirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.LastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,lstInteractionRec[0].Interacting_With__r.Birthdate__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.M_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.M_VAL);
                            }
                            // US4696940: Added below code for adding INQR_ID based on Interacting with in case of call transfer
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
                            {
                                intWithAcc =HUMWithoutSharingQueryHelper.getAccount(lstInteractionRec[0].Interacting_With__c);
                                sIntWithMemberId =  setInquiryId(intWithAcc);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,sIntWithMemberId);
                            }
                        }
                        when 'Group' 
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,lstInteractionRec[0].Interacting_With__r.Group_Number__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.E_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.E_VAL);
                            }
                        }
                        when 'Provider' 
                        {
                            List<ConsumerID__c> taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sinteractionWithId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,(taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.P_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.P_VAL);
                            }
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,lstInteractionRec[0].Interacting_With__r.NPI_ID__c);
                            }
                        }
                        when 'Agent' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,lstInteractionRec[0].Interacting_With__r.agent_ID__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.B_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.B_VAL);
                            }
                        }
                        when 'Unknown-Member' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,lstInteractionRec[0].Interacting_With__r.FirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.LastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,lstInteractionRec[0].Interacting_With__r.Birthdate__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                            }
                        }
                        when 'Unknown-Group' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,lstInteractionRec[0].Interacting_With__r.Group_Number__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                            }
                        }
                        when 'Unknown-Provider' {
                            List<ConsumerID__c> taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sinteractionWithId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,(taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                            }
                        }
                        when 'Unknown-Agent' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_LAST,lstInteractionRec[0].Interacting_With__r.name);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_ID,lstInteractionRec[0].Interacting_With__r.agent_ID__c);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_DOB,'');
                            //US-5602717 Added below changes to save updated npi id for call transfer scenario. 
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_NPI,'');
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                            }
                        }
                         when 'Other' {
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.U_VAL);
                            }
                        }
                        when else {
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL,PhoneBookConstants_H_HUM.M_VAL);
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.IVR_CALLER_TYPE,PhoneBookConstants_H_HUM.M_VAL);
                            }
                        }
                    }
                }
                else 
                {
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
                    String sInteractingAboutId;
                    String sIntAboutMemberId ='';
                    String sInteractingAboutType;
                    String sIntAboutFirstName;
                    String sIntAboutLastName;
                    String sIntAboutDOB;
                    String sIntAboutGroupNum;
                    String sIntAboutAgentId;
                    String sIntAboutName;
                    boolean sIntAboutAuth;
                    // US4696940: removed below code for adding Auth_IND based on Interacting with in case of call transfer
                    if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
                    {
                        if(lstInteractionRec[0].Interaction_Members__r.size() > 0)
                        {  
                            sInteractingAboutId =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__c;
                            sInteractingAboutType = lstInteractionRec[0].Interaction_Members__r[0].Interacting_About_Type__c;
                            sIntAboutFirstName =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.FirstName;
                            sIntAboutLastName =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.LastName;
                            sIntAboutDOB =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.BirthDate__c;
                            sIntAboutGroupNum =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Group_Number__c;
                            sIntAboutAgentId =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Agent_Id__c;
                            sIntAboutName =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Name;
                            // US4696940: Added below code for adding AUTH_IND based on Interacting about in case of call transfer
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
                            {
                                sIntAboutAuth = lstInteractionRec[0].Interaction_Members__r[0].Authenticated__c;
                            }
                        }
                    }
                    else{
                        if(lstInteractionRec[0].Interaction_Members__r.size()==1)
                        {
                            sInteractingAboutId =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__c;
                            sInteractingAboutType = lstInteractionRec[0].Interaction_Members__r[0].Interacting_About_Type__c;
                            sIntAboutFirstName =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.FirstName;
                            sIntAboutLastName =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.LastName;
                            sIntAboutDOB =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.BirthDate__c;
                            sIntAboutGroupNum =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Group_Number__c;
                            sIntAboutAgentId =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Agent_Id__c;
                            sIntAboutName =  lstInteractionRec[0].Interaction_Members__r[0].Interacting_About__r.Name;
                        }
                        else if(intAbtId!=Null && intAbtId !='')
                        {	
                            for(Interaction_Member__c eachIntMem : lstInteractionRec[0].Interaction_Members__r)
                            {
                                if(eachIntMem.Interacting_About__c==intAbtId)
                                {
                                    sInteractingAboutId =  eachIntMem.Interacting_About__c;
                                    sInteractingAboutType = eachIntMem.Interacting_About_Type__c;
                                    sIntAboutFirstName =  eachIntMem.Interacting_About__r.FirstName;
                                    sIntAboutLastName =  eachIntMem.Interacting_About__r.LastName;
                                    sIntAboutDOB =  eachIntMem.Interacting_About__r.BirthDate__c;
                                    sIntAboutGroupNum =  eachIntMem.Interacting_About__r.Group_Number__c;
                                    sIntAboutAgentId =  eachIntMem.Interacting_About__r.Agent_Id__c;
                                    sIntAboutName =  eachIntMem.Interacting_About__r.Name;
                                    Break;
                                }
                            }
                        }
                    }
                    // US4696940: Added below code for adding AUTH_ID based on Interacting about in case of call transfer
                    if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
                        {
                            if(sIntAboutAuth)
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'Y');
                            }
                            else
                            {
                                interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'N');
                            }
                    }
                    switch on sInteractingAboutType 
                    {
                        when 'Member' 
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,sIntAboutFirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutLastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,sIntAboutDOB);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.M_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,'');
                            
                            // US4696940: Added below code for adding INQA_ID and gen key based on Interacting about in case of call transfer
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
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
                        when 'Group' 
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,sIntAboutGroupNum);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.E_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                        }
                        when 'Provider' 
                        {
                            List<ConsumerID__c> taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sInteractingAboutId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,(taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.P_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                        }
                        when 'Agent' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,sIntAboutAgentId);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.B_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                        }
                        when 'Unknown-Member' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,sIntAboutFirstName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutLastName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,sIntAboutDOB);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,'');
                        }
                        when 'Unknown-Group' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,sIntAboutGroupNum);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                        }
                        when 'Unknown-Provider' {
                            List<ConsumerID__c> taxId = [SELECT Id, Account__c, Consumer_ID__c FROM ConsumerID__c where account__c =: sInteractingAboutId];
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,(taxId.size()>0?taxId[0].Consumer_ID__c:''));
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                        }
                        when 'Unknown-Agent' {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,sIntAboutName);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,sIntAboutAgentId);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL,PhoneBookConstants_H_HUM.U_VAL);
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                        }
                    }
                }
                else
                {
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_LAST,'');
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_ID,'');
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_FIRST,'');
                    interactionsavecallmapping.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL,'');
                    // US4696940: Added below code for adding AUTH_IND based on Interacting with in case of call transfer if about not added
                    if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
                    {
                        if(lstInteractionRec[0].Authenticated__c == true)
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'Y');
                        }
                        else
                        {
                            interactionsavecallmapping.put(PhoneBookConstants_H_HUM.Authentication_IND, 'N');
                        }
                        interactionsavecallmapping.put(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY,'0');
                    }
                }
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_D_HUM', 'getInteractionInfoForSaveCallData');
        }
        return interactionsavecallmapping;
    }
    
    
    /**
* Returns Map of the policy details 
* Method to get the policy data to be sent to saveCallData webservice 
*
* @param  String UUID
* @return Map < String, String >
*/
    public static Map < String, String > getCallTransferData(String UUID)
    {
        Map < String, String > callDataListMap = new Map < String, String > ();
        Map < String, String > mapSoftPhone = new Map < String, String > ();
        try
        {
            if (!String.isEmpty(UUID)) 
            {
                List < Integration_Mapping__c > lstsoftphoneCallRouting = new List < Integration_Mapping__c > ();
                String sQry = 'SELECT Mapping__c FROM Integration_Mapping__c WHERE Record_ID__c = :UUID ORDER BY CreatedDate DESC Limit 1';
                lstsoftphoneCallRouting = Database.query(sQry);
                if (!lstsoftphoneCallRouting.isEmpty()) 
                {
                    if (lstsoftphoneCallRouting[0] != null) 
                    {
                        if (lstsoftphoneCallRouting[0].Mapping__c != null) 
                        {
                            if (lstsoftphoneCallRouting[0].Mapping__c.contains(system.label.HUMSPhoneMappingObj_semicolon) && lstsoftphoneCallRouting[0].Mapping__c.contains(system.label.HUMSPhoneMappingObj_colon)) 
                            {
                                for (String s: lstsoftphoneCallRouting[0].Mapping__c.split(system.label.HUMSPhoneMappingObj_semicolon)) 
                                {
                                    if (s.contains(system.label.HUMSPhoneMappingObj_colon)) 
                                    {
                                        List < String > lstTemp = s.split(system.label.HUMSPhoneMappingObj_colon);
                                        if (lstTemp.size() == 1) 
                                        {
                                            mapSoftPhone.put(lstTemp[0], '');
                                        }
                                        else if(lstTemp.size() > 1)
                                        {
                                            mapSoftPhone.put(lstTemp[0], lstTemp[1]); 
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (mapSoftPhone.size() > 0) 
                    {
                        HUMSoftPhoneCallTransferHelper getCallTransferData = HUMSoftPhoneCallTransferService.createResponse(mapSoftPhone);
                        if (getCallTransferData.ASOIndicator != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.ASO_IND_VAL, getCallTransferData.ASOIndicator);
                        }
                        if (getCallTransferData.BusinessSegmentIndicator != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.BUS_SEGMENT_IND_VAL, getCallTransferData.BusinessSegmentIndicator);
                        }
                        if (getCallTransferData.HdphIndicator != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.HDHP_IND_VAL, getCallTransferData.HdphIndicator);
                        }
                        if (getCallTransferData.InquiredAboutDOB != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.INQA_DOB_VAL, getCallTransferData.InquiredAboutDOB);
                        }
                        if (getCallTransferData.InquiredAboutGroupId != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL, getCallTransferData.InquiredAboutGroupId);
                        }
                        if (getCallTransferData.InquiredAboutPlatformCode != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD_VAL, getCallTransferData.InquiredAboutPlatformCode);
                        }
                        if (getCallTransferData.MajorLineOfBusiness != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.MAJOR_LOB_VAL, getCallTransferData.MajorLineOfBusiness);
                        }
                        if (getCallTransferData.MemberId != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.MEMBER_ID_VAL, getCallTransferData.MemberId);
                        }
                        if (getCallTransferData.MtvBusinessLevel5 != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.MTV_BL5_VAL, getCallTransferData.MtvBusinessLevel5);
                        }
                        if (getCallTransferData.MtvBusinessLevel7 != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.MTV_BL7_VAL, getCallTransferData.MtvBusinessLevel7);
                        }
                        if (getCallTransferData.ProductTypeCode != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.PROD_TYPE_CODE_VAL, getCallTransferData.ProductTypeCode);
                        }
                        if (getCallTransferData.SellingLedger != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.SELLING_LEDGER_VAL, getCallTransferData.SellingLedger);
                        }
                        if (getCallTransferData.StateOfIssue != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.STATE_OF_ISSUE_VAL, getCallTransferData.StateOfIssue);
                        }
                        if (getCallTransferData.VENDOR_ASOFDATE != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.VENDOR_ASOFDATE_VAL, getCallTransferData.VENDOR_ASOFDATE);
                        }
                        if (getCallTransferData.VENDOR_SUB_GROUPID != null) 
                        {
                            callDataListMap.put(PhoneBookConstants_H_HUM.VENDOR_SUB_GROUPID_VAL, getCallTransferData.VENDOR_SUB_GROUPID);
                        }
                    }
                }
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_D_HUM', 'getCallTransferData');
        }
        return callDataListMap;
    }
    
        /**
* <p>
*  Method to extract interaction for transfer scenario
* 
* @param  String uuid 
* @return List<Interaction__c>
*/
     public static List<Interaction__c> getInteraction(String uuid)
     {
        List<Interaction__c> lstInteractionRec = new List<Interaction__c>();
        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
        {
            lstInteractionRec = [SELECT Id,Survey_ID__c,Call_Category_Description__c,Caller_Name__c,Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c, CreatedDate,Call_data__c,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Interaction__c,Authenticated__c FROM Interaction_Members__r order by Name ASC) FROM Interaction__c where UUID__c = :uuid order by Name desc limit 1];
        }
        else
        {
            lstInteractionRec = [SELECT Id,Survey_ID__c,Call_Category_Description__c,Caller_Name__c,Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c, CreatedDate,Call_data__c,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Interaction__c,Authenticated__c FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where UUID__c = :uuid order by Name desc limit 1];
        }
      	return lstInteractionRec;
    }

     /**
* <p>
*  Method to insert interaction for transfer scenario
* 
* @param  Interaction__c interactionRec 
* @return String
*/
    
    public static String insertInteraction(Interaction__c interactionRec)
    {
        try
        {
            insert interactionRec;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_D_HUM', 'insertInteraction');
        }
        return interactionRec.id;
    }
    
     /**
* <p>
*  Method to insert interaction member for transfer scenario
* 
* @param  List<Interaction_Member__c> interactionMemRec 
* @return NA
*/
    
    public static void insertInteractionMember(List<Interaction_Member__c> interactionMemRec)
    {
        try
        {
            insert interactionMemRec;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_D_HUM', 'insertInteractionMember');
        }
   }

       /**
* <p>
*  Method to retrieve memberId from  Pharmacy Page
* 
* @param  String policyMemberId, String paymentType
* @return Map<String,String>
*/

    public static Map<String,String> fetchPolicyMemberData(String policyMemberId,String paymentType)
    {
        Map<String,String> secureDataParams = new Map<String,String>();
        List<Member_ID__c> memIDRec;
        try
        {
            if(policyMemberId!= '' && policyMemberId!= null)
            {
                memIDRec = [SELECT Member_ID_Value__c,Member_Dependent_Code__c,Policy_Member__r.Member__r.Enterprise_ID__c,Policy_Member__r.Subscriber__c,Policy_Member__r.Policy_Platform__c,Policy_Member__r.Name FROM Member_ID__c where Policy_Member__c=:policyMemberId AND Type__c like 'Member-Id-Base' LIMIT 1];
                  if(memIDRec.size() > 0)
                {
                    secureDataParams.put(PhoneBookConstants_H_HUM.Member_ID_Value,memIDRec[0].Member_ID_Value__c);
                    secureDataParams.put(PhoneBookConstants_H_HUM.Member_Dependent_Code,memIDRec[0].Member_Dependent_Code__c);
                    secureDataParams.put(PhoneBookConstants_H_HUM.Enterprise_ID,memIDRec[0].Policy_Member__r.Member__r.Enterprise_ID__c);
                    secureDataParams.put(PhoneBookConstants_H_HUM.Policy_Platform,memIDRec[0].Policy_Member__r.Policy_Platform__c);
                }
            }
            if(paymentType == 'CBIS')
            {
                if(memIDRec.size() > 0)
                {
                    secureDataParams.put(PhoneBookConstants_H_HUM.SubscriberIdBase,memIDRec[0].Member_ID_Value__c);  
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'fetchPolicyMemberData');
        }
        return secureDataParams;
    }

    /*
     * Method Name : setInquiryId
     * Description : This Method is used to set Interacting With and About Ids.  
     * Return type : Id 
     * Paramater   : ID (Account)
     */
    public static String setInquiryId(Account oAcc)
    {
        system.debug('setInquiryId=>' + oAcc);
        String memberID;
        String sInquiredAboutId = (oAcc.policy_members__r.size()>0)? oAcc.policy_members__r[0].Name:null;
        if(sInquiredAboutId.length() > 9 && sInquiredAboutId.length() < 13 && (sInquiredAboutId.substring(0, 1).equalsIgnoreCase('H') || sInquiredAboutId.isNumeric()))
        {
            memberID = sInquiredAboutId.substring(0,(sInquiredAboutId.length()-2));
        }
        return memberID;
    }
}