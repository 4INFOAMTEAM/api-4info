package net.fourinfo.gateway.model;

/**
 * Store parameters for Gateway.sendMessageRequest() routine
 * 
 * @author John Finkelstein
 */
public class MessageRequestParameters {
    public String message = null;
    public String shortCode = null;
    public Carrier carrier = null;
    public String phoneNumber = null;
    public HandsetInfo handsetInfo = null;
    public String clientId = null;
    public String clientKey = null;
    public boolean handsetDeliveryReceipt = false;

    public MessageRequestParameters() {};

    public MessageRequestParameters(String message, String shortCode, Carrier carrier,
				    String phoneNumber, HandsetInfo handsetInfo,
				    String clientId, String clientKey,
				    boolean handsetDeliveryReceipt) {
	this.message = message;
	this.shortCode = shortCode;
	this.carrier = carrier;
	this.phoneNumber = phoneNumber;
	this.handsetInfo = handsetInfo;
	this.clientId = clientId;
	this.clientKey = clientKey;
	this.handsetDeliveryReceipt = handsetDeliveryReceipt;
    }
}
