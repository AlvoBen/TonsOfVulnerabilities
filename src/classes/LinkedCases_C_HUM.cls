/******************************************************************************************************************************
Apex Class Name  : LinkedCases_C_HUM
Version          : 1.0 
Created Date     : 07/14/2016
Function         : Controller to handle display Linked Cases section on Case detail page.                   
Test Class       : LinkedCases_T_HUM

Modification Log :
 *    Developer             Code Review            Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 * Harika Devi Kalidindi      33954              07/14/2016          REQ-269761 Original Version
 * Harika Devi Kalidindi      37687              08/30/2016          Defect 238584.0001 Fix Mapped scaseIntent to Intent_Id__r.Name
 * Anupama tavva              81949              04/04/2017          REQ - 306357- update to the CTCI model
 * Shreya Choodamani	                         04/28/2017          REQ#: 311397 Updated the code to show the Unlink hyperlink for Pharmacy Supervisors(Add sPharmacyUser Variable)
 * Vinay Lingegowda				                 08/26/2020			 User Story 1364284: PR00091574 - IR 5050-OGO- Add clone identifier on Case detail page (Clone case)- (CRM)	
 * Vinay Lingegowda                              10/10/2020          USER STORY 1544779 PR00091574 - IR 5050 - OGO - Tech Debt- Remove switch for OGO on/off(CRM)	
 * Mohammed Noor                              	 04/26/2021          US2163321 - Case Management Checkmarx Security violations - Case History & Case Misc
 * Gowthami Thota & Manohar						 08/06/2021			 US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page 
 **********************************************************************************************************************************/
public with sharing Class LinkedCases_C_HUM
{
    @testVisible private List<LinkedCases_Wrapper_DTO_HUM> lstwrapper = NULL;
    private String sortOrder = CaseDetailConstants_HUM.Sort_Expression_DESC_HUM;
    Private  Integer iCounter = CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM;
    Private Integer iList_size = CaseDetailConstants_HUM.PageSize_HUM;
    private Integer iTotalSize = CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM;
    private Integer iMax_limit = CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM;
    private Integer iPageFirstRecord = CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM;
    
    public boolean bOGOUser {get;set;}
    
     
    /*
     * Method Name : LinkedCases_C_HUM
     * Description : Constructor for the class , Intialize variables
     * Return type : NA
     * Paramater   : ApexPages.StandardController
     */
    public LinkedCases_C_HUM(ApexPages.StandardController controller) 
    {
        standardCon = controller;
        sCaseId = ApexPages.currentPage().getParameters().get('Id');
        lstCaseJunction = new List<Case_Junction__c >();
        sortableField = CaseDetailConstants_HUM.CREATEDDATE_HUM;
        sPharmacyUser = Pharmacy_H_HUM.IsPharmacyUser();
        bOGOUser = UtilityHelper_H_AHT_HUM.checkCustomPermissionAccess('Oral_Grievance_Oversight_OGO');
       
    }
    
    public ApexPages.StandardController standardCon{get;set;}
    public Id sCaseId{get;set;}
    public String sSortExp{Get;Set;}   
    Public List<Case_Junction__c > lstCaseJunction{Get; Set;}
    Public string sPharmacyUser{Get;Set;}
    Public String sortableField 
    {
        get 
        {
            return sSortExp;
        }
        set 
        {
            if (value == sSortExp)
            {
                sortOrder = (sortOrder == CaseDetailConstants_HUM.Sort_Expression_DESC_HUM) ? CaseDetailConstants_HUM.Sort_Expression_ASC_HUM : CaseDetailConstants_HUM.Sort_Expression_DESC_HUM;
            }
            else{sortOrder = CaseDetailConstants_HUM.Sort_Expression_DESC_HUM;}
            sSortExp = value;
        }
    }
    
    
    /*
     * Method Name : getListwrapper
     * Description : Wrapper class list to display related cases
     * Return type : LinkedCases_Wrapper_DTO_HUM
     * Paramater   : NA
     */
    
    public List<LinkedCases_Wrapper_DTO_HUM> getListwrapper()
    {
        sCaseId = ApexPages.currentPage().getParameters().get('id');
        if(lstwrapper != NULL)    return getPagination(lstwrapper);
        else 
        {
            lstwrapper = new List<LinkedCases_Wrapper_DTO_HUM>();
            lstwrapper = getRelatedCases();
            return getPagination(lstwrapper);
        }
         
    }
    
    /*
     * Method Name : getsortOrder
     * Description : This method is used to get sortorder
     * Return type : String
     * Paramater   : NA
     */
    public String getsortOrder()
    {
        if (String.isBlank(sortOrder))
        {
            return CaseDetailConstants_HUM.Sort_Expression_DESC_HUM;
        }
        else
        {
            return sortOrder;
        }
    }
    
    /*
     * Method Name : setsortOrder
     * Description : This method is used to set sortorder
     * Return type : NA
     * Paramater   : String
     */
    public void setsortOrder(String value)
    {
        sortOrder = value;
    }
    
    
    
    
    
    /*
     * Method Name : getRelatedCases
     * Description : This method is used for Passing all the related cases into a wrapperclass list
     * Return type : List<LinkedCases_Wrapper_DTO_HUM>
     * Paramater   : NA
     */
    public List<LinkedCases_Wrapper_DTO_HUM> getRelatedCases()
    { 
       for(Case_Junction__c objCaseJunction1 : [Select Case__r.CTCI_List__r.Classification__c,Case__c,CreatedDate,Case_Master__c,Case__r.Id,Case__r.CaseNumber,Case__r.Created_By_Queue__c,CreatedBy.Name,Case__r.CreatedBy.Name,Case__r.CTCI_List__r.Intent__r.Name,Case__r.ClosedDate,Case__r.Interacting_About__c,Case__r.Status,Case__r.CreatedDate,Case__r.CTCI_List__r.Classification__r.name,IsLinked__c,Case_Master__r.Id,Case_Master__r.caseNumber,Case_Master__r.Created_By_Queue__c,Case_Master__r.CreatedBy.Name,Case_Master__r.CTCI_List__r.Intent__r.Name,Case_Master__r.ClosedDate,Case_Master__r.Interacting_About__c,Case_Master__r.Status,Case_Master__r.CreatedDate,Case_Master__r.CTCI_List__r.Classification__r.name,Work_Queue__r.Name,IsCloneCase__c from Case_Junction__c where ((IsLinked__c=true) AND (Case__c=:scaseId OR Case_Master__c=: scaseId )) order By CreatedDate Desc limit :CaseDetailConstants_HUM.NUMBER_CONSTANT_SEVENTHOUSAND_HUM ])
        {
            LinkedCases_Wrapper_DTO_HUM objCaseJwrapper = new LinkedCases_Wrapper_DTO_HUM(objCaseJunction1,sortableField,sortorder);
            if(objCaseJunction1 .Case_Master__c == scaseId)
            {
                objCaseJwrapper.sCaseId                =  objCaseJunction1 .Case__r.Id;
                objCaseJwrapper.sCaseMasterId          =  objCaseJunction1 .Case_Master__r.Id;
                objCaseJwrapper.dCreatedDate           =  objCaseJunction1 .CreatedDate;
                objCaseJwrapper.bIsLinked              =  objCaseJunction1 .IsLinked__c;
                objCaseJwrapper.sWorkQueueName         =  objCaseJunction1 .Work_Queue__r.Name;
                objCaseJwrapper.sCreatedByName         =  objCaseJunction1 .CreatedBy.Name;
                objCaseJwrapper.sCaseCaseNumber        =  objCaseJunction1 .Case__r.CaseNumber;
                objCaseJwrapper.sCaseCreatedByQueue    =  objCaseJunction1 .Case__r.Created_By_Queue__c;
                objCaseJwrapper.sCaseCreatedByName     =  objCaseJunction1 .Case__r.CreatedBy.Name;
                objCaseJwrapper.sCaseIntent            =  objCaseJunction1 .Case__r.CTCI_List__r.Intent__r.Name ;
                objCaseJwrapper.dCaseClosedDate        =  objCaseJunction1 .Case__r.ClosedDate;
                objCaseJwrapper.sCaseInteractingAbout  =  objCaseJunction1 .Case__r.Interacting_About__c;
                objCaseJwrapper.sCaseStatus            =  objCaseJunction1 .Case__r.Status;
                objCaseJwrapper.dCaseCreatedDate       =  objCaseJunction1 .Case__r.CreatedDate;
                objCaseJwrapper.sCaseClassification    =  objCaseJunction1 .Case__r.CTCI_List__r.Classification__r.name;
                objCaseJwrapper.sIsCloneCase           =  objCaseJunction1.IsCloneCase__c?'Yes':'';
            }
            else
            {
                objCaseJwrapper.sCaseId                =  objCaseJunction1.Case_Master__r.Id;
                objCaseJwrapper.sCaseMasterId          =  objCaseJunction1.Case__r.Id;
                objCaseJwrapper.bIsLinked              =  objCaseJunction1.IsLinked__c;
                objCaseJwrapper.dCreatedDate           =  objCaseJunction1.CreatedDate;
                objCaseJwrapper.sWorkQueueName         =  objCaseJunction1.Work_Queue__r.Name;
                objCaseJwrapper.sCreatedByName         =  objCaseJunction1.CreatedBy.Name;
                objCaseJwrapper.sCaseCaseNumber        =  objCaseJunction1.Case_Master__r.CaseNumber;
                objCaseJwrapper.sCaseCreatedByQueue    =  objCaseJunction1.Case_Master__r.Created_By_Queue__c;
                objCaseJwrapper.sCaseCreatedByName     =  objCaseJunction1.Case_Master__r.CreatedBy.Name;
                objCaseJwrapper.sCaseIntent            =  objCaseJunction1.Case_Master__r.CTCI_List__r.Intent__r.Name;
                objCaseJwrapper.dCaseClosedDate        =  objCaseJunction1.Case_Master__r.ClosedDate;
                objCaseJwrapper.sCaseInteractingAbout  =  objCaseJunction1.Case_Master__r.Interacting_About__c;
                objCaseJwrapper.sCaseStatus            =  objCaseJunction1.Case_Master__r.Status;
                objCaseJwrapper.dCaseCreatedDate       =  objCaseJunction1.Case_Master__r.CreatedDate;
                objCaseJwrapper.sCaseClassification    =  objCaseJunction1.Case_Master__r.CTCI_List__r.Classification__r.name;
                objCaseJwrapper.sIsCloneCase           =  objCaseJunction1.IsCloneCase__c?'Yes':'';
            }
            
            objCaseJwrapper.sSortType = sortableField;
            objCaseJwrapper.sortorder = sortorder;
            lstwrapper.add(objCaseJwrapper);
        }
        lstwrapper.sort();
        return lstwrapper;  
    }
    
     
    /*
     * Method Name : unlinking
     * Description : Method for unlinking cases 
     * Return type : NA
     * Paramater   : NA
     */
    public void unlinking()
    {
        String sRelatedCaseId =  ApexPages.CurrentPage().getParameters().get('caseNo');
        Case_Junction__c obj =[Select Case__c,Case_Master__c from Case_Junction__c where IsLinked__c=true AND ((Case__c=:scaseId AND Case_Master__c=:sRelatedCaseId )OR (Case__c=:sRelatedCaseId AND Case_Master__c=:scaseId)) Limit: CaseDetailConstants_HUM.NUMBER_CONSTANT_ONE_HUM ];
        if(obj != null)
        {
            if(Schema.SObjectType.Case_Junction__c.fields.IsLinked__c.isUpdateable())	obj.IsLinked__c=false;
            update obj ;
        }
        lstwrapper.clear();
        getRelatedCases();
        getListwrapper();
    }
     
     
     /*
     * Method Name : doSort
     * Description : This method is used for sorting related cases section 
     * Return type : NA
     * Paramater   : NA
     */       
    public void doSort() 
    {
        for(LinkedCases_Wrapper_DTO_HUM objCaseJunctionWrapper : lstwrapper)
        {
            objCaseJunctionWrapper .sSortType = sortableField;
            objCaseJunctionWrapper .sortOrder = sortOrder;
            
        }
        lstwrapper.sort();
        
    }
    
    
     /*
      * Method Name   :    getPagination
      * Description   :    Pagination 
      * Return Type   :    List<LinkedCases_Wrapper_DTO_HUM>
      * Parameters    :    List<LinkedCases_Wrapper_DTO_HUM>
      */
    public List<LinkedCases_Wrapper_DTO_HUM> getPagination(List<LinkedCases_Wrapper_DTO_HUM> lstwrapper1)
    {   
            iList_size = CaseDetailConstants_HUM.PageSize_HUM;
            iTotalSize = lstwrapper1.size();
            iList_size = (iList_size > iTotalSize ) ? iTotalSize : ilist_size;
            if(iList_size == iTotalSize) iList_size = iTotalSize;
        
           
        List <LinkedCases_Wrapper_DTO_HUM>  lstwrapperPaginationHelper = new List <LinkedCases_Wrapper_DTO_HUM>();
        iMax_limit = ((iCounter + ilist_size) > iTotalSize) ? iTotalSize : (iCounter + ilist_size);
        iCounter = (iCounter > iMax_limit) ? CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM : iCounter;
        try
        { 
            if(NULL == lstwrapper1)
            {
                lstwrapperPaginationHelper = new List<LinkedCases_Wrapper_DTO_HUM>();
            } 
            else if(!lstwrapper1.isEmpty())
            {
                lstwrapperPaginationHelper.clear();
            }
            
            if(lstwrapper1.isEmpty())    return NULL;
            
            for(integer i = iCounter ; i <  iMax_limit ; i++)
            {
                lstwrapperPaginationHelper.add(lstwrapper1.get(i));
            }
            iPageFirstRecord = iCounter;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'LinkedCases_C_HUM', 'getPagination');
        }
        return lstwrapperPaginationHelper;
    }
    
    
    /*  
     * Method Name   :    getPrevious
     * Description   :    This Method is used to display the previous page record(s)    
     * Return Type   :    void    
     * Parameters    :    NA
     */
    public void getPrevious()
    {
        iCounter -= ilist_size;
        iPageFirstRecord = iCounter;
    }
    
    
    /*  
     * Method Name   :    getNext
     * Description   :    This Method is used to display the next page record(s)    
     * Return Type   :    void    
     * Parameters    :    NA
     */
    public void getNext()
    {
        iCounter += ilist_size;
        iPageFirstRecord = iCounter;
    
    }
   
   
    /*  
     * Method Name   :    getLast
     * Description   :    This Method is used to display the last page record(s)    
     * Return Type   :    void    
     * Parameters    :    NA
     */
    public void getLast()
    {
        Integer iRec = iTotalSize/ilist_size;
        iCounter = (Math.mod(iTotalSize, ilist_size) > CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM) ? iRec*ilist_size : (iRec - CaseDetailConstants_HUM.NUMBER_CONSTANT_ONE_HUM)*ilist_size;
        iMax_limit = ((iCounter + ilist_size) > iTotalSize) ? iTotalSize : (iCounter + ilist_size);
        iPageFirstRecord = iCounter;
    }
    
    
    /*  
     * Method Name   :    getFirst
     * Description   :    This Method is used to display the first page record(s)       
     * Return Type   :    void    
     * Parameters    :    NA
     */
    public void getFirst()
    {
        iCounter = CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM;
        iPageFirstRecord = iCounter;
    }
    
    
    /*  
     * Method Name   :    getDisablePrevious
     * Description   :    This method will disable the previous buttons    
     * Return Type   :    boolean
     * Parameters    :    NA
     */
    public Boolean getDisablePrevious()
    {
        return (iCounter > CaseDetailConstants_HUM.NUMBER_CONSTANT_ZERO_HUM ) ?  true:false ;
    }
    
    
    /*  
     * Method Name   :    getDisableNext
     * Description   :    This method will disable the next buttons    
     * Return Type   :    boolean
     * Parameters    :    NA
     */
    public Boolean getDisableNext()
    {
        return (iCounter + iList_size < iTotalSize) ? true:false ;
    }
    
    
    /*  
     * Method Name   :    getTotal_size
     * Description   :    This Method is used to display the total size of record(s)    
     * Return Type   :    integer
     * Parameters    :    NA
     */
    public Integer getTotal_size()
    {
        return iTotalSize;
    }
 
 
    /*  
     * Method Name   :    getPageFirstRecord
     * Description   :    This Method is used to display the first page record(s)    
     * Return Type   :    Integer
     * Parameters    :    NA
     */
    public Integer getPageFirstRecord()
    {
        return iPageFirstRecord + CaseDetailConstants_HUM.NUMBER_CONSTANT_ONE_HUM ;
    }
    
    /*
     * Method Name   :    getPageLastRecord
     * Description   :    This Method is used to display the last page record(s)    
     * Return Type   :    Integer
     * Parameters    :    NA
     */
    public Integer getPageLastRecord()
    {
        return getDisableNext() ? (iPageFirstRecord + iList_size) :  iTotalSize ;
    }
    
    
}