package net.fourinfo.gateway.model;

abstract class GenericRequest {
	protected String clientId;

	protected String clientKey;

	public GenericRequest() {
		// no-op
	}

	public GenericRequest(String clientId, String clientKey) {
		this.clientId = clientId;
		this.clientKey = clientKey;
	}

	/**
	 * @return the clientId
	 */
	public String getClientId() {
		return this.clientId;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the clientKey
	 */
	public String getClientKey() {
		return this.clientKey;
	}

	/**
	 * @param clientKey
	 *            the clientKey to set
	 */
	public void setClientKey(String clientKey) {
		this.clientKey = clientKey;
	}

	/**
	 * Get a XML byte array representing this object.
	 * 
	 * @return
	 */
	public abstract byte[] getXmlByteArray();

	public String toString() {
		return new String(getXmlByteArray());
	}
}
