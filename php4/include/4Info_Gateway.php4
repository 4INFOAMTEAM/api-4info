<?php
/*
 * 4Info Messaging Gateway
 *
 * The 4Info Messaging Gateway allows third parties to use 4Info’s SMS delivery platform to send 
 * text messages to end consumers over the 4Info short code (44636), and to send and receive text 
 * messages over their own short code. Immediate applications for this Gateway include phone 
 * number validation and delivery of general content messaging to end users. The Gateway can also 
 * use the 4Info advertisement server to include ads in messages.
 */

/**
 * A collection of constants the define the default parameters of the 4Info
 * mobile gateway.
 */
define('_4INFO_DEFAULT_CARRIER_LIST_URL', 'http://gateway.4info.net/list');
define('_4INFO_DEFAULT_MESSAGE_REQUEST_URL', 'http://gateway.4info.net/msg');
define('_4INFO_DEFAULT_SHORT_CODE', '44636');


/**
 * Global address type definitions
 */
define('_4INFO_PHONE_NUMBER_TYPE', 5);
define('_4INFO_SHORT_CODE_TYPE', 6);


/**
 * Global response status definitions
 */
define('_4INFO_RESPONSE_UNKNOWN', 0);
define('_4INFO_RESPONSE_SUCCESS', 1);
define('_4INFO_RESPONSE_FAILURE', 2);
define('_4INFO_RESPONSE_CONNECTION_FAILURE', 3);
define('_4INFO_RESPONSE_VALIDATION_ERROR', 4);
define('_4INFO_RESPONSE_AUTHENTICATION_FAILURE', 5);
define('_4INFO_RESPONSE_ADDRESSING_ERROR', 6);
define('_4INFO_RESPONSE_GATEWAY_ACK', 7);
define('_4INFO_RESPONSE_BROKER_ACK', 8);
define('_4INFO_RESPONSE_SMSC_ACK', 9);
define('_4INFO_RESPONSE_HANDSET_ACK', 10);
define('_4INFO_RESPONSE_RETRY_QUEUE', 11);
define('_4INFO_RESPONSE_CONFIRMATION_SUCCESS', 21);
define('_4INFO_RESPONSE_CONFIRMATION_FAILURE', 22);
define('_4INFO_RESPONSE_BLOCKED_USER', 101);
define('_4INFO_RESPONSE_DEACTIVATED_USER', 102);
define('_4INFO_RESPONSE_UNKNOWN_USER', 103);

	
/**
 * _4Info_Gateway
 *
 * The _4Info_Gateway class provides access to all gateway services.  To access services of the
 * 4Info Messaging Gateway, create an instance of this class passing in your client credentials.
 * Client credentials are given to you when you successfully register as a Gateway Partner.
 * 
 * The _4Info_Gateway class makes use of php's cURL library (http://www.php.net/curl) for 
 * submitting http requests and php's xml parser functions (http://www.php.net/xml) for
 * parsing xml data.
 * 
 * cURL library (http://www.php.net/curl)
 * To use PHP's cURL support you must also compile PHP --with-curl[=DIR] where DIR is the location 
 * of the directory containing the lib and include directories. In the "include" directory 
 * there should be a folder named "curl" which should contain the easy.h and curl.h files. 
 * There should be a file named libcurl.a located in the "lib" directory. Beginning with 
 * PHP 4.3.0 you can configure PHP to use cURL for URL streams --with-curlwrappers.
 * 
 * xml parser functions (http://www.php.net/xml)
 * These functions are enabled by default, using the bundled expat library. You can disable XML 
 * support with --disable-xml. If you compile PHP as a module for Apache 1.3.9 or later, PHP will
 * automatically use the bundled expat library from Apache. In order you don't want to use the 
 * bundled expat library configure PHP --with-expat-dir=DIR, where DIR should point to the base 
 * installation directory of expat. The windows version of PHP has built in support for this 
 * extension. You do not need to load any additional extension in order to use these functions.
 */
class _4Info_Gateway
{
  var $carrierListUrl;
  var $messageRequestUrl;
  var $clientId;
  var $clientKey;
		
		
  /**
   * Default constructor of a 4Info Gateway object.  The constructor should be passed
   * gateway credentials, including a clientId and clientKey.  If desired, a
   * carrier list url and a message request url can be specified, otherwise the default
   * 4Info gateway values will be used.
   */
  function _4Info_Gateway($clientId = false, $clientKey = false, $carrierListUrl = false, $messageRequestUrl = false)
  {
    $this->clientId = $clientId;
    $this->clientKey = $clientKey;
			
    if(!($this->carrierListUrl = $carrierListUrl))
      $this->carrierListUrl = _4INFO_DEFAULT_CARRIER_LIST_URL;
				
    if(!($this->messageRequestUrl = $messageRequestUrl))
      $this->messageRequestUrl = _4INFO_DEFAULT_MESSAGE_REQUEST_URL;
  }
		
		
  function setClientId($clientId)
  {
    $this->clientId = $clientId;
  }
		
		
  function setClientKey($clientKey)
  {
    $this->clientKey = $clientKey;
  }
		
		
  function setCarrierListUrl($carrierListUrl = false)
  {
    if($carrierListUrl)
      $this->carrierListUrl = $carrierListUrl;
    else
      $this->carrierListUrl = _4INFO_DEFAULT_CARRIER_LIST_URL;
  }
		
		
  function setMessageRequestUrl($messageRequestUrl = false)
  {
    if($messageRequestUrl)
      $this->messageRequestUrl = $messageRequestUrl;
    else
      $this->messageRequestUrl = _4INFO_DEFAULT_MESSAGE_REQUEST_URL;
  }
		
		
  /**
   * Carrier List Request
   * 
   * This service returns a list of supported carriers and their associated identifies for 
   * use in phone number validation and text message delivery can be queried using a GET 
   * over HTTP. Customers should cache this list, but it is important to refresh it 
   * periodically (at least once a day).
   */
  function getCarriers($shortCode = false)
  {
    if(!$shortCode)
      $shortCode = _4INFO_DEFAULT_SHORT_CODE;
			
    $xml = file_get_contents($this->carrierListUrl . '?clientId=' . $this->clientId . '&clientKey=' . $this->clientKey . '&shortCode=' . $shortCode);
			
    $parser = xml_parser_create();
    xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
    xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
    xml_parse_into_struct($parser, $xml, $values, $tags);
    xml_parser_free($parser);
			
    $carriers = array();
			
    foreach($values as $val => $node) {
      if($node['tag'] == 'carrier') {
	$carrier = new _4Info_Gateway_Carrier($node['attributes']['id'], $node['value']);
	$carriers[$carrier->id] = $carrier;
      }
    }
	        
    return $carriers;
  }
		
		
  /**
   * Phone Number Validation
   * 
   * 4Info provides a convenience for partners wishing to validate the telephone numbers of its 
   * users. Requiring the user to input a validation code sent to the phone is one way for the 
   * partner to enforce the opt-in process required by wireless providers. The phone validation 
   * service is an XML web service available for credentialed partners over HTTP POST.
   * 
   * After a phone validation request is submitted to the Messaging Gateway, 4Info delivers an 
   * SMS to the MSISDN provided which contains the same confirmation code returned to the partner 
   * in the HTTP response. Once the user receives the message, they submit the confirmation code 
   * to the third party for match verification. If the codes match, the partner considers the 
   * MSISDN confirmed and permits the end user to receive SMS content thereafter. If the codes 
   * do not match, the third party notifies the user. If need be, another confirmation code can 
   * be sent to the MSISDN by restarting the phone validation above.
   * 
   * This function submits a phone number validation request to the 4INFO Messaging Gateway
   * using the phone number passed in. A _4Info_Gateway_Response object is returned 
   * from this function call.  The returned _4Info_Gateway_Response object includes the 
   * success/failure status of the validation request and, if successful, the 5 digit 
   * confirmation code that was sent to the user.
   */
  function sendValidationRequest($phoneNumber)
  {
    $xml  = '<?xml version="1.0"?>';
    $xml .= '<request clientId="' . $this->clientId . '" clientKey="' . $this->clientKey . '" type="VALIDATION">';
    $xml .= '<validation>';
    $xml .= '<recipient>';
    $xml .= '<type>' . _4INFO_PHONE_NUMBER_TYPE . '</type>';
    $xml .= '<id>' . $phoneNumber . '</id>';
    $xml .= '</recipient>';
    $xml .= '</validation>';
    $xml .= '</request>';
	         
    return $this->makeRemoteCall($this->messageRequestUrl, $xml);
  }
		
		
  /**
   * Text Message Delivery
   * 
   * 4Info provides the ability for partners to send MT SMS messages to its users. This is 
   * available for credentialed partners as an XML web service over HTTP POST. The request adds 
   * the message to the 4Info Messaging Gateway queue, where an optional advertisement is appended 
   * to the message, and it is prepared for dispatch to the wireless operators. While this process 
   * is usually immediate, partners are not guaranteed that the message will be delivered at the 
   * time of request. Because of the asynchronous nature of the wireless operators' SMS 
   * infrastructure, delivery can be confirmed by the a status request at a later time.
   * 
   * This function accepts a _4Info_Gateway_Sms object, which contains the qualified address of 
   * the message sender, the qualified address of the message recipient, and the text to be
   * contained in the SMS message.  Using the credentials associated with this gateway instance,
   * the SMS message is posted to the 4Info mobile gateway for delivery.  The gateway's response
   * is encapsulated in a _4Info_Gateway_Response object and returned.  The
   * _4Info_Gateway_Response object includes the success/failure status of the attempted message
   * delivery and, if successful, includes a request ID that may subsequently be utilized by 
   * to query for the status of the response.
   */
  function sendMessageRequest($sms)
  {
    $xml  = '<?xml version="1.0"?>';
    $xml .= '<request clientId="' . $this->clientId . '" clientKey="' . $this->clientKey . '" type="MESSAGE">';
    $xml .= '<message receipt="' . $sms->handsetDeliveryReceipt . '">';
    $xml .= '<sender>';
    $xml .= '<type>' . _4INFO_SHORT_CODE_TYPE . '</type>';
    $xml .= '<id>' . $sms->sender->phoneNumber . '</id>';
    $xml .= '</sender>';
    $xml .= '<recipient>';
    $xml .= '<type>' . _4INFO_PHONE_NUMBER_TYPE . '</type>';
    $xml .= '<id>' . $sms->recipient->phoneNumber . '</id>';
    $xml .= '</recipient>';
    $xml .= '<text>' . $sms->message . '</text>';
    $xml .= '</message>';
    $xml .= '</request>';

    return $this->makeRemoteCall($this->messageRequestUrl, $xml);
  }
		

  /**
   * Unblock Request - Blacklist Management
   * 
   * In order to enforce compliance with wireless operators' and MMA guidelines, 
   * 4Info has implemented “STOP” functionality for any end users using 4Info or 
   * partner services. When a user sends a “STOP” message in response to a partner 
   * message, 4Info stores the number in a blacklist for this partner. MT SMS messages 
   * in the future will be refused by the Messaging Gateway with status message BLOCKED_USER.
   * 
   * In order to facilitate a good user experience, partners may register a URL where 
   * 4Info will POST phone numbers that have been blacklisted and/or “STOP”ped by the user. 
   * This allows the partner to correctly update the user's validation status in their own 
   * system, and potentially prompt the user to revalidate the same phone, or validate a 
   * new phone. Upon successful revalidation, the partner may activate the user's phone 
   * number again, and remove it from the blacklist.
   * 
   * This function sends an unblock request to the 4Info mobile gateway for the phone
   * number specified.  Using the credentials associated with this gateway instance,
   * the phone number is posted to the 4Info mobile gateway in an attempt to remove it from
   * the unblocked list.  The gateway's response is encapsulated in a _4Info_Gateway_Response 
   * object and returned.  The _4Info_Gateway_Response object includes the success/failure 
   * status of the unblock request.
   */
  function sendUnblockRequest($phoneNumber)
  {
    $xml  = '<?xml version="1.0"?>';
    $xml .= '<request clientId="' . $this->clientId . '" clientKey="' . $this->clientKey . '" type="UNBLOCK">';
    $xml .= '<unblock>';
    $xml .= '<recipient>';
    $xml .= '<type>' . _4INFO_PHONE_NUMBER_TYPE . '</type>';
    $xml .= '<id>' . $phoneNumber . '</id>';
    $xml .= '</recipient>';
    $xml .= '</unblock>';
    $xml .= '</request>';

    return $this->makeRemoteCall($this->messageRequestUrl, $xml); 

  }
	    
		
  /**
   * makeRemoteCall
   * 
   * This helper function is used to post XML data to a specified url.  It is
   * assumed the url being posted to will return an xml response formated as a
   * 4Info gateway response.  The passed in XML data string is POSTed over HTTP to 
   * the specified url using php's curl library.  Based HTTP authentication 
   * can be added to the function below if needed.  The XML response to the HTTP POST
   * is received and parsed into a _4Info_Gateway_Response object prior to being
   * returned.
   */
  function makeRemoteCall($url, $xml)
  {
    // Initialize CURL and set CURL options
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL,            $url);
    curl_setopt($ch, CURLOPT_HTTP_VERSION,   1);
    curl_setopt($ch, CURLOPT_POST,           1);
    curl_setopt($ch, CURLOPT_POSTFIELDS,     $xml);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); //Tell curl to return data
    curl_setopt($ch, CURLOPT_HEADER,         0); //Tell curl not to return headers
			
    // Make the HTTP request and receive the results
    $response = curl_exec($ch);
			
    //Check for a curl error
    if(curl_errno($ch)) {
      //TODO: provide better error handling
      print curl_error($ch);
      return false;
    }
			
    //Close the curl connection
    curl_close($ch);
			
    //Parse the response, grabbing only the body of the returned message
    return $this->parseResponse($response);
  }


  /**
   * This helper function parses a _4Info_Gateway_Response object from the passed
   * in XML data string.
   */
  function parseResponse($xml)
  {
    $parser = xml_parser_create();
    xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
    xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
    xml_parse_into_struct($parser, trim($xml), $values, $tags);
    xml_parser_free($parser);
			
    $response = new _4Info_Gateway_Response();
			
    foreach($values as $val => $node) {
      if($node['tag'] == 'id' && isset($node['value']))
	$response->statusId = $node['value'];
      if($node['tag'] == 'message' && isset($node['value']))
	$response->statusMessage = $node['value'];
      if($node['tag'] == 'requestId' && isset($node['value']))
	$response->requestId = $node['value'];
      if($node['tag'] == 'confCode' && isset($node['value']))
	$response->confCode = $node['value'];
    }
			
    //Validate that the response was received appropriately
    if($response->statusId)
      return $response;
			    
    return false;
  }
		
		
  /**
   * This helper function parses a _4Info_Gateway_Sms object from the passed
   * in XML data string.
   */
  function parseSms($xml)
  {
    $parser = xml_parser_create();
    xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
    xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
    xml_parse_into_struct($parser, trim($xml), $values, $tags);
    xml_parser_free($parser);
			
    $sms = new _4Info_Gateway_Sms();
			
    foreach($values as $val => $node) {
      if($node['tag'] == 'recipient' && $node['type'] == 'open') {
	$recipient = new _4Info_Gateway_Address();
	$address =& $recipient;
      }
      if($node['tag'] == 'sender' && $node['type'] == 'open') {
	$sender = new _4Info_Gateway_Address();
	$address =& $sender;
      }
      if($node['tag'] == 'type' && isset($node['value']))
	$address->addressType = $node['value'];
      if($node['tag'] == 'id' && isset($node['value']))
	$address->phoneNumber = $node['value'];
      if($node['tag'] == 'value' && isset($node['value']))
	$address->carrier = $node['value'];
      if($node['tag'] == 'recipient' && $node['type'] == 'close')
	$sms->recipient = $recipient;
      if($node['tag'] == 'sender' && $node['type'] == 'close')
	$sms->sender = $sender;				
      if($node['tag'] == 'message' && isset($node['attributes']['id']))
	$sms->requestId = $node['attributes']['id'];
      if($node['tag'] == 'text' && isset($node['value']))
	$sms->message = $node['value'];				
    }
			
    //Validate that the SMS message was received appropriately
    if($sms->recipient && $sms->sender && $sms->message)
      return $sms;
				
    return false;
  }
		
		
  /**
   * This helper function parses a _4Info_Gateway_Address object from the passed
   * in XML data string.
   */
  function parseBlock($xml)
  {
    $parser = xml_parser_create();
    xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
    xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
    xml_parse_into_struct($parser, trim($xml), $values, $tags);
    xml_parser_free($parser);
			
    $address = new _4Info_Gateway_Address();
			
    foreach($values as $val => $node) {
      if($node['tag'] == 'type' && isset($node['value']))
	$address->addressType = $node['value'];
      if($node['tag'] == 'id' && isset($node['value']))
	$address->phoneNumber = $node['value'];
      if($node['tag'] == 'value' && isset($node['value']))
	$address->carrier = $node['value'];
    }
			
    //Validate that the address was received appropriately
    if($address->phoneNumber)
      return $address;
			
    return false;
  }

		
  /**
   * This helper function parses a _4Info_Gateway_Receipt object from the passed
   * in XML data string.
   */
  function parseReceipt($xml)
  {
    $parser = xml_parser_create();
    xml_parser_set_option($parser, XML_OPTION_CASE_FOLDING, 0);
    xml_parser_set_option($parser, XML_OPTION_SKIP_WHITE, 1);
    xml_parse_into_struct($parser, trim($xml), $values, $tags);
    xml_parser_free($parser);
			
    $receipt = new _4Info_Gateway_Receipt();
			
    foreach($values as $val => $node) {
      if($node['tag'] == 'requestId' && isset($node['value']))
	$receipt->requestId = $node['value'];
      if($node['tag'] == 'id' && isset($node['value']))
	$receipt->status['id'] = $node['value'];
      if($node['tag'] == 'message' && isset($node['value']))
	$receipt->status['message'] = $node['value'];
    }
			
    //Validate that the address was received appropriately
    if($receipt->requestId)
      return $receipt;
			
    return false;
  }		
		
  /**
   * A helper function which can be used retrieve XML data posted to the current script.
   * There are two implementions for this function and you can use either depending
   * on preferences.  If you set the passed in parameter "useGlobals" to true, this function
   * will attempt to use the PHP global variable HTTP_RAW_POST_DATA to retrieve the POSTed
   * XML.  This is a memory intensive implementation that requires the always_populate_raw_post_data
   * php.ini directive to be set to true (http://www.php.net/manual/en/ini.core.php#ini.always-populate-raw-post-data).
   * If the parameter useGlobals is not set or set to false, this function uses PHP input stream
   * wrappers to retrieve the POSTed XML data.  This method is preferred and requires no
   * php.ini directives to be set.
   */
  function getPostedXML($useGlobals = false)
  {
    //Two options of setting up a servlet to accept raw xml data posted to it, use
    //your preferred method
			
    // 1) Use HTTP_RAW_POST_DATA (memory intensive and requires php.ini directives set)
    if($useGlobals) {
      if(!isset($GLOBALS['HTTP_RAW_POST_DATA'])) {
	return false;
      }
      return trim($GLOBALS['HTTP_RAW_POST_DATA']);
    }
			
    //2) Use PHP input stream wrapper (requires PHP 4.3.0+, http://www.php.net/manual/en/wrappers.php)
    if(!$useGlobals) {
      return file_get_contents("php://input");
    }
  }

}

/**
 * _4Info_Gateway_Sms
 * 
 * An SMS message.  SMS messages have a sender address, a recipient address,
 * a message body, and a requestId that uniquely identifies the message.
 */
class _4Info_Gateway_Sms
{
  var $sender;
  var $recipient;
  var $message;
  var $requestId;
  var $handsetDeliveryReceipt;
		
  function _4Info_Gateway_Sms($sender = false, $recipient = false, $message = false, $requestId = false, $handsetDeliveryReceipt = false)
  {
    $this->sender = $sender;
	
	//Set default sender shortcode if it wasn't passed in
	if (empty($this->sender->phoneNumber))
	  $this->sender->phoneNumber = _4INFO_DEFAULT_SHORT_CODE;

    $this->recipient = $recipient;
    $this->message = $message;
    $this->requestId = $requestId;
	$this->handsetDeliveryReceipt = $handsetDeliveryReceipt ? 'true' : 'false';
  }
}

/**
 * _4Info_Gateway_Address
 * 
 * A simple mobile address.  A mobile address is defined with a type, either a phone number or
 * short code, a phone number which identifies the address, and a carrier the phone number is
 * operated by, if applicable.
 */
class _4Info_Gateway_Address
{
  var $phoneNumber;
  var $addressType;
  var $carrier;

  function _4Info_Gateway_Address($phoneNumber = false, $addressType = false, $carrier = false)
  {
    $this->phoneNumber = $phoneNumber;
    $this->addressType = $addressType;
    $this->carrier = $carrier;
  }
}

/**
 * _4Info_Gateway_Receipt
 * 
 * A simple handset delivery receipt. A receipt is defined with a request id, status id and
 * status message.
 */
class _4Info_Gateway_Receipt
{		
  var $requestId;
  var $status;

  function _4Info_Gateway_Receipt($requestId = false, $status = array('id' => null, 'message' => null))
  {
    $this->requestId = $requestId;
    $this->status = $status;
  }
}

/**
 * _4Info_Gateway_Carrier
 * 
 * A mobile carrier. Mobile carriers are defined with a unique id and name. 
 */
class _4Info_Gateway_Carrier
{
  var $id;
  var $name;
		
  function _4Info_Gateway_Carrier($id = false, $name = false)
  {
    $this->id = $id;
    $this->name = $name;
  }
}


/**
 * _4Info_Gateway_Response
 * 
 * Common response.
 */
class _4Info_Gateway_Response
{
  var $requestId;
  var $confCode;
  var $statusId;
  var $statusMessage;
		
		
  function _4Info_Gateway_Response($requestId = false, $confCode = false, $statusId = false, $statusMessage = false)
  {
    $this->requestId = $requestId;
    $this->confCode = $confCode;
    $this->statusId = $statusId;
    $this->statusMessage = $statusMessage;
  }
		
}
	
?>