/*******************************************************************************************************************************
Apex Class Name : HUMPreChatController_CommercialB2B
Version         : 1.0
Created On      : 09/02/2020
Function        : 1. This class serves as Controller of the HUMPreChat_CommercialB2B.
2. This Class Is being used by site user, this user Is Not having access to metadata such as custom settings being used in the class.
Hence in order to fulfil this requirement, running this class in'Without Sharing' mode.

Test Class      : HUMPreChatController_CommercialB2BTest
Modification Log: 
* Modification ID  Developer Name          Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0               Akshay Pai                                     09/02/2020                   Original Version(REQ-1133858)
****************************************************************************************************************************/
global without sharing class HUMPreChatController_CommercialB2B
{
    /**
	* This is a constructor for the class.
	*
	*/
    public HUMPreChatController_CommercialB2B() 
    {
        getWebChatConstants();         
    }
    
    public String sTrans {Get;Set;}
    public String sSecondary_Chat_Id {Get;Set;}
    public String[] stringCharacters {Get;Set;}
    public map<String,HUMWebChatConstants__c> sWebChatVarMap{get;set;}
    public static String sDecryptMemberGenKey {get; set;}
    public static String sDecryptLsessionID{get; set;}
    public static String sInteractionName{get;set;}
    public transient String pageParams { get; private set; }
    public String webChatTranscriptDetail { get; set; }
    public static String sDecryptBotTranscript{get; set;}
    public static String sChatType{get;set;}
    public static String chatBtnName{get;set;}
    public static String skillName{get;set;}
    
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
        
        pageParams = '';
        
        for (String key : ApexPages.currentPage().getParameters().keySet())
        {
            
            if (key != 'transcript')  
            {
                pageParams += '(Key: ' + key + ' Value: ' + ApexPages.currentPage().getParameters().get(key) + ') ';
            }                
            
        }
    }
	
    /**
	* initVariable Method constructor.
	* <p>
	* This method is used to initialize the custom setting variables.
	*
	* @param  
	* @return void
	*/
    public void initVariable() 
    {
        try
        {
            sChatType = System.Label.chatTypeHumanaSecure;        
            sSecondary_Chat_Id = '';
            
            
            for(LiveChatButton btn : [SELECT Id, DeveloperName, MasterLabel, skillId,skill.MasterLabel FROM LiveChatButton WHERE Type =: GLOBAL_CONSTANT_HUM.LIVEAGENT_BUTTON_TYPE and DeveloperName =: GLOBAL_CONSTANT_HUM.COMMERCIAL_B2B_BUTTON_API_NAME])
            {
                chatBtnName = btn.MasterLabel;
                skillName = btn.skill.MasterLabel;
                
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
			
			Datetime currentTime = Datetime.now();
            String secTempDateTime = currentTime.format('YYYYMMddHHmmss', UserInfo.getTimeZone().getID())+currentTime.millisecond();
            
            sSecondary_Chat_Id = sSecondary_Chat_Id+'-'+secTempDateTime;
            sSecondary_Chat_Id = sSecondary_Chat_Id.replaceAll( '\\s+', '');
            
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController_CommercialB2B','initVariable');
        }
    }
    
    /**
	* This method accepts Customer ID sent by VF page and finds corresponding Acount.
	* <p>
	* It creates Interaction record, and affiliates it to Account Created earlier. Extracts Classification,Intent and Policy Member details from the data passed.
	*
	* @param  sSourceSystem                                 sSourceSystem
	* @param  sCustomerType                                 sCustomerType
	* @param  sCustomerId                                   encrypted sCustomerId
	* @param  sClassification                               sClassification
	* @param  sIntent                                       sIntent
	* @param  sPolicyNo                                     sPolicyNo
	* @param  sStartDate                                    sStartDate
	* @param  sEndDate                                      sEndDate
	* @param  sLsessionID                                   encrypted sLsessionID
	* @param  sBotTranscript                                encrypted sBotTranscript
	* @return HUMPreChatResultWrapper_CommercialB2B                 generated interaction details in wrapper
	*/
    @RemoteAction
    global static HUMPreChatResultWrapper_CommercialB2B findAccountByCustomerId(String sSourceSystem, String sCustomerType, String sCustomerId, String sClassification, String sIntent, String sPolicyNo, String sStartDate, String sEndDate,String sLsessionID, String sBotTranscript) 
    {
        try
        {   
            Id caseRecordTypeID = Schema.SObjectType.Case.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_HUM.GROUP_CASE_RECTYPE_API_NAME).getRecordTypeId();
            String interactingWith ='';
            String accSearchQuery ='';
            String classification = '';
            String intent = '';
            String CTCI = '';
            String policyMember = '';
            
            if( String.isNotBlank(sSourceSystem) && sSourceSystem.trim().toUppercase() == GLOBAL_CONSTANT_HUM.SOURCE_SYSTEM_IVAWATSON && String.isNotBlank(sCustomerType) && (sCustomerType.trim().toUppercase() == GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_GROUP || sCustomerType.trim().toUppercase() == GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_AGENT) )
            {        
                if(String.isNotBlank(sCustomerId))
                {
                    String sDecryptCustomerID = getDecryptedString(sCustomerId);
                    if(String.isNotBlank(sDecryptCustomerID))
                    {
                        accSearchQuery = 'Select Id FROM Account WHERE ';
                        
                        if(sCustomerType.trim().toUppercase() == GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_GROUP)
                        {
                            Id grpAccRecordTypeID = Schema.SObjectType.Account.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_HUM.GROUP_ACC_RECTYPE_API_NAME).getRecordTypeId();
                            interactingWith = GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_GROUP;
                            accSearchQuery += 'RecordTypeID =\''+grpAccRecordTypeID+'\' and Group_Number__c =\''+sDecryptCustomerID+'\' LIMIT 1';
                        }
                        else if(sCustomerType.trim().toUppercase() == GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_AGENT)
                        {
                            Id agtAccRecordTypeID = Schema.SObjectType.Account.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_HUM.AGENT_ACC_RECTYPE_API_NAME).getRecordTypeId();
                            interactingWith = GLOBAL_CONSTANT_HUM.CUSTOMER_TYPE_AGENT;
                            accSearchQuery += 'RecordTypeID =\''+agtAccRecordTypeID+'\' and Agent_ID__c =\''+sDecryptCustomerID+'\' LIMIT 1';
                            caseRecordTypeID = Schema.SObjectType.Case.getRecordTypeInfosByDeveloperName().get(GLOBAL_CONSTANT_HUM.AGENT_CASE_RECTYPE_API_NAME).getRecordTypeId();
                        }
                    }
                    else
                    {
                        logErrors('Decrypted customerId is null for customerId '+ sCustomerId,'HUMPreChatController_CommercialB2B','findAccountByCustomerId' );
                        return null;
                    }
                }
                else
                {
                    logErrors('CustomerId is null '+ sCustomerId,'HUMPreChatController_CommercialB2B','findAccountByCustomerId' );
                    return null;
                }
            }
            else
            {
                logErrors('Either Source system :'+sSourceSystem+' or Customer Type :'+sCustomerType+' is not valid','HUMPreChatController_CommercialB2B','findAccountByCustomerId' );
                return null;        
            }
            
            if(String.isNotBlank(accSearchQuery))
            {
                list< Account > lstAccount = new list< Account >();
                
                lstAccount = database.query(accSearchQuery);
                
                if (!lstAccount.isEmpty()) 
                {
                    Interaction__c oInt = new Interaction__c(Interacting_With__c = lstAccount[0].Id,
                                                             Interacting_With_Type__c = interactingWith,
                                                             Interaction_Origin__c = GLOBAL_CONSTANT_HUM.WEBCHAT_LIVEAGENT_HUM );
                    
                    Database.saveResult oSvr = Database.insert(oInt,false);
                    List<Database.SaveResult> listSaveResults = new List<Database.SaveResult>{oSvr};
                        List<interaction__C> lstInteractions = new List<interaction__C>{oInt};
                            boolean isError = HUMExceptionHelper.processSaveResults(listSaveResults,lstInteractions,'HUMPreChatController_CommercialB2B','findAccountByCustomerId','Interaction__C');
                    
                    if (!oSvr.isSuccess())
                        return null;
                    
                    oInt = [Select Name,Interacting_With__c from Interaction__c where Id=:oInt.id];
                    
                    Interaction_member__c oHUMIntMember = new Interaction_member__c(Interacting_about_type__c = interactingWith,
                                                                                    Interacting_about__c =lstAccount[0].Id,
                                                                                    Interaction__c = oInt.ID );
                    
                    Database.saveResult oSvrIntMem = Database.insert(oHUMIntMember,false);
                    List<Database.SaveResult> listSaveResultsIntMembers = new List<Database.SaveResult>{oSvrIntMem};
                        List<Interaction_member__c> lstInteractionMembers = new List<Interaction_member__c>{oHUMIntMember};
                            isError = HUMExceptionHelper.processSaveResults(listSaveResultsIntMembers,lstInteractionMembers, 'HUMPreChatController_CommercialB2B','findAccountByCustomerId','Interaction_member__c');
                    
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
                    
                    return new HUMPreChatResultWrapper_CommercialB2B(oInt.Interacting_With__c, oInt.Id, oHUMIntmember.id, oInt.Name, interactingWith, HUMConstants.HUMWebChatText, HUMConstants.CASE_TYPE,classification,intent,policyMember,CTCI,caseRecordTypeID,sDecryptLsessionID,sDecryptBotTranscript); 
                }
                else
                {                   
                    logErrors('No Accounts fetched for given customerID','HUMPreChatController_CommercialB2B','findAccountByCustomerId');
                    return null;
                }
            }
            else
            {
                logErrors('Invalid inputs received, Account Search Query is Blank'+ accSearchQuery,'HUMPreChatController_CommercialB2B','findAccountByCustomerId' );
                return null;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMPreChatController_CommercialB2B','findAccountByCustomerId');
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
            HUMExceptionHelper.logErrors(e,'HUMPreChatController_CommercialB2B','getDecryptedString');
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
	* Method called from HUMPostWebChatPage and Go365PostChat_VF_HUM ActionFunction. 
	* This method is used to save the transcript
	* @param None
	* @return None   
	*/  
    public PageReference goToHUMSaveChatTranscriptPage() 
    {    
        PageReference pageRef= new PageReference('/apex/HUMSaveChatTranscript_CommercialB2B'); 
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
            for (String key : ApexPages.currentPage().getParameters().keySet())
            {      
                if(key =='chatDetails')
                {
            
                    String interName= ApexPages.currentPage().getParameters().get(key);
                    sInteractionName = interName.substringBetween('"label":"InteractionName","value":"','"');
                    
                }
                else if (key == 'transcript')  
                {
                    webChatTranscriptDetail = ApexPages.currentPage().getParameters().get(key);
                }
            }            
        }
        catch(exception e)
        {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,'An Error Occured -- Please try again. If the issue persists contact Help Desk.'));
        }
    }
}