package net.fourinfo.gateway.model;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represent a phone number validation request. This request should be
 * constructed, then the getXml() method should be called to get HTTP body that
 * should be POSTed to the Gateway.
 * 
 * @author jason
 * 
 */
public class ValidationRequest extends GenericRequest {
	protected Carrier carrier = null; // the carrier information

	protected String MSISDN = null; // the phone number

	public ValidationRequest(String clientId, String clientKey) {
		super(clientId, clientKey);
	}

	/**
	 * @return the carrier
	 */
	public Carrier getCarrier() {
		return this.carrier;
	}

	/**
	 * @param carrier
	 *            the carrier to set
	 */
	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

	/**
	 * @return the mSISDN
	 */
	public String getMSISDN() {
		return this.MSISDN;
	}

	/**
	 * MSISDN (phone number): This is the standard internationalized format for
	 * phone numbers. The 4INFO Messaging Gateway only accepts numbers in this
	 * format for US handsets. The format is: +<country code><national number>
	 * For example, the US number (415) 555-1212 would be represented as
	 * +14155551212
	 * 
	 * Use the <code>setPhoneNumber()</code> method to submit a localized
	 * phone number string, which will set the MSISDN automatically.
	 * 
	 * @param msisdn
	 *            the MSISDN to set
	 */
	public void setMSISDN(String msisdn) {
		this.MSISDN = msisdn;
	}

	/**
	 * Set the country code, plus phone number. This will strip out non-digit
	 * numbers.
	 * 
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String countryCode, String phoneNumber) {
		// the start character is a "+1" for en_US
		StringBuffer msisdn = new StringBuffer("+");
		msisdn.append(countryCode);

		// only accept the digit characters
		final char[] numbers = phoneNumber.toCharArray();
		for (int x = 0; x < numbers.length; x++) {
			final char c = numbers[x];
			if ((c >= '0') && (c <= '9'))
				msisdn.append(c);
		}

		setMSISDN(msisdn.toString());
	}

	/**
	 * Set the phone number. This method accepts a pretty-printed phone number
	 * string like "(202) 555-1212", and converts it to the appropriate MSISDN
	 * string.
	 * 
	 * NOTE: currently it only works for US phone number, as it adds "+1" to the
	 * number string.
	 * 
	 * 
	 * @param phoneNumber
	 */
	public void setPhoneNumber(String phoneNumber) {
		// TODO: localize this using a ResourceBundle, and a Locale argument to
		// determine the country code
		setPhoneNumber("1", phoneNumber);
	}

	/**
	 * Render something like:
	 * 
	 * <?xml version=”1.0” ?> <request clientId="123" clientKey="32IFJ23OFIJIWR"
	 * type="VALIDATION"> <validation> <recipient> <type>5</type>
	 * <id>+14155551212</id> <property> <name>CARRIER</name> <value>5</value>
	 * </property> </recipient> </validation> </request>
	 * 
	 * @return the xml as a string
	 */
	public byte[] getXmlByteArray() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument(); // Create from whole
			// cloth

			Element type = (Element) document.createElement("type");
			type.appendChild(document
					.createTextNode("5"));

			Element msisdn = (Element) document.createElement("id");
			msisdn.appendChild(document.createTextNode(MSISDN));

			Element recipient = (Element) document.createElement("recipient");
			recipient.appendChild(type);
			recipient.appendChild(msisdn);

			if (carrier != null) {
			    Element pName = (Element) document.createElement("name");
			    pName.appendChild(document.createTextNode("CARRIER"));
			    Element pValue = (Element) document.createElement("value");
			    pValue.appendChild(document.createTextNode(carrier.getId().toString()));

			    Element property = (Element) document.createElement("property");
			    property.appendChild(pName);
			    property.appendChild(pValue);
			    recipient.appendChild(property);
			}

			Element validation = (Element) document.createElement("validation");
			validation.appendChild(recipient);

			Element root = (Element) document.createElement("request");
			root.setAttribute("clientId", clientId);
			root.setAttribute("clientKey", clientKey);
			root.setAttribute("type", "VALIDATION");
			root.appendChild(validation);

			document.appendChild(root);
			document.setXmlVersion("1.0");
			document.normalizeDocument();

			// print the output in a portable way
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(document), new StreamResult(
					buff));
			return buff.toByteArray(); // buff.toString("UTF-8");

		} catch (ParserConfigurationException pce) {
			// Parser with specified options can't be built
			pce.printStackTrace();
		} catch (TransformerConfigurationException tce) {
			tce.printStackTrace();
		} catch (TransformerException te) {
			te.printStackTrace();
			// } catch (UnsupportedEncodingException uee) {
			// uee.printStackTrace();
		}
		return null;
	}
}
