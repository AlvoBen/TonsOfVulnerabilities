<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Destination_Work_Queue_Unique</fullName>
        <field>Destination_Work_Queue_Unique__c</field>
        <formula>( Work_Queue_Allocation__c + Destination_Work_Queue__c )</formula>
        <name>Destination Work Queue Unique</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Destination Work Queue Unique</fullName>
        <actions>
            <name>Destination_Work_Queue_Unique</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <formula>OR( 		ISNEW(), 		ISCHANGED(Destination_Work_Queue__c) 		)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
