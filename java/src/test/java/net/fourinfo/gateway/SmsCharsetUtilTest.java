package net.fourinfo.gateway;

import junit.framework.TestCase;

public class SmsCharsetUtilTest extends TestCase {
	SmsCharsetUtil util;

	public SmsCharsetUtilTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		util = SmsCharsetUtil.getInstance();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUnicodeToGsm() throws Exception {
		String unicode = "This is a UNICODE string.";

		char[] gsmBytes = util.unicodeToGsm(unicode);
		String gsm = new String(gsmBytes);

		this.assertEquals(unicode, gsm);
		System.out.println("passed test with string: " + unicode);
	}

	public void testGsmToUnicode() throws Exception {
		String unicode = "This is a UNICODE string.";

		// convert To and then back To
		char[] gsmBytes = util.unicodeToGsm(unicode);
		String gsm = util.gsmToUnicode(new String(gsmBytes));

		this.assertEquals(unicode, gsm);
		System.out.println("passed test with string: " + unicode);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(SmsCharsetUtilTest.class);
	}
}