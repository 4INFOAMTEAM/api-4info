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

public class MessageRequest extends ValidationRequest {

	protected String message = null;

	protected String shortCode = null;

	protected HandsetInfo handsetInfo = null;

	protected boolean handsetDeliveryReceipt = false;

	public MessageRequest(String clientId, String clientKey) {
		super(clientId, clientKey);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the shortCode
	 */
	public String getShortCode() {
		return this.shortCode;
	}

	/**
	 * @param shortCode
	 *            the shortCode to set
	 */
	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	/**
	 * @return the handset info
	 */
	public HandsetInfo getHandsetInfo() {
		return this.handsetInfo;
	}

	/**
	 * @param handsetInfo
	 *            the handset info to set
	 */
	public void setHandsetInfo(HandsetInfo handsetInfo) {
		this.handsetInfo = handsetInfo;
	}

	/**
	 * @return the handset delivery reciept
	 */
	public boolean getHandsetDeliveryReceipt() {
		return this.handsetDeliveryReceipt;
	}

	/**
	 * @param handsetDeliveryReceipt
	 *            the handset delivery reciept to set
	 */
	public void setHandsetDeliveryReceipt(boolean handsetDeliveryReceipt) {
		this.handsetDeliveryReceipt = handsetDeliveryReceipt;
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
			Document document = builder.newDocument();

			Element text = (Element) document.createElement("text");
			if (message != null) {
			    text.appendChild(document.createTextNode(message));
			} else { 
			    text.appendChild(document.createTextNode(""));
			}

			Element type = (Element) document.createElement("type");
			type.appendChild(document.createTextNode("5"));

			Element msisdn = (Element) document.createElement("id");
			msisdn.appendChild(document.createTextNode(MSISDN));

			Element recipient = (Element) document.createElement("recipient");
			recipient.appendChild(type);
			recipient.appendChild(msisdn);

			if (carrier != null) {
			    recipient.appendChild(buildProperty(document, "CARRIER", carrier.getId().toString()));
			}
			if (handsetInfo != null) {
			    Integer handsetMfgId = handsetInfo.getHandsetMfgId();
			    String userAgent = handsetInfo.getUserAgent();
			    String handsetModel = handsetInfo.getHandsetModel();
			    if (handsetMfgId != null) {
				recipient.appendChild(buildProperty(document, "HANDSET_MFG", handsetMfgId.toString()));
			    }
			    if (userAgent != null && userAgent.length() > 0) {
				recipient.appendChild(buildProperty(document, "USER_AGENT", userAgent));;
			    }
			    if (handsetModel != null && handsetModel.length() > 0) {
				recipient.appendChild(buildProperty(document, "HANDSET_MODEL", handsetModel));
			    }
			}
			Element msg = (Element) document.createElement("message");

			if (shortCode != null) {
			    Element shortCodeType = (Element) document.createElement("type");
			    shortCodeType.appendChild(document.createTextNode("6"));
			    Element shortCodeElement = (Element) document.createElement("id");
			    shortCodeElement.appendChild(document.createTextNode(shortCode));
			    Element sender = (Element) document.createElement("sender");
			    sender.appendChild(shortCodeType);
			    sender.appendChild(shortCodeElement);
			    msg.appendChild(sender);
			}

			msg.appendChild(recipient);
			msg.appendChild(text);
			msg.setAttribute("receipt", handsetDeliveryReceipt ? "true" : "false");

			Element root = (Element) document.createElement("request");
			root.setAttribute("clientId", clientId);
			root.setAttribute("clientKey", clientKey);
			root.setAttribute("type", "MESSAGE");
			root.appendChild(msg);

			document.appendChild(root);
			document.setXmlVersion("1.0");
			//document.normalizeDocument();

			//System.out.println(document.toString());

			// print the output in a portable way
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			//transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			ByteArrayOutputStream buff = new ByteArrayOutputStream();
			transformer.transform(new DOMSource(document), new StreamResult(
					buff));
			try {
			    //System.out.println(buff.toString("UTF-8"));
			    //System.out.println(buff.toString());
			} catch (Exception ignore) {}
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

	public Element buildProperty(Document document, String name, String value) {
		Element pName = (Element) document.createElement("name");
		pName.appendChild(document.createTextNode(name));
		Element pValue = (Element) document.createElement("value");
		pValue.appendChild(document.createTextNode(value));
		Element property = (Element) document.createElement("property");
		property.appendChild(pName);
		property.appendChild(pValue);
		return property;
	}
}
