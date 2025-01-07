/***********************************************************************************************************************************
* Apex Class Name  : PhoneBook_C_HUM 
* Version          : 1.0
* Created Date     : 22nd January, 2021
* Function         : CRM Search Integration
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Rashmi Patil/Isha Gupta/Shreya Agrawal                            01/22/2021            Original Version
* Nikhil Malhotra/Ekta Bist											02/25/2021			  Implementing Softpohone logging. 
* Nikhil Malhotra                                                   03/05/2021            Modularising processResponse method.
* Shreya Agrawal/Isha Gupta											01/30/2021			  PhoneBook Implementation.
* Navajit Sarkar/Sahil Verma 	            					    07/12/2021            User story -2287945 added authentication parameter to the search URl
* Shreya Agrawal/Isha Gupta											07/13/2021            MF2- US-2357061  IVR End Call or Transfer – IVR Call Data End Agent Session
* Shreya Agrawal/Isha Gupta											07/16/2021            MF5- US-2230090  IVR Call Transfer passing Interaction Data
* Shreya Agrawal													08/03/2021            MF1- US-2484219  Phonebook Secure Payment UI Controls( PBS- eBilling) MF1
* Isha Gupta												        08/03/2021            US- 2484206  T1PRJ0036776_Phonebook Secure Payment UI Controls (E-post, Pharmacy)MF1
* Isha Gupta												        08/03/2021            US-2484212  T1PRJ0036776_Phonebook Secure Payment UI Controls(CBIS) MF1
* Isha Gupta												        09/01/2021            T1PRJ0036776_SFDC_Inbound Call - CRM Interaction Creation and Population from IVR Call [Member,Provider,Agent,Group]
* Shreya Agrawal                                                    09/30/2021            US-2614412, US-2611229, US-2697756 Breakfix Stories
* Shreya Agrawal                                                    10/15/2021            US-2564501, US-2230077, US-2564644, US-2564484 Outbound Auto Interaction creation Stories
* Sahil Verma/Shreya Agrawal                                        02/16/2022            US-2564501 - Check IVR interaction for inbound calls and create if not found
* Sahil Verma/ Shreya Agrawal                                       03/04/2022            US-2783145,2649919  T1PRJ0036776_SFDC_Secure Pay Flow Tabbed Structure within Phonebook [UI]
* Harshada Kamble                                                   08/16/2022            US-3701398 - T1PRJ0272347: Dev for IOP-2022-5960542 & INC1860844
* Harshada Kamble                                                   08/19/2022            User Story 3653389: PCC Genesys Migration - Transfer to Provider Survey
* Harshada Kamble                                                   09/30/2022            User Story 3791679: T1PRJ0036776: Genesys Softphone Transfer Phone Book Not Returning All Routing Rules Available for Members (INC1894888) 
* Harshada Kamble                                                   01/05/2023            US 3994079 : PCC Call Transfer Phonebook Transfers- Added IsSoftPhoneIndicator in input request to filter softphone routing rules 
* Harshada Kamble                                                   01/05/2023            US 3849706,3944555 : updating interaction origin as Inbound call in case of Watson or System interaction clone.  
* Harshada Kamble                                                   01/05/2023            US 3944531-T1PRJ0036776: UCID not mapping to UUID field in Salesforce CRM (INC2009416)
* Harshada Kamble                                                   02/03/2023            US 3980680: T1PRJ0036776: RxE - Save Campaign name and keycode to CRM interactions
* Harshada Kamble                                                   02/03/2023            US 3979946: T1PRJ0036776: PCC VOC Survey Transfer Identification 
* Harshada Kamble/Anil Pavithran                                    03/05/2023           User Story 4144165: T1PRJ0307696: CRM Service: Format is incorrect/Unable to authenticate - DOB Format
* Harshada Kamble/Anil Pavithran                                    03/09/2023            US 2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
* Sivaprakash Rajendran                                             04/27/2023            US 4040542: T1PRJ0036776: Enterprise - Caller Type Population with Phonebook - Classic (Genesys)
* Kiran Kumar Kotni                                                 05/10/2023           User Story 4420291: T1PRJ0036776: Change 2 Field Names - Correct RxE implementation & Provider Survey Checkbox
* Mayur Pardeshi                                                     06/16/2023				User Story 4726311: T1PRJ0036776: PCC Classic - Second Member Information Is Not Being Updated on 'Interacting About' field from Watson in a Multimember auth call (Genesys)
* Harshada Kamble                                                   06/22/2023            User Story 4696940: T1PRJ0036776: Classic - Multi-member transfer call handling
* Mayur Pardeshi                                                    07/05/2023	          User Story 4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
* Kiran Kotni                                                       08/04/2023            User Story 4878631: T1PRJ0036776: RxE - Classic - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
* Harshada Kamble                                                   01/19/2024            User Story 5602717: T1PRJ0036776: Pass NPI & Tax ID from Watson IVR to Genesys to CRM Salesforce Service
* Mayur Pardeshi                                                    02/05/2024            User Story 5668340: T1PRJ0036776: Alphabetize Phone Book
**********************************************************************************************************************************/
public with sharing class PhoneBook_C_HUM
{
    private Boolean mflag;
    private Boolean aflag;
    private Boolean pflag;
    private Boolean gflag;
    private String callerVal;
    public String sconLabel;
    public String AllPhBookRulesRequest; // Continuation Request
    public String defaultphBookRequest; // Continuation Request
    public String SaveCallDataRequest;  // Continuation Request
    public  PhoneBook_S_HUM  phshum  =  new PhoneBook_S_HUM();
    public Map<Object,Object> DataModal = new Map<Object,Object>();
    public Boolean bOnCallTransfer =false;
    public transient Map<String,String> PolicyCaseDataMap; 
    
    public String callAction {get; set; }
    public String uuidataValue {get; set; }
    public String searchPageURl {get;set; }
    public String CallerType {get;set;}    
    public String callInfoLog {get; set; }
    public String callLogType {get; set; }
    public String SPExtrnlId {get; set; }
    public String reason {get; set; }
    public String category {get; set; }
    public String callActionInt {get; set; }
    public String callLogInfoInt {get; set; }
    public String reasonInt {get; set; }
    public String idval {get; set; }   
    public String searchParam{ get; set;}
    public String uniqueTransferNumber{get;set;}
    public String deafultUniqueTransferNumber{get;set;}
    public String phoneBookSelected {get; set;}
    public String ParamsValueOnCallTransfer {get;set;} 
    public String errorMessage{get;set;}
    public String placeholderVal{get;set;} 
    public static String numberVal{ get; set;}
    public static String result {get;set;}
    public Boolean isDefaultPhoneBook {get;set;}
    public Boolean isnotDefaultPhoneBook {get;set;}    
    public Boolean isShowBox{get;set;}
    public Boolean isShowClickToDial{get;set;}
    public Boolean isCallConnected{get;set;}
    public Boolean isCallNotConnected{get;set;}
    public Boolean enableConsultTransfer{get;set;}
    public Boolean enableBlindTransfer{get;set;}
    public Boolean isSurveyButtonVisible{get;set;}     
    public boolean displayPopup{get; set;}
    public Boolean callerTypeDisable {get;set;}
    public Boolean bSwitchCallReasonCode{get;set;}
    public Boolean bSwitchSecurePay{get;set;}
    public Boolean bSwitchOBInteraction{get;set;}
    public Boolean bSwitchDisablePopOut{get;set;}
    public Boolean bSwitchHoldPopup{get;set;}
    public Boolean bSwitchPhonebookTabs{get;set;}
    public Boolean bSwitchSurveyTransfer{get;set;}
    public Boolean bSwitchPhoneBookRules{get;set;}
    public Integer HoldTimerPopup{get;set;}
    public String isManualTransfer{get;set;}
    public Boolean isRefreshedval{get;set;}
    public Boolean isTransferredCall{get;set;}
    public String interactionIdVal{get;set;}
    public String aboutIdVal{get;set;}
    public String PaymentTypeSelected{get;set;}
    public String secureFlowId{get;set;}
    public String SurveyTransferNo{get;set;}
    public String autoInteractionIDvalue{get;set;}
    public List<SelectOption> phoneBookNames {get;set;}
    public List<SelectOption> phoneBookNamesList {get;set;}   
    public List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> phBookTransferList{get;set;}
    public List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> defaultTransferList {get;set;}
    public List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> defaultTransferListAll {get;set;}    
    public List<SelectOption> CallerTypes{get;set;}        
    public Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>> phBookRulesMap {get;set;}    
    public GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList phBookTransferListSelected{get;set;}
    public RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList defaultPhBookTransferListSelected{get;set;}
    //US#2191493 - SFDC_Caller Type population
    public String interactingWithVal {get;set;}
    private static set<string> setMemberIDTypes = new set<string>{PhoneBookConstants_H_HUM.MEDICAREID_HUM,PhoneBookConstants_H_HUM.SSN_HUM,PhoneBookConstants_H_HUM.MEMBERIDBASE_HUM,PhoneBookConstants_H_HUM.MEDICADEID_HUM,PhoneBookConstants_H_HUM.UNKNOWN_MEMBER_MEDICAIDID, PhoneBookConstants_H_HUM.CBIS_ALT_ID};
    public Boolean isCRMSPCCPermissionSetUser {get;set;}
    public String calllogattributes {get; set; } 
    public Boolean updateCampaignNameAndKeycodeSwitch{get;set;}
    public Boolean updateInteractionAttributeSwitch{get;set;}
    public Boolean callerTypePopulationSwitch{get;set;} //US#4040542-Added for Caller Type Population with Phonebook.
    public Boolean callTransferSwitch{get;set;}

    public PhoneBook_C_HUM()
    {
       bSwitchSecurePay = PhoneBook_H_HUM.isCRMFunctionalityON('2484363');
       bSwitchOBInteraction = PhoneBook_H_HUM.isCRMFunctionalityON('2230077');
       bSwitchDisablePopOut = PhoneBook_H_HUM.isCRMFunctionalityON('2546987');
       bSwitchHoldPopup = PhoneBook_H_HUM.isCRMFunctionalityON('2760576'); 
       HoldTimerPopup = PhoneBook_H_HUM.getHoldTime('HoldTime');
       bSwitchPhonebookTabs = PhoneBook_H_HUM.isCRMFunctionalityON('2649919');

        bSwitchSurveyTransfer = PhoneBook_H_HUM.isCRMFunctionalityON('3653389');
        SurveyTransferNo = PhoneBook_LC_HUM.getSurveyTransferNumber();
        isSurveyButtonVisible =false;
        isCRMSPCCPermissionSetUser = PhoneBook_LC_HUM.fetchPermissionSetAccess(PhoneBookConstants_H_HUM.CRMS_685_PCC_Access_HUM); 
        bSwitchPhoneBookRules = PhoneBook_H_HUM.isCRMFunctionalityON('3791679');
        updateCampaignNameAndKeycodeSwitch = PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitch);
        updateInteractionAttributeSwitch = PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.updateInteractionAttributeSwitch);
	    callerTypePopulationSwitch = PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.callerTypePopulationSwitch);//US#4040542-Added for Caller Type Population with Phonebook.
        callTransferSwitch = PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic);
    }
    
    /**
* <p>
* Method to get the Caller Types picklist values
*
* @return List<SelectOption>
*/
    public List<SelectOption> getCallerTypes() 
    {
        List<SelectOption> options = new List<SelectOption>();
        options.add(new SelectOption(PhoneBookConstants_H_HUM.SELECT_VAL,PhoneBookConstants_H_HUM.SELECT_VAL));
        options.add(new SelectOption(PhoneBookConstants_H_HUM.CALLER_MEMBER_VALUE,PhoneBookConstants_H_HUM.CALLER_MEMBER_VALUE));
        options.add(new SelectOption(PhoneBookConstants_H_HUM.CALLER_PROVIDER_VALUE,PhoneBookConstants_H_HUM.CALLER_PROVIDER_VALUE));
        options.add(new SelectOption(PhoneBookConstants_H_HUM.CALLER_GROUP_VALUE,PhoneBookConstants_H_HUM.CALLER_GROUP_VALUE));
        options.add(new SelectOption(PhoneBookConstants_H_HUM.CALLER_AGENT_VALUE,PhoneBookConstants_H_HUM.CALLER_AGENT_VALUE));
        //US#2191493 - SFDC_Caller Type population
        if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
            options.add(new SelectOption(PhoneBookConstants_H_HUM.CALLER_UNKNOWN,PhoneBookConstants_H_HUM.CALLER_UNKNOWN));
        }
        return options;
    }

    //US#2191493 - SFDC_Caller Type population
    public void updateCallerTypeVal()
    {
        CallerType =  interactingWithVal;
        displayFilteredDefaultQueues();
    }
    
    /**
* <p>
* Method to create soft Phone Logs for events
*
* @return Void
*/
    public void createRecord()
    {
        try
        {    
            CRMFunctionality_ONOFF_Switch__c level1 = CRMFunctionality_ONOFF_Switch__c.getValues(PhoneBookConstants_H_HUM.CS_1799704_GENESYS_LEVEL1);
            CRMFunctionality_ONOFF_Switch__c level2 = CRMFunctionality_ONOFF_Switch__c.getValues(PhoneBookConstants_H_HUM.CS_1799704_GENESYS_LEVEL2);
            CRMFunctionality_ONOFF_Switch__c level3 = CRMFunctionality_ONOFF_Switch__c.getValues(PhoneBookConstants_H_HUM.CS_1799704_GENESYS_LEVEL3);
            if(level3.IsON__c && level3.IsON__c != null)
            {
                if( reason == PhoneBookConstants_H_HUM.STATUS_UPDATED || reason == PhoneBookConstants_H_HUM.LOGGED_OUT || reason == PhoneBookConstants_H_HUM.INTERACTION|| reason == PhoneBookConstants_H_HUM.CONNECTED || category == PhoneBookConstants_H_HUM.CAT_ROUTING_STATUS || category == PhoneBookConstants_H_HUM.CAT_VIEW|| reason == PhoneBookConstants_H_HUM.ACW_COMPLETED || reason == PhoneBookConstants_H_HUM.ACW_REQUIRED || reason == PhoneBookConstants_H_HUM.DISCONNECTED|| category == PhoneBookConstants_H_HUM.CAT_CHANGE || category == PhoneBookConstants_H_HUM.CAT_CONNECT)
                {
                    PhoneBook_D_HUM.createSoftPhoneLog(callAction,callInfoLog,callLogType,SPExtrnlId,'',uuidataValue);
                }
            }
            else if(level2.IsON__c && level2.IsON__c != null)
            {
                if(reason == PhoneBookConstants_H_HUM.INTERACTION|| reason == PhoneBookConstants_H_HUM.STATUS_UPDATED || reason == PhoneBookConstants_H_HUM.LOGGED_OUT || reason == PhoneBookConstants_H_HUM.CONNECTED || category == PhoneBookConstants_H_HUM.CAT_CHANGE || category == PhoneBookConstants_H_HUM.CAT_CONNECT)
                {
                    PhoneBook_D_HUM.createSoftPhoneLog(callAction,callInfoLog,callLogType,SPExtrnlId,'',uuidataValue);
                    
                }
            }
            else if(level1.IsON__c && level1.IsON__c != null)
            {
                if((reason == PhoneBookConstants_H_HUM.STATUS_UPDATED || reason == PhoneBookConstants_H_HUM.LOGGED_OUT) && category == '')
                {
                    PhoneBook_D_HUM.createSoftPhoneLog(callAction,callInfoLog,callLogType,SPExtrnlId,'','');
                }
            }
            if(reason == PhoneBookConstants_H_HUM.Secure_Transfer && category == '')
            {
                PhoneBook_D_HUM.createSoftPhoneLog(callAction,callInfoLog,'','','',uuidataValue);
            }
        } 
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createRecord');
        }       
    }
    
    /**
* Returns request object of continuation object
* <p>
* Method to invoke the getCalldata continuation requests to get the caller information from IVR
*
* @return continuation object
*/
    public  Continuation  getDataFromWebService()
    {
        defaultTransferList = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        defaultTransferListAll =new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        Continuation ret = null;
        DataModal.put('uuidataValue', uuidataValue);
        try
        {
            if(String.isBlank(uuidataValue)||uuidataValue==null)
            {
                CalloutException e = new CalloutException();
                e.setMessage('UUIData not available!!');    
                throw e;
            }
            else
            { 
                ret = phshum.getDataFromWebService(uuidataValue,this);
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'getDataFromWebService');
        }
        return ret;  
    } 
    
    /**
* Returns responses of continuation object
* <p>
* Callback Method to parse the getCallData service response
*
* @return Object
*/
    public  Object processResponse() 
    {
        autoInteractionIDvalue='';
        DataModal = new Map<Object,Object>();
        mflag=false;aflag=false;pflag=false;gflag=false;
        searchPageURl = '/apex/HUMSoftPhoneIntermediatePage?';
        object ret = null;
        isTransferredCall = false;
        aboutIdVal='';
        interactionIdVal='';
        String responseRet;
        Boolean bSwitchSaveCallData_2230090 = PhoneBook_H_HUM.isCRMFunctionalityON('2230090');
        Map<String,SOFTPHONE_URL_PARAM__c> supMap = new Map<String,SOFTPHONE_URL_PARAM__c>();
        try
        {
            responseRet = (String) phshum.processResponse(sconLabel) ;
            PhoneBook_DTO_HUM pbWebserviceRes = (PhoneBook_DTO_HUM) System.JSON.deserialize(responseRet, PhoneBook_DTO_HUM.class);
            List<PhoneBook_DTO_HUM.CallDataValuesList> arrayData = pbWebserviceRes.GetCallDataResponse.GetCallDataResult.CallDataValues.CallDataValuesList;
            setCallerType(arrayData);
            if (bSwitchSurveyTransfer !=null && bSwitchSurveyTransfer == true)
            {
                    isSurveyButtonVisible =true;
            }
            List<SOFTPHONE_URL_PARAM__c> sup = SOFTPHONE_URL_PARAM__c.getall().values();
            for(SOFTPHONE_URL_PARAM__c supEach: sup){
                supMap.put((supEach.Name__c+supEach.Param_for__c).ToUpperCase(),supEach);
            }
            if(!PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.transferCallSwitchClassic))
            {
                if(!String.isBlank(responseRet) && (responseRet.contains(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER) || (responseRet.contains(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS) && (DataModal.get(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS)!= ''))) )
                {
                    isTransferredCall = true; 
                    if(DataModal.get('uuidataValue')!='' && bSwitchSaveCallData_2230090){
                        getAboutIdAndInteractionId();
                    }
                }
                else
                {
                    if(PhoneBook_H_HUM.isCRMFunctionalityON('2230000'))
                    {
                        autoInteractionIDvalue = createAutoInteraction(DataModal,uuidataValue,isRefreshedval,idval,calllogattributes);
                    }
                }
            }
            else
            {
                if(PhoneBook_H_HUM.isCRMFunctionalityON('2230000'))
                {
                    autoInteractionIDvalue = createAutoInteraction(DataModal,uuidataValue,isRefreshedval,idval,calllogattributes);
                }
            }
            if(isRefreshedval){
                List<Interaction__c> lstInter =  [SELECT id FROM Interaction__c WHERE UUID__c =:uuidataValue Order By Name desc LIMIT 1];
                if(lstInter.size()>0)
                {
                    autoInteractionIDvalue = lstInter[0].id;
                }
            }
            if( arrayData.size() >0)
            {
                if(mflag == true)
                {
                    UpdateSearchPageURLForCallerTypeMember(supMap);
                }
                else if(aflag == true)
                {
                    UpdateSearchPageURLForCallerTypeAgent(supMap);
                }
                else if(pflag == true)
                {
                    UpdateSearchPageURLForCallerTypeProvider(supMap);
                }
                else if(gflag == true)
                {
                    UpdateSearchPageURLForCallerTypeGroup(supMap);
                }
                else {
                    searchPageURl='/apex/HUMSoftPhoneIntermediatePage?00NF000000CBqWV=&FirstName_SOFTPHONE=&Member_Id1_SOFTPHONE=&LastName_SOFTPHONE=&iAboutType=M&'+'autoInteractionIDvalue='+autoInteractionIDvalue;
                }
                
                searchPageURl = searchPageURl.removeEnd('&');
                if(!PhoneBook_H_HUM.isCRMFunctionalityON('1728596_IVRDataCapture'))
                {
                    searchPageURl = searchPageURl + '&callerType=Inbound%2BCall&uuID='+uuidataValue+'&authIndicator='+DataModal.get(PhoneBookConstants_H_HUM.Authentication_IND)+'&autoInteractionIDvalue='+autoInteractionIDvalue;
                }
                else
                {
                    searchPageURl = searchPageURl + '&callerType=Inbound%2BCall&uuID='+uuidataValue+'&interactionId='+idval+'&surveyId='+DataModal.get(PhoneBookConstants_H_HUM.SURVEY_ID)+'&callCategoryDesc='+DataModal.get(PhoneBookConstants_H_HUM.Call_Catg_Desc)+'&authIndicator='+DataModal.get(PhoneBookConstants_H_HUM.Authentication_IND)+'&autoInteractionIDvalue='+autoInteractionIDvalue;
                }
                searchPageURl= EncodingUtil.urlEncode(searchPageURl, 'UTF-8');
                searchPageURl = searchPageURl.replaceAll('\\+','%20');
                searchPageURl = searchPageURl.replaceAll('\\-','%2D');
            }
            else
            {
                if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                    callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_UNKNOWN;
                }
                else{
                    callerVal = CallerType = PhoneBookConstants_H_HUM.SELECT_VAL;
                }
                searchPageURl='/apex/HUMSoftPhoneIntermediatePage?00NF000000CBqWV=&FirstName_SOFTPHONE=&Member_Id1_SOFTPHONE=&LastName_SOFTPHONE=&iAboutType=M&uuID='+uuidataValue+'&interactionId='+idval+'&surveyId=&callCategoryDesc='+'&autoInteractionIDvalue='+autoInteractionIDvalue;
            }
            createInboundCallLog(DataModal);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'processResponse');
        }
        return null;
    }
    
    /**
* <p>
* Method to create the inbound call log  
*
* @param Map<Object,Object> DataModal
* @return Void
*/
    public void createInboundCallLog(Map<Object,Object> DataModal)
    {
        String callerId = '';
        String inboundCall = PhoneBookConstants_H_HUM.INBOUND;
        try
        {
            String uuidata = (String)DataModal.get('uuidataValue');
            PhoneBook_D_HUM.createSoftPhoneLog(callActionInt, callLogInfoInt, inboundCall, idval, callerId,uuidata);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createInboundCallLog');
        }
    }
    
    /**
* <p>
* Method to close the info popup box  
*
* @return Void
*/
    public void closePopup() 
    {        
        displayPopup = false;    
    } 
    
    /**
* <p>
* Method to show the info popup box  
*
* @return Void
*/
    public void showPopup() 
    {
        try
        {        
            displayPopup = true;
            if(uniqueTransferNumber != null)
            { 
                for(GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList phBookSelected : phBookTransferList)
                {
                    if(phBookSelected.TransferNumber == uniqueTransferNumber)
                    {
                        phBookTransferListSelected = phBookSelected;
                        uniqueTransferNumber = null;
                        break;
                    }
                }   
            }
            else if(deafultUniqueTransferNumber !=null)
            {
                for(RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList phBookSelected : defaultTransferList)
                {
                    if(phBookSelected.TransferNumber == deafultUniqueTransferNumber)
                    {
                        defaultPhBookTransferListSelected = phBookSelected;
                        deafultUniqueTransferNumber = null;
                        break;
                    }
                }
                phBookTransferListSelected = null;      
            }
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'showPopup','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }    
    
    /**
* <p>
* Method to display the different queue list based on the phonebook selected
*
* @return Void
*/    
    public void displayphBookRules()
    {
        try
        {
            searchParam='';
            enableConsultTransfer=true;
            enableBlindTransfer=true;
            if(phBookRulesMap !=null && phBookRulesMap.containsKey(phoneBookSelected))
            {
                placeholderVal = PhoneBookConstants_H_HUM.QueueName;
                isnotDefaultPhoneBook= true;
                isDefaultPhoneBook =false;
                phBookTransferList = removeDupPhBookList(phoneBookSelected);    
                if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.alphabetizeCallReasonsSwitch)){
                    if(phBookTransferList!=null && !phBookTransferList.isEmpty()){
                        phBookTransferList=sortTransferList(phBookTransferList);
                  }  
                } 
            }
            else if(phoneBookSelected == PhoneBookConstants_H_HUM.DEFAULT_VAL && isCallConnected ==true)
            { 
                placeholderVal = PhoneBookConstants_H_HUM.CallReason;
                isnotDefaultPhoneBook= false;
                isDefaultPhoneBook =true;
                CreateDefaultTransferListByCallerType();
            }
            else if(isCallConnected ==true && phoneBookSelected == PhoneBookConstants_H_HUM.SELECT_VAL)
            { 
                isnotDefaultPhoneBook= true;
                isDefaultPhoneBook =false;
                placeholderVal = PhoneBookConstants_H_HUM.QueueName; 
                phBookTransferList=null;     
            }
            else
            {
                placeholderVal = PhoneBookConstants_H_HUM.QueueName;
                phBookTransferList=null;
            }
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'displayphBookRules','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }
    
    /**
* Returns request object of continuation object
* <p>
* Method to invoke the getAllPhoneBookRules continuation requests to get list of enterprise PhoneBooks
*
* @return continuation object
*/
    public Continuation AllPhoneBookRules()
    {
        bSwitchCallReasonCode = PhoneBook_H_HUM.isCRMFunctionalityON('2275140');
        callerTypeDisable = true; 
        enableConsultTransfer=true; 
        enableBlindTransfer=true;
        isCallConnected=false;
        isCallNotConnected=true;
        isShowBox = false;
        isShowClickToDial=true;
        phBookRulesMap = new Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>>(); 
        phoneBookNames = new List<SelectOption>();
        phoneBookSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
        isnotDefaultPhoneBook = true;
        phoneBookNames.add(new SelectOption(PhoneBookConstants_H_HUM.SELECT_VAL,PhoneBookConstants_H_HUM.SELECT_VAL));
        CallerTypes = new List<SelectOption>();
        CallerTypes.add(new SelectOption(PhoneBookConstants_H_HUM.SELECT_VAL,PhoneBookConstants_H_HUM.SELECT_VAL)); 
        Continuation ret = null;
        placeholderVal = PhoneBookConstants_H_HUM.QueueName;
        PaymentTypeSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
        
        try
        {
            ret = phshum.AllPhoneBookRules(this);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'AllPhoneBookRules');
        }
        return ret;
    }
    
    /**
* Returns responses of continuation object
* <p>
* Callback Method to parse the getAllPhoneBookRules service response
*
* @return Object
*/
    public Object processResponsegetAllPhoneBookRules() 
    {
        try
        {
            phBookRulesMap = new Map<String,List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>>();
            String allPhBookResp = (String) phshum.processResponsegetAllPhoneBookRules(this.AllPhBookRulesRequest);
            if(!allPhBookResp.containsIgnoreCase('faultstring'))
            {
            	fetchAllPhoneBookNamesRules(allPhBookResp);
            }
            else
            {
                throw new CalloutException();
            }
        }
        catch(Exception e)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(e), 'PhoneBook_C_HUM', 'processResponsegetAllPhoneBookRules','System.CalloutException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }       
        return null;
    }
    
    /**
* <p>
* Method to parse the getAllPhoneBookRules response and create the enterprise phonebook List
*
* @Param String sAllPhBookResponse
* @return Void
*/
    public Void fetchAllPhoneBookNamesRules(String sAllPhBookResponse)
    {
        try
        {
            GetAllPhoneBookRulesResponse_DTO_HUM phBookRulesresponse = (GetAllPhoneBookRulesResponse_DTO_HUM)System.JSON.deserialize(sAllPhBookResponse, GetAllPhoneBookRulesResponse_DTO_HUM.class);
            List<GetAllPhoneBookRulesResponse_DTO_HUM.AllPhoneBookRulesResultList> phBookRulesResList = phBookRulesresponse.GetAllPhoneBookRulesResponse.AllPhoneBookRulesResult.AllPhoneBookRulesResultList;
            for( GetAllPhoneBookRulesResponse_DTO_HUM.AllPhoneBookRulesResultList eachPhBookRuleSet: phBookRulesResList)
            {
                phBookRulesMap.put(eachPhBookRuleSet.Key,eachPhBookRuleSet.TransferNumber.TransferNumberList);
                phoneBookNames.add(new SelectOption(eachPhBookRuleSet.Key, eachPhBookRuleSet.Key));
            }
            if(!isCallConnected)
            {
                phoneBookNamesList = new List<SelectOption>();
                phoneBookNamesList.addAll(phoneBookNames);
            }
        }
        catch(Exception e)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(e), 'PhoneBook_C_HUM', 'fetchAllPhoneBookNamesRules','System.CalloutException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }
    
    /**
* Returns request object of continuation object
* <p>
* Method to invoke the RouteSoftphoneXml continuation requests to get the Transfer PhoneBook queues based on the Policy
*
* @return continuation object
*/
    public Continuation fetchDefaultPhoneBookRules()
    {
        isDefaultPhoneBook= true;
        isnotDefaultPhoneBook = false;
        String ASOIndicator = (String)((DataModal.get(PhoneBookConstants_H_HUM.ASO_IND_VAL) != null)? DataModal.get(PhoneBookConstants_H_HUM.ASO_IND_VAL) : '');
        String BusinessSegmentIndicator= (String) ((DataModal.get(PhoneBookConstants_H_HUM.BUS_SEGMENT_IND_VAL) != null)? DataModal.get(PhoneBookConstants_H_HUM.BUS_SEGMENT_IND_VAL) : '');
        String HdhpIndicator= (String)((DataModal.get(PhoneBookConstants_H_HUM.HDHP_IND_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.HDHP_IND_VAL) : '');
        String SubscriberDOB=(String)(( DataModal.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL) : '');
        String GroupID= (String)((DataModal.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL) : '');
        String PlatFormCD= (String)((DataModal.get(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.INQA_PLTFRM_CD_VAL) : '');
        String MajorLOB=(String)(( DataModal.get(PhoneBookConstants_H_HUM.MAJOR_LOB_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.MAJOR_LOB_VAL) : '');
        String SubscriberID= (String)((DataModal.get(PhoneBookConstants_H_HUM.MEMBER_ID_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.MEMBER_ID_VAL) : '');
        String MtvBusinessLevel5= (String)((DataModal.get(PhoneBookConstants_H_HUM.MTV_BL5_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.MTV_BL5_VAL) : '');
        String MtvBusinessLevel7= (String)((DataModal.get(PhoneBookConstants_H_HUM.MTV_BL7_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.MTV_BL7_VAL) : '');
        String ProductTypeCode= (String)((DataModal.get(PhoneBookConstants_H_HUM.PROD_TYPE_CODE_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.PROD_TYPE_CODE_VAL) : '');
        String SellingLedger= (String)((DataModal.get(PhoneBookConstants_H_HUM.SELLING_LEDGER_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.SELLING_LEDGER_VAL) : '');
        String StateOfIssue= (String)((DataModal.get(PhoneBookConstants_H_HUM.STATE_OF_ISSUE_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.STATE_OF_ISSUE_VAL) : '');
        String AsOfDate= (String)((DataModal.get(PhoneBookConstants_H_HUM.VENDOR_ASOFDATE_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.VENDOR_ASOFDATE_VAL) : '');
        String SubGroupID= (String)((DataModal.get(PhoneBookConstants_H_HUM.VENDOR_SUB_GROUPID_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.VENDOR_SUB_GROUPID_VAL) : '');
        String SellingLedgerForMktName= (String)((DataModal.get(PhoneBookConstants_H_HUM.SELLING_LEDGER_VAL)!= null)? DataModal.get(PhoneBookConstants_H_HUM.SELLING_LEDGER_VAL) : '');
        String AkaName= '';
        String ActParent= '';
        String CimId= '';
        String IsSoftPhoneIndicator= PhoneBookConstants_H_HUM.IsSoftPhoneIndicator;
        String ICMSiteCode= '';
        String NGroupID= '';
        String ApplicationSourceRequest = PhoneBookConstants_H_HUM.SOFTPHONE_VAL;
        String reqBody = JSON.serialize(CreateRouteXMLRequest_DTO_HUM.requestStructureRouteXML(PlatFormCD, SubscriberID, SubscriberDOB, SellingLedger, GroupID, ASOIndicator, MajorLOB,  SellingLedgerForMktName, AkaName, ActParent, CimId, StateOfIssue, BusinessSegmentIndicator, HdhpIndicator, MtvBusinessLevel5, IsSoftPhoneIndicator, MtvBusinessLevel7, ProductTypeCode, ApplicationSourceRequest, NGroupID, SubGroupID ) );
        Continuation ret = null;
        try
        {
             ret = phshum.fetchDefaultPhoneBookRules(reqBody,this);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'fetchDefaultPhoneBookRules');
        }
        return ret;
    }
    
    /**
* Returns responses of continuation object
* <p>
* Callback Method to parse the RouteSoftphoneXml service response
*
* @return Object
*/
    public Object processResponsegetDefaultPhBookRules()
    {
        defaultTransferList = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        defaultTransferListAll =new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        String defaultphBookResponse; 
        try
        {
            if(bOnCallTransfer ==true)
            {
                defaultphBookResponse = (String) phshum.processResponsegetDefaultPhBookRules(this.defaultphBookRequest,this) ;    
                PhoneBook_D_HUM.insertIntegrationmapping(PolicyCaseDataMap); 
                bOnCallTransfer = false;
            }
            else
            {
                defaultphBookResponse = (String) phshum.processResponsegetDefaultPhBookRules(this.defaultphBookRequest) ;    
            }
            if(!defaultphBookResponse.containsIgnoreCase('faultstring'))
            {
                RouteSoftPhoneXMLResponse_DTO_HUM routeXMLresponse = (RouteSoftPhoneXMLResponse_DTO_HUM)System.JSON.deserialize(defaultphBookResponse, RouteSoftPhoneXMLResponse_DTO_HUM.class);
                if(routeXMLresponse !=null)
                {
                    Map<String,RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> uniqueQsDefaultTransferListMap = new Map<String,RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>(); 
                    for(RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList eachTransferQ:routeXMLresponse.RouteSoftphoneXmlResponse.CallTransferRec.CallTransferRecList)
                    {
                        if (bSwitchPhoneBookRules!= null && bSwitchPhoneBookRules == true)
                            uniqueQsDefaultTransferListMap.put(eachTransferQ.CatCd+eachTransferQ.TransferNumber+eachTransferQ.CallerType,eachTransferQ);
                        else 
                            uniqueQsDefaultTransferListMap.put(eachTransferQ.CatCd+eachTransferQ.TransferNumber,eachTransferQ);
                    }
                    defaultTransferListAll = uniqueQsDefaultTransferListMap.values();
                    CreateDefaultTransferListByCallerType(); 
                }
            }
            else
            {
                throw new CalloutException('Error occured: For Genesys Interaction Id: '+idval+', RoutingXML web service returned fault response: '+defaultphBookResponse);
            }
        }
        catch(Exception e)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(e), 'PhoneBook_C_HUM', 'processResponsegetDefaultPhBookRules','System.CalloutException','Integration Error');
            String errMsg = 'There was an issue retrieving the call data and transfer phonebook. Continue using other phonebooks or please refresh your browser and try again. If this issue persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.WARNING,errMsg));
        }
        return null;
    }
    
    /**
* <p>
* Method to handle the search box
*
* @return Void
*/
    public void searchRules()
    {
        try
        {
            enableConsultTransfer=true;
            enableBlindTransfer=true;
            if(searchParam  == '' )
            {
                if(phoneBookSelected == PhoneBookConstants_H_HUM.DEFAULT_VAL)
                {
                    isDefaultPhoneBook= true;
                    isnotDefaultPhoneBook = false;
                    CreateDefaultTransferListByCallerType();  
                }
                else if(phoneBookSelected == PhoneBookConstants_H_HUM.SELECT_VAL)
                {
                    phBookTransferList = null;
                }
                else
                {
                    phBookTransferList = removeDupPhBookList(phoneBookSelected);
                }
            }
            else
            {
                phBookTransferList =  new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
                if(phoneBookSelected != PhoneBookConstants_H_HUM.DEFAULT_VAL)
                {
                    isnotDefaultPhoneBook = true;
                    isDefaultPhoneBook = false;
                    for(GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList tl :  removeDupPhBookList(phoneBookSelected))
                        {
                            String que = tl.QueueName;
                            if(que.containsIgnoreCase(searchParam) && phBookTransferList.size()< PhoneBookConstants_H_HUM.Constant_100_HUM)
                            {
                                phBookTransferList.add(tl);
                            }
                        }
                }
                else if(defaultTransferList !=null &&  isCallConnected == true && phoneBookSelected == PhoneBookConstants_H_HUM.DEFAULT_VAL)
                {
                    isnotDefaultPhoneBook = false;
                    isDefaultPhoneBook = true;
                    CreateDefaultTransferListByCallerType();
                    List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> defaultTransferListTemp = defaultTransferList;
                    defaultTransferList =  new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
                    for(RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList dl: defaultTransferListTemp)
                    {
                        String defCallCd = dl.CategoryCd;
                        if(defCallCd.containsIgnoreCase(searchParam) && phBookTransferList.size()< PhoneBookConstants_H_HUM.Constant_100_HUM)
                        {
                            defaultTransferList.add(dl);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'searchRules','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }  
    }
    
    public void callDisplay()
    {
        if(isCallConnected == true)
        {
            enableConsultTransfer=false;
            enableBlindTransfer=false;
            isManualTransfer = 'N';
            if (bSwitchSurveyTransfer !=null && bSwitchSurveyTransfer == true)
            {
                    isSurveyButtonVisible =true;
            }
        }
    }
    
    public void callDisplayDef()
    {
        if(isCallConnected == true)
        {
            enableConsultTransfer=false;
            enableBlindTransfer=false;
            isManualTransfer = 'N';
            if (bSwitchSurveyTransfer !=null && bSwitchSurveyTransfer == true)
            {
                    isSurveyButtonVisible =true;
            }
        }
    }
    
    /**
* <p>
* Method to handle the enabling and disabling of buttons on PhoneBook
*
* @return Void
*/
    public void refreshbuttons()
    {
        try
        {
            if(numberVal != '' && numberVal.isNumeric() && (numberVal.length()==7 || numberVal.length()==10))
            {
                if(isCallConnected == true)
                {
                    enableConsultTransfer=false;
                    enableBlindTransfer=false;
                    isManualTransfer = 'Y';
                }
            }
            else if(numberVal != '' && numberVal.isNumeric() && (numberVal.length()!=7 || numberVal.length()!=10 ))
            {
                enableConsultTransfer=true;
                enableBlindTransfer=true;
            }
            else if(numberVal != '' && !numberVal.isNumeric() && (numberVal.length()!=7 || numberVal.length()!=10 ))
            {
                enableConsultTransfer=true;
                enableBlindTransfer=true;
            }
            else if(numberVal == '') 
            {
                enableConsultTransfer=true;
                enableBlindTransfer=true;
            }
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'refreshbuttons','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }   
    
    /**
* <p>
* Method to handle the connected inBound call
*
* @return Void
*/
    public  void onCallConnected()
    {
        try
        {
            closePopup();
            errorMessage='Please enter 7 DD or 10 Digit phone number ';
            callerTypeDisable = false;
            searchParam='';
            enableConsultTransfer=true;
            enableBlindTransfer=true;
            isCallConnected=true;
            isCallNotConnected=false;
            isShowBox=true;
            isShowClickToDial=false;
            isDefaultPhoneBook = true;
            isnotDefaultPhoneBook = false;
            SelectOption defaultVal = new SelectOption(PhoneBookConstants_H_HUM.DEFAULT_VAL,PhoneBookConstants_H_HUM.DEFAULT_VAL);
            if(!phoneBookNamesList.contains(defaultVal))
            {
                phoneBookNamesList.add(defaultVal);
            }
            phoneBookSelected = PhoneBookConstants_H_HUM.DEFAULT_VAL;
            placeholderVal = PhoneBookConstants_H_HUM.CallReason;
            CallerTypes = getCallerTypes(); 
            PaymentTypeSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
            if (bSwitchSurveyTransfer !=null && bSwitchSurveyTransfer == true)
            {
                    isSurveyButtonVisible =true;
            }
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'onCallConnected','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }
    
    /**
* <p>
* Method to handle the Disconnected Call
*
* @return Void
*/
    public  void onCallDisconnected()
    {
        try
        {
            autoInteractionIDvalue='';
            closePopup();
            placeholderVal = PhoneBookConstants_H_HUM.QueueName;
            errorMessage='';
            ApexPages.getMessages().clear();
            callerTypeDisable = true;
            enableConsultTransfer=true;
            enableBlindTransfer=true;
            isSurveyButtonVisible =false;
            isCallConnected=false;
            isCallNotConnected=true;
            isShowBox=false;
            isshowClickToDial=true;
            phoneBookSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
            isnotDefaultPhoneBook = true;
            isDefaultPhoneBook = false;
            phBookTransferList=null;
            searchParam='';
            phoneBookNamesList = new List<SelectOption>();
            phoneBookNamesList.addAll(phoneBookNames);
            CallerTypes = new List<SelectOption>();
            CallerTypes.add(new SelectOption(PhoneBookConstants_H_HUM.SELECT_VAL,PhoneBookConstants_H_HUM.SELECT_VAL)); 
            CallerType =PhoneBookConstants_H_HUM.SELECT_VAL;
            PaymentTypeSelected = PhoneBookConstants_H_HUM.SELECT_VAL; 
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'onCallDisconnected','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }
    
    /**
* Returns request object of continuation object
* <p>
* Method to invoke the RouteSoftphoneXml continuation requests to get the Transfer PhoneBook queues based on the Policy
* upon click of Call transfer button on Policy or Case
*
* @return continuation object
*/
    public Continuation OnCallTransfer()
    {
        closePopup();
        SelectOption defaultVal = new SelectOption(PhoneBookConstants_H_HUM.DEFAULT_VAL,PhoneBookConstants_H_HUM.DEFAULT_VAL);
        Continuation ret = null;
        if( isCallConnected == true && isCallNotConnected == false)
        {
            placeholderVal = PhoneBookConstants_H_HUM.CallReason;
            searchParam='';
            isnotDefaultPhoneBook = false;
            isDefaultPhoneBook = true;
            if(!phoneBookNamesList.contains(defaultVal))
            {
                phoneBookNamesList.add(defaultVal);
            }
            phoneBookSelected = PhoneBookConstants_H_HUM.DEFAULT_VAL;
            defaultTransferList = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
            defaultTransferListAll =new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>(); 
            ApexPages.getMessages().clear();
            CallerType = callerVal;
            bOnCallTransfer = true;
            PaymentTypeSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
            if(ParamsValueOnCallTransfer.contains('{'))
            {
                PolicyCaseDataMap = (Map<String,String>) Json.deserialize(ParamsValueOnCallTransfer, Map<String,String>.class);
            }
            else
            {
                getParamsMapforCase(ParamsValueOnCallTransfer);  
            }
            try
            {
                if(PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjPolicyMember) != '')
                {
                    HUMSoftPhoneCallTransferHelper oSFResponce = HUMSoftPhoneCallTransferService.createResponse(PolicyCaseDataMap); 
                    //US 3994079 : PCC Call Transfer Phonebook Transfers- Added IsSoftPhoneIndicator in input request to filter softphone routing rules
                    String reqBodyForRoutingXML = JSON.serialize(CreateRouteXMLRequest_DTO_HUM.requestStructureRouteXML(oSFResponce.InquiredAboutPlatformCode, oSFResponce.MemberId, oSFResponce.InquiredAboutDOB, oSFResponce.SellingLedger, oSFResponce.InquiredAboutGroupId, oSFResponce.ASOIndicator, oSFResponce.MajorLineOfBusiness,  oSFResponce.SellingLedger, '', '', '',oSFResponce.StateOfIssue, oSFResponce.BusinessSegmentIndicator, oSFResponce.HdphIndicator,oSFResponce.MtvBusinessLevel5, PhoneBookConstants_H_HUM.IsSoftPhoneIndicator,oSFResponce.MtvBusinessLevel7,oSFResponce.ProductTypeCode, 'Softphone', '',oSFResponce.VENDOR_SUB_GROUPID));
                    ret = phshum.OnCallTransfer(reqBodyForRoutingXML,this);
                }
                else
                {
                    throw new CalloutException('Error occured: Policy not found in the case Id- '+PolicyCaseDataMap.get(Label.HUMSPhoneMappingObjCase));
                }
            }
            catch(exception e)
            {
                isnotDefaultPhoneBook = true;
                isDefaultPhoneBook = false;
                phoneBookNamesList = new List<SelectOption>();
                phoneBookNamesList.addAll(phoneBookNames);
                placeholderVal = PhoneBookConstants_H_HUM.QueueName;
                PaymentTypeSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
                phBookTransferList =  new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
                defaultTransferList = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
                String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(e), 'PhoneBook_C_HUM', 'OnCallTransfer','System.InvalidParameterValueException','Integration Error');
                String errMsg = 'Transfer phonebook not available for this transfer, please use alternate phonebook.';
                ApexPages.addMessage(new ApexPages.message(ApexPages.severity.INFO,errMsg));
            }
        }
        return ret;
    }    
    
    /**
* <p>
* Method to get the map of params from Case when call Transfer triggered from Case detail Page
*
* @Param Id CaseId
* @return Void
*/
    public void getParamsMapforCase(Id CaseId)
    {
        PolicyCaseDataMap = new Map<String,String>();
        String InteractionId= '';
        String InteractingFor = '';
        String InteractingAbt ='';
        String PolicyMemberId='';
        try
        {
            String sCaseQry ='Select Policy_Member__r.id,Interacting_With__r.id,Account.id,Interaction_ID__c from Case where id = :CaseId';
            Case caseObj = Database.query(sCaseQry);
            if(String.isNotBlank(caseObj.Interaction_ID__c))
                InteractionId= (String)caseObj.Interaction_ID__c;
            if(String.isNotBlank(caseObj.Interacting_With__r.id))
                InteractingFor = (String)caseObj.Interacting_With__r.id;
            if(String.isNotBlank(caseObj.Interacting_With__r.id))
                InteractingAbt =(String)caseObj.Account.id;
            if(String.isNotBlank(caseObj.Policy_Member__r.id))
                PolicyMemberId=(String)caseObj.Policy_Member__r.id;
            PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjInteraction, InteractionId);
            PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjInqFor,InteractingFor);
            PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjInqAb,InteractingAbt);
            PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjPolicyMember,PolicyMemberId);
            PolicyCaseDataMap.put(Label.HUMSPhoneMappingObjCase, CaseId);
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'getParamsMapforCase','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));            
        }
    }
    
    /**
* <p>
* Method to reset the variables on a call disconnect
*
* @return Void
*/
    public void reAssignVar()
    {
        try
        {
            isCallConnected=false;
            isCallNotConnected=true;
            isShowBox=false;
            isshowClickToDial=true;
            phoneBookNamesList = new List<SelectOption>(); 
            phoneBookNamesList.addAll(phoneBookNames);
            CallerTypes = new List<SelectOption>();
            CallerTypes.add(new SelectOption(PhoneBookConstants_H_HUM.SELECT_VAL,PhoneBookConstants_H_HUM.SELECT_VAL)); 
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'reAssignVar','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }
    
    /**
* <p>
* Method to handle the display of transfer PhoneBook Queues
*
* @return Void
*/
    public void displayFilteredDefaultQueues()
    {
        callerVal = CallerType;
        searchParam='';
        try
        {
            if(defaultTransferListAll!=null  && phoneBookSelected == PhoneBookConstants_H_HUM.DEFAULT_VAL )
            {
                CreateDefaultTransferListByCallerType();
                isDefaultPhoneBook = true;
                isnotDefaultPhoneBook = false;
            }
            else
            {
                isDefaultPhoneBook = false;
                isnotDefaultPhoneBook = true;
                phBookTransferList = phBookRulesMap.get(phoneBookSelected);
            }
        }
        catch (Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'displayFilteredDefaultQueues','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));
        }
    }
    
    /**
* <p>
* Method to create the Transfer phoneBook list filtered by the Caller Types
*
* @return Void
*/
    public void CreateDefaultTransferListByCallerType()
    {
        try
        {
            defaultTransferList = new  List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
            for(RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList eachTransferRes:defaultTransferListAll)
            {
                if (CallerType == PhoneBookConstants_H_HUM.CALLER_AGENT_VALUE) 
                {
                    if (eachTransferRes.CallerType == CallerType || eachTransferRes.CallerType == PhoneBookConstants_H_HUM.CALLER_BROKER_VALUE) 
                    {
                        defaultTransferList.add(eachTransferRes);
                    }
                }
                else if (CallerType == PhoneBookConstants_H_HUM.CALLER_GROUP_VALUE) 
                {
                    if (eachTransferRes.CallerType == CallerType || eachTransferRes.CallerType == PhoneBookConstants_H_HUM.CALLER_EMPLOYER_VALUE)
                    {
                        defaultTransferList.add(eachTransferRes);
                    }
                }
                else
                {
                    if (eachTransferRes.CallerType == CallerType) 
                    {
                        defaultTransferList.add(eachTransferRes);
                    }
                }   
            }
            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.alphabetizeCallReasonsSwitch)){
              if(defaultTransferList!=null && !defaultTransferList.isEmpty()){
                defaultTransferList=sortDefaultTransferList(defaultTransferList);
            }  
            }
            
        }
        catch(Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'CreateDefaultTransferListByCallerType','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg)); 
        }
    }

    
    /**
* <p>
* Method to handle Outbound Calls
*
* @return Void
*/
    public void onOutBoundCallConnect()
    {
        errorMessage='Please enter 7 DD or 10 Digit phone number ';
        isCallConnected = true;
        isCallNotConnected = false;
        isShowBox = true;
        isshowClickToDial=false;
        PaymentTypeSelected = PhoneBookConstants_H_HUM.SELECT_VAL;
    }
    
    /**
* <p>
* Method to set the Caller Type based on the caller info received from getCallData
*
* @return Void
*/
    public void setCallerType(List<PhoneBook_DTO_HUM.CallDataValuesList> arrayData)
    {
        for(Object arr:arrayData)
        {
            String strr = JSON.serialize(arr);
            Map<String, Object> dt =   (Map<String, Object>) JSON.deserializeUntyped(strr);
            DataModal.put(dt.get(PhoneBookConstants_H_HUM.DATANAME_VAL), dt.get(PhoneBookConstants_H_HUM.DATAVALUE_VAL));
        }
        if(DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.M_VAL || DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.S_VAL)
        {
            mflag = true;
            callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_MEMBER_VALUE;
        }
        else if(DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.A_VAL || DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.B_VAL)
        {
            DataModal.put(PhoneBookConstants_H_HUM.INQR_TYP_VAL, PhoneBookConstants_H_HUM.A_VAL); 
            if(DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) == PhoneBookConstants_H_HUM.B_VAL){
                DataModal.put(PhoneBookConstants_H_HUM.INQA_TYP_VAL, PhoneBookConstants_H_HUM.A_VAL); 
            }
            aflag = true;
            callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_AGENT_VALUE; 
        }
        else if(DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.P_VAL || DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.PPI_VAL)
        {
            pflag = true;
            callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_PROVIDER_VALUE;
        }
        else if(DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.E_VAL || DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.G_VAL)
        {
            gflag = true;
            callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_GROUP_VALUE;
        }
        //US#2191493 - SFDC_Caller Type population
        else if (DataModal.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL) == PhoneBookConstants_H_HUM.U_VAL && PhoneBook_H_HUM.isCRMFunctionalityON('2191493'))
        {
            callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_UNKNOWN;
        }
    }
    
    /**
* <p>
* Method to create the search Page URL for Caller Type Member data
*
* @return Void
*/
    public void UpdateSearchPageURLForCallerTypeMember(Map<String,SOFTPHONE_URL_PARAM__c> sup)
    {
        for(Object dtKey:DataModal.keySet())
        {
            String SupNameParam = dtKey+PhoneBookConstants_H_HUM.M_VAL;
            if(sup.containsKey(SupNameParam.toUpperCase())) 
            {
                if(dtKey !=PhoneBookConstants_H_HUM.INQR_TYP_VAL && dtKey != PhoneBookConstants_H_HUM.INQA_TYP_VAL && DataModal.get(dtKey)!='')
                {
                    if(sup.get(SupNameParam).Value__c != PhoneBookConstants_H_HUM.DOB_VAR )
                    {
                        searchPageURl = searchPageURl + sup.get(SupNameParam).Value__c +'='+DataModal.get(dtKey)+'&';
                    }
                    else if(sup.get(SupNameParam).Value__c == PhoneBookConstants_H_HUM.DOB_VAR) 
                    {
                        String datevalue;
                        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.MemberDOBFormatSwitch))
                        {
                            datevalue = setDateFormatForBirthDate((String)DataModal.get(dtKey));
                        } 
                        else
                        {
                            datevalue = (String)DataModal.get(dtKey);
                            datevalue = datevalue.replaceAll('\\-','\\/');
                        }
                        searchPageURl = searchPageURl + sup.get(SupNameParam).Value__c +'='+datevalue+'&';
                    }  
                }
                else if(dtKey==PhoneBookConstants_H_HUM.INQA_TYP_VAL && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.M_VAL)
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParam).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
                else if(dtKey==PhoneBookConstants_H_HUM.INQA_TYP_VAL && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.S_VAL)
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.M_VAL);
                    searchPageURl = searchPageURl + sup.get(SupNameParam).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
                else if(dtKey==PhoneBookConstants_H_HUM.INQR_TYP_VAL && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.M_VAL)
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParam).Value__c +'='+DataModal.get(dtKey)+'&iWithTypeDesc=Member'+'&'; 
                } 
                else if(dtKey==PhoneBookConstants_H_HUM.INQR_TYP_VAL && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.S_VAL)
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.M_VAL);
                    searchPageURl = searchPageURl + sup.get(SupNameParam).Value__c +'='+DataModal.get(dtKey)+'&iWithTypeDesc=Member'+'&'; 
                } 
            }
        }
    }
    
    /**
* <p>
* Method to create the search Page URL for Caller Type Agent
*
* @return Void
*/
    public void UpdateSearchPageURLForCallerTypeAgent(Map<String,SOFTPHONE_URL_PARAM__c> sup)
    {
        for(Object dtKey:DataModal.keySet())
        {
            String SupNameParamA = dtKey+PhoneBookConstants_H_HUM.A_VAL;
            String SupNameParamG = dtKey+PhoneBookConstants_H_HUM.G_VAL;
            String SupNameParamM = dtKey+PhoneBookConstants_H_HUM.M_VAL;
            String SupNameParamP = dtKey+PhoneBookConstants_H_HUM.P_VAL;

            if(dtKey != PhoneBookConstants_H_HUM.INQR_TYP_VAL && dtKey != PhoneBookConstants_H_HUM.INQA_TYP_VAL && DataModal.get(dtKey) != '')
            {
                if(!dtKey.ToString().contains(PhoneBookConstants_H_HUM.INQA_VAL) )
                {
                    if(dtKey == PhoneBookConstants_H_HUM.INQR_ID && DataModal.get(dtKey) != '' && DataModal.get(dtKey).ToString().length()==9 && sup.containsKey(SupNameParamP.toUpperCase()))
                    {
                        searchPageURl = searchPageURl + sup.get(SupNameParamP).Value__c +'='+DataModal.get(dtKey)+'&'; 
                    }
                    else if(sup.containsKey(SupNameParamA.toUpperCase()))
                    {     
                        searchPageURl = searchPageURl + sup.get(SupNameParamA).Value__c +'='+DataModal.get(dtKey)+'&';        
                    }
                }
                else
                {
                    if(sup.containsKey(SupNameParamG.toUpperCase()) && ( DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) == PhoneBookConstants_H_HUM.G_VAL || DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) == PhoneBookConstants_H_HUM.E_VAL ))
                    {
                        searchPageURl = searchPageURl + sup.get(SupNameParamG).Value__c +'='+DataModal.get(dtKey)+'&';
                    }
                    else if(sup.containsKey(SupNameParamM.toUpperCase()) && DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) == PhoneBookConstants_H_HUM.M_VAL)
                    {
                        searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&';
                    } 
                    else if(sup.containsKey(SupNameParamM.toUpperCase()) && DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL) == PhoneBookConstants_H_HUM.S_VAL)
                    {
                        searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&';
                    } 
                }
            }
            else if(dtKey == PhoneBookConstants_H_HUM.INQA_TYP_VAL && sup.containsKey(SupNameParamM.toUpperCase()))
            {
                if(DataModal.get(dtKey) == PhoneBookConstants_H_HUM.E_VAL )
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey) +'&iAboutTypeDesc=Employer'+'&';
                }
                else if(DataModal.get(dtKey) == PhoneBookConstants_H_HUM.G_VAL )
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Group'+'&';
                }
                else if(DataModal.get(dtKey) == PhoneBookConstants_H_HUM.M_VAL )
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
                else if(DataModal.get(dtKey) == PhoneBookConstants_H_HUM.S_VAL)
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.M_VAL);
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
            }
            else if(dtKey == PhoneBookConstants_H_HUM.INQR_TYP_VAL && sup.containsKey(SupNameParamM.toUpperCase()))
            {
                if(DataModal.get(dtKey) == PhoneBookConstants_H_HUM.A_VAL || DataModal.get(dtKey) == PhoneBookConstants_H_HUM.B_VAL)
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.A_VAL);
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iWithTypeDesc=Agent'+'&';
                }  
            }
        }
    }
    
    /**
* <p>
* Method to create the search Page URL for Caller Type Provider
*
* @return Void
*/
    public void UpdateSearchPageURLForCallerTypeProvider(Map<String,SOFTPHONE_URL_PARAM__c> sup)
    {
        boolean isInqrVal = false;
        //US-5602717 Added below changes to remove last name from search page URL if tax id is available in Provider call data. 
        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
        {
            object INQR_VAL = DataModal.get(PhoneBookConstants_H_HUM.INQR_ID);
            if(INQR_VAL != null && string.valueOf(INQR_VAL) != '' ){
                isInqrVal = true;
            }
        }
        for(Object dtKey:DataModal.keySet())
        {
            String SupNameParamP = dtKey+PhoneBookConstants_H_HUM.P_VAL;
            String SupNameParamM = dtKey+PhoneBookConstants_H_HUM.M_VAL;
            if(dtKey != PhoneBookConstants_H_HUM.INQR_TYP_VAL && dtKey != PhoneBookConstants_H_HUM.INQA_TYP_VAL && DataModal.get(dtKey) != '')
            {
                if(!dtKey.ToString().contains(PhoneBookConstants_H_HUM.INQA_VAL))
                {
                    if(sup.containsKey(SupNameParamP.toUpperCase()) )
                    {
                        //US-5602717 Added below changes to remove last name from search page URL if tax id is available in Provider call data.
                        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch))
                        {
                            if(isInqrVal ==true)
                            {
                                if( sup.get(SupNameParamP).Value__c != PhoneBookConstants_H_HUM.INQR_LAST_VALUE)
                                {
                                    searchPageURl = searchPageURl + sup.get(SupNameParamP).Value__c +'='+DataModal.get(dtKey)+'&';
                                }
                            }
                            else
                            {
                                searchPageURl = searchPageURl + sup.get(SupNameParamP).Value__c +'='+DataModal.get(dtKey)+'&';
                            }
                        }
                        else{
                            if( sup.get(SupNameParamP).Value__c != PhoneBookConstants_H_HUM.INQR_NPI_VALUE)
                            {
                                searchPageURl = searchPageURl + sup.get(SupNameParamP).Value__c +'='+DataModal.get(dtKey)+'&';
                            }

                        }
                        
                    }
                }
                else
                {
                    if(sup.containsKey(SupNameParamM.toUpperCase()))
                    {
                        if(sup.get(SupNameParamM).Value__c != PhoneBookConstants_H_HUM.DOB_VAR)
                        {
                            searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&';
                        }
                        else if(sup.get(SupNameParamM).Value__c == PhoneBookConstants_H_HUM.DOB_VAR) 
                        {
                            String datevalue;
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.MemberDOBFormatSwitch))
                            {
                                datevalue = setDateFormatForBirthDate((String)DataModal.get(dtKey));
                            } 
                            else
                            {
                                datevalue = (String)DataModal.get(dtKey);
                                datevalue = datevalue.replaceAll('\\-','\\/');
                            }
                            searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+datevalue+'&';
                        }
                    }
                }
            }
            else if(dtKey == PhoneBookConstants_H_HUM.INQA_TYP_VAL)
            {
                if(sup.containsKey(SupNameParamM.toUpperCase()) && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.M_VAL  )
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
                else if(sup.containsKey(SupNameParamM.toUpperCase()) && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.S_VAL  )
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.M_VAL);
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
            }
            else if(dtKey == PhoneBookConstants_H_HUM.INQR_TYP_VAL)
            {
                if(sup.containsKey(SupNameParamM.toUpperCase()) && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.P_VAL )
                {
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iWithTypeDesc=Provider'+'&';
                }  
                else if(sup.containsKey(SupNameParamM.toUpperCase()) && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.PPI_VAL )
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.P_VAL);
                    searchPageURl = searchPageURl + sup.get(SupNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iWithTypeDesc=Provider'+'&';
                }  
            }
        }
    }
    
    /**
* <p>
* Method to create the search Page URL for Caller Type Group
*
* @return Void
*/
    public void UpdateSearchPageURLForCallerTypeGroup(Map<String,SOFTPHONE_URL_PARAM__c> sup)
    {
        for(Object dtKey:DataModal.keySet())
        {
            String supNameParamG = dtKey + PhoneBookConstants_H_HUM.G_VAL;
            String supNameParamM = dtKey + PhoneBookConstants_H_HUM.M_VAL;
            
            if(dtKey != PhoneBookConstants_H_HUM.INQR_TYP_VAL && dtKey != PhoneBookConstants_H_HUM.INQA_TYP_VAL)
            {
                if(!dtKey.ToString().contains(PhoneBookConstants_H_HUM.INQA_VAL))
                {
                    if(sup.containsKey(supNameParamG.toUpperCase()) && DataModal.get(dtKey) != '')
                    {
                        searchPageURl = searchPageURl + sup.get(supNameParamG).Value__c +'='+DataModal.get(dtKey)+'&';
                    }
                    else if(sup.containsKey(supNameParamG.toUpperCase()) && dtKey == PhoneBookConstants_H_HUM.INQR_ID && DataModal.get(dtKey) == '')
                    {
                        if(DataModal.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL) != '')
                        {
                            searchPageURl = searchPageURl + sup.get(supNameParamG).Value__c +'='+DataModal.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL)+'&';
                        }
                    }
                }
                else
                {
                    if(sup.containsKey(supNameParamM.toUpperCase()) && DataModal.get(dtKey) != '')
                    {
                        if(sup.get(supNameParamM).Value__c != PhoneBookConstants_H_HUM.DOB_VAR)
                        {
                            searchPageURl = searchPageURl + sup.get(supNameParamM).Value__c +'='+DataModal.get(dtKey)+'&';
                        }
                        else if(sup.get(supNameParamM).Value__c == PhoneBookConstants_H_HUM.DOB_VAR) 
                        {
                            String datevalue;
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.MemberDOBFormatSwitch))
                            {
                                datevalue = setDateFormatForBirthDate((String)DataModal.get(dtKey));
                            } 
                            else
                            {
                                datevalue = (String)DataModal.get(dtKey);
                                datevalue = datevalue.replaceAll('\\-','\\/');
                            }
                            searchPageURl = searchPageURl + sup.get(supNameParamM).Value__c +'='+datevalue+'&';
                        } 
                    }
                }
            }
            else if(dtKey == PhoneBookConstants_H_HUM.INQA_TYP_VAL)
            {
                if(sup.containsKey(supNameParamM.toUpperCase()) && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.M_VAL )
                {
                    searchPageURl = searchPageURl + sup.get(supNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
                else if(sup.containsKey(supNameParamM.toUpperCase()) && DataModal.get(dtKey) == PhoneBookConstants_H_HUM.S_VAL )
                {
                    DataModal.put(dtKey,PhoneBookConstants_H_HUM.M_VAL);
                    searchPageURl = searchPageURl + sup.get(supNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iAboutTypeDesc=Member'+'&';
                }
            }
            else if(dtKey == PhoneBookConstants_H_HUM.INQR_TYP_VAL)
            {
                if(sup.containsKey(supNameParamM.toUpperCase()) && (DataModal.get(dtKey) == PhoneBookConstants_H_HUM.G_VAL || DataModal.get(dtKey) == PhoneBookConstants_H_HUM.E_VAL ))
                {
                    searchPageURl = searchPageURl + sup.get(supNameParamM).Value__c +'='+DataModal.get(dtKey)+'&iWithTypeDesc=Group'+'&';
                }  
            }
        }
    } 

/**
* <p>
* Method to populate the Secure payment Types
*
* @return List<SelectOption>
*/
    public List<SelectOption> getSecurePaymentTypes()
    {
        List<SelectOption> options = new List<SelectOption>();
        try
        {
            options.add(new SelectOption(PhoneBookConstants_H_HUM.SELECT_VAL,PhoneBookConstants_H_HUM.SELECT_VAL));
            List<SecurePayments_HUM__c> allSecurePayment = SecurePayments_HUM__c.getall().values();
            
            if(!allSecurePayment.isEmpty())
            {
                for(SecurePayments_HUM__c sp : allSecurePayment)
                {
                    options.add(new SelectOption(sp.SecurePaymentName__c,sp.SecurePaymentName__c));
                }
                secureFlowId = allSecurePayment[0].FlowId__c;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'getSecurePaymentTypes');
        }
        return options;
    }

/**
* Returns request object of continuation object
* <p>
* Method to invoke the saveCallData continuation requests after collecting and forming the request structure
*
* @param  String UUID
* @return continuation object
*/
    @RemoteAction
    public static Continuation updateInteractionInfoToIVR(String UUID,map<String,String> StateDataMapping,String intAbtId)
    {
        String saveCallDataReq = '';
        Continuation ret = null;
        try
        {
            Boolean bSwitchSaveCallData_2357061 = PhoneBook_H_HUM.isCRMFunctionalityON('2357061'); 
            if(bSwitchSaveCallData_2357061)
            {
                saveCallDataReq = PhoneBook_S_HUM.createSaveCalldataRequest(UUID,StateDataMapping,intAbtId);
                ret = PhoneBook_S_HUM.saveCallDataServiceCallout(saveCallDataReq); 
            }
        }
        catch(Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex), 'PhoneBook_C_HUM', 'updateInteractionInfoToIVR','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));  
        }
        return ret;
    }
    
    /**
* Returns responses of continuation object
* <p>
* Callback Method to parse the saveCallData service response
*
* @param  List<String> List of Continuation Labels
* @return Object
*/
    public static Object processResponseSaveCallData(list<string> lstLabels,object oState) 
    {
        HttpResponse responseRet = new HttpResponse();
        try
        {
            responseRet = (HttpResponse) PhoneBook_S_HUM.processResponseSaveCallData(lstLabels,oState) ;
            if (responseRet == null || ((responseRet.getStatusCode() != PhoneBookConstants_H_HUM.HTTP_OK_HUM && responseRet.getStatusCode() != PhoneBookConstants_H_HUM.HTTP_ACCEPTED_HUM) || (responseRet.getBody().containsIgnoreCase(PhoneBookConstants_H_HUM.VOB_FAULT_CODE))))
        	{
            	throw new CalloutException();
            }
        }
        catch(Exception ex)
        {
            String errorLogNum =  HUMExceptionHelper.logError(String.valueOf(ex) + responseRet, 'PhoneBook_C_HUM', 'processResponseSaveCallData','System.InvalidParameterValueException','Integration Error');
            String errMsg = 'An error has occurred; please refresh your browser and try again.  If this error persists, contact the help desk for assistance: '+errorLogNum;
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,errMsg));  
        }
        return null;
    }
    
    /**
* <p>
*Creates the interactionid and aboutid for the transfer to second agent to open about page
*
* @param  NA
* 
*/
    
    public void getAboutIdAndInteractionId()
    {
        Interaction__c clonedInteraction;
        List<Interaction__c> lstInteractionRec = PhoneBook_D_HUM.getInteraction(uuidataValue);
        List<Interaction_Member__c> intMembersLst = new List<Interaction_Member__c>();
        if (lstInteractionRec.size() > 0) 
        {
            Interaction__c oInt = lstInteractionRec[0];
            if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){oInt.Authenticated__c = DataModal.get('AUTHENTICATION_IND') !=null ? (DataModal.get('AUTHENTICATION_IND') == 'Y'?true:false) : false;}
            if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){oInt.Genesys_Interaction_ID__c = idval;}
            //US 3849706,3944555 : updating interaction origin as Inbound call in case of Watson or System interaction clone.  
            if(PhoneBook_H_HUM.isCRMFunctionalityON('3849706'))
            {
                if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){oInt.Interaction_Origin__c = PhoneBookConstants_H_HUM.Inbound_Call;}
                if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){oInt.Genesys_Interaction_ID__c =idval;}
            }   
            if (PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitch))
            {
                if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes)) {
                    if(Schema.sObjectType.Interaction__c.fields.Genesys_Data__c.isCreateable()){oInt.Genesys_Data__c = calllogattributes;}
                }
            }
            //US 4878631: T1PRJ0036776: RxE - Classic - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
            if(PhoneBook_H_HUM.isCRMFunctionalityON('4878631')){
                if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes)) {
                    if(Schema.sObjectType.Interaction__c.fields.Campaign_name_and_Keycode__c.isCreateable()){
                        oInt.Campaign_name_and_Keycode__c = PhoneBook_LD_HUM.getKeycodeCampaignNameFormat(calllogattributes); 
                    }
                }
            }   
            String dataModalString = JSON.serialize(DataModal);
            if(PhoneBook_H_HUM.isCRMFunctionalityON('2704639')) {
                if(Schema.sObjectType.Interaction__c.fields.Call_Data__c.isCreateable()){
                	oInt.Call_Data__c = (dataModalString != null && dataModalString.length() > 2500) ? '' : dataModalString;
                }
            }
            
            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
            {
                if(isRefreshedval)
                {
                    interactionIdVal = oInt.id;
                    autoInteractionIDvalue = oInt.id;
                }
                else{
                    String intId = cloneInteraction(oInt);
                    interactionIdVal = intId;
                    autoInteractionIDvalue = intId;
                }
            }
            else
            {
                if(!isRefreshedval)
                {
                    clonedInteraction = oInt.clone(false,true,false,false);
                    String intId = PhoneBook_D_HUM.insertInteraction(clonedInteraction);
                    interactionIdVal = intId;
                    autoInteractionIDvalue = intId;
                }
                else
                {
                    interactionIdVal = oInt.id;
                    autoInteractionIDvalue = oInt.id;
                }
                if (oInt.Interaction_Members__r.size() == 1) 
                {
                    if(!isRefreshedval)
                    {
                        Interaction_Member__c clonedIntMember = oInt.Interaction_Members__r[0].clone(false,true,false,false);
                        clonedIntMember.Interaction__c = clonedInteraction.id;
                        intMembersLst.add(clonedIntMember);
                        PhoneBook_D_HUM.insertInteractionMember(intMembersLst);
                    }
                    aboutIdVal = oInt.Interaction_Members__r[0].Interacting_About__c;
                }
                else if(oInt.Interaction_Members__r.size() > 1)
                {
                    for(Interaction_Member__c eachMember:oInt.Interaction_Members__r )
                    {
                        if(!isRefreshedval)
                        {
                            Interaction_Member__c clonedIntMember = eachMember.clone(false,true,false,false);
                            clonedIntMember.Interaction__c = clonedInteraction.id;
                            intMembersLst.add(clonedIntMember);
                        }
                        if(eachMember.Interacting_About__r.BirthDate__c == DataModal.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL)
                        && eachMember.Interacting_About__r.LastName == DataModal.get(PhoneBookConstants_H_HUM.INQA_LAST)
                        && eachMember.Interacting_About__r.FirstName == DataModal.get(PhoneBookConstants_H_HUM.INQA_FIRST)
                        && eachMember.Interacting_About_Type__c.subString(0,1) == DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL))
                        { 
                            aboutIdVal = eachMember.Interacting_About__c; 
                        }
                    }
                    if(!isRefreshedval)
                    {
                        PhoneBook_D_HUM.insertInteractionMember(intMembersLst);
                    }
                }
            }
        }
        //US 3944531 : UCID not mapping to UUID field in Salesforce CRM (INC2009416)- if interaction creation failed for Agent 1 then added below logic for handling interaction creation for transferred call 
        else if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
        {
            if(PhoneBook_H_HUM.isCRMFunctionalityON('2230000'))
            {
                autoInteractionIDvalue = createAutoInteraction(DataModal,uuidataValue,isRefreshedval,idval,calllogattributes);
            }
        }
    }

    /**
* <p>
*  Method to retrieve memberId from  Pharmacy Page
* 
* @param  String policyMemberId, String paymentType
* @return Map<String,String>
*/
    @RemoteAction
    public static  Map<String,String> fetchPolicyMemberData(String policyMemberId,String paymentType)
    {
        Map<String,String> secureDataParams = PhoneBook_D_HUM.fetchPolicyMemberData(policyMemberId, paymentType);
        return secureDataParams;
    }

    /**
    * Returns interaction Id of the auto created interaction
    * <p>
    * Method to create auto interaction
    *
    *@param  Object searchModal,String UUID,Boolean isrefresh,String genesysInteractionId
    * @return interactionId
    */

     public String createAutoInteraction(Object searchModal,String UUID,Boolean isrefresh,String genesysInteractionId, String calllogattributes)
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

        if((String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_ID) != null && (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_ID) !='' )
        {
            INQR_ID = (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_ID);
        }
        else if((String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL)==PhoneBookConstants_H_HUM.G_VAL || (String)searchDataMap.get(PhoneBookConstants_H_HUM.INQR_TYP_VAL)==PhoneBookConstants_H_HUM.E_VAL )
        {
            if((String)searchDataMap.get(PhoneBookConstants_H_HUM.INQA_GRP_ID_VAL)!=null)
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


        //US2760646- For Authenticated Member call, if only “With” specified and "About" not specified or "About Type" is specified as "M/S" then set the “About” to the same as the With 
        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
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
        if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
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
        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonCallSecondMemberSwitch)==false){
        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch)) 
        { 
            lstInteractionRec = [SELECT Id, Caller_Name__c, Call_data__c, Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Authenticated__c,Interaction__c FROM Interaction_Members__r order by Name ASC) FROM Interaction__c where UUID__c =: UUID order by Name desc limit 1];
        } 
        else 
        { 
            lstInteractionRec = [SELECT Id, Caller_Name__c,Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c,Survey_ID__c, Call_Category_Description__c, CreatedDate,Call_data__c,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c,Authenticated__c, Interaction__c FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where UUID__c =: UUID order by Name desc limit 1];
        }
        }
        
        if (lstInteractionRec.size() > 0) 
        {
            Interaction__c oInt = lstInteractionRec[0];
            if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){oInt.Authenticated__c = AUTHENTICATION_IND;}
            //US 3849706,3944555 : updating interaction origin as Inbound call in case of Watson or System interaction clone.  
            if(PhoneBook_H_HUM.isCRMFunctionalityON('3849706'))
            {
                if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){oInt.Interaction_Origin__c = PhoneBookConstants_H_HUM.Inbound_Call;}
                if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){oInt.Genesys_Interaction_ID__c = genesysInteractionId;}
            }
            if (PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitch))
            {
                if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes))
                {
                    if(Schema.sObjectType.Interaction__c.fields.Genesys_Data__c.isCreateable()){oInt.Genesys_Data__c = calllogattributes;}
                }
            }
            //US 4878631: T1PRJ0036776: RxE - Classic - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
            if(PhoneBook_H_HUM.isCRMFunctionalityON('4878631')){
                if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes)) {
                    if(Schema.sObjectType.Interaction__c.fields.Campaign_name_and_Keycode__c.isCreateable()){
                        oInt.Campaign_name_and_Keycode__c = PhoneBook_LD_HUM.getKeycodeCampaignNameFormat(calllogattributes); 
                    }
                }
            }
            if(!isrefresh)
            {
                //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
                {
                    interactionId = cloneInteraction(oInt);  
                }
                else{
                    clonedInteraction = oInt.clone(false,true,false,false);
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
        }
        else
        {
            if(!isrefresh)
            {
                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = AUTHENTICATION_IND;}
                if(Schema.sObjectType.Interaction__c.fields.Survey_ID__c.isCreateable()){inc.Survey_ID__c = SURVEY_ID;}
                if(Schema.sObjectType.Interaction__c.fields.Call_Category_Description__c.isCreateable()){inc.Call_Category_Description__c = Call_Catg_Desc;}
                if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){inc.Genesys_Interaction_ID__c = genesysInteractionId;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()){incMember.Interaction__c = inc.Id;}
                if(Schema.sObjectType.Interaction__c.fields.uuid__c.isCreateable()){inc.uuid__c = UUID;}
                if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){inc.Interaction_Origin__c = 'Inbound Call';}
                if (PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.updateCampaignNameAndKeycodeSwitch))
                {
                    if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes))
                        if(Schema.sObjectType.Interaction__c.fields.Genesys_Data__c.isCreateable()){inc.Genesys_Data__c = calllogattributes;}
                }
                //US 4878631: T1PRJ0036776: RxE - Classic - Save Campaign Keycode & Campaign name data to existing column "interaction :campaign name and Keycode" - Genesys
                if(PhoneBook_H_HUM.isCRMFunctionalityON('4878631')){
                    if(calllogattributes != null && string.isNotBlank(calllogattributes) && string.isNotEmpty(calllogattributes))
                        if(Schema.sObjectType.Interaction__c.fields.Campaign_name_and_Keycode__c.isCreateable()){
                            inc.Campaign_name_and_Keycode__c = PhoneBook_LD_HUM.getKeycodeCampaignNameFormat(calllogattributes);
                        }
                }
                if(PhoneBook_H_HUM.isCRMFunctionalityON('2704639')) {
                    if(Schema.sObjectType.Interaction__c.fields.Call_data__c.isCreateable()){
                        inc.Call_data__c = (searchDataString != null && searchDataString.length() > 2500) ? '' : searchDataString;  
                    }
                }
                if (INQR_TYP != '') 
                {
                    Boolean isAccountId= false;
                    switch on INQR_TYP 
                    {
                        when 'M','S'
                        {
                            isCallerTypehandled =true;
                            String AccountId = '';
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('3701398'))  
                                AccountId = getCallMemberAccountDetails(INQR_ID, DependentCode, INQR_GEN_KEY, INQR_FIRST, INQR_LAST, INQR_PLTFRM_CD, AUTHENTICATION_IND);
                            else
                                AccountId = getMemberWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);

                            if (AccountId != '') 
                            {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Member';}
                                isAccountId= true;
                            }
                        }
                        when 'P','PPI'
                        {
                            isCallerTypehandled =true;
                            String AccountId = getProviderWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB, INQR_NPI);
                            if (AccountId !='') 
                            {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Provider';}
                                isAccountId= true;
                            }
                        }
                        when 'A','B'
                        {                            
                            isCallerTypehandled =true;
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('4599393')== true){
								String AccountId =PhoneBook_LD_HUM.getAgentAccountId(INQR_ID);
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
                        when 'G','E' 
                        {                            
                            isCallerTypehandled =true;
                            String AccountId = getGroupWithAccountId( INQR_ID, INQR_FIRST, INQR_LAST, INQR_DOB, INQA_TYP, INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                            if (AccountId !='') 
                            {
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = AccountId;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = 'Group';}
                                isAccountId= true;
                            }
                        }
                    }
                    if(!isAccountId)
                    {
                        if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
                        {
                            InteractingWithAccountIdNotfound = true;
                            if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                            isAccountId= true;
                        }
                        else{
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2895841')){
                                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('3701398'))
                            {
                                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = true;}
                                isAccountId= true;
                            }
                            else
                            {
                                if(INQR_FIRST != '' || INQR_LAST != '') 
                                {
                                    if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQR_FIRST +' '+ INQR_LAST;}
                                    isAccountId= true;
                                }
                                else if(INQA_TYP == INQR_TYP  && (INQA_FIRST != '' || INQA_LAST != '')) 
                                {
                                    if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQA_FIRST +' '+ INQA_LAST;}
                                    isAccountId= true;
                                }
                                else if(AUTHENTICATION_IND == false)
                                {
                                    isAccountId= true;
                                }
                            }
                        }
                    }
                    //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                    if(INQA_TYP != ''){
                        switch on INQA_TYP 
                        {
                            when 'M','S'
                            {
                                String AboutId = '';
                                if(PhoneBook_H_HUM.isCRMFunctionalityON('3701398'))
                                {
                                    if (isWithAboutSame && inc.Interacting_With__c != null) {
                                        AboutId = inc.Interacting_With__c;
                                    }
                                    else{
                                        AboutId = getCallMemberAccountDetails(INQA_ID, DependentCode, MEMBER_PERS_GEN_KEY, INQA_FIRST, INQA_LAST, INQA_PLTFRM_CD, AUTHENTICATION_IND);
                                    }
                                }
                                else
                                    AboutId = getMemberAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);

                                if (AboutId != '') 
                                {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = AboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Member';}
                                    isAboutId = true;
                                }
                            }
                            
                            when 'P','PPI'
                            {
                                String aboutId = getProviderAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                                
                                if (aboutId !='') 
                                {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = aboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Provider';}
                                    isAboutId = true;
                                }
                            }
                            
                            when 'A','B'
                            {
                                String aboutId= getAgentAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                                
                                if (aboutId !='') 
                                {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = aboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Agent';}
                                    isAboutId = true;
                                }
                            }
                            when 'G','E' 
                            {
                                String aboutId=getGroupAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
                                
                                if (aboutId !='') 
                                {
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = aboutId;}
                                    if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c = 'Group';}
                                    isAboutId = true;
                                }
                            }
                        }
                    }
                    if(isAccountId)
                    {
                        try{
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2876026'))
                            {
                                interactionLst.add(inc);
                                clonedAgentInteraction = inc.clone(false,true,false,false);
                                interactionLst.add(clonedAgentInteraction);
                                insert interactionLst;
                                interactionId = interactionLst.get(1).Id;
                            }
                            else{
                                //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                                if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
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
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531') && AUTHENTICATION_IND == true)
                            {
                                if(!isCallerTypehandled)
                                    String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.CALLER_TYPE_NOT_HANDLED_LOG, new string[]{UUID, INQR_TYP}), 'PhoneBook_C_HUM', 'createAutoInteraction','CustomException','Integration Error');

                                if(InteractingWithAccountIdNotfound && isCallerTypehandled)
                                    String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.INTERACTING_WITH_ACCOUNT_ID_NOT_FOUND_LOG, new string[]{UUID, INQR_TYP}), 'PhoneBook_C_HUM', 'createAutoInteraction','CustomException','Integration Error');
                            }
                        }
                        catch(Exception e)
                        {
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
                            {
                                e.setMessage(string.format(PhoneBookConstants_H_HUM.CREATE_AUTO_INTERACTION_FAIL_LOG, new string[]{UUID, e.getMessage()}));
                                HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createAutoInteraction');
                                if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = null;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = '';}
                            }
                            else 
                            {
                                HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createAutoInteraction');
                                if(PhoneBook_H_HUM.isCRMFunctionalityON('2895841'))
                                {
                                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                                }
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){inc.Interacting_With__c = null;}
                                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_Type__c.isCreateable()){inc.Interacting_With_Type__c = '';}
                                if(!PhoneBook_H_HUM.isCRMFunctionalityON('3701398'))
                                {
                                    if(INQR_FIRST != '' || INQR_LAST != '') 
                                    {
                                        if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQR_FIRST +' '+ INQR_LAST;}
                                    }
                                    else if(INQA_TYP == INQR_TYP  && (INQA_FIRST != '' || INQA_LAST != '')) 
                                    {
                                        if(Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()){inc.Caller_Name__c = INQA_FIRST +' '+ INQA_LAST;}
                                    }
                                }
                                else{
                                    if(Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()){inc.Authenticated__c = false;}
                                }
                            }
                            if(PhoneBook_H_HUM.isCRMFunctionalityON('2876026'))
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
                                //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                                if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch)){

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
                    //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                    if(interactionId != '')
                    {
                        if(isAboutId)
                        {
                            try{
                                if(PhoneBook_H_HUM.isCRMFunctionalityON('2876026'))
                                {
                                    incMember.Interaction__c = interactionLst.get(0).Id;
                                    intMembersLst.add(incMember);
                                    Interaction_Member__c clonedAgentIntMember = incMember.clone(false,true,false,false);
                                    clonedAgentIntMember.Interaction__c = interactionLst.get(1).Id;
                                    intMembersLst.add(clonedAgentIntMember);
                                    insert intMembersLst;
                                }
                                else{
                                    //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                                    if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
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
                                if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
                                {
                                    e.setMessage(string.format(PhoneBookConstants_H_HUM.CREATE_AUTO_INTERACTION_MEMBER_FAIL_LOG, new string[]{UUID, e.getMessage()}));
                                    HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createAutoInteraction');
                                }
                                else{
                                    HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createAutoInteraction');
                                }
                            }
                        }
                        if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
                        {
                            if(AUTHENTICATION_IND && !isAboutId)
                                String errorLogNum =  HUMExceptionHelper.logError(string.format(PhoneBookConstants_H_HUM.INTERACTING_ABOUT_ACCOUNT_ID_NOT_FOUND_LOG, new string[]{UUID, INQA_TYP}), 'PhoneBook_C_HUM', 'createAutoInteraction','CustomException','Integration Error');
                        }
                    }
                }
                else
                {
                    try{
                        if(PhoneBook_H_HUM.isCRMFunctionalityON('2876026'))
                        {
                            interactionLst.add(inc);
                            clonedAgentInteraction = inc.clone(false,true,false,false);
                            interactionLst.add(clonedAgentInteraction);
                            insert interactionLst;
                            interactionId = interactionLst.get(1).Id;
                        }
                        else{
                            //US2760646: T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members)
                            if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.multiMemberAuthSwitch))
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
                        if(PhoneBook_H_HUM.isCRMFunctionalityON('3944531'))
                        {
                            e.setMessage(string.format(PhoneBookConstants_H_HUM.CREATE_AUTO_INTERACTION_FAIL_LOG, new string[]{UUID, e.getMessage()}));
                            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createAutoInteraction');
                        }
                        else {
                            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'createAutoInteraction');
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
        String tempAccountId = '';
        if ( INQR_ID != '') {
            List<List<sObject>> allAccountIdx = [FIND :INQR_ID IN Name FIELDS RETURNING Member_ID__c(Policy_Member__r.Member__c,Name,Policy_Member__r.Member__r.FirstName,Policy_Member__r.Member__r.LastName where ETL_Record_Deleted__c = false)];
            Member_ID__c[] AccountIdx = (Member_ID__c[])allAccountIdx[0];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AccountId = AccountIdx[0].Policy_Member__r.Member__c;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(Member_ID__c memPlan : AccountIdx){
                        if(INQR_FIRST != '' && INQR_LAST != '' && memPlan.Policy_Member__r.Member__r.FirstName == INQR_FIRST && memPlan.Policy_Member__r.Member__r.LastName == INQR_LAST && memPlan.Name == INQR_ID)
                        {
                            AccountId = memPlan.Policy_Member__r.Member__c;
                            break;
                        }
                        else if(memPlan.Name == INQR_ID){
                            tempAccountId = memPlan.Policy_Member__r.Member__c;
                            accountIds.add(memPlan.Policy_Member__r.Member__c);
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
    
    public static String getMemberAboutAccountId(String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB){
        String AboutId='';
        String sFirstName='' ;
        String sLastName='' ;
        String sBirthdate='';
        String tempAboutId='';
        if (INQA_ID != '') {
            List<List<sObject>> allAccountIdx = [FIND :INQA_ID IN Name FIELDS RETURNING Member_ID__c(Policy_Member__r.Member__c,Name,Policy_Member__r.Member__r.FirstName,Policy_Member__r.Member__r.LastName where ETL_Record_Deleted__c = false)];
            Member_ID__c[] AccountIdx = (Member_ID__c[])allAccountIdx[0];
            if(AccountIdx.size() > 0){
                if(AccountIdx.size() == 1) {
                    AboutId = AccountIdx[0].Policy_Member__r.Member__c ;
                }
                else{
                    Set<Id> accountIds = new Set<Id>();
                    for(Member_ID__c memPlan : AccountIdx){
                        if(INQA_FIRST != '' && INQA_LAST != '' && memPlan.Policy_Member__r.Member__r.FirstName == INQA_FIRST && memPlan.Policy_Member__r.Member__r.LastName == INQA_LAST && memPlan.Name == INQA_ID)
                        {
                            AboutId = memPlan.Policy_Member__r.Member__c;
                            break;
                        }
                        else if(memPlan.Name == INQA_ID){
                            tempAboutId = memPlan.Policy_Member__r.Member__c;
                            accountIds.add(memPlan.Policy_Member__r.Member__c);
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
    
    public static String getProviderWithAccountId(String INQR_ID,String INQR_FIRST,String INQR_LAST,String INQR_DOB,String INQA_TYP,String INQA_ID,String INQA_FIRST,String INQA_LAST,String INQA_DOB, string INQR_NPI){ 
        String taxId ='';
        String AccountId='';
        String sFirstName='';
        String sLastName='';

        //US-5602717 Added below changes to find interacting with record id based on tax id and npi combination if available in provider call data.
        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch)==true)
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
        
        if (INQR_ID != ''  && AccountId == '') {
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
                    else if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonCallSecondMemberSwitch)==true){
                        //US-5602717 if call data yields multiple Results in CRM, then the "Interacting With" will be blank. 
                        if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonProviderCallSwitch)==false)
                        {
                            AccountId = PhoneBook_LD_HUM.getProviderAccountId(AccountIdx);
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
                    else if(PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.watsonCallSecondMemberSwitch)==true){
                        aboutId = PhoneBook_LD_HUM.getProviderAccountId(AccountIdx);
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
        else if (INQA_TYP == 'A' || INQA_TYP == 'B' ) {
            AccountId = getAgentAboutAccountId( INQA_ID, INQA_FIRST, INQA_LAST, INQA_DOB);
        } 
        return AccountId;
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
    * <p>
    *  Method to remove duplicate queues from  Enterprise phoneBook list
    * 
    * @param  String PhoneBookName
    * @return List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>
    */
    public List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> removeDupPhBookList(String PhoneBookName)
    {
        Map<String,GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> uniqueQueuMap= new Map<String,GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> UniquePhBookUpdatedList = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> phBookList = new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        
        if(PhoneBookName=='select')
        {
            List<List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>> AllphBookList = phBookRulesMap.values();
            for(List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> eachphList:AllphBookList)
            {
            	phBookList.addAll(eachphList);
            }
        }
        else
        {
            phBookList = phBookRulesMap.get(PhoneBookName);
        }
        for(GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList eachQueue:phBookList)
        {
            uniqueQueuMap.put(eachQueue.QueueName+eachQueue.TransferNumber,eachQueue);
        }
        if(!uniqueQueuMap.isEmpty())
        {
            UniquePhBookUpdatedList = uniqueQueuMap.values();
        }
        return UniquePhBookUpdatedList;
    }

    /**
    * Returns interaction Id and UUId from the auto created interaction
    * <p>
    * Method to create auto interaction on Outbound calls
    *
    *@param  String focussedRecordIdOB,String genInteractionId,String focussedPageName
    * @return Map<String,String>
    */
    @RemoteAction
    public static Map<String,String> createAutoInteractionOnOB(String focussedRecordIdOB,String genInteractionId,String focussedPageName)
    {
        Map<String,String> resultMap = new Map<String,String>();
        String InteractionIdOB = '';
        Interaction__c intOB= new Interaction__c();
        Interaction_Member__c incMember = new Interaction_Member__c();
        if(Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()){intOB.Interaction_Origin__c = 'Outbound Call';}
        if(Schema.sObjectType.Interaction__c.fields.Genesys_Interaction_ID__c.isCreateable()){intOB.Genesys_Interaction_ID__c = genInteractionId;}     
        if (focussedPageName == 'AccountdetailPage')
        {
            List<Account> accList = [select id, Name,recordType.Name from Account where id=: focussedRecordIdOB Limit 1];
            if(accList.size()>0){
                 switch on accList[0].recordType.Name 
                 {
                    when 'Agent/Broker' { accList[0].recordType.Name = 'Agent'; }
                    when 'Unknown Member' { accList[0].recordType.Name = 'Unknown-Member'; }
                    when 'Unknown Group' { accList[0].recordType.Name = 'Unknown-Group'; }
                    when 'Unknown Provider' { accList[0].recordType.Name = 'Unknown-Provider';}
                    when 'Unknown Agent/Broker' { accList[0].recordType.Name = 'Unknown-Agent'; }
                }
                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){intOB.Interacting_With__c = accList[0].id;}
                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()){intOB.Interacting_With_type__c = accList[0].recordType.Name;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = accList[0].id;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c =accList[0].recordType.Name;}
            }
        }
        else if(focussedPageName == 'CaseDetailPage'){
            List<Case> CaseList = [select id, AccountId,Account.recordType.Name from Case where id=: focussedRecordIdOB Limit 1];
            if(CaseList.size()>0){
                switch on CaseList[0].Account.recordType.Name 
                {
                    when 'Agent/Broker' { CaseList[0].Account.recordType.Name = 'Agent'; }
                    when 'Unknown Member' { CaseList[0].Account.recordType.Name = 'Unknown-Member'; }
                    when 'Unknown Group' { CaseList[0].Account.recordType.Name = 'Unknown-Group'; }
                    when 'Unknown Provider' { CaseList[0].Account.recordType.Name = 'Unknown-Provider';}
                    when 'Unknown Agent/Broker' { CaseList[0].Account.recordType.Name = 'Unknown-Agent'; }
                }
                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){intOB.Interacting_With__c = CaseList[0].AccountId;}
                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()){intOB.Interacting_With_type__c = CaseList[0].Account.recordType.Name;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = CaseList[0].AccountId;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c =CaseList[0].Account.recordType.Name;}
            }
        }
        else if(focussedPageName == 'PolicyMemberPage'){
            List<Policy_Member__c> PolicyMemList = [select id, Member__c,Member__r.recordType.Name from Policy_Member__c where id=: focussedRecordIdOB Limit 1];
            if(PolicyMemList.size()>0){
                switch on PolicyMemList[0].Member__r.recordType.Name 
                {
                    when 'Agent/Broker' { PolicyMemList[0].Member__r.recordType.Name = 'Agent'; }
                    when 'Unknown Member' { PolicyMemList[0].Member__r.recordType.Name = 'Unknown-Member'; }
                    when 'Unknown Group' { PolicyMemList[0].Member__r.recordType.Name = 'Unknown-Group'; }
                    when 'Unknown Provider' { PolicyMemList[0].Member__r.recordType.Name = 'Unknown-Provider';}
                    when 'Unknown Agent/Broker' { PolicyMemList[0].Member__r.recordType.Name = 'Unknown-Agent'; }
                }
                if(Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()){intOB.Interacting_With__c = PolicyMemList[0].Member__c;}
                if(Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()){intOB.Interacting_With_type__c = PolicyMemList[0].Member__r.recordType.Name;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About__c.isCreateable()){incMember.Interacting_About__c = PolicyMemList[0].Member__c;}
                if(Schema.sObjectType.Interaction_Member__c.fields.Interacting_About_Type__c.isCreateable()){incMember.Interacting_About_Type__c =PolicyMemList[0].Member__r.recordType.Name;}
            }
        }
        insert intOB;
        if(Schema.sObjectType.Interaction_Member__c.fields.Interaction__c.isCreateable()){incMember.Interaction__c = intOB.Id;}
        insert incMember;
        List<Interaction__c> lstInteractionReclatest = [SELECT Id,Name, UUID__c, Genesys_Interaction_ID__c, CreatedDate FROM Interaction__c where Id =: intOB.Id order by CreatedDate desc limit 1]; 
        if(lstInteractionReclatest.size()>0)
        {
            String dateString = lstInteractionReclatest[0].CreatedDate.format('MMddYY',UserInfo.getTimeZone().toString());
            String currentTime = lstInteractionReclatest[0].CreatedDate.format('hhmmss',  UserInfo.getTimeZone().toString());
            String UUIDataValue = dateString + '000'+ currentTime + '00000'+ lstInteractionReclatest[0].Name.right(12).substring(0,12); 
            resultMap.put('UUIDataValue', UUIDataValue);
            if(lstInteractionReclatest[0].UUID__c ==Null){
                lstInteractionReclatest[0].UUID__c = UUIDataValue;
            } 
            update lstInteractionReclatest[0];
        }
        InteractionIdOB= (String) lstInteractionReclatest[0].id;
        resultMap.put('InteractionIdOB', InteractionIdOB);
        return resultMap;
    }
    
    /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the getCalldata continuation requests to get the caller information from IVR when multiple tabs are opened
    *
    * @return continuation object
    */
    public Continuation getDataFromWebServiceMultipleTabs()
    {
        defaultTransferList = new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        defaultTransferListAll =new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        Continuation ret = null;
        DataModal.put('uuidataValue', uuidataValue);
        try
        {
            if(String.isBlank(uuidataValue)||uuidataValue==null)
            {
                CalloutException e = new CalloutException();
                e.setMessage('UUIData not available!!');    
                throw e;
            }
            else
            { 
                ret = phshum.getDataFromWebServiceMultipletabs(uuidataValue,this);
            }
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'getDataFromWebServiceMultipletabs');
        }
        return ret;  
    } 
    
    /**
* Returns responses of continuation object
* <p>
* Callback Method to parse the getCallData service response
*
* @return Object
*/
    public  Object processResponseMultipletabs() 
    {
        autoInteractionIDvalue='';
        DataModal = new Map<Object,Object>();
        mflag=false;aflag=false;pflag=false;gflag=false;
        searchPageURl = '/apex/HUMSoftPhoneIntermediatePage?';
        object ret = null;
        isTransferredCall = false;
        aboutIdVal='';
        interactionIdVal='';
        String responseRet;
        Boolean bSwitchSaveCallData_2230090 = PhoneBook_H_HUM.isCRMFunctionalityON('2230090');
        Map<String,SOFTPHONE_URL_PARAM__c> supMap = new Map<String,SOFTPHONE_URL_PARAM__c>();
        try
        {
            responseRet = (String) phshum.processResponseMultipletabs(sconLabel) ;
            PhoneBook_DTO_HUM pbWebserviceRes = (PhoneBook_DTO_HUM) System.JSON.deserialize(responseRet, PhoneBook_DTO_HUM.class);
            List<PhoneBook_DTO_HUM.CallDataValuesList> arrayData = pbWebserviceRes.GetCallDataResponse.GetCallDataResult.CallDataValues.CallDataValuesList;
            setCallerType(arrayData);
            if (bSwitchSurveyTransfer !=null && bSwitchSurveyTransfer == true)
            { 
                    isSurveyButtonVisible =true;
            }
            List<SOFTPHONE_URL_PARAM__c> sup = SOFTPHONE_URL_PARAM__c.getall().values();
            for(SOFTPHONE_URL_PARAM__c supEach: sup){
                supMap.put((supEach.Name__c+supEach.Param_for__c).ToUpperCase(),supEach);
            }
            
            if(!String.isBlank(responseRet) && (responseRet.contains(PhoneBookConstants_H_HUM.ISMANUAL_TRANSFER) || responseRet.contains(PhoneBookConstants_H_HUM.NUMBER_OF_TRANSFERS)))
            {
                isTransferredCall = true;
                if(DataModal.get('uuidataValue')!='' && bSwitchSaveCallData_2230090)
                {
                    List < Interaction__c > checkMultipleIntLst = [SELECT Id,Survey_ID__c,Call_Category_Description__c,Caller_Name__c,Authenticated__c, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c, UUID__c, Genesys_Interaction_ID__c, CreatedDate,(SELECT Id, Interacting_About__c,Interacting_About__r.FirstName,Interacting_About__r.LastName,Interacting_About__r.BirthDate__c, Interacting_About_Type__c, Interaction__c FROM Interaction_Members__r order by CreatedDate desc) FROM Interaction__c where UUID__c =: uuidataValue AND createdById =: UserInfo.getUserId() order by Name DESC Limit 1];
                    if(checkMultipleIntLst.size()>0)
                    {
                        interactionIdVal = checkMultipleIntLst[0].id;
                        autoInteractionIDvalue = checkMultipleIntLst[0].id;
                        if(checkMultipleIntLst[0].Interaction_Members__r.size()==1)
                        {
                            aboutIdVal = checkMultipleIntLst[0].Interaction_Members__r[0].Interacting_About__c;
                        }
                        else if(checkMultipleIntLst[0].Interaction_Members__r.size()>1)
                        {
                            for(Interaction_Member__c eachMember:checkMultipleIntLst[0].Interaction_Members__r )
                            {
                                if(eachMember.Interacting_About__r.BirthDate__c == DataModal.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL)
                                    && eachMember.Interacting_About__r.LastName == DataModal.get(PhoneBookConstants_H_HUM.INQA_LAST)
                                    && eachMember.Interacting_About__r.FirstName == DataModal.get(PhoneBookConstants_H_HUM.INQA_FIRST)
                                    && eachMember.Interacting_About_Type__c.subString(0,1) == DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL))
                                { 
                                    aboutIdVal = eachMember.Interacting_About__c; 
                                }
                            } 
                        }
                    }
                }
            }
            else{
                if(PhoneBook_H_HUM.isCRMFunctionalityON('2230000'))
                {
                    List<Interaction__c> InteractionLst = [SELECT Id, Caller_Name__c FROM Interaction__c where UUID__c =: uuidataValue AND createdById =: UserInfo.getUserId() order by Name DESC Limit 1];
                    if(InteractionLst.size()>0)
                    {
                        autoInteractionIDvalue = InteractionLst[0].id;
                    }
                }
            }
            if(isRefreshedval){
                List<Interaction__c> lstInter =  [SELECT id FROM Interaction__c WHERE UUID__c =:uuidataValue Order By Name desc LIMIT 1];
                if(lstInter.size()>0)
                {
                    autoInteractionIDvalue = lstInter[0].id;
                }
            }
            if( arrayData.size() >0)
            {
                if(mflag == true)
                {
                    UpdateSearchPageURLForCallerTypeMember(supMap);
                }
                else if(aflag == true)
                {
                    UpdateSearchPageURLForCallerTypeAgent(supMap);
                }
                else if(pflag == true)
                {
                    UpdateSearchPageURLForCallerTypeProvider(supMap);
                }
                else if(gflag == true)
                {
                    UpdateSearchPageURLForCallerTypeGroup(supMap);
                }
                else {
                    searchPageURl='/apex/HUMSoftPhoneIntermediatePage?00NF000000CBqWV=&FirstName_SOFTPHONE=&Member_Id1_SOFTPHONE=&LastName_SOFTPHONE=&iAboutType=M&'+'autoInteractionIDvalue='+autoInteractionIDvalue;
                }
                
                searchPageURl = searchPageURl.removeEnd('&');
                if(!PhoneBook_H_HUM.isCRMFunctionalityON('1728596_IVRDataCapture'))
                {
                    searchPageURl = searchPageURl + '&callerType=Inbound%2BCall&uuID='+uuidataValue+'&authIndicator='+DataModal.get(PhoneBookConstants_H_HUM.Authentication_IND)+'&autoInteractionIDvalue='+autoInteractionIDvalue;
                }
                else
                {
                    searchPageURl = searchPageURl + '&callerType=Inbound%2BCall&uuID='+uuidataValue+'&interactionId='+idval+'&surveyId='+DataModal.get(PhoneBookConstants_H_HUM.SURVEY_ID)+'&callCategoryDesc='+DataModal.get(PhoneBookConstants_H_HUM.Call_Catg_Desc)+'&authIndicator='+DataModal.get(PhoneBookConstants_H_HUM.Authentication_IND)+'&autoInteractionIDvalue='+autoInteractionIDvalue;
                }
                searchPageURl= EncodingUtil.urlEncode(searchPageURl, 'UTF-8');
                searchPageURl = searchPageURl.replaceAll('\\+','%20');
                searchPageURl = searchPageURl.replaceAll('\\-','%2D');
            }
            else
            {
                if(PhoneBook_H_HUM.isCRMFunctionalityON('2191493')){
                    callerVal = CallerType = PhoneBookConstants_H_HUM.CALLER_UNKNOWN;
                }
                else{
                    callerVal = CallerType = PhoneBookConstants_H_HUM.SELECT_VAL;
                }
                searchPageURl='/apex/HUMSoftPhoneIntermediatePage?00NF000000CBqWV=&FirstName_SOFTPHONE=&Member_Id1_SOFTPHONE=&LastName_SOFTPHONE=&iAboutType=M&uuID='+uuidataValue+'&interactionId='+idval+'&surveyId=&callCategoryDesc='+'&autoInteractionIDvalue='+autoInteractionIDvalue;
            }
            createInboundCallLog(DataModal);
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'Phonebook_C_HUM', 'processResponseMultipletabs');
        }
        return null;
    }

    /*
    * getCallMemberAccountDetails
    * <p>
    * This method is used to get member Account details for InteractingWith and Interacting About for Interaction 
    * @returntype string
    * @param    accountId, memberGenKey, accountFirstName, accountLastName, IsAuthenticate
    */
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
    * setDateFormatForBirthDate
    * <p>
    * This method will format DOB setDateFormatForBirthDate
    * @returntype Boolean
    * @param  uuid
    */
    public static String setDateFormatForBirthDate(String memberdatevalue)
    {
        Date dt;
        String datevalue;
        DateTime datetimevalue; 
        try
        {    
            if(memberdatevalue != null){
                if(memberdatevalue.contains('/')){
                    dt = Date.parse(memberdatevalue);
                }    
                else{
                    dt = Date.valueof(memberdatevalue);
                }

            Integer d = dt.day();
            Integer mo = dt.month();
            Integer yr = dt.year();
            datetimevalue = DateTime.newInstance(yr, mo, d);
            datevalue = datetimevalue.format('MM') + '/' + datetimevalue.format('dd') + '/' + datetimevalue.format('YYYY');
            }
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_C_HUM', 'setDateFormatForBirthDate');
        }
        return datevalue;
    }

                    
    /*
    * cloneInteraction
    * <p>
    * US2760646- For cloning system interaction and transfer call interaction 
    * 1. If Inbound call is marked authenticated then both Interacting With and About are mandatory. If not, create unauth inetraction with provided data.
    * 2. For Authenticated Member call, if only “With” specified or "About Type" is specified as "M" then set the “About” to the same as the With 
    * 3. For Authenticated calls except Member call, if only “With” specified create an interaction and set just the Interacting With info. Set the Authenticated checkbox as false
    * @returntype string
    * @param  Interaction__c
    */
    public string  cloneInteraction(Interaction__c oInt)
    {
        String intId; 
        //string aboutIdVal ='';
        Interaction_Member__c incMember = new Interaction_Member__c();
        List<Interaction_Member__c> intMembersLst = new List<Interaction_Member__c>();
        Interaction__c clonedInteraction = new Interaction__c();
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
                        else if(!isTransferredCall && clonedInteraction.Authenticated__c == true && clonedInteraction.Interacting_with__C != null && clonedIntMember.interacting_about__C != null)
                        {
                            if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){clonedIntMember.Authenticated__C = true;}
                        }

                        intId = PhoneBook_D_HUM.insertInteraction(clonedInteraction);
                        interactionIdVal = intId;
                        autoInteractionIDvalue = intId;
                        
                        if(Schema.sObjectType.Interaction_Member__c.fields.Authenticated__C.isCreateable()){clonedIntMember.Interaction__c = clonedInteraction.id;}
                        intMembersLst.add(clonedIntMember);
                    }
                    if(eachMember.Interacting_About__r.BirthDate__c == DataModal.get(PhoneBookConstants_H_HUM.INQA_DOB_VAL)
                    && eachMember.Interacting_About__r.LastName == DataModal.get(PhoneBookConstants_H_HUM.INQA_LAST)
                    && eachMember.Interacting_About__r.FirstName == DataModal.get(PhoneBookConstants_H_HUM.INQA_FIRST)
                    && eachMember.Interacting_About_Type__c.subString(0,1) == DataModal.get(PhoneBookConstants_H_HUM.INQA_TYP_VAL))
                    { 
                        aboutIdVal = eachMember.Interacting_About__c; 
                    }
                    if(string.isEmpty(aboutIdVal) && string.isBlank(aboutIdVal))
                    {
                        aboutIdVal = eachMember.Interacting_About__c; 
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
                    incMember.Authenticated__C= true;
                    incMember.interacting_about__C = clonedInteraction.Interacting_with__C;
                    incMember.interacting_about_type__C = clonedInteraction.Interacting_With_Type__c;
                    aboutIdVal = incMember.interacting_about__C; 
                
                }
                else{
                    clonedInteraction.Authenticated__c =false;
                    incMember.Authenticated__C= false;
                }

                intId = PhoneBook_D_HUM.insertInteraction(clonedInteraction);
                incMember.Interaction__c = clonedInteraction.id;
                intMembersLst.add(incMember);
                PhoneBook_D_HUM.insertInteractionMember(intMembersLst);
            }
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'PhoneBook_C_HUM', 'cloneInteraction');
        }
        return intid;
    }
     /*
     * Method Name   :    getCallerTypeForPhoneBook
     * Description   :    //US#4040542 - Created for Caller Type Population with Phonebook.
     *                    This method fetches the matching caller Type from Custom Metadata and helps to setup correct Caller value on the phonebook
     *                    based on the interactingWithtype selected.
     * return Type   :    String
     * Parameters    :    String
     */
    @RemoteAction
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
     * Method Name   :    sortDefaultTransferList
     * Description   :    //US#5668340 - T1PRJ0036776: Alphabetize Phone Book.
     *                    This method is fto sort the callreasons entries in the phone book in CRM Lightning are arranged in alphabetical order.
     * return Type   :    List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>
     * Parameters    :    List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>
     */       
    public static List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> sortDefaultTransferList(List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> defaultTransferList){
        Map<string,RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> mapOfdefTra=new Map<string,RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        List<string> lstofcall=new List<string>();
        List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList> sortedTransferBook=new List<RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList>();
        for(RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList obj:defaultTransferList){
            mapOfdefTra.put(obj.CategoryCd,obj);
            lstofcall.add(obj.CategoryCd);
        }
        lstofcall.sort();
        for(string str:lstofcall){
            for(RouteSoftPhoneXMLResponse_DTO_HUM.CallTransferRecList mapof:mapOfdefTra.values()){
                if(mapOfdefTra.containsKey(str) && mapOfdefTra.get(str).CategoryCd==mapof.CategoryCd){
                    sortedTransferBook.add(mapof);
                }
            }
			}
        if(sortedTransferBook.isEmpty()==false){
            return sortedTransferBook;
        }else{
            return defaultTransferList;
        }    
    }
     /*
     * Method Name   :    sortDefaultTransferList
     * Description   :    //US#5668340 - T1PRJ0036776: Alphabetize Phone Book.
     *                    This method is fto sort the callreasons entries in the phone book in CRM Lightning are arranged in alphabetical order.
     * return Type   :    List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>
     * Parameters    :    List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>
     */       
    public static List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> sortTransferList(List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> defaultTransferList){
        Map<string,GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> mapOfdefTra=new Map<string,GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        List<string> lstofcall=new List<string>();
        List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList> sortedTransferBook=new List<GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList>();
        for(GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList obj:defaultTransferList){
            mapOfdefTra.put(obj.QueueName,obj);
            lstofcall.add(obj.QueueName);
        }
        lstofcall.sort();
        for(string str:lstofcall){
            for(GetAllPhoneBookRulesResponse_DTO_HUM.TransferNumberList mapof:mapOfdefTra.values()){
                if(mapOfdefTra.containsKey(str) && mapOfdefTra.get(str).QueueName==mapof.QueueName){
                    sortedTransferBook.add(mapof);
                }
            }
			}
        if(sortedTransferBook.isEmpty()==false){
            return sortedTransferBook;
        }else{
            return defaultTransferList;
        }    
    }

    
}