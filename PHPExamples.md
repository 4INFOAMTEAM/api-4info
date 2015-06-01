# PHP Messaging Gateway API Implementation #

## PHP4 ##
An implementation of the Messaging Gateway API has been implemented for users of PHP4. The implementation uses PHP4's cURL library and XML parsing library.  In order to use this implementation, you will need both libraries installed.

  * cURL Library (http://www.php.net/curl) To use PHP's cURL support you must also compile PHP --with-curl[=DIR] where DIR is the location of the directory containing the lib and include directories. In the "include" directory there should be a folder named "curl" which should contain the easy.h and curl.h files. There should be a file named libcurl.a located in the "lib" directory. Beginning with PHP 4.3.0 you can configure PHP to use cURL for URL streams --with-curlwrappers.

  * XML Parsing Functions (http://www.php.net/xml)
These functions are enabled by default, using the bundled expat library. You can disable XML support with --disable-xml. If you compile PHP as a module for Apache 1.3.9 or later, PHP will automatically use the bundled expat library from Apache. In order you don't want to use the bundled expat library configure PHP --with-expat-dir=DIR, where DIR should point to the base installation directory of expat. The windows version of PHP has built in support for this extension. You do not need to load any additional extension in order to use these functions.

## PHP5 ##
An implementation of the Messaging Gateway API has been implemented for users of PHP5.
The implementation uses the PHP5's PECL HTTP library and simpleXML parsing library. In
order to use this implementation, you will need both libraries installed.

  * pecl http library (http://pecl.php.net/package/pecl_http) This PECL extension is not bundled with PHP.  Information for installing this PECL extension may be found in the manual chapter titled Installation of PECL extensions.

  * simplexml functions (http://php.net/simplexml) The SimpleXML extension provides a very simple and easily usable toolset to convert XML to an object that can be processed with normal property selectors and array iterators.


## Outbound (MT) messaging sample ##

### Set up the gateway: ###
The constructor should be passed gateway credentials, including a clientId and clientKey.
If desired, a carrier list url and a message request url can be specified, otherwise the
default 4Info gateway values will be used.

```
$gateway = new _4Info_Gateway();
$gateway->setClientId('25');
$gateway->setClientKey('283G1835JO1FM1F');
$gateway->setCarrierListUrl('http://gateway.4info.net/list');
$gateway->setMessageRequestUrl('http://gateway.4info.net/msg');
```

or

```
$gateway = new _4Info_Gateway('25','283G1835JO1FM1F');
```


### Get carrier list: ###
To retrieve a list of supported carriers for the 4INFO Messaging Gateway,
instantiate a Gateway object with your client credentials and request a carrier list.

```
$gateway = new _4Info_Gateway('25','283G1835JO1FM1F');
$carriers = $gateway->getCarriers();
```


### Send a validation SMS message: ###

```
$gateway = new _4Info_Gateway('25','283G1835JO1FM1F');
$phoneNumber = '+16505551212';
$response = $gateway->sendValidationRequest($phoneNumber);
```


### Send an SMS message: ###

```
$gateway = new _4Info_Gateway('25','283G1835JO1FM1F');
$sender = new _4Info_Gateway_Address('44636');
$recipient = new _4Info_Gateway_Address('+16505551212');
$message = 'Test message.';
$handsetDeliveryReceipt = false;
$sms = new _4Info_Gateway_Sms($sender, $recipient, $message, $handsetDeliveryReceipt);
$response = $gateway->sendMessageRequest($sms);
```


## Inbound (MO) messaging sample ##

```
//Get the XML submitted to this servlet
$xml = _4INFO_Gateway::getPostedXML(false);
 
//Check that some xml was posted
if(!$xml) {
        header("HTTP/1.1 503 Service Unavailable");
        exit;
}

// Parse the incoming XML SMS message
$sms = _4INFO_Gateway::parseSms($xml);

// TODO: Dispatch the sms to your application for processing

// Respond indicating a successful receipt of the inbound message
header("HTTP/1.1 202 Accepted");
```


## Inbound Blacklist sample ##

```
//Get the XML submitted to this servlet
$xml = _4INFO_Gateway::getPostedXML(false);
 
//Check that some xml was posted
if(!$xml) {
        header("HTTP/1.1 503 Service Unavailable");
        exit;
}

// Parse the incoming XML blacklist notification
$address = _4INFO_Gateway::parseBlock($xml);

// TODO: Dispatch the address to your application for processing

// Respond indicating a successful receipt of the inbound message
header("HTTP/1.1 202 Accepted");
```