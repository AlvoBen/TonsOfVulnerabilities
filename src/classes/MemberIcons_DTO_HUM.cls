/*****************************************************************************************
Apex class Name    : MemberIcons_DTO_HUM
Version            : 1.0
Function           : DTO class used to display member icons
Created Date       : 08/13/2019
Test Class         : MemberIcons_CT_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                      8/14/2019          Original Version
 *   Amar Gadewar                                        01/28/2020         USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
 *   Ankima Srivastava                                   04/07/2020         Used Annotation for LWC
 ****************************************************************************************************************************/
public class MemberIcons_DTO_HUM
{
    public MemberIcons_DTO_HUM() 
    {
        bIconsPresnt = false;
    }
    
    @AuraEnabled public Boolean bIconsPresnt{get;set;}
    
    @AuraEnabled public List<Icon_DTO_HUM> lstMemberIcons{get;set;}

    public class Icon_DTO_HUM
    {
        public Icon_DTO_HUM()
        {
            bIconVisible = false;
            sDocumentId = '';
            sIconLabel = '';
            sOrder = '0';
        }
        @AuraEnabled public String sIconName{get;set;}
        @AuraEnabled public String sDocumentId{get;set;}
        @AuraEnabled public Boolean bIconVisible{get;set;}
        @AuraEnabled public String sIconLabel{get;set;}
        @AuraEnabled public String sPermissionSets{get;set;}
        @AuraEnabled public String sPageName{get;set;}
        @AuraEnabled public String sProfileName{get;set;}
        @AuraEnabled public String sOrder{get;set;}
    }

}