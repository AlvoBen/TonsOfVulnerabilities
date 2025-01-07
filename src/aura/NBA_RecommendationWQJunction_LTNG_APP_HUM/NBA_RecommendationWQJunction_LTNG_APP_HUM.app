<aura:application extends="force:slds"  access="GLOBAL" >
	<aura:attribute name="selectedRecords" type="NBALwcMultiLookupController.SObjectResult[]" description="text" ></aura:attribute>
<lightning:card title=""> 
    <c:nBALwcMultiLookup objectName ="Work_Queue_Setup__c" fieldName="Name" 
                      onselected="{!c.selectedRecords}"/>   
</lightning:card>
</aura:application>