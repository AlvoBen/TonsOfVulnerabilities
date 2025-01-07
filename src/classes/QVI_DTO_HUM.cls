/*******************************************************************************************************************************
Apex Class Name : QVI_DTO_HUM.cls
Version         : 1.0
Created On      : 2016
Function        : Class contains methods for services to fetch benefits information.

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey

-*/
public with sharing class QVI_DTO_HUM
{
	
	public List<QVIWrapper> lstQVIWrapper = new List<QVIWrapper>();
	
	public class QVIWrapper
	{
		public String sAPINameAndValue ;
		public String sRecId ;
		public String sRecName ;
		public String sSetGroup ;
		public string checkBox = '<input  name="chkSelect" type="checkbox" style="width: 40%;margin: 28%;" onclick="enableDisableCreateOrder()">';
		public String  DT_RowId;
		
		/**
	     * Constructor for the class , Intialize variables
	     * 
	     * @return NULL
	     */
		public QVIWrapper(Queue_View_Item__c objQVI, Integer rNum)
		{
			sAPINameAndValue = objQVI.API_Name__c+' '+objQVI.Field_Operator__c+' '+objQVI.Field_Value__c ;
			sRecId = objQVI.Id ;
			sRecName = objQVI.name;
			sSetGroup = objQVI.Set_Group__c ;
			DT_RowId = 'row_'+rNum;
			
			
		}
	}
    
}