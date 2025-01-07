/*******************************************************************************************************************************************************
Apex Class Name  : CreateRouteXMLRequest_DTO_HUM
Version          : 1.0
Created Date     : 03/15/2021
Function         : This DTO class is used to generate RoutingXML service Request Body
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            		 Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                              03/15/2021                 Original Version
* Arpit Jain/Navajit Sarkar                              03/18/2021                 Added methods to get service bodies
**********************************************************************************************************************************************/
public class CreateRouteXMLRequest_DTO_HUM
 {
    public  RoutingXMLDTO  RoutingXML ;  

    public class  RoutingXMLDTO 
	{
        public String  PlatFormCD = '';
        public String SubscriberID = '';
        public String SubscriberDOB = '';
		public String SellingLedger = '';
		public String GroupID = '';
		public String ASOIndicator = '';
		public String MajorLOB = '';
		public String SellingLedgerForMktName = '';
		public String AkaName = '';
		public String ActParent = '';
		public String CimId = '';
		public String StateOfIssue = '';
		public String BusinessSegmentIndicator = '';
		public String HdhpIndicator = '';
		public String MtvBusinessLevel5 = '';
		public String IsSoftPhoneIndicator = '';
		public String MtvBusinessLevel7 = '';
		public String ProductTypeCode = '';
		public String ApplicationSourceRequest = '';
        public KeyValue_DTO_HUM AdditionalContextInformation;
    }
    
    public class KeyValue_DTO_HUM 
	{
        public List<keyValueofStringDTO> KeyValueOfstringstring;    
    }
    
    public class keyValueofStringDTO{
        public String Value='';
        public String Key='';	
    }

	/*
    * Method name : requestStructureRouteXML  
    * Description : This method will form the requestbody for routingxml service
    * Parameter   :String PlatFormCD,String SubscriberID,String SubscriberDOB,String SellingLedger,String GroupID,String ASOIndicator,String MajorLOB, String SellingLedgerForMktName,String AkaName,String ActParent,String CimId,String StateOfIssue,String BusinessSegmentIndicator,String HdhpIndicator,String MtvBusinessLevel5,String IsSoftPhoneIndicator,String MtvBusinessLevel7,String ProductTypeCode,String ApplicationSourceRequest,String NGroupID,String SubGroupID
    * Return : CreateRouteXMLRequest_DTO_HUM
    */
    public static CreateRouteXMLRequest_DTO_HUM requestStructureRouteXML(String PlatFormCD,String SubscriberID,String SubscriberDOB,String SellingLedger,String GroupID,String ASOIndicator,String MajorLOB, String SellingLedgerForMktName,String AkaName,String ActParent,String CimId,String StateOfIssue,String BusinessSegmentIndicator,String HdhpIndicator,String MtvBusinessLevel5,String IsSoftPhoneIndicator,String MtvBusinessLevel7,String ProductTypeCode,String ApplicationSourceRequest,String NGroupID,String SubGroupID)
    {
        CreateRouteXMLRequest_DTO_HUM oRequest = New CreateRouteXMLRequest_DTO_HUM();
        CreateRouteXMLRequest_DTO_HUM.RoutingXMLDTO routeRequest = New CreateRouteXMLRequest_DTO_HUM.RoutingXMLDTO();
        CreateRouteXMLRequest_DTO_HUM.KeyValue_DTO_HUM dtoObject = New CreateRouteXMLRequest_DTO_HUM.KeyValue_DTO_HUM();
        List<CreateRouteXMLRequest_DTO_HUM.keyValueofStringDTO> lst = new List<CreateRouteXMLRequest_DTO_HUM.keyValueofStringDTO>();       
        CreateRouteXMLRequest_DTO_HUM.keyValueofStringDTO keyValueofString1 = New CreateRouteXMLRequest_DTO_HUM.keyValueofStringDTO();
        keyValueofString1.Value=NGroupID;
       keyValueofString1.Key =PhoneBookConstants_H_HUM.NGroupID_VAL; 
        CreateRouteXMLRequest_DTO_HUM.keyValueofStringDTO keyValueofString2 = New CreateRouteXMLRequest_DTO_HUM.keyValueofStringDTO();
        keyValueofString2.Value=SubGroupID;
        keyValueofString2.Key = PhoneBookConstants_H_HUM.Demo_VAL;      	
       	lst.add(keyValueofString1);
        lst.add(keyValueofString2);
        dtoObject.KeyValueOfstringstring = lst;        
        routeRequest.PlatFormCD =PlatFormCD;
        routeRequest.SubscriberID =SubscriberID;
        routeRequest.SubscriberDOB = SubscriberDOB;
        routeRequest.SellingLedger = SellingLedger;
        routeRequest.GroupID = GroupID;
        routeRequest.ASOIndicator = ASOIndicator;
        routeRequest.MajorLOB = MajorLOB;
        routeRequest.SellingLedgerForMktName = SellingLedgerForMktName;
        routeRequest.AkaName = AkaName;
        routeRequest.ActParent = ActParent;
        routeRequest.CimId = CimId;
        routeRequest.StateOfIssue = StateOfIssue;
        routeRequest.BusinessSegmentIndicator =BusinessSegmentIndicator;
        routeRequest.HdhpIndicator = HdhpIndicator;
        routeRequest.MtvBusinessLevel5 =MtvBusinessLevel5;
        routeRequest.IsSoftPhoneIndicator =IsSoftPhoneIndicator;
        routeRequest.MtvBusinessLevel7 = MtvBusinessLevel7;
        routeRequest.ProductTypeCode = ProductTypeCode;
        routeRequest.ApplicationSourceRequest = ApplicationSourceRequest;
        routeRequest.AdditionalContextInformation=dtoObject;
        oRequest.RoutingXML = routeRequest;
        Return oRequest;
    }
    
}