/*******************************************************************************************************************************
Apex Class Name : HUMPreChatController 
Version         : 1.0
Created On      : 09/26/2014
Function        : 1. This class serves as Controller of the HUMPrechat as the Live Agent Pre chat survey form.
                  2. This Class Is being used by site user, this user Is Not having access to metadata such as custom settings being used in the class.
                     Hence in order to fulfil this requirement, running this class in'Without Sharing' mode.

        Test Class      : HUMPreChatControllerTest
Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Ninad Patil             18240                   09/26/2014               Original Version
 * 1.1              Ninad Patil             18240                   09/29/2014               Fixed Internal code reveiw comment.
 * 1.2              Ninad Patil             18240                   09/30/2014               Fixed Code reveie comments.
 * 1.3              Ninad Patil             18975                   10/30/2014               Done changes for CR50
 * 1.4              Sagar Tapadia           20293                   01/12/2015               Changes related to Encryption of Body before saving into database.
 * 1.5              Sagar Tapadia           20302                   01/13/2015               Changes added to update owner of custom chat record to agent.
 * 1.6              Sagar Tapadia           23562                   05/28/2015               CA5027355:Updated Code to create Custom record when agent accepts chat with no body.
 * 1.7              Shreya Choodamani       25923                   08/28/2015               made changes to pass the interaction number(name) in the URL for survey monkey as per REQ - 205290.
 * 1.8              Sagar Tapadia           27486                   11/02/2015               REQ-217639: Changes done in Line 60. Added calling of method from Page action to initialize custom setting.
 * 1.9              Pradeepkumar Dani                               07/29/2016               Added changes for PlanName and Category drop down - REQ - 270013
 * 1.10             Pradeepkumar Dani                               08/02/2016               Updated class with APEX standards - REQ - 270091
 * 1.11             Shiva Pasumarty                                 08/17/2016               REQ 270037 : Ability to provide the RSO segment with a separate Post Chat Survey
 * 1.12             Shiva Pasumarty         38010                   09/01/2016               Defect 240310.0001 - US-270018_Start Chat and Agents are not available both windows are displaying in Contact Us Window
 * 1.13             Shiva Pasumarty         61536                   02/09/2017               Defect 247436.0001- Decryption issues.
 * 1.14             Muralidhar Kollu                                02/23/2017               REQ - 301488: Add additional logging to help diagnose interaction id issue in prod
 * 1.15             Avinash Choubey         70261                   02/21/2017               changed creation of custom chat record to upsert in saveCustomTranscriptOnEncryption
 * 1.16             Muralidhar Kollu        80609                   02/28/2017               REQ - 306139: Post Web Chat - Additional Debugging on empty interactionId (CA Tix 6197943 and 6351830)
 * 1.17             Santhi Mandava          90926                   05/09/2017               REQ - 313381 : Created logInteractionDetails method.
 * 1.18             Joel George             94916                   05/22/2017               Added a method for PreChat Logging
 * 1.19             Vishal Verma            99430                   06/06/2017               REQ - 320399 : CA 6197943 -WebChat/Survey Monkey missing Interaction Id fix.
 * 2.0              Vishal Verma            110849                  07/17/2017               REQ - 323377 : CA 6917822 -WebChat/Survey Monkey missing Interaction Id fix
 * 2.1              Rajesh C	            144547		            11/03/2017               REQ - 321230 CRM will expose the unique identifier(s) that can be used to link Chat And Web Data sets
 * 2.2              Rajesh C                157145                  12/07/2017               REQ - 348289 Populate Skill Name in Live Agent Window And Visitor Tab (MyHumana)
 * 2.3              Rajesh C        		171913 	                01/11/2018               REQ - 350669 CR735: Securing the Save Chat Option - Live Chat & Post Chat Windows 
 * 2.4            Vandana Chaudhari  		220488	 	            04/09/2018               REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548)  
 * 2.5        	  Vandana Chaudhari     				 	        07/20/2018         	 	 REQ - 370601 CA Ticket 7646745 Fix: Web Chat Auto Create Case
 * 2.6            Akshay Pai                                        07/16/2020               REQ - 1292117 IVA NINA - Member Secure chat changes
 * 2.7            Joel George                                       07/20/2020               REQ - 1159264 Include Chat Type
 * 2.8            Akshay Pai                                        08/05/2020               REQ - 1131866 IVA WATSON - Member Secure chat changes
 * 2.9            Lakshmi Madduri                                   2/16/2021                REQ - 1685604 Switch Creation
 * 3.0            Vishnu Pilli                                   	07/29/2021               MF-22 Accolade Impact Member changes
 * 3.1            Vishnu Pilli                                   	09/07/2021               MF-22 Accolade Impact Member changes - Added switch to determine the SSO URL
 * 3.2            Alvaro Madrid                                   	05/02/2022               REQ - 3288511 Fix to avoid Secondary_Chat_Id duplicated in liveChatTranscripts
 ****************************************************************************************************************************/
global without sharing class HUMPreChatController 
{
    /**
    * This is a constructor for the class.
    *
    */
    public HUMPreChatController() 
    {
        getWebChatConstants();         
    }

    Public String sTrans {Get;Set;}
    Public String sSecondary_Chat_Id {Get;Set;}
    Public String[] stringCharacters {Get;Set;}
    Public map<String,HUMWebChatConstants__c> sWebChatVarMap{get;set;}
    public static String sDecryptMemberGenKey {get; set;}
    public static String sDecryptLsessionID{get; set;}
    public static String sInteractionName{get;set;}
    public static String sButtonId{get;set;}
    public static String sDecryptAcclId {get; set;}
    public static String sDecryptAcclBenefitCode {get; set;}
    public static String sDecryptMemberId {get; set;}
    public static String sDecryptDependentCode {get; set;}
    public static Boolean bGSO{get;set;}
    public static Boolean bRSO{get;set;}
    public string btnMapJson {get;set;}
    public string btnSkillMapJson {get;set;}
    public string BtnName {get;set;}
    public transient String pageHeaders{ get; private set; }
    public transient String pageParams { get; private set; }
    Public String webChatTranscriptDetail { get; set; }
    public static String sDecryptBotTranscript{get; set;}
     public static map<String, livechatbutton> mapButtonSkill;
	
	public static String sChatType{get;set;}
    public static String sChatTypeGo365{get;set;}
    
	Public Boolean bSwitch {Get;Set;}
    Public boolean bGo365Switch {get;set;}
    Public Boolean savechatSwitch {get;set;}
    
    /*
        MF-22: Used to retrive the Custom Metadata for Accolade Impact Members
    */
    public Map<String, String> mapAccoladeSettings {get; set; }
    private static final String SSO_URL = 'SSO_URL';
    private static final String SSO_URL_NONPROD = 'SSO_URL_NONPROD';
    private static final String SSO_URL_GO365 = 'SSO_URL_GO365';
    private static final String SSO_URL_GO365_NONPROD = 'SSO_URL_GO365_NONPROD';
	private static final String NON_SSO_MESSAGE = 'Non_SSO_Message';
    private static final String NON_SSO_MESSAGE_NONPROD = 'Non_SSO_Message_NONPROD';
       
       /**
    * getWebChatConstants()
    * <p>
    * This method is used to fetch chat related customs setting values.
    * @param  NA                                
    * @return NA                 
    */
    public void getWebChatConstants() 
    {
		sWebChatVarMap = new Map<String,HUMWebChatConstants__c>();
		sWebChatVarMap = HUMWebChatConstants__c.getAll();
		
        //  MF-22:
        mapAccoladeSettings = new Map<String, String>();
        for (Chat_Accolade_Impact_Setting__mdt metaAcc : [SELECT Id, MasterLabel, DeveloperName, Value__c 
                                                            FROM Chat_Accolade_Impact_Setting__mdt]) {
            mapAccoladeSettings.put(metaAcc.DeveloperName, metaAcc.Value__c);
        }   //   end of for-each metadata

        Boolean isSandbox = [SELECT IsSandbox FROM Organization LIMIT 1].IsSandbox;
        if(isSandbox){
            mapAccoladeSettings.put(SSO_URL, mapAccoladeSettings.get(SSO_URL_NONPROD));
            mapAccoladeSettings.put(SSO_URL_GO365, mapAccoladeSettings.get(SSO_URL_GO365_NONPROD));
			mapAccoladeSettings.put(NON_SSO_MESSAGE, mapAccoladeSettings.get(NON_SSO_MESSAGE_NONPROD));
        }

		pageParams = '';
		
		for (String key : ApexPages.currentPage().getParameters().keySet())
		{

			if (key != 'transcript')  
			{
				pageParams += '(Key: ' + key + ' Value: ' + ApexPages.currentPage().getParameters().get(key) + ') ';
			}                

		}
		
		bSwitch = HUMUtilityHelper.isCRMFunctionalityON('1685604');
		bGo365Switch = HUMUtilityHelper.isCRMFunctionalityON('1840119');
        savechatSwitch = HUMUtilityHelper.isCRMFunctionalityON('3773361');
    }
    /**
    * initVariable Method constructor.
    * <p>
    * This method is used to initialize the custom setting variables.
    *
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @return HUMPreChatResultWrapperHelper                 decrypted values in wrapper
    */
    public void initVariable() 
    {
        try
        {
			sChatType = System.Label.chatTypeHumanaSecure;
            sChatTypeGo365 = System.Label.chatTypeGo365;
            btnMapJson ='';
            mapButtonSkill= new map<String, livechatbutton>();          
            sSecondary_Chat_Id = '';
            map<String, livechatbutton> btnMap = New map<String, livechatbutton>();
            for(livechatbutton btn : [SELECT Id, DeveloperName,SkillId,skill.MasterLabel, MasterLabel FROM LiveChatButton WHERE Type = 'Standard'])
            {
            String str = (String.ValueOf(btn.Id)).substring(0, 15); 
            
                btnMap.put(btn.Developername, btn);
                mapButtonSkill.put(str ,btn);
            }
            btnMapJson = JSON.serialize(btnMap);
            btnSkillMapJson = JSON.serialize(mapButtonSkill);
            
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
            sSecondary_Chat_Id = sSecondary_Chat_Id +'-'; //+secTempDateTime;
            sSecondary_Chat_Id = sSecondary_Chat_Id.replaceAll( '\\s+', '');
            
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController','initVariable');
        }
    }
    
    /**
    * This method accepts Enterprise Id/ Customer ID sent by VF page and finds corresponding Acount.
    * <p>
    * It creates Interaction record, and affiliates it to Account Created earlier. Extracts Classification,Intent and Policy Member details from the data passed.
    *
    * @param  sEnterpriseID                                 encrypted sEnterpriseID
    * @param  sSourceSystem                                 sSourceSystem
    * @param  sCustomerType                                 sCustomerType
    * @param  sCustomerId                                   encrypted sCustomerId
    * @param  sClassification                               sClassification
    * @param  sIntent                                       sIntent
    * @param  sPolicyNo                                     encrypted sPolicyNo
    * @param  sStartDate                                    sStartDate
    * @param  sEndDate                                      sEndDate
    * @return HUMPreChatResultWrapperHelper                 generated interaction details in wrapper
    */
    @RemoteAction
    global static HUMPreChatResultWrapperHelper findAccountByPersonId(String sEnterpriseID, String sSourceSystem, String sCustomerType, String sCustomerId, String sClassification, String sIntent, String sPolicyNo, String sStartDate, String sEndDate) 
    {
        try
        {   
            String accountToSearch ='';
            String classification = '';
            String intent = '';
            String policyMember = '';
            String CTCI = '';
            
            if (String.isNotBlank(sSourceSystem) && (sSourceSystem.trim().toUppercase() == GLOBAL_CONSTANT_HUM.SOURCE_SYSTEM_NINA || sSourceSystem.trim().toUppercase() == GLOBAL_CONSTANT_HUM.SOURCE_SYSTEM_IVAWATSON))
            {
                accountToSearch = sCustomerId;
            }
            else
            {
                accountToSearch = sEnterpriseID;
            }   

            if(String.isNotBlank(accountToSearch))
            {
                //calling function to decrypt Enterprise Id.
                String sDecryptPersonId = getDecryptedString(accountToSearch);
                if(String.isNotBlank(sDecryptPersonId))
                {
                    list< Account > lstAccount = new list< Account >();
                    
                    if (String.isBlank(sCustomerType) || sCustomerType.trim().toUppercase() == GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_MEMBER)
                    lstAccount = [select Id FROM Account WHERE enterprise_id__C = : sDecryptPersonId limit 1];
                                    
                    if (!lstAccount.isEmpty()) 
                    {
                        Interaction__c oInt = new Interaction__c(Interacting_With__c = lstAccount[0].Id,
                                                                Interacting_With_Type__c = GLOBAL_CONSTANT_HUM.MEMBER_LIVEAGENT_HUM,
                                                                Interaction_Origin__c = GLOBAL_CONSTANT_HUM.WEBCHAT_LIVEAGENT_HUM );
                        
                        Database.saveResult oSvr = Database.insert(oInt,false);
                        List<Database.SaveResult> listSaveResults = new List<Database.SaveResult>{oSvr};
                        List<interaction__C> lstInteractions = new List<interaction__C>{oInt};
                        boolean isError = HUMExceptionHelper.processSaveResults(listSaveResults,lstInteractions,'HUMPreChatController','findAccountByPersonId','Interaction__C');
                        
                        if (!oSvr.isSuccess())
                        return null;
                        
                        oInt = [Select Name,Interacting_With__c from Interaction__c where Id=:oInt.id];
                        
                        Interaction_member__c oHUMIntMember = new Interaction_member__c(Interacting_about_type__c = GLOBAL_CONSTANT_HUM.MEMBER_LIVEAGENT_HUM,
                                                                                        Interacting_about__c =lstAccount[0].Id,
                                                                                        Interaction__c = oInt.ID );
                        
                        Database.saveResult oSvrIntMem = Database.insert(oHUMIntMember,false);
                        List<Database.SaveResult> listSaveResultsIntMembers = new List<Database.SaveResult>{oSvrIntMem};
                        List<Interaction_member__c> lstInteractionMembers = new List<Interaction_member__c>{oHUMIntMember};
                        isError = HUMExceptionHelper.processSaveResults(listSaveResultsIntMembers,lstInteractionMembers, 'HUMPreChatController','findAccountByPersonId','Interaction_member__c');
                        
                        if (!oSvrIntMem.isSuccess())
                        return null;
                        
                        List<CTCI_Junction__c>  CTCIList = new List<CTCI_Junction__c>();
                        if( String.isNotBlank(sClassification) && String.isNotBlank(sIntent))
                        {                            
                            CTCIList =[Select Id,Classification__c,Intent__c from CTCI_Junction__c where IsActive__c = true and Classification__r.name =: sClassification and Classification__r.Is_Active__c = true and Intent__r.name =: sIntent and Intent__r.Is_Active__c = true];
                        }
                        
                        List<Policy_Member__c> lstPolMem = new List<Policy_Member__c>();
                        if( String.isNotBlank(sPolicyNo) && String.isNotBlank(sStartDate))
                        {   
                            String sDecryptPolicyMemId = getDecryptedString(sPolicyNo);
                            String policyEndDate = String.isNotBlank(sEndDate) ? sEndDate : GLOBAL_CONSTANT_HUM.DEFAULT_POLICY_END_DATE ;
                                                        
                            if(String.isNotBlank(sDecryptPolicyMemId))
                            lstPolMem = [Select id from Policy_Member__c where Name =:sDecryptPolicyMemId AND Effective_Date__c =:sStartDate AND End_Date__c =: policyEndDate AND Member__c=: lstAccount[0].Id];
                        }                        
                                                                       
                        if (!CTCIList.IsEmpty())
                        {
                            classification = CTCIList[0].Classification__c;
                            intent = CTCIList[0].Intent__c;
                            CTCI = CTCIList[0].id;
                        }
                        
						if (!lstPolMem.IsEmpty())
                        {
                            policyMember = lstPolMem[0].Id;
                        }
                       
                        return new HUMPreChatResultWrapperHelper(oInt.Interacting_With__c, oInt.Id, oHUMIntmember.id, oInt.Name, HUMConstants.HUMSearchTypeMember, HUMConstants.HUMWebChatText, HUMConstants.CASE_TYPE,classification,intent,policyMember,CTCI); 
                    }
                    else
                    {
                        logErrors('No Accounts fetched for Decrypted PersonId ' + sDecryptPersonId,'HUMPreChatController','findAccountByPersonId');
                            return null;
                    }
                }
                else
                {
                    logErrors('Decrypted PersonId is null for EnterpriseId '+ accountToSearch,'HUMPreChatController','findAccountByPersonId' );
                    return null;
                }
            }
            else
            {
                logErrors('EnterpriseID is null' + accountToSearch,'HUMPreChatController','findAccountByPersonId');
                return null;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController','findAccountByPersonId');
            return null;
        }
    }
    
    /**
    * Method will process the encrypted data.
    * <p>
    * This method Accepts Session ID & IVA bot transcript which is in Encrypted form and convert it into Decrypted form as String.
    *
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @param  sBotTranscript                                encrypted sBotTranscript
    * @return HUMPreChatResultWrapperHelper                 decrypted values in wrapper
    */
    @remoteAction
    global static HUMPreChatResultWrapperHelper decryptUserIdentifiers(String sMemberGenKey, String sLsessionID, String sBotTranscript) 
    {
       try
       {
            if(!String.isEmpty(sMemberGenKey))
            {
                sDecryptMemberGenKey = getDecryptedString(sMemberGenKey);
            }
	        
            if(!String.isEmpty(sLsessionID))
            {
                sDecryptLsessionID = getDecryptedString(sLsessionID);
            }
            
            if(!String.isEmpty(sBotTranscript))
            { 
                sDecryptBotTranscript = '';              
                String strFormatEncryptedString = getDecryptedString(sBotTranscript).replace('\\n', '<br>');          
            
                for(String bstr : strFormatEncryptedString.split('<br>'))
                   sDecryptBotTranscript += bstr+'\n';   
			   
				sDecryptBotTranscript = sDecryptBotTranscript.removeEnd('\n');
            }
            
            return new HUMPreChatResultWrapperHelper(sDecryptMemberGenKey, sDecryptLsessionID,sDecryptBotTranscript);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController','decryptUserIdentifiers');
            return null;
        }
    }
    
    /**
    * Method will process the encrypted data.
    * <p>
    * This method Accepts Session ID, IVA bot transcript and Accolade related params which is in Encrypted form and convert it into Decrypted form as String.
    *
    * @param  sMemberGenKey                                 encrypted sMemberGenKey
    * @param  sLsessionID                                   encrypted sLsessionID
    * @param  sBotTranscript                                encrypted sBotTranscript
    * @param  sAcclId                                       encrypted AccoladeId
    * @param  sAcclBenfitCode                               encrypted BenefitCode
    * @param  sMemberId                                     encrypted Member Id
    * @param  sDependentCode                                encrypted DependentCode
    * @return HUMPreChatResultWrapperHelper                 decrypted values in wrapper
    */
    @remoteAction
    global static HUMPreChatResultWrapperHelper decryptClientUserData(String sMemberGenKey, String sLsessionID, String sBotTranscript, String sAcclId, String sAcclBenfitCode, String sMemberId, String sDependentCode) 
    {
       try
       {
            if(!String.isEmpty(sMemberGenKey))
            {
                sDecryptMemberGenKey = getDecryptedString(sMemberGenKey);
            }
	        
            if(!String.isEmpty(sLsessionID))
            {
                sDecryptLsessionID = getDecryptedString(sLsessionID);
            }
            
            if(!String.isEmpty(sBotTranscript))
            { 
                sDecryptBotTranscript = '';              
                String strFormatEncryptedString = getDecryptedString(sBotTranscript).replace('\\n', '<br>');          
            
                for(String bstr : strFormatEncryptedString.split('<br>'))
                   sDecryptBotTranscript += bstr+'\n';   
			   
				sDecryptBotTranscript = sDecryptBotTranscript.removeEnd('\n');
            }

            if (String.isNotBlank(sAcclId)) {
                sDecryptAcclId = getDecryptedString(sAcclId);
            }
            if (String.isNotBlank(sAcclBenfitCode)) {
                sDecryptAcclBenefitCode = getDecryptedString(sAcclBenfitCode);
            }
            if (String.isNotBlank(sMemberId)) {
                sDecryptMemberId = getDecryptedString(sMemberId);
            }
            if (String.isNotBlank(sDependentCode)) {
                sDecryptDependentCode = getDecryptedString(sDependentCode);
            }
            
            return new HUMPreChatResultWrapperHelper(sDecryptMemberGenKey, sDecryptLsessionID, sDecryptBotTranscript, sDecryptAcclId, sDecryptAcclBenefitCode, sDecryptMemberId, sDecryptDependentCode);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController','decryptClientUserData');
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
            blob  cryptoKey = blob.valueOf(GLOBAL_CONSTANT_HUM.CRYPTOKEY_LIVEAGENT_HUM);
            blob hextoBLOBEncrypt = EncodingUtil.convertFromHex(sTobeDecrypted);
            Blob decryptedData = Crypto.decryptWithManagedIV(GLOBAL_CONSTANT_HUM.CRYPTOALGORITHM_LIVEAGENT_HUM,cryptoKey,hextoBLOBEncrypt);
            String sDecryptedData = decryptedData.toString();
            return sDecryptedData;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController','getDecryptedString');
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
            boolean isError = HUMExceptionHelper.processUpsertResults(lstSvr, lstCustomTranscript,'HUMPreChatController', 'saveCustomTranscriptOnEncryption', 'Humana_Chat_Transcript__c');
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
            HUMExceptionHelper.logErrors(e,'HUMPreChatController','onCommitTranscript');
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
    * Logs Errors in Error_Log__c objet
    * <p>
    *     *
    * @param  headerParams header parameters.
    * @param  pageParams   Page parameters.
    * @param  sSurveryLink Survey monkey url
    * @param  sIntName     Interaction name
    */
    @RemoteAction
    global static void logInteractionDetails(String headerParams,String pageParams, String sSurveryLink,String sIntName)
    {
		try
		{
			If(String.IsNotBlank(sIntName))
			{
				logErrors('Survey Monkey Url: ' + sSurveryLink ,'HUMPreChatController','logInteractionDetails');
			}
			else
			{
				logErrors('Survey Monkey Url: ' + sSurveryLink ,'HUMPreChatController','logInteractionDetails');
				logErrors('Empty Interaction Id ... Page Parameters: ' + pageParams + ' Header Parameters: ' + headerParams,'HUMPreChatController','logInteractionDetails');
			}
		} 
		catch(Exception e)
		{
			HUMExceptionHelper.logErrors(e,'HUMPreChatController','logInteractionDetails');
		} 
    }
	
    /**
    * Method called from Page as remote action to log the Prechat variables. 
    *
    * @param sCustomAccountId  Prechat Variable
    * @param sAccountId Prechat Variable
    * @param sIntId  Prechat Variable
    * @param sIntMemberId Prechat Variable
	* @param sIntName Prechat Variable
    */    
    @RemoteAction
    global static void logPreChatVariables(String sCustomAccountId , String sAccountId, String sIntId, String sIntMemberId , String sIntName)
    {
		try
		{
			Error_Log__c errorLog = new Error_Log__c();
			errorLog = new Error_Log__c(Error_Code__c ='PreChat Logging',
			Error_Message__c = 'sCustomAccountId: ' + sCustomAccountId + ' , sAccountId: '+sAccountId+ ' ,  sIntId: '+sIntId+ ' , sIntMemberId: '+sIntMemberId + ' , sInteractionName: ' +sIntName ,
			Request_Sent__c = true,
			class_name__c = 'HUMPreChatController',
			Method_Name__c = 'logPreChatVariables',
			Type__c = 'Application Error');        
			database.insert(errorLog); 
		} 
		catch(Exception e)
		{
			HUMExceptionHelper.logErrors(e,'HUMPreChatController','logInteractionError');
		}      
    } 
    
    
    /**
    * Method called from HUMPostWebChatPage and Go365PostChat_VF_HUM ActionFunction. 
    * This method is used to save the transcript
    * @param None
    * @return None   
    */  
    public PageReference goToHUMSaveChatTranscriptPage() 
    {    
        PageReference pageRef= new PageReference('/apex/HUMSaveChatTranscript'); 
        pageRef.setRedirect(false);        
        return pageRef;
    }
    
     /**
    * postchatinit Method constructor.
    * <p>
    * This method is used to read the page parameters.
    * @param  NA                                
    * @return NA                 
    */
    public void postchatinit() 
    {
        try
        {
            pageHeaders ='';
            bGSO = false;
            bRSO = false;
            sButtonId = Apexpages.currentpage().getparameters().get('buttonId');
            
            if(sButtonId!=null)
            {
                list<LiveChatButton> LCB = new list<LiveChatButton>();
                LCB = [Select Id, DeveloperName, MasterLabel from LiveChatButton where ID=:sButtonId];
                BtnName = LCB[0].DeveloperName;
               
                if (BtnName.contains('RSO'))
                    bRSO = true;
                else
                    bGSO = true;
            }
            
            for (String key : ApexPages.currentPage().getParameters().keySet())
            {      
                if(key =='attachedRecords')
                {
                    String interParam= ApexPages.currentPage().getParameters().get(key);
                    interParam = interParam.substringBetween('"Interaction__c":"','"');
                    if(interParam!= null && String.isNotBlank(interParam))
                    {
                        Interaction__c intObj = new Interaction__c();
                        intObj = [Select id,Name from Interaction__c where id=:interParam];
                        if(intObj!= null)
                        sInteractionName =  intObj.Name;
                    }
                    
                }
                else if (key == 'transcript')  
                {
                    webChatTranscriptDetail = ApexPages.currentPage().getParameters().get(key);
                }
                

            }
            
            if (sWebChatVarMap.get('EnableInteractionLogging') != null && sWebChatVarMap.get('EnableInteractionLogging').Value__c == 'true') 
            {
                for (String key : ApexPages.currentPage().getHeaders().keySet()) 
                {
                    
                    pageHeaders += '(Key: ' + key + ' Value: ' + ApexPages.currentPage().getHeaders().get(key) + ') ';
                }   
            }

        }
        catch(exception e)
        {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,'An Error Occured -- Please try again. If the issue persists contact Help Desk.'));
        }
    }
}