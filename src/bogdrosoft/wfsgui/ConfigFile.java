/*
 * ConfigFile.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2009-2025 Bogdan Drozdowski, bogdro (at) users . sourceforge . net
 * License: GNU General Public License, v3+
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for reading and writing WipeFreeSpaceGUI2 configuration files.
 * @author Bogdan Drozdowski
 */
public class ConfigFile
{
	// patterns for matching:
	private static final Pattern ALL_ZERO_PATTERN = Pattern.compile
		("all_zero\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern SUPERBLOCK_PATTERN = Pattern.compile
		("superblock_offset\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);	// NOI18N
	private static final Pattern BLOCKSIZE_PATTERN = Pattern.compile
		("blocksize\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern FORCE_WIPE_PATTERN = Pattern.compile
		("force\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern ITERATIONS_PATTERN = Pattern.compile
		("iterations\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);	// NOI18N
	private static final Pattern LAST_ZERO_PATTERN = Pattern.compile
		("last_zero\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern NOPART_PATTERN = Pattern.compile
		("no_part\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern NOUNRM_PATTERN = Pattern.compile
		("no_undel\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern NOWFS_PATTERN = Pattern.compile
		("no_free\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern IOCTL_PATTERN = Pattern.compile
		("no_cache\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern FS_LIST_PATTERN = Pattern.compile
		("filesystems\\s*=\\s*(.*+)", Pattern.CASE_INSENSITIVE);	// NOI18N
	private static final Pattern X_PATTERN = Pattern.compile
		("x\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);			// NOI18N
	private static final Pattern Y_PATTERN = Pattern.compile
		("y\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);			// NOI18N
	private static final Pattern WIDTH_PATTERN = Pattern.compile
		("width\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern HEIGHT_PATTERN = Pattern.compile
		("height\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern IS_MAX_PATTERN = Pattern.compile
		("ismax\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern FONT_SIZE_PATTERN = Pattern.compile
		("font_size\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern PATH_TO_WFS_PATTERN = Pattern.compile
		("wfs_path\\s*=\\s*(.*+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern IS_METHOD_CHOSEN_PATTERN = Pattern.compile
		("is_method\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern METHOD_NAME_PATTERN = Pattern.compile
		("method\\s*=\\s*(.*+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern NO_WIPE_ZERO_PATTERN = Pattern.compile
		("no_wipe_zero_blocks\\s*=\\s*(.*)", Pattern.CASE_INSENSITIVE);	// NOI18N
	private static final Pattern USE_DEDICATED_PATTERN = Pattern.compile
		("use_dedicated\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);	// NOI18N
	private static final Pattern IS_WIPING_ORDER_CHOSEN_PATTERN = Pattern.compile
		("is_order\\s*=\\s*(\\d+)", Pattern.CASE_INSENSITIVE);		// NOI18N
	private static final Pattern WIPING_ORDER_PATTERN = Pattern.compile
		("order\\s*=\\s*(.*+)", Pattern.CASE_INSENSITIVE);		// NOI18N

	private static final Pattern COMMENT_PATTERN = Pattern.compile
		("^\\s*#.*");		// NOI18N

	private File cfgFile;
	// wipefreespace parameters:
	private boolean allzeros;
	private int superOffset = -1;
	private int blockSize = -1;
	private boolean force;
	private int iterations = -1;
	private boolean lastZero;
	private boolean nopart;
	private boolean nounrm;
	private boolean nowfs;
	private boolean ioctl;
	private List<String> fsList;
	private String wfsPath;
	private boolean selectMethod;
	private String methodName;
	private boolean noWipeZeroBlocks = false;
	private boolean useDedicated = false;
	private boolean selectOrder;
	private String wipingOrder;
	// main window's parameters:
	private int x;
	private int y;
	private int width;
	private int height;
	private boolean isMax;
	private int fontSize;

	/**
	 * Creates a new instance of ConfigFile.
	 * @param f The file to read/write parameters from/to.
	 */
	public ConfigFile (File f)
	{
		if ( f == null )
		{
			throw new IllegalArgumentException ("ConfigFile:f==null");	// NOI18N
		}
		cfgFile = f;
	}

	/**
	 * Read the parameters from the given file and stores them inside private
	 * fields.
	 * @throws Exception on file error.
	 */
	public void read () throws Exception
	{
		allzeros = false;
		superOffset = -1;
		blockSize = -1;
		force = false;
		iterations = -1;
		lastZero = false;
		nopart = false;
		nounrm = false;
		nowfs = false;
		ioctl = false;
		fsList = new ArrayList<String> (10);
		x = 0;
		y = 0;
		width = 800;
		height = 600;
		isMax = false;
		fontSize = 12;

		BufferedReader br = null;
		try
		{
			// don't force a specific encoding
			br = new BufferedReader (new FileReader (cfgFile));
			String line;
			do
			{
				try
				{
					line = br.readLine ();
				}
				catch (IOException ex)
				{
					break;
				}
				if ( line == null )
				{
					break;
				}
				if ( line.length () == 0 )
				{
					continue;
				}

				if ( COMMENT_PATTERN.matcher(line).matches() )
				{
					continue;
				}

				Matcher allzeroM = ALL_ZERO_PATTERN.matcher (line);
				Matcher superM = SUPERBLOCK_PATTERN.matcher (line);
				Matcher bsizeM = BLOCKSIZE_PATTERN.matcher (line);
				Matcher forceM = FORCE_WIPE_PATTERN.matcher (line);
				Matcher iterM = ITERATIONS_PATTERN.matcher (line);
				Matcher lastzeroM = LAST_ZERO_PATTERN.matcher (line);
				Matcher nopartM = NOPART_PATTERN.matcher (line);
				Matcher nounrmM = NOUNRM_PATTERN.matcher (line);
				Matcher nowfsM = NOWFS_PATTERN.matcher (line);
				Matcher ioctlM = IOCTL_PATTERN.matcher (line);
				Matcher fsListM = FS_LIST_PATTERN.matcher (line);
				Matcher xM = X_PATTERN.matcher (line);
				Matcher yM = Y_PATTERN.matcher (line);
				Matcher widthM = WIDTH_PATTERN.matcher (line);
				Matcher heightM = HEIGHT_PATTERN.matcher (line);
				Matcher isMaxM = IS_MAX_PATTERN.matcher (line);
				Matcher fontSizeM = FONT_SIZE_PATTERN.matcher (line);
				Matcher wfsPathM = PATH_TO_WFS_PATTERN.matcher (line);
				Matcher isMethodM = IS_METHOD_CHOSEN_PATTERN.matcher (line);
				Matcher methodNameM = METHOD_NAME_PATTERN.matcher (line);
				Matcher noWipeZeroM = NO_WIPE_ZERO_PATTERN.matcher (line);
				Matcher useDedicatedM = USE_DEDICATED_PATTERN.matcher (line);
				Matcher isOrderM = IS_WIPING_ORDER_CHOSEN_PATTERN.matcher(line);
				Matcher orderM = WIPING_ORDER_PATTERN.matcher(line);

				if ( allzeroM.matches () )
				{
					allzeros = readIntUsingMatcher (allzeroM, 1, "all_zero") != 0;	// NOI18N
				}
				else if ( superM.matches () )
				{
					superOffset = readIntUsingMatcher (superM, 1, "super_off");	// NOI18N
				}
				else if ( bsizeM.matches () )
				{
					blockSize = readIntUsingMatcher (bsizeM, 1, "block_size");	// NOI18N
				}
				else if ( forceM.matches () )
				{
					force = readIntUsingMatcher (forceM, 1, "force") != 0;	// NOI18N
				}
				else if ( iterM.matches () )
				{
					iterations = readIntUsingMatcher (iterM, 1, "interations");	// NOI18N
				}
				else if ( lastzeroM.matches () )
				{
					lastZero = readIntUsingMatcher (lastzeroM, 1, "last_zero") != 0;	// NOI18N
				}
				else if ( nopartM.matches () )
				{
					nopart = readIntUsingMatcher (nopartM, 1, "no_part") != 0;	// NOI18N
				}
				else if ( nounrmM.matches () )
				{
					nounrm = readIntUsingMatcher (nounrmM, 1, "no_unrm") != 0;	// NOI18N
				}
				else if ( nowfsM.matches () )
				{
					nowfs = readIntUsingMatcher (nowfsM, 1, "no_wfs") != 0;	// NOI18N
				}
				else if ( ioctlM.matches () )
				{
					ioctl = readIntUsingMatcher (ioctlM, 1, "ioctl") != 0;	// NOI18N
				}
				else if ( fsListM.matches () )
				{
					try
					{
						String[] fs = fsListM.group (1).split (Utils.ZERO_BYTE);	// NOI18N
						if ( fs != null )
						{
							for ( int i=0; i < fs.length; i++ )
							{
								if ( fs[i] == null )
								{
									continue;
								}
								if ( fs[i].length () == 0 )
								{
									continue;
								}
								fsList.add (fs[i]);
							}
						}
					}
					catch (Exception ex)
					{
						Utils.handleException (ex, "ConfigFile.read.parseInt (fs_list)");	// NOI18N
					}
				}
				else if ( xM.matches () )
				{
					x = readIntUsingMatcher (xM, 1, "x");	// NOI18N
				}
				else if ( yM.matches () )
				{
					y = readIntUsingMatcher (yM, 1, "y");	// NOI18N
				}
				else if ( widthM.matches () )
				{
					width = readIntUsingMatcher (widthM, 1, "width");	// NOI18N
				}
				else if ( heightM.matches () )
				{
					height = readIntUsingMatcher (heightM, 1, "height");	// NOI18N
				}
				else if ( isMaxM.matches () )
				{
					isMax = readIntUsingMatcher (isMaxM, 1, "is_max") != 0;	// NOI18N
				}
				else if ( fontSizeM.matches () )
				{
					fontSize = readIntUsingMatcher (fontSizeM, 1, "font_size");	// NOI18N
				}
				else if ( wfsPathM.matches () )
				{
					wfsPath = wfsPathM.group (1);
				}
				else if ( isMethodM.matches () )
				{
					selectMethod = readIntUsingMatcher (isMethodM, 1, "is_method") != 0;	// NOI18N
				}
				else if ( methodNameM.matches () )
				{
					methodName = methodNameM.group (1);
				}
				else if ( noWipeZeroM.matches () )
				{
					noWipeZeroBlocks = readIntUsingMatcher (noWipeZeroM, 1, "no_wipe_zero_blocks") != 0;	// NOI18N
				}
				else if ( useDedicatedM.matches () )
				{
					useDedicated = readIntUsingMatcher (useDedicatedM, 1, "use_dedicated") != 0;	// NOI18N
				}
				else if ( isOrderM.matches () )
				{
					selectOrder = readIntUsingMatcher (isOrderM, 1, "is_order") != 0;	// NOI18N
				}
				else if ( orderM.matches () )
				{
					wipingOrder = orderM.group (1);
				}
			} while (true);
		}
		catch (IOException ex)
		{
			Utils.handleException(ex, "ConfigFile.read");
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException ex2)
				{
					Utils.handleException(ex2, "ConfigFile.read->exception");
				}
			}
		}

		// verify here
		if ( x < 0 )
		{
			x = 0;
		}
		if ( y < 0 )
		{
			y = 0;
		}
		if ( width < 0 )
		{
			width = 640;
		}
		if ( height < 0 )
		{
			height = 480;
		}
		if ( fontSize < 0 )
		{
			fontSize = 16;
		}
	}

	/**
	 * Writes the parameters to the given file.
	 * @throws Exception on file error.
	 */
	public void write () throws Exception
	{
		PrintWriter pw = null;
		try
		{
			// don't force a specific encoding
			pw = new PrintWriter (cfgFile);
			StringBuilder fsString = new StringBuilder (100);
			if ( fsList != null )
			{
				for ( int i=0; i < fsList.size (); i++ )
				{
					fsString.append (fsList.get (i)).append(Utils.ZERO_BYTE);
				}
			}
			pw.println ("all_zero = " + ((allzeros)? 1 : 0));	// NOI18N
			pw.println ("superblock_offset = " + superOffset);			// NOI18N
			pw.println ("blocksize = " + blockSize);				// NOI18N
			pw.println ("force = " + ((force)? 1 : 0));		// NOI18N
			pw.println ("iterations = " + iterations);				// NOI18N
			pw.println ("last_zero = " + ((lastZero)? 1 : 0));	// NOI18N
			pw.println ("no_part = " + ((nopart)? 1 : 0));		// NOI18N
			pw.println ("no_undel = " + ((nounrm)? 1 : 0));		// NOI18N
			pw.println ("no_free = " + ((nowfs)? 1 : 0));		// NOI18N
			pw.println ("no_cache = " + ((ioctl)? 1 : 0));		// NOI18N
			pw.println ("x = " + x);						// NOI18N
			pw.println ("y = " + y);						// NOI18N
			pw.println ("width = " + width);					// NOI18N
			pw.println ("height = " + height);					// NOI18N
			pw.println ("ismax = " + ((isMax)? 1 : 0));		// NOI18N
			pw.println ("filesystems = " + fsString.toString());			// NOI18N
			pw.println ("font_size = " + fontSize);					// NOI18N
			pw.println ("wfs_path = " + wfsPath);					// NOI18N
			pw.println ("is_method = " + ((selectMethod)? 1 : 0));	// NOI18N
			pw.println ("method = " + methodName);					// NOI18N
			pw.println ("no_wipe_zero_blocks = " + ((noWipeZeroBlocks)? 1 : 0));	// NOI18N
			pw.println ("use_dedicated = " + ((useDedicated)? 1 : 0));		// NOI18N
			pw.println ("is_order = " + ((selectOrder)? 1 : 0));	// NOI18N
			pw.println ("order = " + wipingOrder);					// NOI18N
		}
		catch (IOException ex)
		{
			Utils.handleException(ex, "ConfigFile.write");
		}
		finally
		{
			if (pw != null)
			{
				try
				{
					pw.close();
				}
				catch (Exception e)
				{
					Utils.handleException(e, "ConfigFile.write->exception");
				}
			}
		}
	}

	// ================ setters:

	/**
	 * Sets the X variable.
	 * @param v the new value.
	 */
	public void setX (int v)
	{
		x = v;
	}

	/**
	 * Sets the Y variable.
	 * @param v the new value.
	 */
	public void setY (int v)
	{
		y = v;
	}

	/**
	 * Sets the width variable.
	 * @param v the new value.
	 */
	public void setWidth (int v)
	{
		width = v;
	}

	/**
	 * Sets the height variable.
	 * @param v the new value.
	 */
	public void setHeight (int v)
	{
		height = v;
	}

	/**
	 * Sets the isMaximized variable.
	 * @param v the new value.
	 */
	public void setIsMaximized (boolean v)
	{
		isMax = v;
	}

	/**
	 * Sets the all-zeros variable.
	 * @param v the new value.
	 */
	public void setAllZeros (boolean v)
	{
		allzeros = v;
	}

	/**
	 * Sets the superblock offset variable.
	 * @param v the new value.
	 */
	public void setSuperOffset (int v)
	{
		superOffset = v;
	}

	/**
	 * Sets the block size variable.
	 * @param v the new value.
	 */
	public void setBlockSize (int v)
	{
		blockSize = v;
	}

	/**
	 * Sets the "force" variable.
	 * @param v the new value.
	 */
	public void setForce (boolean v)
	{
		force = v;
	}

	/**
	 * Sets the iterations variable.
	 * @param v the new value.
	 */
	public void setIterations (int v)
	{
		iterations = v;
	}

	/**
	 * Sets the last-zero variable.
	 * @param v the new value.
	 */
	public void setLastZero (boolean v)
	{
		lastZero = v;
	}

	/**
	 * Sets the wipe-partially-used-blocks variable.
	 * @param v the new value.
	 */
	public void setWipePart (boolean v)
	{
		nopart = v;
	}

	/**
	 * Sets the wipe-undelete variable.
	 * @param v the new value.
	 */
	public void setWipeUndel (boolean v)
	{
		nounrm = v;
	}

	/**
	 * Sets the wipe-free-space variable.
	 * @param v the new value.
	 */
	public void setWipeFreeSpace (boolean v)
	{
		nowfs = v;
	}

	/**
	 * Sets the ioctl variable.
	 * @param v the new value.
	 */
	public void setIoctl (boolean v)
	{
		ioctl = v;
	}

	/**
	 * Sets the fsList variable.
	 * @param v the new value.
	 */
	public void setFSList (List<String> v)
	{
		fsList = new ArrayList<String> (v);
	}

	/**
	 * Sets the font size variable.
	 * @param v the new value.
	 */
	public void setFontSizeValue (int v)
	{
		fontSize = v;
	}

	/**
	 * Sets the wipefreespace path variable.
	 * @param v the new value.
	 */
	public void setWfsPath (String v)
	{
		wfsPath = v;
	}

	/**
	 * Sets the selectMethod variable.
	 * @param v the new value.
	 */
	public void setIsMethodSelected (boolean v)
	{
		selectMethod = v;
	}

	/**
	 * Sets the method name variable.
	 * @param v the new value.
	 */
	public void setMethodName (String v)
	{
		methodName = v;
	}

	/**
	 * Sets the noWipeZeroBlocks variable.
	 * @param v the new value.
	 */
	public void setNoWipeZeroBlocks (boolean v)
	{
		noWipeZeroBlocks = v;
	}

	/**
	 * Sets the useDedicated variable.
	 * @param v the new value.
	 */
	public void setUseDedicated (boolean v)
	{
		useDedicated = v;
	}

	/**
	 * Sets the selectOrder variable.
	 * @param v the new value.
	 */
	public void setIsOrderSelected (boolean v)
	{
		selectOrder = v;
	}

	/**
	 * Sets the wiping order variable.
	 * @param v the new value.
	 */
	public void setWipingOrder (String v)
	{
		wipingOrder = v;
	}

	// ================ getters:

	/**
	 * Gets the X variable.
	 * @return the variable's value.
	 */
	public int getX ()
	{
		return x;
	}

	/**
	 * Gets the Y variable.
	 * @return the variable's value.
	 */
	public int getY ()
	{
		return y;
	}

	/**
	 * Gets the width variable.
	 * @return the variable's value.
	 */
	public int getWidth ()
	{
		return width;
	}

	/**
	 * Gets the height variable.
	 * @return the variable's value.
	 */
	public int getHeight ()
	{
		return height;
	}

	/**
	 * Gets the isMax variable.
	 * @return the variable's value.
	 */
	public boolean getIsMax ()
	{
		return isMax;
	}

	/**
	 * Gets the all-zeros variable.
	 * @return the variable's value.
	 */
	public boolean getAllZeros ()
	{
		return allzeros;
	}

	/**
	 * Gets the superblock offset variable.
	 * @return the variable's value.
	 */
	public int getSuperOffset ()
	{
		return superOffset;
	}

	/**
	 * Gets the block size variable.
	 * @return the variable's value.
	 */
	public int getBlockSize ()
	{
		return blockSize;
	}

	/**
	 * Gets the "force" variable.
	 * @return the variable's value.
	 */
	public boolean getForce ()
	{
		return force;
	}

	/**
	 * Gets the iterations variable.
	 * @return the variable's value.
	 */
	public int getIterations ()
	{
		return iterations;
	}

	/**
	 * Gets the last-zero variable.
	 * @return the variable's value.
	 */
	public boolean getLastZero ()
	{
		return lastZero;
	}

	/**
	 * Gets the wipe-partially-used-blocks variable.
	 * @return the variable's value.
	 */
	public boolean getNoWipePart ()
	{
		return nopart;
	}

	/**
	 * Gets the wipe-undelete variable.
	 * @return the variable's value.
	 */
	public boolean getNoWipeUndel ()
	{
		return nounrm;
	}

	/**
	 * Gets the wipe-free-space variable.
	 * @return the variable's value.
	 */
	public boolean getNoWipeFreeSpace ()
	{
		return nowfs;
	}

	/**
	 * Gets the ioctl variable.
	 * @return the variable's value.
	 */
	public boolean getIoctl ()
	{
		return ioctl;
	}

	/**
	 * Gets the fsList variable.
	 * @return the variable's value.
	 */
	public List<String> getFSList ()
	{
		return fsList;
	}

	/**
	 * Gets the font size variable.
	 * @return the variable's value.
	 */
	public int getFontSizeValue ()
	{
		return fontSize;
	}

	/**
	 * Gets the wipefreespace path variable.
	 * @return the variable's value.
	 */
	public String getWfsPath ()
	{
		return wfsPath;
	}

	/**
	 * Gets the selectMethod variable.
	 * @return the variable's value.
	 */
	public boolean getIsMethod ()
	{
		return selectMethod;
	}

	/**
	 * Gets the method name variable.
	 * @return the variable's value.
	 */
	public String getMethodName ()
	{
		return methodName;
	}

	/**
	 * Gets the noWipeZeroBlocks variable.
	 * @return the variable's value.
	 */
	public boolean getIsNoWipeZeroBlocks ()
	{
		return noWipeZeroBlocks;
	}

	/**
	 * Gets the useDedicated variable.
	 * @return the variable's value.
	 */
	public boolean getIsUseDedicated ()
	{
		return useDedicated;
	}

	/**
	 * Gets the selectOrder variable.
	 * @return the variable's value.
	 */
	public boolean getIsOrder ()
	{
		return selectOrder;
	}

	/**
	 * Gets the wiping order variable.
	 * @return the variable's value.
	 */
	public String getWipingOrder ()
	{
		return wipingOrder;
	}

	private int readIntUsingMatcher(Matcher m, int group, String field)
	{
		try
		{
			return Integer.parseInt (m.group (group));
		}
		catch (NumberFormatException ex)
		{
			Utils.handleException (ex, "ConfigFile.read.parseInt (" + field + ")");	// NOI18N
			return -1;
		}
	}
}
