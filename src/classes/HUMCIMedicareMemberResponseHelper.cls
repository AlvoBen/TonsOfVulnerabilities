/**************************************************************************************************************************************
Apex Class Name : HUMCIMedicareMemberResponseHelper
Version         : 1.0
Created On      : 12/03/2014
Function        : class used to build response for CI Mediacare Member service
Test Class      : HUMCIMedicareMemberResponseHelperTest

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Pradeep Veeravali        19768                        12/03/2014                 Original Version
* Pradeep Veeravali        20628                        01/27/2015                 Modified to add new fields in Response for POA & EOE indicators   
* Pradeep Veeravali        20878                        02/11/2015                 Modified to remove EOE fields      
****************************************************************************************************************************************/
public with sharing class HUMCIMedicareMemberResponseHelper
{    
 
    public String sLISIndicator = '';
    public String sSubsidyEffectDate = '';
    public String sSubsidyEndDate = '';
    public String sLISSubsidyLevel = '';
    public String sLISCopayCategory = '';
    public String sLTSSIndicator = '';
    public String sESRDIndicator = '';
    public String sSPAPIndicator = '';
    public String sSNPIndicator = '';
    public String sSNPTypeCode = '';
    public String sPOAIndicator = '';
    public String sPOALastName = '';
    public String sPOAFirstName = '';
    public String sPOAPhoneNumber = '';
    public String sPOAType = '';
    public String sPOAApplDate = '';
    
    public list<Error> Errors = null;
    
    public class Error {
        public String code;
        public String description;
    }
    
    
}