package net.fourinfo.gateway.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a 4INFO SMS message. This is used to sending and receiving
 * SMS messages using the Gateway.
 * 
 * @author Garth Patil <a href="mailto:g@4info.net">g@4info.net</a>
 * 
 */
public class Sms {

    private Address sender;

    private Address recipient;

    private String message = null;

    private String requestId = null;

    public Sms() {
	// no-op
    }

    /**
     * @return the sender
     */
    public Address getSender() {
	return this.sender;
    }
    
    /**
     * @param sender
     *            the sender to set
     */
    public void setSender(Address sender) {
	this.sender = sender;
    }
    
    /**
     * @return the recipient
     */
    public Address getRecipient() {
	return this.recipient;
    }
    
    /**
     * @param recipient
     *            the recipient to set
     */
    public void setRecipient(Address recipient) {
	this.recipient = recipient;
    }
    
    /**
     * @return the message
     */
    public String getMessage() {
	return this.message;
    }
    
    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
	this.message = message;
    }
    
    /**
     * Request Identifier: The unique identifier returned by the 4INFO Messaging
     * Gateway is in the Universally Unique Identifier (UUID). A UUID is
     * essentially a 16-byte (128 bit) number and in its canonical form a UUID
     * may look like this: 550e8400-e29b-41d4-a716-446655440000
     * This value is not set until a successful message send process is complete, 
     * or the Sms is an inbound request.
     * 
     * @return the requestId
     */
    public String getRequestId() {
	return this.requestId;
    }
    
    /**
     * @param requestId
     *            the requestId to set
     */
    public void setRequestId(String requestId) {
	this.requestId = requestId;
    }

    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof Sms))
	    return false;
	
	final Sms s = (Sms) o;
	
	return new EqualsBuilder().append(this.sender, s.sender).append(this.recipient, s.recipient).append(this.message, s.message).append(this.requestId, s.requestId).isEquals();
    }
    
    public int hashCode() {
	return new HashCodeBuilder(1923028325, -1034774645).append(this.sender).append(this.recipient).append(this.message).append(this.requestId).toHashCode();
    }
    
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .append("sender", this.sender).append("recipient", this.recipient).append("message", this.message).append("requestId", this.requestId).toString();
    }
    
}
