/****************************************************************************
Apex Class Name  : HUMUpdateCaseTaskHelperTest
Version          : 1.0 
Created On       : 9/23/2014
Function         : Test class to HUMUpdateCaseTaskHelper apex class

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mrudula Jaddu                17996                    9/23/2014                  Original Version
* Mrudula Jaddu                19514                    11/26/2014                 Defect 162663 - code changed as per CC #19514 defect. 
* Santhi Mandava                                        02/24/2015                 As per req-02635, we have created MD relationship between work queue 
set up and queue view,Work queue priority objects so modified code to include new fields.
* Avinash Choubey              34551                    08/04/2016                 defect 235634 fix    
* Sharan Shanmugam                                      11/01/2016                 Modified the test class to increase code coverage
* Sharan Shanmugam             50226                    11/30/2106                 Implemented REQ - 290990 -  removed all lookup related fields from 
Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
* Avinash Choubey                                       12/20/2016                 REQ 296640       ( added the IsActive__c= true in WQ and QV records and passed active queue names in initializeListAndMaps method)
* Harshith Mandya                                       29/12/2016                 REQ - 296640
* Pallavi ravishankar          98201                    05/31/2017                 Implemented REQ 318433 - changed classification And intent id references to CTCI references
* Pradeepkumar Dani            99570                    06/05/2017                 Intent__c.Classification__c & Intent__c.Etl_Idtfr__c Clean up
* Pradeepkumar Dani           102268                    06/16/2017                 Case Clean Up (Classification__c,Intent__c,Classification_Ctci__c,Intent_Ctci__c,Classification_Text__c,Intent_Text__c)
* Suresh Sunkari                                        07/10/2018                 REQ 357690 To get Criteria Value from Custom Setting
* Shailesh Mali                                         08/09/2019                 REQ - 405958 WorkQueueView Optimization
* Jasmeen Shangari                                      02/08/2021                  Azure ID 1790946 - Fix for Incident - INC0806755
* Mithra Bharadwaj                                      02/09/2021                  US#1522825 SonarQube Changes
*******************************************************************************************************************************/

@isTest
private class HUMUpdateCaseTaskHelperTest
{
    /**
* Test Setup method to create all the data required for testing.
*
* @param     NULL
* @return    void 
*/
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('HUMUpdateCRMUser1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU';
        insert oUser;
        system.assertEquals(oUser.Current_Queue__c,'Cincinnati CRU');  
        
        User oUser1 = HUMTestDataHelperTest.createTestUser('HUMUpdateCRMUser2','Customer Care Specialist');
        oUser1.Current_Queue__c = 'Louisville RSO Enrollment';
        insert oUser1;
        
        
        system.runAs(oUser)
        {
            
            Work_Queue_Priority_CriteriaLimit_Value__C cv = new Work_Queue_Priority_CriteriaLimit_Value__C();
            cv.name = 'Criteria';
            cv.Value__C = 22;
            insert cv;
            
            Work_Queue_Setup__c oWQS = new Work_Queue_Setup__c(name ='Cincinnati Calls', IsActive__c= TRUE);
            insert oWQS ;
            system.assert(oWQS.name =='Cincinnati Calls'); 
            
            Work_Queue_Setup__c oWQS1 = new Work_Queue_Setup__c(name ='Cincinnati CRU',IsActive__c= TRUE);
            insert oWQS1 ;
            system.assert(oWQS1.name =='Cincinnati CRU');
            
            Work_Queue_Setup__c oWQS2 = new Work_Queue_Setup__c(name ='Louisville RSO Enrollment', IsActive__c= TRUE);
            insert oWQS2 ;            
            
            List<Queue_View__c> lstQueueView = new List<Queue_View__c>();
            Queue_View__c oQv1 = new Queue_View__c(Queue_View_Name__c='Other11',Expression__c='((true)OR(true))OR((false)AND(true))', Department__c='CRU',Work_Queue_Name__c=oWQS.id, Queue_Name__c='Cincinnati CRU', Service_Center__c='Cincinnati',IsActive__c= TRUE); 
            lstQueueView.add(oQv1);
            
            Queue_View__c oQv2 = new Queue_View__c(Queue_View_Name__c='Other111',Expression__c='((true OR false) AND (true))', Department__c='CRU', Work_Queue_Name__c=oWQS.id,Queue_Name__c='Cincinnati CRU', Service_Center__c='Cincinnati',IsActive__c= TRUE); 
            lstQueueView.add(oQv2);
            
            Queue_View__c oQv3 = new Queue_View__c(Queue_View_Name__c='Other123',Expression__c='((true OR false) AND (true))', Department__c='CRU', Work_Queue_Name__c=oWQS.id,Queue_Name__c='Cincinnati CRU', Service_Center__c='Cincinnati',IsActive__c= TRUE); 
            lstQueueView.add(oQv3);
            
            Queue_View__c oQv4 = new Queue_View__c(Queue_View_Name__c='Other1234',Expression__c='((true)OR(true))', Department__c='CRU', Work_Queue_Name__c=oWQS1.id,Queue_Name__c='Cincinnati CRU', Service_Center__c='Cincinnati',IsActive__c= TRUE); 
            lstQueueView.add(oQv4);
            
            Queue_View__c oQv5 = new Queue_View__c(Queue_View_Name__c='Other12345', Department__c='CRU', Work_Queue_Name__c=oWQS.id,Queue_Name__c='Cincinnati CRU', Service_Center__c='Cincinnati',IsActive__c= TRUE); 
            lstQueueView.add(oQv5);
            
            //For Louisville RSO Enenrollment
            Queue_View__c oQv6 = new Queue_View__c(Queue_View_Name__c='Other1', Expression__c = '((true) AND (true)) OR ((false))', Queue_Name__c='Exp1', Service_Center__c='Louisville', Department__c='Retail Service Operations (RSO) Enrollment', Work_Queue_Name__c=oWQS2.id, IsActive__c= TRUE); 
            lstQueueView.add(oQv6);
            
            Queue_View__c oQv7 = new Queue_View__c(Queue_View_Name__c='Other2',Expression__c = '((true) AND (true)) OR ((false))', Queue_Name__c='Exp2', Service_Center__c='Louisville', Department__c='Retail Service Operations (RSO) Enrollment', Work_Queue_Name__c=oWQS2.id, IsActive__c= TRUE); 
            lstQueueView.add(oQv7);
            
            Queue_View__c oQv8 = new Queue_View__c(Queue_View_Name__c='Other3',  Queue_Name__c='WithoutExp1', Service_Center__c='Louisville', Department__c='Retail Service Operations (RSO) Enrollment', Work_Queue_Name__c=oWQS2.id, IsActive__c= TRUE);  
            lstQueueView.add(oQv8);
            
            Queue_View__c oQv9 = new Queue_View__c(Queue_View_Name__c='Other4',  Queue_Name__c='WithoutExp2', Service_Center__c='Louisville', Department__c='Retail Service Operations (RSO) Enrollment', Work_Queue_Name__c=oWQS2.id, IsActive__c= TRUE); 
            lstQueueView.add(oQv9);
            
            Queue_View__c oQv10 = new Queue_View__c(Queue_View_Name__c='Other5',Expression__c = '((true) AND (true)) AND ((true))', Queue_Name__c='Exp3', Service_Center__c='Louisville', Department__c='Retail Service Operations (RSO) Enrollment', Work_Queue_Name__c=oWQS2.id, IsActive__c= TRUE); 
            lstQueueView.add(oQv10);
            
            Queue_View__c oQv11 = new Queue_View__c(Queue_View_Name__c='Other6', Queue_Name__c='WitoutExp3', Service_Center__c='Louisville', Department__c='Retail Service Operations (RSO) Enrollment', Work_Queue_Name__c=oWQS2.id, IsActive__c= TRUE); 
            lstQueueView.add(oQv11);
            insert lstQueueView;
            
            system.assert(lstQueueView[3].Queue_View_Name__c == 'Other1234');
            
            List<Queue_View_Item__c> lstQWI = new List<Queue_View_Item__c>();
            Queue_View_Item__c oQueueViewRCOther1 = new Queue_View_Item__c();
            oQueueViewRCOther1.API_Name__c = 'Classification';
            oQueueViewRCOther1.Field_Operator__c = 'Equals';
            oQueueViewRCOther1.Field_Value__c = 'Disenrollment';
            oQueueViewRCOther1.Queue_View__c = oQv6.Id;            
            lstQWI.add(oQueueViewRCOther1);            
            
            Queue_View_Item__c oQueueViewRCOther11 = new Queue_View_Item__c();
            oQueueViewRCOther11.API_Name__c = 'Intent';
            oQueueViewRCOther1.Field_Operator__c = 'Equals';
            oQueueViewRCOther11.Field_Value__c = 'Rekey';
            oQueueViewRCOther11.Queue_View__c = oQv6.Id;            
            lstQWI.add(oQueueViewRCOther11);            
            
            Queue_View_Item__c oQueueViewRCOther111 = new Queue_View_Item__c();
            oQueueViewRCOther111.API_Name__c = 'PreviousOwnerQueue';
            oQueueViewRCOther111.Field_Operator__c = 'Equals';
            oQueueViewRCOther111.Field_Value__c = 'Louisville RSO Enrollment';
            oQueueViewRCOther111.Queue_View__c = oQv6.Id;            
            lstQWI.add(oQueueViewRCOther111);            
            
            Queue_View_Item__c oQueueViewRCOther2 = new Queue_View_Item__c();
            oQueueViewRCOther2.API_Name__c = 'Classification';
            oQueueViewRCOther2.Field_Operator__c = 'Equals';
            oQueueViewRCOther2.Field_Value__c = 'Disenrollment';
            oQueueViewRCOther2.Queue_View__c = oQv7.Id;            
            lstQWI.add(oQueueViewRCOther2);            
            
            Queue_View_Item__c oQueueViewRCOther22 = new Queue_View_Item__c();
            oQueueViewRCOther22.API_Name__c = 'Intent';
            oQueueViewRCOther22.Field_Operator__c = 'Equals';
            oQueueViewRCOther11.Field_Value__c = 'Rekey';
            oQueueViewRCOther22.Queue_View__c = oQv7.Id;            
            lstQWI.add(oQueueViewRCOther22);            
            
            Queue_View_Item__c oQueueViewRCOther222 = new Queue_View_Item__c();
            oQueueViewRCOther222.API_Name__c = 'PreviousOwnerQueue';
            oQueueViewRCOther222.Field_Operator__c = 'Equals';
            oQueueViewRCOther222.Field_Value__c = 'Louisville RSO Enrollment';
            oQueueViewRCOther222.Queue_View__c = oQv7.Id;            
            lstQWI.add(oQueueViewRCOther222);           
            
            Queue_View_Item__c oQueueViewROther3 = new Queue_View_Item__c();
            oQueueViewROther3.API_Name__c = 'OwnerQueue';
            oQueueViewROther3.Field_Operator__c = 'Equals';
            oQueueViewROther3.Field_Value__c = 'Louisville RSO Enrollment';
            oQueueViewROther3.Queue_View__c = oQv8.Id;            
            lstQWI.add(oQueueViewROther3);    

            Queue_View_Item__c oQueueViewROther312 = new Queue_View_Item__c();
            oQueueViewROther312.API_Name__c = 'Topic';
            oQueueViewROther312.Field_Operator__c = 'Not Equals';
            oQueueViewROther312.Field_Value__c = 'Special Needs Plan';
            oQueueViewROther312.Queue_View__c = oQv8.Id;            
            lstQWI.add(oQueueViewROther312); 			
            
            Queue_View_Item__c oQueueViewROther4 = new Queue_View_Item__c();
            oQueueViewROther4.API_Name__c = 'Classification';
            oQueueViewROther4.Field_Operator__c = 'Not Equals';
            oQueueViewROther4.Field_Value__c = 'Disenrollment';
            oQueueViewROther4.Queue_View__c = oQv9.Id;            
            lstQWI.add(oQueueViewROther4);
            
            Queue_View_Item__c oQueueViewROther5 = new Queue_View_Item__c();
            oQueueViewROther5.API_Name__c = 'Classification';
            oQueueViewROther5.Field_Operator__c = 'Equals';
            oQueueViewROther5.Field_Value__c = 'claims';
            oQueueViewROther5.Queue_View__c = oQv10.Id;            
            lstQWI.add(oQueueViewROther5);            
            
            Queue_View_Item__c oQueueViewROther55 = new Queue_View_Item__c();
            oQueueViewROther55.API_Name__c = 'Topic';
            oQueueViewROther55.Field_Operator__c = 'Not Equals';
            oQueueViewROther55.Field_Value__c = 'Special Needs Plan';
            oQueueViewROther55.Queue_View__c = oQv10.Id;            
            lstQWI.add(oQueueViewROther55);            
            
            Queue_View_Item__c oQueueViewROther555 = new Queue_View_Item__c();
            oQueueViewROther555.API_Name__c = 'OwnerQueue';
            oQueueViewROther555.Field_Operator__c = 'Equals';
            oQueueViewROther555.Field_Value__c  = 'Louisville RSO Enrollment';
            oQueueViewROther555.Queue_View__c = oQv10.Id;            
            lstQWI.add(oQueueViewROther555);            
            
            Queue_View_Item__c oQueueViewRC6 = new Queue_View_Item__c();
            oQueueViewRC6.API_Name__c = 'Classification';
            oQueueViewRC6.Field_Operator__c = 'Equals';
            oQueueViewRC6.Field_Value__c = 'Disenrollment';
            oQueueViewRC6.Queue_View__c = oQv11.Id;            
            lstQWI.add(oQueueViewRC6); 
            
            Queue_View_Item__c oQueueViewRC66 = new Queue_View_Item__c();
            oQueueViewRC66.API_Name__c = 'Intent';
            oQueueViewRC66.Field_Operator__c = 'Equals';
            oQueueViewRC66.Field_Value__c = 'Rekey';
            oQueueViewRC66.Queue_View__c = oQv11.Id;            
            lstQWI.add(oQueueViewRC66);    
            
            Queue_View_Item__c oQueueViewRC666 = new Queue_View_Item__c();
            oQueueViewRC666.API_Name__c = 'OwnerQueue';
            oQueueViewRC666.Field_Operator__c = 'Equals';
            oQueueViewRC666.Field_Value__c  = 'Louisville RSO Enrollment';
            oQueueViewRC666.Queue_View__c = oQv11.Id;            
            lstQWI.add(oQueueViewRC666);    
            
            Queue_View_Item__c oQueueViewRC11 = new Queue_View_Item__c();
            oQueueViewRC11.API_Name__c = 'OwnerQueue';
            oQueueViewRC11.Field_Operator__c = 'Equals';
            oQueueViewRC11.Field_Value__c  = 'Louisville RSO Enrollment';
            oQueueViewRC11.Queue_View__c = oQv2.Id;            
            lstQWI.add(oQueueViewRC11);            
            
            Queue_View_Item__c oQueueViewRC12 = new Queue_View_Item__c();
            oQueueViewRC12.API_Name__c = 'Intent';
            oQueueViewRC12.Field_Operator__c = 'Equals';
            oQueueViewRC12.Field_Value__c = 'Rekey';
            oQueueViewRC12.Queue_View__c = oQv2.Id;            
            lstQWI.add(oQueueViewRC12); 
            
            Queue_View_Item__c oQueueViewRC13 = new Queue_View_Item__c();
            oQueueViewRC13.API_Name__c = 'Classification';
            oQueueViewRC13.Field_Operator__c = 'Equals';
            oQueueViewRC13.Field_Value__c = 'Disenrollment';
            oQueueViewRC13.Queue_View__c = oQv2.Id;            
            lstQWI.add(oQueueViewRC13); 
            
            Queue_View_Item__c oQueueViewRC21 = new Queue_View_Item__c();
            oQueueViewRC21.API_Name__c = 'OwnerQueue';
            oQueueViewRC21.Field_Operator__c = 'Not Equals';
            oQueueViewRC21.Field_Value__c = 'NA';
            oQueueViewRC21.Queue_View__c = oQv3.Id;            
            lstQWI.add(oQueueViewRC21);            
            
            Queue_View_Item__c oQueueViewRC22 = new Queue_View_Item__c();
            oQueueViewRC22.API_Name__c = 'Intent';
            oQueueViewRC22.Field_Operator__c = 'Equals';
            oQueueViewRC22.Field_Value__c = 'Rekey';
            oQueueViewRC22.Queue_View__c = oQv3.Id;            
            lstQWI.add(oQueueViewRC22); 
            
            Queue_View_Item__c oQueueViewR23 = new Queue_View_Item__c();
            oQueueViewR23.API_Name__c = 'Classification';
            oQueueViewR23.Field_Operator__c = 'Not Equals';
            oQueueViewR23.Field_Value__c = 'Disenrollment';
            oQueueViewR23.Queue_View__c = oQv3.Id;            
            lstQWI.add(oQueueViewR23); 
            
            Queue_View_Item__c oQueueView = new Queue_View_Item__c();
            oQueueView.API_Name__c = 'CaseStatus';
            oQueueView.Field_Operator__c = 'Equals';
            oQueueView.Field_Value__c = 'In Progress';
            oQueueView.Queue_View__c = oQv4.Id;            
            lstQWI.add(oQueueView); 
            
            Queue_View_Item__c oQueueView7 = new Queue_View_Item__c();
            oQueueView7.API_Name__c = 'PlanIssueState';
            oQueueView7.Field_Operator__c = 'Equals';
            oQueueView7.Field_Value__c = 'IL, TX';
            oQueueView7.Queue_View__c = oQv1.Id;                        
            lstQWI.add(oQueueView7);  
            
            Queue_View_Item__c oQueueView2 = new Queue_View_Item__c();
            // modified field from product to product type as part of defect 154260                   
            oQueueView2.API_Name__c = 'ProductType';
            oQueueView2.Field_Operator__c = 'Equals';
            oQueueView2.Field_Value__c = 'Mediclaim';
            oQueueView2.Queue_View__c = oQv1.Id;            
            lstQWI.add(oQueueView2);
            
            Queue_View_Item__c oQueueView31 = new Queue_View_Item__c();
            oQueueView31.API_Name__c = 'Intent';
            oQueueView31.Field_Operator__c = 'Equals';
            oQueueView31.Field_Value__c = 'Filling';
            oQueueView31.Queue_View__c = oQv5.Id;                        
            lstQWI.add(oQueueView31);  
            
            Queue_View_Item__c oQueueView32 = new Queue_View_Item__c();
            oQueueView32.API_Name__c = 'Classification';
            oQueueView32.Field_Operator__c = 'Equals';
            oQueueView32.Field_Value__c = 'Claims';
            oQueueView32.Queue_View__c = oQv5.Id;                        
            lstQWI.add(oQueueView32);  
            insert lstQWI;      
            system.assert(lstQWI[0].API_Name__c == 'Classification');
            
            Work_Queue_Priority__c oWFP = new Work_Queue_Priority__c();
            oWFP.Criteria_1__c='CR';
            oWFP.Work_Queue_Name__c= oWQS.id;
            insert oWFP;  
            system.assert(oWFP.Criteria_1__c == 'CR'); 
            
            list<Work_Queue_Priority__c>  lstWQP = new list<Work_Queue_Priority__c>();
            for(integer i = 0; i < 2; i++){
                Work_Queue_Priority__c oWQP = new Work_Queue_Priority__c();
                if(i == 0){
                    oWQP.Work_Queue_Name__c = oWQS1.id; 
                }
                
                if( i == 1){
                    oWQP.Work_Queue_Name__c = oWQS2.id; 
                }
                oWQP.Criteria_1__c = 'ProductType';
                oWQP.Criteria_2__c = 'FundingType';
                oWQP.Criteria_3__c = 'PlanSegmentSize';
                oWQP.Criteria_4__c = 'PlanIssueState';
                oWQP.Criteria_5__c = 'StateMemResidAdd';
                oWQP.Criteria_6__c = 'EmployerGroupName'; 
                oWQP.Criteria_7__c = 'PolicyPlatform';
                oWQP.Criteria_8__c = 'Type';
                oWQP.Criteria_9__c = 'Subtype';
                oWQP.Criteria_10__c = 'CaseStatus';
                oWQP.Criteria_11__c = 'TaskStatus';
                oWQP.Criteria_12__c = 'InteractingWithType';
                oWQP.Criteria_13__c = 'PreviousOwnerQueue';
                oWQP.Criteria_14__c = 'PreviousCaseOwnerRole';
                oWQP.Criteria_15__c = 'OwnerQueue';
                oWQP.Criteria_16__c = 'Classification';
                oWQP.Criteria_17__c = 'Intent';
                oWQP.Criteria_18__c = 'Topic';
                oWQP.Criteria_19__c = 'Product Code';
                oWQP.Criteria_20__c = 'ShippingStateCode';
                oWQP.Criteria_21__c = 'BillingStateCode';
                oWQP.Criteria_22__c = 'Behavioral Health';
                oWQP.Priority_1__c = '6';
                oWQP.Priority_2__c = '7';
                oWQP.Priority_3__c = '5';
                oWQP.Priority_4__c = '8';
                oWQP.Priority_5__c = '9';
                oWQP.Priority_6__c = '10';
                oWQP.Priority_7__c = '11';
                oWQP.Priority_8__c = '12';
                oWQP.Priority_9__c = '13';
                oWQP.Priority_10__c = '14';
                oWQP.Priority_11__c = '15';
                oWQP.Priority_12__c = '16';
                oWQP.Priority_13__c = '4';
                oWQP.Priority_14__c = '17';
                oWQP.Priority_15__c = '18';
                oWQP.Priority_16__c = '1';
                oWQP.Priority_17__c = '2';
                oWQP.Priority_18__c = '3';  
                oWQP.Priority_19__c = '19';
                oWQP.Priority_20__c = '20';
                oWQP.Priority_21__c = '21';
                oWQP.Priority_22__c = '22';
                 lstWQP.add(oWQP);
            }
            if(!lstWQP.isempty()) insert lstWQP;
            HUMQueueViewBatchSettings__c oHumview = new HUMQueueViewBatchSettings__c();
            oHumview.name='QueueViewBatchSetup';
            oHumview.TriggerExecutionRequired__c= true;
            oHumview.BatchExecutionDate__c = datetime.valueof(system.now());
            insert oHumview;
            system.assert(oHumview.name == 'QueueViewBatchSetup');  
            
            List<HUMConstants__c> lstConstanst=new List<HUMConstants__c>();
            lstConstanst.Add(new HUMConstants__c(Name='HUMConstantOne',IntegerValue__c=1,StringValue__c='1'));
            lstConstanst.Add(new HUMConstants__c(Name='HUMLISTSIZEZERO',IntegerValue__c=0,StringValue__c='0'));
            lstConstanst.Add(new HUMConstants__c(Name='HUMViewCriteriaLimit',IntegerValue__c=22,StringValue__c=''));
            lstConstanst.Add(new HUMConstants__c(Name='HUMDefaultBatchLimit',IntegerValue__c=5,StringValue__c=''));
            lstConstanst.Add(new HUMConstants__c(Name='HUMAllOtherView',StringValue__c='Other'));
            lstConstanst.Add(new HUMConstants__c(Name='HUMCompleted',StringValue__c='Completed'));
            lstConstanst.Add(new HUMConstants__c(Name='HUMClosed',StringValue__c='Closed'));
            lstConstanst.Add(new HUMConstants__c(Name='HUMStatusCancelled',StringValue__c='Cancelled'));
            insert lstConstanst;
            system.assert(lstConstanst[0].name == 'HUMConstantOne');
            
            HUMQueueViewCriteria__c qvItem = new HUMQueueViewCriteria__c();
            qvItem.Name = 'OwnerQueue';
            qvItem.API_Name__c = 'Owner_Queue__c';
            insert qvItem;
            
            HUMQueueViewCriteria__c qvItem1 = new HUMQueueViewCriteria__c();
            qvItem1.Name = 'PlanIssueState';
            qvItem1.API_Name__c = 'Plan_Issue_State__c';
            insert qvItem1;
            
            HUMQueueViewCriteria__c qvItem2 = new HUMQueueViewCriteria__c();
            qvItem2.Name = 'Classification';
            qvItem2.API_Name__c = 'Classification__c';
            insert qvItem2;
            
            HUMQueueViewCriteria__c qvItem3 = new HUMQueueViewCriteria__c();
            qvItem3.Name = 'Intent';
            qvItem3.API_Name__c = 'Intent__c';
            insert qvItem3;
            
            HUMQueueViewCriteria__c qvItem4 = new HUMQueueViewCriteria__c();
            qvItem4.Name = 'Topic';
            qvItem4.API_Name__c = 'Topic__c';
            insert qvItem4;

            Classification_Type__c ctRec = new Classification_Type__c();
            ctRec.name = 'calls';
            ctRec.Is_Active__c=true;
            Insert ctRec;
            
            List<Classification__c> lstClassification = New List<Classification__c>();
            Classification__c classificationRec = New Classification__c();
            classificationRec.Name = 'Claims';
            classificationRec.Is_Active__c = true;
            lstClassification.add(classificationRec);  
            
            Classification__c classificationRec1 = New Classification__c();
            classificationRec1.Name = 'Disenrollment';
            classificationRec1.Is_Active__c = true;
            lstClassification.add(classificationRec1); 
            
            insert lstClassification;
            system.assertEquals(lstClassification[0].Name=='Claims',true);
            
            List<Intent__c> lstIntent = New List<Intent__c>();
            Intent__c intentRec = New Intent__c();
            intentRec.Name = 'Filling';
            intentRec.Is_Active__c = true;
            lstIntent.add(intentRec);
            
            Intent__c intentRec1 = New Intent__c();
            intentRec1.Name = 'Rekey';
            intentRec1.Is_Active__c = true;
            lstIntent.add(intentRec1);
            
            insert lstIntent;
            system.assertEquals(lstIntent.size(), 2);
            
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = classificationRec.Id;
            ctciRec.Intent__c = intentRec.id;
            ctciRec.IsActive__c = true;
            insert ctciRec;
            
            CTCI_Junction__c ctciRec1 = new CTCI_Junction__c();
            ctciRec1.Classification_Type__c = ctRec.id;
            ctciRec1.Classification__c = lstClassification[1].Id;
            ctciRec1.Intent__c = lstIntent[1].id;
            ctciRec1.IsActive__c = true;
            insert ctciRec1;
            
            List<case> caseList = new List<case>();
            case oCaseRecord = new case();
            oCaseRecord.Owner_Queue__c = 'Cincinnati Calls';
            oCaseRecord.Status = 'In Progress';  
            oCaseRecord.Classification_Id__c = lstClassification[0].id;
            oCaseRecord.Intent_Id__c  = lstIntent[0].id;
            oCaseRecord.ctci_list__c = ctciRec.id;
            caseList.add(oCaseRecord);
            
            case oCaseRecord1 = new case();
            oCaseRecord1.Owner_Queue__c = 'Louisville RSO Enrollment';
            oCaseRecord1.Status = 'In Progress';  
            oCaseRecord1.Classification_Id__c = lstClassification[1].id;
            oCaseRecord1.Intent_Id__c  = lstIntent[1].id;
            oCaseRecord1.ctci_list__c = ctciRec1.id;
            caseList.add(oCaseRecord1);
            insert caseList;
        }
    }   
    
    
    /**
* This is the main method which call all other and call the Helper methods of HUMUpdateCaseTaskHelper 
*
* @param     NULL
* @return    void 
*/
    private static testMethod void OverAllMainTestMethod()
    {
        
        User oCurrentUser = [SELECT id,Current_Queue__c, LastName from User WHERE  Current_Queue__c ='Louisville RSO Enrollment' and ISACTIVE=True LIMIT 1];
        Case cas = [Select id, Status, CTCI_List__r.Classification__r.Name, Classification_Id__c, CTCI_List__r.Intent__r.Name, Intent_Id__c, CTCI_List__c, Owner_Queue__C, Topic__c from Case where  CTCI_List__r.Classification__r.Name = 'Disenrollment']; 
        list<CTCI_Junction__c> oCTCI = [select id,name ,classification__c,classification__r.name,Intent__c,intent__r.name from  CTCI_Junction__c LIMIT 1];
        system.assert(oCTCI.size()!=0);
        system.assertEquals(oCurrentUser.Current_Queue__c,'Louisville RSO Enrollment');  
        insert new CRMFunctionality_ONOFF_Switch__c(Name = '1790946',IsON__c=false,Release_Name__c='03/19/2021',Scrum_Team__c='CRM Transformers',Story_Title__c='PR00094226 - Incident - INC0806755 - CRM Service - MHK to CRM Queue/View is flipping to Other due to logic.');         		
        System.runAs(oCurrentUser)
        {
            List<Queue_View__c> lstQueueViews = [SELECT Id, Expression__c,Queue_View_Name__c, Queue_Name__c, Service_Center__c, Department__c FROM Queue_View__c  iLimit];
            Set<String> setQueueName = new Set<String>();
            for(Work_Queue_Setup__c oWQS : [select id, name from Work_Queue_Setup__c where  IsActive__c = TRUE ])
            {
                setQueueName.add(oWQS.name);
            }
            System.assertEquals(setQueueName.size() ,3);
            Test.startTest();
            Queue_View__c qv = [SELECT Id,Expression__C,Queue_View_Name__c FROM Queue_View__c Where Queue_View_Name__c = 'Other111'];
            HUMUpdateCaseTaskHelper.initializeListAndMaps(false,setQueueName );
		    HUMUpdateCaseTaskHelper.retrieveQueuePriority(setQueueName);
            System.assertEquals(HUMUpdateCaseTaskHelper.mapViewItems.get(qv.Id).size(),3);
            System.assertEquals(HUMUpdateCaseTaskHelper.lstQueueView.size() ,11);
            map<string,string> mapPriority = new map<string,string>();            
            mapPriority.put('Classification', '1');
            mapPriority.put('Intent', '2');
            mapPriority.put('Topic', '3');
            mapPriority.put('PreviousOwnerQueue', '4');
            mapPriority.put('OwnerQueue', '18');            
            
            List<Queue_View__c> sQV = [SELECT Id,Name, Expression__C, Queue_View_Name__c, Work_Queue_Name__r.Name, Work_Queue_Name__r.Service_Center__c, Work_Queue_Name__r.Department__c,(SELECT name,API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c FROM Queue_View_Items__r order by API_Name__c  ) FROM Queue_View__c where  Queue_View_Name__c IN ('Other111','Other12345','Other123')] ;
            HUMUpdateCaseTaskHelper.checkFilterConditions(sQV[0] , cas ,sQV[0].Queue_View_Items__r, oCTCI[0]);
            HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(sQV , mapPriority,0);
            Queue_View__c sQV1 = [SELECT Id, Expression__C, Queue_View_Name__c, Work_Queue_Name__r.Name, Work_Queue_Name__r.Service_Center__c, Work_Queue_Name__r.Department__c,(SELECT name,API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c FROM Queue_View_Items__r order by API_Name__c  ) FROM Queue_View__c where  Queue_View_Name__c = 'Other12345'] ;
            HUMUpdateCaseTaskHelper.checkFilterConditions(sQV1, cas, sQV1.Queue_View_Items__r, oCTCI[0]);
            HUMUpdateCaseTaskHelper.getUpdatedQueueNames();  
            
            //For multiple QueueView and QueueViewItem combination
            List<Queue_View__c> queueViewList = [SELECT Id, Expression__C, Queue_View_Name__c, Work_Queue_Name__r.Name, Work_Queue_Name__r.Service_Center__c, Work_Queue_Name__r.Department__c,(SELECT name,API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c FROM Queue_View_Items__r order by API_Name__c  ) FROM Queue_View__c where  Queue_View_Name__c IN ('Other1','Other2','Other3','Other4','Other5','Other6') order by Queue_View_Name__c ] ;
            for(integer i= 0 ; i < queueViewList.size(); i++){
                HUMUpdateCaseTaskHelper.checkFilterConditions(queueViewList[i] , cas ,queueViewList[i].Queue_View_Items__r, oCTCI[0]);  
                HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(queueViewList, mapPriority, 1);
            }
            
            List<Classification__c> lstClassification =  [Select Id, Name from Classification__c where Name IN('Claims','Disenrollment') order by Name];
            List<Intent__c> lstIntent = [Select Id, Name from Intent__c where Name IN ('Filling','Rekey') order by Name ];
            Classification_Type__c ctRec = [Select Id from Classification_Type__c where Name = 'calls' limit 1 ];
            CTCI_Junction__c ctciRec = new CTCI_Junction__c();
            ctciRec.Classification_Type__c = ctRec.id;
            ctciRec.Classification__c = lstClassification[0].Id;
            ctciRec.Intent__c = lstIntent[0].id;
            ctciRec.IsActive__c = true;
            insert ctciRec;
            
            CTCI_Junction__c ctciRec1 = new CTCI_Junction__c();
            ctciRec1.Classification_Type__c = ctRec.id;
            ctciRec1.Classification__c = lstClassification[1].Id;
            ctciRec1.Intent__c = lstIntent[1].id;
            ctciRec1.IsActive__c = true;
            insert ctciRec1;
            
            List<case> caseList = new List<case>();
            for(integer i = 0; i < 50;i++){
                case oCaseRecord = new case();
                oCaseRecord.Status = 'In Progress';
                if(i<10){
                    oCaseRecord.Owner_Queue__c = 'Cincinnati Calls';
                    oCaseRecord.Classification_Id__c = lstClassification[0].id;
                    oCaseRecord.Intent_Id__c  = lstIntent[0].id;
                    oCaseRecord.ctci_list__c = ctciRec.id;
                }
                else{
                    oCaseRecord.Classification_Id__c = lstClassification[1].id;
                    oCaseRecord.Intent_Id__c  = lstIntent[1].id;
                    oCaseRecord.Owner_Queue__c = 'Louisville RSO Enrollment';
                    oCaseRecord.Previous_Owner_Queue__c  = 'Louisville RSO Enrollment';
                    oCaseRecord.ctci_list__c = ctciRec1.id;
                }
                caseList.add(oCaseRecord);
            }
            insert caseList;
            Test.stopTest();
        }
    } 
	
/**
* This is the method which call all other and call the Helper methods of HUMUpdateCaseTaskHelper to test null for Not Equal scenario
*
* @param     NULL
* @return    void 
*/ 
    private static testMethod void ViewTestMethod()
    {
        
        User oCurrentUser = [SELECT id,Current_Queue__c, LastName from User WHERE  Current_Queue__c ='Louisville RSO Enrollment' and ISACTIVE=True LIMIT 1];
        Case cas = [Select id, Status, CTCI_List__r.Classification__r.Name, Classification_Id__c, CTCI_List__r.Intent__r.Name, Intent_Id__c, CTCI_List__c, Owner_Queue__C, topic__c from Case where  CTCI_List__r.Classification__r.Name = 'Disenrollment']; 
        list<CTCI_Junction__c> oCTCI = [select id,name ,classification__c,classification__r.name,Intent__c,intent__r.name from  CTCI_Junction__c LIMIT 1];
        insert new CRMFunctionality_ONOFF_Switch__c(Name = '1790946',IsON__c=True,Release_Name__c='03/19/2021',Scrum_Team__c='CRM Transformers',Story_Title__c='PR00094226 - Incident - INC0806755 - CRM Service - MHK to CRM Queue/View is flipping to Other due to logic.');                 
        system.assert(oCTCI.size()!=0);
        system.assertEquals(oCurrentUser.Current_Queue__c,'Louisville RSO Enrollment');  
        System.runAs(oCurrentUser)
        {
            List<Queue_View__c> lstQueueViews = [SELECT Id, Expression__c,Queue_View_Name__c, Queue_Name__c, Service_Center__c, Department__c FROM Queue_View__c  iLimit];
            Set<String> setQueueName = new Set<String>();
            for(Work_Queue_Setup__c oWQS : [select id, name from Work_Queue_Setup__c where  IsActive__c = TRUE ])
            {
                setQueueName.add(oWQS.name);
            }
            System.assertEquals(setQueueName.size() ,3);
            map<string,string> mapPriority = new map<string,string>();            
            mapPriority.put('Classification', '1');
            mapPriority.put('Intent', '2');
            mapPriority.put('Topic', '3');
            mapPriority.put('PreviousOwnerQueue', '4');
            mapPriority.put('OwnerQueue', '18');                 
            Test.startTest();
            HUMUpdateCaseTaskHelper.initializeListAndMaps(false,setQueueName );
            HUMUpdateCaseTaskHelper.retrieveQueuePriority(setQueueName);

           //For multiple QueueView and QueueViewItem combination
            List<Queue_View__c> queueViewList = [SELECT Id, Expression__C, Queue_View_Name__c, Work_Queue_Name__r.Name, Work_Queue_Name__r.Service_Center__c, Work_Queue_Name__r.Department__c,(SELECT name,API_Name__c, Field_Operator__c, Field_Value__c, Queue_View__c FROM Queue_View_Items__r order by API_Name__c  ) FROM Queue_View__c where  Queue_View_Name__c IN ('Other1','Other2','Other3','Other4','Other5','Other6') order by Queue_View_Name__c ] ;
            for(integer i= 0 ; i < queueViewList.size(); i++){
                HUMUpdateCaseTaskHelper.checkFilterConditions(queueViewList[i] , cas ,queueViewList[i].Queue_View_Items__r, oCTCI[0]);  
                HUMUpdateCaseTaskHelper.getMatcheViewBasedOnPriority(queueViewList, mapPriority, 1);
            }
            Test.stopTest();
        }
    } 	
}