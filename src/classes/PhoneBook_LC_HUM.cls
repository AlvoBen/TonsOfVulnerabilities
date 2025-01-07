/*******************************************************************************************************************************************************
Apex Class Name  : PhoneBook_LC_HUM
Version          : 1.0
Created Date     : 02/09/2021
Function         : This controller class contains GetCallData and PhoneBook related webservices
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            		 Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                              02/09/2021                 Original Version
* Nikhil                                                 02/22/2021                 Implementing softphone logging. 
* Arpit Jain/Navajit Sarkar                              03/09/2021                 PhoneBook webservices
* Arpit Jain/Navajit Sarkar                              07/12/2021                 US-2357061 : IVR Call End or Transfer
* Harshada Kamble                                        08/19/2022                 User Story 3653389: PCC Genesys Migration - Transfer to Provider Survey
* Harshada Kamble                                        02/03/2023                 US 3980680: T1PRJ0036776: RxE - Save Campaign name and keycode to CRM interactions
* Santhi Mandava                                         02/06/2013                 User Story 4082261,4084543: Display interaction information on search page and account detail page.
* Sivaprakash Rajendran                                  05/04/2023                 US 4579434 - T1PRJ0036776: Enterprise - Caller Type Population with Phonebook - Lightning (Genesys)
**********************************************************************************************************************************************/
public with sharing class PhoneBook_LC_HUM 
{
    public PhoneBook_LC_HUM() 
    {
    }

    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getMember details continuation requests
    *
    * @param  String uuid 
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object getCallData(String uuid)
    {
        Object ret = null;
        try
        {
            if(String.isBlank(uuid))
            {
                CalloutException e = new CalloutException();
                e.setMessage('UUIData not available!!');    
                throw e;
            }
            else
            { 
                ret = PhoneBook_LS_HUM.getCallData(uuid);
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'getCallData');
        }
        return ret;
    }
    
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getDefault Phonebook continuation requests
    *
    * @param  String body 
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object getDefaultPH(String body)
    {
        Object ret = null;
        try
        {
            ret = PhoneBook_LS_HUM.getDefaultPH(body);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'getDefaultPH');
        }
        return ret;
    }
    
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getAllPhoneBookRules continuation requests
    *
    * @param void 
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object getAllPhoneBookRules()
    {
        Object ret = null;
        try
        {
            ret = PhoneBook_LS_HUM.getAllPhoneBookRules();
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'getAllPhoneBookRules');
        }
        return ret;
    }
    
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the callTransfer continuation requests
    *
    * @param  Object callTransferData 
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object callTransfer(Object callTransferData)
    {
        Object ret = null;
        try
        {
            ret = PhoneBook_LS_HUM.callTransfer(callTransferData);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'callTransfer');
        }
        return ret;
    }

     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the getCallData service response
     *
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled
    public static Object getprocessResponse(Object state) 
    {
        String phoneBookRulesRes='';
        try
        {
            String contId = String.valueof(state);
            HttpResponse response = Continuation.getResponse(contId);
            if(response != null && ((response.getStatusCode() == PhoneBookConstants_H_HUM.HTTP_OK_HUM || response.getStatusCode() == PhoneBookConstants_H_HUM.HTTP_ACCEPTED_HUM) && (!response.getBody().containsIgnoreCase(PhoneBookConstants_H_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                phoneBookRulesRes = response.getBody();   
            }
            else
            {
                throw new CalloutException('Error occured: GetCallData web service returned fault response: '+response.getBody());
            }     
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logError(String.valueOf(e), 'Phonebook_LC_HUM', 'getprocessResponse','System.CalloutException','Integration Error');
        }
        return phoneBookRulesRes;
    }

     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the GetDefaultPhonebook service response
     *
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled
    public static Object processResponseGetDefaultPhonebook(Object state)
    {
        String defaultPhonebooks;
        try
        {
            String contId = String.valueof(state);
            HttpResponse response = Continuation.getResponse(contId);
            defaultPhonebooks = response.getBody();
            if(!defaultPhonebooks.containsIgnoreCase('faultstring'))
            {
                return defaultPhonebooks;
            }
            else 
            {
                throw new CalloutException('Error occured: RoutingXML web service returned fault response: '+defaultPhonebooks);
            }
        }
        catch(Exception e)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(e), 'Phonebook_LC_HUM', 'processResponsegetDefaultPhBookRules','System.CalloutException','Integration Error');
            defaultPhonebooks = 'There was an issue retrieving the call data and transfer phonebook. Continue using other phonebooks or please refresh your browser and try again. If this issue persists, contact the help desk for assistance: '+errorLogNum;
        } 
        return defaultPhonebooks;
    }     

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the getAllPhoneBookRules service response
     *
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled
    public static Object processResponsegetAllPhoneBookRules(Object state) 
    {
        String phoneBookRulesRes='';
        try
        {
            String contId = String.valueof(state);
            HttpResponse response = Continuation.getResponse(contId);
            if(response != null && ((response.getStatusCode() == PhoneBookConstants_H_HUM.HTTP_OK_HUM || response.getStatusCode() == PhoneBookConstants_H_HUM.HTTP_ACCEPTED_HUM) && (!response.getBody().containsIgnoreCase(PhoneBookConstants_H_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                phoneBookRulesRes = response.getBody();  
            }
            else
            {
                throw new CalloutException('Error occured: AllPhoneBookRules web service returned fault response: '+response.getBody());
            } 
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logError(String.valueOf(e), 'Phonebook_LC_HUM', 'processResponsegetAllPhoneBookRules','System.CalloutException','Integration Error');
        }        
        return phoneBookRulesRes;
    }

     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse the CallTransfer service response
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled
	public static Object processCallTransferResponse( Object state)
	{
        String routingResult='';
        try
	    {
            String contId = String.valueof(state);
            HttpResponse response = Continuation.getResponse(contId);
            routingResult = response.getBody();
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'processCallTransferResponse'); 
        }        
        return routingResult;
	}
    
	 /**
     * Returns void
     * <p>
     * method to create logging records
     *
     * @param  String callAction
     * @param  String callInfoLog
     * @param  String callLogType
     * @param  String SPExtrnlId
     * @param  String reason
     * @param  String category
     * @param  String caller
     * @return void
     */
    @AuraEnabled
    public static void createRecord(String callAction,String callInfoLog,String callLogType,String SPExtrnlId,String reason, String category, String caller,String UUID) 
    {
        try
        {
            PhoneBook_LS_HUM.createRecord(callAction, callInfoLog, callLogType, SPExtrnlId, reason, category, caller , UUID);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'createRecord');
        }
    }
    
    /**
    * Returns void
    * <p>
    * Method to insert record into Integration_Mapping__c object when call transfer button is clicked
    *
    * @param  Object callTransferData 
    * @return void
    */
    @AuraEnabled
    public static void insertIntegrationmapping(Object callTransferData)
    {
        try
	    {
            PhoneBook_LD_HUM.insertIntegrationmapping(callTransferData);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'insertIntegrationmapping');
        }
    }
    
    /**
    * Returns Interacting About Account Id
    * <p>
    * Method to get Interacting About Account Id on transferred call
    *
    * @param  String uuid 
    * @return String
    */
    @AuraEnabled
    public static Map<String,String> getAboutAccountId(String UUID,Boolean isrefresh,String authIndicator,String genesysInteractionId,Object searchModal, String calllogattributes)
    {
        String aboutId = '';
        Map<String,String> mapIntAccountInfo = new Map<String,String>();
        try
	    {
            mapIntAccountInfo = PhoneBook_LD_HUM.getAboutAccountId(UUID,isrefresh,authIndicator,genesysInteractionId,searchModal,calllogattributes);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'getAboutAccountId');
        }
        return mapIntAccountInfo;
    }
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the saveCallData continuation requests on call disconnect
    *
    * @param  Object disconnectattributes
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object disconnectSaveCallData(Object disconnectattributes)
    {
        Object ret = null;
        try
        {
            String searchDataString = JSON.serialize(disconnectattributes);
            Map < String, object > searchDataMap = (Map < String, object > ) JSON.deserializeUntyped(searchDataString);
            String UUID = (String) searchDataMap.get('UUID');
            if(!String.isBlank(UUID))
            {
            ret = PhoneBook_LS_HUM.disconnectSaveCallData(disconnectattributes);
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'disconnectSaveCallData');
        }
        return ret;
    }

    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the saveCallData continuation requests on consult/blind transfer
    *
    * @param  Object transferattributes
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object consultBlindSaveCallData(Object transferattributes)
    {
        Object ret = null;
        try
        {
            ret = PhoneBook_LS_HUM.consultBlindSaveCallData(transferattributes);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'consultBlindSaveCallData');
        }
        return ret;
    }

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to get the savecalldata response on call disconnect
     *
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled
    public static Object respdisconnectSaveCallData(Object state) 
    {
        String result='';
        try
	    {
            String contId = String.valueof(state);
            HttpResponse response = Continuation.getResponse(contId);
            result = response.getBody();
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'respdisconnectSaveCallData'); 
        }        
        return result;
    }

    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to get the savecalldata response on consult/blind transfer
     *
     * @param  Object State of Continuation
     * @return Object
     */
    @AuraEnabled
    public static Object respConsultBlindSaveCallData(Object state) 
    {
        String result='';
        try
	    {
            String contId = String.valueof(state);
            HttpResponse response = Continuation.getResponse(contId);
            result = response.getBody();
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'respConsultBlindSaveCallData'); 
        }        
        return result;
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
        try
        {
            switchMap = PhoneBook_LD_HUM.fetchSwitchs();
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'PhoneBook_LC_HUM', 'fetchSwitchs');
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
    @AuraEnabled
    public static Map<String,String> getSecurePaymentsDetails()
    {
        Map<String,String> securePaymentsMap = new Map<String,String>();
        try 
        {
            securePaymentsMap = PhoneBook_LD_HUM.getSecurePaymentsDetails();
        } 
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'getSecurePaymentsDetails');
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
    @AuraEnabled
    public static MemberPlan getMemberPlanRecord(String recid){
        MemberPlan mObj;
        try 
        {
            mObj = PhoneBook_LD_HUM.getMemberPlanRecord(recid);
        } 
        catch (Exception ex) 
        {
           HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'getMemberPlanRecord');
        }
        return mObj;
    }
    /**
    * Returns interaction Id of the auto interaction created on inbound call
    * <p>
    * Method to create auto interaction on inbound call
    *
    * @return InteractionId
    */
    @AuraEnabled
    public static String createAutoInteraction(Object searchModal,String UUID,Boolean isrefresh,String genesysInteractionId, String calllogattributes){
        String interactionId = '';
        try 
        {
            Boolean Switch_2230000 = PhoneBook_LD_HUM.fetchSwitchs().get('Switch_2230000');
            if(Switch_2230000 != false)
            {
                interactionId = PhoneBook_LD_HUM.createAutoInteraction( searchModal, UUID, isrefresh, genesysInteractionId,calllogattributes);
            }
        } 
        catch (Exception ex) 
        {
           HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'createAutoInteraction');
        }
        return interactionId;
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
        try 
        {
            accIdwithrecordType = PhoneBook_LD_HUM.getaccountWithRecordType(recId,obj);
        } 
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'getaccountWithRecordType');
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
    @AuraEnabled
    public Static String createInteraction(String uuid,String interactingWith,String genInteractionId, String interactingWithType,String origin)
    {
        String interactionId = '';
        Interaction_Member__c oIntMem = new Interaction_Member__c();
        try 
        {
            interactionId = PhoneBook_LD_HUM.createInteraction(uuid,interactingWith,genInteractionId,interactingWithType,origin);           
            if(interactionId != null)
            {
                oIntMem.Interacting_About__c =  interactingWith;
                oIntMem.Interacting_About_Type__c = interactingWithType;
                oIntMem.Interaction__c = interactionId;
                String intmemId = PhoneBook_LD_HUM.createInteractionMember(oIntMem);
            }
        } 
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'createInteraction');
        }
        return interactionId;
    }
    
    /**
    * Returns void
    * <p>
    * Method to update the UUID for the interaction
    *
    * @return void
    */
    @AuraEnabled
    public Static String updateUUIDInInteraction(String interactionId)
    {
        String UUIDataValue = '';
        try 
        {
            String dateString = Datetime.now().format('MMddYY');
            String currentTime = System.now().format('hhmmss',  UserInfo.getTimeZone().toString());
            List<Interaction__c> lstInteractionReclatest = [SELECT Id,Name, UUID__c, Genesys_Interaction_ID__c, CreatedDate FROM Interaction__c where Id=: interactionId]; 
            if(lstInteractionReclatest.size()>0)
            {
                UUIDataValue = dateString + '000'+ currentTime + '00000'+ lstInteractionReclatest[0].Name.right(12).substring(0,12); 
                lstInteractionReclatest[0].UUID__c = UUIDataValue;
                update lstInteractionReclatest;
            }
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'updateUUIDInInteraction');
        }
        return UUIDataValue;
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
        try 
        {
            mapIntAccountInfo = PhoneBook_LD_HUM.getClonedInteractionAboutId(UUID);
        } 
        catch (Exception ex) 
        {
        HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'getClonedInteractionAboutId');
        }
        return mapIntAccountInfo;
    }

    /**
    * Returns Integer
    * <p>
    * Method to get Hold time from custom setting
    *
    * @return Integer
    */
    @AuraEnabled
    public static Integer getHoldTime(String sTobefetched)
    {
        Integer holdTime;
        try 
        {
            holdTime = PhoneBook_H_HUM.getHoldTime(sTobefetched);
        } 
        catch (Exception ex) 
        {
           HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'getHoldTime');
        }
        return holdTime;
    }
    
    /**
    * Returns Transfer number for survey of tin case of provider call
    * @return surveyTransferNumber
    */
    @AuraEnabled
    public static string getSurveyTransferNumber()
    {
        string surveyTransferNumber;
        try 
        {
            SOFTPHONE_PRPID_HUM__c softphoneprpid = SOFTPHONE_PRPID_HUM__c.getValues(PhoneBookConstants_H_HUM.SURVEY_TRANSFER_NUMBER);
            if(softphoneprpid != null ) 
            {
                    surveyTransferNumber = softphoneprpid.Value__c;
            }
        }
        catch (Exception ex) 
        {
        HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'getSurveyTransferNumber');
        }
        return surveyTransferNumber;
    } 

    /**
    * Check user is having permission set access or not
    * @param String PermissionSetName
    * @returnParam Boolean bHasPermission
    */
    @AuraEnabled
    public static boolean fetchPermissionSetAccess(String sPermissionSetName){
        boolean bHasPermission = false;
        try{
            List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND PermissionSet.Name = :sPermissionSetName];
            if(null != lstPMAssign && !lstPMAssign.isEmpty()) bHasPermission = true;
            if(Test.isRunningTest()){throw new NullPointerException();}
        }catch(exception ex){
            bHasPermission = false;
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'fetchPermissionSetAccess');
        }
        return bHasPermission;
    }


    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the saveCallData continuation requests on survey transfer
    *
    * @param  Object transferattributes
    * @return Object
    */
    @AuraEnabled(continuation = true)
    public static Object surveyTransferSaveCallData(Object transferattributes)
    {
        Object ret = null;
        try
        {
            ret = PhoneBook_LS_HUM.surveyTransferSaveCallData(transferattributes);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'surveyTransferSaveCallData');
        }
        return ret;
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
            IntAttribute = PhoneBook_LD_HUM.updateInteractionAttribute(uuid);
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_LC_HUM', 'updateInteractionAttribute');
        }
        return IntAttribute;
    }

    /*
     * Method Name   :    getCallerTypeForPhoneBook
     * Description   :    //US#4579434 - Created for Caller Type Population with Phonebook.
     *                    This method fetches the matching caller Type from Custom Metadata and helps to setup correct Caller value on the phonebook
     *                    based on the interactingWithtype selected.
     * return Type   :    String
     * Parameters    :    String
     */
    @AuraEnabled(cacheable = true)
    public static String getCallerTypeForPhoneBook(String interactingWithType)
    {
        String callerType; 
        Phonebook_Caller_Type__mdt callerTypeData = [SELECT Caller_Type__c FROM Phonebook_Caller_Type__mdt where Interacting_With_Type__c = : PhoneBookConstants_H_HUM.PHONEBOOK_INTERACTING_WITH_TYPE_MAPPING ];        
            
        if(callerTypeData != null){
            Map<String, object> callerTypeJson = (Map<String, object>) JSON.deserializeUntyped(callerTypeData.Caller_Type__c);
            callerType = (String) callerTypeJson.get(interactingWithType);
        }
        return callerType;
    }

    /*
     * Method Name   :    getCallMemberAccountDetails
     * Description   :    //US#4579434 - Created for Caller Type Population with Phonebook.
     *                    This method calls the getCallMemberAccountDetails method in PhoneBook_LD_HUM
     * return Type   :    String
     * Parameters    :    String
     */
    @AuraEnabled
    public static String getCallMemberAccountDetails(Object searchModal)
    {
        String searchDataString = JSON.serialize(searchModal);
        Map<String, object> searchDataMap =   (Map<String, object>) JSON.deserializeUntyped(searchDataString);

        String INQA_ID = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_ID) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_ID) : '';
        String INQA_FIRST = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_FIRST) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_FIRST) : '';
        String INQA_LAST = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_LAST) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_LAST) : '';
        Boolean AUTHENTICATION_IND = searchDataMap.get(PhoneBookConstants_H_HUM.Authentication_IND) !=null ? (searchDataMap.get(PhoneBookConstants_H_HUM.Authentication_IND) == 'Y'?true:false) : false;
        String MEMBER_PERS_GEN_KEY = (String)searchDataMap.get(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.MEMBER_PERS_GEN_KEY) : '';
        String DependentCode = (String)searchDataMap.get(PhoneBookConstants_H_HUM.DependentCode) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.DependentCode) : '';
        String INQA_PLTFRM_CD = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD) != null ? (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD) : '';

        String intAboutId = '';
        try
        {
            intAboutId = PhoneBook_LD_HUM.getCallMemberAccountDetails(INQA_ID, DependentCode, MEMBER_PERS_GEN_KEY, INQA_FIRST, INQA_LAST, INQA_PLTFRM_CD, AUTHENTICATION_IND);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'PhoneBook_LC_HUM', 'getCallMemberAccountDetails');
        }
        return intAboutId;
    }
}