<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>Estimate_Due</fullName>
        <description>Estimate Due</description>
        <protected>false</protected>
        <recipients>
            <recipient>vbatham@humana.com</recipient>
            <type>user</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Open_Item_Escalation_Notice</template>
    </alerts>
    <alerts>
        <fullName>Send_Email_to_Assigned_Architect</fullName>
        <description>Send Email to Assigned Architect</description>
        <protected>false</protected>
        <recipients>
            <field>Estimating_Architect__c</field>
            <type>userLookup</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Impact_Estimate</template>
    </alerts>
    <rules>
        <fullName>Due Date Near</fullName>
        <actions>
            <name>Estimate_Due</name>
            <type>Alert</type>
        </actions>
        <actions>
            <name>Estimate_due</name>
            <type>Task</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Impact__c.Due_Date__c</field>
            <operation>equals</operation>
            <value>TODAY</value>
        </criteriaItems>
        <description>Send Notice when Due Date is Near</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Impacts Intake Rule</fullName>
        <actions>
            <name>Estimate_Pending</name>
            <type>Task</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Impact__c.Status__c</field>
            <operation>equals</operation>
            <value>In Progress</value>
        </criteriaItems>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Inform Architect</fullName>
        <active>false</active>
        <formula>DATEVALUE(CreatedDate)  &lt;  Due_Date__c</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <tasks>
        <fullName>Estimate_Pending</fullName>
        <assignedToType>owner</assignedToType>
        <dueDateOffset>10</dueDateOffset>
        <notifyAssignee>false</notifyAssignee>
        <priority>Normal</priority>
        <protected>false</protected>
        <status>Not Started</status>
        <subject>Action Required: Intake Estimate</subject>
    </tasks>
    <tasks>
        <fullName>Estimate_due</fullName>
        <assignedTo>vbatham@humana.com</assignedTo>
        <assignedToType>user</assignedToType>
        <dueDateOffset>0</dueDateOffset>
        <notifyAssignee>true</notifyAssignee>
        <offsetFromField>Impact__c.Due_Date__c</offsetFromField>
        <priority>Normal</priority>
        <protected>false</protected>
        <status>Not Started</status>
        <subject>Estimate due</subject>
    </tasks>
</Workflow>
