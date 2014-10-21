package net.fourinfo.gateway.model;

import junit.framework.TestCase;

public class QueryRequestTest extends TestCase {
	public QueryRequestTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetXmlByteArray() throws Exception {
		QueryRequest req = new QueryRequest("123", "32IFJ23OFIJIWR");
		Carrier carrier = new Carrier(new Long(5), "CARRIER");
		req.setRequestId("F81D4FAE-7DEC-11D0-A765-00A0C91E6BF6");

		byte[] buffXml = req.getXmlByteArray();
		String xml = new String(buffXml);

		System.out.println(xml);

		this.assertNotNull(xml);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(QueryRequestTest.class);
	}
}
