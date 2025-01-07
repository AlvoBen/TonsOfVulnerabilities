/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_Wrapper_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This Class is a wrapper class for KMDocumentServiceREST DoSearch to display result on UI
******************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
public class PharmacyRXAccessBenefits_Wrapper_HUM {
  
    @AuraEnabled
    public KMDSearchResultsWrapper KMDSearchResultsWrapper;
    
    public class KMDSearchResultsWrapper {
        @AuraEnabled
        public List<Documents> Documents;       
        @AuraEnabled
        public Integer NumberRetrieved;
        @AuraEnabled
        public String SearchReferenceID;
        @AuraEnabled
        public Integer TotalDocumentsFound;
    }

    public class Documents {
        @AuraEnabled
        public String FileType;
        @AuraEnabled
        public String FileUrl;
        @AuraEnabled
        public String FileName;
    }
  
}