/*
 * CommandLineParser.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2022-2025 Bogdan Drozdowski, bogdro (at) users . sourceforge . net
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

import bogdrosoft.wfsgui.gui.WfsMainWindow;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * CommandLineParser - utility methods connected to parsing the command line.
 * @author Bogdan Drozdowski
 */
public class CommandLineParser
{
	// ----------- i18n stuff
	private static final ResourceBundle CMDLINE_BUNDLE =
		ResourceBundle.getBundle("bogdrosoft/wfsgui/i18n/CommandLineParser");
	private static final String PROG_INTRO_STRING = CMDLINE_BUNDLE.getString("A_GUI_for_WipeFreeSpace");
	private static final String CMD_LINE_STR = CMDLINE_BUNDLE.getString("Commandline_options")
		+ Utils.COLON +
		"\n--all-zeros\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("wipe_only_using_zeros") +
		"\n-b|--superblock <off>\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("superblock_offset") +
		"\n-B|--blocksize <size>\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("block_size") +
		"\n--conf <file>\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("config_from_file") +
		"\n-f|--force\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("continue_if_errors") +
		"\n--help, -h, -?, /?\t- "+	// NOI18N
		CMDLINE_BUNDLE.getString("print_help") +
		"\n--lang LL_CC_VV\t\t- "+	// NOI18N
		CMDLINE_BUNDLE.getString("select_language") +
		":\n\t\t\t  " +	// NOI18N
		CMDLINE_BUNDLE.getString("LL_language_CC") +
		"\n\t\t\t  " +	// NOI18N
		CMDLINE_BUNDLE.getString("country_code_VV_variant") +
		".\n\t\t\t  " +	// NOI18N
		CMDLINE_BUNDLE.getString("Separate_them_LL_required") +
		"\n--last-zero\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("add_wiping_with_zeros") +
		"\n--licence, --license\t- "+	// NOI18N
		CMDLINE_BUNDLE.getString("display_license") +
		"\n--method <name>\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("help_wiping_method") +
		"\n-n|--iterations <n>\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("number_of_iterations") +
		"\n--nopart\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("do_NOT_wipe_part") +
		"\n--nounrm\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("do_NOT_wipe_undel") +
		"\n--nowfs\t\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("do_NOT_wipe_unused") +
		"\n--no-wipe-zero-blocks\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("do_NOT_wipe_zero_blocks") +
		"\n--order <type>\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("help_wiping_order") +
		"\n--use-dedicated\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("use_dedicated") +
		"\n--use-ioctl\t\t- " +	// NOI18N
		CMDLINE_BUNDLE.getString("disable_cache") +
		"\n--version, -V\t\t- "+	// NOI18N
		CMDLINE_BUNDLE.getString("display_version")
		;
	private static final String VER_WORD = CMDLINE_BUNDLE.getString("Version");

	private static volatile boolean isMax = false;
	private static volatile boolean onlyZeros = false;
	private static volatile boolean superOff = false;
	private static volatile String superOffValue;
	private static volatile boolean blkSize = false;
	private static volatile String blkSizeValue;
	private static volatile boolean force = false;
	private static volatile boolean lastZero = false;
	private static volatile boolean method = false;
	private static volatile String methodName;
	private static volatile boolean iter = false;
	private static volatile String iterationsValue;
	private static volatile boolean nopart = false;
	private static volatile boolean nounrm = false;
	private static volatile boolean nowfs = false;
	private static volatile boolean ioctl = false;
	private static volatile boolean noWipeZeroBlocks = false;
	private static volatile boolean useDedicated = false;
	private static volatile boolean isOrder = false;
	private static volatile String wipingOrder;
	private static volatile int x = 0;
	private static volatile int y = 0;
	private static volatile int width;
	private static volatile int height;
	private static volatile int fontSize;
	private static volatile List<String> fsList;
	private static volatile String wfsPath;

	// non-instantiable
	private CommandLineParser () {}

	/**
	 * Gets the current "is maximized" property value.
	 * @return the current "is maximized" property value.
	 */
	public static synchronized boolean isMax ()
	{
		return isMax;
	}

	/**
	 * Gets the current "onlyZeros" property value.
	 * @return the current "onlyZeros" property value.
	 */
	public static synchronized boolean isOnlyZeros ()
	{
		return onlyZeros;
	}

	/**
	 * Gets the current "is superOff selected" property value.
	 * @return the current "is superOff selected" property value.
	 */
	public static synchronized boolean isSuperOff ()
	{
		return superOff;
	}

	/**
	 * Gets the current "superblock offset" property value.
	 * @return the current "superblock offset" property value.
	 */
	public static synchronized String getSuperOffValue ()
	{
		return superOffValue;
	}

	/**
	 * Gets the current "is blkSize selected" property value.
	 * @return the current "is blkSize selected" property value.
	 */
	public static synchronized boolean isBlkSize ()
	{
		return blkSize;
	}

	/**
	 * Gets the current "blocksize" property value.
	 * @return the current "blocksize" property value.
	 */
	public static synchronized String getBlkSizeValue ()
	{
		return blkSizeValue;
	}

	/**
	 * Gets the current "is force" property value.
	 * @return the current "is force" property value.
	 */
	public static synchronized boolean isForce ()
	{
		return force;
	}

	/**
	 * Gets the current "is lastZero" property value.
	 * @return the current "is lastZero" property value.
	 */
	public static synchronized boolean islastZero ()
	{
		return lastZero;
	}

	/**
	 * Gets the current "is method selected" property value.
	 * @return the current "is method selected" property value.
	 */
	public static synchronized boolean isMethod ()
	{
		return method;
	}

	/**
	 * Gets the current "methodName" property value.
	 * @return the current "methodName" property value.
	 */
	public static synchronized String getMethodName ()
	{
		return methodName;
	}

	/**
	 * Gets the current "is iter selected" property value.
	 * @return the current "is iter selected" property value.
	 */
	public static synchronized boolean isIter ()
	{
		return iter;
	}

	/**
	 * Gets the current "iterationsValue" property value.
	 * @return the current "iterationsValue" property value.
	 */
	public static synchronized String getIterationsValue ()
	{
		return iterationsValue;
	}

	/**
	 * Gets the current "is nopart" property value.
	 * @return the current "is nopart" property value.
	 */
	public static synchronized boolean isNopart ()
	{
		return nopart;
	}

	/**
	 * Gets the current "is nounrm" property value.
	 * @return the current "is nounrm" property value.
	 */
	public static synchronized boolean isNounrm ()
	{
		return nounrm;
	}

	/**
	 * Gets the current "is nowfs" property value.
	 * @return the current "is nowfs" property value.
	 */
	public static synchronized boolean isNowfs ()
	{
		return nowfs;
	}

	/**
	 * Gets the current "is ioctl" property value.
	 * @return the current "is ioctl" property value.
	 */
	public static synchronized boolean isIoctl ()
	{
		return ioctl;
	}

	/**
	 * Gets the current "is noWipeZeroBlocks" property value.
	 * @return the current "is noWipeZeroBlocks" property value.
	 */
	public static synchronized boolean isNoWipeZeroBlocks ()
	{
		return noWipeZeroBlocks;
	}

	/**
	 * Gets the current "is useDedicated" property value.
	 * @return the current "is useDedicated" property value.
	 */
	public static synchronized boolean isUseDedicated ()
	{
		return useDedicated;
	}

	/**
	 * Gets the current "is wiping order selected" property value.
	 * @return the current "is wiping order selected" property value.
	 */
	public static synchronized boolean isOrder ()
	{
		return isOrder;
	}

	/**
	 * Gets the current "wipingOrder" property value.
	 * @return the current "wipingOrder" property value.
	 */
	public static synchronized String getWipingOrder ()
	{
		return wipingOrder;
	}

	/**
	 * Gets the current X coordinate.
	 * @return the current X coordinate.
	 */
	public static synchronized int getX ()
	{
		return x;
	}

	/**
	 * Gets the current Y coordinate.
	 * @return the current Y coordinate.
	 */
	public static synchronized int getY ()
	{
		return y;
	}

	/**
	 * Gets the current width.
	 * @return the current width.
	 */
	public static synchronized int getWidth ()
	{
		return width;
	}

	/**
	 * Gets the current height.
	 * @return the current height.
	 */
	public static synchronized int getHeight ()
	{
		return height;
	}

	/**
	 * Gets the current fontSize.
	 * @return the current fontSize.
	 */
	public static synchronized int getFontSize ()
	{
		return fontSize;
	}

	/**
	 * Gets the current filesystem list.
	 * @return the current filesystem list.
	 */
	public static synchronized List<String> getFsList ()
	{
		return fsList;
	}
	/**
	 * Gets the current "wfsPath" property value.
	 * @return the current "wfsPath" property value.
	 */
	public static synchronized String getWfsPath ()
	{
		return wfsPath;
	}

	/**
	 * Reads the configuration from the given file and sets fields.
	 * @param f The file to read the config from.
	 */
	private static synchronized void readConfig (File f)
	{
		if ( f == null )
		{
			return;
		}
		if ( (! f.exists ()) || (! f.canRead ()) )
		{
			return;
		}

		try
		{
			ConfigFile cfg = new ConfigFile (f);
			cfg.read ();

			isMax = cfg.getIsMax ();
			x = cfg.getX ();
			y = cfg.getY ();
			onlyZeros = cfg.getAllZeros();
			superOffValue = String.valueOf(cfg.getSuperOffset());
			blkSizeValue = String.valueOf(cfg.getBlockSize());
			force = cfg.getForce();
			lastZero = cfg.getLastZero();
			methodName = cfg.getMethodName();
			iterationsValue = String.valueOf(cfg.getIterations());
			nopart = cfg.getNoWipePart();
			nounrm = cfg.getNoWipeUndel();
			nowfs = cfg.getNoWipeFreeSpace();
			ioctl = cfg.getIoctl();
			noWipeZeroBlocks = cfg.getIsNoWipeZeroBlocks();
			useDedicated = cfg.getIsUseDedicated();
			superOff = cfg.getSuperOffset() >= 0;
			blkSize = cfg.getBlockSize() > 0;
			method = cfg.getMethodName() != null;
			iter = cfg.getIterations() > 0;
			fsList = cfg.getFSList();
			width = cfg.getWidth();
			height = cfg.getHeight();
			fontSize = cfg.getFontSizeValue();
			wfsPath = cfg.getWfsPath();
			isOrder = cfg.getIsOrder();
			wipingOrder = cfg.getWipingOrder();
		}
		catch (Exception ex)
		{
			Utils.handleException (ex, "static readConfig");	// NOI18N
		}
	}

	/**
	 * Parses the given command line and performs actions based on it.
	 * @param args The command line to parse.
	 */
	public static synchronized void parse (String[] args)
	{
		if ( args == null )
		{
			return;
		}

		for ( int i = 0; i < args.length; i++ )
		{
			String currentArg = args[i].toLowerCase (Locale.ENGLISH);
			if ( currentArg.equals ("--help")	// NOI18N
				|| currentArg.equals ("-h")	// NOI18N
				|| currentArg.equals ("-?")	// NOI18N
				|| currentArg.equals ("/?") )	// NOI18N
			{
				System.out.println ("WipeFreeSpaceGUI2 - " + PROG_INTRO_STRING +	// NOI18N
					"\nAuthor: Bogdan Drozdowski, bogdro (at) users . sourceforge . net\n" +	// NOI18N
					"License: GPLv3+\n" +	// NOI18N
					"https://wipefreespace.sourceforge.io/\n\n" +	// NOI18N
					CMD_LINE_STR
					);
				Starter.closeProgram (0);
			}
			else if ( currentArg.equals ("--license")	// NOI18N
				|| currentArg.equals ("--licence") )	// NOI18N
			{
				System.out.println ("WipeFreeSpaceGUI2 - "+ PROG_INTRO_STRING +	// NOI18N
					"\nSee https://wipefreespace.sourceforge.io/\n" +	// NOI18N
					"Author: Bogdan 'bogdro' Drozdowski, bogdro (at) users . sourceforge . net.\n\n" +	// NOI18N
					"    This program is free software: you can redistribute it and/or modify\n" +	// NOI18N
					"    it under the terms of the GNU General Public License as published by\n" +	// NOI18N
					"    the Free Software Foundation, either version 3 of the License, or\n" +	// NOI18N
					"    (at your option) any later version.\n" +	// NOI18N
					"    This program is distributed in the hope that it will be useful,\n" +	// NOI18N
					"    but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +	// NOI18N
					"    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +	// NOI18N
					"    GNU General Public License for more details.\n" +	// NOI18N
					"    You should have received a copy of the GNU General Public License\n" +	// NOI18N
					"    along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"	// NOI18N
					);
				Starter.closeProgram (0);
			}
			else if ( currentArg.equals ("--version")	// NOI18N
				|| currentArg.equals ("-v") )	// NOI18N
			{
				System.out.println ("WipeFreeSpaceGUI2 " + VER_WORD + " " + WfsMainWindow.WFSGUI_VERSION);	// NOI18N
				Starter.closeProgram (0);
			}
			if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--all-zeros") )	// NOI18N
			{
				onlyZeros = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--superblock")	// NOI18N
				|| args[i].equals ("-b") )	// NOI18N
			{
				superOff = true;
				if ( i+1 < args.length )
				{
					superOffValue = args[i+1];
				}
				i++;
			}
			else if ( args[i].equals ("-B")	// NOI18N
				|| args[i].toLowerCase (Locale.ENGLISH).equals ("--blocksize") )	// NOI18N
			{
				blkSize = true;
				if ( i+1 < args.length )
				{
					blkSizeValue = args[i+1];
				}
				i++;
			}
			else if ( currentArg.equals ("--conf") )	// NOI18N
			{
				if ( i < args.length-1 )
				{
					readConfig (new File (args[i+1]));
				}
			}
			else if ( args[i].equals ("-f")	// NOI18N
				|| args[i].toLowerCase (Locale.ENGLISH).equals ("--force") )	// NOI18N
			{
				force = true;
			}
			else if ( currentArg.equals ("--lang") )	// NOI18N
			{
				if ( i < args.length-1 )
				{
					try
					{
						String[] locale = args[i+1].split ("_");	// NOI18N
						if ( locale != null )
						{
							Locale newLoc = null;
							if (locale.length == 1)
							{
								newLoc = new Locale (locale[0]);
							}
							else if (locale.length == 2)
							{
								newLoc = new Locale (locale[0],
									locale[1]);
							}
							else if (locale.length == 3)
							{
								newLoc = new Locale (locale[0],
									locale[1], locale[2]);
							}
							if ( newLoc != null )
							{
								Locale.setDefault (newLoc);
							}
						}
					}
					catch ( Exception ex )
					{
						Utils.handleException (ex,
							"cmdline.lang(" + args[i+1] + ")");	// NOI18N
					}
					i++;
				}
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--last-zero") )	// NOI18N
			{
				lastZero = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--method") )	// NOI18N
			{
				method = true;
				if ( i+1 < args.length )
				{
					methodName = args[i+1];
				}
				i++;
			}
			else if ( args[i].equals ("-n")	// NOI18N
				|| args[i].toLowerCase (Locale.ENGLISH).equals ("--iterations") )	// NOI18N
			{
				iter = true;
				if ( i+1 < args.length )
				{
					iterationsValue = args[i+1];
				}
				i++;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--nopart") )	// NOI18N
			{
				nopart = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--nounrm") )	// NOI18N
			{
				nounrm = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--nowfs") )	// NOI18N
			{
				nowfs = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--no-wipe-zero-blocks") )	// NOI18N
			{
				noWipeZeroBlocks = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--order") )	// NOI18N
			{
				isOrder = true;
				if ( i+1 < args.length )
				{
					wipingOrder = args[i+1];
				}
				i++;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--use-dedicated") )	// NOI18N
			{
				useDedicated = true;
			}
			else if ( args[i].toLowerCase (Locale.ENGLISH).equals ("--use-ioctl") )	// NOI18N
			{
				ioctl = true;
			}
		}	// for i
	}
}
