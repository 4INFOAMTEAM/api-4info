<?php

require_once '../include/4Info_Gateway.php4';
	
/**
 * Blacklist Server
 * 
 * This is a simple server that can be used to receive xml black list requests submitted to it.
 * The server makes use of the _4INFO_Gateway class helper function, getPostedXML(), to
 * retrieved XML data submitted to this script.  This script should be place in a publicly
 * accessible directory on your web server and registered with 4INFO to receive blacklist
 * requests.
 */
	 
	 
//Get the XML submitted to this server
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
?>