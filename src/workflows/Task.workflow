<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <fieldUpdates>
        <fullName>Created_By_Queue</fullName>
        <field>Created_By_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>Created By Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Data_timeClosedTaskUpdate</fullName>
        <field>Date_Time_Closed__c</field>
        <formula>Now()</formula>
        <name>Data/timeClosedTaskUpdate</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Data_timeReopnedTaskUpdate</fullName>
        <field>Date_Time_Reopned__c</field>
        <formula>NOW()</formula>
        <name>Data/timeReopnedTaskUpdate</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Department</fullName>
        <field>Department__c</field>
        <formula>&apos;Humana Wellness Solutions&apos;</formula>
        <name>Department</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Last_Modified_By_Queue</fullName>
        <field>LastModifiedby_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>Last Modified By Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>OwnerQueue</fullName>
        <field>Owner_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>OwnerQueue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>ServiceCenter</fullName>
        <field>Service_Center__c</field>
        <formula>&apos;Humana Wellness Solutions&apos;</formula>
        <name>ServiceCenter</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Created_By_Queue</fullName>
        <field>Created_By_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>Update Created By Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Last_Modified_By_Queue</fullName>
        <field>LastModifiedby_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>Update Last Modified By Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Previous_Owner_Queue_Value</fullName>
        <description>When the Task is transferred from the original queue to another queue and the user = owner, the Previous Owner Queue = Original Queue but when the user != owner, the Previous Owner Queue = User.Current_Work_Queue__c.</description>
        <field>Previous_Owner_Queue__c</field>
        <formula>IF(PRIORVALUE(OwnerId) = $User.Id &amp;&amp; PRIORVALUE(Task_Owner__c) ==  $User.FirstName + &apos; &apos; + $User.LastName, PRIORVALUE(Owner_Queue__c),  $User.Current_Queue__c)</formula>
        <name>Update Previous Owner Queue Value</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>WorkQueueViewName</fullName>
        <field>Work_Queue_View_Name__c</field>
        <formula>&apos;Other&apos;</formula>
        <name>WorkQueueViewName</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>HUMTaskClosed</fullName>
        <actions>
            <name>Data_timeClosedTaskUpdate</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Task.Status</field>
            <operation>equals</operation>
            <value>Closed,Completed</value>
        </criteriaItems>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>HUMTaskReOpened</fullName>
        <actions>
            <name>Data_timeReopnedTaskUpdate</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <formula>AND(ISCHANGED( Status),  ISPICKVAL(PRIORVALUE(Status),&quot;Closed&quot;) )</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Task Created</fullName>
        <actions>
            <name>Created_By_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Department</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Last_Modified_By_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>OwnerQueue</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>ServiceCenter</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>WorkQueueViewName</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>When Task is created through Wellness Coaching app, setup default values to Queues &amp; Owner field</description>
        <formula>$Profile.Name  = &apos;Humana Wellness Coach&apos;  ||  $Profile.Name = &apos;Humana Wellness Manager&apos;</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Task Owner Queue Updated</fullName>
        <actions>
            <name>Update_Previous_Owner_Queue_Value</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>Triggers when the Task.Owner_Queue__c is modified.</description>
        <formula>ISCHANGED(Owner_Queue__c) &amp;&amp; !ISNEW()</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>