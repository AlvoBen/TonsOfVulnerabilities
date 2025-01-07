<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Set_Created_By_Queue_to_Case_Queue</fullName>
        <description>Set the Attachment&apos;s Created By Queue field to the parent cases&apos;s queue</description>
        <field>Created_By_Queue__c</field>
        <formula>Related_To_Case__r.Owner_Queue__c</formula>
        <name>Set Created By Queue to Case Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_Created_By_Queue_to_Current</fullName>
        <description>Set the Attachment&apos;s Created By Queue field to the current queue of the logged in user</description>
        <field>Created_By_Queue__c</field>
        <formula>IF($Profile.Name != &quot;ETL API Access&quot;, $User.Current_Queue__c,  Related_To_Case__r.Created_By_Queue__c )</formula>
        <name>Set Created By Queue to Current</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Attachment Created By Case Owner</fullName>
        <actions>
            <name>Set_Created_By_Queue_to_Case_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>When creating a New Attachment, if the Case Owner and current user are the same, update the Created By Owner with Case record Owner Queue Value.</description>
        <formula>IF( Related_To_Case__r.Case_Owner__c  =  ( $User.FirstName  &amp; &quot; &quot; &amp;  $User.LastName )  , true, false)</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>Attachment Not Created By Case Owner</fullName>
        <actions>
            <name>Set_Created_By_Queue_to_Current</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>When creating a New Attachment, if the Case Owner and current user are not same, update the Created By Owner with Current Owner Work Queue.</description>
        <formula>IF( Related_To_Case__r.Owner__c   &lt;&gt;   ( $User.FirstName &amp; &quot; &quot; &amp; $User.LastName )  , true, false)</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
</Workflow>
