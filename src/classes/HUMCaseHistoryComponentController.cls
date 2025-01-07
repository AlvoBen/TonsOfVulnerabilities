/******************************************************************************************************************************
Apex Class Name  : HUMCaseHistoryComponentController
Version          : 1.0 
Created Date     : MAY 26 2014
Function         : Controller to HUMCaseHistoryComponent VF component. It is used to display cases 
                   related to Account, Policy or Policy Member in salesforce.
                   Search records based on Case Number or Date Created range.
Test Class       : HUMCaseHistoryComponentControllerTest
Modification Log :
 *  Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *  Sagar Tapadia      15767                05/26/2014         Original Version
 *  Shreya Choodamani  15767                06/17/2014         Updated the SOQL to include Interacting About.
 *  Sagar Tapadia      15767                06/27/2014         Added error message when no search criteria is provided
 *  Ninad Patil        15767                06/27/2014         Code changed for associating Interaction to case on click of case link.
 *  Ninad Patil        15767                06/30/2014         Removed Code for associating Interaction to case on click of case link.
 *  Sagar Tapadia      15767                07/02/2014         Updated code to handle date as string with or without "/"
 *  Sagar Tapadia      15767                07/07/2014         Updated Code to change the placement of Constructor and Variables
                                                                (public getter/setters variables and private variables specific to functionality are added after constructor)
 *  Sagar Tapadia      15767                07/10/2014         Updated Query to fetch Classification and Intent values instead of Category and Reason.
 *  Sagar Tapadia      15767                07/15/2014         Updated Default sorting order from Ascending to Descending  
 *  Sagar Tapadia      15767                07/23/2014         Added logic to show error messages if filter criteria's are not proper.
 *  Sagar Tapadia      17015                08/04/2014         Making changes as per Review Guidelines  
 *  Vishal Verma       17626                08/27/2014         Added logic to check that Start Date and End date should not be future date
 *  Vishal Verma       17980                09/17/2014         Added logic to compare created date and closed date for cases in Logged in User Timezone
                                                                 and then display them on the page.      
 *  Sagar Tapadia      18216                09/26/2014         Updated Query to get Queue Name from Created By Queue and not use Owner Queue field on Case.
 *  Manish Kumar       21062                02/19/2015         For  REQ#72747 added getComments method                                                                                                                                                             
 *  Manish Kumar       27139                10/14/2015         Fixed creation of duplicate Case from Recent tab issue. Added a method createCaseAndRedirect
 *  Prasanthi Kandula  28219                11/20/2015         Added logic to enable/disable Case button based on the policies for the Group
 *  Kritika P Popat    28219                11/23/2015         Class modified for REQ 161520
 *  Manish Kumar                            01/06/2016         Fixed defefct# 211500.0001, 212554.0001, 212524.0001
 *  Sharan Shanmugam   30013				02/17/2016		   Added logic to enable/disable Case button based on the policies for the Member as part of REQ - 157784
 *  Prachi Bhasin      33344                07/18/2016         Changes made as part of REQ- 270349
 ********************************************************************************************************************************/
public with sharing Class HUMCaseHistoryComponentController 
{
    
}