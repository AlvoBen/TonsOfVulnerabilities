<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>Test_Email_Alert</fullName>
        <ccEmails>dgustafson@salesforce.com;</ccEmails>
        <description>Test_Email_Alert</description>
        <protected>false</protected>
        <recipients>
            <recipient>ksanders11@humana.com</recipient>
            <type>user</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Customization_Request_Approval</template>
    </alerts>
    <rules>
        <fullName>Chatter_Test_KLS</fullName>
        <actions>
            <name>Test_Email_Alert</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>User.MobilePhone</field>
            <operation>notEqual</operation>
        </criteriaItems>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
