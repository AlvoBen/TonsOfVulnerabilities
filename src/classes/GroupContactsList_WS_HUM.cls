/*
Class Name       : GroupContactsList_WS_HUM
Created Date     : 05/20/2021
Function         : Webservice Class for GroupContactsList_C_HUM
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     05/20/2021			  US2067694: T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service
*2.0     Firoja Begam                            08/18/2021			  USER STORY 2527651 T1PRJ0022885 - MF 8 - Display Small Group Contacts on Group Contact tab in CRM Service - Connect to Secured DP https service URL
-------------------------------------------------------------------------------------------------------------------------------*/


public class GroupContactsList_WS_HUM {
    public static final string serviceName = 'EPICCService'; 
    public static final string serviceName_V2 = 'EPICCService_V2';
    public static final string isShowAlternate = 'true', isRequestorList = 'true' ,isSameFeature = 'true';
    public static final string sortDirection = 'asc';
    public static final string sortBy = 'LastName';
    public static final string currentPage ='1', pageSize = '1';
    public static final string divisionId = '9999';
    
    public static httpRequest getGroupContactsRequest(HUM_Webservice_Callout__c hwc){
        HttpRequest request = new HttpRequest();
        request.setEndpoint(hwc.End_Point_URL__c);
        request.setMethod(hwc.Request_Type__c);
        request.setHeader('Content-Type',hwc.Content_Type__c);
        request.setTimeout(Integer.valueOf(hwc.Timeout__c));
        if(!test.isRunningTest())
        request.setClientCertificateName(hwc.Certificate_Name__c);
        return request;
    }
    
    public static GroupContactsListInfoRequest_DTO_HUM getGroupContactsRequestBody(String searchText, String isShowAlternate, String sortDirection,
                                                                                    String sortBy, String isRequestorList, String currentPage,
                                                                                    String isSameFeature, String pageSize, String divisionId){
        GroupContactsListInfoRequest_DTO_HUM req = new GroupContactsListInfoRequest_DTO_HUM();
        req.AssignmentListingInfo = new GroupContactsListInfoRequest_DTO_HUM.AssignmentListingInfo();
        req.AssignmentListingInfo.SearchText = searchText;
        req.AssignmentListingInfo.IsShowAlternate = isShowAlternate;
        req.AssignmentListingInfo.SortDirection = sortDirection;
        req.AssignmentListingInfo.SortBy = sortBy;
        req.AssignmentListingInfo.IsRequestorList = isRequestorList;
        req.AssignmentListingInfo.CurrentPage = currentPage;
        req.AssignmentListingInfo.IsSameFeature = isSameFeature;
        req.AssignmentListingInfo.PageSize = pageSize;
        req.AssignmentListingInfo.DivisionId = divisionId;
        return req;
    }
}