package net.fourinfo.gateway;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import net.fourinfo.gateway.model.Address;
import net.fourinfo.gateway.model.Carrier;
import net.fourinfo.gateway.model.HandsetInfo;
import net.fourinfo.gateway.model.MessageRequest;
import net.fourinfo.gateway.model.MessageRequestParameters;
import net.fourinfo.gateway.model.QueryRequest;
import net.fourinfo.gateway.model.Response;
import net.fourinfo.gateway.model.Sms;
import net.fourinfo.gateway.model.UnblockRequest;
import net.fourinfo.gateway.model.ValidationRequest;
import net.fourinfo.gateway.xml.CarrierHandler;
import net.fourinfo.gateway.xml.MessageHandler;
import net.fourinfo.gateway.xml.ResponseHandler;

/**
 * Stateless communication interface with the 4INFO Gateway service.
 * 
 * @author Jason Thrasher
 */
public class Gateway {
    private static final int HTTP_OK = 200;

    /**
     * The url for the carrier list, like: http://gateway.4info.net/list
     */
    private String carrierListUrl = null;

    /**
     * The url for validating requests, like: http://gateway.4info.net/msg
     */
    private String validationRequestUrl = null;

    private String clientId;

    private String clientKey;

    private static final Log log = LogFactory.getLog(Gateway.class);

    private static SmsCharsetUtil charsetUtil = SmsCharsetUtil.getInstance();

    // static {
    // Properties props = getServiceProperties();
    // try {
    // setBaseUrl(props.getProperty("gateway.url"));
    // } catch (MalformedURLException murle) {
    // log.warn("invalid url: " + props.getProperty("gateway.url"), murle);
    // }
    // }

    public Gateway() {
	// no-op
    }

    public Gateway(URL gatewayUrl) throws MalformedURLException {
	setGatewayUrl(gatewayUrl.toExternalForm());
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
     * @return the carrierListUrl
     */
    public String getCarrierListUrl() {
	return carrierListUrl;
    }

    public void setCarrierListUrl(String carrierListUrl) {
	this.carrierListUrl = carrierListUrl;
    }

    /**
     * @return the validationRequestUrl
     */
    public String getValidationRequestUrl() {
	return validationRequestUrl;
    }

    public void setValidationRequestUrl(String validationRequestUrl) {
	this.validationRequestUrl = validationRequestUrl;
    }

    /**
     * Get the list of currently enabled carriers, and their ID values. 4INFO
     * can only send text messages to phone numbers belonging to the supported
     * carriers.
     * 
     * @return a list of Carriers
     * @throws IOException
     */
    public List<Carrier> getCarriers() throws IOException {
	HttpClient mClient = new HttpClient(new SimpleHttpConnectionManager());
	GetMethod get = null;
	try {
	    get = new GetMethod(carrierListUrl);
	    get.setFollowRedirects(true);

	    // check response code
	    int iGetResultCode = mClient.executeMethod(get);

	    if (iGetResultCode != HTTP_OK) {
		throw new IOException("HTTP " + iGetResultCode + " "
				      + get.getStatusText());
	    }

	    // read the XML
	    InputStream in = new BufferedInputStream(get
						     .getResponseBodyAsStream());
	    XMLReader xr = XMLReaderFactory.createXMLReader();
	    CarrierHandler handler = new CarrierHandler();
	    xr.setContentHandler(handler);
	    xr.setErrorHandler(handler);
	    xr.parse(new InputSource(in));
	    return handler.getCarriers();

	} catch (SAXException saxe) {
	    throw new IOException(saxe.getMessage());
	} catch (MalformedURLException murle) {
	    throw new IOException(murle.getMessage());
	} finally {
	    if (get != null) {
		get.releaseConnection();
	    }
	}
    }

    /**
     * Send a validation request for the given phone number. The Response will
     * include a confirmation code for local confirmation by the user. The user
     * should enter the conf code into the third party application to verify
     * that they own the given cell phone number.
     * 
     * @param clientId
     * @param clientKey
     * @param carrier
     * @param phoneNumber
     * @return the 4INFO response, with a confirmation code
     */
    public Response sendValidationRequest(Carrier carrier, String phoneNumber,
					  String clientId, String clientKey) throws IOException {
	ValidationRequest req = new ValidationRequest(clientId, clientKey);
	req.setCarrier(carrier);
	req.setPhoneNumber(phoneNumber);

	return getResponse(req.getXmlByteArray());
    }

    public Response sendValidationRequest(Carrier carrier, String phoneNumber)
	throws IOException {
	return sendValidationRequest(carrier, phoneNumber, clientId, clientKey);
    }

    public Response sendValidationRequest(String phoneNumber)
	throws IOException {
	return sendValidationRequest(null, phoneNumber, clientId, clientKey);
    }

    /**
     * Send a SMS message to the given phone number.
     * 
     * @param clientId
     * @param clientKey
     * @param carrier
     * @param phoneNumber
     * @param shortCode
     * @param message The SMS message to send
     * @return
     * @throws IOException
     */
    public Response sendMessageRequest(String message, String shortCode,
				       Carrier carrier, String phoneNumber,
				       HandsetInfo handsetInfo,
				       String clientId, String clientKey,
				       boolean handsetDeliveryReceipt)
	throws IOException {

	// convert the unicode message to GSM encoding
	// TODO: test that this works on a real phone
	// NOTE: 4INFO converts char sequence '\''n' to a new line
	message = message.replace("\n", "\\n");
	//	message = new String(charsetUtil.unicodeToGsm(message));

	MessageRequest req = new MessageRequest(clientId, clientKey);
	req.setCarrier(carrier);
	req.setPhoneNumber(phoneNumber);
	req.setHandsetInfo(handsetInfo);
	req.setShortCode(shortCode);
	req.setMessage(message);
	req.setHandsetDeliveryReceipt(handsetDeliveryReceipt);
	byte[] b = req.getXmlByteArray();
	System.out.println(new String(b));
	return getResponse(req.getXmlByteArray());
    }

    // Bottleneck 1 - param object version
    public Response sendMessageRequest(MessageRequestParameters params) 
    throws IOException {
	return sendMessageRequest
	    (params.message, params.shortCode, params.carrier, params.phoneNumber,
	     params.handsetInfo, params.clientId, params.clientKey,
	     params.handsetDeliveryReceipt);
    }

    // Bottleneck 2 - direct parameter list
    public Response sendMessageRequest(String message, String shortCode,
				       Carrier carrier, String phoneNumber,
				       HandsetInfo handsetInfo,
				       String clientId, String clientKey)
	throws IOException {
	return sendMessageRequest(message, shortCode, carrier, phoneNumber, handsetInfo, clientId, clientKey, false);
    }


    public Response sendMessageRequest(String message, String shortCode,
				       Carrier carrier, String phoneNumber,
				       String clientId, String clientKey)
	throws IOException {
	return sendMessageRequest(message, shortCode, carrier, phoneNumber,
				  null, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, Carrier carrier,
				       String phoneNumber, String clientId, String clientKey)
	throws IOException {
	return sendMessageRequest(message, null, carrier, phoneNumber, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, Carrier carrier,
				       String phoneNumber) throws IOException {
	return sendMessageRequest(message, null, carrier, phoneNumber, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, String shortCode, Carrier carrier,
				       String phoneNumber) throws IOException {
	return sendMessageRequest(message, shortCode, carrier, phoneNumber, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, String shortCode, Carrier carrier,
				       String phoneNumber, HandsetInfo handsetInfo) throws IOException {
	return sendMessageRequest(message, shortCode, carrier, phoneNumber, handsetInfo, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, Carrier carrier,
				       String phoneNumber, HandsetInfo handsetInfo) throws IOException {
	return sendMessageRequest(message, null, carrier, phoneNumber, handsetInfo, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, String phoneNumber) throws IOException {
	return sendMessageRequest(message, null, null, phoneNumber, clientId, clientKey);
    }

    public Response sendMessageRequest(String message, String shortCode, String phoneNumber) throws IOException {
	return sendMessageRequest(message, shortCode, null, phoneNumber, clientId, clientKey);
    }

    public Response sendMessageRequest(Sms sms, String clientId, String clientKey)
	throws IOException {
	Response r = sendMessageRequest(sms.getMessage(), sms.getSender().getPhoneNumber(),
					sms.getRecipient().getCarrier(), sms.getRecipient().getPhoneNumber(),
					clientId, clientKey);
	sms.setRequestId(r.getRequestId());
	return r;
    }

    public Response sendMessageRequest(Sms sms)
	throws IOException {
	Response r = sendMessageRequest(sms.getMessage(), sms.getSender().getPhoneNumber(),
					sms.getRecipient().getCarrier(), sms.getRecipient().getPhoneNumber(),
					clientId, clientKey);
	sms.setRequestId(r.getRequestId());
	return r;
    }

    /**
     * In order to facilitate a good user experience. Partners may register a URL where 4Info
     * will POST phone numbers that have been blacklisted and/or "STOP"ped by the user.
     * This allows the partner to correctly update the user's validation status in their own system,
     * and potentially prompt the user to revalidate the same phone, or validate a new phone.
     * Upon successful revalidation, the partner may activate the user's phone number again,
     * and remove it from the blacklist.
     * @param clientId
     * @param clientKey
     * @param carrier
     * @param phoneNumber
     * @return a status response
     * @throws IOException
     */
    public Response sendUnblockRequest(String clientId, String clientKey,
				       String phoneNumber, Carrier carrier)
	throws IOException
    {
	UnblockRequest req = new UnblockRequest(clientId, clientKey);
	req.setPhoneNumber(phoneNumber);
	req.setCarrier(carrier);
	return getResponse(req.getXmlByteArray());
    }

    public Response sendUnblockRequest(String phoneNumber, Carrier carrier)
	throws IOException
    {
	return sendUnblockRequest(clientId, clientKey, phoneNumber, carrier);
    }

    public Response sendUnblockRequest(Address address)
	throws IOException
    {
	return sendUnblockRequest(address.getPhoneNumber(), address.getCarrier());
    }

    /**
     * Refactored repeatedly used request/response code.
     * 
     * @param requestBody
     * @return
     * @throws IOException
     */
    private Response getResponse(byte[] requestBody) throws IOException {
	HttpClient mClient = new HttpClient(new SimpleHttpConnectionManager());
	PostMethod post = null;
	try {
	    post = new PostMethod(validationRequestUrl);
	    // post.setFollowRedirects(true);

	    // add the xml request
	    post.addRequestHeader("Content-Length", Integer
				  .toString(requestBody.length));
	    post.setRequestEntity(new ByteArrayRequestEntity(requestBody,
							     "text/xml"));

	    // check response code
	    int iGetResultCode = mClient.executeMethod(post);

	    if (iGetResultCode != HTTP_OK) {
		throw new IOException("HTTP " + iGetResultCode + " "
				      + post.getStatusText());
	    }

	    // read the XML
	    return getResponse(post.getResponseBodyAsStream());

	} catch (MalformedURLException murle) {
	    throw new IOException(murle.getMessage());
	} finally {
	    if (post != null) {
		post.releaseConnection();
	    }
	}
    }

    /**
     * Parse response body XML into a Response object.
     * 
     * @param in
     * @return
     * @throws IOException
     */
    private static Response getResponse(InputStream in) throws IOException {
	InputStream bin = new BufferedInputStream(in);
	try {
	    XMLReader xr = XMLReaderFactory.createXMLReader();
	    ResponseHandler handler = new ResponseHandler();
	    xr.setContentHandler(handler);
	    xr.setErrorHandler(handler);
	    xr.parse(new InputSource(bin));

	    return handler.getResponse();
	} catch (SAXException saxe) {
	    throw new IOException(saxe.getMessage());
	} finally {
	    if (in != null)
		in.close();
	}
    }

    /**
     * Parse an XML request for an inbound message into an Sms object.
     * 
     * @param in
     * @return Sms message object
     * @throws IOException
     */
    public static Sms parseSms(InputStream in) throws IOException {
	InputStream bin = new BufferedInputStream(in);
	try {
	    XMLReader xr = XMLReaderFactory.createXMLReader();
	    MessageHandler handler = new MessageHandler();
	    xr.setContentHandler(handler);
	    xr.setErrorHandler(handler);
	    xr.parse(new InputSource(bin));
	    return handler.getMessage();
	} catch (SAXException saxe) {
	    throw new IOException(saxe.getMessage());
	} finally {
	    if (in != null)
		in.close();
	}
    }


    /**
     * Load the service properties from the configuration file
     * "4info.properties" in the classpath.
     * 
     * @return
     */
    private static Properties getServiceProperties() {
	ClassLoader loader = ClassLoader.getSystemClassLoader();

	ResourceBundle bundle;
	String name = "4info";
	try {
	    if (loader == null) {
		bundle = PropertyResourceBundle.getBundle(name, Locale
							  .getDefault());
	    } else {
		bundle = PropertyResourceBundle.getBundle(name, Locale
							  .getDefault(), loader);
	    }
	} catch (MissingResourceException e) {
	    // Then, try to load from context classloader
	    bundle = PropertyResourceBundle.getBundle(name,
						      Locale.getDefault(), Thread.currentThread()
						      .getContextClassLoader());
	}

	Properties defProps = new Properties();
	for (Enumeration<String> keys = bundle.getKeys(); keys
		 .hasMoreElements();) {
	    final String key = (String) keys.nextElement();
	    final String value = bundle.getString(key);

	    defProps.put(key, value);
	}
	return defProps;
    }

    /**
     * Given the base url, setup the LIST and MSG urls.
     * 
     * @param baseUrl
     * @throws MalformedURLException
     */
    private void setGatewayUrl(String baseUrl) throws MalformedURLException {
	URL gateway = new URL(baseUrl);
	log.debug("gateway base url: " + gateway.toExternalForm());

	carrierListUrl = new URL(gateway, "list").toExternalForm();
	validationRequestUrl = new URL(gateway, "msg").toExternalForm();

	log.debug("carrierListUrl: " + carrierListUrl);
	log.debug("validationRequestUrl: " + validationRequestUrl);
    }

    public String toString() {
	return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
	    .append("clientId", this.clientId).append("clientKey",
						      this.clientKey).append("validationRequestUrl",
									     this.validationRequestUrl).append("carrierListUrl",
													       this.carrierListUrl).toString();
    }

    public static void main(String[] argv)
	throws Exception
    {
	Gateway g = new Gateway();
	g.setValidationRequestUrl("http://test-gateway.4info.net/msg");
	g.setClientId("25");
	g.setClientKey("23O5GIJ90QFJFMQR");
	try {
	    Response resp = g.sendMessageRequest("this _ is _ a _ message", new Carrier(5L, "Sprint PCS"), "+16505551212");
	    System.out.println(resp);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
