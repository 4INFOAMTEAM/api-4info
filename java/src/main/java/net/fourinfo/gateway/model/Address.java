package net.fourinfo.gateway.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * A phone number. Used to represent a user's handset or a service
 * provider's short code.
 * 
 * @author Garth Patil <a href="mailto:g@4info.net">g@4info.net</a>
 *
 */
public class Address {

    public static final int PHONE_NUMBER_TYPE = 5;

    public static final int SHORT_CODE_TYPE = 6;

    private String phoneNumber;

    private int addressType;

    private Carrier carrier;

    public Address() {
	// no-op
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
	return this.phoneNumber;
    }
    
    /**
     * @param phoneNumber
     *            the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    /**
     * @return the addressType
     */
    public int getAddressType() {
	return this.addressType;
    }
    
    /**
     * @param addressType
     *            the addressType to set
     */
    public void setAddressType(int addressType) {
	this.addressType = addressType;
    }

    /**
     * @return the carrier
     */
    public Carrier getCarrier() {
	return this.carrier;
    }
    
    /**
     * @param carrier
     *            the carrier to set
     */
    public void setCarrier(Carrier carrier) {
	this.carrier = carrier;
    }

    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (!(o instanceof Address))
	    return false;
	
	final Address s = (Address) o;
	
	return new EqualsBuilder().append(this.phoneNumber, s.phoneNumber).append(this.addressType, s.addressType).append(this.carrier, s.carrier).isEquals();
    }
    
    public int hashCode() {
	return new HashCodeBuilder(1923028325, -1034774645).append(this.phoneNumber).append(this.addressType).append(this.carrier).toHashCode();
    }
    
    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .append("phoneNumber", this.phoneNumber).append("addressType", this.addressType).append("carrier", this.carrier).toString();
    }
}
