<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>Functional_Review_Reminder</fullName>
        <description>Functional Review Reminder</description>
        <protected>false</protected>
        <recipients>
            <type>owner</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Please_Complete_Functional_Review</template>
    </alerts>
    <alerts>
        <fullName>Functional_review_not_complete</fullName>
        <description>Functional review not complete</description>
        <protected>false</protected>
        <recipients>
            <type>owner</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Please_Complete_Functional_Review</template>
    </alerts>
    <alerts>
        <fullName>Technical_Review_Reminder_Email</fullName>
        <description>Technical Review Reminder Email</description>
        <protected>false</protected>
        <recipients>
            <type>owner</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Please_Complete_Technical_Review</template>
    </alerts>
    <alerts>
        <fullName>Technical_review_not_complete</fullName>
        <description>Technical review not complete</description>
        <protected>false</protected>
        <recipients>
            <type>owner</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Please_Complete_Technical_Review</template>
    </alerts>
    <alerts>
        <fullName>User_Review_Reminder_Email</fullName>
        <description>User Review Reminder Email</description>
        <protected>false</protected>
        <recipients>
            <type>owner</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Please_Complete_User_Review</template>
    </alerts>
    <alerts>
        <fullName>User_design_review_not_complete</fullName>
        <description>User design review not complete</description>
        <protected>false</protected>
        <recipients>
            <type>owner</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/Please_Complete_User_Review</template>
    </alerts>
    <rules>
        <fullName>Complete Functional Review Reminder</fullName>
        <actions>
            <name>Functional_review_not_complete</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <booleanFilter>1 AND 2 AND 3</booleanFilter>
        <criteriaItems>
            <field>Major_Feature__c.Functional_Review_Completed__c</field>
            <operation>notEqual</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Major_Feature__c.Expected_Functional_Review_Date__c</field>
            <operation>lessThan</operation>
            <value>TODAY</value>
        </criteriaItems>
        <criteriaItems>
            <field>Major_Feature__c.Expected_Functional_Review_Date__c</field>
            <operation>notEqual</operation>
        </criteriaItems>
        <description>This workflow will send an email reminder if the Functional Review Date has passed and the Complete field has not been checked.</description>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Complete Technical Review Reminder</fullName>
        <actions>
            <name>Technical_review_not_complete</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Major_Feature__c.Technical_Review_Completed__c</field>
            <operation>equals</operation>
            <value>False</value>
        </criteriaItems>
        <criteriaItems>
            <field>Major_Feature__c.Expected_Technical_Review_Date__c</field>
            <operation>lessThan</operation>
            <value>TODAY</value>
        </criteriaItems>
        <criteriaItems>
            <field>Major_Feature__c.Expected_Technical_Review_Date__c</field>
            <operation>notEqual</operation>
        </criteriaItems>
        <description>This workflow will send an email reminder if the Technical Design Review Date has passed and the Complete field has not been checked.</description>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Complete User Review Reminder</fullName>
        <actions>
            <name>User_design_review_not_complete</name>
            <type>Alert</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Major_Feature__c.User_Design_Review_Complete__c</field>
            <operation>equals</operation>
            <value>False</value>
        </criteriaItems>
        <criteriaItems>
            <field>Major_Feature__c.Expected_User_Review_Date__c</field>
            <operation>lessThan</operation>
            <value>TODAY</value>
        </criteriaItems>
        <criteriaItems>
            <field>Major_Feature__c.Expected_User_Review_Date__c</field>
            <operation>notEqual</operation>
        </criteriaItems>
        <description>This workflow will send an email reminder if the User Review Date has passed and the Complete field has not been checked.</description>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>
