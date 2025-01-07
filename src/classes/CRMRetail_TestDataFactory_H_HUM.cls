/*****************************************************************************************************************************************************************************
Apex Class Name : CRMRetail_TestDataFactory_H_HUM
Version         : 1.0
Created On      : 6/29/2019
Function        : Keeps all test data creation for Storefront in a single spot and call from test classes.
Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Melkisan Selvaraj                                               6/29/2020               Adding Rating to Lead Records.
*Abhishek Maurya								    			 12/22/2020				 User-Story 1755491- Substitute Member Id to Visitor Id on Interaction
*Abhishek Maurya								                 02/17/2021				 Production fix - Duplicate check
*Abhishek Maurya								 				 04/16/2021			 	 User-Story 1712045- CRM Storefront Notifications: Consolidated Prompt
* Vinoth L                                                       05/14/2021              User Story 2071202 -CRM Storefront Notifications: Date Suppression
* Vinoth L                                                       05/14/2021              User Story 2068463 -CRM Storefront Notifications: Determine Last Notification Date
* Vinoth L                                                       04/29/2021              USer Story 2119738 -CRM Storefront Notifications: Aging In Eligibilty Action Required Prompt 
* Ekta Bist                                                      06/07/2021              User Story 2322470 -CRM Storefront Virtual Neighborhood Center - New Visitor 
* Vinoth L                                     					 07/21/2021              User Story - 2492158 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Track All Screenings
* Abhishek Maurya                              					 07/21/2021              User Story - 2491779 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: One-on-One Tracking Enhancements
* Vinoth L                                                       09/05/2021              09/24 Post Evaluation -Enhancement of Code Coverage
* Vivek Sharma                                                   05/30/2022              User Story 3248244: T1PRJ0154546 / SF / MF9 Storefront - Display Visitor ID
* Vivek Sharma                                                   06/17/2022              User Story 3316820: T1PRJ0154546 / SF / MF9 Storefront - SNP: Change Notifications Section to Custom
* Vinoth L                                              		 02/01/2023              02/24 Test class Fix
* Vinoth L                      				                 02/28/2023              User Story 4349337: T1PRJ0154546 / SF / MF9 Storefront: Deprecate Old Home Page Components
* Vinoth L                                         	             04/26/2023              User Story 4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic   
* Vinoth L                      				  			 	 08/26/2023           	 User Story 5012060: T1PRJ0154546 / DP / MF9 Storefront - Add preferred name on Account
**************************************************************************************************************************************************/
@isTest
public class CRMRetail_TestDataFactory_H_HUM 
{
    /*
    * Method Name   :   createAttendance
    * Description   :   Creates instance of Attendance object and returns it
    * Return Type   :   Attendance__c
    * Parameters    :   Id, Date
    */
    public static Attendance__c createAttendance(Id LocId, Date intDate) 
    {
        Attendance__c att = new Attendance__c();
        try
        { 
            att.Date__c = intDate;
            att.Current_Attendance__c = 1;
            att.First_Time_Visitors_Today__c = 0;
            att.Location__c = LocId;
            att.Maximum_Concurrent_Visitors__c = 1;
            att.Visitors_Today__c = 1;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createAttendance');
        }
        return att; 
    }
    
    /*
    * Method Name   :   createHUMWebServiceCstmStng
    * Description   :   Creates instance of HUM_Webservice_Callout__c object and returns it
    * Return Type   :   HUM_Webservice_Callout__c
    * Parameters    :   String, String, String, Boolean, String, String
    */
    public static HUM_Webservice_Callout__c createHUMWebServiceCstmStng(String cstmStngName,String requestType,String certificateName, Boolean isActive,String endPoint,String timeOut) 
    {
        HUM_Webservice_Callout__c crmRetailHUMCstmStng = new HUM_Webservice_Callout__c();
        try
        { 
        	crmRetailHUMCstmStng.Name = cstmStngName;    
            crmRetailHUMCstmStng.Request_Type__c = requestType;   
            crmRetailHUMCstmStng.Certificate_Name__c = certificateName;
            crmRetailHUMCstmStng.Active__c = isActive;
            crmRetailHUMCstmStng.End_Point_URL__c = endPoint;
            crmRetailHUMCstmStng.Timeout__c = timeOut;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createHUMWebServiceCstmStng');
        }
        return crmRetailHUMCstmStng; 
    }

    
    /*
    * Method Name   :   createLocation
    * Description   :   Creates an instance of Storefront_Location__c object and returns it
    * Return Type   :   Storefront_Location__c
    * Parameters    :   NA
    */
    public static Storefront_Location__c createLocation() 
    {
        Storefront_Location__c Loc = new Storefront_Location__c();
        try
        { 
            Loc.Name = 'Virginia';
            Loc.Address_Line_1__c = '9005';
            Loc.City__c='Virginia';
            Loc.Zip_Code__c='40222';
            Loc.Org_Facility__c='12445';
            Loc.State__c='Virginia - VA';
            Loc.Location_Phone__c='9898989898';
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createLocation');
        }
        return Loc; 
    }
    /*
    * Method Name   :   createVirtualLocation
    * Description   :   Creates an instance of Storefront_Location__c object and returns it
    * Return Type   :   Storefront_Location__c
    * Parameters    :   NA
    */
    public static Storefront_Location__c createVirtualLocation() 
    {
        Storefront_Location__c SfLoc = new Storefront_Location__c();
        try
        { 
            SfLoc.Name = 'Tulsa';
            SfLoc.Address_Line_1__c = '9003';
            SfLoc.City__c='Tulsa';
            SfLoc.Zip_Code__c='40223';
            SfLoc.Org_Facility__c='12423';
            SfLoc.State__c='Tulsa - TA';
            SfLoc.Location_Phone__c='9898871623';
            SfLoc.Location_Type__c = 'Virtual';
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createVirtualLocation');
        }
        return SfLoc; 
    }
    
    /*
    * Method Name   :   createAccount
    * Description   :   Creates an instance of Account object and returns it
    * Return Type   :   Account
    * Parameters    :   NA
    */
    public static Account createAccount(String groupNum,Id recordTypeId,Date perBirthDate,string birthDate, String FirstName,String lastName,
                       String middleName,String entpriseId,boolean homeOfficeAcc,String perMailing,String postalCode,String homePhone,String barcode,Date waiverDate,String veteranStatus,String notificatonOptOut) 
    {
        Account oAcc = new Account();        
        try
        {               
            oAcc.Group_Number__c = groupNum;
            oAcc.recordtypeid = recordTypeId;
            oAcc.PersonBirthdate = perBirthDate;
            oAcc.Birthdate__c = birthDate;
            oAcc.FirstName = FirstName;
            oAcc.LastName = lastName;
	        oAcc.MiddleName = middleName;
            oAcc.Enterprise_ID__c=entpriseId;
            oAcc.Home_Office_Account__c = homeOfficeAcc;
            oAcc.PersonMailingState = perMailing;
            oAcc.PersonMailingPostalCode = postalCode;
            oAcc.PersonHomePhone = homePhone;
            oAcc.GCM_Visitor_Barcode__c = barcode;
            oAcc.Waiver_Date__c = waiverDate;
            oAcc.Veteran_Status__c=veteranStatus;
            oAcc.CRMRetailNotificationOptOut__c=notificatonOptOut;
            insert oAcc;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createAccount');
        }
        return oAcc; 
    }
    /*
    * Method Name   :   createAccountInstance
    * Description   :   Creates an instance of Account object and returns it
    * Return Type   :   Account
    * Parameters    :   NA
    */
    public static Account createAccountInstance(String groupNum,Id recordTypeId,Date perBirthDate,string birthDate, String FirstName,String lastName,
                       String middleName,String entpriseId,boolean homeOfficeAcc,String perMailing,String postalCode,String homePhone,String barcode,Date waiverDate,String veteranStatus,String notificatonOptOut) 
    {
        Account oAcc = new Account();         
        oAcc.Group_Number__c = groupNum;
        oAcc.recordtypeid = recordTypeId;
        oAcc.PersonBirthdate = perBirthDate;
        oAcc.Birthdate__c = birthDate;
        oAcc.FirstName = FirstName;
        oAcc.LastName = lastName;
        oAcc.MiddleName = middleName;
        oAcc.Enterprise_ID__c=entpriseId;
        oAcc.Home_Office_Account__c = homeOfficeAcc;
        oAcc.PersonMailingState = perMailing;
        oAcc.PersonMailingPostalCode = postalCode;
        oAcc.PersonHomePhone = homePhone;
        oAcc.GCM_Visitor_Barcode__c = barcode;
        oAcc.Waiver_Date__c = waiverDate;
        oAcc.Veteran_Status__c=veteranStatus;
        oAcc.CRMRetailNotificationOptOut__c=notificatonOptOut;
        oAcc.CRMRetail_PreferredName__c = oAcc.FirstName.substring(0,1)+oAcc.LastName.substring(0,1);
        return oAcc; 
    }
    /*
    * Method Name   :   createPolicyInstance
    * Description   :   Returns an instance of Policy__c object
    * Return Type   :   Policy__c
    * Parameters    :   NA
    */
    public static Policy__c createPolicyInstance(Id accId,String GroupNumber,Id prodId) 
    {
        Policy__c oPolicy1 = new Policy__c();        
        try
        {                         
            oPolicy1.Group_Name__c = accId;
            oPolicy1.Policy_Group_Number__c=GroupNumber;
            oPolicy1.Coverage_Plan_Effective_Date__c = '03/04/2020';
            oPolicy1.Coverage_Plan_end_date__c = '03/04/2025'; 
            oPolicy1.Product__C=prodId;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createPolicyInstance');
        }
        return oPolicy1; 
    }
    /*
    * Method Name   :   createPolicy
    * Description   :   Creates an instance of Policy__c object and returns it
    * Return Type   :   Policy__c
    * Parameters    :   NA
    */
    public static Policy__c createPolicy(Id accId,String GroupNumber) 
    {
        Policy__c oPolicy1 = new Policy__c();        
        try
        {                         
            oPolicy1.Group_Name__c = accId;
            oPolicy1.Policy_Group_Number__c=GroupNumber;
            oPolicy1.Coverage_Plan_Effective_Date__c = '03/04/2020';
            oPolicy1.Coverage_Plan_end_date__c = '03/04/2025'; 
            insert oPolicy1;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createPolicy');
        }
        return oPolicy1; 
    }
    
    /*
    * Method Name   :   createPolicyMember
    * Description   :   Creates an instance of Policy_Member__c object and returns it
    * Return Type   :   Policy_Member__c
    * Parameters    :   NA
    */
    public static Policy_Member__c createPolicyMember(ID accID, Id policId,String Name,String effDate, String endDate,boolean etlRecDel) 
    {
        Policy_Member__c oPolicyMember = new Policy_Member__c();                
        try
        {             
            oPolicyMember.Member__c = accID;
            oPolicyMember.Policy__c = policId;
            oPolicyMember.Name = Name;
            oPolicyMember.Effective_Date__c = effDate;
            oPolicyMember.End_Date__c =endDate;
            oPolicyMember.ETL_Record_Deleted__c = etlRecDel;
            insert oPolicyMember;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createPolicyMember');
        }
        return oPolicyMember; 
    }
    /*
    * Method Name   :   createPolicyMemberInstance
    * Description   :   Creates an instance of Policy_Member__c object and returns it
    * Return Type   :   Policy_Member__c
    * Parameters    :   NA
	*/
    public static Policy_Member__c createPolicyMemberInstance(ID accID, Id policId,String Name,String effDate, String endDate,boolean etlRecDel) 
    {
        Policy_Member__c oPolicyMember1 = new Policy_Member__c();      
        oPolicyMember1.Member__c = accID;
        oPolicyMember1.Policy__c = policId;
        oPolicyMember1.Name = Name;
        oPolicyMember1.Effective_Date__c = effDate;
        oPolicyMember1.End_Date__c =endDate;
        oPolicyMember1.ETL_Record_Deleted__c = etlRecDel;
        return oPolicyMember1; 
    }
      /*
    * Method Name   :   createMemberId
    * Description   :   Creates an instance of Member_ID__c object and returns it
    * Return Type   :   Member_ID__c
    * Parameters    :   NA
    */
    public static Member_ID__c createMemberId(ID policyMemID, String Name,String memberDependent, Id policyId,String extId,String memberIdValue) 
    {
      Member_ID__c oMember1 = new Member_ID__c();               
        try
        {             
           oMember1.Policy_Member__c = policyMemID;
            oMember1.Name = name;
            oMember1.Member_Dependent_Code__c =memberDependent;
            oMember1.Policy__c = policyId;
            oMember1.Member_Card_External_ID__c = extId;
            oMember1.Member_Id_Value__c = memberIdValue;
            insert oMember1;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createMemberId');
        }
        return oMember1; 
    }
    
    /*
    * Method Name   :   createMemberIdInstance
    * Description   :   Creates an instance of Member_ID__c object and returns it without commit
    * Return Type   :   Member_ID__c
    * Parameters    :   NA
    */
    public static Member_ID__c createMemberIdInstance(ID policyMemID, String Name,String memberDependent, Id policyId,String extId,String memberIdValue) 
    {
      Member_ID__c oMember = new Member_ID__c();               
        try
        {             
            oMember.Policy_Member__c = policyMemID;
            oMember.Name = name;
            oMember.Member_Dependent_Code__c =memberDependent;
            oMember.Policy__c = policyId;
            oMember.Member_Card_External_ID__c = extId;
            oMember.Member_Id_Value__c = memberIdValue;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createMemberIdInstance');
        }
        return oMember; 
    }
    
    /*
    * Method Name   :   createEvent
    * Description   :   Creates an instance of Event object and returns it
    * Return Type   :   Event
    */
    public static Event createEvent(Id reason, Id locationId ,DateTime startDateTime ,DateTime endDateTime,Date activityDate,boolean isRecurrence, String showAs,integer onSiteAttenCount){
        Event eventRef=new Event();
        eventRef.Reason__c=reason;
        eventRef.WhatId=locationId;
        eventRef.startDateTime=startDateTime; 
        eventRef.endDateTime=endDateTime;
        eventRef.activityDate=activityDate;
        eventRef.isRecurrence=isRecurrence;
        eventRef.showAs=showAs;
        eventRef.Scheduled_Onsite_Attendee_Count__c=onSiteAttenCount;        
        return eventRef;        
    }     
    public static Storefront_Location__C createLocation(String name,String Address,String City,String zipCode,String orgFacility,String State,integer maximumOccupancy){
        Storefront_Location__C locationRef= new Storefront_Location__C();
        locationRef.Name = name;
        locationRef.Address_Line_1__c = Address;
        locationRef.City__c = City;
        locationRef.Zip_Code__c = zipCode;
        locationRef.Org_Facility__c = orgFacility;
        locationRef.State__c = State;
        locationRef.Maximum_Occupancy__c = maximumOccupancy;
        return locationRef; 
    }
    public static Storefront_I_Type__c createInteractionType(String Name){
        Storefront_I_Type__c interactionTypeRef = new Storefront_I_Type__c();
        interactionTypeRef.Name = Name;
        return interactionTypeRef;
    }
    public static Storefront_I_Reason__c createInteractionReason(String Name,Id interactionType){
        Storefront_I_Reason__c interactionReasonRef = new Storefront_I_Reason__c();
        interactionReasonRef.Name = Name;
        interactionReasonRef.Interaction_Type__c = interactionType;
        return interactionReasonRef;
    }
    public static Storefront_Interaction__c createInteraction(Id accountId,Id locationId,Id reasonId, Boolean isAllDayEvent, String interactionVisitorType,Date interactionDate){
    	Storefront_Interaction__c interactionRef = new Storefront_Interaction__c();
        interactionRef.Account__c = accountId;
        interactionRef.Location__c = locationId;
        interactionRef.Reason__c = reasonId;
        interactionRef.isAllDayEvent__c = isAllDayEvent;
        interactionRef.CRM_Retail_Interaction_Visitor_Type__c = interactionVisitorType;
        interactionRef.Interaction_Date__c = interactionDate;
        return interactionRef;
    }
    public static Storefront_Notification__C createNotification(String notificationType,Id Location,String visitorType,String Instructions,integer suppressionDays,Boolean Inactive,Date resetDate,Decimal lowAge,Decimal highAge){
    	Storefront_Notification__C notificationRef = new Storefront_Notification__C();
        notificationRef.Notification_Type__c = notificationType;
        notificationRef.CRMRetail_Location__c = Location;
        notificationRef.Visitor_Type__c = visitorType;
        notificationRef.Instructions__c = Instructions;
        notificationRef.Suppression_Days__c = suppressionDays;
        notificationRef.Inactive__c = Inactive;
        notificationRef.Reset_Date__c = resetDate;
        notificationRef.Low_Age_in_Years__c = lowAge;
        notificationRef.High_Age_In_Years__c = highAge;
        return notificationRef;
    }
    /*
    * Method Name   :   createObjAccount
    * Description   :   Creates an instance of Account object and returns it
    * Return Type   :   Account
    * Parameters    :   NA
    */
    public static Account createObjAccount(Id recordTypeId,string birthDate, String FirstName,String lastName,
                       String postalCode,String emailId, String city, String state, Boolean isDeleted) 
    {
        Account oAcc = new Account();        
        try
        {               
            oAcc.recordtypeid = recordTypeId;
            oAcc.Birthdate__c = birthDate;
            oAcc.FirstName = FirstName;
            oAcc.LastName = lastName;
            oAcc.PersonMailingPostalCode = postalCode;
            oAcc.PersonEmail = emailId;
            oAcc.ETL_Record_Deleted__c = isDeleted;
            oAcc.PersonMailingCity = city;
            oAcc.PersonMailingState = state;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CRMRetail_TestDataFactory_H_HUM ', 'createObjAccount');
        }
        return oAcc; 
    }
    /*
    * Method Name   :   createOOORecord
    * Description   :   Creates an instance of One on One tracking object and returns it
    * Return Type   :   One on One Tracking
    * Parameters    :   Account Id, Location Id, Reason for Visit,Determinants and Screening completed on OOO Tracking
    */
    public static CRMRetail_OOO_Tracking__c  createOOORecord(Id accId,Id locationId,String reasonForVisit,String Determinants,Boolean screenCmpleted){
        CRMRetail_OOO_Tracking__c trackingObj = new CRMRetail_OOO_Tracking__c();
        trackingObj.Account__c = accId;
        trackingObj.Appointment_Date__c = system.today();
        trackingObj.Appointment_Time__c = Time.newInstance(1, 2, 3, 4);
        trackingObj.CRM_Retail_Location__c = locationId;
        trackingObj.Number_of_People__c = 1;
        trackingObj.Length_of_Visit_in_minutes__c = 30;
        trackingObj.Reason_for_Visit__c = reasonForVisit;
        trackingObj.SDoH_Determinants__c=Determinants;
        trackingObj.SDoH_Screening_Completed__c=screenCmpleted;
        return trackingObj;
    }
    /*
    * Method Name   :   createSdohRecord
    * Description   :   Creates an instance of SDoH object and returns it
    * Return Type   :   CRM Retail SDoH Determinant
    * Parameters    :   Name of SDoH Record,One on One Tracking Id
    */
    public static CRMRetail_SDoHDetermt__c createSdohRecord(String Name,Id oooId){
        CRMRetail_SDoHDetermt__c sDoHRecord = new CRMRetail_SDoHDetermt__c();
        sDoHRecord.Name = Name;
        sDoHRecord.Determinant__c = Name;    
        sDoHRecord.CRMRetail_OOO_Tracking__c = oooId;
        sDoHRecord.Source__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HNC;
        sDoHRecord.Score_Date__c = system.today(); 
        sDoHRecord.Description__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_VERY_LIKELY;    
        sDoHRecord.Rating__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_RATING_FOR_CHOSEN_LIST;   
        return sDoHRecord;
    }
    /*
    * Method Name   :   getInteractionInstance
    * Description   :   Creates an instance of Storefront Interaction object and returns it
    * Return Type   :   CRM Retail Interaction
    * Parameters    :   List of copying interaction, Reason NAme, Category Type
    */
    public static Storefront_Interaction__c getInteractionInstance(List<Storefront_Interaction__c> lstDupInts,String reasonName, String categoryType){
        Storefront_Interaction__c sftTemp = new Storefront_Interaction__c();
        List<Storefront_I_Reason__c> lstSfRsn = [SELECT Id from Storefront_I_Reason__c where Name =: reasonName];
        sftTemp.Account__c = lstDupInts[0].Account__c;
        sftTemp.Category__c = categoryType;
        if(!lstSfRsn.isEmpty()){
         	sftTemp.Reason__c = lstSfRsn[0].Id;   
        }else{
        	sftTemp.Reason__c = lstDupInts[0].Reason__c;
        }
        sftTemp.Visitor_ID__c = lstDupInts[0].Visitor_ID__c;
        sftTemp.CRM_Retail_Interaction_Visitor_Type__c = lstDupInts[0].CRM_Retail_Interaction_Visitor_Type__c;
        sftTemp.Location__c = lstDupInts[0].Location__c;
        sftTemp.Storefront_Event_Starttime__c = lstDupInts[0].Storefront_Event_Starttime__c;
        sftTemp.Interaction_Date__c = lstDupInts[0].Interaction_Date__c;
        return sftTemp;
    }
}