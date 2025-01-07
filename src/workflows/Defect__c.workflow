<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Actual_Closed_Date</fullName>
        <description>Populates the Actual Closed Date on the Defect Object</description>
        <field>Actual_Closed_Date__c</field>
        <formula>now()</formula>
        <name>Actual Closed Date</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Populate Defect Actual Closed Date</fullName>
        <actions>
            <name>Actual_Closed_Date</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Defect__c.Defect_Status__c</field>
            <operation>equals</operation>
            <value>5a. Closed</value>
        </criteriaItems>
        <description>Populates the Actual Closed Date on the Defect Object</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
</Workflow>
