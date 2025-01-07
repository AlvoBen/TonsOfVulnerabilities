/*****************************************************************************************************************************************************************************
    @Apex Class Name  : DemographicUpdate_LS_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Class Name  : DemographicUpdate_LT_HUM
	@classic class name : DemographicUpdate_D_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar                                 		  06/09/2022                 Original Version
******************************************************************************************************************************************************************************/

public with sharing class DemographicUpdate_LS_HUM {
    public static string sClassName = 'DemographicUpdate_LS_HUM';
    public class ServiceDownException extends Exception {}
    
    /*
    * Method Name: startMemberRequest
    * Description: This AuraEnabled() continuation method will make and process MBEPlus call results
    * Parameter: Case Record Id
    * Return Type: Continuation object
    */
    @AuraEnabled(continuation=true)
    public static object startMemberRequest(string caseRecordId) {
        HttpRequest request = new HttpRequest();
        Continuation continuation = new Continuation(GLOBAL_CONSTANT_HUM.VOB_ServiceTimeOut_HUM);
        String personId = '';
        try {
            GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
            personId = genericHelper.fetchPersonId(caseRecordId);
           	continuation.ContinuationMethod = GLOBAL_CONSTANT_HUM.MM_DU_PROCESSRESPMETHOD_HUM;
            request = DemographicUpdate_S_HUM.getMemberMBERequest(personId);
            

            if(request != null) {
                continuation.state = continuation.addHttpRequest(request);
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'startMemberRequest');
        }
        return continuation;
    }

    /*
    * Method Name: processMemberResponse
    * Description: This static method will channelize the continuation state to response processor (test class purposes)
    * Parameter: NA
    * Return Type: Continuation object
    */
    public static Object processMemberResponse(){
        List<String> labels = new List<String>();
        Object result = DemographicUpdate_LS_HUM.processMemberResponse(labels);
        return result;
    }
    
    /*
    * Method Name: processMemberResponse
    * Description: This AuraEnabled() continuation method will make and process MBEPlus call results
    * Parameter: Case Record Id
    * Return Type: Continuation object
    */
    @AuraEnabled()
    public static Object processMemberResponse(List<String> labels) {

        DemographicUpdateMBEPlus_DTO_HUM dtoWrapperResponse = new DemographicUpdateMBEPlus_DTO_HUM();
        Map<String, DemographicUpdateMBEPlus_DTO_HUM> dtoWrapperResponseMap = 
                    new Map<String, DemographicUpdateMBEPlus_DTO_HUM>();
        try{
            HttpResponse response = Continuation.getResponse(labels[0]);
            if ((response != null && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE) && 
            (response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)))
            {   
                DemographicMBEPlusResponse_DTO_HUM dtoResponse = 
                    (DemographicMBEPlusResponse_DTO_HUM)System.JSON.deserialize(response.getBody(), 
                    DemographicMBEPlusResponse_DTO_HUM.class);

                dtoWrapperResponse = morphMemberResponse(dtoResponse);
                
                dtoWrapperResponseMap.put('responseMBE', dtoWrapperResponse);

                return System.JSON.serialize(dtoWrapperResponseMap);
            }
            else {
                FaultCode_DTO_HUM dtoFaultCode = FaultCode_DTO_HUM.parse(response.getBody());
                DemographicMBEPlusResponse_DTO_HUM dtoResponse = new DemographicMBEPlusResponse_DTO_HUM();
                dtoWrapperResponse.memberResponseDTO = generateBlankResponse();
                dtoWrapperResponse.memberCalloutErrored = true;
                dtoWrapperResponse.memberCalloutError = dtoFaultCode.Fault.detail.errorInfo.error_message;
                dtoWrapperResponse.faultResponseDTO = dtoFaultCode;
                dtoWrapperResponseMap.put('responseFault', dtoWrapperResponse);
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), sClassName, 'processMemberResponse');
                return System.JSON.serialize(dtoWrapperResponseMap);
            } 
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'processMemberResponse');
            dtoWrapperResponse.memberCalloutErrored = true;
            dtoWrapperResponse.memberCalloutError = ex.getMessage();
            return System.JSON.serialize(dtoWrapperResponseMap);
        }
    }

    /*
    * Method Name: morphMemberResponse
    * Description: map response to the response DTO
    * Parameter: HttpResponse
    * Return Type: DemographicUpdateMBEPlus_DTO_HUM
    */
    @testvisible
    private static DemographicUpdateMBEPlus_DTO_HUM morphMemberResponse(DemographicMBEPlusResponse_DTO_HUM dtoResponse) {
        
        DemographicUpdateMBEPlus_DTO_HUM wrapperResponse = new DemographicUpdateMBEPlus_DTO_HUM();

        DemographicMBEPlusResponse_DTO_HUM.MessageList dtoMsgList = (DemographicMBEPlusResponse_DTO_HUM.MessageList)dtoResponse.GetMemberResponse.Body.MessageList;

        DemographicMBEPlusResponse_DTO_HUM.Message dtoMessage = new DemographicMBEPlusResponse_DTO_HUM.Message();

        for(DemographicMBEPlusResponse_DTO_HUM.Message dtoMsg : dtoMsgList.Message) {
            dtoMessage.Type = dtoMsg.Type;
            dtoMessage.Code = dtoMsg.Code;
            dtoMessage.Description = dtoMsg.Description;
            dtoMessage.Source = dtoMsg.Source;
            break;
        }

        if(dtoMessage != null) {
            string responseMsgType = dtoMessage.Type;
            string responseMessage = '';
            boolean isError = false;
            if(responseMsgType.toLowerCase() == 'error') {
                responseMessage = dtoMessage.Source + ' Error: ' + dtoMessage.Code + ' ' + dtoMessage.Description;
                isError = true;
            }   
            else {
                responseMessage = dtoMessage.Source + ' Success: ' + dtoMessage.Code + ' ' + dtoMessage.Description;
                isError = false;
            }
                
            wrapperResponse.memberCalloutErrored = isError;
            wrapperResponse.memberCalloutError = responseMessage;
            wrapperResponse.memberResponseDTO = dtoResponse;
        }
        else {
            wrapperResponse.memberCalloutErrored = true;
            wrapperResponse.memberCalloutError = 'Unexpected error in MBE Response, contact your system Admin.';
            wrapperResponse.memberResponseDTO = dtoResponse;
        }
        
        return wrapperResponse;

    }

    /*
    * Method Name: generateBlankResponse
    * Description: this method generates blank response based on DTO for UI processing when faultresponse is received
    * Parameter: nothing
    * Return Type: 
    MBEPlusResponse_DTO_HUM
    */
    @testvisible
    private static DemographicMBEPlusResponse_DTO_HUM generateBlankResponse() {
        DemographicMBEPlusResponse_DTO_HUM dtoResponse = new DemographicMBEPlusResponse_DTO_HUM();
        DemographicMBEPlusResponse_DTO_HUM.GetMemberResponse dtoMemberResponse = new DemographicMBEPlusResponse_DTO_HUM.GetMemberResponse();
        DemographicMBEPlusResponse_DTO_HUM.Body dtoBody = new DemographicMBEPlusResponse_DTO_HUM.Body();
        DemographicMBEPlusResponse_DTO_HUM.Member dtoMember = new DemographicMBEPlusResponse_DTO_HUM.Member();
        DemographicMBEPlusResponse_DTO_HUM.MessageList dtoMsgList = new DemographicMBEPlusResponse_DTO_HUM.MessageList();
        DemographicMBEPlusResponse_DTO_HUM.Contact dtoContact = new DemographicMBEPlusResponse_DTO_HUM.Contact();
        DemographicMBEPlusResponse_DTO_HUM.ContactProfile dtoContactProfile = new DemographicMBEPlusResponse_DTO_HUM.ContactProfile();
        DemographicMBEPlusResponse_DTO_HUM.EmailList dtoEmailList = new DemographicMBEPlusResponse_DTO_HUM.EmailList();
        DemographicMBEPlusResponse_DTO_HUM.PhoneList dtoPhoneList = new DemographicMBEPlusResponse_DTO_HUM.PhoneList();
        DemographicMBEPlusResponse_DTO_HUM.AddressList dtoAddressList = new DemographicMBEPlusResponse_DTO_HUM.AddressList();
        DemographicMBEPlusResponse_DTO_HUM.PersonEmail dtoPersonEmail = new DemographicMBEPlusResponse_DTO_HUM.PersonEmail();
        DemographicMBEPlusResponse_DTO_HUM.PersonPhone dtoPersonPhone = new DemographicMBEPlusResponse_DTO_HUM.PersonPhone();
        DemographicMBEPlusResponse_DTO_HUM.PersonAddress dtoPersonAddress = new DemographicMBEPlusResponse_DTO_HUM.PersonAddress();
        DemographicMBEPlusResponse_DTO_HUM.Message dtoMessage = new DemographicMBEPlusResponse_DTO_HUM.Message();

        List<DemographicMBEPlusResponse_DTO_HUM.PersonEmail> dtoPersonEmailList = new List<DemographicMBEPlusResponse_DTO_HUM.PersonEmail>();
        dtoPersonEmail.Address = '';
        dtoPersonEmail.Type = '';
        dtoPersonEmailList.add(dtoPersonEmail);

        List<DemographicMBEPlusResponse_DTO_HUM.PersonPhone> dtoPersonPhoneList = new List<DemographicMBEPlusResponse_DTO_HUM.PersonPhone>();
        dtoPersonPhone.PhoneNumber = '';
        dtoPersonPhone.Type = '';
        dtoPersonPhoneList.add(dtoPersonPhone);

        List<DemographicMBEPlusResponse_DTO_HUM.PersonAddress> dtoPersonAddressList = new List<DemographicMBEPlusResponse_DTO_HUM.PersonAddress>();
        dtoPersonAddress.AddressLine1 = '';
        dtoPersonAddress.City = '';
        dtoPersonAddress.County = '';
        dtoPersonAddress.CountyCode = '';
        dtoPersonAddress.StateCode = '';
        dtoPersonAddress.Type = '';
        dtoPersonAddress.ZipCode = '';
        dtoPersonAddressList.add(dtoPersonAddress);

        dtoMember.FirstName = '';
        dtoMember.LastName = '';
        dtoMember.DateOfBirth = '';
        dtoMember.Gender = '';
        dtoMember.MiddleInitial = '';
        dtoMember.Ssn = '';
        dtoMember.MasterId = '';
        dtoPhoneList.PersonPhone = dtoPersonPhoneList;
        dtoAddressList.PersonAddress = dtoPersonAddressList;
        dtoEmailList.PersonEmail = dtoPersonEmailList;
        dtoContactProfile.PhoneList = dtoPhoneList;
        dtoContactProfile.EmailList = dtoEmailList;
        dtoContactProfile.AddressList = dtoAddressList;
        dtoContact.ContactProfile = dtoContactProfile;
        dtoMember.Contact = dtoContact;

        dtoBody.Member = dtoMember;
        dtoMemberResponse.Body = dtoBody;
        dtoResponse.GetMemberResponse = dtoMemberResponse;

        return dtoResponse;
    }
}