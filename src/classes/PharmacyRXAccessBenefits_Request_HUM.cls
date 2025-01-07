/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_Request_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This Class is request class for KMDocumentServiceREST DoSearch
****************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
public with sharing class PharmacyRXAccessBenefits_Request_HUM {
    
    public DoSearchRequest DoSearchRequest;
    
    public class DoSearchRequest {
        public Boolean BenefitGridsOnly; 
        public String ConsumingApp;
        public Boolean DoPaging;
        public Integer MaxNumberResults;
        public String Query;
        public Integer ResultsPerPage; 
        public String SLSessionID;
        public String SortOrder;
        public String UserAKAName; 
    }
}