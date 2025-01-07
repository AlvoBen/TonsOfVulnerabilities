<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>Email_to_Article_Feedback_Creator_when_the_Article_Feedback_is_Complete_Accepted</fullName>
        <description>Email to Article Feedback Creator when the Article Feedback is Complete Accepted</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>unfiled$public/New_Article_Feedback_Complete_Accepted_Email_Template</template>
    </alerts>
    <alerts>
        <fullName>Notification_to_Requester_when_Article_feedback_status_is_Complete_Rejected</fullName>
        <description>Notification to Requester when Article feedback status is Complete Rejected</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>unfiled$public/New_Article_Feedback_Complete_Rejected_Email_Template</template>
    </alerts>
    <fieldUpdates>
        <fullName>KM_Commercial_Queue_Assignment</fullName>
        <field>OwnerId</field>
        <lookupValue>KM_Commercial_Call_Authors</lookupValue>
        <lookupValueType>Queue</lookupValueType>
        <name>KM Commercial Queue Assignment</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>KM_Editors_Queue_Assignment</fullName>
        <field>OwnerId</field>
        <lookupValue>KM_Editors</lookupValue>
        <lookupValueType>Queue</lookupValueType>
        <name>KM Editors Queue Assignment</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>KM_Request_Queue_Assignment</fullName>
        <field>OwnerId</field>
        <lookupValue>KM_Request</lookupValue>
        <lookupValueType>Queue</lookupValueType>
        <name>KM Request Queue Assignment</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Existing Article General Feedback Assignment</fullName>
        <actions>
            <name>KM_Commercial_Queue_Assignment</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Article_Feedback__c.Article_Status__c</field>
            <operation>equals</operation>
            <value>online</value>
        </criteriaItems>
        <criteriaItems>
            <field>Article_Feedback__c.RecordTypeId</field>
            <operation>equals</operation>
            <value>Existing Article</value>
        </criteriaItems>
        <description>This rule wuill be used to route feedback on existing Article to appropriate queue</description>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>Existing Article Publish Feedback Assignment</fullName>
        <actions>
            <name>KM_Editors_Queue_Assignment</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Article_Feedback__c.Article_Status__c</field>
            <operation>equals</operation>
            <value>draft</value>
        </criteriaItems>
        <criteriaItems>
            <field>Article_Feedback__c.RecordTypeId</field>
            <operation>equals</operation>
            <value>Existing Article</value>
        </criteriaItems>
        <description>This workflow rule would route the Article Feedback on existing article to appropriate queue.</description>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>New Article Feedback Assignment</fullName>
        <actions>
            <name>KM_Request_Queue_Assignment</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Article_Feedback__c.RecordTypeId</field>
            <operation>equals</operation>
            <value>New Article</value>
        </criteriaItems>
        <description>This workflow rule would route the Article Feedback records for New Articles to &quot;KM Request&quot; queue.</description>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>New Article Feedback Complete Accepted Notification</fullName>
        <actions>
            <name>Email_to_Article_Feedback_Creator_when_the_Article_Feedback_is_Complete_Accepted</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Article_Feedback__c.RecordTypeId</field>
            <operation>equals</operation>
            <value>New Article</value>
        </criteriaItems>
        <criteriaItems>
            <field>Article_Feedback__c.Feedback_Status__c</field>
            <operation>equals</operation>
            <value>Complete Accepted</value>
        </criteriaItems>
        <description>This workflow rule would send an email notification to the feedback provider when their feedback has been completed and accepted</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>New Article Feedback Complete Rejected Notification</fullName>
        <actions>
            <name>Notification_to_Requester_when_Article_feedback_status_is_Complete_Rejected</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Article_Feedback__c.RecordTypeId</field>
            <operation>equals</operation>
            <value>New Article</value>
        </criteriaItems>
        <criteriaItems>
            <field>Article_Feedback__c.Feedback_Status__c</field>
            <operation>equals</operation>
            <value>Complete Rejected</value>
        </criteriaItems>
        <description>This workflow rule would send an email notification to the feedback provider when their feedback has been completed and rejected</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
</Workflow>
