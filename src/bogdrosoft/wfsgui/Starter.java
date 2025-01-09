/*
 * Starter.java, part of the WipeFreeSpaceGUI2 package.
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

import bogdrosoft.wfsgui.gui.UiUtils;
import bogdrosoft.wfsgui.gui.WfsMainWindow;
import java.io.File;
import java.io.PrintStream;
import java.util.ResourceBundle;

/**
 * This class starts the WipeFreeSpaceGUI2 program or reports an error to the user.
 * @author Bogdan Drozdowski
 */
public class Starter
{
	private static final String START_ERROR
		= ResourceBundle.getBundle("BogDroSoft/wfsgui/i18n/Starter")
		.getString("WfsGui2_Start_Error");

	private static String logFile = "wfsgui2.log";	// NOI18N

	private Starter ()
	{
		// private constructor so the class can't be instantiated
	}

	/**
	 * WipeFreeSpaceGUI2 program starting point.
	 * @param args the command line arguments.
	 */
	public static void main(String[] args)
	{
		// redirect stderr (caught and uncaught exceptions) to a file
		logFile = redirectStderrToFile (logFile);

		// set default uncaught exception handler:
		Thread.setDefaultUncaughtExceptionHandler (Utils.handler);

		try
		{
			WfsMainWindow.start (args);
		}
		catch (Throwable ex)
		{
			Utils.handleException (ex, "*** " + START_ERROR + " ***"); // NOI18N
			UiUtils.showErrorMessage(null, START_ERROR);
			// close the log file:
			redirectStderrToFile (null);
			// exit the program:
			System.exit (-1);
		}
	}
	
	/**
	 * Called when the programs needs to close.
	 * @param retval Return value passed to System.exit ().
	 */
	public static void closeProgram (int retval)
	{
		// close logging
		if ( System.err != null )
		{
			System.err.close ();
		}
		if ( logFile != null )
		{
			// remove the log file if empty
			File log = new File (logFile);
			if ( log.exists () && log.length() == 0 )
			{
				if ( (! log.delete ()) && retval == 0 )
				{
					retval = 1;
				}
			}
		}
		System.exit (retval);
	}

	/**
	 * Redirects the standard error output to a log file.
	 * @param filename The name of the file to redirect the output to.
	 * @return The filename the output was actually redirected to.
	 */
	private static String redirectStderrToFile (String filename)
	{
		if ( filename == null )
		{
			System.err.close ();
			return null;
		}
		try
		{
			// don't force any encodings, because the translated messages may
			// be in another encoding
			System.setErr (new PrintStream (new File (filename)));
		}
		catch (Exception ex)
		{
			String dirSep = null;
			try
			{
				dirSep = System.getProperty ("file.separator", "/");	// NOI18N
			} catch (Exception e) {}
			if ( dirSep == null )
			{
				dirSep = File.separator;
			}
			if ( dirSep == null )
			{
				dirSep = "/";	// NOI18N
			}
			String[] dirs = new String[7];
			try
			{
				dirs[0] = System.getProperty ("user.dir");	// NOI18N
			} catch (Exception e) {}
			try
			{
				dirs[1] = System.getProperty ("user.home");	// NOI18N
			} catch (Exception e) {}
			try
			{
				dirs[2] = System.getenv ("HOME");	// NOI18N
			} catch (Exception e) {}
			try
			{
				dirs[3] = System.getenv ("TMP");	// NOI18N
			} catch (Exception e) {}
			try
			{
				dirs[4] = System.getenv ("TEMP");	// NOI18N
			} catch (Exception e) {}
			try
			{
				dirs[5] = System.getenv ("TMPDIR");	// NOI18N
			} catch (Exception e) {}
			try
			{
				dirs[6] = System.getenv ("TEMPDIR");	// NOI18N
			} catch (Exception e) {}
			int i;
			for ( i = 0; i < dirs.length; i++ )
			{
				if ( dirs[i] == null )
				{
					continue;
				}
				if ( dirs[i].isEmpty () )
				{
					continue;
				}
				try
				{
					// don't force any encodings, because the translated messages may
					// be in another encoding
					System.setErr (new PrintStream (new File (
						dirs[i] + dirSep + filename)));
					filename = dirs[i] + dirSep + filename;
					break;
				}
				catch (Exception e)
				{
					Utils.handleException (ex, "redirectStderrToFile -> setErr");	// NOI18N
				}
			}
			if ( i == dirs.length )
			{
				Utils.handleException (ex, "redirectStderrToFile -> none of dirs worked");	// NOI18N
			}
		}
		return filename;
	}
}
