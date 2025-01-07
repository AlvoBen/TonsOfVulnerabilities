/****************************************************************************
Apex Class Name  : PcpPdpHistory_DTO_HUM
Version          : 1.0
Created Date     : March 16, 2016
Function         : Wrapper class for holding PCP/PCD active table details
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Avinash Choubey                             03/16/2016                 Original Version
* Avinash Choubey          30369              03/29/2016                 fixed review comments
* Melkisan Selvaraj	        252166		      06/19/2018  		         REQ - 366157 MBEServices replaced by MBEPlusService
* Seema Kaulgi                             	  02/25/2019         		 REQ - 383975 Display Grouper Number and Grouper Name on PCP/PCD Tab (RF)
***************************************************************************************************/

public with sharing class  PcpPdpHistory_DTO_HUM
{
	public PcpPdpHistory_DTO_HUM(String PCP_PCD_Name, String PCP_PCD_Number, String Effective_Date, String ProviderId, String Practice_Location, String Grouper_Name, String Grouper_Number, Boolean boldThisRow)
	{
		sPCP_PCD_Name = PCP_PCD_Name;
		sPCP_PCD_Number = PCP_PCD_Number;
		sEffective_Date = Effective_Date;
		sProviderId = ProviderId;
		sPracticeLocation = Practice_Location;
        sGrouper_Name = Grouper_Name;
        sGrouper_Number = Grouper_Number;
		bBoldThisRow = boldThisRow;
	}

	public String sPCP_PCD_Name{ get; set; }
	public String sPCP_PCD_Number{ get; set; }
	public String sEffective_Date{ get; set; }
	public String sProviderId{ get; set; }
	public String sPracticeLocation{get; set;}
    public String sGrouper_Name{ get; set; }
    public String sGrouper_Number{ get; set; }
	public Boolean bBoldThisRow{ get; set; }
}