/*
 * ConfigFileTest.java, part of the WipeFreeSpaceGUI2 package.
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
package bogdrosoft.wfsgui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ConfigFileTest - a test for the ConfigFile class.
 * @author Bogdan Drozdowski
 */
public class ConfigFileTest
{
	private static File f;

	@Before
	public void setUp () throws Exception
	{
		f = File.createTempFile("wfsgui2", null);
	}

	
	public ConfigFileTest()
	{
	}
	
	@BeforeClass
	public static void setUpClass()
	{
	}
	
	@AfterClass
	public static void tearDownClass()
	{
	}
	
	@After
	public void tearDown()
	{
	}

	/**
	 * Test of read method, of class ConfigFile.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testRead() throws Exception
	{
		System.out.println("read");
		List<String> fsList = new ArrayList<String>(1);
		fsList.add("aaa");
		ConfigFile instance = new ConfigFile(f);
		instance.setAllZeros(true);
		instance.setBlockSize(1234);
		instance.setFSList(fsList);
		instance.setFontSizeValue(23);
		instance.setForce(true);
		instance.setHeight(2345);
		instance.setIoctl(true);
		instance.setIsMaximized(true);
		instance.setIsMethodSelected(true);
		instance.setIsOrderSelected(true);
		instance.setIterations(3456);
		instance.setLastZero(true);
		instance.setMethodName("bbb");
		instance.setNoWipeZeroBlocks(true);
		instance.setSuperOffset(4567);
		instance.setUseDedicated(true);
		instance.setWfsPath("ccc");
		instance.setWidth(567);
		instance.setWipeFreeSpace(true);
		instance.setWipePart(true);
		instance.setWipeUndel(true);
		instance.setWipingOrder("ddd");
		instance.setX(11);
		instance.setY(22);
		instance.write();
		instance = new ConfigFile(f);
		instance.read();
		assertEquals(instance.getAllZeros(), true);
		assertEquals(instance.getBlockSize(), 1234);
		assertEquals(instance.getFSList(), fsList);
		assertEquals(instance.getFontSizeValue(), 23);
		assertEquals(instance.getForce(), true);
		assertEquals(instance.getHeight(), 2345);
		assertEquals(instance.getIoctl(), true);
		assertEquals(instance.getIsMax(), true);
		assertEquals(instance.getIsMethod(), true);
		assertEquals(instance.getIsOrder(), true);
		assertEquals(instance.getIterations(), 3456);
		assertEquals(instance.getLastZero(), true);
		assertEquals(instance.getMethodName(), "bbb");
		assertEquals(instance.getIsNoWipeZeroBlocks(), true);
		assertEquals(instance.getSuperOffset(), 4567);
		assertEquals(instance.getIsUseDedicated(), true);
		assertEquals(instance.getWfsPath(), "ccc");
		assertEquals(instance.getWidth(), 567);
		assertEquals(instance.getNoWipeFreeSpace(), true);
		assertEquals(instance.getNoWipePart(), true);
		assertEquals(instance.getNoWipeUndel(), true);
		assertEquals(instance.getWipingOrder(), "ddd");
		assertEquals(instance.getX(), 11);
		assertEquals(instance.getY(), 22);
	}

	/**
	 * Test of write method, of class ConfigFile.
	 * @throws java.lang.Exception
	 */
	@Test
	public void testWrite() throws Exception
	{
		System.out.println("write");
		ConfigFile instance = new ConfigFile(f);
		instance.write();
		assertTrue(f.length() > 0);
	}

	/**
	 * Test of setX method, of class ConfigFile.
	 */
	@Test
	public void testSetX()
	{
		System.out.println("setX");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setX(v);
		assertEquals(v, instance.getX());
	}

	/**
	 * Test of setY method, of class ConfigFile.
	 */
	@Test
	public void testSetY()
	{
		System.out.println("setY");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setY(v);
		assertEquals(v, instance.getY());
	}

	/**
	 * Test of setWidth method, of class ConfigFile.
	 */
	@Test
	public void testSetWidth()
	{
		System.out.println("setWidth");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setWidth(v);
		assertEquals(v, instance.getWidth());
	}

	/**
	 * Test of setHeight method, of class ConfigFile.
	 */
	@Test
	public void testSetHeight()
	{
		System.out.println("setHeight");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setHeight(v);
		assertEquals(v, instance.getHeight());
	}

	/**
	 * Test of setIsMaximized method, of class ConfigFile.
	 */
	@Test
	public void testSetIsMaximized()
	{
		System.out.println("setIsMaximized");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setIsMaximized(v);
		assertEquals(v, instance.getIsMax());
	}

	/**
	 * Test of setAllZeros method, of class ConfigFile.
	 */
	@Test
	public void testSetAllZeros()
	{
		System.out.println("setAllZeros");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setAllZeros(v);
		assertEquals(v, instance.getAllZeros());
	}

	/**
	 * Test of setSuperOffset method, of class ConfigFile.
	 */
	@Test
	public void testSetSuperOffset()
	{
		System.out.println("setSuperOffset");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setSuperOffset(v);
		assertEquals(v, instance.getSuperOffset());
	}

	/**
	 * Test of setBlockSize method, of class ConfigFile.
	 */
	@Test
	public void testSetBlockSize()
	{
		System.out.println("setBlockSize");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setBlockSize(v);
		assertEquals(v, instance.getBlockSize());
	}

	/**
	 * Test of setForce method, of class ConfigFile.
	 */
	@Test
	public void testSetForce()
	{
		System.out.println("setForce");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setForce(v);
		assertEquals(v, instance.getForce());
	}

	/**
	 * Test of setIterations method, of class ConfigFile.
	 */
	@Test
	public void testSetIterations()
	{
		System.out.println("setIterations");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setIterations(v);
		assertEquals(v, instance.getIterations());
	}

	/**
	 * Test of setLastZero method, of class ConfigFile.
	 */
	@Test
	public void testSetLastZero()
	{
		System.out.println("setLastZero");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setLastZero(v);
		assertEquals(v, instance.getLastZero());
	}

	/**
	 * Test of setWipePart method, of class ConfigFile.
	 */
	@Test
	public void testSetWipePart()
	{
		System.out.println("setWipePart");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setWipePart(v);
		assertEquals(v, instance.getNoWipePart());
	}

	/**
	 * Test of setWipeUndel method, of class ConfigFile.
	 */
	@Test
	public void testSetWipeUndel()
	{
		System.out.println("setWipeUndel");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setWipeUndel(v);
		assertEquals(v, instance.getNoWipeUndel());
	}

	/**
	 * Test of setWipeFreeSpace method, of class ConfigFile.
	 */
	@Test
	public void testSetWipeFreeSpace()
	{
		System.out.println("setWipeFreeSpace");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setWipeFreeSpace(v);
		assertEquals(v, instance.getNoWipeFreeSpace());
	}

	/**
	 * Test of setIoctl method, of class ConfigFile.
	 */
	@Test
	public void testSetIoctl()
	{
		System.out.println("setIoctl");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setIoctl(v);
		assertEquals(v, instance.getIoctl());
	}

	/**
	 * Test of setFSList method, of class ConfigFile.
	 */
	@Test
	public void testSetFSList()
	{
		System.out.println("setFSList");
		List<String> v = new ArrayList<String>(1);
		v.add("zzz");
		ConfigFile instance = new ConfigFile(f);
		instance.setFSList(v);
		assertEquals(v, instance.getFSList());
	}

	/**
	 * Test of setFontSizeValue method, of class ConfigFile.
	 */
	@Test
	public void testSetFontSizeValue()
	{
		System.out.println("setFontSizeValue");
		int v = 123;
		ConfigFile instance = new ConfigFile(f);
		instance.setFontSizeValue(v);
		assertEquals(v, instance.getFontSizeValue());
	}

	/**
	 * Test of setWfsPath method, of class ConfigFile.
	 */
	@Test
	public void testSetWfsPath()
	{
		System.out.println("setWfsPath");
		String v = "aaaa";
		ConfigFile instance = new ConfigFile(f);
		instance.setWfsPath(v);
		assertEquals(v, instance.getWfsPath());
	}

	/**
	 * Test of setIsMethodSelected method, of class ConfigFile.
	 */
	@Test
	public void testSetIsMethodSelected()
	{
		System.out.println("setIsMethodSelected");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setIsMethodSelected(v);
		assertEquals(v, instance.getIsMethod());
	}

	/**
	 * Test of setMethodName method, of class ConfigFile.
	 */
	@Test
	public void testSetMethodName()
	{
		System.out.println("setMethodName");
		String v = "bbbb";
		ConfigFile instance = new ConfigFile(f);
		instance.setMethodName(v);
		assertEquals(v, instance.getMethodName());
	}

	/**
	 * Test of setNoWipeZeroBlocks method, of class ConfigFile.
	 */
	@Test
	public void testSetNoWipeZeroBlocks()
	{
		System.out.println("setNoWipeZeroBlocks");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setNoWipeZeroBlocks(v);
		assertEquals(v, instance.getIsNoWipeZeroBlocks());
	}

	/**
	 * Test of setUseDedicated method, of class ConfigFile.
	 */
	@Test
	public void testSetUseDedicated()
	{
		System.out.println("setUseDedicated");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setUseDedicated(v);
		assertEquals(v, instance.getIsUseDedicated());
	}

	/**
	 * Test of setIsOrderSelected method, of class ConfigFile.
	 */
	@Test
	public void testSetIsOrderSelected()
	{
		System.out.println("setIsOrderSelected");
		boolean v = true;
		ConfigFile instance = new ConfigFile(f);
		instance.setIsOrderSelected(v);
		assertEquals(v, instance.getIsOrder());
	}

	/**
	 * Test of setWipingOrder method, of class ConfigFile.
	 */
	@Test
	public void testSetWipingOrder()
	{
		System.out.println("setWipingOrder");
		String v = "cccc";
		ConfigFile instance = new ConfigFile(f);
		instance.setWipingOrder(v);
		assertEquals(v, instance.getWipingOrder());
	}
}
