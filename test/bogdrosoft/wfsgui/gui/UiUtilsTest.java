/*
 * UiUtilsTest.java, part of the WipeFreeSpaceGUI2 package.
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

import java.awt.Component;
import java.awt.Window;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.filechooser.FileFilter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 * UiUtilsTest - a test for the UiUtils class.
 * @author Bogdan Drozdowski
 */
public class UiUtilsTest
{
	/**
	 * Test of changeGUI method, of class UiUtils.
	 */
	@Test
	public void testChangeGUI()
	{
		System.out.println("changeGUI");
		Runnable r = null;
		UiUtils.changeGUI(r);
		// passing anything different from null can't be checked,
		// because there's no GUI
	}

	/**
	 * Test of setFontSize method, of class UiUtils.
	 */
	@Test
	public void testSetFontSize()
	{
		System.out.println("setFontSize");
		Component c = new JLabel("testLabel");
		float newSize = 11.0F;
		UiUtils.setFontSize(c, newSize);
		assertTrue(
			"Size differs: new=" + c.getFont().getSize()
				+ ", expected: " + newSize,
			Math.abs (newSize - c.getFont().getSize()) < 0.01
		);
	}

	/**
	 * Test of getFontSize method, of class UiUtils.
	 */
	@Test
	public void testGetFontSize()
	{
		System.out.println("getFontSize");
		int fontSize = 25;
		JSpinner spin = new JSpinner();
		spin.setValue(fontSize);
		assertTrue(Math.abs(fontSize - UiUtils.getFontSize(spin)) < 0.001);
	}

	/**
	 * Test of changeSizeToScreen method, of class UiUtils.
	 */
	@Test
	@Ignore("UI test without UI")
	public void testChangeSizeToScreen()
	{
		System.out.println("changeSizeToScreen");
		Window w = null;
		UiUtils.changeSizeToScreen(w);
	}

	/**
	 * Test of showErrorMessage method, of class UiUtils.
	 */
	@Test
	@Ignore("Requires interactive user action")
	public void testShowErrorMessage()
	{
		System.out.println("showErrorMessage");
		Component c = null;
		String msg = "";
		UiUtils.showErrorMessage(c, msg);
	}

	/**
	 * Test of createConfigFileChooser method, of class UiUtils.
	 */
	@Test
	public void testCreateConfigFileChooser()
	{
		System.out.println("createConfigFileChooser");
		String description = "";
		JFileChooser result = UiUtils.createConfigFileChooser(description);
		assertNotNull(result);
		FileFilter resultFilter = result.getFileFilter();
		assertTrue(resultFilter.accept(new File("test.cfg")));
		assertFalse(resultFilter.accept(new File("test.png")));
		assertTrue(resultFilter.accept(new File("test.png.cfg")));
	}

	/**
	 * Test of createWfsProgramFileChooser method, of class UiUtils.
	 */
	@Test
	public void testCreateWfsProgramFileChooser()
	{
		System.out.println("createWfsProgramFileChooser");
		JFileChooser result = UiUtils.createWfsProgramFileChooser();
		assertNotNull(result);
		FileFilter resultFilter = result.getFileFilter();
		assertTrue(resultFilter.accept(new File("wipefreespace")));
	}
}
