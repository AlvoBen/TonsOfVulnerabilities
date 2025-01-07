<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Set_Created_By_Queue_to_Current_Queue</fullName>
        <description>Set the Interaction&apos;s Created By Queue to the current user&apos;s queue</description>
        <field>Created_By_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>Set Created By Queue to Current Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
        <targetObject>Interaction__c</targetObject>
    </fieldUpdates>
    <rules>
        <fullName>Case Interaction Created</fullName>
        <actions>
            <name>Set_Created_By_Queue_to_Current_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When creating a New Case Interaction record, update the Created By Owner with Current Owner Work Queue.</description>
        <formula>true</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
</Workflow>
