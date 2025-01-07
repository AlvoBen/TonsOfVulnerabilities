/***********************************************************************************************************************************
* Apex Class Name  : RouteSoftPhoneXMLResponse_DTO_HUM 
* Version          : 1.0
* Created Date     : 2nd February, 2021
* Function         : Wrapper Class for RouteSoftphoneXml web services response
* Test Class       : 
****************************************************************************
* Modification Log:
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Shreya Agrawal                            						02/02/2021            Original Version
**********************************************************************************************************************************/
public class RouteSoftPhoneXMLResponse_DTO_HUM 
{
	public RouteSoftphoneXmlResponse RouteSoftphoneXmlResponse;
	public class RouteSoftphoneXmlResponse 
	{
		public CallTransferRec CallTransferRec;
	}
	public class CallTransferRec 
	{
		public CallTransferRecList[] CallTransferRecList;
	}
	public class CallTransferRecList 
	{
        public String TransferNumber {get;set;}	//1664819
		public String CallerType {get;set;}	//Employer
		public String SiteCd {get;set;}	//GB
		public String CatCd {get;set;}	//PRI
		public String CategoryCd {get;set;}	//Precert Initiate
		public String QueueName {get;set;}	//GBO Broker Medical/Life Claims and Benefits
		public String MarketDesc {get;set;}	//
		public String MarketID {get;set;}	//84238
		public String TransferDataKey {get;set;}	//EM0048334148423812345
		public String SiteDesc {get;set;}	//
		public String ShareableTollFree {get;set;}	//0
		public String OperatingHours {get;set;}	//
		public String Notes {get;set;}	//
		public String TransferType {get;set;}	//Warm
		public String TransferConnectNumber {get;set;}	//8554100506
	}
}