/******************************************************************************************************************
Apex class Name    : EhubAttachmentResponse_DTO_HUM 
Version            : 1.0
Function           : Response DTO for Ehub attachment
Created On         : OCt 27 2018
Test Class         : 

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Srinivas Damera                                  10/27/2018         Original Version 
------------------------------------------------------------------------------------------------------------------*/
public with sharing class EhubAttachmentResponse_DTO_HUM {

    public AddDocument AddDocument {get; set;}

    public class AddDocumentResultDTO {
        public String Status {get; set;} 
        public String DocId {get; set;}
        public String Version {get; set;}
        
    }

    public class AddDocument {
        public AddDocumentResponse AddDocumentResponse {get; set;}
    }

    public class AddDocumentResponse {
        public AddDocumentResultDTO AddDocumentResultDTO {get; set;}
    }
}