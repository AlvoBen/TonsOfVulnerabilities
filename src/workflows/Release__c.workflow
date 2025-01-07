<?xml version="1.0" encoding="UTF-8"?>
<Workflow xmlns="http://soap.sforce.com/2006/04/metadata">
    <alerts>
        <fullName>IT_Order_of_Magnitude_Email_Notification</fullName>
        <description>Email Notification Sent to IT Architect notifying them that an Order of Magnitude estimate is needed.</description>
        <protected>false</protected>
        <recipients>
            <recipient>vbatham@humana.com</recipient>
            <type>user</type>
        </recipients>
        <senderType>CurrentUser</senderType>
        <template>BME_Templates/IT_Order_of_Magnitude_Assignment</template>
    </alerts>
    <tasks>
        <fullName>IT_Order_of_Magnitude_Estimate</fullName>
        <assignedTo>vbatham@humana.com</assignedTo>
        <assignedToType>user</assignedToType>
        <description>The IT Order of Magnitude Estimate will occur after the Champion/Sponsor approves the Project Request.</description>
        <dueDateOffset>5</dueDateOffset>
        <notifyAssignee>true</notifyAssignee>
        <priority>High</priority>
        <protected>false</protected>
        <status>Not Started</status>
        <subject>IT Order of Magnitude Estimate</subject>
    </tasks>
    <tasks>
        <fullName>IT_Order_of_Magnitude_Estimate1</fullName>
        <assignedTo>CEO</assignedTo>
        <assignedToType>role</assignedToType>
        <description>The IT Order of Magnitude Estimate will occur after the Champion/Sponsor approves the Project Request.</description>
        <dueDateOffset>5</dueDateOffset>
        <notifyAssignee>true</notifyAssignee>
        <priority>Normal</priority>
        <protected>false</protected>
        <status>Not Started</status>
        <subject>IT Order of Magnitude Estimate</subject>
    </tasks>
    <tasks>
        <fullName>IT_Order_of_Magnitude_Estimate_New</fullName>
        <assignedTo>IT_Architects</assignedTo>
        <assignedToType>role</assignedToType>
        <description>A Task has been added for an IT Architect to provide an IT Order of Magnitude Estimate for the Project</description>
        <dueDateOffset>5</dueDateOffset>
        <notifyAssignee>true</notifyAssignee>
        <priority>Normal</priority>
        <protected>false</protected>
        <status>Not Started</status>
        <subject>IT Order of Magnitude Estimate</subject>
    </tasks>
    <tasks>
        <fullName>Order_of_Magnitude_Review</fullName>
        <assignedTo>Architecture_Program_Manager</assignedTo>
        <assignedToType>role</assignedToType>
        <description>A new IT Order of Magnitude Estimate has been added to your queue. Please review within 5 days.</description>
        <dueDateOffset>5</dueDateOffset>
        <notifyAssignee>true</notifyAssignee>
        <priority>Normal</priority>
        <protected>false</protected>
        <status>Not Started</status>
        <subject>Order of Magnitude Review</subject>
    </tasks>
</Workflow>
