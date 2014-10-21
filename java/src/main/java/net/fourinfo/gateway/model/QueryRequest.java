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
 * A request object for querying the delivery status of an SMS message. The
 * "requestId" is used to identify the message, and should be the same one
 * returned by the request to send the message who's status is being queried.
 * 
 * @author Jason Thrasher
 */
public class QueryRequest extends GenericRequest {
	private String requestId;

	public QueryRequest(String clientId, String clientKey) {
		this.clientId = clientId;
		this.clientKey = clientKey;
	}

	/**
	 * @return the requestId
	 */
	public String getRequestId() {
		return this.requestId;
	}

	/**
	 * @param requestId
	 *            the requestId to set
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * Render something like:
	 * 
	 * <?xml version=”1.0” ?> <request clientId="123" clientKey="32IFJ23OFIJIWR"
	 * type="STATUS"> <query>
	 * <requestId>F81D4FAE-7DEC-11D0-A765-00A0C91E6BF6</requestId></query></request>
	 * 
	 * @return the xml as a string
	 */
	public byte[] getXmlByteArray() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument(); // Create from whole
			// cloth

			Element id = (Element) document.createElement("requestId");
			id.appendChild(document.createTextNode(requestId));

			Element query = (Element) document.createElement("query");
			query.appendChild(id);

			Element root = (Element) document.createElement("request");
			root.setAttribute("clientId", clientId);
			root.setAttribute("clientKey", clientKey);
			root.setAttribute("type", "STATUS");
			root.appendChild(query);

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
