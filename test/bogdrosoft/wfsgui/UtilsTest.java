/*
 * UtilsTest.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2024 Bogdan Drozdowski, bogdro (at) users . sourceforge . net
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

import java.util.Calendar;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * UtilsTest - a test for the Utils class.
 * @author Bogdan Drozdowski
 */
public class UtilsTest
{
	/**
	 * Test of convertCalendarMonthToReal method, of class Utils.
	 */
	@Test
	public void testConvertCalendarMonthToReal ()
	{
		System.out.println ("convertCalendarMonthToReal");
		assertEquals(1, Utils.convertCalendarMonthToReal(Calendar.JANUARY));
		assertEquals(2, Utils.convertCalendarMonthToReal(Calendar.FEBRUARY));
		assertEquals(3, Utils.convertCalendarMonthToReal(Calendar.MARCH));
		assertEquals(4, Utils.convertCalendarMonthToReal(Calendar.APRIL));
		assertEquals(5, Utils.convertCalendarMonthToReal(Calendar.MAY));
		assertEquals(6, Utils.convertCalendarMonthToReal(Calendar.JUNE));
		assertEquals(7, Utils.convertCalendarMonthToReal(Calendar.JULY));
		assertEquals(8, Utils.convertCalendarMonthToReal(Calendar.AUGUST));
		assertEquals(9, Utils.convertCalendarMonthToReal(Calendar.SEPTEMBER));
		assertEquals(10, Utils.convertCalendarMonthToReal(Calendar.OCTOBER));
		assertEquals(11, Utils.convertCalendarMonthToReal(Calendar.NOVEMBER));
		assertEquals(12, Utils.convertCalendarMonthToReal(Calendar.DECEMBER));
	}

	/**
	 * Test of convertRealMonthToCalendar method, of class Utils.
	 */
	@Test
	public void testConvertRealMonthToCalendar ()
	{
		System.out.println ("convertRealMonthToCalendar");
		assertEquals(Calendar.JANUARY, Utils.convertRealMonthToCalendar(1));
		assertEquals(Calendar.FEBRUARY, Utils.convertRealMonthToCalendar(2));
		assertEquals(Calendar.MARCH, Utils.convertRealMonthToCalendar(3));
		assertEquals(Calendar.APRIL, Utils.convertRealMonthToCalendar(4));
		assertEquals(Calendar.MAY, Utils.convertRealMonthToCalendar(5));
		assertEquals(Calendar.JUNE, Utils.convertRealMonthToCalendar(6));
		assertEquals(Calendar.JULY, Utils.convertRealMonthToCalendar(7));
		assertEquals(Calendar.AUGUST, Utils.convertRealMonthToCalendar(8));
		assertEquals(Calendar.SEPTEMBER, Utils.convertRealMonthToCalendar(9));
		assertEquals(Calendar.OCTOBER, Utils.convertRealMonthToCalendar(10));
		assertEquals(Calendar.NOVEMBER, Utils.convertRealMonthToCalendar(11));
		assertEquals(Calendar.DECEMBER, Utils.convertRealMonthToCalendar(12));
	}

	/**
	 * Test of handleException method, of class Utils.
	 */
	@Test
	public void testHandleException ()
	{
		System.out.println ("handleException");
		Throwable ex = null;
		Object data = null;
		Utils.handleException (ex, data);
		ex = new Exception ("test1");
		Utils.handleException (ex, data);
		data = "test1msg";
		Utils.handleException (ex, data);
		ex = null;
		Utils.handleException (ex, data);
	}

	/**
	 * Test of createOpenFileChooser method, of class Utils.
	 */
	@Test
	public void testJoinArrays ()
	{
		System.out.println ("joinArrays");
		byte[] a1 = new byte[] {1, 2};
		byte[] a2 = new byte[] {3, 4, 5};
		byte[] res = Utils.joinArrays(a1, a2);
		assertEquals (res.length, a1.length + a2.length);
		for (int i = 0; i < a1.length; i++ )
		{
			assertEquals (res[i], a1[i]);
		}
		for (int i = 0; i < a2.length; i++ )
		{
			assertEquals (res[a1.length + i], a2[i]);
		}
	}
}
