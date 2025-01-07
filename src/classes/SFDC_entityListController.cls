/************************************************************************************************************************************************
Apex Class Name  : SFDC_entityListController
Version          : 1.1
Created Date     : Apr 18 2016
Function         : Controller Class for the Service Console Testing Utility.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Anthony Weaver                                   08/03/2016                	 Enhancements made to remove hardcoded recordtypeIds.  Also 
*                                                                                  added dynamic retrieval of the record ids.
*************************************************************************************************************************************************/
public with sharing class SFDC_entityListController {

	// Should consider making this dynamic based on objects available in the org
    private static Map<String, String> entityMap = new Map<String,String>{
        'Accounts' => 'Account.notspecific'
       ,'Business Accounts' => 'Account.false'
       ,'Person Accounts' => 'Account.true'
       ,'Cases' => 'Case'
       ,'Opportunities' => 'Opportunity'
       ,'Leads' => 'Lead'
       ,'Contacts' => 'Contact'
       ,'Policy Members' => 'Policy_Member__c'
    };
        
    public Id recTypeId {get; set; }
    public List<Id> entityIdList {get; set; }
    private Map<String, Id> recTypeMap = new Map<String,Id>();
    public string entityName { get; set; }
    public string recTypeSelected { get; set; }
    
	public SFDC_entityListController(){
		// Initialize the class variables
        entityName = 'Accounts';
        recTypeSelected = 'Not Specific';
        entityIdList = new List<Id>();
        // Retrieve the Account record IDs.
        retrieveEntityIds();
    }

    // Return the list of Entities available for running the test
    public List<SelectOption> getEntityList(){
        List<SelectOption> entities = new List<SelectOption>();
        for(String display: entityMap.keySet()){
            entities.add(new SelectOption(display,display));
        }

        return entities;
    }

    // Retrieve the list of record IDs to use for the test.
	public void retrieveEntityIds(){
        System.debug('retrieveEntityIds: entityName=' + entityName);
        if ( !entityIdList.isEmpty()){
        	entityIdList.clear();    
        }
        entityIdList = retrieveEntityIdList(entityName, true);
    }

	private List<Id> retrieveEntityIdList(String selectedEntity, Boolean includeRecordTypeLimit){
        String objType = entityMap.get(selectedEntity);
        if(objType.startsWithIgnoreCase('account')){
        	objType = 'Account';
        }
        
        String soql = 'SELECT Id FROM ' + objType; 
        
        if (recTypeSelected.equalsIgnoreCase('not specific') || includeRecordTypeLimit == false){
            soql = soql + ' LIMIT 200';
        } else {
            recTypeId = recTypeMap.get(recTypeSelected);
        	soql = soql + ' WHERE RecordTypeId = :recTypeId LIMIT 200';
        }
        
        List<sObject> sobjList = Database.query(soql);
        List<Id> sObjIdList;
        if (!sobjList.isEmpty()){
        	sObjIdList = new List<Id>(new Map<Id,SObject>(sobjList).keySet());    
        }
        return sObjIdList;
    }

    
    // Get the list of Record Types associated with the Entity selected.
    public List<SelectOption> getRecordTypeList(){
        String objType = entityMap.get(entityName);
        // Default values of false & false will retrieve only business accounts
        Boolean accountPersonType = false;
        Boolean accountPersonType2 = false;

        recTypeId = null;
        recTypeSelected = 'Not Specific';
        
        if(objType.startsWithIgnoreCase('account')){
            // If person accounts is selected, ensure both booleans are set to be true to retrieve only person accounts
            if(objType.containsIgnoreCase('.true')){
                accountPersonType = true;
                accountPersonType2 = true;
            }
            // If not specific, then ensure one of the 2 booleans is set to true to retrieve both business and person accounts.
            if(objType.containsIgnoreCase('.notspecific')){
                accountPersonType = true;
            }
        	objType = 'Account';
        }

        List<SelectOption> recTypes = new List<SelectOption>();
        recTypes.add(new SelectOption('Not Specific','Not Specific'));
        recTypeMap.clear();
        
        for(RecordType rec:[SELECT Id, Name, DeveloperName, SobjectType, IsActive, IsPersonType FROM RecordType WHERE SobjectType = :objType and (IsPersonType = :accountPersonType OR IsPersonType = :accountPersonType2 ) Order By SobjectType, IsPersonType, Name]){
            recTypeMap.put(rec.Name, rec.Id);
        	recTypes.add( new SelectOption(rec.Name,rec.Name));
        }

		return recTypes;
    }

    @remoteAction
    public static List<String> getSubtabEntityList(String tabObject){
        List<String> entities = new List<String>();
        String exclude = tabObject;
        if (exclude.containsIgnoreCase('Accounts')){
            exclude = 'Accounts';
        }
        for(String display: entityMap.keySet()){
            if ( !display.containsIgnoreCase(exclude)){
            	entities.add(display);
            }
        }
        return entities;
    }
    
    @remoteAction
    public static List<Id> retrieveSubtabEntityIds(String subtabObject){
        String objType = entityMap.get(subtabObject);
        if(objType.startsWithIgnoreCase('account')){
        	objType = 'Account';
        }
        
        // Only need 1 record Id as this Id is used to open the Primary Tab.
        String soql = 'SELECT Id FROM ' + objType + ' LIMIT 1';
        
        List<sObject> sobjList = Database.query(soql);
        List<Id> sObjIdList;
        if (!sobjList.isEmpty()){
        	sObjIdList = new List<Id>(new Map<Id,SObject>(sobjList).keySet());    
        }
        return sObjIdList;
    }
}