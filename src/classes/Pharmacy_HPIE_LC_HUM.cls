/*****************************************************************************************
Apex class Name    : Pharmacy_HPIE_LC_HUM
Version            : 1.0
Function           : controller for displaying PCP update template data.
Created Date       : 07/18/2023
Test Class         : Pharmacy_HPIE_LT_HUM
Modification Log: 
Developer Name           Code Review                       Date                       Description 
*--------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     						 07/18/2022                user story 4861950, 4861945
* Atul Patil                    						 07/28/2023                user story 4861950, 4861945
* Vishal Shinde                                          31/8/2023                 US-4908765-Mail Order Management - Pharmacy - OMS Originated Notes and profile fix
* Jagadeesh Kureti                                       06/SEP/2023               User Story 5012565: T1PRJ0870026 MF27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Details tab - Account details Edit fields
* Jonathan Dickinson			  					     09/04/2023				   User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
* Jonathan Dickinson			                         09/22/2023				   User Story 5061288: T1PRJ0870026   MF 27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy -  Details tab - Address section
* Swapnali Sonawane                                      10/23/2023                US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson                                     02/27/2024                User Story 5738539: T1PRJ1374973: DF 8518 - 8519 - 8520; C06 Case Management; Lightning - Case Comments - Error when adding comment before closing or transferring a case and notes reflected in incorrect section
* vishal Shinde                                          02/29/2024                US - 5142800- Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions – Create Order
* Vishal Shinde                                          02/29/2024                US - 5109892 HPIE Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - Finance Queue /Member consent
* Jonathan Dickinson                                     02/29/2024                User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
* Jonathan Dickinson                                     03/01/2024                User Story 5058187: T1PRJ1295995 - (T1PRJ0870026)- MF 27409 HPIE/CRM SF - Tech - C12 Mail Order Management - Pharmacy - "Prescriptions & Order summary" tab - "Edit Order' 
****************************************************************************************************************/
public with sharing class Pharmacy_HPIE_LC_HUM {

    @AuraEnabled
    public static object getFinanceDetails(string personId, string userId, string organization, string requesttime){
        try {
            ExternalService.HPIEPatient service = NEW ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.Getx20Finance_Request request =NEW ExternalService.HPIEPatient.Getx20Finance_Request();
            ExternalService.HPIEPatient_Getx20Finance_IN_generated finanaceGenerated = New  ExternalService.HPIEPatient_Getx20Finance_IN_generated();
            finanaceGenerated.organizationPatientId = personId;
            finanaceGenerated.organization = organization;
            finanaceGenerated.requestTime = requesttime;
            finanaceGenerated.userId  = userId;
            finanaceGenerated.sourceApplication  = Label.HPIE_Source;
            request.generated = finanaceGenerated;
            request.generated_set =true;
            ExternalService.HPIEPatient.Getx20Finance_Response response = service.Getx20Finance(request);
            ExternalService.HPIEPatient_Getx20Finance_OUT_201 financeResponse = response.Code201;
            return financeResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getFinanceDetails');
            throw new AuraHandledException(e.getMessage());
            
        }
    }


    @AuraEnabled
    public static object getDemographicsDetails(string personId, string userId, string organization, string requesttime){
        try {
            ExternalService.HPIEPatient service = NEW ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.Getx20Demographics_Request request =NEW ExternalService.HPIEPatient.Getx20Demographics_Request();
            ExternalService.HPIEPatient_Getx20Demographics_IN_generated demoGrGenerated =NEW ExternalService.HPIEPatient_Getx20Demographics_IN_generated();
            demoGrGenerated.organizationPatientId = personId;
            demoGrGenerated.organization = organization;
            demoGrGenerated.requestTime = requesttime;
            demoGrGenerated.userId = userId;
            demoGrGenerated.sourceApplication = Label.HPIE_Source;
            request.generated = demoGrGenerated;
            request.generated_set = true;
            ExternalService.HPIEPatient.Getx20Demographics_Response  response = service.Getx20Demographics(request);
            ExternalService.HPIEPatient_Getx20Demographics_OUT_201 demographics = response.Code201;            
            return demographics;
 
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getDemographicsDetails');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled 
    public static object getPrescriptions(string personId, string userId, string organization, string requestedTime, string startDate, string endDate){
        try {
            ExternalService.HPIEPrescription  service = NEW ExternalService.HPIEPrescription();
            ExternalService.HPIEPrescription.Getx20Prescriptions_Request   request = new ExternalService.HPIEPrescription.Getx20Prescriptions_Request  ();
            ExternalService.HPIEPrescription_Getx20Prescriptions_IN_generated presRequest = new ExternalService.HPIEPrescription_Getx20Prescriptions_IN_generated();
            presRequest.organizationPatientId = personId;
            presRequest.organization = organization;
            presRequest.requestTime = requestedTime;
            presRequest.userId = userId;
            presRequest.sourceApplication = Label.HPIE_Source;
            presRequest.startDate = startDate;
            presRequest.endDate = endDate;
            request.generated = presRequest;
            ExternalService.HPIEPrescription.Getx20Prescriptions_Response  response = service.Getx20Prescriptions(request);
            ExternalService.HPIEPrescription_Getx20Prescriptions_OUT_201   presciptions = response.Code201;
            return presciptions;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getPrescriptions');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled
    public static object getProfileDetails(string personId, string userId, string organization, string requestedTime){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.getprofiledetail_Request request =new ExternalService.HPIEPatient.getprofiledetail_Request();
            ExternalService.HPIEPatient_getprofiledetail_IN_generated  profileRequest =new ExternalService.HPIEPatient_getprofiledetail_IN_generated ();
            profileRequest.organizationPatientId = personId;
            profileRequest.organization = organization;
            profileRequest.requestTime = requestedTime;
            profileRequest.userId = userId;
            profileRequest.sourceApplication = Label.HPIE_Source;    
            request.generated = profileRequest;
            ExternalService.HPIEPatient.getprofiledetail_Response   response = service.getprofiledetail(request);
            ExternalService.HPIEPatient_getprofiledetail_OUT_201  profileResponse = response.Code201; 
            return profileResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getProfileDetails');
            throw new AuraHandledException(e.getMessage());
             
        }
    }

    @AuraEnabled(cacheable=true)
    public static object getHealthHistoryDetails(string personId, string userId, string organization, string requesttime){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.gethealthhistory_Request    request =new ExternalService.HPIEPatient.gethealthhistory_Request ();
            ExternalService.HPIEPatient_gethealthhistory_IN_generated   healthrequest =new ExternalService.HPIEPatient_gethealthhistory_IN_generated();
            healthrequest.organizationPatientId = personId;
            healthrequest.organization = organization;
            healthrequest.requestTime = requesttime;
            healthrequest.userId = userId;
            healthrequest.sourceApplication = Label.HPIE_Source;
            request.generated = healthrequest;
            ExternalService.HPIEPatient.gethealthhistory_Response    response = service.gethealthhistory(request);
            ExternalService.HPIEPatient_gethealthhistory_OUT_201   healthResponse = response.Code201; 
            return healthResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getHealthHistoryDetails');
            throw new AuraHandledException(e.getMessage());
            
        }
    }

    @AuraEnabled(cacheable=true)
    public static object getPlanDetails(string personId, string userId, string organization, string requestedTime){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.getPlans_Request request =new ExternalService.HPIEPatient.getPlans_Request();
            ExternalService.HPIEPatient_getPlans_IN_generated planRequest =new ExternalService.HPIEPatient_getPlans_IN_generated();
            planRequest.organizationPatientId = personId;
            planRequest.organization = organization;
            planRequest.requestTime = requestedTime;
            planRequest.userId = userId;
            planRequest.sourceApplication = Label.HPIE_Source;
            request.generated = planRequest;
            ExternalService.HPIEPatient.getPlans_Response response = service.getPlans(request);
            ExternalService.HPIEPatient_getPlans_OUT_201 profileResponse = response.Code201; 
            return profileResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getPlanDetails');
            throw new AuraHandledException(e.getMessage());
            
        }
    }

    @AuraEnabled
    public static object getPreferences(string personId, string userId, string organization, string requestedTime){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.GetPreference_Request request =new ExternalService.HPIEPatient.GetPreference_Request();
            ExternalService.HPIEPatient_GetPreference_IN_generated prefRequest =new ExternalService.HPIEPatient_GetPreference_IN_generated();
            prefRequest.organizationPatientId = personId;
            prefRequest.organization = organization;
            prefRequest.requestTime = requestedTime;
            prefRequest.userId = userId;
            prefRequest.sourceApplication = Label.HPIE_Source;
            request.generated = prefRequest;
            ExternalService.HPIEPatient.GetPreference_Response response = service.GetPreference(request);
            ExternalService.HPIEPatient_GetPreference_OUT_201 profileResponse = response.Code201;
            return profileResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getPreferences');
            throw new AuraHandledException(e.getMessage());
            
        }
    }

    @AuraEnabled(cacheable=true)
    public static object getOrderSummary(string personId, string userId, string organization, string requestedTime, string startDate, string endDate){
        	 try {            
            ExternalService.HPIEOrders  service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Getx20OrderSummary_Request   request = new ExternalService.HPIEOrders.Getx20OrderSummary_Request  ();
            ExternalService.HPIEOrders_Getx20ordersummary_IN_generated orderRequest = new ExternalService.HPIEOrders_Getx20ordersummary_IN_generated();
            orderRequest.organizationPatientId = personId;
            orderRequest.organization = organization;
            orderRequest.requestTime = requestedTime;
            orderRequest.userId = userId;
            orderRequest.sourceApplication = 'CRM';
            orderRequest.startDate = startDate;
            orderRequest.endDate = endDate;
            request.generated = orderRequest;
            ExternalService.HPIEOrders.Getx20ordersummary_Response  response = service.Getx20ordersummary(request);
            ExternalService.HPIEOrders_Getx20ordersummary_OUT_201   orders = response.Code201;
            return orders;    
         } catch (Exception e) {
             HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getOrderSummary');
             throw new AuraHandledException(e.getMessage());
            
         } 
    }

    @AuraEnabled(cacheable=true)
    public static object getOrderDetails(string personId, string userId, string organization, string requestedTime, string orderId){
        try {
            ExternalService.HPIEOrders  service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Getx20orderdetail_Request   request = new ExternalService.HPIEOrders.Getx20orderdetail_Request  ();
            ExternalService.HPIEOrders_Getx20orderdetail_IN_generated detailRequest = new ExternalService.HPIEOrders_Getx20orderdetail_IN_generated();

            detailRequest.organizationPatientId = personId;
            detailRequest.organization = organization;
            detailRequest.requestTime = requestedTime;
            detailRequest.userId = userId;
            detailRequest.sourceApplication = Label.HPIE_Source;
            detailRequest.orderId = orderId;
            request.generated = detailRequest;
            ExternalService.HPIEOrders.Getx20orderdetail_Response  response = service.Getx20orderdetail(request);
            ExternalService.HPIEOrders_Getx20orderdetail_OUT_201   details = response.Code201;
           return details;    
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getOrderDetails');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled
    public static object getEvents(string personId, string userId, string organization, string requestedTime, string orderId){
        try {
            ExternalService.HPIEOrders  service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Getx20Events_Request   request = new ExternalService.HPIEOrders.Getx20Events_Request  ();
            ExternalService.HPIEOrders_Getx20Events_IN_generated eventRequest = new ExternalService.HPIEOrders_Getx20Events_IN_generated();
            eventRequest.organizationPatientId = personId;
            eventRequest.organization = organization;
            eventRequest.requestTime = requestedTime;
            eventRequest.userId = userId;
            eventRequest.sourceApplication = Label.HPIE_Source;
            eventRequest.orderId = orderId;
            request.generated = eventRequest;
            ExternalService.HPIEOrders.Getx20Events_Response  response = service.Getx20Events(request);
            ExternalService.HPIEOrders_Getx20Events_OUT_201   events = response.Code201;
           return events;    
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getEvents');
            throw new AuraHandledException(e.getMessage());
            
        }
    }

    @AuraEnabled(cacheable=true)
    public static object getTasks(string personId, string userId, string organization, string requestedTime){
        try {
            ExternalService.HPIEOrders  service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Getx20Tasks_Request  request = new ExternalService.HPIEOrders.Getx20Tasks_Request  ();
            ExternalService.HPIEOrders_Getx20Tasks_IN_generated taskRequest = new ExternalService.HPIEOrders_Getx20Tasks_IN_generated();
            taskRequest.organizationPatientId = personId;
            taskRequest.organization = organization;
            taskRequest.requestTime = requestedTime;
            taskRequest.userId = userId;
            taskRequest.sourceApplication = Label.HPIE_Source;
            taskRequest.personCode = '';
            request.generated = taskRequest;
            ExternalService.HPIEOrders.Getx20Tasks_Response  response = service.Getx20Tasks(request);
            ExternalService.HPIEOrders_Getx20Tasks_OUT_201  tasks = response.Code201;
            return tasks;    
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getTasks');
            throw new AuraHandledException(e.getMessage());
           
        }
    }


 @AuraEnabled
    public static object getPatientLognotes(string personId, string userId, string organization, string startDate, string endDate){ 
        try {
            ExternalService.HPIENotes  service = NEW ExternalService.HPIENotes();
            ExternalService.HPIENotes.Getx20Patientx20Logx20notes_Request request = new ExternalService.HPIENotes.Getx20Patientx20Logx20notes_Request();
            ExternalService.HPIENotes_Getx20Patientx20Logx20notes_IN_generated generated= new ExternalService.HPIENotes_Getx20Patientx20Logx20notes_IN_generated();
            generated.organizationPatientId = personId;
            generated.organization = organization;
            generated.userId = userId; 
            generated.sourceApplication = Label.HPIE_SOURCE;
            generated.startDate = startDate;
            generated.endDate = endDate;
            generated.PersonId = '';
            request.generated =generated;
            ExternalService.HPIENotes.Getx20Patientx20Logx20notes_Response response = service.Getx20Patientx20Logx20notes(request);
            ExternalService.HPIENotes_Getx20Patientx20Logx20notes_OUT_201    PatientLogNote = response.Code201;
            return PatientLogNote;     
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getPatientLognotes');
            throw new AuraHandledException(e.getMessage());
              
        }
    }

    @AuraEnabled
	public static object addLogNote(string personId, string userId, string organization, string logNoteCode, string logNoteMsg){
		try {
			ExternalService.HPIENotes service = new ExternalService.HPIENotes();
			ExternalService.HPIENotes.Addx20Patientx20Logx20notes_Request request = new ExternalService.HPIENotes.Addx20Patientx20Logx20notes_Request();
			ExternalService.HPIENotes_Addx20Patientx20Logx20notes_IN_generated   noteRequest = new ExternalService.HPIENotes_Addx20Patientx20Logx20notes_IN_generated  ();
			noteRequest.UserId = userId;
			noteRequest.Organization = organization;
			noteRequest.OrganizationPatientId = personId;
			noteRequest.Personid = '';
			noteRequest.SourceApplication = System.Label.HPIE_Source;
			List<ExternalService.HPIENotes_Addx20Patientx20Logx20notes_IN_generated_Notes> lstNotes = new List<ExternalService.HPIENotes_Addx20Patientx20Logx20notes_IN_generated_Notes>();
			ExternalService.HPIENotes_Addx20Patientx20Logx20notes_IN_generated_Notes note = new ExternalService.HPIENotes_Addx20Patientx20Logx20notes_IN_generated_Notes();
			note.NoteCode = logNoteCode;
			note.LogNote = logNoteMsg;
			note.NotePriority = false;
			lstNotes.add(note);
			noteRequest.Notes = lstNotes;
			request.generated = noteRequest;
			ExternalService.HPIENotes.Addx20Patientx20Logx20notes_Response  response = service.Addx20Patientx20Logx20notes(request);
			ExternalService.HPIENotes_Addx20Patientx20Logx20notes_OUT_201 noteResponse = response.Code201;
            return noteResponse;
		} catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','addLogNote');
			throw new AuraHandledException(e.getMessage());
           
		}
	}
              
    @AuraEnabled
    public static object updateCapType(string personId, string userId, string requestedTime, string organization, string capType){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updatex20Preference_Request request = new ExternalService.HPIEPatient.updatex20Preference_Request();
            ExternalService.HPIEPatient_updatex20Preference_IN_generated updatePrefRequest = new ExternalService.HPIEPatient_updatex20Preference_IN_generated();
            updatePrefRequest.organizationPatientId = personId;
            updatePrefRequest.userId = userId;
            updatePrefRequest.requestTime = requestedTime;
            updatePrefRequest.organization= organization;
            updatePrefRequest.sourceApplication = Label.HPIE_Source;
            ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference prefRequest = new ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference();
            ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference_capType capRequest = new ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference_capType();
            capRequest.code = capType;
            prefRequest.capType = capRequest;
            updatePrefRequest.preference = prefRequest;
            request.generated = updatePrefRequest;
            ExternalService.HPIEPatient.updatex20Preference_Response response = service.updatex20Preference(request);
            ExternalService.HPIEPatient_updatex20Preference_OUT_201 updatedResponse = response.Code201;
            return updatedResponse;
        } catch (Exception e) {
             HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateCapType');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled
    public static object updatePrimaryPhone(string personId, string userId, string requestedTime, string organization, String primaryPhoneNumber){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updatePhone_Request request = new ExternalService.HPIEPatient.updatePhone_Request();
            ExternalService.HPIEPatient_updatePhone_IN_generated updatePhoneRequest = new ExternalService.HPIEPatient_updatePhone_IN_generated();
            List<ExternalService.HPIEPatient_updatePhone_IN_generated_phones> phonesList = new List<ExternalService.HPIEPatient_updatePhone_IN_generated_phones>();
            
            ExternalService.HPIEPatient_updatePhone_IN_generated_phones_z0type phoneType = new ExternalService.HPIEPatient_updatePhone_IN_generated_phones_z0type();
            phoneType.code  = '1';
            phoneType.description = 'Primary Phone';
                
            ExternalService.HPIEPatient_updatePhone_IN_generated_phones phone = new ExternalService.HPIEPatient_updatePhone_IN_generated_phones();
            phone.z0type = phoneType;
            phone.primaryPhone = true;
            phone.z0number  = primaryPhoneNumber;
            phonesList.add(phone);
            

            updatePhoneRequest.phones = phonesList;
            updatePhoneRequest.organizationPatientId = personId;
            updatePhoneRequest.userId = userId;
            updatePhoneRequest.requestTime = requestedTime;
            updatePhoneRequest.organization = organization;
            updatePhoneRequest.sourceApplication = Label.HPIE_Source;
            request.generated = updatePhoneRequest;
            ExternalService.HPIEPatient.updatePhone_Response response = service.updatePhone(request);
            return response;
        } catch (Exception e) {
             HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updatePrimaryPhone');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled
    public static object updateAlternatePhone(string personId, string userId, string requestedTime, string organization, string alternatePhoneNumber){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updatePhone_Request request = new ExternalService.HPIEPatient.updatePhone_Request();
            ExternalService.HPIEPatient_updatePhone_IN_generated updatePhoneRequest = new ExternalService.HPIEPatient_updatePhone_IN_generated();
            List<ExternalService.HPIEPatient_updatePhone_IN_generated_phones> phonesList = new List<ExternalService.HPIEPatient_updatePhone_IN_generated_phones>();
                        
            ExternalService.HPIEPatient_updatePhone_IN_generated_phones_z0type phoneType = new ExternalService.HPIEPatient_updatePhone_IN_generated_phones_z0type();
            phoneType.code  = '2';
            phoneType.description = 'Alternate Phone';

            ExternalService.HPIEPatient_updatePhone_IN_generated_phones phone = new ExternalService.HPIEPatient_updatePhone_IN_generated_phones();
            phone.z0type = phoneType;
            phone.primaryPhone = false;
            phone.z0number = alternatePhoneNumber;
            phonesList.add(phone);            

            updatePhoneRequest.phones = phonesList;
            updatePhoneRequest.organizationPatientId = personId;
            updatePhoneRequest.userId = userId;
            updatePhoneRequest.requestTime = requestedTime;
            updatePhoneRequest.organization = organization;
            updatePhoneRequest.sourceApplication = Label.HPIE_Source;
            request.generated = updatePhoneRequest;
            ExternalService.HPIEPatient.updatePhone_Response response = service.updatePhone(request);
            return response;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateAlternatePhone');
            throw new AuraHandledException(e.getMessage());
            
        }
    }

    @AuraEnabled
    public static object updateEmail(string personId, string userId, string requestedTime, string organization, String emailId){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updatex20Profile_Request request = new ExternalService.HPIEPatient.updatex20Profile_Request();
            ExternalService.HPIEPatient_updatex20Profile_IN_generated updateProfileRequest = new ExternalService.HPIEPatient_updatex20Profile_IN_generated();            
            updateProfileRequest.organizationPatientId = personId;
            updateProfileRequest.userId = userId;
            updateProfileRequest.requestTime = requestedTime;
            updateProfileRequest.organization = organization;
            updateProfileRequest.sourceApplication = Label.HPIE_Source;
            ExternalService.HPIEPatient_updatex20Profile_IN_generated_profile profileRequest = new ExternalService.HPIEPatient_updatex20Profile_IN_generated_profile();
            profileRequest.emailAddress = emailId;
            updateProfileRequest.profile  = profileRequest;
            request.generated = updateProfileRequest;
            ExternalService.HPIEPatient.updatex20Profile_Response response = service.updatex20Profile(request);
            ExternalService.HPIEPatient_updatex20Profile_OUT_201 updatedResponse = response.Code201;
            return updatedResponse;
        } catch (Exception e) {
             HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateEmail');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled
    public static object getFamilyLognotes(string personId, string userId, string organization, string startDate, string endDate, string accData){ 
        try {
            ExternalService.HPIENotes service = new ExternalService.HPIENotes();
            ExternalService.HPIENotes.Getx20Familyx20Logx20notes_Request request = new ExternalService.HPIENotes.Getx20Familyx20Logx20notes_Request();
            ExternalService.HPIENotes_Getx20Familyx20Logx20notes_IN_generated  generated= new ExternalService.HPIENotes_Getx20Familyx20Logx20notes_IN_generated ();
            generated.organizationPatientId = personId;
            generated.organization = organization;
            generated.userId = userId;
            generated.sourceApplication ='CRM';
            generated.startDate = startDate;
            generated.endDate = endDate;
            generated.AccountId = accData;
            generated.PersonId = '';
            request.generated =generated;
            ExternalService.HPIENotes.Getx20Familyx20Logx20notes_Response  response = service.Getx20Familyx20Logx20notes(request);
            ExternalService.HPIENotes_Getx20Familyx20Logx20notes_OUT_201     PatientLogNote = response.Code201;
            return PatientLogNote;
        }catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','getFamilyLognotes');
            throw new AuraHandledException(e.getMessage());
             
		}
	}

    public class FamilyNote {
        public String noteCode {get; set;}
        public String logNote {get; set;}
    }

    @AuraEnabled
	public static object addFamilyLogNote(String jsonrequest){
		try {
            Map<String,Object> mapRequest = (Map<String,Object>) JSON.deserializeUntyped(jsonrequest);
			ExternalService.HPIENotes service = new ExternalService.HPIENotes();
			ExternalService.HPIENotes.addFamilyLogNotes_Request request = new ExternalService.HPIENotes.addFamilyLogNotes_Request();
			ExternalService.HPIENotes_addFamilyLogNotes_IN_generated noteRequest = new ExternalService.HPIENotes_addFamilyLogNotes_IN_generated();
			noteRequest.UserId = (String) mapRequest.get('userId');
			noteRequest.OrganizationPatientId = (String) mapRequest.get('organizationPatientId');
            noteRequest.Organization = (String) mapRequest.get('organization');
            noteRequest.AccountId = (String) mapRequest.get('accountId');
			noteRequest.SourceApplication = System.Label.HPIE_Source;

			List<ExternalService.HPIENotes_addFamilyLogNotes_IN_generated_Notes> lstNotes = new List<ExternalService.HPIENotes_addFamilyLogNotes_IN_generated_Notes>();
            List<FamilyNote> notes = (List<FamilyNote>) JSON.deserialize(JSON.serialize(mapRequest.get('notes')), List<FamilyNote>.class);

            for(FamilyNote note: notes) {
                ExternalService.HPIENotes_addFamilyLogNotes_IN_generated_Notes newNote = new ExternalService.HPIENotes_addFamilyLogNotes_IN_generated_Notes();
                newNote.NoteCode = note.noteCode;
                newNote.LogNote = note.logNote;
                lstNotes.add(newNote);
            }

			noteRequest.Notes = lstNotes;
			request.generated = noteRequest;
			ExternalService.HPIENotes.addFamilyLogNotes_Response  response = service.addFamilyLogNotes(request);
			ExternalService.HPIENotes_addFamilyLogNotes_OUT_201 noteResponse = response.Code201;
            return noteResponse;
		} catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','addFamilyLogNote');
			throw new AuraHandledException(e.getMessage());
		}
	}

   

    public class AddFinanceRequest {
        @AuraEnabled public String userId {get; set;}
        @AuraEnabled public String requestTime {get; set;}
        @AuraEnabled public String organization {get; set;}
        @AuraEnabled public String organizationPatientId {get; set;}
        @AuraEnabled public Integer cardKey {get; set;}
        @AuraEnabled public String cardTypeCode {get; set;}
        @AuraEnabled public String cardTypeDesc {get; set;}
        @AuraEnabled public Integer expMonth {get; set;}
        @AuraEnabled public Integer expYear {get; set;}
        @AuraEnabled public String firstName {get; set;}
        @AuraEnabled public String middleName {get; set;}
        @AuraEnabled public String lastName {get; set;}
        @AuraEnabled public Boolean active {get; set;}
        @AuraEnabled public Boolean autoCharge {get; set;}
        @AuraEnabled public Boolean spendingAccount {get; set;}
        @AuraEnabled public String tokenKey {get; set;}
    }

    @AuraEnabled(cacheable=true)
    public static object addFinance(AddFinanceRequest parameters) {
        try {
            ExternalService.HPIEPatient service = NEW ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.Addx20finance_Request request = NEW ExternalService.HPIEPatient.Addx20finance_Request();
            ExternalService.HPIEPatient_Addx20finance_IN_generated addFinanceGenerated = New ExternalService.HPIEPatient_Addx20finance_IN_generated();

            addFinanceGenerated.organizationPatientId = parameters.organizationPatientId;
            addFinanceGenerated.organization = parameters.organization;
            addFinanceGenerated.requestTime = parameters.requestTime;
            addFinanceGenerated.userId = parameters.userId;
            addFinanceGenerated.sourceApplication = Label.HPIE_Source;

            ExternalService.HPIEPatient_Addx20finance_IN_generated_finance financeProp = New ExternalService.HPIEPatient_Addx20finance_IN_generated_finance();
            ExternalService.HPIEPatient_Addx20finance_IN_generated_finance_paymentCards paymentCardProp = New ExternalService.HPIEPatient_Addx20finance_IN_generated_finance_paymentCards();
            ExternalService.HPIEPatient_Addx20finance_IN_generated_finance_paymentCards_z0type cardTypeProp = New ExternalService.HPIEPatient_Addx20finance_IN_generated_finance_paymentCards_z0type();
            paymentCardProp.key = parameters.cardKey;
            cardTypeProp.code = parameters.cardTypeCode;
            cardTypeProp.description = parameters.cardTypeDesc;
            paymentCardProp.z0type = cardTypeProp;
            paymentCardProp.expirationMonth = parameters.expMonth;
            paymentCardProp.expirationYear = parameters.expYear;
            paymentCardProp.firstName = parameters.firstName;
            paymentCardProp.middleName = parameters.middleName;
            paymentCardProp.lastName = parameters.lastName;
            paymentCardProp.active = parameters.active;
            paymentCardProp.autoCharge = parameters.autoCharge;
            paymentCardProp.spendingAccount = parameters.spendingAccount;
            paymentCardProp.tokenKey = parameters.tokenKey;
            List<ExternalService.HPIEPatient_Addx20finance_IN_generated_finance_paymentCards> paymentCardList = New List<ExternalService.HPIEPatient_Addx20finance_IN_generated_finance_paymentCards>();
            paymentCardList.add(paymentCardProp);
            financeProp.paymentCards = paymentCardList;
            addFinanceGenerated.finance = financeProp;
            request.generated = addFinanceGenerated;
            ExternalService.HPIEPatient.Addx20finance_Response response = service.Addx20finance(request);
            ExternalService.HPIEPatient_Addx20finance_OUT_201 addFinanceResponse = response.Code201;
            return addFinanceResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','addFinance');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    
    public class UpdateFinanceRequest {
        @AuraEnabled public String userId {get; set;}
        @AuraEnabled public String requestTime {get; set;}
        @AuraEnabled public String organization {get; set;}
        @AuraEnabled public String organizationPatientId {get; set;}
        @AuraEnabled public Integer cardKey {get; set;}
        @AuraEnabled public String cardTypeCode {get; set;}
        @AuraEnabled public String cardTypeDesc {get; set;}
        @AuraEnabled public Integer expMonth {get; set;}
        @AuraEnabled public Integer expYear {get; set;}
        @AuraEnabled public String firstName {get; set;}
        @AuraEnabled public String middleName {get; set;}
        @AuraEnabled public String lastName{get; set;}
        @AuraEnabled public Boolean active {get; set;}
        @AuraEnabled public Boolean autoCharge {get; set;}
        @AuraEnabled public Boolean spendingAccount {get; set;}
    }

    @AuraEnabled(cacheable=true)
    public static object updateFinance(UpdateFinanceRequest parameters) {
        try {
            ExternalService.HPIEPatient service = NEW ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updatex20finance_Request request = NEW ExternalService.HPIEPatient.updatex20finance_Request();
            ExternalService.HPIEPatient_updatex20finance_IN_generated updateFinanceGenerated = New ExternalService.HPIEPatient_updatex20finance_IN_generated();

            updateFinanceGenerated.organizationPatientId = parameters.organizationPatientId;
            updateFinanceGenerated.organization = parameters.organization;
            updateFinanceGenerated.requestTime = parameters.requestTime;
            updateFinanceGenerated.userId = parameters.userId;
            updateFinanceGenerated.sourceApplication = Label.HPIE_Source;
            
            ExternalService.HPIEPatient_updatex20finance_IN_generated_finance financeProp = New ExternalService.HPIEPatient_updatex20finance_IN_generated_finance();
            ExternalService.HPIEPatient_updatex20finance_IN_generated_finance_paymentCards paymentCardProp = New ExternalService.HPIEPatient_updatex20finance_IN_generated_finance_paymentCards();
            ExternalService.HPIEPatient_updatex20finance_IN_generated_finance_paymentCards_z0type cardTypeProp = New ExternalService.HPIEPatient_updatex20finance_IN_generated_finance_paymentCards_z0type();
            paymentCardProp.key = parameters.cardKey;
            cardTypeProp.code = parameters.cardTypeCode;
            cardTypeProp.description = parameters.cardTypeDesc;
            paymentCardProp.z0type = cardTypeProp;
            paymentCardProp.expirationMonth = parameters.expMonth;
            paymentCardProp.expirationYear = parameters.expYear;
            paymentCardProp.firstName = parameters.firstName;
            paymentCardProp.middleName = parameters.middleName;
            paymentCardProp.lastName = parameters.lastName;
            paymentCardProp.active = parameters.active;
            paymentCardProp.autoCharge = parameters.autoCharge;
            paymentCardProp.spendingAccount = parameters.spendingAccount;
            List<ExternalService.HPIEPatient_updatex20finance_IN_generated_finance_paymentCards> paymentCardList = New List<ExternalService.HPIEPatient_updatex20finance_IN_generated_finance_paymentCards>();
            paymentCardList.add(paymentCardProp);
            financeProp.paymentCards = paymentCardList;
            updateFinanceGenerated.finance = financeProp;
            request.generated = updateFinanceGenerated;
            ExternalService.HPIEPatient.updatex20finance_Response response = service.updatex20finance(request);
            ExternalService.HPIEPatient_updatex20finance_OUT_201 updateFinanceResponse = response.Code201;
            return updateFinanceResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateFinance');
            throw new AuraHandledException(e.getMessage());
             
        }
    }

    public class AddPaymentRequest {
        @AuraEnabled public String organizationPatientId {get; set;}
        @AuraEnabled public String userId {get; set;}
        @AuraEnabled public String organization {get; set;}
        @AuraEnabled public String requestTime {get; set;}
        @AuraEnabled public Integer key {get; set;}
        @AuraEnabled public Double amount {get; set;}
        @AuraEnabled public String z0date {get; set;}
        @AuraEnabled public Boolean authorized {get; set;}
    }

    @AuraEnabled(cacheable=true)
    public static object addOneTimePayment(AddPaymentRequest parameters) {
        try {
            ExternalService.HPIEPatient service = NEW ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.addx20onex20timex20payment_Request request = NEW ExternalService.HPIEPatient.addx20onex20timex20payment_Request();
            ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated addPaymentGenerated = New ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated();
            
            addPaymentGenerated.organizationPatientId = parameters.organizationPatientId;
            addPaymentGenerated.organization = parameters.organization;
            addPaymentGenerated.requestTime = parameters.requestTime;
            addPaymentGenerated.userId = parameters.userId;
            addPaymentGenerated.sourceApplication = Label.HPIE_Source;

            ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance financeProp = New ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance();
            
            ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments paymentsProp = New ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments();
            List<ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments> paymentsList = New List<ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments>();
            
            ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments_cards cardsProp = New ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments_cards();
            List<ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments_cards> cardsList = New List<ExternalService.HPIEPatient_addx20onex20timex20payment_IN_generated_finance_payments_cards>();

            cardsProp.key = parameters.key;
            cardsProp.amount = parameters.amount;
            cardsProp.z0date = parameters.z0date;
            cardsProp.authorized = parameters.authorized;

            cardsList.add(cardsProp);
            paymentsProp.cards = cardsList;
            paymentsList.add(paymentsProp);
            financeProp.payments = paymentsList;
            addPaymentGenerated.finance = financeProp;
            request.generated = addPaymentGenerated;

            ExternalService.HPIEPatient.addx20onex20timex20payment_Response response = service.addx20onex20timex20payment(request);
            ExternalService.HPIEPatient_addx20onex20timex20payment_OUT_201 addPaymentResponse = response.Code201;
            
            return addPaymentResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','addOneTimePayment');
            throw new AuraHandledException(e.getMessage());
           
        }
    }

    @AuraEnabled
        public static Object updatePrescription(String key, string personId, string userId, string organization,
        string requestedTime, Boolean autoRefill, Boolean archived){
            try {
                ExternalService.HPIEPrescription  service = new ExternalService.HPIEPrescription();
                ExternalService.HPIEPrescription.Updatex20Prescriptions_Request  request = new ExternalService.HPIEPrescription.Updatex20Prescriptions_Request();
                ExternalService.HPIEPrescription_Updatex20Prescriptions_IN_generated  updatepresRequest = 
                new ExternalService.HPIEPrescription_Updatex20Prescriptions_IN_generated();
                updatepresRequest.organizationPatientId = personId;
                updatepresRequest.organization = organization;
                updatepresRequest.requestTime = requestedTime;
                updatepresRequest.userId = userId;
                updatepresRequest.sourceApplication = Label.HPIE_Source;
                updatepresRequest.personCode = '';
                updatepresRequest.memberId='';
                List<ExternalService.HPIEPrescription_Updatex20Prescriptions_IN_generated_prescriptions> lstPrescriptions = new List<ExternalService.HPIEPrescription_Updatex20Prescriptions_IN_generated_prescriptions>();
                ExternalService.HPIEPrescription_Updatex20Prescriptions_IN_generated_prescriptions prescription = new ExternalService.HPIEPrescription_Updatex20Prescriptions_IN_generated_prescriptions();
                prescription.key = key;
                prescription.autoRefill = autoRefill;
                prescription.archived = archived;
                prescription.pharmacyNPI='';
                lstPrescriptions.add(prescription);
                updatepresRequest.prescriptions = lstPrescriptions;
                request.generated = updatepresRequest;
                ExternalService.HPIEPrescription.Updatex20Prescriptions_Response response = service.Updatex20Prescriptions(request);
                ExternalService.HPIEPrescription_Updatex20Prescriptions_OUT_201 updatedResponse = response.Code201;
                return updatedResponse;
            } catch (Exception e) {
                HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updatePrescription');
                throw new AuraHandledException(e.getMessage());
            }
        }
	


    @AuraEnabled
    public static Object addNewAddress(String jsonrequest){
        try {
                Map<String,Object> mapRequest = (Map<String,Object>)JSON.deserializeUntyped(jsonrequest);
                ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
                ExternalService.HPIEPatient.addAddresses_Request request = new ExternalService.HPIEPatient.addAddresses_Request();
                ExternalService.HPIEPatient_addAddresses_IN_generated addAddressGenerated = new ExternalService.HPIEPatient_addAddresses_IN_generated();
                addAddressGenerated.organizationPatientId = (string)mapRequest.get('organizationPatientId');
                addAddressGenerated.organization = (string)mapRequest.get('organization');
                addAddressGenerated.requestTime = (string)mapRequest.get('requestTime');
                addAddressGenerated.userId = (string)mapRequest.get('userId');
                addAddressGenerated.sourceApplication = Label.HPIE_Source;                
                ExternalService.HPIEPatient_addAddresses_IN_generated_addresses addressesProp = new ExternalService.HPIEPatient_addAddresses_IN_generated_addresses();
                List<ExternalService.HPIEPatient_addAddresses_IN_generated_addresses> addressesList = new List<ExternalService.HPIEPatient_addAddresses_IN_generated_addresses>();                
                ExternalService.HPIEPatient_addAddresses_IN_generated_addresses_z0type addressTypeProp = new ExternalService.HPIEPatient_addAddresses_IN_generated_addresses_z0type();
                addressTypeProp.code = (string)mapRequest.get('code');
                addressTypeProp.description = (string)mapRequest.get('description');
                addressesProp.z0type = addressTypeProp;    
                addressesProp.addressLine1 = (string)mapRequest.get('addressLine1');
                addressesProp.addressLine2 = (string)mapRequest.get('addressLine2');
                addressesProp.city = (string)mapRequest.get('city');
                addressesProp.stateCode = (string)mapRequest.get('stateCode');
                addressesProp.zipCode = (string)mapRequest.get('zipCode');
                addressesProp.overrideReason = (string)mapRequest.get('overrideReason');
                addressesProp.overrideReasonCode = (Double)mapRequest.get('overrideReasonCode');
                addressesProp.uspsValidated = (Boolean)mapRequest.get('uspsValidated');
                addressesProp.active = (Boolean)mapRequest.get('active');
                addressesList.add(addressesProp);                
                addAddressGenerated.addresses = addressesList;
                request.generated = addAddressGenerated;
                ExternalService.HPIEPatient.addAddresses_Response response = service.addAddresses(request);
                ExternalService.HPIEPatient_addAddresses_OUT_201 addAddressResponse = response.Code201;                
                return addAddressResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','addNewAddress');
            throw new AuraHandledException(e.getMessage());
             
        }
    }

    public class AddressClass {
        public Integer key {get; set;} 
        public String type {get; set;}
        public String code {get; set;}
        public String description {get; set;}
        public String addressLine1 {get; set;}
        public String addressLine2 {get; set;}
        public String city {get; set;}
        public String stateCode {get; set;}
        public String zipCode {get; set;}
        public String overrideReason {get; set;}
        public Double overrideReasonCode {get; set;}
        public Boolean uspsValidated {get; set;}
        public Boolean active {get; set;}
    }
    
    @AuraEnabled
    public static object updateAddress(String jsonrequest) {
        try {
            Map<String,Object> mapRequest = (Map<String,Object>)JSON.deserializeUntyped(jsonrequest);
            ExternalService.HPIEPatient service = NEW ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updateAddresses_Request request = NEW ExternalService.HPIEPatient.updateAddresses_Request();
            ExternalService.HPIEPatient_updateAddresses_IN_generated updateAddressGenerated = New ExternalService.HPIEPatient_updateAddresses_IN_generated();
            
            updateAddressGenerated.organizationPatientId = (string)mapRequest.get('organizationPatientId');
            updateAddressGenerated.organization = (String) mapRequest.get('organization');
            updateAddressGenerated.requestTime = (String) mapRequest.get('requestTime');
            updateAddressGenerated.userId = (String) mapRequest.get('userId');
            updateAddressGenerated.sourceApplication = Label.HPIE_Source;

            List<ExternalService.HPIEPatient_updateAddresses_IN_generated_addresses> addressesList = New List<ExternalService.HPIEPatient_updateAddresses_IN_generated_addresses>();
            List<AddressClass> addresses = (List<AddressClass>) JSON.deserialize(JSON.serialize(mapRequest.get('addresses')), List<AddressClass>.class);
            for (AddressClass address: addresses) {
                ExternalService.HPIEPatient_updateAddresses_IN_generated_addresses addressesProp = New ExternalService.HPIEPatient_updateAddresses_IN_generated_addresses();
                ExternalService.HPIEPatient_updateAddresses_IN_generated_addresses_z0type addressTypeProp = New ExternalService.HPIEPatient_updateAddresses_IN_generated_addresses_z0type();
                addressesProp.key = address.key;
                addressTypeProp.code = address.code;
                addressTypeProp.description = address.description;
                addressesProp.z0type = addressTypeProp;
                addressesProp.addressLine1 = address.addressLine1;
                addressesProp.addressLine2 = address.addressLine2;
                addressesProp.city = address.city;
                addressesProp.stateCode = address.stateCode;
                addressesProp.zipCode = address.zipCode;
                addressesProp.overrideReason = address.overrideReason;
                addressesProp.overrideReasonCode = address.overrideReasonCode;
                addressesProp.uspsValidated = address.uspsValidated;
                addressesProp.active = address.active;

                addressesList.add(addressesProp);
            }
            
            updateAddressGenerated.addresses = addressesList;
            request.generated = updateAddressGenerated;
            ExternalService.HPIEPatient.updateAddresses_Response response = service.updateAddresses(request);
            ExternalService.HPIEPatient_updateAddresses_OUT_201 updateAddressResponse = response.Code201;
            
            return updateAddressResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateAddress');
            throw new AuraHandledException(e.getMessage());
             
        }
    }


    @AuraEnabled
    public static Object updateMemberConsent(string personId, string userId, string requestedTime,  string organization,
    string consentBeginDate,Boolean memberConsent ){
        try {
            ExternalService.HPIEPatient service = new ExternalService.HPIEPatient();
            ExternalService.HPIEPatient.updatex20Preference_Request request = new ExternalService.HPIEPatient.updatex20Preference_Request();
            ExternalService.HPIEPatient_updatex20Preference_IN_generated updatePrefRequest = new ExternalService.HPIEPatient_updatex20Preference_IN_generated();
            updatePrefRequest.organizationPatientId = personId;
            updatePrefRequest.userId = userId;
            updatePrefRequest.requestTime = requestedTime;
            updatePrefRequest.organization = organization;
            updatePrefRequest.sourceApplication = System.Label.HPIE_Source;
            ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference prefRequest = new ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference();
            ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference_consents  consentRequest = new ExternalService.HPIEPatient_updatex20Preference_IN_generated_preference_consents();
            consentRequest.memberConsentRequired  = memberConsent;
            if(memberConsent){
                consentRequest.consentBeginDate = consentBeginDate;
            }        
            prefRequest.consents  = consentRequest;
            updatePrefRequest.preference = prefRequest;
            request.generated = updatePrefRequest;
            ExternalService.HPIEPatient.updatex20Preference_Response response = service.updatex20Preference(request);
            ExternalService.HPIEPatient_updatex20Preference_OUT_201 updatedResponse = response.Code201;
            return updatedResponse;
        } catch (Exception e) {
                 HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateMemberConsent');
				throw new AuraHandledException(e.getMessage());
               
        }
    }

    public class UpdateOrderLineClass {
        public UpdateOrderCopayClass copay {get; set;} 
        public Boolean memberConsent {get; set;}
        public UpdateOrderProductClass product {get; set;}
    }

    public class UpdateOrderCopayClass {
        public Boolean consent {get; set;} 
    }

    public class UpdateOrderProductClass {
        public UpdateOrderProductPrescriptionClass prescription {get; set;} 
    }

    public class UpdateOrderProductPrescriptionClass {
        public String prescriptionKey {get; set;} 
    }

    @AuraEnabled
    public static object updateOrder(String jsonrequest) {
        try {
            Map<String,Object> mapRequest = (Map<String,Object>) JSON.deserializeUntyped(jsonrequest);

            ExternalService.HPIEOrders service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Updatex20orderdetail_Request request = NEW ExternalService.HPIEOrders.Updatex20orderdetail_Request();
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated updateOrderGenerated = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated();

            updateOrderGenerated.organizationPatientId = (String) mapRequest.get('organizationPatientId');
            updateOrderGenerated.organization = (String) mapRequest.get('organization');
            updateOrderGenerated.requestTime = (String) mapRequest.get('requestTime');
            updateOrderGenerated.userId = (String) mapRequest.get('userId');
            updateOrderGenerated.sourceApplication = Label.HPIE_Source;

            Map<String,Object> mapOrder = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapRequest.get('order')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order order = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order();
            order.orderId = (String) mapOrder.get('orderId');
            order.cancelOrder =  (Boolean) mapOrder.get('cancelOrder');

        if(!order.cancelOrder){ 
            Map<String,Object> mapBilling = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapOrder.get('billing')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing billing = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing();
            Map<String,Object> mapBillingAddress = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBilling.get('address')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_address billingAddress = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_address();
            billingAddress.key = (Integer) mapBillingAddress.get('key');
            billing.address = billingAddress;
            Map<String,Object> mapBillingPayment = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBilling.get('payment')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment billingPayment = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment();
            Map<String,Object> mapBillingPaymentMethod = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBillingPayment.get('method')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment_method billingPaymentMethod = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment_method();
            billingPaymentMethod.code = (Double) mapBillingPaymentMethod.get('code');
            billingPaymentMethod.description = (String) mapBillingPaymentMethod.get('description');
            Map<String,Object> mapBillingPaymentCard = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBillingPayment.get('card')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment_card billingPaymentCard = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment_card();
            billingPaymentCard.key = (Integer) mapBillingPaymentCard.get('key');
            billingPayment.method = billingPaymentMethod;
            billingPayment.z0date = (String) mapBillingPayment.get('date');
            billingPayment.card = billingPaymentCard;
            billing.payment = billingPayment;

            Map<String,Object> mapShipping = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapOrder.get('shipping')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_shipping shipping = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_shipping();
            Map<String,Object> mapShippingMethod = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapShipping.get('method')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_shipping_method shippingMethod = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_shipping_method();
            shippingMethod.code = (Double) mapShippingMethod.get('code');
            Map<String,Object> mapShippingAddress = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapShipping.get('address')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_shipping_address shippingAddress = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_shipping_address();
            shippingAddress.key = (Integer) mapShippingAddress.get('key');
            shipping.method = shippingMethod;
            shipping.priority = (Boolean) mapShipping.get('priority');
            shipping.address = shippingAddress;

            List<ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines> lineList = New List<ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines>();
            List<UpdateOrderLineClass> lines = (List<UpdateOrderLineClass>) JSON.deserialize(JSON.serialize(mapOrder.get('lines')), List<UpdateOrderLineClass>.class);

            for (UpdateOrderLineClass line: lines) {
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines newLine = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines();
                if (line.copay != null){
                    ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_copay lineCopay = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_copay();
                    lineCopay.consent = line.copay.consent;
                    newLine.copay = lineCopay;
                }
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product lineProduct = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product();
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product_prescription lineProductPrescription = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product_prescription();
                lineProductPrescription.prescriptionKey = line.product.prescription.prescriptionKey;
                lineProduct.prescription = lineProductPrescription;
                if(line.memberConsent != null) {newLine.memberConsent = line.memberConsent;}
                newLine.product = lineProduct;

                lineList.add(newLine);
            }

            order.billing = billing;
            order.shipping = shipping;
            order.lines = lineList;
        }
            updateOrderGenerated.order = order;
            request.generated = updateOrderGenerated;
            ExternalService.HPIEOrders.Updatex20orderdetail_Response response = service.Updatex20orderdetail(request);
            ExternalService.HPIEOrders_Updatex20orderdetail_OUT_201 updateOrderResponse = response.Code201;

            return updateOrderResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','updateOrder');
            throw new AuraHandledException(e.getMessage());
               
        }
    }

    public class UpdateReleaseOrderLineClass {
        public UpdateReleaseOrderCopayClass copay {get; set;}
        public UpdateReleaseOrderProductClass product {get; set;}
    }

    public class UpdateReleaseOrderCopayClass {
        public Boolean consent {get; set;} 
    }

    public class UpdateReleaseOrderProductClass {
        public UpdateReleaseOrderProductPrescriptionClass prescription {get; set;} 
    }

    public class UpdateReleaseOrderProductPrescriptionClass { 
        public String prescriptionKey {get; set;} 
    }

    @AuraEnabled
    public static object releaseOrder(String jsonrequest,Boolean releaseOrdercheck) {

        try {
            Map<String,Object> mapRequest = (Map<String,Object>) JSON.deserializeUntyped(jsonrequest);

            ExternalService.HPIEOrders service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Updatex20orderdetail_Request request = NEW ExternalService.HPIEOrders.Updatex20orderdetail_Request();
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated updateOrderGenerated = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated();

            updateOrderGenerated.organizationPatientId =(String) mapRequest.get('organizationPatientId');
            updateOrderGenerated.organization = (String) mapRequest.get('organization');
            updateOrderGenerated.requestTime = (String) mapRequest.get('requestTime');
            updateOrderGenerated.userId = (String) mapRequest.get('userId');
            updateOrderGenerated.sourceApplication = Label.HPIE_Source;
            
            Map<String,Object> mapOrder = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapRequest.get('order')));
            ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order order = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order();
            order.orderId = (String) mapOrder.get('orderId');

            List<ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines> lineList = New List<ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines>();
            List<UpdateReleaseOrderLineClass> lines = (List<UpdateReleaseOrderLineClass>) JSON.deserialize(JSON.serialize(mapOrder.get('lines')), List<UpdateReleaseOrderLineClass>.class);

            for (UpdateReleaseOrderLineClass line: lines) {
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines newLine = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines();
                if (line.copay != null){
                    ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_copay lineCopay = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_copay();
                    lineCopay.consent = line.copay.consent;
                    newLine.copay = lineCopay;
                }
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product lineProduct = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product();
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product_prescription lineProductPrescription = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_lines_product_prescription();
                lineProductPrescription.prescriptionKey = line.product.prescription.prescriptionKey;
                lineProduct.prescription = lineProductPrescription;
                newLine.product = lineProduct;

                lineList.add(newLine);
            }
           if(releaseOrdercheck){  
                Map<String,Object> mapBilling = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapOrder.get('billing')));
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing billing = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing();
                Map<String,Object> mapBillingPayment = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBilling.get('payment')));
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment billingPayment = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment();
                Map<String,Object> mapBillingPaymentCard = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBillingPayment.get('card')));
                ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment_card billingPaymentCard = New ExternalService.HPIEOrders_Updatex20orderdetail_IN_generated_order_billing_payment_card();
                billingPaymentCard.key = (Integer) mapBillingPaymentCard.get('key');
                billingPayment.card = billingPaymentCard;
                billing.payment = billingPayment;
                order.billing = billing;
            } 
                order.lines = lineList;

            updateOrderGenerated.order = order;
            request.generated = updateOrderGenerated;
            ExternalService.HPIEOrders.Updatex20orderdetail_Response response = service.Updatex20orderdetail(request);
            ExternalService.HPIEOrders_Updatex20orderdetail_OUT_201 updateOrderResponse = response.Code201;

            return updateOrderResponse;
        } catch (Exception e) {
             HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','releaseOrder');
            throw new AuraHandledException(e.getMessage());
              
        }
    }

    public class AddOrderLineClass {
        public AddOrderProductClass product {get; set;}
    }

    public class AddOrderProductClass {
        public AddOrderProductPrescriptionClass prescription {get; set;} 
    }

    public class AddOrderProductPrescriptionClass {
        public String prescriptionKey {get; set;} 
        public Double  fillNumber {get; set;} 
    }

    @AuraEnabled 
    public static object createOrder(String jsonrequest) {     

        try { 
            Map<String,Object> mapRequest = (Map<String,Object>)JSON.deserializeUntyped(jsonrequest);
            ExternalService.HPIEOrders service = NEW ExternalService.HPIEOrders();
            ExternalService.HPIEOrders.Addx20orderdetail_Request request = NEW ExternalService.HPIEOrders.Addx20orderdetail_Request();
            ExternalService.HPIEOrders_Addx20orderdetail_IN_generated addOrderGenerated = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated();

            addOrderGenerated.organizationPatientId = (String) mapRequest.get('organizationPatientId');
            addOrderGenerated.userId = (String) mapRequest.get('userId');
            addOrderGenerated.requestTime = (String) mapRequest.get('requestTime');
            addOrderGenerated.organization = (String) mapRequest.get('organization');
            addOrderGenerated.sourceApplication = Label.HPIE_Source; 
           
             Map<String,Object> mapOrder = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapRequest.get('order')));
             ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order order = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order();

                Map<String,Object> mapType = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapOrder.get('type')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_z0type  type = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_z0type();
                type.code = (Double) mapType.get('code');
                order.z0type =type;

                Map<String,Object> mapBilling = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapOrder.get('billing')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing billing = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing();
                Map<String,Object> mapBillingAddress = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBilling.get('address')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_address billingAddress = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_address();
                billingAddress.key = (Integer) mapBillingAddress.get('key');
                billing.address = billingAddress;

                Map<String,Object> mapBillingPayment = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBilling.get('payment')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_payment billingPayment = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_payment();
                Map<String,Object> mapBillingPaymentMethod = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBillingPayment.get('method')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_payment_method billingPaymentMethod = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_payment_method();
                billingPaymentMethod.code = (Double) mapBillingPaymentMethod.get('code');
                billingPaymentMethod.description = (String) mapBillingPaymentMethod.get('description');
                Map<String,Object> mapBillingPaymentCard = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapBillingPayment.get('card')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_payment_card billingPaymentCard = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_billing_payment_card();
                billingPaymentCard.key = (Double) mapBillingPaymentCard.get('key');
                billingPayment.method = billingPaymentMethod;
                billingPayment.card = billingPaymentCard;
                billing.payment = billingPayment;

                Map<String,Object> mapShipping = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapOrder.get('shipping')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_shipping shipping = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_shipping();
                Map<String,Object> mapShippingMethod = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapShipping.get('method')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_shipping_method shippingMethod = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_shipping_method();
                shippingMethod.code = (Double) mapShippingMethod.get('code');
                Map<String,Object> mapShippingAddress = (Map<String,Object>) JSON.deserializeUntyped(JSON.serialize(mapShipping.get('address')));
                ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_shipping_address shippingAddress = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_shipping_address();
                shippingAddress.key = (Double) mapShippingAddress.get('key');
                shipping.method = shippingMethod;
                shipping.priority = (Boolean) mapShipping.get('priority');
                shipping.address = shippingAddress;
                
                List<ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines> lineList = New List<ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines>();
                List<AddOrderLineClass> lines = (List<AddOrderLineClass>) JSON.deserialize(JSON.serialize(mapOrder.get('lines')), List<AddOrderLineClass>.class);
               
                for (AddOrderLineClass line: lines) {
                    ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines newLine = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines();
                    ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines_product lineProduct = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines_product();
                  
                    ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines_product_identifiers_id lineProductIdentifierId = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines_product_identifiers_id();
                    ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines_product_prescription lineProductPrescription = New ExternalService.HPIEOrders_Addx20orderdetail_IN_generated_order_lines_product_prescription();
                   
                    lineProductPrescription.prescriptionKey = line.product.prescription.prescriptionKey;
                    lineProductPrescription.fillNumber = line.product.prescription.fillNumber;
                    lineProduct.prescription = lineProductPrescription;

                    newLine.product = lineProduct;
                    lineList.add(newLine);
                }

                order.lines = lineList;
                order.billing = billing;
                order.shipping = shipping;
    
            addOrderGenerated.order=order;
            request.generated=addOrderGenerated;
            ExternalService.HPIEOrders.Addx20orderdetail_Response response=service.Addx20orderdetail(request);
            ExternalService.HPIEOrders_Addx20orderdetail_OUT_201 addorderResponse=response.Code201;

            return addorderResponse;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','createOrder');
            throw new AuraHandledException(e.getMessage()); 
               
        }
    }

     public class AddCreateFillClass {
            public String  prescriptionKey {get; set;}
        }

        @AuraEnabled
        public static object createFill(String jsonrequest) {
              try{
                Map<String,Object> mapRequest = (Map<String,Object>) JSON.deserializeUntyped(jsonrequest);
                ExternalService.HPIEPrescription  service = new ExternalService.HPIEPrescription();
                ExternalService.HPIEPrescription.CreateFills_Request request = new ExternalService.HPIEPrescription.CreateFills_Request();
                ExternalService.HPIEPrescription_CreateFillsRequest  addFillsGenerated = new ExternalService.HPIEPrescription_CreateFillsRequest();
                addFillsGenerated.organizationPatientId = (String) mapRequest.get('organizationPatientId');
                addFillsGenerated.userId = (String) mapRequest.get('userId');
                addFillsGenerated.requestTime = (String)mapRequest.get('requestTime');
                addFillsGenerated.organization = (String) mapRequest.get('organization');
                addFillsGenerated.sourceApplication = Label.HPIE_Source; 

                List<ExternalService.HPIEPrescription_CreateFillsRequest_Fills> createFillsList = New List<ExternalService.HPIEPrescription_CreateFillsRequest_Fills>();
                List<AddCreateFillClass> createFills = (List<AddCreateFillClass>) JSON.deserialize(JSON.serialize(mapRequest.get('Fills')), List<AddCreateFillClass>.class);

                for (AddCreateFillClass createFill: createFills) {
                    ExternalService.HPIEPrescription_CreateFillsRequest_Fills newCreateFill = New ExternalService.HPIEPrescription_CreateFillsRequest_Fills();
                    newCreateFill.prescriptionKey = createFill.prescriptionKey;

                    createFillsList.add(newCreateFill);
                }

                addFillsGenerated.Fills = createFillsList;
                request.generated = addFillsGenerated;
                ExternalService.HPIEPrescription.CreateFills_Response response = service.CreateFills(request);
                ExternalService.HPIEPrescription_CreateFillsResponse  addCreateFillsResponse = response.Code201;
                
                return addCreateFillsResponse;
              }
              catch(Exception e){
                   HUMExceptionHelper.logErrors(e,'Pharmacy_HPIE_LC_HUM','createFill'); 
                throw new AuraHandledException(e.getMessage());
                 
              } 
        }
}