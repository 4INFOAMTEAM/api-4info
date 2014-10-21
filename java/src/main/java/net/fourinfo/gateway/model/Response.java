package net.fourinfo.gateway.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a generic 4INFO response. This response object is returned for
 * Validation, Message, and Query requests.
 * 
 * @author Jason Thrasher
 * 
 */
public class Response {
	/**
	 * Status is unknown. The request identifier was unknown to the messaging
	 * gateway.
	 */
	public static final int UNKNOWN = 0;

	/**
	 * Success. A general success message is returned when the message has been
	 * delivered, but 4INFO cannot identify the specific success status (6-10).
	 */
	public static final int SUCCESS = 1;

	/**
	 * Failure. The messaging gateway failed to deliver the message to the phone
	 * provided. No retries will occur.
	 */
	public static final int FAILURE = 2;

	/**
	 * Connection Failure. A network error occurred. Retry will occur.
	 */
	public static final int CONNECTION_FAILURE = 3;

	/**
	 * Validation Error. The request contained invalid syntax.
	 */
	public static final int VALIDATION_ERROR = 4;

	/**
	 * Authentication Failure. Credentials provided for the request were
	 * inaccurate or insufficient.
	 */
	public static final int AUTHENTICATION_FAILURE = 5;

	/**
	 * Addressing Error. The phone number was incorrect or unknown.
	 */
	public static final int ADDRESSING_ERROR = 6;

	/**
	 * Successfully queued at gateway. 4INFO's messaging gateway received the
	 * request and successfully queued the message for delivery.
	 */
	public static final int GATEWAY_ACK = 7;

	/**
	 * Successfully queued at broker. An external messaging gateway received the
	 * request and successfully queued the message for delivery.
	 */
	public static final int BROKER_ACK = 8;

	/**
	 * Successfully queued at SMSC. A carrier's SMSC received the request and
	 * successfully queued the message for delivery.
	 */
	public static final int SMSC_ACK = 9;

	/**
	 * Handset receipt acknowledgment. The handset received the message.
	 */
	public static final int HANDSET_ACK = 10;

	private String requestId = null;

	private String confCode = null;

	private int statusId = -1;

	private String statusMessage = null;

	public Response() {
		// no-op
	}

	// public void parseXml(InputStream in) throws IOException {
	// DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	// // factory.setValidating(true);
	// // factory.setNamespaceAware(true);
	// try {
	// DocumentBuilder builder = factory.newDocumentBuilder();
	// Document document = builder.parse(in);
	//
	// } catch (SAXException sxe) {
	// // Error generated during parsing)
	// Exception x = sxe;
	// if (sxe.getException() != null)
	// x = sxe.getException();
	// x.printStackTrace();
	//
	// } catch (ParserConfigurationException pce) {
	// // Parser with specified options can't be built
	// pce.printStackTrace();
	// }
	// }

	/**
	 * Request Identifier: The unique identifier returned by the 4INFO Messaging
	 * Gateway is in the Universally Unique Identifier (UUID). A UUID is
	 * essentially a 16-byte (128 bit) number and in its canonical form a UUID
	 * may look like this: 550e8400-e29b-41d4-a716-446655440000
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

	/**
	 * @return the confCode, or null if the request was not a ValidationRequest
	 */
	public String getConfCode() {
		return this.confCode;
	}

	/**
	 * @param confCode
	 *            the confCode to set
	 */
	public void setConfCode(String confCode) {
		this.confCode = confCode;
	}

	/**
	 * @return the statusId
	 */
	public int getStatusId() {
		return this.statusId;
	}

	/**
	 * @param statusId
	 *            the statusId to set
	 */
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return this.statusMessage;
	}

	/**
	 * @param statusMessage
	 *            the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Response))
			return false;

		final Response r = (Response) o;

		return new EqualsBuilder().append(this.requestId, r.requestId).append(
				this.statusId, r.statusId).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(1923028325, -1034774645).append(
				this.requestId).toHashCode();
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("requestId", this.requestId).append("statusId",
						this.statusId).append("statusMessage",
						this.statusMessage).append("confCode", this.confCode)
				.toString();
	}
}
