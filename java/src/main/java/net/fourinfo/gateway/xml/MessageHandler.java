package net.fourinfo.gateway.xml;

import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import net.fourinfo.gateway.model.Address;
import net.fourinfo.gateway.model.Carrier;
import net.fourinfo.gateway.model.Sms;

/**
 * <?xml version="1.0" ?>
 * <request type="MESSAGE">
 *   <message id="F81D4FAE-7DEC-11D0-A765-00A0C91E6BF6">
 *     <recipient>
 *       <type>6</type>
 *       <id>12345</id>
 *     </recipient>
 *     <sender>
 *       <type>5</type>
 *       <id>+16505551212</id>
 *       <property>
 *         <name>CARRIER</name>
 *         <value>5</value>
 *       </property>
 *     </sender>
 *     <text>Test message.</text>
 *   </message>
 * </request>
 * 
 * @author Garth Patil <a href="mailto:g@4info.net">g@4info.net</a>
 *
 */
public class MessageHandler extends DefaultHandler {

    protected static Log mLog = LogFactory.getLog(MessageHandler.class);
    
    private StringBuffer charContent = null; // text content of an element
    
    private Sms message = new Sms();
    
    private Stack stack = new Stack();

    public MessageHandler() {
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
			     Attributes atts)
    {
	// reset the content buffer
	charContent = new StringBuffer();
	if (qName.equals("message")) {
	    message.setRequestId(atts.getValue("id"));
	}
	if (qName.equals("recipient")) {
	    Address address = new Address();
	    stack.push(address);
	}
	if (qName.equals("sender")) {
	    Address address = new Address();
	    stack.push(address);
	}
	if (qName.equals("property")) {
	    Carrier carrier = new Carrier();
	    stack.push(carrier);
	}
    }
    
    public void endElement(String uri, String name, String qName) {
	mLog.debug("End element: " + qName);

	if (qName.equals("recipient")) {
	    mLog.debug("end recipient");
	    Address address = (Address)stack.pop();
	    message.setRecipient(address);
	} else if (qName.equals("sender")) {
	    mLog.debug("end sender");
	    Address address = (Address)stack.pop();
	    message.setSender(address);
	} else if (qName.equals("property")) {
	    mLog.debug("end property");
	    Carrier carrier = (Carrier)stack.pop();
	    Address address = (Address)stack.peek();
	    address.setCarrier(carrier);
	} else if (qName.equals("type")) {
	    mLog.debug("end type");
	    Object a = stack.peek();
	    if (a instanceof Address) {
		Address address = (Address)a;
		address.setAddressType(Integer.parseInt(charContent.toString()));
	    }
	} else if (qName.equals("id")) {
	    mLog.debug("end id");
	    Object a = stack.peek();
	    if (a instanceof Address) {
		Address address = (Address)a;
		address.setPhoneNumber(charContent.toString());
	    }
	} else if (qName.equals("value")) {
	    mLog.debug("end value");
	    Object c = stack.peek();
	    if (c instanceof Carrier) {
		Carrier carrier = (Carrier)c;
		carrier.setId(Long.parseLong(charContent.toString()));
	    }
	} else if (qName.equals("text")) {
	    mLog.debug("end text");
	    message.setMessage(charContent.toString());
	} else {
	    mLog.debug("End element unknown:   {" + uri + "} " + name);
	}
    }
    
    public void characters(char ch[], int start, int length) {
	charContent.append(ch, start, length);
	mLog.debug("Characters: " + charContent);
    }
    
    public Sms getMessage() {
	return this.message;
    }
    
}
