<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>Send_TX_Complaint_Letter_to_Customer</fullName>
        <ccEmails>yimyeung@deloitte.com</ccEmails>
        <description>Send TX Complaint Letter to Customer</description>
        <protected>false</protected>
        <recipients>
            <type>creator</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>unfiled$public/SUPPORTNewassignmentnotificationSAMPLE</template>
    </alerts>
    <fieldUpdates>
        <fullName>Case_Owner</fullName>
        <field>Case_Owner__c</field>
        <formula>IF(BEGINS(OwnerId, &apos;005&apos;) ,  $User.FirstName + &apos; &apos; +  $User.LastName  ,  $User.Current_Queue__c )</formula>
        <name>Case Owner</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Check_Medicare_Reopened_Flag</fullName>
        <description>When the case is reopened (changed from Closed to other status) the Reopened flag should be set.</description>
        <field>Reopened__c</field>
        <literalValue>1</literalValue>
        <name>Check Medicare Reopened Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Check_Reopened_Flag</fullName>
        <description>When the case is reopened (changed from Closed to other status) the Reopened flag should be set.</description>
        <field>Reopened__c</field>
        <literalValue>1</literalValue>
        <name>Check Reopened Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Den_Update_TX_Complaint_Letter_Requested</fullName>
        <description>To update Texas Complaint Letter Requested field when all conditions are met.</description>
        <field>Texas_Complaint_Letter_Requested__c</field>
        <literalValue>1</literalValue>
        <name>Den Update TX Complaint Letter Requested</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Department</fullName>
        <description>Set default value as &quot;Humana Wellness Solutions&quot; for Wellness app</description>
        <field>Department__c</field>
        <formula>&apos;Humana Wellness Solutions&apos;</formula>
        <name>Department</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Email_Sent</fullName>
        <field>Email_Sent__c</field>
        <literalValue>1</literalValue>
        <name>Email Sent</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>OGO_ChangeCaseStatus</fullName>
        <description>Change case status back to &quot;In Progress&quot; if it is &quot;Closed&quot;.</description>
        <field>Status</field>
        <literalValue>In Progress</literalValue>
        <name>OGO_ChangeCaseStatus</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Owner_Queue</fullName>
        <description>This is to capture the user assigned queue and when user create Care plan</description>
        <field>Owner_Queue__c</field>
        <formula>$User.Current_Queue__c</formula>
        <name>Owner Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Service_Center</fullName>
        <description>Set Default Value as &apos;Humana Wellness Solutions&apos; for Wellness app</description>
        <field>Service_Center__c</field>
        <formula>&apos;Humana Wellness Solutions&apos;</formula>
        <name>Service Center</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_CoKY_Case_Flag</fullName>
        <description>Set the CoKY Case flag equal to True</description>
        <field>CoKY_Case__c</field>
        <literalValue>1</literalValue>
        <name>Set CoKY Case Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_Contract_Protected_Case_to_1</fullName>
        <field>Contract_Protected_Case__c</field>
        <formula>&apos;1&apos;</formula>
        <name>Set Contract Protected Case to 1</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_DST_Case_Flag</fullName>
        <description>Set the DST Case flag equal to True</description>
        <field>DST_Case__c</field>
        <literalValue>1</literalValue>
        <name>Set DST Case Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_General_Case_Flag</fullName>
        <field>General_Case__c</field>
        <literalValue>1</literalValue>
        <name>Set General Case Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_HO_Dental_Case_Flag</fullName>
        <description>Set the HO Dental Case flag equal to true</description>
        <field>HO_Dental_Case__c</field>
        <literalValue>1</literalValue>
        <name>Set HO Dental Case Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_HO_Medical_Case_Flag</fullName>
        <description>Set the HO Medical Case flag to true</description>
        <field>HO_Medical_Case__c</field>
        <literalValue>1</literalValue>
        <name>Set HO Medical Case Flag</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_Tenant_ID_to_Humana</fullName>
        <description>Sets the Tenant ID equal to Humana (00)</description>
        <field>Tenant_Id__c</field>
        <formula>&apos;00&apos;</formula>
        <name>Set Tenant ID to Humana</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Set_Tenant_Id_equal_to_parent_Account</fullName>
        <field>Tenant_Id__c</field>
        <formula>Account.Tenant_Id__c</formula>
        <name>Set Tenant Id equal to parent Account</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>UpdateProductTypeBasedSharing</fullName>
        <description>To update the field IsProductTypeBasedSharing so that the boolean value can be used in case sharing rule</description>
        <field>IsProductTypeBasedSharing__c</field>
        <literalValue>1</literalValue>
        <name>UpdateProductTypeBasedSharing</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>UpdateType_to_Closed_HP_Agent_BrokerCase</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_HP_Agent_Broker_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>UpdateType to Closed HP Agent/BrokerCase</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Associate_Msg_Field</fullName>
        <description>Updating field with message to assoicate that complaint letter will be generated and send to customer after case is saved.</description>
        <field>TX_Complaint_Related_Message__c</field>
        <formula>&quot;A TX Complaint Letter must be sent for Fully Insured HMO, HMO Lite, NPOS and PPO membership with an employer market in TX or a member residence in TX.&quot;</formula>
        <name>Update Associate Msg Field</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>true</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Case_Owner_Profile</fullName>
        <description>Capture the Case Owner Profile.  Use this when a case is transferred from one queue to another queue.</description>
        <field>Previous_Case_Owner_Role__c</field>
        <formula>$Profile.Name</formula>
        <name>Update Case Owner Profile</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>true</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Case_Previous_Owner_Queue</fullName>
        <description>When the Case is transferred from the original queue to another queue and the user = owner, the Previous Owner Queue = Original Queue but when the user != owner, the Previous Owner Queue = User.Current_Work_Queue__c.</description>
        <field>Previous_Owner_Queue__c</field>
        <formula>IF(PRIORVALUE(OwnerId) = $User.Id, PRIORVALUE(Owner_Queue__c), $User.Current_Queue__c)</formula>
        <name>Update Case Previous Owner Queue</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Created_By_Queue</fullName>
        <description>This is to capture the user assigned queue and when user &apos;created&apos; a case record.  Only on create not on edit.</description>
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
        <fullName>Update_ReOpen_Date_Field</fullName>
        <field>Re_Open_Case_Date__c</field>
        <formula>IF(ISPICKVAL(Status,&quot;Closed&quot;), null,TODAY() )</formula>
        <name>Update ReOpen Date Field</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Record_Type</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Unknown_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Record Type</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Record_Type_to_Case_Closed</fullName>
        <description>Updates the Case Record Type to Case Closed</description>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Member_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Record Type to Case Closed</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Record_Type_to_Closed_Member_Case</fullName>
        <description>Updates the Case Record Type to Closed Member Case</description>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Member_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Record Type to Closed Member Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_TX_Complaint_Letter_Requested</fullName>
        <description>To update Texas Complaint Letter Requested field when all conditions are met.</description>
        <field>Texas_Complaint_Letter_Requested__c</field>
        <literalValue>1</literalValue>
        <name>Update TX Complaint Letter Requested</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Literal</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Tenant_Id</fullName>
        <field>Tenant_Id__c</field>
        <formula>Policy_Member__r.Policy__r.Group_Name__r.Tenant_Id__c</formula>
        <name>Update Tenant Id</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Agent_Broker_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Agent_Broker_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Agent/Broker Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_Agent_Broker_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Agent_Broker_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed Agent/Broker Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_Group_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Group_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed Group Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_HP_Group_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_HP_Group_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed HP Group Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_HP_Member_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_HP_Member_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed HP Member Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_HP_Provider_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_HP_Provider_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed HP Provider Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_HP_Unknown_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_HP_Unknown_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed HP Unknown Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_Medicare_Case</fullName>
        <description>Update Type to Closed Medicare Case</description>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Medicare_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed Medicare Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_Provider_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Provider_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed Provider Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Closed_Unknown_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Closed_Unknown_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Closed Unknown Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Group_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Group_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Group Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_HP_Agent_Broker_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>HP_Agent_Broker_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to HP Agent/Broker Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_HP_Group_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>HP_Group_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to HP Group Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_HP_Member_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>HP_Member_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to HP Member Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_HP_Provider_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>HP_Provider_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to HP Provider Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_HP_Unknown_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>HP_Unknown_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to HP Unknown Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Medicare_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Medicare_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Medicare Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Member_Case</fullName>
        <description>Updates the Case Record Type to Case Open</description>
        <field>RecordTypeId</field>
        <lookupValue>Member_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Member Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Provider_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Provider_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Provider Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Update_Type_to_Unknown_Case</fullName>
        <field>RecordTypeId</field>
        <lookupValue>Unknown_Case</lookupValue>
        <lookupValueType>RecordType</lookupValueType>
        <name>Update Type to Unknown Case</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>LookupValue</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <fieldUpdates>
        <fullName>Work_Queue_View</fullName>
        <description>Set default value for WorkQueueView</description>
        <field>Work_Queue_View_Name__c</field>
        <formula>&apos;Other&apos;</formula>
        <name>Work Queue View</name>
        <notifyAssignee>false</notifyAssignee>
        <operation>Formula</operation>
        <protected>false</protected>
        <reevaluateOnChange>false</reevaluateOnChange>
    </fieldUpdates>
    <rules>
        <fullName>Agent%2FBroker Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_Agent_Broker_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>Agent/Broker Case</value>
        </criteriaItems>
        <description>When the cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Agent%2FBroker Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Agent_Broker_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed Agent/Broker Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Case Created</fullName>
        <actions>
            <name>Update_Created_By_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Last_Modified_By_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>When a case is created, update the last modified by queue and created by queue fields</description>
        <formula>$Profile.Name != &apos;ETL API Access&apos; &amp;&amp; (NOT($Permission.CRMS_205_PDPPilot_Custom) || ($Permission.CRMS_205_PDPPilot_Custom &amp;&amp; ISBLANK(Created_By_Queue__c) &amp;&amp; ISBLANK(LastModifiedby_Queue__c)))</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>Case Created with No Parent Policy</fullName>
        <actions>
            <name>Set_Tenant_Id_equal_to_parent_Account</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When case is created without a Parent Policy Member record, set the Tenant Id equal to that of the Parent Account of the Case record</description>
        <formula>Policy_Member__c = null</formula>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Case Created with Parent Policy</fullName>
        <actions>
            <name>Update_Tenant_Id</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When case is created with a parent Policy Member record, set the Tenant Id equal to that of the Parent Group Account of the Policy Member&apos;s Policy record</description>
        <formula>Policy_Member__c != null</formula>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Case Owner Queue Field Updated</fullName>
        <actions>
            <name>Update_Case_Owner_Profile</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Case_Previous_Owner_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>Updating case owner profile when Department or Queue is modified.</description>
        <formula>ISCHANGED(Department__c)  ||  ISCHANGED(Topic__c)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>CoKY Case Created</fullName>
        <actions>
            <name>Set_CoKY_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>At time of case creation, if there is a parent Policy Member record, set the CoKY Case flag to True if the Parent Group Account of the Policy Member&apos;s Policy record has the CoKY Account flag set to True, if not then check the parent Account</description>
        <formula>IF(Policy_Member__c != null, Policy_Member__r.Policy__r.Group_Name__r.CoKY_Account__c,  Account.CoKY_Account__c )</formula>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>DST Case Created</fullName>
        <actions>
            <name>Set_DST_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>At time of case creation, if there is a parent Policy Member record, set the DST Case flag to True if the Parent Group Account of the Policy Member&apos;s Policy record has the DST Account flag set to True, if not then check the parent Account</description>
        <formula>IF(Policy_Member__c != null, Policy_Member__r.Policy__r.Group_Name__r.DST_Account__c,  Account.DST_Account__c )</formula>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Date Stamp Reopen Case Date</fullName>
        <actions>
            <name>Update_ReOpen_Date_Field</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>Date Stamp the Reopen Case field</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Den Indicator for TX Complaint Letter Request</fullName>
        <actions>
            <name>Den_Update_TX_Complaint_Letter_Requested</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>Is an indicator to identify case record is TX complaint letter related and a letter will need to be generate to sent out to customer</description>
        <formula>AND( ISPICKVAL(Interacting_About_Type__c,&quot;Member&quot;), ISPICKVAL(Interacting_With_Type__c,&quot;Member&quot;), ISPICKVAL(Status,&quot;In Progress&quot;) || ISPICKVAL(Status,&quot;Pending - Response&quot;), ISPICKVAL(Origin,&quot;Inbound Call&quot;) || ISPICKVAL(Origin,&quot;Web Chat&quot;), (Plan_Issue_State__c = &apos;TX&apos;), ISPICKVAL(Policy_Member__r.Policy__r.ASO__c,&quot;N&quot;), Policy_Member__r.Product__c = &apos;DEN&apos;, Policy_Member__r.Policy__r.Major_LOB__c != &quot;OHBD&quot;, Policy_Member__r.Policy__r.Major_LOB__c != &quot;POSD&quot;, Policy_Member__r.Policy__r.Major_LOB__c != &quot;OPPD&quot;, Policy_Member__r.Policy__r.Major_LOB__c != &quot;SUPPD&quot;, Policy_Member__r.Policy__r.Major_LOB__c != &quot;SUPD&quot;,Policy_Member__r.Policy__r.Major_LOB__c != &quot;OCHD&quot;, ISPICKVAL(G_A_Rights_Given__c,&quot;Yes&quot;) || ISPICKVAL(Complaint__c,&quot;Yes&quot;), ISPICKVAL(Language_Preference__c, &quot;English&quot;)|| ISPICKVAL(Language_Preference__c, &quot;Spanish&quot;), Texas_Complaint_Letter_Requested__c = False)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>General Case Created</fullName>
        <actions>
            <name>Set_General_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>At time of case creation, if there is a parent Policy Member record, set the General Case flag to True if the Parent Group Account of the Policy Member&apos;s Policy record has the General Account flag set to True, if not then check the parent Account</description>
        <formula>IF(Policy_Member__c != null, Policy_Member__r.Policy__r.Group_Name__r.General_Account__c, IF( OR( CONTAINS(RecordType.Name , &apos;Medicare Case&apos;), CONTAINS(RecordType.Name , &apos;Closed Medicare Case&apos;) ) , true, Account.General_Account__c))</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>Group Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_Group_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>Group Case</value>
        </criteriaItems>
        <description>When the cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Group Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Group_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed Group Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Group or Member Case Created</fullName>
        <actions>
            <name>Set_General_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Set_Tenant_ID_to_Humana</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Account.RecordTypeId</field>
            <operation>equals</operation>
            <value>Group,Unknown Group,Member,Unknown Member</value>
        </criteriaItems>
        <description>At time of case creation, if the Interacting About is a Group, Unknown Group, Member, Unknown Member, set the Tenant ID equal to Humana (00) and General Case flag equal to TRUE.</description>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>HO Dental Case Created</fullName>
        <actions>
            <name>Set_HO_Dental_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>At time of case creation, if there is a parent Policy Member record, set the HO Dental Case flag to True if the Parent Group Account of the Policy Member&apos;s Policy record has the HO Account flag set to True and it is a dental product type</description>
        <formula>AND (IF(Policy_Member__c != null, AND(Policy_Member__r.Policy__r.Group_Name__r.Home_Office_Account__c,  IF(CASE(Policy_Member__r.Policy__r.Product__r.ProductCode, &quot;ADD&quot;, 1, &quot;DEN&quot;, 1, &quot;Supplemental Critical Illness&quot;, 1, &quot;WVB AD&amp;D Group Voluntary&quot;, 1 , &quot;WVB Supplemental Health-Group&quot;, 1, &quot;DPL&quot;, 1, &quot;ACCHLTH&quot;, 1, &quot;CRT&quot;, 1, &quot;CRTL&quot;, 1, &quot;DENTAL&quot;, 1, &quot;STOPLOSS&quot;, 1, &quot;VBN&quot;, 1, &quot;VLF&quot;, 1, &quot;VOLBEN&quot;, 1, &quot;VOLLIFE&quot;, 1, &quot;LIF&quot;, 1, &quot;LTD&quot;, 1, &quot;LIFE&quot;, 1, &quot;LTC&quot;, 1, &quot;OTH&quot;, 1, &quot;STD&quot;, 1, &quot;UNKNOWN&quot;, 1, &quot;VIS&quot;, 1, &quot;VISION&quot;, 1, &quot;WHOLE LIFE TERM&quot;, 1, &quot;WKC&quot;, 1, 0) = 1, TRUE, FALSE)), Account.Home_Office_Account__c ),NOT(OR( $Profile.Name = &apos;Humana Pharmacy Specialist&apos;,  $Permission.CRMS_300_HP_Supervisor_Custom )))</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>HO Medical Case Created</fullName>
        <actions>
            <name>Set_HO_Medical_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>At time of case creation, if there is a parent Policy Member record, set the HO Medical Case flag to True if the Parent Group Account of the Policy Member&apos;s Policy record has the HO Account flag set to True and it is a medical product type</description>
        <formula>IF(Policy_Member__c != null, AND(Policy_Member__r.Policy__r.Group_Name__r.Home_Office_Account__c,  IF(CASE(Policy_Member__r.Policy__r.Product__r.ProductCode, &quot;DRG&quot;, 1, &quot;FSA&quot;, 1, &quot;Supplemental Critical Illness&quot;, 1, &quot;WVB AD&amp;D Group Voluntary&quot;, 1 , &quot;WVB Supplemental Health-Group&quot;, 1, &quot;HLT&quot;, 1, &quot;MED&quot;, 1, &quot;RX&quot;, 1, &quot;CLINICAL&quot;, 1,  &quot;HSA&quot;, 1,&quot;SAB&quot;, 1, &quot;MEDICAL&quot;, 1, &quot;LIF&quot;, 1, &quot;LTD&quot;, 1, &quot;LIFE&quot;, 1, &quot;LTC&quot;, 1, &quot;OTH&quot;, 1, &quot;STD&quot;, 1, &quot;UNKNOWN&quot;, 1, &quot;VIS&quot;, 1, &quot;VISION&quot;, 1, &quot;WHOLE LIFE TERM&quot;, 1, &quot;WKC&quot;, 1, 0) = 1, TRUE, FALSE)), Account.Home_Office_Account__c )</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>HP Agent%2FBroker Case Closed</fullName>
        <actions>
            <name>UpdateType_to_Closed_HP_Agent_BrokerCase</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>HP Agent/Broker Case</value>
        </criteriaItems>
        <description>When the Humana Pharmacy Cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>HP Agent%2FBroker Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_HP_Agent_Broker_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the Humana Pharmacy Cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed HP Agent/Broker Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>HP Group Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_HP_Group_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>HP Group Case</value>
        </criteriaItems>
        <description>When the Humana Pharmacy Cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>HP Group Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_HP_Group_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the Humana Pharmacy Cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed HP Group Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>HP Member Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_HP_Member_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>HP Member Case</value>
        </criteriaItems>
        <description>When the Humana Pharmacy Cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>HP Member Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_HP_Member_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the Humana Pharmacy Cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed HP Member Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>HP Provider Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_HP_Provider_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>HP Provider Case</value>
        </criteriaItems>
        <description>When the Humana Pharmacy Cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>HP Provider Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_HP_Provider_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the Humana Pharmacy Cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed HP Provider Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>HP Unknown Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_HP_Unknown_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>HP Unknown Case</value>
        </criteriaItems>
        <description>When the Humana Pharmacy Cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>HP Unknown Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_HP_Unknown_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the Humana Pharmacy Cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed HP Unknown Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Indicator for TX Complaint Letter Request</fullName>
        <actions>
            <name>Update_TX_Complaint_Letter_Requested</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>Is an indicator to identify case record is TX complaint letter related and a letter will need to be generate to sent out to customer</description>
        <formula>AND(             ISPICKVAL(Interacting_About_Type__c,&quot;Member&quot;), ISPICKVAL(Interacting_With_Type__c,&quot;Member&quot;), ISPICKVAL(Status,&quot;In Progress&quot;) ||  ISPICKVAL(Status,&quot;Pending - Response&quot;), ISPICKVAL(Origin,&quot;Inbound Call&quot;) || ISPICKVAL(Origin,&quot;Web Chat&quot;), (Plan_Issue_State__c = &apos;TX&apos;),  ISPICKVAL(Policy_Member__r.Policy__r.ASO__c,&quot;N&quot;), Policy_Member__r.Product__c = &apos;MED&apos;, (Policy_Member__r.Policy__r.Major_LOB__c = &quot;HMO&quot; || Policy_Member__r.Policy__r.Major_LOB__c = &quot;HML&quot; || Policy_Member__r.Policy__r.Major_LOB__c = &quot;POS&quot; || Policy_Member__r.Policy__r.Major_LOB__c = &quot;NPOS&quot; || Policy_Member__r.Policy__r.Major_LOB__c = &quot;PPO&quot;),  ISPICKVAL(G_A_Rights_Given__c,&quot;Yes&quot;) || ISPICKVAL(Complaint__c,&quot;Yes&quot;),  ISPICKVAL(Language_Preference__c, &quot;English&quot;)||  ISPICKVAL(Language_Preference__c, &quot;Spanish&quot;), Texas_Complaint_Letter_Requested__c = False)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Medicare Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_Medicare_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>Medicare Case</value>
        </criteriaItems>
        <description>When the cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Medicare Case Reopen</fullName>
        <actions>
            <name>Check_Medicare_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Medicare_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True, RecordType.Name == &apos;Closed Medicare Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Member Case Closed</fullName>
        <actions>
            <name>Update_Record_Type_to_Closed_Member_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>Member Case</value>
        </criteriaItems>
        <description>When the cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Member Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Member_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed Member Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>OGO AutoRoute Closed Case BackTo InProgress Status</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>OGO_ChangeCaseStatus</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Member_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>OGO Routing Rules: Ability to automatically update case status to In Progress if it is a Complaint.</description>
        <formula>AND(       ISPICKVAL( Status , &apos;Closed&apos;),       ISCHANGED( Owner_Queue__c),       Owner_Queue__c  = &apos;Louisville RSO Oral Grievance Oversight&apos;,       RecordType.Name == &apos;Closed Member Case&apos;,       ISPICKVAL( Autoroute_Status__c , &quot;Routed Pending Accept&quot;)        )</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Onshore Contract Data Protected Case Created</fullName>
        <actions>
            <name>Set_Contract_Protected_Case_to_1</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <formula>IF(Policy_Member__c != null,       ISPICKVAL(Policy_Member__r.Policy__r.Group_Name__r.Contract_Protected_Data__c, &apos;1&apos;),       IF(ISPICKVAL( Account.Contract_Protected_Data__c , &apos;1&apos;),true,false))</formula>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>ProductTypeCaseSharing</fullName>
        <actions>
            <name>UpdateProductTypeBasedSharing</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>This workflow Rule is use to update boolean field to true .</description>
        <formula>AND( CONTAINS(&quot;F23:F22:W23:F35:W28:F27:W27:F25:F26:W26:W24:F36:F33:W33:F34:W34:W22&quot;,IF(Policy_Member__r.Policy__r.Product__r.Name = &apos;VIS&apos;, Policy_Member__r.Policy__r.Product__r.Product_Type__c,  Policy_Member__r.Policy__r.Major_LOB__c )), Account.RecordType.DeveloperName = &apos;Member&apos;, Account.Security_Groups__c = &apos;Home Office&apos; )</formula>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Provider Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_Provider_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>Provider Case</value>
        </criteriaItems>
        <description>When the cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Provider Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Provider_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed Provider Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Provider or Agent Case Created</fullName>
        <actions>
            <name>Set_General_Case_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <criteriaItems>
            <field>Account.RecordTypeId</field>
            <operation>equals</operation>
            <value>Provider,Agent/Broker,Unknown Agent/Broker,Unknown Provider</value>
        </criteriaItems>
        <description>At time of case creation, if the Interacting About is a Provider, Unknown Provider, Agent/Broker, Unknown Agent/Broker, set the Tenant ID equal to Humana (00) and General Case flag equal to TRUE.</description>
        <triggerType>onCreateOnly</triggerType>
    </rules>
    <rules>
        <fullName>Trigger to Send Email to Customer</fullName>
        <actions>
            <name>Send_TX_Complaint_Letter_to_Customer</name>
            <type>Alert</type>
        </actions>
        <actions>
            <name>Email_Sent</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <formula>Apply_to_Complaint__c = True &amp;&amp;  ISPICKVAL (Language_Preference__c, &quot;English&quot;) || ISPICKVAL (Language_Preference__c, &quot;Spanish&quot;) &amp;&amp; Email_Sent__c = False</formula>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Unknown Case Closed</fullName>
        <actions>
            <name>Update_Type_to_Closed_Unknown_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <criteriaItems>
            <field>Case.IsClosed</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.RecordTypeId</field>
            <operation>equals</operation>
            <value>Unknown Case</value>
        </criteriaItems>
        <description>When the cases status is changed from Open Status to any Closed Status, the Case Record Type will be updated to Case Closed</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Unknown Case Reopen</fullName>
        <actions>
            <name>Check_Reopened_Flag</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Update_Type_to_Unknown_Case</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <description>When the cases status is changed from Close Status to any Open Status, the Case Record Type will be updated to Case Open and the Reopened field will be updated to True</description>
        <formula>AND(ISCHANGED(IsClosed), PRIORVALUE(IsClosed)== True,  RecordType.Name == &apos;Closed Unknown Case&apos;)</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
    <rules>
        <fullName>Update Associate Msg Field</fullName>
        <actions>
            <name>Update_Associate_Msg_Field</name>
            <type>FieldUpdate</type>
        </actions>
        <active>false</active>
        <booleanFilter>(1 AND 2) OR 3</booleanFilter>
        <criteriaItems>
            <field>Case.Apply_to_Complaint__c</field>
            <operation>equals</operation>
            <value>True</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.Language_Preference__c</field>
            <operation>equals</operation>
            <value>English</value>
        </criteriaItems>
        <criteriaItems>
            <field>Case.Language_Preference__c</field>
            <operation>equals</operation>
            <value>Spanish</value>
        </criteriaItems>
        <description>Notifying message to associate that complaint letter will generate and send to customer after case is saved.</description>
        <triggerType>onCreateOrTriggeringUpdate</triggerType>
    </rules>
    <rules>
        <fullName>Wellness Care Plan</fullName>
        <actions>
            <name>Case_Owner</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Department</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Owner_Queue</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Service_Center</name>
            <type>FieldUpdate</type>
        </actions>
        <actions>
            <name>Work_Queue_View</name>
            <type>FieldUpdate</type>
        </actions>
        <active>true</active>
        <description>Updated default value sfor CarePlan cases created for Wellness App</description>
        <formula>$Profile.Name  = &apos;Humana Wellness Coach&apos;  ||  $Profile.Name = &apos;Humana Wellness Manager&apos;</formula>
        <triggerType>onAllChanges</triggerType>
    </rules>
</Workflow>