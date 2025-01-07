<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>Story_Assignment_Notification</fullName>
        <description>Story Assignment Notification</description>
        <protected>false</protected>
        <recipients>
            <field>Assigned_To__c</field>
            <type>userLookup</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Story_Assignment_Notification</template>
    </alerts>
    <rules>
        <fullName>Story Assignment Notification</fullName>
        <actions>
            <name>Story_Assignment_Notification</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <description>Notify a user when they are assigned a story.</description>
        <formula>ISCHANGED( Assigned_To__c )</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
