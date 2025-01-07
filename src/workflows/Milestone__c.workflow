<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>BME_Milestone_Actual_Completion_Update</fullName>
        <description>Update the Actual completion date when the status of Milestone changes to Completed</description>
        <field>Actual_Completion_Date__c</field>
        <formula>Today()</formula>
        <name>BME - Milestone-Actual Completion Update</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Update Actual Completion date</fullName>
        <actions>
            <name>BME_Milestone_Actual_Completion_Update</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Milestone__c.Status__c</field>
            <operation>equals</operation>
            <value>Completed</value>
        </criteriaItems>
        <description>This workflow will update the actual completion date when the Milestone status is updated to Completed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
</Workflow>
