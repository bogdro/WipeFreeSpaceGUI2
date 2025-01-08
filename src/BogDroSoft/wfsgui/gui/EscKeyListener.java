/*
 * EscKeyListener.java, part of the WipeFreeSpaceGUI2 package.
 *
 * Copyright (C) 2011-2024 Bogdan Drozdowski, bogdro (at) users . sourceforge . net
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

package BogDroSoft.wfsgui.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;

/**
 * A class that closes the window when the Esc key is pressed. Must
 * be registered on all elements of the window that desire to catch this key.
 * @author Bogdan Drozdowski
 */
public class EscKeyListener extends KeyAdapter
{
	private final Window frame; // need Window for dispose().

	/**
	 * Creates an EscKeyListener for the given Window.
	 * @param wFrame The frame to connect this listener to. All
	 *	of the frame's Components will have this listener
	 *	installed, too.
	 */
	public EscKeyListener (Window wFrame)
	{
		frame = wFrame;
	}

	/**
	 * Installs the key listener into the Windows passed in the constructor.
	 */
	public void install()
	{
		addKeyListeners (frame);
	}

	/**
	 * Recursively adds this keylistener to the given Component
	 *	and all its subcomponents.
	 * @param c The Component with Components that will have this keylistener.
	 */
	private void addKeyListeners (Component c)
	{
		if ( c == null )
		{
			return;
		}
		c.addKeyListener (this);
		if ( c instanceof Container )
		{
			Component[] subComps = ((Container)c).getComponents ();
			if ( subComps != null )
			{
				for ( int i = 0; i < subComps.length; i++ )
				{
					if ( subComps[i] != null )
					{
						addKeyListeners (subComps[i]);
					}
				}
			}
		}
	}

	/**
	 * Receives key-typed events (called when the user types a key).
	 * @param ke The key-typed event.
	 */
	@Override
	public void keyTyped (KeyEvent ke)
	{
		if ( ke == null || frame == null )
		{
			return;
		}
		if ( ke.getKeyChar () == KeyEvent.VK_ESCAPE )
		{
			WindowListener[] listeners = frame.getWindowListeners ();
			if ( listeners != null )
			{
				for ( int i = 0; i < listeners.length; i++ )
				{
					if ( listeners[i] != null )
					{
						listeners[i].windowClosing (null);
					}
				}
			}
			frame.dispose ();
		}
	}

	@Override
	public String toString ()
	{
		return "EscKeyListener(" + frame.getName () + ")";	// NOI18N
	}
}
