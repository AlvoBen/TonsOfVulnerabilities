/**************************************************************************** 
Apex Class Name  : DocumentListRequest_H_HUM 
Version          : 1.0  
Function         : Response helper for buildViewDocumentsRequest method in ViewDocuments_S_HUM 
Test Class       : DocumentListResponse_T_HUM 

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                             10/15/2018                 Original Version 
************************************************************************************************************************************************* */
public class DocumentListRequest_H_HUM {

    public GetCorrespondenceAndDocumentsRequest getCorrespondenceAndDocumentsRequest;

    public class GetCorrespondenceAndDocumentsRequest {
        public String context;
        public String medhokID;
    }

    
    public static DocumentListRequest_H_HUM parse(String json) {
        return (DocumentListRequest_H_HUM) System.JSON.deserialize(json, DocumentListRequest_H_HUM.class);
    }
}