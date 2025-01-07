<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>An_Article_Needs_Review</fullName>
        <description>An Article Needs Review</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>Community_Emails/Knowledge_Article_Requires_Review</template>
    </alerts>
    <alerts>
        <fullName>An_Article_Requires_Approval</fullName>
        <description>An Article Requires Approval</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>Community_Emails/An_Article_Requires_Approval</template>
    </alerts>
    <alerts>
        <fullName>An_Article_has_been_Approved</fullName>
        <description>An Article has been Approved</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>Community_Emails/Article_Approved</template>
    </alerts>
    <alerts>
        <fullName>Article_Rejected_please_Review</fullName>
        <description>Article Rejected Please Review</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>Community_Emails/Knowledge_Article_Requires_Review</template>
    </alerts>
    <fieldUpdates>
        <fullName>Article_Validated</fullName>
        <description>Article Validated upon approval</description>
        <field>ValidationStatus</field>
        <literalValue>Validated</literalValue>
        <name>Article Validated</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>true</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Visible_To_Community_Members</fullName>
        <description>The article will be visible to Community Members upon approval</description>
        <field>IsVisibleInCsp</field>
        <literalValue>1</literalValue>
        <name>Visible To Community Members</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Visible_Within_Public_Knowledge_Base</fullName>
        <description>Article will be visible in public knowledge base</description>
        <field>IsVisibleInPkb</field>
        <literalValue>1</literalValue>
        <name>Visible Within Public Knowledge Base</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>true</reevaluateOnChange>
    </fieldUpdates>
    <knowledgePublishes>
        <fullName>Approve_and_Publish</fullName>
        <action>PublishAsNew</action>
        <description>Approve and publish article as new.</description>
        <label>Approve and Publish</label>
        <language>en_US</language>
        <protected>false</protected>
    </knowledgePublishes>
    <knowledgePublishes>
        <fullName>Publish</fullName>
        <action>Publish</action>
        <label>Publish</label>
        <language>en_US</language>
        <protected>false</protected>
    </knowledgePublishes>
    <rules>
        <fullName>Knowledge - Auto Publish an article</fullName>
        <actions>
            <name>Publish</name>
            <type>KnowledgePublish</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Knowledge__kav.Auto_Publish__c</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
