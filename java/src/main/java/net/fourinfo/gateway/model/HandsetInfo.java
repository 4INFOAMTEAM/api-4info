package net.fourinfo.gateway.model;

/**
 * Stores handset device information: manufacturer, user agent, model
 * 
 * @author Jason Thrasher
 */
public class HandsetInfo {

    	private Integer handsetMfgId;

	private String userAgent;

	private String handsetModel;

	public HandsetInfo() {

	}

	public HandsetInfo(Integer handsetMfgId, String userAgent, String handsetModel) {
	    this.handsetMfgId = handsetMfgId;
	    this.userAgent = userAgent;
	    this.handsetModel = handsetModel;
	}

	/**
	 * @return the handsetMfgId
	 */
	public Integer getHandsetMfgId() {
		return this.handsetMfgId;
	}

	/**
	 * @param handsetMfgId
	 *            the handsetMfgId to set
	 */
	public void setHandsetMfgId(Integer handsetMfgId) {
		this.handsetMfgId = handsetMfgId;
	}

	/**
	 * @return the userAgent
	 */
	public String getUserAgent() {
		return this.userAgent;
	}

	/**
	 * @param userAgent
	 *            the userAgent to set
	 */
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * @return the handsetModel
	 */
	public String getHandsetModel() {
		return this.handsetModel;
	}

	/**
	 * @param handsetModel
	 *            the handsetModel to set
	 */
	public void setHandsetModel(String handsetModel) {
		this.handsetModel = handsetModel;
	}

}
