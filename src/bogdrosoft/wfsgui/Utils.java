/*
 * Utils.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2022-2024 Bogdan Drozdowski, bogdro (at) users . sourceforge . net
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

import java.awt.Component;
import java.util.Calendar;

/**
 * A utility class, containing some useful methods and fields.
 * @author Bogdan Drozdowski
 */
public class Utils
{
	/**
	 * A sample uncaught-exception handler instance for threads.
	 */
	public static final UncExHndlr HANDLER = new UncExHndlr ();

	/** An empty String. */
	public static final String EMPTY_STR = "";	// NOI18N
	/** A String with the Carriage Return character. */
	public static final String CR = "\r";		// NOI18N
	/** A String with the Line Feed character. */
	public static final String LF = "\n";		// NOI18N
	/** A single colon String. */
	public static final String COLON = ":";		// NOI18N
	/** A String with a single comma character. */
	public static final String COMMA = ",";		// NOI18N
	/** A single zero ("0") String. */
	public static final String ZERO = "0";		// NOI18N
	/** A single space String. */
	public static final String SPACE = " ";		// NOI18N
	/** A String with a single apostrophe character. */
	public static final String APOSTROPHE = "'";	// NOI18N
	/** A String with a single dot. */
	public static final String DOT = ".";		// NOI18N
	/** A single left parenthesis String. */
	public static final String L_PAREN = "(";	// NOI18N
	/** A single right parenthesis String. */
	public static final String R_PAREN = ")";	// NOI18N
	/** A single question-mark String. */
	public static final String QUESTION_MARK = "?";	// NOI18N
	/** A String with a single "double quote" character. */
	public static final String DQUOT = "\"";	// NOI18N
	/** A String with a single byte of the value binary zero. */
	public static final String ZERO_BYTE = "\000";	// NOI18N
	/** A String with a single dash character. */
	public static final String DASH = "-";		// NOI18N

	private static final String MSG_START = ", Message='";			// NOI18N
	private static final String DATA_START = ", Data='";			// NOI18N
	private static final String EXCEPTION_AT_STR = "\tat\t";		// NOI18N
	private static final String UNKNOWN_CLASS = "<Unknown class>";		// NOI18N
	private static final String UNKNOWN_METHOD = "<Unknown method>";	// NOI18N
	private static final String UNKNOWN_FILE = "<Unknown file>";		// NOI18N

	private static final boolean DEBUG_EXCEPTIONS = true;

	private Utils ()
	{
		// non-instantiable
	}

	public static int convertCalendarMonthToReal (int month)
	{
		if ( month == Calendar.JANUARY )
		{
			return 1;
		}
		else if ( month == Calendar.FEBRUARY )
		{
			return 2;
		}
		else if ( month == Calendar.MARCH )
		{
			return 3;
		}
		else if ( month == Calendar.APRIL )
		{
			return 4;
		}
		else if ( month == Calendar.MAY )
		{
			return 5;
		}
		else if ( month == Calendar.JUNE )
		{
			return 6;
		}
		else if ( month == Calendar.JULY )
		{
			return 7;
		}
		else if ( month == Calendar.AUGUST )
		{
			return 8;
		}
		else if ( month == Calendar.SEPTEMBER )
		{
			return 9;
		}
		else if ( month == Calendar.OCTOBER )
		{
			return 10;
		}
		else if ( month == Calendar.NOVEMBER )
		{
			return 11;
		}
		else if ( month == Calendar.DECEMBER )
		{
			return 12;
		}
		return -1;
	}

	public static int convertRealMonthToCalendar (int month)
	{
		if ( month == 1 )
		{
			return Calendar.JANUARY;
		}
		else if ( month == 2 )
		{
			return Calendar.FEBRUARY;
		}
		else if ( month == 3 )
		{
			return Calendar.MARCH;
		}
		else if ( month == 4 )
		{
			return Calendar.APRIL;
		}
		else if ( month == 5 )
		{
			return Calendar.MAY;
		}
		else if ( month == 6 )
		{
			return Calendar.JUNE;
		}
		else if ( month == 7 )
		{
			return Calendar.JULY;
		}
		else if ( month == 8 )
		{
			return Calendar.AUGUST;
		}
		else if ( month == 9 )
		{
			return Calendar.SEPTEMBER;
		}
		else if ( month == 10 )
		{
			return Calendar.OCTOBER;
		}
		else if ( month == 11 )
		{
			return Calendar.NOVEMBER;
		}
		else if ( month == 12 )
		{
			return Calendar.DECEMBER;
		}
		return -1;
	}

	private static void displayExceptionLine (String line, boolean newLine)
	{
		if ( line == null || (System.out == null && System.err == null) )
		{
			return;
		}
		if ( System.out != null )
		{
			System.out.print (line);
			if (newLine)
			{
				System.out.println ();
			}
			System.out.flush ();
		}
		if ( System.err != null )
		{
			System.err.print (line);
			if (newLine)
			{
				System.err.println ();
			}
			System.err.flush ();
		}
	}

	/**
	 * Displays all the important information about exceptions.
	 * @param ex The exception to display.
	 * @param data Any additional data to display.
	 */
	public static synchronized void handleException (Throwable ex, Object data)
	{
		if ( ex == null || (System.out == null && System.err == null) )
		{
			return;
		}
		try
		{
			Calendar c = Calendar.getInstance ();

			int month  = convertCalendarMonthToReal (c.get (Calendar.MONTH));
			int day    = c.get (Calendar.DAY_OF_MONTH);
			int hour   = c.get (Calendar.HOUR_OF_DAY);
			int minute = c.get (Calendar.MINUTE);
			int second = c.get (Calendar.SECOND);

			String time = c.get (Calendar.YEAR) + DASH
				+ ((month  < 10)? ZERO : EMPTY_STR ) + month  + DASH
				+ ((day    < 10)? ZERO : EMPTY_STR ) + day    + SPACE
				+ ((hour   < 10)? ZERO : EMPTY_STR ) + hour   + COLON
				+ ((minute < 10)? ZERO : EMPTY_STR ) + minute + COLON
				+ ((second < 10)? ZERO : EMPTY_STR ) + second + COLON + SPACE;

			displayExceptionLine (time + ex, false);
		}
		catch (Throwable e)
		{
			// ignore here to avoid recurrency
		}

		try
		{
			String msg = ex.getMessage ();
			if ( msg != null )
			{
				displayExceptionLine (MSG_START + msg + APOSTROPHE, false);
			}
		}
		catch (Throwable e)
		{
			// ignore here to avoid recurrency
		}

		try
		{
			if ( data != null )
			{
				displayExceptionLine (DATA_START + data.toString () + APOSTROPHE, false);
			}
		}
		catch (Throwable e)
		{
			// ignore here to avoid recurrency
		}

		try
		{
			displayExceptionLine (EMPTY_STR, true);
		}
		catch (Throwable e)
		{
			// ignore here to avoid recurrency
		}

		StackTraceElement[] ste = ex.getStackTrace ();
		if ( ste != null )
		{
			for ( int i = 0; i < ste.length; i++ )
			{
				if ( ste[i] != null )
				{
					try
					{
						String clazz = ste[i].getClassName ();
						String file = ste[i].getFileName ();
						String function = ste[i].getMethodName ();
						int line = ste[i].getLineNumber ();
						String toShow = EXCEPTION_AT_STR;
						if ( clazz != null )
						{
							// let's display only our files
							if ( ! DEBUG_EXCEPTIONS
								&& ! clazz.startsWith
								("bogdro") )	// NOI18N
							{
								continue;
							}
							toShow += clazz;
						}
						else
						{
							toShow += UNKNOWN_CLASS;
						}
						if ( function != null )
						{
							toShow += DOT + function;
						}
						else
						{
							toShow += DOT + UNKNOWN_METHOD;
						}
						if ( file != null )
						{
							toShow += SPACE + L_PAREN + file;
						}
						else
						{
							toShow += SPACE + L_PAREN + UNKNOWN_FILE;
						}
						toShow += COLON + String.valueOf (line) + R_PAREN;

						displayExceptionLine (toShow, true);
					}
					catch (Throwable e)
					{
						// ignore here to avoid recurrency
					}
				}
			}
		}
		handleException (ex.getCause(), null);
	}
	/**
	 * This function joins 2 arrays of bytes together.
	 * @param orig The first array.
	 * @param toAdd The array to add.
	 * @return an array starting with "orig" followed by "toAdd".
	 */
	public static byte[] joinArrays (byte[] orig, byte[] toAdd)
	{
		if ( orig == null )
		{
			return toAdd;
		}
		if ( toAdd == null )
		{
			return orig;
		}
		byte[] ret = new byte[orig.length + toAdd.length];
		System.arraycopy (orig, 0, ret, 0, orig.length);
		System.arraycopy (toAdd, 0, ret, orig.length, toAdd.length);
		return ret;
	}

	/**
	 * A sample uncaught-exception handler class for threads.
	 */
	public static class UncExHndlr implements Thread.UncaughtExceptionHandler
	{
		private final Component target;

		public UncExHndlr ()
		{
			target = null;
		}
		
		public UncExHndlr (Component c)
		{
			target = c;
		}
		/**
		 * Called when an uncaught exception occurrs.
		 * @param t The thread, in which the exception occurred.
		 * @param ex The exception that occurred.
		 */
		@Override
		public void uncaughtException (Thread t, Throwable ex)
		{
			try
			{
				handleException (ex, "Utils.UncaughtExceptionHandler: Thread="	// NOI18N
					+ ((t != null)? t.getName() : "?"));	// NOI18N
			} catch (Throwable th) {}
			try
			{
				if ( target != null )
				{
					target.paintAll(target.getGraphics());
				}
			} catch (Throwable th) {}
		}

		@Override
		public String toString ()
		{
			return "Utils.UncExHndlr";	// NOI18N
		}
	}
}
