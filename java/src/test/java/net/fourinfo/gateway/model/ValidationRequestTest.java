package net.fourinfo.gateway.model;

import junit.framework.TestCase;

public class ValidationRequestTest extends TestCase {
	public ValidationRequestTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetXmlByteArray() throws Exception {
		ValidationRequest req = new ValidationRequest("123", "32IFJ23OFIJIWR");
		Carrier carrier = new Carrier(new Long(5), "CARRIER");
		req.setCarrier(carrier);
		req.setPhoneNumber("(202) 555-1212");
		byte[] buffXml = req.getXmlByteArray();
		String xml = new String(buffXml);

		System.out.println(xml);

		this.assertNotNull(xml);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(ValidationRequestTest.class);
	}
}
