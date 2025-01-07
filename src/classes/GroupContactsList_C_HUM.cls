/*
Class Name       : GroupContactsList_C_HUM
Created Date     : 05/20/2021
Function         : Controller for GroupContactsList_CMP_HUM component
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service
* Firoja Begam                  08/18/2021				   USER STORY 2527651 T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service - Connect to Secured DP https service URL
-------------------------------------------------------------------------------------------------------------------------------*/


public class GroupContactsList_C_HUM {
    public string groupId{get;set;}
    public GroupContactsListInfoResponse_DTO_HUM asstListInfo{get;set;}
    public string asstListInfoJson{get;set;}
    public boolean isLoaded {get;set;}
    public string continuationRequestLabel;
    
    public GroupContactsList_C_HUM(){
        isLoaded = false;
    }
    
    public void closeSection(){
        isLoaded = false;
    }
    
    public Object startAssignmentListRequest() {
        //Added new EPICC Service instance for V2.0 on SwitchOn of US2527651
        HUM_Webservice_Callout__c hwc;
        if(HUMUtilityHelper.isCRMFunctionalityON('2527651')){
        hwc = HUM_Webservice_Callout__c.getInstance(GroupContactsList_WS_HUM.serviceName_V2);
        }else{
        hwc = HUM_Webservice_Callout__c.getInstance(GroupContactsList_WS_HUM.serviceName);
        }
        Integer serviceTimeOut = Integer.valueOf(hwc.Timeout__c)/1000;
        // Create continuation with a timeout
        Continuation con = new Continuation(serviceTimeOut);
        // Set callback method
        con.continuationMethod='processAssignmentListResponse';
        // Create callout request
        HttpRequest req = GroupContactsList_WS_HUM.getGroupContactsRequest(hwc);
        String body = json.serialize(GroupContactsList_WS_HUM.getGroupContactsRequestBody(groupId, GroupContactsList_WS_HUM.isShowAlternate, GroupContactsList_WS_HUM.sortDirection, 
                                                                                            GroupContactsList_WS_HUM.sortBy, GroupContactsList_WS_HUM.isRequestorList, GroupContactsList_WS_HUM.currentPage, 
                                                                                            GroupContactsList_WS_HUM.isSameFeature, GroupContactsList_WS_HUM.pageSize, GroupContactsList_WS_HUM.divisionId));
        
        req.setBody(body);
        system.debug('testreq'+body);
        // Add callout request to continuation
        this.continuationRequestLabel = con.addHttpRequest(req);
        
        // Return the continuation
        return con;  
    }
    
    // Callback method 
    public Object processAssignmentListResponse() {  
        try{
            // Get the response by using the unique label
            HttpResponse response = Continuation.getResponse(this.continuationRequestLabel);
            String responseBody = response.getBody();
            //responseBody = '{ "assignmentServiceResponse": [ { "ContactInfo": { "FirstName": "Iain  G", "LastName": "D Alfonso", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "19", "ContactTypeName": "Primary" }, "LevelInfo": { "LevelId": "0", "LevelName": "Group" }, "DivisionInfo": { "DivisionName": "", "DivisionDisplayName": "" }, "AddressInfo": { "AddressLine1": "12400 W Hwy 71", "AddressLine2": "Suite 350  Pmb 341", "AddressLine3": "", "City": "Austin", "State": "TX", "Zip": "78738" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "Migrating" }, "PhoneInfo": { "PhoneNumber": "5123011675", "Extension": "", "FaxNumber": "0" } }, { "ContactInfo": { "FirstName": "Iain", "LastName": "Gillott", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "19", "ContactTypeName": "Primary" }, "LevelInfo": { "LevelId": "0", "LevelName": "Division" }, "DivisionInfo": { "DivisionName": "Igillottresearch", "DivisionDisplayName": "01 - Igillottresearch" }, "AddressInfo": { "AddressLine1": "12400 W Hwy 71", "AddressLine2": "Suite #350  Pmb 341", "AddressLine3": "", "City": "Austin", "State": "TX", "Zip": "78738" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "Migrating" }, "PhoneInfo": { "PhoneNumber": "5123011675", "Extension": "", "FaxNumber": "0" } }, { "ContactInfo": { "FirstName": "Patricia", "LastName": "Granberry", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "20", "ContactTypeName": "Backup" }, "LevelInfo": { "LevelId": "0", "LevelName": "Group" }, "DivisionInfo": { "DivisionName": "", "DivisionDisplayName": "" }, "AddressInfo": { "AddressLine1": "", "AddressLine2": "", "AddressLine3": "", "City": "", "State": "", "Zip": "" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "NA" }, "PhoneInfo": { "PhoneNumber": "0", "Extension": "", "FaxNumber": "0" } }, { "ContactInfo": { "FirstName": "Mohan", "LastName": "Test", "Suffix": "" }, "ContactTypeInfo": { "ContactTypeId": "20", "ContactTypeName": "Backup" }, "LevelInfo": { "LevelId": "0", "LevelName": "Group" }, "DivisionInfo": { "DivisionName": "", "DivisionDisplayName": "" }, "AddressInfo": { "AddressLine1": "", "AddressLine2": "", "AddressLine3": "", "City": "", "State": "", "Zip": "" }, "EmailInfo": { "EmailAddress": "", "EmailStatus": "NA" }, "PhoneInfo": { "PhoneNumber": "5022980669", "Extension": "", "FaxNumber": "0" } } ] }';
            if(String.isBlank(responseBody)){
                throw new HUMGroupContactsException('No Response Receieved from the API');
            }
            system.debug('response=='+responseBody);
            asstListInfo = (GroupContactsListInfoResponse_DTO_HUM)json.deserialize(responseBody, GroupContactsListInfoResponse_DTO_HUM.class);
            asstListInfoJson = json.serialize(asstListInfo);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'GroupContactsList_C_HUM', 'processAssignmentListResponse');
            return null;
        }
        isLoaded = true;
        return null;
    }
    
    public class HUMGroupContactsException extends Exception {
        
    }
}