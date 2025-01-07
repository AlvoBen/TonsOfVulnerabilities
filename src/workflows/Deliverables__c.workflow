<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Update_Actual_Completion_Date</fullName>
        <description>To update the actual completion date when the status of the deliverable is changed to Completed</description>
        <field>Actual_Completion_Date__c</field>
        <formula>Today()</formula>
        <name>Update Actual Completion Date</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>BME - Update Actual Completion Date</fullName>
        <actions>
            <name>Update_Actual_Completion_Date</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Deliverables__c.Status__c</field>
            <operation>equals</operation>
            <value>Completed</value>
        </criteriaItems>
        <description>This workflow will update the actual completion date of the deliverable when the status is changed to completed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
</Workflow>
