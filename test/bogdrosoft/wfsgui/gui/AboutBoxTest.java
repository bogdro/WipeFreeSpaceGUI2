/*
 * AboutBoxTest.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2025 Bogdan Drozdowski, bogdro (at) users . sourceforge . net
 * License: GNU General Public License, v3+
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bogdrosoft.wfsgui.gui;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayInputStream;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 * AboutBoxTest - a test for the AboutBox class.
 * @author Bogdan Drozdowski
 */
public class AboutBoxTest
{
	@Test
	public void testConstruct()
	{
		Assume.assumeTrue("Need GUI", !GraphicsEnvironment.isHeadless());
		AboutBox ab = new AboutBox(null, false, 12);
		assertTrue(UiTestHelper.isKeyListenerPresent(ab));
	}

	/**
	 * Test of getFileContents method, of class AboutBox.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testGetFileContents() throws Exception
	{
		String data = "aaaa";
		assertEquals(
			data.length(),
			AboutBox.getFileContents(
				new ByteArrayInputStream(data.getBytes("UTF-8"))
			).length()
		);
	}

	/**
	 * Test of createURI method, of class AboutBox.
	 */
	@Test
	public void testCreateURI()
	{
		assertNotNull(AboutBox.createURI("https", "www.example.com"));
	}

	/**
	 * Test of createWebURI method, of class AboutBox.
	 */
	@Test
	public void testCreateWebURI()
	{
		assertNotNull(AboutBox.createWebURI("https://www.example.com"));
	}
}
