/**************************************************************************** 
Apex Class Name :  RTIServiceSummaryResponse_H_HUM 
Version: 
Created Date      November 21, 2016
Function         : Response helper for GetInteractionSummary method in RTI service
Test Class       : RTIServiceSummaryResponse_T_HUM
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Pallavi Ravishankar          61268                 01/04/2017          Original Version
*  Kritika Popat                86349                 07/04/2017          Phase 2 Service changes
*  Kritika Popat                86349                 10/04/2017          Pagination    
*  Kritika Popat                90451                 24/04/2017          IVR And Print 2nd level
*  Kritika Popat                111075                07/18/2017          Message in Print Card
*  Shivam Anand                 180694                02/05/2018          REQ-350840,350841,350843-Display End Date/Time for WB, MB And MA
*  Anjani  Vaddadi                                    21/02/2020          REQ- RTI VAT changes
*  Prasanthi Kandula                                  16/04/2020          US-1003374 RTI2020 To display Send link on RTI Detail page    
*  Anjani Vaddadi                                     7/03/2020           US - 1125628- Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
*  Prasanthi Kandula                                  05/18/2021          US - 2082407 Added new card for channel Email
*  Anuradha Gajbhe                                    05/18/2021          US - 2082385 Added logic to display new card for channel Text
*  Rahul Chaurasia                                    05/19/2021          US - 2082359 Added logic to display new card for channel Web
*  Rahul Chaurasia                                    06/09/2021          US - 2082359 Fixed Category description issue for WEB
*  Anuradha Gajbhe                                    08/19/2021          US - 2496294 Pass Message Definition Code (MDC) on Outbound Communications attached to CRM Cases via RTI
*************************************************************************************************/

Public Class RTIServiceSummaryResponse_H_HUM  
{
    Public Static RTIServiceSummaryResponse_H_HUM parse (String response) 
    {
        Return (RTIServiceSummaryResponse_H_HUM) System.JSON.deserialize(response, RTIServiceSummaryResponse_H_HUM.Class);
    }

Public InteractionSummaryResponse InteractionSummaryResponse;
    
    Public Class InteractionSummaryResponse {
        Public InteractionSummaryResult InteractionSummaryResult;
    }
    
    
    /*
     * nbRecord : Total number Of records returned by C360, which may be different the actual number Of records
     *            returned by the service call.  For instance, the request may specific a"recordLimit" of 200 
     *            records, therefore the actual number of records returned to the service requestor will be 200,
     *            but the nbRecord may be larger, e.g. 550, representing the total number of records in the service
     *            collection 
     *  
    */
    Public Class InteractionSummaryResult
    {
        Public Integer nbRecords;    
        Public List<Interaction> Interactions;
    }
    /*
     * interactionkey : Single interaction Key from C360
     * interactionsrckey : Single source system Key from the source system
     * sourceDescription : taxonomy lookup For description Of source code
     * sourceCode : A code To define the source Of the interaction.  Unique To Each source system.
     * channelCode : Channel/Mode of Interaction fulfulfilment, such as IV, PRINT, MA, MB, WB.
     * channelDescription : The taxonomy lookup For description Of channel code
     * directionCode : A single digit value which identifies if the interaction Is Inbound (I) Or Outbound (O) Or Unknown (U) 
     * actionTime : TUTC Date time the interaction occurred
     * PRINT: The embedded Print Object
     * MA: The embedded Mobile Object
     * MB: The embedded Mobile Object
     * IV: The embedded IVR Object
     * WB: The embedded Web Object
    */
        
    Public Class Interaction
    {
        Public String interactionkey;
        Public String interactionsrckey;
        Public String sourceDescription;
        Public String sourceCode;
        Public String channelCode;
        Public String channelDescription;
        Public String directionCode;
        Public DateTime actionTime;
        Public PRINT print;
        Public MA ma;
        Public MB mb;
        Public IV iv;
        Public EMAIL email;     
        Public EM em;     
        Public WB wb;
        Public VAT vat;
        Public WEB web;     
        Public SMS sms;            
    }
    
    /*
     * categoryCode : The High level classification For the purpose Of the Interaction
     * categoryDescription : The taxonomy lookup For description Of purpose category code
     * businessArea : The High level identifier Of the segment Of business that relates To the purpose For the interaction, such As MEDICARE, MEDICAID Or COMMERCIAL
     * businessSubArea : The Sub class() value For purpose, If one exists
     * departmentName : The Name of the entity fulfulling the interaction request
     * startDateTime : The Interaction Timestamp. Actual time when the interaction was recorderd as start time in the source
     * endDateTime : Actual time when the interaction was recorderd as end time in the source
     * statusCode : The Outcome(Description Of status)
     * statusDescription : The taxonomy lookup for description of status code
     * isFinal : indicates whether the disposition Is a final disposition Or interim
    */    
    Public Class PRINT
    {
        Public String categoryCode;
        Public String categoryDescription;
        Public DateTime startDateTime;
        Public DateTime endDateTime;
        Public String businessArea;
        Public String busineesSubArea;
        Public String departmentName;
        Public String statusCode;
        Public String statusDescription;
        Public Boolean isFinal;
        Public String MessageDefinitionDescription;
        Public String trackingID;
        public string MemberCardId;
        public string DependentCode;
        public string PlatformCode;
        public string SDRPersonId;
        public string PersonGenKey;
        Public String MessageFulfillmentId;
        Public Boolean Printable;
        Public String MessageDefinitionIDCD;
    }   
    
    public class EMAIL
    {
        Public String MessageDefinitionDescription;  
        Public String BusineesSubArea;
        Public DateTime startDateTime;
        Public DateTime endDateTime; 
        Public String StatusDescription;
        Public string RecipientEmail;
        Public String trackingID;
		Public String MessageDefinitionIDCD;
    } 
    
    public class EM
    {
        Public String MessageDefinitionDescription;  
        Public String BusineesSubArea;
        Public DateTime startDateTime;
        Public DateTime endDateTime; 
        Public String StatusDescription;
        Public string RecipientEmail;
        Public String trackingID;
		Public String MessageDefinitionIDCD;
    } 
    
    /*
     * interactingWithCode : Identifies the type Of ID that Is populated In SRC_INTRXN_WITH_ID, such As SRC_MBR_ID, IDCARD_MBR_ID, MEDICARE_ID etc.
     * interactingWithDesc : taxonomy lookup For description Of With type code
     * EndDateTime : The Event End Date Time
    */      
    Public Class MA
    {
       Public String interactingWithCode;
       Public String interactingWithDesc;
       public DateTime EndDateTime;
    }
    
    /*
     * interactingWithCode : Identifies the type Of ID that Is populated In SRC_INTRXN_WITH_ID, such As SRC_MBR_ID, IDCARD_MBR_ID, MEDICARE_ID etc.
     * interactingWithDesc : taxonomy lookup For description Of With type code
     * EndDateTime : The Event End Date Time
    */      
    Public Class MB
    {
        Public String interactingWithCode;
        Public String interactingWithDesc;
        public DateTime EndDateTime;
    }        
    
    /*
     * categoryCode : A high level classification Of the type Of Interaction data by subject, such As IVR, WEB, SURVEY
     * categoryDescription : The taxonomy lookup For description Of category code
     * departmentName: The Name Of the entity fulfulling the interaction request
     * statusDescription : The taxonomy lookup For description Of status code
     * statusCode : The Outcome(Description of status)
     * lastModuleCD : High level classification For the purpose Of the Interaction
     * lastModuleName : taxonomy lookup For description Of purpose category code
     * interactingWithCode : The Interacting With Code (M = Member, P=Provider)
     * interactingWithDesc : The taxonomy lookup For description Of With type code
     * startdatetime : The Event Start() Date Time
     * statusDescription : The taxonomy lookup For description Of status code
     * enddatetime : The Event End() Date Time
    */     
    Public Class IV
    {
        Public String categoryCode;
        Public String categoryDescription;
        Public DateTime startdatetime;
        Public DateTime enddatetime;
        Public String interactingWithCode;
        Public String interactingWithDesc;
        Public String statusDescription;
        Public String statusCode;
        Public String lastModuleCD;
        Public String lastModuleName;
        Public String departmentName;
            
    }
    
    /*
     * interactingWithCode : Identifies the type Of ID that Is populated In SRC_INTRXN_WITH_ID, such As SRC_MBR_ID, IDCARD_MBR_ID, MEDICARE_ID etc.
     * interactingWithDesc : taxonomy lookup For description Of With type code
     * EndDateTime : The Event End Date Time
    */      
    Public Class WB
    {
        Public String interactingWithCode;
        Public String interactingWithDesc;
        public DateTime EndDateTime;
        Public String MessageDefinitionDescription;
        Public String BusineesSubArea;
        Public String CategoryCode;
        Public String trackingID;
		Public String categoryDescription;
		Public String MessageDefinitionIDCD;
    } 
    Public Class WEB
    {
        Public String interactingWithCode;
        Public String interactingWithDesc;
        public DateTime EndDateTime;
        Public String MessageDefinitionDescription;
        Public String BusineesSubArea;
        Public String CategoryCode;
        Public String trackingID;       
		Public String categoryDescription;
		Public String MessageDefinitionIDCD;
    }

    /* SMS code response
     
     * businessSubArea : The Sub class() value For purpose, If one exists
     * startDateTime : The Interaction Timestamp. Actual time when the interaction was recorderd as start time in the source
     * endDateTime : Actual time when the interaction was recorderd as end time in the source
     * statusDescription : The taxonomy lookup for description of status code
    */    
    Public Class SMS
    {
        Public String MessageDefinitionDescription;
        Public String busineesSubArea;
        Public DateTime startDateTime;
        Public DateTime endDateTime;
        Public String statusDescription;
        Public String recipientPhone;
        Public String trackingID;
		Public String MessageDefinitionIDCD;
    }   
    
    //Vat code response
    public class VAT
    {
        Public String campaignName;
        Public DateTime startDateTime;
        Public DateTime endDateTime;
        Public String PhoneNumber;
        Public String statusDesc;
        Public String statusCode;
        Public String StatusDescription;
        Public String SubStatusCode;
        Public String SubStatusDesc;
        Public String trackingID;  
        Public String MessageDefinitionIDCD;                                            
    }
    
}