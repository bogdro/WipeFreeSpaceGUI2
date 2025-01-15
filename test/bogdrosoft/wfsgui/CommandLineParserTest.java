/*
 * UtilsTest.java, part of the WipeFreeSpaceGUI2 package.
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

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

/**
 * CommandLineParserTest - a test for the CommandLineParser class.
 * @author Bogdan Drozdowski
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) // some tests are renamed on purpose
public class CommandLineParserTest
{
	/**
	 * Test of isOnlyZeros method, of class CommandLineParser.
	 */
	@Test
	public void testIsOnlyZeros()
	{
		System.out.println("isOnlyZeros");
		assertEquals(false, CommandLineParser.isOnlyZeros());
		boolean expResult = true;
		String[] params = {"--all-zeros"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isOnlyZeros();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isSuperOff method, of class CommandLineParser.
	 */
	@Test
	public void testAIsSuperOff()
	{
		System.out.println("isSuperOff");
		assertEquals(false, CommandLineParser.isSuperOff());
		boolean expResult = true;
		String[] params = {"--superblock", "1"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isSuperOff();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getSuperOffValue method, of class CommandLineParser.
	 */
	@Test
	public void testGetSuperOffValue()
	{
		System.out.println("getSuperOffValue");
		String expResult = "1";
		String[] params = {"--superblock", "1"};
		CommandLineParser.parse (params);
		String result = CommandLineParser.getSuperOffValue();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isBlkSize method, of class CommandLineParser.
	 */
	@Test
	public void testAIsBlkSize()
	{
		System.out.println("isBlkSize");
		assertEquals(false, CommandLineParser.isBlkSize());
		boolean expResult = true;
		String[] params = {"--blocksize", "1"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isBlkSize();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getBlkSizeValue method, of class CommandLineParser.
	 */
	@Test
	public void testGetBlkSizeValue()
	{
		System.out.println("getBlkSizeValue");
		String expResult = "2";
		String[] params = {"--blocksize", "2"};
		CommandLineParser.parse (params);
		String result = CommandLineParser.getBlkSizeValue();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isForce method, of class CommandLineParser.
	 */
	@Test
	public void testIsForce()
	{
		System.out.println("isForce");
		assertEquals(false, CommandLineParser.isForce());
		boolean expResult = true;
		String[] params = {"--force"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isForce();
		assertEquals(expResult, result);
	}

	/**
	 * Test of islastZero method, of class CommandLineParser.
	 */
	@Test
	public void testIslastZero()
	{
		System.out.println("islastZero");
		assertEquals(false, CommandLineParser.islastZero());
		boolean expResult = true;
		String[] params = {"--last-zero"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.islastZero();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isMethod method, of class CommandLineParser.
	 */
	@Test
	public void testAIsMethod()
	{
		System.out.println("isMethod");
		assertEquals(false, CommandLineParser.isMethod());
		boolean expResult = true;
		String[] params = {"--method", "3"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isMethod();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getMethodName method, of class CommandLineParser.
	 */
	@Test
	public void testGetMethodName()
	{
		System.out.println("getMethodName");
		String expResult = "3";
		String[] params = {"--method", "3"};
		CommandLineParser.parse (params);
		String result = CommandLineParser.getMethodName();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isIter method, of class CommandLineParser.
	 */
	@Test
	public void testAIsIter()
	{
		System.out.println("isIter");
		assertEquals(false, CommandLineParser.isIter());
		boolean expResult = true;
		String[] params = {"--iterations", "4"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isIter();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getIterationsValue method, of class CommandLineParser.
	 */
	@Test
	public void testGetIterationsValue()
	{
		System.out.println("getIterationsValue");
		String expResult = "4";
		String[] params = {"--iterations", "4"};
		CommandLineParser.parse (params);
		String result = CommandLineParser.getIterationsValue();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isNopart method, of class CommandLineParser.
	 */
	@Test
	public void testIsNopart()
	{
		System.out.println("isNopart");
		assertEquals(false, CommandLineParser.isNopart());
		boolean expResult = true;
		String[] params = {"--nopart"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isNopart();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isNounrm method, of class CommandLineParser.
	 */
	@Test
	public void testIsNounrm()
	{
		System.out.println("isNounrm");
		assertEquals(false, CommandLineParser.isNounrm());
		boolean expResult = true;
		String[] params = {"--nounrm"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isNounrm();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isNowfs method, of class CommandLineParser.
	 */
	@Test
	public void testIsNowfs()
	{
		System.out.println("isNowfs");
		assertEquals(false, CommandLineParser.isNowfs());
		boolean expResult = true;
		String[] params = {"--nowfs"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isNowfs();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isIoctl method, of class CommandLineParser.
	 */
	@Test
	public void testIsIoctl()
	{
		System.out.println("isIoctl");
		assertEquals(false, CommandLineParser.isIoctl());
		boolean expResult = true;
		String[] params = {"--use-ioctl"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isIoctl();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isNoWipeZeroBlocks method, of class CommandLineParser.
	 */
	@Test
	public void testIsNoWipeZeroBlocks()
	{
		System.out.println("isNoWipeZeroBlocks");
		assertEquals(false, CommandLineParser.isNoWipeZeroBlocks());
		boolean expResult = true;
		String[] params = {"--no-wipe-zero-blocks"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isNoWipeZeroBlocks();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isUseDedicated method, of class CommandLineParser.
	 */
	@Test
	public void testIsUseDedicated()
	{
		System.out.println("isUseDedicated");
		assertEquals(false, CommandLineParser.isUseDedicated());
		boolean expResult = true;
		String[] params = {"--use-dedicated"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isUseDedicated();
		assertEquals(expResult, result);
	}

	/**
	 * Test of isOrder method, of class CommandLineParser.
	 */
	@Test
	public void testAIsOrder()
	{
		System.out.println("isOrder");
		assertEquals(false, CommandLineParser.isOrder());
		boolean expResult = true;
		String[] params = {"--order", "5"};
		CommandLineParser.parse (params);
		boolean result = CommandLineParser.isOrder();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getWipingOrder method, of class CommandLineParser.
	 */
	@Test
	public void testGetWipingOrder()
	{
		System.out.println("getWipingOrder");
		String expResult = "5";
		String[] params = {"--order", "5"};
		CommandLineParser.parse (params);
		String result = CommandLineParser.getWipingOrder();
		assertEquals(expResult, result);
	}

	/**
	 * Test of parse method, of class CommandLineParser.
	 */
	@Test
	public void testParse()
	{
		System.out.println("parse");
		String[] args = {
			// skip the options that cause an exit
			"--all-zeros",
			"-b", "1",
			"-B", "2",
			"-f",
			//"-?",
			//"/?",
			//"--help",
			"--lang", "de_DE",
			"--last-zero",
			//"--licence",
			//"--license",
			"--method", "3",
			"--iterations", "4",
			"--order",
			"--nopart",
			"--nounrm",
			"--nowfs",
			"--no-wipe-zero-blocks",
			"--order", "BLOCK",
			"--use-dedicated",
			"--use-ioctl"//,
			//"--version",
			//"-V"
		};
		CommandLineParser.parse(args);
	}
}
