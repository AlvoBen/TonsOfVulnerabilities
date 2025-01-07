/*******************************************************************************************************************************
Apex Class Name : CWPUnsecureChat_C_HUM 
Version         : 1.0
Created On      : 4/13/2023
Function        : 1. This class serves as Controller of the CWP Unsecure Prechat form (CWPUnsecurePreChat_VF_HUM).
                  2. This Class Is being used by site user, this user Is Not having access to metadata such as custom settings being used in the class.
                     Hence in order to fulfil this requirement, running this class in 'Without Sharing' mode.

Test Class      : CWPUnsecureChat_T_HUM
Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Alvaro Madrid                                    04/13/2023                Original Version
 * 1.0              Shivani Shrivastav                               04/17/2023                Original Version
 * 1.0				Shivani Shrivastav                               09/29/2023              US5175974-Append interaction to post chat survey URL for CWP Unsecure 
 ****************************************************************************************************************************/
global without sharing class CWPUnsecureChat_C_HUM {
    //This is a constructor for the class.
   public CWPUnsecureChat_C_HUM() 
   {
       initVariable();
       postchatinit();
       getWebChatConstants();                
   }

   public Boolean bShowError{get;set;}
   public String sSecondary_Chat_Id {get;set;}
   public map<String,HUMWebChatConstants__c> sWebChatVarMap{get;set;}
   public string chatBtnId {get;set;}
   public transient String pageParams { get; private set; }
   public String webChatTranscriptDetail { get; set; }
   public static String sSkillName{get;set;}
   public static String sBtnSkillId{get; set;}
   public static String sInteractionName{get;set;}
   public String sUserQueue {get;set;}
   public static String sDecryptBotTranscript{get; set;}

   public Boolean bSwitch {Get;Set;}
   public Boolean botTranscriptSwitch {Get;Set;}
   public String sIntName {get;set;}
   public Boolean preChatSurveySwitch {Get;Set;}
   public Boolean caseTabSwitch {get;set;}
   public Boolean sSwitchForInteraction{get;set;}
   public static String sChatType{get;set;}
   
   /*Unsecure routing*/
   public String btnMapJson {get;set;}
   public String btnSkillMapJson {get;set;}
   public static Map<String, LiveChatButton> mapButtonIdButton;
   
   /**
   * getWebChatConstants()
   * <p>
   * This method is used to fetch chat related customs setting values.
   * @param  NA                                
   * @return NA                 
   */
   public void getWebChatConstants() 
   {
       try
       {
           bShowError = false;
           bSwitch = HUMUtilityHelper.isCRMFunctionalityON('1840251');
           botTranscriptSwitch = HUMUtilityHelper.isCRMFunctionalityON('3750632');
           preChatSurveySwitch = HUMUtilityHelper.isCRMFunctionalityON('3786815');
           caseTabSwitch = HUMUtilityHelper.isCRMFunctionalityON('3796187');
           sSwitchForInteraction = HUMUtilityHelper.isCRMFunctionalityON('5175974');
           sWebChatVarMap = new Map<String,HUMWebChatConstants__c>();
           sWebChatVarMap = HUMWebChatConstants__c.getAll();
       }
       catch(exception e)
       {  
           bShowError = true;
           HUMExceptionHelper.logErrors(e,'Go365UnsecureChat_C_HUM','getWebChatConstants');
       }
   }
   /**
   * it Method constructor.
   * <p>
   * This method is used to read the page parameters.
   * @param  NA                                
   * @return NA                 
   */
   public void postchatinit()  
   {
       try
       {
           string intId;
           pageParams = '';
           for (String key : ApexPages.currentPage().getParameters().keySet())
           {   
              if(key =='attachedRecords')
               {
                   String interParam= ApexPages.currentPage().getParameters().get(key);
                   interParam = interParam.substringBetween('"Interaction__c":"','"');
                   if(interParam!= null && String.isNotBlank(interParam))
                   {
                       intId = interParam;
                   }
               }
               if (key != 'transcript')  
               {
                   pageParams += '(Key: ' + key + ' Value: ' + ApexPages.currentPage().getParameters().get(key) + ') ';
               }                
               else
               {
                   webChatTranscriptDetail = ApexPages.currentPage().getParameters().get(key);
               }
           }
                       
           if (string.isNotBlank(intId))
           {
               Interaction__c intObj1 = new Interaction__c();
               intObj1 = [Select id,Name from Interaction__c where id=:intId];
               if(intObj1!= null)
               {
                   sIntName =  intObj1.Name;
               }
           }
       }
       catch(exception e)
       {
           ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,'An Error Occured -- Please try again. If the issue persists contact Help Desk.'));
       }
   }
   /**
   * initVariable Method constructor.
   * <p>
   * This method is used to initialize the custom setting variables and to store web chat parameters
   *
   * @return NA                 
   */
   public void initVariable() 
  {
      try
      {
          sChatType = 'CWP Unsecure';
          chatBtnId ='';
          List<User> userLst = [Select id, Current_Queue__c from User where ID=:UserInfo.getUserId()];
          sUserQueue = userLst[0].Current_Queue__c; 
          sSecondary_Chat_Id = '';

          for(LiveChatButton btn : [SELECT Id, DeveloperName, MasterLabel, skillId,skill.MasterLabel FROM LiveChatButton WHERE Type =: GLOBAL_CONSTANT_HUM.LIVEAGENT_BUTTON_TYPE and DeveloperName ='CWP_Unsecure'])
          {      
              string btnId = btn.Id;
              chatBtnId = btnId.substring(0,15);
              sSkillName = btn.skill.MasterLabel;
              sBtnSkillId = btn.skillId;
          }
          
          String pParam;
          for (String key : ApexPages.currentPage().getParameters().keySet())
          {
              if(key =='endpoint')
              {
                  pParam = ApexPages.currentPage().getParameters().get(key); 
                  List<string> stringCharacters = pParam.split('&');
                  for(string sChar: stringCharacters)
                  {
                      if(sChar.contains('session_id'))
                      {
                           string sChar1 = sChar.remove('session_id=');
                           string sChar2 = sChar1.remove(')');                     
                           sSecondary_Chat_Id = sChar2.trim();                         
                      }                   
                  }            
              }
          }
          generateCustomChatKey();   
      }
      catch(exception e)
      {
          ApexPages.addmessage(new ApexPages.message(ApexPages.severity.WARNING,'An Error Occured -- Please try again. If the issue persists contact Help Desk.'));
      }
  }
   
   /**
   * generateCustomChatKey  Method constructor.
   * <p>
   * This method is used to populate secondary chat id and other required web chat details.
   *
   */
    public void generateCustomChatKey() 
   {
       try
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
       catch(Exception e)
       {
           HUMExceptionHelper.logErrors(e,'CWPUnsecureChat_C_HUM','generateCustomChatKey');
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
   global static String findInteractionName(String chatDetails)
   {
       try
       {
           String intNameReturn= '';              
            if(!String.isEmpty(chatDetails))
             for(LiveChatTranscript lctObj : [Select id, Interaction__r.Name from LiveChatTranscript where Secondary_Chat_ID__c=: chatDetails Limit 1 ])
             {
               intNameReturn =   lctObj.Interaction__r.Name;
             }
           return intNameReturn;           
       }
       catch(Exception e)
       {
           HUMExceptionHelper.logErrors(e,'CWPUnsecureChat_C_HUM','findInteractionName');
           return null;
       }
   }
   
   /**
   * Method called from CWP Unsecure Post Chat
   * This method is used to save the transcript
   * @param None
   * @return None   
   */  
   public PageReference goToHUMSaveChatTranscriptPage() 
   {  
       PageReference pageRef = Page.CWPUnsecureSaveChatTranscript_HUM;
       pageRef.setRedirect(false);             
       return pageRef;
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
   public static void onCommitTranscript(string sChatKey,String sUserID)
   {
       try
       {
           if(!String.isEmpty(sChatKey) && !String.isEmpty(sUserID))
           {
               //Calling method for insertion by passing chat key,agent salesforce ID.
               List<Humana_Chat_Transcript__c> lstCustomTranscript = new List<Humana_Chat_Transcript__c>();
               Humana_Chat_Transcript__c objChat = new Humana_Chat_Transcript__c();
               objChat.Chat_Key__c = sChatKey;
               objChat.OwnerId = Id.valueOf(sUserID);
               lstCustomTranscript.add(objChat);
               //Upserting Custom Chat Object record.
               Schema.SObjectField keyFieldName= Humana_Chat_Transcript__c.Fields.chat_Key__c;
               Database.UpsertResult [] lstSvr = Database.upsert(lstCustomTranscript,keyFieldName, false);
               boolean isError = HUMExceptionHelper.processUpsertResults(lstSvr, lstCustomTranscript,'CWPUnsecureChat_C_HUM', 'onCommitTranscript', 'Humana_Chat_Transcript__c');
           }
           else
           {
               logErrors('Empty Chat Key or sUserId' ,'CWPUnsecureChat_C_HUM','onCommitTranscript');                       
           }
       }
       catch(Exception e)
       {
           HUMExceptionHelper.logErrors(e,'CWPUnsecureChat_C_HUM','onCommitTranscript');
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
   * Method will process the encrypted data.
   * <p>
   * This method Accepts Session ID & IVA bot transcript which is in Encrypted form and convert it into Decrypted form as String.

   * @param  sBotTranscript                                encrypted sBotTranscript
   * @return HUMPreChatResultWrapperHelper                 decrypted values in wrapper
   */
   @remoteAction
   global static HUMPreChatResultWrapperHelper decryptUserIdentifiers(String sBotTranscript) 
   {
      try
      {    
           if(!String.isEmpty(sBotTranscript))
           { 
               sDecryptBotTranscript = '';              
               String strFormatEncryptedString = getDecryptedString(sBotTranscript).replace('\\n', '<br>');          
           
               for(String bstr : strFormatEncryptedString.split('<br>')){
                   sDecryptBotTranscript += bstr+'\n';
               }
               sDecryptBotTranscript = sDecryptBotTranscript.removeEnd('\n');
           }
           return new HUMPreChatResultWrapperHelper(sDecryptBotTranscript);
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
}