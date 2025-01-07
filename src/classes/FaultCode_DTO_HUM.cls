/**
 * @File Name          : FaultCode_DTO_HUM.cls
 * @Description        : DTO Class for Faultcode recieved during Service Calls
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/12/2020, 1:19:02 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public class FaultCode_DTO_HUM {

	public class ErrorInfo {
		public String error_code {get;set;} // in json: error-code
		public String error_headers {get;set;} // in json: error-headers
		public String error_message {get;set;} // in json: error-message
		public String error_protocol_reason_phrase {get;set;} // in json: error-protocol-reason-phrase
		public String error_protocol_response {get;set;} // in json: error-protocol-response
		public String error_subcode {get;set;} // in json: error-subcode

		public ErrorInfo(JSONParser parser) {
			while (parser.nextToken() != System.JSONToken.END_OBJECT) {
				if (parser.getCurrentToken() == System.JSONToken.FIELD_NAME) {
					String text = parser.getText();
					if (parser.nextToken() != System.JSONToken.VALUE_NULL) {
						if (text == 'error-code') {
							error_code = parser.getText();
						} else if (text == 'error-headers') {
							error_headers = parser.getText();
						} else if (text == 'error-message') {
							error_message = parser.getText();
						} else if (text == 'error-protocol-reason-phrase') {
							error_protocol_reason_phrase = parser.getText();
						} else if (text == 'error-protocol-response') {
							error_protocol_response = parser.getText();
						} else if (text == 'error-subcode') {
							error_subcode = parser.getText();
						} else {
							consumeObject(parser);
						}
					}
				}
			}
		}
	}
	
	public class TransactionInfo {
		public String transaction_id {get;set;} // in json: transaction-id
		public String transaction_client {get;set;} // in json: transaction-client
		public String URL_in {get;set;} // in json: URL-in
		public String processor_name {get;set;} // in json: processor-name
		public String processor_type {get;set;} // in json: processor-type

		public TransactionInfo(JSONParser parser) {
			while (parser.nextToken() != System.JSONToken.END_OBJECT) {
				if (parser.getCurrentToken() == System.JSONToken.FIELD_NAME) {
					String text = parser.getText();
					if (parser.nextToken() != System.JSONToken.VALUE_NULL) {
						if (text == 'transaction-id') {
							transaction_id = parser.getText();
						} else if (text == 'transaction-client') {
							transaction_client = parser.getText();
						} else if (text == 'URL-in') {
							URL_in = parser.getText();
						} else if (text == 'processor-name') {
							processor_name = parser.getText();
						} else if (text == 'processor-type') {
							processor_type = parser.getText();
						} else {
							consumeObject(parser);
						}
					}
				}
			}
		}
	}
	
	public class Fault {
		public String faultcode {get;set;} 
		public String faultstring {get;set;} 
		public Detail detail {get;set;} 

		public Fault(JSONParser parser) {
			while (parser.nextToken() != System.JSONToken.END_OBJECT) {
				if (parser.getCurrentToken() == System.JSONToken.FIELD_NAME) {
					String text = parser.getText();
					if (parser.nextToken() != System.JSONToken.VALUE_NULL) {
						if (text == 'faultcode') {
							faultcode = parser.getText();
						} else if (text == 'faultstring') {
							faultstring = parser.getText();
						} else if (text == 'detail') {
							detail = new Detail(parser);
						} else {
							consumeObject(parser);
						}
					}
				}
			}
		}
	}
	
	public Fault Fault {get;set;} 

	public FaultCode_DTO_HUM(JSONParser parser) {
		while (parser.nextToken() != System.JSONToken.END_OBJECT) {
			if (parser.getCurrentToken() == System.JSONToken.FIELD_NAME) {
				String text = parser.getText();
				if (parser.nextToken() != System.JSONToken.VALUE_NULL) {
					if (text == 'Fault') {
						Fault = new Fault(parser);
					} else {
						System.debug(LoggingLevel.WARN, 'FaultCode_DTO_HUM consuming unrecognized property: '+text);
						consumeObject(parser);
					}
				}
			}
		}
	}
	
	public class Detail {
		public TransactionInfo transactionInfo {get;set;} 
		public ErrorInfo errorInfo {get;set;} 

		public Detail(JSONParser parser) {
			while (parser.nextToken() != System.JSONToken.END_OBJECT) {
				if (parser.getCurrentToken() == System.JSONToken.FIELD_NAME) {
					String text = parser.getText();
					if (parser.nextToken() != System.JSONToken.VALUE_NULL) {
						if (text == 'transactionInfo') {
							transactionInfo = new TransactionInfo(parser);
						} else if (text == 'errorInfo') {
							errorInfo = new ErrorInfo(parser);
						} else {
							System.debug(LoggingLevel.WARN, 'Detail consuming unrecognized property: '+text);
							consumeObject(parser);
						}
					}
				}
			}
		}
	}
	
	
	public static FaultCode_DTO_HUM parse(String json) {
		System.JSONParser parser = System.JSON.createParser(json);
		return new FaultCode_DTO_HUM(parser);
	}
	
	public static void consumeObject(System.JSONParser parser) {
		Integer depth = 0;
		do {
			System.JSONToken curr = parser.getCurrentToken();
			if (curr == System.JSONToken.START_OBJECT || 
				curr == System.JSONToken.START_ARRAY) {
				depth++;
			} else if (curr == System.JSONToken.END_OBJECT ||
				curr == System.JSONToken.END_ARRAY) {
				depth--;
			}
		} while (depth > 0 && parser.nextToken() != null);
	}

}