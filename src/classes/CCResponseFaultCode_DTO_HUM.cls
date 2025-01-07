/**************************************************************************** 
Apex Class Name  : CCResponseFaultCode_DTO_HUM
Version          : 1.0  
Created Date     : March 20 2018 
Description      : This class is used as Fault Response
****************************************************************************
Modification Log:

* Developer Name          Code Review                Date             Description
*----------------------------------------------------------------------------------------------------------------
* sumit kumar              206605                    20/03/2018        Original Version
*****************************************************************************************************************/

public with sharing class CCResponseFaultCode_DTO_HUM{
    public Fault Fault;
    Public Class Fault { 
        Public String faultcode='';
        public String faultstring='';
        public detail detail;
    }
    public class detail {
        public transactionInfo transactionInfo;
        public errorInfo errorInfo;
    }
    public class transactionInfo {
        public String transaction_id='';
        public String transaction_client='';
        public String URL_in='';
        public String processor_name='';
        public String processor_type='';
    }
    public class errorInfo {
        public String error_code='';
        public String error_headers='';
        public String error_message='';
        public String error_protocol_reason_phrase='';
        public String error_protocol_response='';
        public String error_subcode='';
    }
}