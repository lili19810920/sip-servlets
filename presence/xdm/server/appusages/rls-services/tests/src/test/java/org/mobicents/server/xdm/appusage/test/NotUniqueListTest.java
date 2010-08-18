package org.mobicents.server.xdm.appusage.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.RuntimeMBeanException;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Test;
import org.mobicents.xcap.client.XcapClient;
import org.mobicents.xcap.client.XcapResponse;
import org.mobicents.xcap.client.impl.XcapClientImpl;
import org.mobicents.xcap.client.uri.DocumentSelectorBuilder;
import org.mobicents.xcap.client.uri.UriBuilder;
import org.openxdm.xcap.common.error.ConstraintFailureConflictException;
import org.openxdm.xcap.common.error.InternalServerErrorException;
import org.openxdm.xcap.common.error.NotUTF8ConflictException;
import org.openxdm.xcap.common.error.NotWellFormedConflictException;
import org.openxdm.xcap.common.xml.TextWriter;
import org.openxdm.xcap.common.xml.XMLValidator;
import org.openxdm.xcap.server.slee.appusage.rlsservices.RLSServicesAppUsage;

public class NotUniqueListTest extends AbstractT {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(NotUniqueListTest.class);
	}
	
	private String user1 = "sip:joe1@example.com";
	private String user2 = "sip:joe2@example.com";
	private String documentName = "index";
	
	@Test
	public void test() throws HttpException, IOException, JAXBException, InterruptedException, TransformerException, NotWellFormedConflictException, NotUTF8ConflictException, InternalServerErrorException, URISyntaxException, InstanceNotFoundException, MBeanException, ReflectionException, MalformedObjectNameException, NullPointerException, NamingException {
		
		initRmiAdaptor();

		try {
			createUser(user1,"password");
		}
		catch (RuntimeMBeanException e) {
			if (!(e.getCause() instanceof IllegalStateException)) {
				e.printStackTrace();
			}
		}
		
		try {
			createUser(user2,"password");
		}
		catch (RuntimeMBeanException e) {
			if (!(e.getCause() instanceof IllegalStateException)) {
				e.printStackTrace();
			}
		}
		
		XcapClient client = new XcapClientImpl();
		
		Credentials credentials1 = new UsernamePasswordCredentials(user1, "password");
		Credentials credentials2 = new UsernamePasswordCredentials(user2, "password");
		
		// create uri		
		String documentSelector1 = DocumentSelectorBuilder.getUserDocumentSelectorBuilder(RLSServicesAppUsage.ID,user1,documentName).toPercentEncodedString();
		String documentSelector2 = DocumentSelectorBuilder.getUserDocumentSelectorBuilder(RLSServicesAppUsage.ID,user2,documentName).toPercentEncodedString();
		
		UriBuilder uriBuilder = new UriBuilder()
			.setSchemeAndAuthority("http://localhost:8080")
			.setXcapRoot("/mobicents")
			.setDocumentSelector(documentSelector1);
		URI documentURI1 = uriBuilder.toURI();
		uriBuilder.setDocumentSelector(documentSelector2);
		URI documentURI2 = uriBuilder.toURI();
		
		// create exception for return codes
		ConstraintFailureConflictException exception = new ConstraintFailureConflictException("junit test");
		
		// read document xml
		InputStream is = this.getClass().getResourceAsStream("example_not_unique_list.xml");
        String content = TextWriter.toString(XMLValidator.getWellFormedDocument(XMLValidator.getUTF8Reader(is)));
		is.close();
		
		// send put request and get response
		XcapResponse response = client.put(documentURI1,RLSServicesAppUsage.MIMETYPE,content,null,credentials1);
		
		// check put response
		System.out.println("Response got:\n"+response);
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response code should be 201",response.getCode() == 201);
		
		// now try to put same document for another user
		response = client.put(documentURI2,RLSServicesAppUsage.MIMETYPE,content,null,credentials2);
		
		// check put response
		System.out.println("Response got:\n"+response);
		assertTrue("Put response must exists",response != null);
		assertTrue("Put response content must be the expected and the response code should be "+exception.getResponseStatus(),response.getCode() == exception.getResponseStatus());
		
		// cleanup
		client.delete(documentURI1,null,credentials1);
		
		client.shutdown();
		
		removeUser(user1);
		removeUser(user2);
	}
		
}
