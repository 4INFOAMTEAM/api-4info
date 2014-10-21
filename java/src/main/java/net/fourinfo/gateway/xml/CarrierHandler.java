package net.fourinfo.gateway.xml;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import net.fourinfo.gateway.model.Carrier;

/**
 * SAX parser handler that moves RSS feed data from an input stream into the
 * database. This class is not threadsafe and should only be used from one
 * thread (or SAX parser) at a time. This class opens and closes Spring-managed
 * hibernate connections.
 * 
 * @author Jason Thrasher
 */
public class CarrierHandler extends DefaultHandler {
	protected static Log mLog = LogFactory.getLog(CarrierHandler.class);

	private ArrayList<Carrier> carriers = new ArrayList<Carrier>();

	private StringBuffer charContent = null; // text content of an element

	private String idS; // the current carrier ID

	public CarrierHandler() {
		super();
	}

	// //////////////////////////////////////////////////////////////////
	// Event handlers.
	// //////////////////////////////////////////////////////////////////

	public void startDocument() {
		mLog.debug("Start document");
	}

	public void endDocument() {
		mLog.debug("End document");
	}

	public void startElement(String uri, String name, String qName,
			Attributes atts) {
		charContent = new StringBuffer();

		if ("".equals(uri)) {
			mLog.debug("Start element: " + qName);
			if (qName.equals("carriers")) {
				mLog.debug("reading carriers");
			} else if (qName.equals("carrier")) {
				mLog.debug("reading carrier");
				idS = atts.getValue("id");
			}
		} else {
			mLog.warn("Start element unknown: {" + uri + "} " + name + " "
					+ qName);
		}
	}

	public void endElement(String uri, String name, String qName) {
		if ("".equals(uri)) {
			// handle normal rss
			mLog.debug("End element: " + qName);
			if (qName.equals("carriers")) {
				mLog.debug("end carriers");
			} else if (qName.equals("carrier")) {
				mLog.debug("end carrier");
				try {
					Carrier c = new Carrier();
					if (charContent != null && charContent.length() > 0) {
						c.setName(URLDecoder.decode(charContent.toString(),
								"UTF-8"));
					}
					c.setId(new Long(idS));

					// add to the list
					carriers.add(c);
				} catch (Exception e) {
					// barf on any exception
					mLog.error("failed to parse carrier id=" + idS + " name="
							+ charContent);
				}

			}
		} else {
			mLog.debug("End element unknown:   {" + uri + "} " + name);
		}
	}

	public void characters(char ch[], int start, int length) {
		charContent.append(ch, start, length);
		mLog.debug("Characters: " + charContent);
	}

	public List<Carrier> getCarriers() {
		return carriers;
	}
}