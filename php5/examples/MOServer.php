<?php

require_once '../include/4Info_Gateway.php';
	
/**
 * MOServer
 * 
 * This is a simple server that can be used to receive xml SMS requests submitted to it.
 * The server makes use of the _4INFO_Gateway class helper function, getPostedXML(), to
 * retrieved XML data submitted to this script.  This script should be place in a publicly
 * accessible directory on your web server and MO messages from your third-party provider
 * should be posted to it.
 */
	 
	 
//Get the XML submitted to this server
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
?>