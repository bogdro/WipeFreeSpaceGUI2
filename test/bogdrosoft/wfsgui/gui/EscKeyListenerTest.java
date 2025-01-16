/*
 * EscKeyListenerTest.java, part of the WipeFreeSpaceGUI2 package.
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

import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;

/**
 * EscKeyListenerTest - a test for the EscKeyListener class.
 * @author Bogdan Drozdowski
 */
public class EscKeyListenerTest
{
	/**
	 * Test of install method, of class EscKeyListener.
	 */
	@Test
	public void testInstall()
	{
		System.out.println("install");
		Assume.assumeTrue("Need GUI", !GraphicsEnvironment.isHeadless());
		JFrame f = new JFrame();
		EscKeyListener listener = new EscKeyListener(f);
		listener.install();
		KeyListener[] listeners = f.getKeyListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			if (listeners[i] == listener)
			{
				return;
			}
		}
		fail("Listener not found");
	}

	/**
	 * Test of keyTyped method, of class EscKeyListener.
	 */
	@Test
	public void testKeyTyped()
	{
		System.out.println("keyTyped");
		Assume.assumeTrue("Need GUI", !GraphicsEnvironment.isHeadless());
		JFrame f = new JFrame();
		EscKeyListener listener = new EscKeyListener(f);
		listener.install();
		listener.keyTyped(
			new KeyEvent(f, 1, 0, 0, 0, (char)KeyEvent.VK_ESCAPE)
		);
	}


	/**
	 * Test of keyTyped method without frame, of class EscKeyListener.
	 */
	@Test
	public void testKeyTypedNullFrame()
	{
		System.out.println("testKeyTypedNullFrame");
		Assume.assumeTrue("Need GUI", !GraphicsEnvironment.isHeadless());
		EscKeyListener listener = new EscKeyListener(null);
		listener.install();
		listener.keyTyped(
			new KeyEvent(new JFrame(), 1, 0, 0, 0, (char)KeyEvent.VK_ESCAPE)
		);
	}

	/**
	 * Test of keyTyped method without event, of class EscKeyListener.
	 */
	@Test
	public void testKeyTypedNullEvent()
	{
		System.out.println("testKeyTypedNullEvent");
		Assume.assumeTrue("Need GUI", !GraphicsEnvironment.isHeadless());
		JFrame f = new JFrame();
		EscKeyListener listener = new EscKeyListener(f);
		listener.install();
		listener.keyTyped(null);
	}
	/**
	 * Test of toString method, of class EscKeyListener.
	 */
	@Test
	public void testToString()
	{
		System.out.println("toString");
		Assume.assumeTrue("Need GUI", !GraphicsEnvironment.isHeadless());
		String name = "testName";
		JFrame f = new JFrame();
		f.setName(name);
		EscKeyListener listener = new EscKeyListener(f);
		assertTrue(listener.toString().contains(name));
	}
}
