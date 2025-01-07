/*******************************************************************************************************************************
Apex Class Name : HUMVerifyFamilyDemoWrapperHelper 
Version         : 1.0
Created On      : 09/08/2014
Function        : Used to display Family Members and their dependents.
Test Class      : HUMVerifyFamilyDemoWrapperHelperTest 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Asha Deepika Koyyana         17674                     09/08/2014                  Original Version
* Manish Kumar                 17674                     09/09/2014                  Code review defect fixed
*******************************************************************************************************************************/
public with sharing class HUMVerifyFamilyDemoWrapperHelper 
{
    /* Method Name  :    HUMVerifyFamilyDemoWrapperHelper
     * Description   :   Initialize the elements
     * Parameter     :   NA
     * Return Type   :   void
     */
    public HUMVerifyFamilyDemoWrapperHelper(Account AccPopulate, String sRelation) 
    {
        this.Account = AccPopulate;
        this.bSelected = false;
        this.sRelation = sRelation;
    }
    
    public Account Account {get; set;}
    public boolean bSelected {get; set;}
    public String sRelation {get; set;}
}