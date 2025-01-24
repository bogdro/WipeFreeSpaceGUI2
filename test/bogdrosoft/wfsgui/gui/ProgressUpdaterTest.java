/*
 * ProgressUpdaterTest.java, part of the WipeFreeSpaceGUI2 package.
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

import java.io.ByteArrayInputStream;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ProgressUpdaterTest - a test for the ProgressUpdater class.
 * @author Bogdan Drozdowski
 */
public class ProgressUpdaterTest
{
	/**
	 * Test of call method, of class ProgressUpdater.
	 */
	@Test
	public void testCall()
	{
		JTextComponent textComponentMock = mock(JTextArea.class);
		when(textComponentMock.getText()).thenReturn("");

		JProgressBar stageBarParam = mock(JProgressBar.class);
		when(stageBarParam.getMaximum()).thenReturn(100);
		when(stageBarParam.getMinimum()).thenReturn(0);

		JProgressBar fsBarParam = mock(JProgressBar.class);
		when(fsBarParam.getMaximum()).thenReturn(100);
		when(fsBarParam.getMinimum()).thenReturn(0);

		JProgressBar totalBarParam = mock(JProgressBar.class);
		when(totalBarParam.getMaximum()).thenReturn(100);
		when(totalBarParam.getMinimum()).thenReturn(0);

		JLabel currentFSLabel = mock(JLabel.class);
		ProgressUpdater instance = new ProgressUpdater(
			new ByteArrayInputStream(new byte[] {1}),
			textComponentMock,
			stageBarParam,
			fsBarParam,
			totalBarParam,
			currentFSLabel,
			3, 3
		);
		instance.startProcessing();
		try
		{
			Thread.sleep (1000);
		} catch (InterruptedException intex) {}
		instance.stop();
		verify(textComponentMock).setText(anyString());
	}
}
