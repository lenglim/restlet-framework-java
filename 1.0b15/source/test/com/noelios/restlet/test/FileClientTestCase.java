
package com.noelios.restlet.test;

import java.io.File;

import junit.framework.TestCase;

import org.restlet.Call;
import org.restlet.connector.Client;
import org.restlet.connector.DefaultClient;
import org.restlet.data.Protocols;
import org.restlet.data.Statuses;

import com.noelios.restlet.data.FileReference;
import com.noelios.restlet.data.StringRepresentation;

/**
 * Unit test case for the File client connector.
 * @author Jerome Louvel (contact@noelios.com) <a href="http://www.noelios.com/">Noelios Consulting</a>
 */
public class FileClientTestCase extends TestCase
{

	public void testFileClient()
	{
		try
		{
			String text = "Test content\r\nLine 2\r\nLine2";
			Client fc = new DefaultClient(Protocols.FILE);
			FileReference fr = new FileReference(File.createTempFile("Restlet", ".txt"));
			
			// Write the text to temporary file
			Call call = fc.put(fr.toString(), new StringRepresentation(text));
			assertTrue(call.getStatus().equals(Statuses.SUCCESS_OK));

			// Get the text and compare to the original
			call = fc.get(fr.toString());
			assertTrue(call.getStatus().equals(Statuses.SUCCESS_OK));
			
			// Delete the file
			call = fc.delete(fr.toString());
			assertTrue(call.getStatus().equals(Statuses.SUCCESS_NO_CONTENT));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
