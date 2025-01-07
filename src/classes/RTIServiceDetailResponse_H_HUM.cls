/**************************************************************************** 
Apex Class Name  : RTIServiceDetailResponse_H_HUM 
Version          : 1.0
Created Date     : June 20, 2017
Function         : Response helper for GetInteractionDetail method in RTI Print View Link service
Test Class       : RTIServiceDetailResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat                                 24/11/2016                Original Version
* Shivam Anand               106733             20/06/2017                RTI print View Link changes
* Imran                                         21/02/2020                REQ-436960 RTI VAT changes
* Prasanthi Kandula                             04/16/2020                US 1003374 - To display send link on RTI Detail page     
*  Prasanthi Kandula                            05/18/2021                US - 2082407 Added new card for channel Email
*  Anuradha Gajbhe                              05/18/2021                US - 2082385 Added logic to display new card for channel Text
*  Rahul Chaurasia                              05/19/2021                US - 2082359 Added logic to display new card for channel Web
****************************************************************************************************/

public class RTIServiceDetailResponse_H_HUM 
{       
    public static RTIServiceDetailResponse_H_HUM parse (String response) 
    {
        return (RTIServiceDetailResponse_H_HUM) System.JSON.deserialize(response, RTIServiceDetailResponse_H_HUM.class);
    }
    
    public InteractionDetailResponse InteractionDetailResponse;
    
    public class InteractionDetailResponse {
        public InteractionDetailResult InteractionDetailResult; 
    }
    
    public class InteractionDetailResult {
    
        /*
         * nbRecord : Total number of records returned by C360, which may be different the actual number of records
         *            returned by the service call.  For instance, the request may specific a"recordLimit" of 200 
         *            records, therefore the actual number of records returned to the service requestor will be 200,
         *            but the nbRecord may be larger, e.g. 550, representing the total number of records in the service
         *            collection 
         *  
        */
    
        public Integer nbRecord;
    
        /*
         * mas : Array of response mobile objects
         * mbs : Array of response mobile objects
         * ivs : Array of response ivr objects
         * wbs : Array of response web objects 
         * vts : Array of response mobile objects    
        */   
        public List<MA> mas;
        public List<MB> mbs;
        public List<IV> ivs;
        public List<WB> wbs; 
        Public List<PRINT> prints; 
        Public List<VAT> vaTs; 
        Public List<Emails> emails;
        Public List<eMs> ems;
        Public List<SMS> smss;
        Public List<WEB> webs;        
        
    }  

   /*
    * actionTime : The Event Date Time
    * appId : The Application Id
    * pageId : The Page Id           
    */       
    public class MA 
    {
        public DateTime actionTime;
        public String pageId;
        public String siteId; 
        public String prodName;
        public String shopActionTypeCD;
        public String eventActionTypeCD;
        public String ActionTypeDesc;
        public String eventName;
        public String elementName;
        public String code;     
        public String Purpose;   
    }    

    /*
     * actionTime : The Event Date Time
     * siteId : The Site Id
     * pageId : The Page Id              
    */      
    public class MB 
    {
        public DateTime actionTime;
        public String pageId;
        public String siteId; 
        public String prodName;
        public String shopActionTypeCD;
        public String eventActionTypeCD;
        public String ActionTypeDesc;
        public String eventName;
        public String elementName;
        public String code;     
        public String Purpose;   
    }        

   /*
    * actionTime : The Event Date Time
    * actionName : The Action Name
    * businessComments : The Business Comments  
    * businessDesc : The Business Description  
    * businessVal : The Business Value  
    * categoryDescription : The Category Description     
    * id : The ivr Record Id    
    * memberResp : The Member Reponse
    * moduleName : The Module Name
    */      
    public class IV 
    {
        public String actionName;
        public DateTime actionTime;
        public String businessComments;
        public String businessDesc;     
        public String businessVal;
        public String memberResp;
        public String moduleName;                       
    }   

    /*
     * actionTime : The Event Date Time
     * siteId : The Site Id
     * pageId : The Page Id              
    */       
    public class WB 
    {
        public DateTime actionTime;
        public String pageId;
        public String siteId; 
        public String prodName;
        public String shopActionTypeCD;
        public String eventActionTypeCD;
        public String ActionTypeDesc;
        public String eventName;
        public String elementName;
        public String code;   
        public String Purpose; 
        Public String DocumentViewUrl;
    
   }   

    public class WEB
    {
        Public String DocumentViewUrl;
        
    }
    

    public class PRINT 
    {
        Public String DocumentViewUrl;
        public string documentType;
    }      
    Public Class VAT
    {    
        Public String DocumentViewUrl;
    } 
    
    public class Emails
    {
        Public String DocumentViewUrl;
        public string documentType;   
    }
    public class eMs
    {
        Public String DocumentViewUrl;
        public string documentType;   
    }
    
    public class SMS
    {
        Public String DocumentViewUrl;
        public string documentType;   
    }
}