/*
Class Name :  PolicyRelatedList_C_HUM 
Version: 1.0 
Created Date     : Feb 03 2016
Function         : Class to fetch the records Of the Group Policy.
    Modification Log

  Developer       Code Review        Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Payal Yadav                        3/1/2016             Original Version
* Harsha N          377              08/30/2016           Queried the fields Effective date And End date as per the REQ - 281088
* Melkisan Selvaraj		             05/14/2018	          REQ - 361220 Included Benefit_Coverage__c to the query string
* Anurag Chaturvedi					08/15/2018			  Added Plan Status field on Group page
* Sagar MN							08/06/2020			  USER STORY 1329433 EDW Reporting changes
* Ajay Chakradhar                   05/05/2021	          REQ - 2184294 - Replacing constants with Consumer Management Constants
*/

Public With sharing Class PolicyRelatedList_C_HUM {
    
    Public String sAccId{Get; Set;}
    Public Integer iNoOfRecords{Get; Set;}
    Public Integer iSize{Get;Set;}
    Public String sProductTypeValue {Get; Set;}
    Private String sPOLRELLIST_PRODUCTTYPE_HUM = 'VIS';

    Public ApexPages.StandardSetController setCon {
        Get{
            If (setCon == null){
                iSize = 5;
                String sSrtQueryString = 'SELECT Id,Product__r.Product_Line_Description__c,Product_Type__c, name,Product__r.Name, Major_LOB__c, platform__c, Coverage_Plan_Effective_Date__c, Coverage_Plan_end_date__c, Product_Description__c,Aso__c, Segment_Indicator__c,Issue_State__c,Benefit_Coverage__c,Plan_Status__c,Source_Cust_Cov_Key__c from policy__c WHERE Group_Name__c= :sAccId  order by Plan_Status__c asc,Coverage_Plan_Effective_Date__c desc  limit 10000';
                setCon = New ApexPages.StandardSetController(Database.getQueryLocator(sSrtQueryString));
                setCon.setPageSize(iSize);
                iNoOfRecords = setCon.getResultSize();
            }
        Return setCon;
        }set;
    }
    
/**
 * Returns a list of Policy Records 
 * <p>
 * get the list of policies records for the given Account.
 *
 * @return      list of Policy Records
 * @see         Policy Records on Table
 */
    Public List<PolicyProductTypeWrapper_DTO_HUM> getPolicy(){
    List<PolicyProductTypeWrapper_DTO_HUM> lstpolicyProductType = New List<PolicyProductTypeWrapper_DTO_HUM>();
    List<policy__c > lstPolicy = New List<policy__c >();
    For (policy__c oPolicy : (List<policy__c >)setCon.getRecords())
    {
        If (oPolicy.Product__r.Name == sPOLRELLIST_PRODUCTTYPE_HUM)
        {
            lstpolicyProductType.add(New PolicyProductTypeWrapper_DTO_HUM(oPolicy,oPolicy.Product_Type__c));
        }
        Else
        {
            lstpolicyProductType.add(New PolicyProductTypeWrapper_DTO_HUM(oPolicy,oPolicy.Major_LOB__c ));
        }
    }
    Return lstpolicyProductType ;
    }
   
/**
 * Returns NULL
 * <p>
 * Refresh the table with New records when click on Next Or Previous Link
 *
 * @return      NULL
* @see         Policy Records when click on link Next Or Previous
 */
    Public pageReference refresh() {
        setCon = null;
        getPolicy();
        setCon.setPageNumber(1);
        Return null;
    }
}