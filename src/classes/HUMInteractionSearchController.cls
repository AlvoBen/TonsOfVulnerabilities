/*******************************************************************************************************************************
Apex Class Name : HUMInteractionSearchController 
Version         : 1.0
Created On      : 05/27/2014
Function        : This class is a controller class for the page which opens as custom look up for the Interaction custom object.
Test Class		: HUMInteractionSearchControllerTest
Modification Log: 
 * Modification	ID         Developer Name          Code Review                     Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0						Ninad Patil             16484                            05/27/2014                 Original Version
 * 1.1						Ninad Patil             16484                            07/31/2014                 Fixed coding standard comments.
 * 1.2						Ninad Patil             17100                            08/05/2014                 Resubmitting After Coding standards correction.
 * 1.3                      Ninad Patil             17315							 08/14/2014                 Added Error MSG when no records are searched.(Defect : 149706.0001)
 * 1.4						Sagar Tapadia			21401							 03/10/2015					Defect 172365 fix: Added Limit to query to only return 100 records.
 *******************************************************************************************************************************/
public with sharing class HUMInteractionSearchController
{    
	//Stores List of interaction wrapper class.
	@TestVisible 
	private List<HUMInteractionWrapperHelper> lstInteractionWrapp = new List<HUMInteractionWrapperHelper>();


	/* 
	 * Method name : HUMInteractionSearchController
	 * Description : This is constructor of the class.
	 * Parameters  : None
	 * Returns     : Void
	 */
	public HUMInteractionSearchController()
	{        
		//Initializing Interaction wrapper.
		lstInteractionWrapp = new list<HUMInteractionWrapperHelper>();
		//Fetching URL paramter 'intname' if it exists.
		if(ApexPages.currentPage().getParameters().containsKey('intname'))
		{
			sIntName = ApexPages.currentPage().getParameters().get('intname'); 
		}
	}

	//Stores selected Ids in the look up.
	public transient String sSelectedIntIds {set; get;}

	//Stores the Interaction Name which is entered as search criteria in the look up.
	public String sIntName{get; set;}

	//Initializing standard set controller variables
	public ApexPages.StandardSetController con 
	{
		get 
		{
			String sQuery;
			if(con == null) 
			{
				sQuery = 'Select Id, Name,Caller_Name__c,Interacting_With_type__c,';
				sQuery += 'Interacting_With__c,createdby.name FROM Interaction__c ';
				sQuery += ' WHERE createdDate  = LAST_N_DAYS:7 ';
				if((sIntName != NULL) && (sIntName != '' ))
				{
					sQuery += ' AND (Name = \'' + String.escapeSingleQuotes(sIntName) + '%\'';
					sQuery += ' Or Name like ' + '\'%' + String.escapeSingleQuotes(sIntName) + '\') ';
				}
				sQuery += ' Order By createdDate desc LIMIT 100';
				con = new ApexPages.StandardSetController(
						Database.getQueryLocator(sQuery));
				//Sets the number of records in each page set.
				if(con != null) con.setPageSize(5);
			}
			return con;
		}
		set;
	}

	/* 
	 * Method name : saveResponse
	 * Description : Selected value is being set in variable to send back to calling function.
	 * Param       : None 
	 * Returns     : void
	 */
	public void saveResponse()
	{
		//Sending selected variable in String sSelectedIntIds.
		for(HUMInteractionWrapperHelper oIntWrap : lstInteractionWrapp)
		{
			if(oIntWrap.isSelected == true)
			{
				sSelectedIntIds = oIntWrap.oInt.id;
			}
		}
	}

	/* 
	 * Method name : getlstInteractionWrapp
	 * Description : This method returns the list of wrapper class HUMInteractionWrapperHelper.
	 *               Wrapper class contains details about records to be selected.
	 * Param       : None
	 * Returns     : List<HUMInteractionWrapperHelper>
	 */
	public List<HUMInteractionWrapperHelper> getlstInteractionWrapp() 
	{
		lstInteractionWrapp = new List<HUMInteractionWrapperHelper>();
		if(con != null)
		{
			if(((List<Interaction__C >)con.getRecords()).size() == 0)
			{
				ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.ERROR,system.Label.HUMNoInteractionRecFound));
				return lstInteractionWrapp ;
			}
			for(Interaction__C oInt: (List<Interaction__C >)con.getRecords())
			{
				//Initially making all the values as NOT selected.
				lstInteractionWrapp.add(new HUMInteractionWrapperHelper (oInt,false));
			} 
		}       
		return lstInteractionWrapp ;
	}

	/* 
	 * Method name : onSearchIntName
	 * Description : This method triggers search again depending upon search criteria.
	 * Param       : None
	 * Returns     : Pagereference
	 */
	public Pagereference onSearchIntName()
	{
		try
		{
			String sQuery;
			if(con != null) 
			{
				sQuery = 'Select Id, Name,Caller_Name__c,Interacting_With_type__c,';
				sQuery += 'Interacting_With__c,createdby.name FROM interaction__c ';
				sQuery += ' WHERE createdDate  = LAST_N_DAYS:7 ';
				if((sIntName != NULL) && (sIntName != '' ))
				{
					sQuery += ' AND ( Name Like \'' + String.escapeSingleQuotes(sIntName) + '%\'';
					sQuery += ' or Name Like ' + '\'%' + String.escapeSingleQuotes(sIntName) + '\' )';
				}
				sQuery += ' Order By createdDate desc LIMIT 100';
				con = new ApexPages.StandardSetController(
						Database.getQueryLocator(sQuery));
				//Sets the number of records in each page set.
				if(con != null) con.setPageSize(5);
			}
		}
		Catch(Exception e)
		{
			HUMExceptionHelper.logErrors(e,'HUMInteractionSearchController','onSearchIntName');
		}
		return Null;
	}
}