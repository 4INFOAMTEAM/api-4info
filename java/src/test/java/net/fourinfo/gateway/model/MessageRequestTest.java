package net.fourinfo.gateway.model;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.fourinfo.gateway.Gateway;

public class MessageRequestTest extends TestCase {
	protected final Log log = LogFactory.getLog(getClass());
	Properties props = new Properties();
	
	public MessageRequestTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		props.load(this.getClass().getResourceAsStream("/4info.properties"));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetXmlByteArray() throws Exception {
		String clientId = props.getProperty("gateway.clientId");
		String clientKey = props.getProperty("gateway.clientKey");
		String phoneNumber = props.getProperty("gateway.test.phoneNumber");
		String carrier = props.getProperty("gateway.test.carrier");
		String url = props.getProperty("gateway.url");
		String message = props.getProperty("gateway.test.message");
		
		MessageRequest req = new MessageRequest(clientId, clientKey);
		Carrier c = new Carrier(1L, "CARRIER");
		req.setCarrier(c);
		req.setPhoneNumber(phoneNumber);
		req.setMessage(message);

		byte[] buffXml = req.getXmlByteArray();
		String xml = new String(buffXml);

		log.debug(xml);

		this.assertNotNull(xml);
	}


	public static void main(String[] args) {
		junit.textui.TestRunner.run(MessageRequestTest.class);
	}
}
