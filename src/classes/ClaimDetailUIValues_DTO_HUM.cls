/********************************************************************************************************************************
Apex Class Name : ClaimDetailUIValues_DTO_HUM
Version         : 1.0
Function        : This class is used to store the values needed on claims pageload
Created Date    : 22 July 2020
Test Class      : 

Modification Log:  
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Prasanthi Kandula                                 07/22/2020           Original Version
 *   Priyanka Puranik								   01/28/2021			REQ - 1759875 PR00094167 - MF 4 - TECH - SF- CRMS – Legacy App Launcher code clean up MTV, CAS
 *   Anuradha Gajbhe								   02/04/2022			US - 2732964 T1PRJ0085708 - Feature 11960 - Remove 'Show More' - Provider Details: Primary Specialty
 ********************************************************************************************************************************/

public class ClaimDetailUIValues_DTO_HUM    {
    
    public ClaimDetailsPage ClaimDetailsPage;
    public class ClaimDetailsPage {
        public ClaimReusableValues ClaimReusableValues; 
        public ClaimLineItems ClaimLineItems;
        public ClaimPayments ClaimPayments;
        public ClaimStatements ClaimStatements;
    }
    
    public class ClaimReusableValues{
        public string Recid = '';
        public string platformCode= '';
        public string ClaimNumber = '';
        public string sClaimType = '';
        public Boolean bPrimaryspecialityShowMore = false;
        public string sFullPrimarySpeciality= '';
        public string Primaryspeciality = '';
        public Boolean bPrimarySpecSwitch= false;
        public List<string> DiagCode = null;
        public string listAllPrimarySpeciality = '';
        public boolean bSwitchPS = false;
        public boolean bSwitchShowmorePS = false;
        public string sProviderSpecFirstRecord = '';
    }
    
    public class ClaimLineItems {
        public string TIN= '';
        public string NPI= '';
        public string lineItemsFooter= '';
        public string codeEditRemote= '';
        public string urlCauseCodeMentorLink = '';
        public Boolean bPrimaryspecialityShowMore = false;
    }
    
    public class ClaimPayments {
        public string applname = '';
        public string clientNo = '';
        public string FirstName = '';
        public string MemId = '';
        public string sGCPUrl = '';
        public string urlcasweb = '';
        public string Relation = '';
        public string claimNo= ''; 
        public Boolean PaymentNonZeroCheckNum = false;
        public Boolean PaymentZeroCheckNum = false;
        public string paymentMethod= '';
        public string AppEnvironment = '';
        public Boolean bPermissionSetApplauncher = false;
        }
    
    public class ClaimStatements {
        public string memberId= '';
        public Boolean isPermission = false;
        public string provIDVal = '';
    } 
}