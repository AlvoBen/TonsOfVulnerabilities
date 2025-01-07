/**************************************************************************** 
Apex Class Name  : DocumentListResponse_H_HUM 
Version          : 1.0  
Function         : Response helper for processViewDocumentsResponse method in ViewDocuments_S_HUM 
Test Class       : DocumentListResponse_T_HUM 

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rajesh                                   10/15/2018                 Original Version 
* Swapnali Sonawane                        04/09/2021                 Req 2119511 added ExRM Fields to MHK Web Service And Additional Data for G&A Status Page in CRM 
************************************************************************************************************************************************* */
public class DocumentListResponse_H_HUM {

    public class CorrespondencesAndDocuments {
        public String documentId;
        public String documentType;
        public String fileName;
        public String userId;
        public String firstName;
        public String lastName;
        public String attachmentReceivedDate;
        public String docSource;
        public String createDate;
    }

    public class GetCorrespondenceAndDocumentsResponse {
        public List<CorrespondencesAndDocuments> correspondencesAndDocuments;
    }

    public GetCorrespondenceAndDocumentsResponse getCorrespondenceAndDocumentsResponse;

    
    public static DocumentListResponse_H_HUM parse(String json) {
        return (DocumentListResponse_H_HUM) System.JSON.deserialize(json, DocumentListResponse_H_HUM.class);
    }
}