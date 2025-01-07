<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>CreatedByQueue_Update</fullName>
        <field>Created_By_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>CreatedByQueue_Update</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Record_Type_Update</fullName>
        <description>Set RecordType equals to CRMInteractions</description>
        <field>RecordTypeId</field>
        <lookupValue>CRMInteractions</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Record Type Update</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Interaction Record Type Update</fullName>
        <actions>
            <name>Record_Type_Update</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>This workflow rule will update interaction RecordType for Automated User</description>
        <formula>CreatedBy.FirstName = &apos;Automated&apos; &amp;&amp; CreatedBy.LastName = &apos;Process&apos;</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>Update Created By Queue</fullName>
        <actions>
            <name>CreatedByQueue_Update</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>Assign &quot;Created By Queue&quot; to an Interaction that is not associated to a Case</description>
        <formula>$Profile.Name &lt;&gt; &quot;ETL API Access&quot;</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
</Workflow>
