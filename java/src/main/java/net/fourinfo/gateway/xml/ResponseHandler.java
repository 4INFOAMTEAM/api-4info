package net.fourinfo.gateway.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import net.fourinfo.gateway.model.Response;

/**
 * <?xml version=”1.0” ?> <response>
 * <requestId>F81D4FAE-7DEC-11D0-A765-00A0C91E6BF6</requestId> <confCode>249G8</confCode>
 * <status> <id>1</id> <message>Success</message> </status> </response>
 * 
 * @author Jason Thrasher
 */
public class ResponseHandler extends DefaultHandler {
	protected static Log mLog = LogFactory.getLog(ResponseHandler.class);

	private StringBuffer charContent = null; // text content of an element

	private Response response = new Response();

	public ResponseHandler() {
		super();
	}

	// //////////////////////////////////////////////////////////////////
	// Event handlers.
	// //////////////////////////////////////////////////////////////////

	public void startDocument() {
		mLog.debug("Start document");
		// this.openSession();
	}

	public void endDocument() {
		mLog.debug("End document");
		// this.closeSession();
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) {

		// reset the content buffer
		charContent = new StringBuffer();
	}

	public void endElement(String uri, String name, String qName) {
		mLog.debug("End element: " + qName);
		if (qName.equals("requestId")) {
			mLog.debug("end requestId");
			response.setRequestId(charContent.toString());

		} else if (qName.equals("confCode")) {
			mLog.debug("end confCode");
			response.setConfCode(charContent.toString());
		} else if (qName.equals("id")) {
			mLog.debug("end id");
			response.setStatusId(Integer.parseInt(charContent.toString()));
		} else if (qName.equals("message")) {
			mLog.debug("end message");
			response.setStatusMessage(charContent.toString());
		} else {
			mLog.debug("End element unknown:   {" + uri + "} " + name);
		}
	}

	public void characters(char ch[], int start, int length) {
		charContent.append(ch, start, length);
		mLog.debug("Characters: " + charContent);
	}

	public Response getResponse() {
		return response;
	}
}