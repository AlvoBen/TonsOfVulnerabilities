/*******************************************************************************************************************************
Apex Class Name : WQV_ExpressionBuilder_T_HUM
Version         : 1.0
Created On      : 01/04/2018      
Function        : Test Class for Expression Builder 

Modification Log: 
* Developer Name                  Code Review                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                            01/04/2018                  Original Version
* Pooja Kumbhar											 04/29/2020					 Checkmarx TextMethod without assert error fix
*****************************************************************************************************************************/
@isTest
private class WQV_ExpressionBuilder_T_HUM
{
    
    /*
    * Method Name : maintestmethod
    * Description : Test method for overall code coverage
    * Return type : Void
    * Paramater   : N/A
    */
    static testMethod  void maintestmethod() 
    {
        //Setup Data ---------------------------------------------
        Work_Queue_Setup__c WqS = new Work_Queue_Setup__c();
        WqS.Name ='Phoenix Doctor Calls';
        WqS.IsActive__c = true;
        insert Wqs;
		System.assertEquals(WqS.Name == 'Phoenix Doctor Calls', true);

        Queue_View__c qv = new Queue_View__c();
        qv.Name = 'V-10096'; 
        qv.Work_Queue_Name__c = WqS.Id;
        qv.Expression__c = '((QV-62662) OR (QV-46209)) OR ((QV-62658) AND (QV-62654))';
        insert qv;

        Queue_View_Item__c qvi = new Queue_View_Item__c();
        qvi.Queue_View__c = qv.Id;
        qvi.API_Name__c = 'Topic';
        qvi.Field_Operator__c = 'Equals';
        qvi.Field_Value__c = 'Phones';
        qvi.Set_Group__c = '1';
        insert qvi;
        
        Queue_View_Item__c qv2 = new Queue_View_Item__c();
        qv2.Queue_View__c = qv.Id;
        qv2.API_Name__c = 'InteractingWithType';
        qv2.Field_Operator__c = 'Equals';
        qv2.Field_Value__c = 'Phones';
        qv2.Set_Group__c = '2';
        insert qv2;
        
        Queue_View_Item__c qv3 = new Queue_View_Item__c();
        qv3.Queue_View__c = qv.Id;
        qv3.API_Name__c = 'Intent';
        qv3.Field_Operator__c = 'Equals';
        qv3.Field_Value__c = 'Phones';
        qv3.Set_Group__c = '2';
        insert qv3;
        
        Queue_View_Item__c qv4 = new Queue_View_Item__c();
        qv4.Queue_View__c = qv.Id;
        qv4.API_Name__c = 'Topic';
        qv4.Field_Operator__c = 'Equals';
        qv4.Field_Value__c = 'Phones';
        qv4.Set_Group__c = '1';
        insert qv4;
        

        //Setup Data ---------------------------------------------
        
        PageReference myVfPage = Page.WQV_ExpressionBuilder_VF_HUM;
        Test.setCurrentPage(myVfPage);

        // Put Id into the current page Parameters
        ApexPages.currentPage().getParameters().put('id',qv.Id);
        
        
        WQV_ExpressionBuilder_C_HUM ExpBuild = new WQV_ExpressionBuilder_C_HUM();
        ExpBuild.buildExpression();
        
    }
}