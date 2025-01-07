/****************************************************************************
Apex Class Name  : PcpPdpActive_DTO_HUM
Version          : 1.0
Created Date     : March 16, 2016
Function         : Wrapper class for holding PCP/PCD active table details
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Choubey                             03/16/2016                 Original Version
* Rajesh Keswani            36101             08/19/2016                 REQ 270275 Claims - PCP/PCD Link *ACTIVE PCP* .
* Melkisan Selvaraj	        252166		      06/19/2018  		         REQ - 366157 MBEServices replaced by MBEPlusService
* Lakshmi Madduri							   10/17/2018				  REQ-355805 Added Practice location
***************************************************************************************************/

public with sharing class PcpPdpActive_DTO_HUM
{
	public PcpPdpActive_DTO_HUM(String PCP_PCD_Name, String PCP_PCD_Number, String PCP_PCD_Phone_Number, String Effective_Date, String Grouper_Name, String Grouper_Number, String ProviderId, String Practice_Location, boolean boldRowFlag)
	{
		sPCP_PCD_Name = PCP_PCD_Name;
		sPCP_PCD_Number = PCP_PCD_Number;
		sPCP_PCD_Phone_Number = PCP_PCD_Phone_Number;
		sEffective_Date = Effective_Date;
		sGrouper_Name = Grouper_Name;
		sGrouper_Number = Grouper_Number;
		sProviderId = ProviderId;
		sPracticeLocation = Practice_Location;
		bBoldRow = boldRowFlag;
	}

	public String sPCP_PCD_Name{ get; set; }
	public String sPCP_PCD_Number{ get; set; }
	public String sPCP_PCD_Phone_Number{ get; set; }
	public String sEffective_Date{ get; set; }
	public String sGrouper_Name{ get; set; }
	public String sGrouper_Number{ get; set; }
	public String sProviderId{ get; set; }
	public String sPracticeLocation{get; set;}
	public boolean bBoldRow{ get; set; }
}