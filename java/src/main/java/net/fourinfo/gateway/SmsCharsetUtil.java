package net.fourinfo.gateway;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SmsCharsetUtil {
	private static final Log mLog = LogFactory.getLog(SmsCharsetUtil.class);

	private static SmsCharsetUtil mInstance = null;

	private static final String GSM_CHARSET_FILE = "/gsm0338.txt";

	public static final char EXT_TABLE_PREFIX = 0x1B;

	private static char[] GSM_ALPHABET_TABLE;

	private static char[] UNI_ALPHABET_TABLE;

	private void init() {
		// load the GMS 338 charset conversion table
		try {
			// map is GSM_CHAR : UNICODE_CHAR
			HashMap<Character, Character> map = new HashMap<Character, Character>();

			// read the file into the map
			InputStream in = this.getClass().getResourceAsStream(
					GSM_CHARSET_FILE);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			//int charCount = 0;
			int maxGsmValue = 0;
			char gsmVal = 0;
			int maxUnicodeValue = 0;
			char uniVal = 0;

			while ((line = reader.readLine()) != null) {
				// skip comments
				if (line.startsWith("#") || line.length() == 0) {
					continue;
				}

				StringTokenizer st = new StringTokenizer(line, "\t");
				while (st.hasMoreTokens()) {
					// substring off the leading "0x" on the hex numbers
					gsmVal = (char) Integer.parseInt(st.nextToken()
							.substring(2), 16);
					uniVal = (char) Integer.parseInt(st.nextToken()
							.substring(2), 16);

					// if ((gsmVal & 0x1B00) == 0x1B00)
					// //System.out.println(Integer.toHexString(gsmVal));
					// break;

					map.put(new Character(gsmVal), new Character(uniVal));
					maxGsmValue = Math.max(maxGsmValue, gsmVal);
					maxUnicodeValue = Math.max(maxUnicodeValue, uniVal);
					//charCount++;
					break;
				}
			}

			// System.out.println("map length: " + map.size());
			// System.out.println("maxGsmValue: " + maxGsmValue);
			// System.out.println("maxUnicodeValue: " + maxUnicodeValue);

			//int len = Math.max(maxGsmValue, maxUnicodeValue);
			GSM_ALPHABET_TABLE = new char[maxUnicodeValue + 1];
			UNI_ALPHABET_TABLE = new char[maxGsmValue + 1];

			// initalize the GSM table
			Set<Character> gsmKeySet = map.keySet();
			for (Iterator<Character> keyIter = gsmKeySet.iterator(); keyIter
					.hasNext();) {
				Character gsmKey = keyIter.next();
				gsmVal = gsmKey.charValue();
				uniVal = map.get(gsmKey).charValue();

				// populate the lookup tables
				GSM_ALPHABET_TABLE[uniVal] = gsmVal;
				UNI_ALPHABET_TABLE[gsmVal] = uniVal;
			}

			// StringBuffer sb = new StringBuffer();
			// for (int i=0; i<len;i++) {
			// sb.append()
			// }

		} catch (Exception e) {
			mLog.fatal("failed to load SMS charset table from file: "
					+ GSM_CHARSET_FILE);
			e.printStackTrace();
		}

	}

	private SmsCharsetUtil() {
		init();
	}

	public static synchronized SmsCharsetUtil getInstance() {
	    if (mInstance == null) {
		mInstance = new SmsCharsetUtil();
	    }
	    return mInstance;
	}

	public char unicodeToGsm(char unicodeChar) {
		return GSM_ALPHABET_TABLE[unicodeChar];
	}

	public char[] unicodeToGsm(String str) {
		char[] gsmBytes = new char[str.length()];

		for (int i = 0; i < gsmBytes.length; i++) {
			gsmBytes[i] = unicodeToGsm(str.charAt(i));
		}

		return gsmBytes;
	}

	public char gsmToUnicode(char gsmChar) {
		return UNI_ALPHABET_TABLE[gsmChar];
	}

	public String gsmToUnicode(String gsm) {
		char[] uniBytes = new char[gsm.length()];

		for (int i = 0; i < uniBytes.length; i++) {
			uniBytes[i] = gsmToUnicode(gsm.charAt(i));
		}

		return new String(uniBytes);
	}

}
