<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Update_Log_code</fullName>
        <field>Log_Code__c</field>
        <formula>text(Humana_Pharmacy_Log_Code__c)</formula>
        <name>Update Log code</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Update Logcode</fullName>
        <actions>
            <name>Update_Log_code</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <formula>AND($Profile.Name !=&quot;Deployment&quot;,$Profile.Name !=&quot;System Administrator&quot;, IF( ISNEW() , NOT( ISPICKVAL( Humana_Pharmacy_Log_Code__c , &quot;&quot;) ), ISCHANGED( Humana_Pharmacy_Log_Code__c ) ))</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
