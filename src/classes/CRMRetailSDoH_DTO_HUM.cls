/******************************************************************************************************************************
Apex Class Name  : CRMRetailSDoH_DTO_HUM
Version          : 1.0 
Created Date     : 06/22/2021
Function         : Wrapper that holds SDoH data     

Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Abhishek Maurya                               06/26/2021          User Story 2308421 -CRM Storefront SDoH: Action Required Prompt
*  Vinoth L                      				 06/26/2021          User Story 2308568 -CRM Storefront SDoH: Track DH&A Screening Results
*  Vinoth L                      				 09/02/2021          User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
*  Abhishek Maurya               				 09/02/2021          User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
*********************************************************************************************************************************/
public with sharing class CRMRetailSDoH_DTO_HUM {
    public String sdrPersonId='';
    public String scoreDate='';
    public String screeningDate='';
    public String metricName=''; 
    public String metricRating=''; 
    public String metricDescription='';  
    public String metricSource=''; 
    public DateTime timestamp =system.now();
    
     /***********************************************************************************************************************************
    * Apex Class Name  : CRMRetail_SDoH_Request_DTO_HUM 
    * Version          : 1.0
    * Created Date     : 28th June, 2021
    * Function         : Wrapper Class for SDoH web services request
    * Test Class       : 
    ****************************************************************************
    * Developer Name                             Code Review #           Date                 Description
    *---------------------------------------------------------------------------------------------------------------------------------
    * Abhishek Maurya                        					            5/25/2021            Original Version   2322470
    **********************************************************************************************************************************/
    public with sharing class  CRMRetail_SDoH_Request_DTO_HUM
    {        
        @AuraEnabled public String EnterpriseId='';
        @AuraEnabled public String toDate='';
        @AuraEnabled public String fromDate=''; 
    }       
    /******************************************************************************************************************************
    Apex Class Name  : CRMRetail_DHNA_Response_DTO
    Version          : 1.0 
    Created Date     : 09/02/2021
    Function         : Wrapper that holds DHNA RESPONSE     

    Modification Log :
    *    Developer             Code Review            Date                 Description
    * ------------------------------------------------------------------------------------------------------------------------------                
    *  Vinoth L                      				 09/02/2021          User Story - 2508432 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Call DH&A 
    *  Abhishek Maurya               				 09/02/2021          User Story - 2599455 T1PRJ0001894 - MF 7 - CRM Storefront SDoH: Share Screening Results - Update Existing 1x1 Tracking
    *********************************************************************************************************************************/
    public with sharing class CRMRetail_DHNA_Response_DTO{
        public String status = '';
        public String message = '';
    	public CRMRetailSDoH_DTO_HUM payload = new CRMRetailSDoH_DTO_HUM();
    }
}