package net.fourinfo.gateway;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fourinfo.gateway.model.Carrier;
import net.fourinfo.gateway.model.HandsetInfo;
import net.fourinfo.gateway.model.Response;
import net.fourinfo.gateway.model.MessageRequestParameters;

public class GatewayTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());

	Gateway gateway;

	String carrierName;

	String phoneNumber;

	HandsetInfo handsetInfo;

	boolean handsetDeliveryReceipt;

	String shortCode;

	String message;

	String requestId = null;

	String clientId;

	String clientKey;

	public GatewayTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream("/4info.properties"));

		// configure the test gateway POJO
		gateway = new Gateway();
		gateway.setClientId(props.getProperty("gateway.clientId"));
		gateway.setClientKey(props.getProperty("gateway.clientKey"));
		gateway.setCarrierListUrl(props.getProperty("gateway.carrierListUrl"));
		gateway.setValidationRequestUrl(props
				.getProperty("gateway.validationRequestUrl"));
		log.info("gateway: " + gateway);

		// test config file values
		shortCode = props.getProperty("gateway.test.shortCode");
		phoneNumber = props.getProperty("gateway.test.phoneNumber");
		carrierName = props.getProperty("gateway.test.carrier");
		handsetInfo = new HandsetInfo();
		try {
		    handsetInfo.setHandsetMfgId(Integer.parseInt
						(props.getProperty("gateway.test.handsetMfgId")));
		} catch (Exception ignore) {}
		handsetInfo.setUserAgent(props.getProperty("gateway.test.userAgent"));
		handsetInfo.setHandsetModel(props.getProperty("gateway.test.handsetModel"));

		handsetDeliveryReceipt = "1".equalsIgnoreCase(props.getProperty("gateway.test.handsetDeliveryReceipt"));

		message = props.getProperty("gateway.test.message");

		// make sure we're ready!
		assertNotNull(carrierName);
		assertNotNull(phoneNumber);
		assertNotNull(message);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetCarriers() throws Exception {
		List<Carrier> carriers = gateway.getCarriers();
		for (Carrier c : carriers) {
			System.out.println(c);
		}
		assertNotNull(carriers);
		assertTrue(carriers.size() > 0);
	}

	public void testSendValidationRequest() throws Exception {
		Carrier carrier = getCarrier(carrierName);
		Response response = null;

		try {
			response = gateway.sendValidationRequest(carrier, phoneNumber);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		System.out.println("response: " + response);

		assertNotNull(response);

		// we should get a conf code back
		String confCode = response.getConfCode();
		System.out.println("Confirmation Code sent to phone: " + confCode);
		assertNotNull(confCode);

		// values used by other tests
		requestId = response.getRequestId();
		assertNotNull(requestId);
	}

	public void testSendMessageRequest() throws Exception {
		Carrier carrier = getCarrier(carrierName);

		Response response = null;

		MessageRequestParameters params = new MessageRequestParameters();
		params.message = message;
		params.carrier = carrier;
		params.phoneNumber = phoneNumber;
		params.handsetInfo = handsetInfo;
		params.handsetDeliveryReceipt = handsetDeliveryReceipt;
		try {
		    
			response = gateway.sendMessageRequest(params);
			requestId = response.getRequestId();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		System.out.println("response: " + response);

		assertNotNull(response);
	}

	public void testSendMessageRequestWithShortCode() throws Exception {
		Carrier carrier = getCarrier(carrierName);

		Response response = null;

		MessageRequestParameters params = new MessageRequestParameters();
		params.message = message;
		params.shortCode = shortCode;
		params.carrier = carrier;
		params.phoneNumber = phoneNumber;
		params.handsetInfo = handsetInfo;
		params.handsetDeliveryReceipt = handsetDeliveryReceipt;
		try {
		    System.out.println("SHORT CODE = "+shortCode);
			response = gateway.sendMessageRequest(params);
			requestId = response.getRequestId();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		System.out.println("response: " + response);

		assertNotNull(response);
	}


	/**
	 * Utility method to get the given carrier name. This is a private method,
	 * rather than a test method because we don't want one test to rely on
	 * another: each test should be able to execute on it's own.
	 * 
	 * @param name
	 *            of the carrier should match valid values form 4INFO
	 * @return a Carrier object, with a valid ID value, if the name is found by
	 *         4INFO, null if not found.
	 * @throws Exception
	 */
	private Carrier getCarrier(String name) throws Exception {
		Carrier carrier = null;
		List<Carrier> carriers = gateway.getCarriers();
		assertNotNull(carriers);
		assertTrue(carriers.size() > 0);

		// set the carrier for other tests to use
		for (Carrier c : carriers) {
			log.debug("carrier: " + c);
			if (name.equalsIgnoreCase(c.getName())) {
				carrier = c;
				break;
			}
		}

		assertNotNull(carrier);
		return carrier;
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(GatewayTest.class);
	}
}
