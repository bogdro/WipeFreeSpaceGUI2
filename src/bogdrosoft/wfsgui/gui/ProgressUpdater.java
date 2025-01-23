/*
 * ProgressUpdater.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2009-2025  Bogdan Drozdowski, bogdro (at) users . sourceforge . net
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

import bogdrosoft.wfsgui.Utils;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.text.JTextComponent;

/**
 * A class that launches a thread that updates the given text component,
 * progress bars and a label.
 * @author Bogdan Drozdowski
 */
public class ProgressUpdater implements Callable<Void>
{
	private static final byte STAGE_1_CHAR = '*';	// NOI18N
	private static final byte STAGE_2_CHAR = '-';	// NOI18N
	private static final byte STAGE_3_CHAR = '=';	// NOI18N

	private static final Pattern FS_PATTERN = Pattern.compile
			("^wipefreespace:([^:]+):\\s+", Pattern.MULTILINE);	// NOI18N

	private final int totalFSnum;
	private final int nStages;
	private final InputStream output;
	private final JTextComponent textComp;
	private final JProgressBar stageBar;
	private final JProgressBar fsBar;
	private final JProgressBar totalBar;
	private final JLabel currentFS;
	private volatile String lastFS = Utils.EMPTY_STR;	// previous filesystem's name

	private volatile boolean isStopped = false;
	private ExecutorService queue;

	/**
	 * Creates a new instance of ProgressUpdater.
	 * @param outputParam The InputStream to read, connected to the program's output/error stream.
	 * @param textCompParam The component to append program's output/error to.
	 * @param stageBarParam The progress bar describing the current stage.
	 * @param fsBarParam The progress bar describing the current filesystem wiping progress.
	 * @param totalBarParam The progress bar describing the total progress.
	 * @param currentFSParam The label to put the current filesystem's name in.
	 * @param totalFS The total number of filesystems to wipe (needed for total progress).
	 * @param numberOfStages The number of wiping stages (number of things to wipe on each filesystem).
	 */
	public ProgressUpdater (final InputStream outputParam,
		final JTextComponent textCompParam,
		final JProgressBar stageBarParam,
		final JProgressBar fsBarParam,
		final JProgressBar totalBarParam,
		final JLabel currentFSParam,
		int totalFS, int numberOfStages)
	{
		if ( outputParam == null )
		{
			throw new IllegalArgumentException ("ProgressUpdater:outputParam = null");	// NOI18N
		}
		if ( textCompParam == null )
		{
			throw new IllegalArgumentException ("ProgressUpdater:textCompParam = null");	// NOI18N
		}

		if ( totalFS <= 0 )
		{
			totalFS = 1;
		}
		totalFSnum = totalFS;
		if ( numberOfStages <= 0 )
		{
			numberOfStages = 3;
		}
		nStages = numberOfStages;
		output = outputParam;
		textComp = textCompParam;
		stageBar = stageBarParam;
		fsBar = fsBarParam;
		totalBar = totalBarParam;
		currentFS = currentFSParam;

		queue = Executors.newCachedThreadPool ();
	}

	@Override
	public Void call ()
	{
		lastFS = Utils.EMPTY_STR;
		while ( ! isStopped )
		{
			try
			{
				Thread.sleep (100);
			}
			catch (InterruptedException intex)
			{
				Thread.currentThread().interrupt();
				break;
			}
			if ( Thread.interrupted () || isStopped ) 
			{
				break;
			}
			// read & update
			try
			{
				if ( output.available () == 0 )
				{
					continue;
				}
			} catch (Exception ex) {continue;}
			try
			{
				byte[] r = new byte[output.available ()];
				int wasRead = output.read (r);
				if ( wasRead <= 0 )
				{
					continue;
				}
				r = Arrays.copyOf (r, wasRead);
				// don't force a specific encoding, because the program
				// might have been translated
				final String read = new String (r, 0, wasRead);
				UiUtils.changeGUI (new Runnable ()
				{
					@Override
					public synchronized void run ()
					{
						textComp.setText (textComp.getText () + read);
						String currText = textComp.getText ();
						Matcher m = FS_PATTERN.matcher (currText);
						String newFSname = lastFS;
						// count the number of FS done already
						String fs = Utils.EMPTY_STR;
						int fsDone = 0;
						while ( m.find () )
						{
							newFSname = m.group (1);
							if ( ! newFSname.equals (fs) )
							{
								if ( ! fs.isEmpty() )
								{
									fsDone++;
								}
								fs = newFSname;
							}
						}
						currentFS.setText (newFSname);
						// the rest of the code is progress-related
						if ( fsBar == null || stageBar == null
							|| totalBar == null )
						{
							return;
						}
						if ( ! lastFS.equals(newFSname)
							&& ! lastFS.equals(Utils.EMPTY_STR))
						{
							// new filesystem started. Substract the
							// current filesystem progress and increase
							// the total progress by 1 filesystem
							stageBar.setValue(0);
							fsBar.setValue(0);
							totalBar.setValue
								(((fsDone-1)
								*(totalBar.getMaximum()
								- totalBar.getMinimum()))
								/totalFSnum);
						}
						lastFS = newFSname;
						// === checking for new FS finished.
						// now find the current progress
						String[] lines = currText
							.replaceAll ("[\r\n]+", "\r")	// NOI18N
							.split ("\r");	// NOI18N
						// find the first line of the current FS
						int l = -1;
						if ( lines != null )
						{
							for ( l = 0; l < lines.length; l++ )
							{
								if ( lines[l] == null )
								{
									continue;
								}
								if ( lines[l].startsWith
									("wipefreespace:" + lastFS) )	// NOI18N
								{
									break;
								}
							}
						}
						if ( l >= 0 && lines != null )
						{
							// first line of the current FS found
							int stage1progress = 0;
							int stage2progress = 0;
							int stage3progress = 0;
							boolean hasStage1 = false;
							boolean hasStage2 = false;
							boolean hasStage3 = false;
							// check for lines with stage characters
							for ( int ln = l; ln < lines.length; ln++ )
							{
								// the length of the lines is the progress
								if ( lines[ln].indexOf (STAGE_1_CHAR) == 0 )
								{
									stage1progress = lines[ln]
										.trim ().length ();
									hasStage1 = true;
								}
								else if ( lines[ln].indexOf (STAGE_2_CHAR) == 0 )
								{
									stage2progress = lines[ln]
										.trim ().length ();
									hasStage2 = true;
								}
								else if ( lines[ln].indexOf (STAGE_3_CHAR) == 0 )
								{
									stage3progress = lines[ln]
										.trim ().length ();
									hasStage3 = true;
								}
							}
							// stage progresses are in the range 0 - 100
							if ( stage1progress < 100 && hasStage1 )
							{
								stageBar.setValue (
									(stage1progress*
									(stageBar.getMaximum ()
									- stageBar.getMinimum ()))/100);
							}
							else if ( stage2progress < 100 && hasStage2 )
							{
								stageBar.setValue (
									(stage2progress*
									(stageBar.getMaximum ()
									- stageBar.getMinimum ()))/100);
							}
							else if ( stage3progress < 100 && hasStage3 )
							{
								stageBar.setValue (
									(stage3progress*
									(stageBar.getMaximum ()
									- stageBar.getMinimum ()))/100);
							}
							else if ( (stage1progress == 100 && hasStage1)
								|| (stage2progress == 100 && hasStage2)
								|| (stage3progress == 100 && hasStage3) )
							{
								stageBar.setValue (
									stageBar.getMaximum ());
							}
							fsBar.setValue (((stage1progress
								+stage2progress
								+stage3progress)*
								(fsBar.getMaximum ()
								- fsBar.getMinimum ()))/(nStages*100));
							totalBar.setValue (fsDone
								*(totalBar.getMaximum ()
								- totalBar.getMinimum ())
								/(totalFSnum * totalBar.getMaximum ())
								+ fsBar.getValue ()*
									(fsBar.getMaximum ()
									- fsBar.getMinimum ())
									/ (totalFSnum * fsBar.getMaximum())
								);
						}
					}
				});
			}
			catch (Exception ex)
			{
				Utils.handleException(ex, "ProgressUpdater.call");	// NOI18N
			}
		}
		return null;
	}

	/**
	 * Starts this ProgressUpdater.
	 */
	public synchronized void startProcessing ()
	{
		while ( queue.submit (this) == null )
		{
			queue = Executors.newCachedThreadPool ();
		}
	}

	/**
	 * Stops this ProgressUpdater.
	 */
	public synchronized void stop ()
	{
		isStopped = true;
		queue.shutdown ();
	}
}
