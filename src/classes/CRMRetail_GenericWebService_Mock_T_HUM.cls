/*******************************************************************************************************************************
Apex Class Name     : CRMRetail_CRMRetail_GenericWebService_Mock_T_HUM
Version             : 1.0
Created Date        : 04/25/2023
Function            : This mock used to generate response for Storefront Web Services

Modification Log: 
* Developer Name           Code Review                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mohamed Thameem                                 	   	   04/26/2023                 User Story 4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic
*******************************************************************************************************************************/
@isTest
public class CRMRetail_GenericWebService_Mock_T_HUM implements HttpCalloutMock {
		protected Integer code;
		protected String status;
		protected String bodyAsString;
		protected Blob bodyAsBlob;
		protected Map<String, String> responseHeaders;

		public CRMRetail_GenericWebService_Mock_T_HUM(Integer code, String status, String body,
                                         Map<String, String> responseHeaders) {
			this.code = code;
			this.status = status;
			this.bodyAsString = body;
			this.bodyAsBlob = null;
			this.responseHeaders = responseHeaders;
		}

		public CRMRetail_GenericWebService_Mock_T_HUM(Integer code, String status, Blob body,
                                         Map<String, String> responseHeaders) {
			this.code = code;
			this.status = status;
			this.bodyAsBlob = body;
			this.bodyAsString = null;
			this.responseHeaders = responseHeaders;
		}

	    public HTTPResponse respond(HTTPRequest req) {
	        HttpResponse resp = new HttpResponse();
			resp.setStatusCode(code);
			resp.setStatus(status);
			if (bodyAsBlob != null) {
				resp.setBodyAsBlob(bodyAsBlob);
			} else {
				resp.setBody(bodyAsString);
			}

			if (responseHeaders != null) {
			     for (String key : responseHeaders.keySet()) {
				resp.setHeader(key, responseHeaders.get(key));
			     }
			}
			return resp;
	    }
}