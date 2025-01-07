/******************************************************************************************************************************
Apex Class Name  : HUMTemplateBaseHelper
Version          : 1.0 
Created Date     : May 22 2015
Function         : Controller to handle unsaved changes popup.
Test Class       : HUMTemplateBaseHelperTest
Modification Log :
*   Developer           Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
    Santhi Mandava                          05/22/2015           Original Version.
    Mithra Bharadwaj                                             US#2089572 SonarQube Changes
**********************************************************************************************************************************/


public with sharing virtual class HUMTemplateBaseHelper 
{
    public HUMTemplateBaseHelper()
    {
        List<Case> lstCases = null;
        sSelectedCaseId = Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam);
        If(!String.isBlank(sSelectedCaseId)) lstCases = [Select CaseNumber from Case Where Id =: sSelectedCaseId];
        If(lstCases !=null && !lstCases.isEmpty()) sCaseNumber = lstCases[0].CaseNumber;
        objFinishUrl = new PageReference('/'+ string.escapeSingleQuotes(sSelectedCaseId));
    }

    public String sCaseNumber{get;set;}
    public PageReference objFinishUrl{get;set;}
    public String sSelectedCaseId{get;set;}
}