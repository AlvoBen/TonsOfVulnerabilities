/*****************************************************************************************
Apex class Name    : AdditionalResource_LC_HUM
Version            : 1.0
Function           : Used for retriving Additional Resource Custom links

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 *   Aishwarya Pawar                                     09/26/2022          REQ- 3742726-T1PRJ0170850- MF19876 - Lightning- Additional Resources -Utility Bar
 *   Aishwarya Pawar                                     10/31/2022          REQ-  3819554
 *   Kalyani Pachpol                                     07/13/2023          US-4810367
 *   Jonathan Dickinson                                  07/26/2023          User Story 4779925: T1PRJ1117588- MF 27094 - CarePlus - Lightning CRM - Create a CarePlus section on Additional Resource Widget
********************************************************************************************************************************/
public with sharing class AdditionalResource_LC_HUM {
    
    @AuraEnabled(cacheable=true)
    public static List<CustomLink__c> getLinks(){
        try {
            Integer iValue = Integer.Valueof(HUMConstants__c.getInstance('HUMConstantHundered').IntegerValue__c); 
            // order by subsection first then name so that the links and sections are automatically ordered according to the picklist value order
            List<CustomLink__c> lstCustomLink = [Select Name,Subsection__c,URL__c from CustomLink__c where RecordType.Name = 'Custom Link' WITH USER_MODE order by Subsection__c, Name Limit : iValue ];
            return lstCustomLink;
        } catch (Exception e) {
            HUMExceptionHelper.logErrors(e, 'AdditionalResource_LC_HUM', 'getLinks');
            throw new AuraHandledException(e.getMessage());
        }
    }

    @AuraEnabled(cacheable=true)
    public static List<String> getSections() {
        List<String> pickVals = new List<String>();
        Schema.DescribeFieldResult field = CustomLink__c.Subsection__c.getDescribe();
        List<Schema.PicklistEntry> entries = field.getPicklistValues();
        for(Schema.PicklistEntry entry : entries) {
            pickVals.add(entry.getLabel());
        }

        return pickVals;
    }
}