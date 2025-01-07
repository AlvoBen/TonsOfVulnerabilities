/*******************************************************************************************************************************
Apex Class Name : HPPreChatController_C_HUM 
Version         : 1.0
Created On      : 09/26/2014
Function        : 1. This class serves as Controller of the HUMPrechat as the Live Agent Pre chat survey form.
                  2. This Class Is being used by site user, this user Is Not having access to metadata such as custom settings being used in the class.
                     Hence in order to fulfil this requirement, running this class in'Without Sharing' mode.

Test Class      : HPPreChatController_T_HUM
Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Joel George                                 03/13/2019               Original Version
 * 1.1              Lakshmi Madduri                             2/16/2021                REQ - 1840097 Switch Creation
 * 1.2              Alvaro Madrid                               07/13/2023               US 4842692 - Decrypt the IVA Bot Transcript from CWP
 * 1.3              Alvaro Madrid                               09/21/2023               US 5140245 -  Ability for the prechat page to dynamically set the button id and name
 * 1.4              Sivaprakash Rajendran                       09/25/2023               US 5140245 -  Ability for the prechat page to dynamically set the butt
 ****************************************************************************************************************************/
global without sharing class HPPreChatController_C_HUM
{
    /**
    * This is a constructor for the class.
    *
    */
    public HPPreChatController_C_HUM() 
    {
        initVariable();                
    }

    Public String sSecondary_Chat_Id {Get;Set;}
    Public String[] stringCharacters {Get;Set;}
    Public map<String,HUMWebChatConstants__c> sWebChatVarMap{get;set;}
    global static String sDecryptMemberGenKey {get; set;}
    global static String sDecryptLsessionID{get; set;}
    public static String sDecryptBotTranscript{get; set;}
    global static String sInteractionName{get;set;}
    global string btnMapJson {get;set;}
    public transient String pageParams { get; private set; }
    Public String webChatTranscriptDetail { get; set; }
    Public static String sSkillName{get;set;}
    public static string sAccName{get;set;}
    public String hpEncryptedValue{get;set;}
    public String sMemberGenKey{get;set;}
    public String sSessionId{get;set;}
    public Boolean bShowError{get;set;}
    Public static String sCType{get;set;}
	Public Boolean bSwitch {Get;Set;}
    public string btnSkillMapJson {get;set;}
    public static map<String, livechatbutton> mapButtonSkill;
    public boolean CWPRoutingIsOn {get;set;}
    public boolean CWPSkippingPreChat {get;set;}

    /**
    * initVariable Method constructor.
    * <p>
    * This method is used to initialize the custom setting variables.
    *
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @return NA                 
    */
    public void initVariable() 
    {
        try
        {
            bSwitch = HUMUtilityHelper.isCRMFunctionalityON('1840097');
            CWPRoutingIsOn = HUMUtilityHelper.isCRMFunctionalityON('5140245');
            CWPSkippingPreChat = HUMUtilityHelper.isCRMFunctionalityON(HUMConstants.SWITCH_US5158379);
            sCType = System.Label.chatTypeHumanaPharmacy;
            sWebChatVarMap = new Map<String,HUMWebChatConstants__c>();
            sWebChatVarMap = HUMWebChatConstants__c.getAll();
            btnMapJson ='';
            bShowError = false;
            sSecondary_Chat_Id = '';
            String sSkillId = '';

            mapButtonSkill= new map<String, livechatbutton>();

                for(livechatbutton liveChatBtn : [SELECT Id, DeveloperName, SkillId, Skill.MasterLabel, MasterLabel FROM LiveChatButton WHERE Type =: GLOBAL_CONSTANT_HUM.LIVEAGENT_BUTTON_TYPE AND PrechatFormPage.Name ='HPPrechat_VF_HUM']){
                    String liveChatBtnID = (String.ValueOf(liveChatBtn.Id)).substring(0, 15); 
                    mapButtonSkill.put(liveChatBtnID ,liveChatBtn);
                }
                                btnSkillMapJson = JSON.serialize(mapButtonSkill);    
            
                for(LiveChatButton btn : [SELECT Id, DeveloperName, MasterLabel, skillId,skill.MasterLabel FROM LiveChatButton WHERE Type =: GLOBAL_CONSTANT_HUM.LIVEAGENT_BUTTON_TYPE and DeveloperName =: GLOBAL_CONSTANT_HUM.LIVEAGENT_BUTTON_NAME])
                {              
                    btnMapJson = JSON.serialize(btn);
                    sSkillName = btn.skill.MasterLabel;
                }   
                       
            pageParams = '';
            
            for (String key : ApexPages.currentPage().getParameters().keySet())
            {
                if (key != 'transcript')  
                {
                    pageParams += '(Key: ' + key + ' Value: ' + ApexPages.currentPage().getParameters().get(key) + ') ';
                }                
                else
                {
                    webChatTranscriptDetail = ApexPages.currentPage().getParameters().get(key);
                }
            }

            stringCharacters = pageParams.split('&');

            for(string sChar: stringCharacters)
            {
                if(sChar.contains('session_id'))
                {
                    string sChar1 = sChar.remove('session_id=');
                    string sChar2 = sChar1.remove(')');
                    sSecondary_Chat_Id = sChar2.trim();                      
                }
            }
            initVariable2();
        }
        catch(exception e)
        {
            bShowError = true;
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,'An Error Occured -- Please try again. If the issue persists contact Help Desk.'));
        }
    }
    
    /**
    * initVariable2 Method constructor.
    * <p>
    * This method is used to initialize the custom setting variables.
    *
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @return HUMPreChatResultWrapperHelper                 decrypted values in wrapper
    */
    public void initVariable2() 
    {
        Datetime secDateTime = system.now();
        Date secTempDate = secDateTime.date();
        String secTempDate1 = string.valueof(secTempDate);
        String[] secTempDateChar;
        secTempDateChar = secTempDate1.split('-');
        String secTempDate2 = '';

        for(string sChar1: secTempDateChar)
        {
            if(sChar1!=null)
            {
                secTempDate2+=sChar1;
            }
        }

        Integer nHour = secDateTime.hour();
        Integer nMin = secDateTime.minute();
        Integer nSecond = secDateTime.second();
        Integer nMilliSec = secDateTime.millisecond();
        String secTempDateTime = secTempDate2+string.valueof(nHour)+string.valueof(nMin)+string.valueof(nSecond)+string.valueof(nMilliSec);
 
        sSecondary_Chat_Id = sSecondary_Chat_Id+'-'+secTempDateTime;
        sSecondary_Chat_Id = sSecondary_Chat_Id.replaceAll( '\\s+', '');
    }
 
    /**
    * This method accepts Enterprise Id sent by VF page and finds corresponding Acount.
    * <p>
    * It creates Interaction record, and affiliates it to Account Created earlier.
    *
    * @param  sEnterpriseID                                 encrypted sEnterpriseID
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @return HUMPreChatResultWrapperHelper                 generated interaction details in wrapper
    */
    @RemoteAction
    global static HPPreChatResultWrapperHelper findAccountByPersonId(String sEnterpriseID, String sMemberGenKey, String sLsessionID) 
    {
        try
        {
            String sDecryptPersonId ='';
            
            if(String.isNotBlank(sEnterpriseID))
            {
                sDecryptPersonId = getDecryptedString(sEnterpriseID);

                if(String.isNotBlank(sDecryptPersonId))
                {
                    //fetching account from Decrypted Enterprise Id.
                    list < Account > lstAccount = [select Id, FirstName, LastName FROM Account WHERE enterprise_id__c = : sDecryptPersonId limit 1];
                    
                    if (!lstAccount.isEmpty()) 
                    {
                        String accFName = lstAccount[0].FirstName;
                        accFName = accFName.toLowerCase().capitalize();                                   
                        sAccName = accFName + ' ' + lstAccount[0].LastName.substring(0,1);
                        //Creating Interaction Record.
                        Interaction__c oInt = new Interaction__c(Interacting_With__c = lstAccount[0].Id,
                                                                Interacting_With_Type__c = GLOBAL_CONSTANT_HUM.MEMBER_LIVEAGENT_HUM,
                                                                Interaction_Origin__c = GLOBAL_CONSTANT_HUM.WEBCHAT_LIVEAGENT_HUM );

                        Database.saveResult oSvr = Database.insert(oInt,false);

                        List<Database.SaveResult> listSaveResults = new List<Database.SaveResult>{oSvr};
                        List<interaction__C> lstInteractions = new List<interaction__C>{oInt};
                        boolean isError = HUMExceptionHelper.processSaveResults(listSaveResults,lstInteractions,'HPPreChatController_C_HUM','findAccountByPersonId','Interaction__C');
                        
                        oInt = [Select Name,Interacting_With__c from Interaction__c where Id=:oInt.id];
                        //Creating Interaction member record.
                        Interaction_member__c oHUMIntMember = new Interaction_member__c(Interacting_about_type__c = GLOBAL_CONSTANT_HUM.MEMBER_LIVEAGENT_HUM,
                                                                                        Interacting_about__c =lstAccount[0].Id,
                                                                                        Interaction__c = oInt.ID );

                        Database.saveResult oSvrIntMem = Database.insert(oHUMIntMember,false);

                        List<Database.SaveResult> listSaveResultsIntMembers = new List<Database.SaveResult>{oSvrIntMem};
                        List<Interaction_member__c> lstInteractionMembers = new List<Interaction_member__c>{oHUMIntMember};
                        isError = HUMExceptionHelper.processSaveResults(listSaveResultsIntMembers,lstInteractionMembers, 'HPPreChatController_C_HUM','findAccountByPersonId','Interaction_member__c');
                        
                        return new HPPreChatResultWrapperHelper(oInt.Interacting_With__c, oInt.Id, oHUMIntmember.id, oInt.Name, HUMConstants.HUMSearchTypeMember, HUMConstants.HUMWebChatText, HUMConstants.CASE_TYPE, sAccName);
                    }
                    else
                    {
                        logErrors('No Accounts fetched for Decrypted PersonId ' +  sDecryptPersonId ,'HPPreChatController_C_HUM','findAccountByPersonId');
                        return null;
                    }
                }
                else
                {
                    logErrors('Decrypted PersonId is null for EnterpriseId '+ sEnterpriseID,'HPPreChatController_C_HUM','findAccountByPersonId' );
                    return null;
                }
            }
            else
            {
                logErrors('EnterpriseID is null' + sEnterpriseID,'HPPreChatController_C_HUM','findAccountByPersonId');
                return null;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HPPreChatController_C_HUM','findAccountByPersonId');
            return null;
        }
    }
    
    /**
    * Method will process the encrypted data.
    * <p>
    * This method Accepts Enterprise ID which is in Encrypted form and convert it into Decrypted form as String.
    *
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @param  sBotTranscript                                encrypted sBotTranscript
    * @return HUMPreChatResultWrapperHelper                 decrypted values in wrapper
    */
    @remoteAction
    global static HPPreChatResultWrapperHelper decryptUserIdentifiers(String sMemberGenKey, String sLsessionID, String sEnterpriseID, String sBotTranscript) 
    {
       Boolean bIsMemberPresent = false;
       try
       {     
            if(!String.isEmpty(sEnterpriseID))
            {
                string sDecryptPersonId = getDecryptedString(sEnterpriseID);
                list<Account> lstAccount = [select Id FROM Account WHERE enterprise_id__c = : sDecryptPersonId limit 1];

                if (lstAccount != null && !lstAccount.isEmpty()) 
                {
                	bIsMemberPresent = true;

					if(!String.isEmpty(sMemberGenKey))
					{
						sDecryptMemberGenKey = getDecryptedString(sMemberGenKey);
					}
					if(!String.isEmpty(sLsessionID))
					{
						sDecryptLsessionID = getDecryptedString(sLsessionID);
					}
				}
            }

            if(!String.isEmpty(sBotTranscript)) {
                sDecryptBotTranscript = '';
                String strFormatEncryptedString = getDecryptedString(sBotTranscript).replace('\\n', '<br>');
            
                for(String bstr : strFormatEncryptedString.split('<br>')) {
                   sDecryptBotTranscript += bstr+'\n';
                }
				sDecryptBotTranscript = sDecryptBotTranscript.removeEnd('\n');
            }
            return new HPPreChatResultWrapperHelper(sDecryptMemberGenKey, sDecryptLsessionID, bIsMemberPresent, sDecryptBotTranscript);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HPPreChatController_C_HUM','decryptUserIdentifiers');
            return null;
        }
    }
    
    /**
    * Method will process the encrypted data.
    * <p>
    * This method accepts Enterprise ID which is in Encrypted form and convert it into Decrypted form as String.
    *
    * @param  sTobeDecrypted                encrypted Enterprise ID
    * @return sDecryptedData                decrypted Enterprise ID
    */
    global static String getDecryptedString(String sTobeDecrypted)
    {   
        try
        {  
            Blob hpIV = Blob.valueOf(GLOBAL_CONSTANT_HUM.IV_LIVEAGENT_PHARMACY);
            blob  cryptoKey = blob.valueOf(GLOBAL_CONSTANT_HUM.ENCRYPTIONKEY_PHARMACY);
            blob base64toBLOBEncrypt = EncodingUtil.base64Decode(sTobeDecrypted);
            Blob decryptedData = Crypto.decrypt(GLOBAL_CONSTANT_HUM.ALGORITHM_PHARMACY,cryptoKey,hpIV,base64toBLOBEncrypt);
            String sDecryptedData = decryptedData.toString();
            return sDecryptedData;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HPPreChatController_C_HUM','getDecryptedString');
            return null;
        }
    }
 
    /**
    * void method to process custom chat record creation
    * <p>
    * This method is a future method. It inserts custom chat transcript record with empty body
    *
    * @param  sChatKey                  chat record unique key
    * @param  sUserID                   user SF record id
    */ 
    public static void saveCustomTranscriptOnEncryption(String sChatKey,String sUserID)
    {
        List<Humana_Chat_Transcript__c> lstCustomTranscript = new List<Humana_Chat_Transcript__c>();
        Humana_Chat_Transcript__c objChat = new Humana_Chat_Transcript__c();

        objChat.Chat_Key__c = sChatKey;
        objChat.OwnerId = Id.valueOf(sUserID);
        lstCustomTranscript.add(objChat);
        
        if(!lstCustomTranscript.isEmpty())
        {
            //Upserting Custom Chat Object record.
            Schema.SObjectField keyFieldName= Humana_Chat_Transcript__c.Fields.chat_Key__c;
            Database.UpsertResult [] lstSvr = Database.upsert(lstCustomTranscript,keyFieldName, false);
            boolean isError = HUMExceptionHelper.processUpsertResults(lstSvr, lstCustomTranscript,'HPPreChatController_C_HUM', 'saveCustomTranscriptOnEncryption', 'Humana_Chat_Transcript__c');
        }
    }

    /**
    * void method to process custom chat record creation
    * <p>
    * Method called from Page as remote action. It in turn calls method which inserts data into custom transcript record.
    *
    * @param  sChatKey                  chat record unique key
    * @param  sUserID                   user SF record id
    */
    @RemoteAction
    global static void onCommitTranscript(string sChatKey,String sUserID)
    {
        try
        {
            if(!String.isEmpty(sChatKey) && !String.isEmpty(sUserID))
            {
                //Calling method for insertion by passing chat key,agent salesforce ID.
                saveCustomTranscriptOnEncryption(sChatKey,sUserID);
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HPPreChatController_C_HUM','onCommitTranscript');
        }
    }

    /**
    * Returns InteractionName from RemoteAction method
    * <p>
    * Method called from Page as remote action. It in turn calls method which gets Name of Interaction based on Interaction Id.
    *
    * @param  sIntId                interaction SF record id
    * @return sInteractionName      InteractionName
    */
    @RemoteAction
    global static String findInteractionName(String sIntId)
    {
        try
        {
            if (String.isNotBlank(sIntId) && sIntId instanceof Id)
            {
                Id interactionId = null;
                
                try
                {
                    interactionId = Id.valueOf(sIntId); 
                }
                Catch(StringException ex) 
                {
                    logErrors('Interaction Id: ' + sIntId + ' is not a valid Id','HPPreChatController_C_HUM','findInteractionName');
                    return null;
                }
                if(interactionId != null)
                {
                    list < Interaction__c> lstInteraction = [Select Name from Interaction__c where Id=:interactionId];

                    if(!lstInteraction.isEmpty())
                    {
                        sInteractionName = lstInteraction[0].Name; 
                        return sInteractionName;
                    }
                    else
                    {
                        logErrors('No Interactions fetched for sIntId '+sIntId ,'HPPreChatController_C_HUM','findInteractionName');
                        return null;                        
                    }
                }
                else
                {
                    logErrors('Interction ID is null ','HPPreChatController_C_HUM','findInteractionName');
                    return null;  
                }
            }
            else
            {
                logErrors('sIntId is either null or not a valid ID ' + sIntId,'HPPreChatController_C_HUM','findInteractionName');
                return null;                
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HPPreChatController_C_HUM','findInteractionName');
            return null;
        }
    }   

    /**
    * Logs Errors in Error_Log__c objet
    * <p>
    *     *
    * @param  erMessage    Error message to be storied Error log object
    * @param  sClassName   Class Name to be storied Error log object
    * @param  sMethodName  Method Name to be storied Error log object
    */  
    @TestVisible
    private static void logErrors(String erMessage, String sClassName, String sMethodName)
    {
        Error_Log__c errorLog = new Error_Log__c();
        errorLog = new Error_Log__c(Error_Code__c ='HUMCustomException',
        Error_Message__c = erMessage,
        Request_Sent__c = true,
        class_name__c = sClassName,
        Method_Name__c = sMethodName,
        Type__c = 'Application Error');        
        database.insert(errorLog);
    }

    /**
    * Method called from HPPostChat_VF_HUM
    * This method is used to save the transcript
    * @param None
    * @return None   
    */  
    public PageReference goToHUMSaveChatTranscriptPage() {    
        PageReference pageRef = Page.HPSaveChatTranscript_HUM;
        pageRef.setRedirect(false);        
        return pageRef;
    }
    
    /**
    * Method called from HPLiveAgentSiteHomePage
    * This method is used to encrypt the enterprise id from crm
    * @param None
    * @return None   
    */  
    public void getEnterpriseId()
    {
        sMemberGenKey = GLOBAL_CONSTANT_HUM.LIVEAGENT_PHARMACY_MEMBERGENKEY;
        sSessionId = GLOBAL_CONSTANT_HUM.LIVEAGENT_PHARMACY_SESSIONID;
        hpEncryptedValue = getEncryptedString(System.currentPagereference().getParameters().get('enterpriseId'));
    }
    
    /**
    * Method called from HPLiveAgentSiteHomePage
    * This method is used to encrypt the enterprise id from crm
    * @param None
    * @return None   
    */     
    public String getEncryptedString(String sEntId)
    {   
        try
        {   
            Blob hpIvEncryption = Blob.valueOf(GLOBAL_CONSTANT_HUM.IV_LIVEAGENT_PHARMACY);
            Blob key = blob.valueOf(GLOBAL_CONSTANT_HUM.ENCRYPTIONKEY_PHARMACY);
            Blob dataToBeEncrypted = blob.valueOf(sEntId);
            Blob encrypted = Crypto.encrypt(GLOBAL_CONSTANT_HUM.ALGORITHM_PHARMACY, key, hpIvEncryption, dataToBeEncrypted);
            String encodeString = EncodingUtil.base64Encode(encrypted);
            return encodeString;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HPPreChatController_C_HUM','getEncryptedString');
            return null;
        }
    }
}