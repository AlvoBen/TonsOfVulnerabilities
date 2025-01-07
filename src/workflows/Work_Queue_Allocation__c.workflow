<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Source_Work_Queue_Unique</fullName>
        <field>Source_Work_Queue_Unique__c</field>
        <formula>Source_Work_Queue__c</formula>
        <name>Source Work Queue Unique</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Work_Queue_Unique</fullName>
        <field>Source_Work_Queue_View_Unique__c</field>
        <formula>Source_Work_Queue_View__c &amp; Source_Work_Queue__c</formula>
        <name>Work Queue Unique</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Source Work Queue Unique</fullName>
        <actions>
            <name>Source_Work_Queue_Unique</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <formula>OR( 		 	        ISNEW(), 		 	        ISCHANGED(Source_Work_Queue__c) 		         )</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Work Queue View</fullName>
        <actions>
            <name>Work_Queue_Unique</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <formula>AND(OR( ISNEW(), ISCHANGED(Source_Work_Queue_View__c) ),NOT(ISBLANK(Source_Work_Queue_View__c)))</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
