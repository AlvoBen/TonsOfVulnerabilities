<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Update_Created_By_Queue_in_AttachmentLog</fullName>
        <field>CreatedByQueue__c</field>
        <formula>Case__r.Created_By_Queue__c</formula>
        <name>Update Created By Queue in AttachmentLog</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Update Created By Queue From Case</fullName>
        <actions>
            <name>Update_Created_By_Queue_in_AttachmentLog</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>This workflow will update created By queue for the attached log when case service executed.</description>
        <formula>AND($Profile.Name == &quot;ETL API Access&quot;,  NOT(ISBLANK(Case__r.Created_By_Queue__c)))</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
</Workflow>
