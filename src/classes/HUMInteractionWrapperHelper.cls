/**********************************************************************************************
Apex Class Name : HUMInteractionWrapperHelper
Version         : 1.0
Created On      : 05/27/2014
Function        : This is a wrapper class for Interaction object custom look up.
Test Class      : HUMInteractionSearchControllerTest
Modification Log: 
 * Modification ID	   Developer Name     Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0					Ninad Patil         16484                   05/27/2014             Original Version
 * 1.1					Ninad Patil         16484                   07/16/2014             Fixed code review comment(Addressed security to the class.)
 * 1.2					Ninad Patil         17100                   07/31/2014             Fixed code reveiw coments)(MOveed position of the constructor to the top.)
 ***************************************************************************************************/
public with sharing class HUMInteractionWrapperHelper
{
	/* 
	 * Method name : HUMInteractionWrapperHelper
	 * Description : This is Constucture of the class.
                     It accepts instance of Interaction__C object and flag to indicate 
                     whether record is selected or not. 
	 * Param       : Interaction Object
     				 Boolean flag (To check whether Interaction is selected or not.) 
	 * Returns     : Void
	 */
	public HUMInteractionWrapperHelper(Interaction__C oInteraction, boolean bIsSelected)
	{
		oInt = oInteraction;
		isSelected = bIsSelected;
	}

	public Interaction__C oInt{set;get;}

	public boolean isSelected{set;get;}

}