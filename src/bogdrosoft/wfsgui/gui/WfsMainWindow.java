/*
 * WfsMainWindow.java, part of the WipeFreeSpaceGUI2 package.
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
package bogdrosoft.wfsgui.gui;

import bogdrosoft.wfsgui.CommandLineParser;
import bogdrosoft.wfsgui.ConfigFile;
import bogdrosoft.wfsgui.Starter;
import bogdrosoft.wfsgui.Utils;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

/**
 * This class represents the main window of the WipeFreeSpaceGUI2 program.
 * @author Bogdan Drozdowski
 */
public class WfsMainWindow extends javax.swing.JFrame
{
	private static final Color GREEN_LABEL_COLOR = new Color (0, 204, 0);
	// i18n stuff:
	private static final ResourceBundle MAIN_BUNDLE =
		ResourceBundle.getBundle("bogdrosoft/wfsgui/i18n/WfsMainWindow");

	private static final String WFSGUI_CONFIG_FILES = MAIN_BUNDLE.getString("WipeFreeSpace_configuration_files");
	private static final String QUESTION_TITLE = MAIN_BUNDLE.getString("Question");
	private static final String ERROR_TITLE = MAIN_BUNDLE.getString("Error");
	private static final String STOP_PROC_MSG = MAIN_BUNDLE.getString("Really_stop");
	private static final String NO_FILESYSTEMS = MAIN_BUNDLE.getString("No_filesystems");
	private static final String CANNOT_EXECUTE = MAIN_BUNDLE.getString("cant_start");
	private static final String WFS_NOT_FOUND_MSG = MAIN_BUNDLE.getString("wipefreespace_not_found");
	private static final String NOTHING_TO_WIPE = MAIN_BUNDLE.getString("NOTHING_TO_WIPE");
	private static final String FILE_EXISTS_OVERWRITE = MAIN_BUNDLE.getString("exists_Overwrite");
	private static final String FILE_NOT_WRITABLE = MAIN_BUNDLE.getString("Cant_write_to_file");

	/** Current version number as a String. */
	public static final String WFSGUI_VERSION =
		ResourceBundle.getBundle("bogdrosoft/wfsgui/rsrc/version")	// NOI18N
		.getString("VER");	// NOI18N

	private JFileChooser fsChooser;
	private JFileChooser cfgFC;
	private JFileChooser progFC;

	private transient volatile Process wfs = null;
	private transient volatile ProgressUpdater stdoutUpdater;
	private transient volatile ProgressUpdater stderrUpdater;
	private transient volatile SwingWorker<Void, Void> wfsMonitor = null;

	/**
	 * Creates new form WfsMainWindow.
	 */
	WfsMainWindow()
	{
		// set uncaught exception handler for GUI threads, just in case:
		Utils.UncExHndlr.setHandlerForGuiThreads(this);

		initComponents();

		/* add the Esc key listener to the frame and all components. */
		new EscKeyListener (this).install();

		optIoctlCheckBox.setForeground (GREEN_LABEL_COLOR);
		optForceCheckBox.setForeground (GREEN_LABEL_COLOR);

		// read the command line:
		optAllZerosCheckBox.setSelected(CommandLineParser.isOnlyZeros());
		optBlocksizeCheckBox.setSelected(CommandLineParser.isBlkSize());
		if ( CommandLineParser.getBlkSizeValue() != null )
		{
			blocksizeTextField.setText(CommandLineParser.getBlkSizeValue());
		}
		else
		{
			blocksizeTextField.setText("");
		}
		optForceCheckBox.setSelected(CommandLineParser.isForce());
		optIoctlCheckBox.setSelected(CommandLineParser.isIoctl());
		optIterCheckBox.setSelected(CommandLineParser.isIter());
		if ( CommandLineParser.getIterationsValue() != null )
		{
			iterationsTextField.setText(CommandLineParser.getIterationsValue());
		}
		else
		{
			iterationsTextField.setText("");
		}
		optLastZeroCheckBox.setSelected(CommandLineParser.islastZero());
		optMethodCheckBox.setSelected(CommandLineParser.isMethod());
		optNoPartCheckBox.setSelected(CommandLineParser.isNopart());
		optNoUnrmCheckBox.setSelected(CommandLineParser.isNounrm());
		optNoWfsCheckBox.setSelected(CommandLineParser.isNowfs());
		optNoWipeZeroBlkCheckBox.setSelected(CommandLineParser.isNoWipeZeroBlocks());
		optSuperblockCheckBox.setSelected(CommandLineParser.isSuperOff());
		optUseDedicatedCheckBox.setSelected(CommandLineParser.isUseDedicated());
		optOrderCheckBox.setSelected(CommandLineParser.isOrder());
		if ( CommandLineParser.getWipingOrder() != null )
		{
			wipingOrderComboBox.setSelectedItem (CommandLineParser.getWipingOrder());
		}
		else
		{
			wipingOrderComboBox.setSelectedIndex(0);
		}
		if ( CommandLineParser.getSuperOffValue() != null)
		{
			superblockTextField.setText(CommandLineParser.getSuperOffValue());
		}
		else
		{
			superblockTextField.setText("");
		}
		if ( CommandLineParser.getMethodName() != null )
		{
			wipingMethodComboBox.setSelectedItem(CommandLineParser.getMethodName());
		}
		else
		{
			wipingMethodComboBox.setSelectedIndex(0);
		}
		// filesystem list
		List<String> fslist = CommandLineParser.getFsList();
		if ( fslist != null && !fslist.isEmpty() )
		{
			fsList.setListData(new Vector<String>(fslist));
		}
		if (CommandLineParser.getFontSize() > 0)
		{
			fontSizeSpinner.setValue (CommandLineParser.getFontSize());
		}
		else
		{
			fontSizeSpinner.setValue (fontSizeSpinner.getValue ());	// refresh the font in the window
		}
		pathToWfsTextField.setText(CommandLineParser.getWfsPath());
		if ( CommandLineParser.isMax() )
		{
			setExtendedState (Frame.MAXIMIZED_BOTH);
		}
		else
		{
			setExtendedState (getExtendedState () & ~ Frame.MAXIMIZED_BOTH);
			if ( CommandLineParser.getX () > 0 && CommandLineParser.getY () > 0 )
			{
				setLocation (Math.max(0, CommandLineParser.getX ()),
					Math.max(0, CommandLineParser.getY ()));
			}
			if ( CommandLineParser.getWidth () > 0 && CommandLineParser.getHeight () > 0 )
			{
				setSize (Math.max(0, CommandLineParser.getWidth ()),
					Math.max(0, CommandLineParser.getHeight ()));
			}
		}
		UiUtils.changeSizeToScreen(this);
	}

	/**
	 * This method is called from within the constructor to initialize the
	 * form. WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents()
        {

                mainScrollPane = new javax.swing.JScrollPane();
                mainPanel = new javax.swing.JPanel();
                fsListLabel = new javax.swing.JLabel();
                fsListScrollPane = new javax.swing.JScrollPane();
                fsList = new javax.swing.JList<>();
                addFsBut = new javax.swing.JButton();
                delFsBut = new javax.swing.JButton();
                wipingOptsLabel = new javax.swing.JLabel();
                optAllZerosCheckBox = new javax.swing.JCheckBox();
                optSuperblockCheckBox = new javax.swing.JCheckBox();
                superblockTextField = new javax.swing.JTextField();
                optBlocksizeCheckBox = new javax.swing.JCheckBox();
                blocksizeTextField = new javax.swing.JTextField();
                optForceCheckBox = new javax.swing.JCheckBox();
                optIterCheckBox = new javax.swing.JCheckBox();
                iterationsTextField = new javax.swing.JTextField();
                optLastZeroCheckBox = new javax.swing.JCheckBox();
                optNoPartCheckBox = new javax.swing.JCheckBox();
                optNoUnrmCheckBox = new javax.swing.JCheckBox();
                optNoWfsCheckBox = new javax.swing.JCheckBox();
                optIoctlCheckBox = new javax.swing.JCheckBox();
                optMethodCheckBox = new javax.swing.JCheckBox();
                wipingMethodComboBox = new javax.swing.JComboBox<>();
                optNoWipeZeroBlkCheckBox = new javax.swing.JCheckBox();
                optUseDedicatedCheckBox = new javax.swing.JCheckBox();
                fontSizeSpinner = new javax.swing.JSpinner();
                fontSizeLabel = new javax.swing.JLabel();
                startButton = new javax.swing.JButton();
                stopButton = new javax.swing.JButton();
                loadConfButton = new javax.swing.JButton();
                saveButton = new javax.swing.JButton();
                aboutButton = new javax.swing.JButton();
                exitButton = new javax.swing.JButton();
                pathToWfsLabel = new javax.swing.JLabel();
                pathToWfsTextField = new javax.swing.JTextField();
                nowWipingDescLabel = new javax.swing.JLabel();
                nowWipingNameLabel = new javax.swing.JLabel();
                stageProgressLabel = new javax.swing.JLabel();
                fsProgressLabel = new javax.swing.JLabel();
                totalProgressLabel = new javax.swing.JLabel();
                fsProgressBar = new javax.swing.JProgressBar();
                stageProgressBar = new javax.swing.JProgressBar();
                totalProgressBar = new javax.swing.JProgressBar();
                wfsOutputLabel = new javax.swing.JLabel();
                wfsOutputScrollPane = new javax.swing.JScrollPane();
                wfsOutputTextArea = new javax.swing.JTextArea();
                wfsErrorsLabel = new javax.swing.JLabel();
                wfsErrorsScrollPane = new javax.swing.JScrollPane();
                wfsErrorsTextArea = new javax.swing.JTextArea();
                wfsBrowseButton = new javax.swing.JButton();
                optOrderCheckBox = new javax.swing.JCheckBox();
                wipingOrderComboBox = new javax.swing.JComboBox<>();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bogdrosoft/wfsgui/i18n/WfsMainWindow"); // NOI18N
                setTitle(bundle.getString("wfsgui_title")); // NOI18N
                setIconImage((new javax.swing.ImageIcon (getClass ().getResource ("/bogdrosoft/wfsgui/rsrc/wfsgui-icon.png"))).getImage ());
                addWindowListener(new java.awt.event.WindowAdapter()
                {
                        public void windowClosing(java.awt.event.WindowEvent evt)
                        {
                                formWindowClosing(evt);
                        }
                });

                fsListLabel.setLabelFor(fsList);
                fsListLabel.setText(bundle.getString("fs_to_wipe")); // NOI18N

                fsListScrollPane.setEnabled(false);
                fsListScrollPane.setFocusable(false);

                fsList.setFocusable(false);
                fsListScrollPane.setViewportView(fsList);

                addFsBut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/add_fs.png"))); // NOI18N
                addFsBut.setText(bundle.getString("but_add_fs")); // NOI18N
                addFsBut.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                addFsButActionPerformed(evt);
                        }
                });

                delFsBut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/del_fs.png"))); // NOI18N
                delFsBut.setText(bundle.getString("but_del_fs")); // NOI18N
                delFsBut.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                delFsButActionPerformed(evt);
                        }
                });

                wipingOptsLabel.setText(bundle.getString("wiping_opts")); // NOI18N

                optAllZerosCheckBox.setText(bundle.getString("opt_all_zeros")); // NOI18N

                optSuperblockCheckBox.setText(bundle.getString("opt_superblock")); // NOI18N
                optSuperblockCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optSuperblockCheckBoxItemStateChanged(evt);
                        }
                });
                optSuperblockCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optSuperblockCheckBoxStateChanged(evt);
                        }
                });

                superblockTextField.setEnabled(false);

                optBlocksizeCheckBox.setText(bundle.getString("opt_blocksize")); // NOI18N
                optBlocksizeCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optBlocksizeCheckBoxItemStateChanged(evt);
                        }
                });
                optBlocksizeCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optBlocksizeCheckBoxStateChanged(evt);
                        }
                });

                blocksizeTextField.setEnabled(false);

                optForceCheckBox.setForeground(new java.awt.Color(0, 204, 0));
                optForceCheckBox.setText(bundle.getString("opt_force")); // NOI18N
                optForceCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optForceCheckBoxItemStateChanged(evt);
                        }
                });
                optForceCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optForceCheckBoxStateChanged(evt);
                        }
                });

                optIterCheckBox.setText(bundle.getString("opt_iterations")); // NOI18N
                optIterCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optIterCheckBoxItemStateChanged(evt);
                        }
                });
                optIterCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optIterCheckBoxStateChanged(evt);
                        }
                });

                iterationsTextField.setEnabled(false);

                optLastZeroCheckBox.setText(bundle.getString("opt_last_zero")); // NOI18N

                optNoPartCheckBox.setText(bundle.getString("opt_nopart")); // NOI18N
                optNoPartCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optNoPartCheckBoxItemStateChanged(evt);
                        }
                });
                optNoPartCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optNoPartCheckBoxStateChanged(evt);
                        }
                });

                optNoUnrmCheckBox.setText(bundle.getString("opt_nounrm")); // NOI18N
                optNoUnrmCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optNoUnrmCheckBoxItemStateChanged(evt);
                        }
                });
                optNoUnrmCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optNoUnrmCheckBoxStateChanged(evt);
                        }
                });

                optNoWfsCheckBox.setText(bundle.getString("opt_nowfs")); // NOI18N
                optNoWfsCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optNoWfsCheckBoxItemStateChanged(evt);
                        }
                });
                optNoWfsCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optNoWfsCheckBoxStateChanged(evt);
                        }
                });

                optIoctlCheckBox.setForeground(new java.awt.Color(0, 204, 0));
                optIoctlCheckBox.setText(bundle.getString("opt_ioctl")); // NOI18N
                optIoctlCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optIoctlCheckBoxItemStateChanged(evt);
                        }
                });
                optIoctlCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optIoctlCheckBoxStateChanged(evt);
                        }
                });

                optMethodCheckBox.setText(bundle.getString("opt_method")); // NOI18N
                optMethodCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optMethodCheckBoxItemStateChanged(evt);
                        }
                });
                optMethodCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optMethodCheckBoxStateChanged(evt);
                        }
                });

                wipingMethodComboBox.setEditable(true);
                wipingMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Gutmann", "random", "DoD", "Schneier" }));
                wipingMethodComboBox.setEnabled(false);

                optNoWipeZeroBlkCheckBox.setText(bundle.getString("opt_no_wipe_zero_blocks")); // NOI18N

                optUseDedicatedCheckBox.setText(bundle.getString("opt_dedicated")); // NOI18N

                fontSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(12, 0, null, 1));
                fontSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                fontSizeSpinnerStateChanged(evt);
                        }
                });

                fontSizeLabel.setLabelFor(fontSizeSpinner);
                fontSizeLabel.setText(bundle.getString("font_size")); // NOI18N

                startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/start.png"))); // NOI18N
                startButton.setText(bundle.getString("but_start_wiping")); // NOI18N
                startButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                startButtonActionPerformed(evt);
                        }
                });

                stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/stop.png"))); // NOI18N
                stopButton.setText(bundle.getString("but_stop_wiping")); // NOI18N
                stopButton.setEnabled(false);
                stopButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                stopButtonActionPerformed(evt);
                        }
                });

                loadConfButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/loadconf.png"))); // NOI18N
                loadConfButton.setText(bundle.getString("but_load_conf")); // NOI18N
                loadConfButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                loadConfButtonActionPerformed(evt);
                        }
                });

                saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/saveconf.png"))); // NOI18N
                saveButton.setText(bundle.getString("but_save_conf")); // NOI18N
                saveButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                saveButtonActionPerformed(evt);
                        }
                });

                aboutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/about.png"))); // NOI18N
                aboutButton.setText(bundle.getString("but_about")); // NOI18N
                aboutButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                aboutButtonActionPerformed(evt);
                        }
                });

                exitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bogdrosoft/wfsgui/rsrc/exit.png"))); // NOI18N
                exitButton.setText(bundle.getString("but_exit")); // NOI18N
                exitButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                exitButtonActionPerformed(evt);
                        }
                });

                pathToWfsLabel.setLabelFor(pathToWfsTextField);
                pathToWfsLabel.setText(bundle.getString("lab_path_to_wfs")); // NOI18N

                nowWipingDescLabel.setText(bundle.getString("lab_now_wiping")); // NOI18N

                nowWipingNameLabel.setText("-");

                stageProgressLabel.setLabelFor(stageProgressBar);
                stageProgressLabel.setText(bundle.getString("lab_stage_progress")); // NOI18N

                fsProgressLabel.setLabelFor(fsProgressBar);
                fsProgressLabel.setText(bundle.getString("lab_fs_progress")); // NOI18N

                totalProgressLabel.setLabelFor(totalProgressBar);
                totalProgressLabel.setText(bundle.getString("lab_total_progress")); // NOI18N

                fsProgressBar.setForeground(new java.awt.Color(0, 204, 0));
                fsProgressBar.setStringPainted(true);

                stageProgressBar.setForeground(new java.awt.Color(0, 204, 0));
                stageProgressBar.setStringPainted(true);

                totalProgressBar.setForeground(new java.awt.Color(0, 204, 0));
                totalProgressBar.setStringPainted(true);

                wfsOutputLabel.setLabelFor(wfsOutputTextArea);
                wfsOutputLabel.setText(bundle.getString("lab_wfs_output")); // NOI18N

                wfsOutputTextArea.setEditable(false);
                wfsOutputTextArea.setColumns(20);
                wfsOutputTextArea.setRows(5);
                wfsOutputScrollPane.setViewportView(wfsOutputTextArea);

                wfsErrorsLabel.setLabelFor(wfsErrorsTextArea);
                wfsErrorsLabel.setText(bundle.getString("lab_wfs_errors")); // NOI18N

                wfsErrorsTextArea.setEditable(false);
                wfsErrorsTextArea.setColumns(20);
                wfsErrorsTextArea.setRows(5);
                wfsErrorsScrollPane.setViewportView(wfsErrorsTextArea);

                wfsBrowseButton.setText(bundle.getString("but_browse_wfs")); // NOI18N
                wfsBrowseButton.addActionListener(new java.awt.event.ActionListener()
                {
                        public void actionPerformed(java.awt.event.ActionEvent evt)
                        {
                                wfsBrowseButtonActionPerformed(evt);
                        }
                });

                optOrderCheckBox.setText(bundle.getString("opt_wiping_order")); // NOI18N
                optOrderCheckBox.addItemListener(new java.awt.event.ItemListener()
                {
                        public void itemStateChanged(java.awt.event.ItemEvent evt)
                        {
                                optOrderCheckBoxItemStateChanged(evt);
                        }
                });
                optOrderCheckBox.addChangeListener(new javax.swing.event.ChangeListener()
                {
                        public void stateChanged(javax.swing.event.ChangeEvent evt)
                        {
                                optOrderCheckBoxStateChanged(evt);
                        }
                });

                wipingOrderComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PATTERN", "BLOCK" }));
                wipingOrderComboBox.setEnabled(false);

                javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
                mainPanel.setLayout(mainPanelLayout);
                mainPanelLayout.setHorizontalGroup(
                        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(fsProgressLabel)
                                                        .addComponent(stageProgressLabel)
                                                        .addComponent(totalProgressLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(fsProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                                                        .addComponent(stageProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(totalProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(fsListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(addFsBut)
                                                                        .addComponent(delFsBut)
                                                                        .addComponent(fsListLabel))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(wipingOptsLabel)
                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                .addComponent(pathToWfsLabel)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(pathToWfsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(wfsBrowseButton))
                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(optAllZerosCheckBox)
                                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                                .addComponent(optSuperblockCheckBox)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(superblockTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                                .addComponent(optBlocksizeCheckBox)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(blocksizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addComponent(optForceCheckBox)
                                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                                .addComponent(optIterCheckBox)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(iterationsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addComponent(optLastZeroCheckBox)
                                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                                .addComponent(optMethodCheckBox)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                                .addComponent(wipingMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                                                .addComponent(optOrderCheckBox)
                                                                                                .addGap(4, 4, 4)
                                                                                                .addComponent(wipingOrderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addComponent(optNoPartCheckBox)
                                                                                        .addComponent(optNoUnrmCheckBox)
                                                                                        .addComponent(optNoWfsCheckBox)
                                                                                        .addComponent(optIoctlCheckBox)
                                                                                        .addComponent(optNoWipeZeroBlkCheckBox)
                                                                                        .addComponent(optUseDedicatedCheckBox)))))
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addComponent(nowWipingDescLabel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(nowWipingNameLabel)))
                                                .addGap(18, 18, 18)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(aboutButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(exitButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                                                        .addComponent(fontSizeLabel)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(fontSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addComponent(startButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(stopButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(loadConfButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                                                .addComponent(saveButton, javax.swing.GroupLayout.Alignment.TRAILING))))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(wfsOutputLabel)
                                                        .addComponent(wfsOutputScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 704, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(wfsErrorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(wfsErrorsLabel))))
                                .addContainerGap(17, Short.MAX_VALUE))
                );

                mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {aboutButton, exitButton, loadConfButton, saveButton, startButton, stopButton});

                mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addFsBut, delFsBut, fsListScrollPane});

                mainPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {fsProgressBar, stageProgressBar, totalProgressBar});

                mainPanelLayout.setVerticalGroup(
                        mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(fsListLabel)
                                                        .addComponent(wipingOptsLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addComponent(optAllZerosCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(optSuperblockCheckBox)
                                                                        .addComponent(superblockTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(optBlocksizeCheckBox)
                                                                        .addComponent(blocksizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optForceCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(optIterCheckBox)
                                                                        .addComponent(iterationsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optLastZeroCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(optMethodCheckBox)
                                                                        .addComponent(wipingMethodComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addComponent(optNoPartCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optNoUnrmCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optNoWfsCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optNoWipeZeroBlkCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optUseDedicatedCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(optIoctlCheckBox)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(optOrderCheckBox)
                                                                        .addComponent(wipingOrderComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(fsListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addGap(31, 31, 31)
                                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(pathToWfsLabel)
                                                                        .addComponent(pathToWfsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(wfsBrowseButton)))
                                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                                .addComponent(addFsBut)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(delFsBut)))
                                                .addGap(18, 18, 18)
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(nowWipingDescLabel)
                                                        .addComponent(nowWipingNameLabel)))
                                        .addGroup(mainPanelLayout.createSequentialGroup()
                                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(fontSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(fontSizeLabel))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(startButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(stopButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(loadConfButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(saveButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(aboutButton)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(exitButton)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(stageProgressLabel)
                                        .addComponent(stageProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(fsProgressLabel)
                                        .addComponent(fsProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(totalProgressLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(totalProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(wfsOutputLabel)
                                        .addComponent(wfsErrorsLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(wfsErrorsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                                        .addComponent(wfsOutputScrollPane))
                                .addContainerGap())
                );

                fontSizeSpinner.getAccessibleContext().setAccessibleName(bundle.getString("font_size_spinner.accname")); // NOI18N
                fontSizeSpinner.getAccessibleContext().setAccessibleDescription(bundle.getString("font_size_spinner.accdesc")); // NOI18N

                mainScrollPane.setViewportView(mainPanel);

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
                getContentPane().setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainScrollPane)
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(mainScrollPane)
                );

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void exitButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exitButtonActionPerformed
        {//GEN-HEADEREND:event_exitButtonActionPerformed

		if (! askForExit())
		{
			return;
		}
		dispose ();
		Starter.closeProgram (0);
        }//GEN-LAST:event_exitButtonActionPerformed

        private void optSuperblockCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optSuperblockCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optSuperblockCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			superblockTextField.setEnabled (true);
			superblockTextField.requestFocusInWindow ();
		}
		else
		{
			superblockTextField.setEnabled (false);
		}
        }//GEN-LAST:event_optSuperblockCheckBoxItemStateChanged

        private void optSuperblockCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optSuperblockCheckBoxStateChanged
        {//GEN-HEADEREND:event_optSuperblockCheckBoxStateChanged
		if ( optSuperblockCheckBox.isSelected () )
		{
			superblockTextField.setEnabled (true);
			superblockTextField.requestFocusInWindow ();
		}
		else
		{
			superblockTextField.setEnabled (false);
		}
        }//GEN-LAST:event_optSuperblockCheckBoxStateChanged

        private void optBlocksizeCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optBlocksizeCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optBlocksizeCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			blocksizeTextField.setEnabled (true);
			blocksizeTextField.requestFocusInWindow ();
		}
		else
		{
			blocksizeTextField.setEnabled (false);
		}
        }//GEN-LAST:event_optBlocksizeCheckBoxItemStateChanged

        private void optBlocksizeCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optBlocksizeCheckBoxStateChanged
        {//GEN-HEADEREND:event_optBlocksizeCheckBoxStateChanged
		if ( optBlocksizeCheckBox.isSelected () )
		{
			blocksizeTextField.setEnabled (true);
			blocksizeTextField.requestFocusInWindow ();
		}
		else
		{
			blocksizeTextField.setEnabled (false);
		}
        }//GEN-LAST:event_optBlocksizeCheckBoxStateChanged

        private void optIterCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optIterCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optIterCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			iterationsTextField.setEnabled (true);
			iterationsTextField.requestFocusInWindow ();
		}
		else
		{
			iterationsTextField.setEnabled (false);
		}
        }//GEN-LAST:event_optIterCheckBoxItemStateChanged

        private void optIterCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optIterCheckBoxStateChanged
        {//GEN-HEADEREND:event_optIterCheckBoxStateChanged
		if ( optIterCheckBox.isSelected () )
		{
			iterationsTextField.setEnabled (true);
			iterationsTextField.requestFocusInWindow ();
		}
		else
		{
			iterationsTextField.setEnabled (false);
		}
        }//GEN-LAST:event_optIterCheckBoxStateChanged

        private void optIoctlCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optIoctlCheckBoxStateChanged
        {//GEN-HEADEREND:event_optIoctlCheckBoxStateChanged
		if ( optIoctlCheckBox.isSelected () )
		{
			optIoctlCheckBox.setForeground (Color.RED);
		}
		else
		{
			optIoctlCheckBox.setForeground (GREEN_LABEL_COLOR);
		}
        }//GEN-LAST:event_optIoctlCheckBoxStateChanged

        private void optForceCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optForceCheckBoxStateChanged
        {//GEN-HEADEREND:event_optForceCheckBoxStateChanged
		if ( optForceCheckBox.isSelected () )
		{
			optForceCheckBox.setForeground (Color.RED);
		}
		else
		{
			optForceCheckBox.setForeground (GREEN_LABEL_COLOR);
		}
        }//GEN-LAST:event_optForceCheckBoxStateChanged

        private void addFsButActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addFsButActionPerformed
        {//GEN-HEADEREND:event_addFsButActionPerformed
		if ( fsChooser == null )
		{
			fsChooser = new JFileChooser ();
			fsChooser.setAcceptAllFileFilterUsed (true);
			fsChooser.setMultiSelectionEnabled (true);
			fsChooser.setDialogType (JFileChooser.OPEN_DIALOG);
		}

		if ( fsChooser.showOpenDialog (this) == JFileChooser.APPROVE_OPTION )
		{
			File[] selected = fsChooser.getSelectedFiles ();
			if ( selected != null )
			{
				ListModel model = fsList.getModel ();
				Vector<String> values = new Vector<String> (selected.length*2);
				if ( model != null )
				{
					for ( int i=0; i < model.getSize (); i++ )
					{
						values.add (model.getElementAt (i).toString ());
					}
				}
				for ( int i=0; i < selected.length; i++ )
				{
					values.add (selected[i].getAbsolutePath ());
				}
				fsList.setListData (values);
			}
		}
        }//GEN-LAST:event_addFsButActionPerformed

        private void delFsButActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_delFsButActionPerformed
        {//GEN-HEADEREND:event_delFsButActionPerformed
		int[] selected = fsList.getSelectedIndices ();
		if ( selected != null )
		{
			ListModel model = fsList.getModel ();
			Vector<String> values = new Vector<String> (selected.length*2);
			if ( model != null )
			{
				boolean wasSelected;
				for ( int i=0; i < model.getSize (); i++ )
				{
					wasSelected = false;
					for ( int j=0; j < selected.length; j++ )
					{
						if ( selected[j] == i )
						{
							wasSelected = true;
							break;
						}
					}
					// only add if wasn't selected for removing
					if (! wasSelected)
					{
						values.add (model.getElementAt (i).toString ());
					}
				}
			}
			fsList.setListData (values);
		}
        }//GEN-LAST:event_delFsButActionPerformed

        private void startButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startButtonActionPerformed
        {//GEN-HEADEREND:event_startButtonActionPerformed
		if ( optNoPartCheckBox.isSelected ()
			&& optNoUnrmCheckBox.isSelected ()
			&& optNoWfsCheckBox.isSelected () )
		{
			UiUtils.showErrorMessage(this, NOTHING_TO_WIPE);
			return;
		}
		ListModel model = fsList.getModel ();
		if ( model != null )
		{
			if ( model.getSize () == 0 )
			{
				UiUtils.showErrorMessage(this, NO_FILESYSTEMS);
				return;
			}
		}
		else
		{
			return;
		}
		Runtime run = Runtime.getRuntime ();
		if ( run == null )
		{
			UiUtils.showErrorMessage(this, CANNOT_EXECUTE);
			return;
		}

		// read the checkboxes and add parameters
		List<String> params = new ArrayList<> (20); // 15 is the minimum.
		String pathToProg = "wipefreespace";		// NOI18N
		String pathText = pathToWfsTextField.getText ();
		if ( pathText != null && ! pathText.isEmpty() )
		{
			pathToProg = pathText;
		}
		params.add (pathToProg);
		if ( optBlocksizeCheckBox.isSelected () )
		{
			try
			{
				// check if blocksize is an integer:
				Integer.parseInt (blocksizeTextField.getText ());
				// if we got here, it is ok
				params.add ("-B");	// NOI18N
				params.add (blocksizeTextField.getText ());
			}
			catch (Exception ex)
			{
				Utils.handleException (ex,
					"startButActionPerformed: " + blocksizeTextField.getText ());		// NOI18N
			}
		}
		if ( optForceCheckBox.isSelected () )
		{
			params.add ("--force");		// NOI18N
		}
		if ( optIoctlCheckBox.isSelected () )
		{
			params.add ("--use-ioctl");	// NOI18N
		}
		if ( optIterCheckBox.isSelected () )
		{
			try
			{
				// check if the number of iterations is an integer:
				Integer.parseInt (iterationsTextField.getText ());
				// if we got here, it is ok
				params.add ("-n");	// NOI18N
				params.add (iterationsTextField.getText ());
			}
			catch (Exception ex)
			{
				Utils.handleException (ex,
					"startButActionPerformed: " + iterationsTextField.getText ());		// NOI18N
			}
		}
		if ( optLastZeroCheckBox.isSelected () )
		{
			params.add ("--last-zero");	// NOI18N
		}
		if ( optNoPartCheckBox.isSelected () )
		{
			params.add ("--nopart");	// NOI18N
		}
		if ( optNoUnrmCheckBox.isSelected () )
		{
			params.add ("--nounrm");	// NOI18N
		}
		if ( optNoWfsCheckBox.isSelected () )
		{
			params.add ("--nowfs");		// NOI18N
		}
		if ( optAllZerosCheckBox.isSelected () )
		{
			params.add ("--all-zeros");	// NOI18N
		}
		if ( optSuperblockCheckBox.isSelected () )
		{
			try
			{
				// check if the superblock offset is an integer:
				Integer.parseInt (superblockTextField.getText ());
				// if we got here, it is ok
				params.add ("-b");	// NOI18N
				params.add (superblockTextField.getText ());
			}
			catch (Exception ex)
			{
				Utils.handleException (ex,
					"startButActionPerformed: " + superblockTextField.getText ());		// NOI18N
			}
		}
		if ( optMethodCheckBox.isSelected () )
		{
			params.add ("--method");	// NOI18N
			params.add (wipingMethodComboBox.getSelectedItem ().toString ());
		}
		if ( optNoWipeZeroBlkCheckBox.isSelected () )
		{
			params.add ("--no-wipe-zero-blocks");	// NOI18N
		}
		if ( optUseDedicatedCheckBox.isSelected () )
		{
			params.add ("--use-dedicated");	// NOI18N
		}
		if ( optOrderCheckBox.isSelected() )
		{
			params.add ("--order");	// NOI18N
			params.add (wipingOrderComboBox.getSelectedItem ().toString ());
		}
		params.add ("--verbose");	// always use verbose output	// NOI18N
		for ( int i = 0; i < model.getSize (); i++ )
		{
			params.add (model.getElementAt (i).toString ());
		}
		// start the Process
		try
		{
			wfsOutputTextArea.setText (params.toString ()
				.replaceAll (Utils.COMMA, Utils.EMPTY_STR) + "\n");	// NOI18N
			wfsErrorsTextArea.setText (Utils.EMPTY_STR);
			stageProgressBar.setValue (0);
			fsProgressBar.setValue (0);
			totalProgressBar.setValue (0);
			int nStages = 3;
			if ( optNoPartCheckBox.isSelected () )
			{
				nStages--;
			}
			if ( optNoUnrmCheckBox.isSelected () )
			{
				nStages--;
			}
			if ( optNoWfsCheckBox.isSelected () )
			{
				nStages--;
			}
			wfs = run.exec (params.toArray (/* just a type marker: */ new String[] {""}));	// NOI18N
			// start Threads that read the program's stdout and stderr and put
			// it in the text areas and update the progress bars accordingly.
			stdoutUpdater = new ProgressUpdater (wfs.getInputStream (),
				wfsOutputTextArea, stageProgressBar,
				fsProgressBar, totalProgressBar,
				nowWipingNameLabel, model.getSize (),  nStages);
			stdoutUpdater.startProcessing ();
			stderrUpdater = new ProgressUpdater (wfs.getErrorStream (),
				wfsErrorsTextArea, null, null, null,
				nowWipingNameLabel, model.getSize (),  nStages);
			stderrUpdater.startProcessing ();

			// a thread that waits for the program to finish and sets the GUI back:
			wfsMonitor = new WfsMonitor();
			wfsMonitor.execute ();
		}
		catch (FileNotFoundException ex)
		{
			Utils.handleException (ex, "exec");	// NOI18N
			UiUtils.showErrorMessage(this, WFS_NOT_FOUND_MSG);
			return;
		}
		catch (Exception ex)
		{
			Utils.handleException (ex, "exec");	// NOI18N
			UiUtils.showErrorMessage(this,
				CANNOT_EXECUTE + Utils.COLON + Utils.SPACE + ex);
			return;
		}
		nowWipingNameLabel.setText ("-");	// NOI18N
		// disable the GUI elements:
		stopButton.setEnabled (true);
		startButton.setEnabled (false);
		exitButton.setEnabled (false);
		addFsBut.setEnabled (false);
		optBlocksizeCheckBox.setEnabled (false);
		blocksizeTextField.setEnabled (false);
		delFsBut.setEnabled (false);
		optForceCheckBox.setEnabled (false);
		optIoctlCheckBox.setEnabled (false);
		optIterCheckBox.setEnabled (false);
		iterationsTextField.setEnabled (false);
		optLastZeroCheckBox.setEnabled (false);
		loadConfButton.setEnabled (false);
		optNoPartCheckBox.setEnabled (false);
		optNoUnrmCheckBox.setEnabled (false);
		optNoWfsCheckBox.setEnabled (false);
		optAllZerosCheckBox.setEnabled (false);
		saveButton.setEnabled (false);
		optSuperblockCheckBox.setEnabled (false);
		superblockTextField.setEnabled (false);
		optNoWipeZeroBlkCheckBox.setEnabled (false);
		optUseDedicatedCheckBox.setEnabled (false);
		optMethodCheckBox.setEnabled (false);
		wipingMethodComboBox.setEnabled (false);
		pathToWfsTextField.setEnabled (false);
		wfsBrowseButton.setEnabled (false);
		optOrderCheckBox.setEnabled (false);
		wipingOrderComboBox.setEnabled (false);
        }//GEN-LAST:event_startButtonActionPerformed

        private void stopButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopButtonActionPerformed
        {//GEN-HEADEREND:event_stopButtonActionPerformed
		if (! askForExit())
		{
			return;
		}
		// Process is stopped. Reenable the GUI elements:
		stopButton.setEnabled (false);
		startButton.setEnabled (true);
		exitButton.setEnabled (true);
		addFsBut.setEnabled (true);
		optBlocksizeCheckBox.setEnabled (true);
		blocksizeTextField.setEnabled (true);
		delFsBut.setEnabled (true);
		optForceCheckBox.setEnabled (true);
		optIoctlCheckBox.setEnabled (true);
		optIterCheckBox.setEnabled (true);
		iterationsTextField.setEnabled (true);
		optLastZeroCheckBox.setEnabled (true);
		loadConfButton.setEnabled (true);
		optNoPartCheckBox.setEnabled (true);
		optNoUnrmCheckBox.setEnabled (true);
		optNoWfsCheckBox.setEnabled (true);
		optAllZerosCheckBox.setEnabled (true);
		saveButton.setEnabled (true);
		optSuperblockCheckBox.setEnabled (true);
		superblockTextField.setEnabled (true);
		optNoWipeZeroBlkCheckBox.setEnabled (true);
		optUseDedicatedCheckBox.setEnabled (true);
		optOrderCheckBox.setEnabled (true);
		optMethodCheckBox.setEnabled (true);
		if ( optMethodCheckBox.isSelected() )
		{
			wipingMethodComboBox.setEnabled (true);
		}
		pathToWfsTextField.setEnabled (true);
		wfsBrowseButton.setEnabled (true);
		if ( optBlocksizeCheckBox.isSelected() )
		{
			blocksizeTextField.setEnabled (true);
		}
		if ( ! fsList.isSelectionEmpty () )
		{
			delFsBut.setEnabled (true);
		}
		if ( optIterCheckBox.isSelected() )
		{
			iterationsTextField.setEnabled (true);
		}
		if ( optSuperblockCheckBox.isSelected() )
		{
			superblockTextField.setEnabled (true);
		}
		if ( optOrderCheckBox.isSelected() )
		{
			wipingOrderComboBox.setEnabled (true);
		}

		nowWipingNameLabel.setText ("-");	// NOI18N
		stageProgressBar.setValue (100);
		fsProgressBar.setValue (100);
		totalProgressBar.setValue (100);
        }//GEN-LAST:event_stopButtonActionPerformed

        private void loadConfButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loadConfButtonActionPerformed
        {//GEN-HEADEREND:event_loadConfButtonActionPerformed
		if ( cfgFC == null )
		{
			cfgFC = UiUtils.createConfigFileChooser(WFSGUI_CONFIG_FILES);
		}
		cfgFC.setDialogType(JFileChooser.OPEN_DIALOG);

		if ( cfgFC.showOpenDialog (this) == JFileChooser.APPROVE_OPTION )
		{
			File f =cfgFC.getSelectedFile ();
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
				optAllZerosCheckBox.setSelected(cfg.getAllZeros());
				optBlocksizeCheckBox.setSelected(cfg.getBlockSize () > 0);
				if ( cfg.getBlockSize () > 0 )
				{
					blocksizeTextField.setText(String.valueOf (cfg.getBlockSize ()));
				}
				else
				{
					blocksizeTextField.setText("");
				}
				optForceCheckBox.setSelected(cfg.getForce ());
				optIoctlCheckBox.setSelected(cfg.getIoctl ());
				optIterCheckBox.setSelected(cfg.getIterations () > 0);
				if ( cfg.getIterations () > 0 )
				{
					iterationsTextField.setText(String.valueOf (cfg.getIterations ()));
				}
				else
				{
					iterationsTextField.setText("");
				}
				optLastZeroCheckBox.setSelected(cfg.getLastZero ());
				optMethodCheckBox.setSelected(cfg.getIsMethod ());
				optNoPartCheckBox.setSelected(cfg.getNoWipePart ());
				optNoUnrmCheckBox.setSelected(cfg.getNoWipeUndel ());
				optNoWfsCheckBox.setSelected(cfg.getNoWipeFreeSpace ());
				optNoWipeZeroBlkCheckBox.setSelected(cfg.getIsNoWipeZeroBlocks ());
				optSuperblockCheckBox.setSelected(cfg.getSuperOffset () >= 0);
				if ( cfg.getSuperOffset () >= 0 )
				{
					superblockTextField.setText(String.valueOf (cfg.getSuperOffset ()));
				}
				else
				{
					superblockTextField.setText("");
				}
				optUseDedicatedCheckBox.setSelected(cfg.getIsUseDedicated ());
				if (  cfg.getMethodName () != null && ! cfg.getMethodName ().isEmpty() )
				{
					wipingMethodComboBox.setSelectedItem (cfg.getMethodName ());
				}
				else
				{
					wipingMethodComboBox.setSelectedIndex(0);
				}
				optOrderCheckBox.setSelected(cfg.getIsOrder());
				if (  cfg.getWipingOrder () != null && ! cfg.getWipingOrder ().isEmpty() )
				{
					wipingOrderComboBox.setSelectedItem (cfg.getWipingOrder ());
				}
				else
				{
					wipingOrderComboBox.setSelectedIndex(0);
				}
				// filesystem list
				List<String> fslist = cfg.getFSList();
				if ( fslist != null && !fslist.isEmpty() )
				{
					fsList.setListData(new Vector<String>(fslist));
				}
				if (cfg.getFontSizeValue () > 0)
				{
					fontSizeSpinner.setValue ((float)cfg.getFontSizeValue ());
				}
				else
				{
					fontSizeSpinner.setValue (fontSizeSpinner.getValue ());	// refresh the font in the window
				}
				pathToWfsTextField.setText(cfg.getWfsPath ());
				if ( cfg.getIsMax () )
				{
					setExtendedState (Frame.MAXIMIZED_BOTH);
				}
				else
				{
					setExtendedState (getExtendedState () & ~ Frame.MAXIMIZED_BOTH);
					setLocation (Math.max(0, cfg.getX ()),
						Math.max(0, cfg.getY ()));
					setSize (Math.max(0, cfg.getWidth ()),
						Math.max(0, cfg.getHeight ()));
				}
				UiUtils.changeSizeToScreen(this);
			}
			catch (Exception ex)
			{
				Utils.handleException (ex, "readConfig: " + f.getAbsolutePath ());	// NOI18N
			}
		}
        }//GEN-LAST:event_loadConfButtonActionPerformed

        private void saveButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveButtonActionPerformed
        {//GEN-HEADEREND:event_saveButtonActionPerformed
		if ( cfgFC == null )
		{
			cfgFC = UiUtils.createConfigFileChooser(WFSGUI_CONFIG_FILES);
		}
		cfgFC.setDialogType(JFileChooser.SAVE_DIALOG);

		if ( cfgFC.showSaveDialog (this) == JFileChooser.APPROVE_OPTION )
		{
			File newConf = cfgFC.getSelectedFile ();
			if ( newConf == null )
			{
				return;
			}
			if ( newConf.getAbsolutePath ().length () == 0 )
			{
				return;
			}
			try
			{
				if ( newConf.exists () )
				{
					int res = JOptionPane.showConfirmDialog (this,
						newConf.getAbsolutePath () + Utils.SPACE + FILE_EXISTS_OVERWRITE,
						QUESTION_TITLE,
						JOptionPane.YES_NO_CANCEL_OPTION);
					if ( res != JOptionPane.YES_OPTION )
					{
						return;
					}
				}
				if ( newConf.exists () && ! newConf.canWrite () )
				{
					JOptionPane.showMessageDialog (this,
						FILE_NOT_WRITABLE + ": " + newConf.getName (),	// NOI18N
						ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			catch (Exception ex) {}
			try
			{
				ConfigFile cfg = new ConfigFile (newConf);
				cfg.setAllZeros (optAllZerosCheckBox.isSelected ());
				if ( optSuperblockCheckBox.isSelected () )
				{
					try
					{
						cfg.setSuperOffset (Integer.parseInt
							(superblockTextField.getText ()));
					}
					catch (Exception ex)
					{
						cfg.setSuperOffset (-1);
					}
				}
				else
				{
					cfg.setSuperOffset (-1);
				}
				if ( optBlocksizeCheckBox.isSelected () )
				{
					try
					{
						cfg.setBlockSize (Integer.parseInt
							(blocksizeTextField.getText ()));
					}
					catch (Exception ex)
					{
						cfg.setBlockSize (-1);
					}
				}
				else
				{
					cfg.setBlockSize (-1);
				}
				cfg.setForce (optForceCheckBox.isSelected ());
				if ( optIterCheckBox.isSelected () )
				{
					try
					{
						cfg.setIterations (Integer.parseInt
							(iterationsTextField.getText ()));
					}
					catch (Exception ex)
					{
						cfg.setIterations (-1);
					}
				}
				else
				{
					cfg.setIterations (-1);
				}
				cfg.setLastZero (optLastZeroCheckBox.isSelected ());
				cfg.setWipePart (optNoPartCheckBox.isSelected ());
				cfg.setWipeUndel (optNoUnrmCheckBox.isSelected ());
				cfg.setWipeFreeSpace (optNoWfsCheckBox.isSelected ());
				cfg.setIoctl (optIoctlCheckBox.isSelected ());
				cfg.setX (getX ());
				cfg.setY (getY ());
				cfg.setWidth (getWidth ());
				cfg.setHeight (getHeight ());
				cfg.setIsMaximized ((getExtendedState () & Frame.MAXIMIZED_BOTH) != 0);
				ListModel model = fsList.getModel ();
				Vector<String> values;
				if ( model != null )
				{
					values = new Vector<String> (model.getSize ());
					for ( int i=0; i < model.getSize (); i++ )
					{
						values.add (model.getElementAt (i).toString ());
					}
				}
				else
				{
					values = new Vector<String> (1);
				}
				cfg.setFSList (values);
				Object val = fontSizeSpinner.getValue ();
				if ( val != null && val instanceof Number )
				{
					cfg.setFontSizeValue (((Number)val).intValue ());
				}
				cfg.setWfsPath (pathToWfsTextField.getText ());
				cfg.setIsMethodSelected (optMethodCheckBox.isSelected ());
				Object wipingMethod = wipingMethodComboBox.getSelectedItem ();
				if ( wipingMethod != null )
				{
					cfg.setMethodName (wipingMethod.toString ());
				}
				cfg.setIsOrderSelected(optOrderCheckBox.isSelected ());
				Object wipingOrder = wipingOrderComboBox.getSelectedItem ();
				if ( wipingOrder != null )
				{
					cfg.setWipingOrder(wipingOrder.toString ());
				}
				cfg.setUseDedicated(optUseDedicatedCheckBox.isSelected());
				cfg.setNoWipeZeroBlocks(optNoWipeZeroBlkCheckBox.isSelected());
				cfg.write ();
			}
			catch (Exception ex)
			{
				Utils.handleException (ex, "WfsMainWindow.saveButtonActionPerformed");	// NOI18N
			}
		}
        }//GEN-LAST:event_saveButtonActionPerformed

        private void optNoPartCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optNoPartCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optNoPartCheckBoxItemStateChanged
		if ( evt.getStateChange() == ItemEvent.SELECTED
			&& optNoUnrmCheckBox.isSelected()
			&& optNoWfsCheckBox.isSelected() )
		{
			optNoPartCheckBox.setSelected(false);
		}
        }//GEN-LAST:event_optNoPartCheckBoxItemStateChanged

        private void optNoPartCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optNoPartCheckBoxStateChanged
        {//GEN-HEADEREND:event_optNoPartCheckBoxStateChanged
		if ( optNoPartCheckBox.isSelected()
			&& optNoUnrmCheckBox.isSelected()
			&& optNoWfsCheckBox.isSelected() )
		{
			optNoPartCheckBox.setSelected(false);
		}
        }//GEN-LAST:event_optNoPartCheckBoxStateChanged

        private void optNoUnrmCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optNoUnrmCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optNoUnrmCheckBoxItemStateChanged
		if ( evt.getStateChange() == ItemEvent.SELECTED
			&& optNoPartCheckBox.isSelected()
			&& optNoWfsCheckBox.isSelected() )
		{
			optNoUnrmCheckBox.setSelected(false);
		}
        }//GEN-LAST:event_optNoUnrmCheckBoxItemStateChanged

        private void optNoUnrmCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optNoUnrmCheckBoxStateChanged
        {//GEN-HEADEREND:event_optNoUnrmCheckBoxStateChanged
		if ( optNoUnrmCheckBox.isSelected()
			&& optNoPartCheckBox.isSelected()
			&& optNoWfsCheckBox.isSelected() )
		{
			optNoUnrmCheckBox.setSelected(false);
		}
        }//GEN-LAST:event_optNoUnrmCheckBoxStateChanged

        private void optNoWfsCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optNoWfsCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optNoWfsCheckBoxItemStateChanged
		if ( evt.getStateChange() == ItemEvent.SELECTED
			&& optNoPartCheckBox.isSelected()
			&& optNoUnrmCheckBox.isSelected() )
		{
			optNoWfsCheckBox.setSelected(false);
		}
        }//GEN-LAST:event_optNoWfsCheckBoxItemStateChanged

        private void optNoWfsCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optNoWfsCheckBoxStateChanged
        {//GEN-HEADEREND:event_optNoWfsCheckBoxStateChanged
		if ( optNoWfsCheckBox.isSelected()
			&& optNoPartCheckBox.isSelected()
			&& optNoUnrmCheckBox.isSelected() )
		{
			optNoWfsCheckBox.setSelected(false);
		}
        }//GEN-LAST:event_optNoWfsCheckBoxStateChanged

        private void fontSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_fontSizeSpinnerStateChanged
        {//GEN-HEADEREND:event_fontSizeSpinnerStateChanged
		UiUtils.setFontSize (this, UiUtils.getFontSize (fontSizeSpinner));
        }//GEN-LAST:event_fontSizeSpinnerStateChanged

        private void optMethodCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optMethodCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optMethodCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			wipingMethodComboBox.setEnabled (true);
		}
		else if ( evt.getStateChange () == ItemEvent.DESELECTED )
		{
			wipingMethodComboBox.setEnabled (false);
		}
        }//GEN-LAST:event_optMethodCheckBoxItemStateChanged

        private void optMethodCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optMethodCheckBoxStateChanged
        {//GEN-HEADEREND:event_optMethodCheckBoxStateChanged
		wipingMethodComboBox.setEnabled(optMethodCheckBox.isSelected());
        }//GEN-LAST:event_optMethodCheckBoxStateChanged

        private void wfsBrowseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_wfsBrowseButtonActionPerformed
        {//GEN-HEADEREND:event_wfsBrowseButtonActionPerformed
		if ( progFC == null )
		{
			progFC = UiUtils.createWfsProgramFileChooser();
		}

		if ( progFC.showOpenDialog (this) == JFileChooser.APPROVE_OPTION )
		{
			pathToWfsTextField.setText (progFC.getSelectedFile ().getAbsolutePath ());
		}
        }//GEN-LAST:event_wfsBrowseButtonActionPerformed

        private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
        {//GEN-HEADEREND:event_formWindowClosing
		if (! askForExit())
		{
			return;
		}
		dispose ();
		Starter.closeProgram (0);
        }//GEN-LAST:event_formWindowClosing

        private void optIoctlCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optIoctlCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optIoctlCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			optIoctlCheckBox.setForeground (Color.RED);
		}
		else
		{
			optIoctlCheckBox.setForeground (GREEN_LABEL_COLOR);
		}
        }//GEN-LAST:event_optIoctlCheckBoxItemStateChanged

        private void optForceCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optForceCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optForceCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			optForceCheckBox.setForeground (Color.RED);
		}
		else
		{
			optForceCheckBox.setForeground (GREEN_LABEL_COLOR);
		}
        }//GEN-LAST:event_optForceCheckBoxItemStateChanged

        private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_aboutButtonActionPerformed
        {//GEN-HEADEREND:event_aboutButtonActionPerformed
		try
		{
			new AboutBox (this, true, UiUtils.getFontSize (fontSizeSpinner))
				.setVisible (true);
		}
		catch (Throwable ex)
		{
			Utils.handleException (ex, "MainWindow.AboutBox.start");	// NOI18N
			UiUtils.showErrorMessage(this, ex.toString ());
		}
        }//GEN-LAST:event_aboutButtonActionPerformed

        private void optOrderCheckBoxItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_optOrderCheckBoxItemStateChanged
        {//GEN-HEADEREND:event_optOrderCheckBoxItemStateChanged
		if ( evt.getStateChange () == ItemEvent.SELECTED )
		{
			wipingOrderComboBox.setEnabled (true);
		}
		else if ( evt.getStateChange() == ItemEvent.DESELECTED )
		{
			wipingOrderComboBox.setEnabled (false);
		}
        }//GEN-LAST:event_optOrderCheckBoxItemStateChanged

        private void optOrderCheckBoxStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_optOrderCheckBoxStateChanged
        {//GEN-HEADEREND:event_optOrderCheckBoxStateChanged
		wipingOrderComboBox.setEnabled(optOrderCheckBox.isSelected());
        }//GEN-LAST:event_optOrderCheckBoxStateChanged

	/**
	 * Asks the user to force stop wiping.
	 * @return true, if the user confirmed.
	 */
	private boolean askForExit()
	{
		if ( wfs != null )
		{
			// ask the user and kill the process.
			try
			{
				int res = JOptionPane.showConfirmDialog (this,
					STOP_PROC_MSG, QUESTION_TITLE,
					JOptionPane.YES_NO_CANCEL_OPTION);
				if ( res != JOptionPane.YES_OPTION )
				{
					return false;
				}
				// stop the Threads reading stdout and stderr
				if ( stdoutUpdater != null )
				{
					stdoutUpdater.stop ();
				}
				if ( stderrUpdater != null )
				{
					stderrUpdater.stop ();
				}
				// stop the process:
				wfs.destroy ();
				wfs = null;
				if ( wfsMonitor != null )
				{
					wfsMonitor.cancel (true);//.interrupt ();
				}
			}
			catch (Exception ex)
			{
				Utils.handleException (ex,
					"askForExit");	// NOI18N
			}
		}
		return true;
	}

	/**
	 * Real program starting point.
	 * @param args the command line arguments.
	 */
	public static void start(String[] args)
	{
		// parse the command line:
		CommandLineParser.parse (args);

		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try
		{
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels())
			{
				if ("Nimbus".equals(info.getName()))
				{
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex)
		{
			java.util.logging.Logger.getLogger(WfsMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex)
		{
			java.util.logging.Logger.getLogger(WfsMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex)
		{
			java.util.logging.Logger.getLogger(WfsMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex)
		{
			java.util.logging.Logger.getLogger(WfsMainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		try
		{
			//JFrame.setDefaultLookAndFeelDecorated (true);
			UIManager.setLookAndFeel (
				UIManager.getSystemLookAndFeelClassName ());
		}
		catch (Exception ex)
		{
			Utils.handleException (ex, "UIManager.setLookAndFeel");	// NOI18N
		}

		/* Create and display the form */
		SwingUtilities.invokeLater (new Runnable ()
		{
			@Override
			public synchronized void run ()
			{
				new WfsMainWindow ().setVisible (true);
			}

			@Override
			public String toString ()
			{
				return "WfsMainWindow.start.Runnable";	// NOI18N
			}
		});
	}

	private class WfsMonitor extends SwingWorker<Void, Void>
	{
		@Override
		protected Void doInBackground ()
		{
			try
			{
				wfs.waitFor ();
			}
			catch (Exception ex)
			{
				Utils.handleException (ex,
					"WfsMonitor: waitFor()");		// NOI18N
			}
			return null;
		}

		@Override
		protected void done ()
		{
			wfs = null;
			stopButtonActionPerformed(null);
		}
	}

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JButton aboutButton;
        private javax.swing.JButton addFsBut;
        private javax.swing.JTextField blocksizeTextField;
        private javax.swing.JButton delFsBut;
        private javax.swing.JButton exitButton;
        private javax.swing.JLabel fontSizeLabel;
        private javax.swing.JSpinner fontSizeSpinner;
        private javax.swing.JList<String> fsList;
        private javax.swing.JLabel fsListLabel;
        private javax.swing.JScrollPane fsListScrollPane;
        private javax.swing.JProgressBar fsProgressBar;
        private javax.swing.JLabel fsProgressLabel;
        private javax.swing.JTextField iterationsTextField;
        private javax.swing.JButton loadConfButton;
        private javax.swing.JPanel mainPanel;
        private javax.swing.JScrollPane mainScrollPane;
        private javax.swing.JLabel nowWipingDescLabel;
        private javax.swing.JLabel nowWipingNameLabel;
        private javax.swing.JCheckBox optAllZerosCheckBox;
        private javax.swing.JCheckBox optBlocksizeCheckBox;
        private javax.swing.JCheckBox optForceCheckBox;
        private javax.swing.JCheckBox optIoctlCheckBox;
        private javax.swing.JCheckBox optIterCheckBox;
        private javax.swing.JCheckBox optLastZeroCheckBox;
        private javax.swing.JCheckBox optMethodCheckBox;
        private javax.swing.JCheckBox optNoPartCheckBox;
        private javax.swing.JCheckBox optNoUnrmCheckBox;
        private javax.swing.JCheckBox optNoWfsCheckBox;
        private javax.swing.JCheckBox optNoWipeZeroBlkCheckBox;
        private javax.swing.JCheckBox optOrderCheckBox;
        private javax.swing.JCheckBox optSuperblockCheckBox;
        private javax.swing.JCheckBox optUseDedicatedCheckBox;
        private javax.swing.JLabel pathToWfsLabel;
        private javax.swing.JTextField pathToWfsTextField;
        private javax.swing.JButton saveButton;
        private javax.swing.JProgressBar stageProgressBar;
        private javax.swing.JLabel stageProgressLabel;
        private javax.swing.JButton startButton;
        private javax.swing.JButton stopButton;
        private javax.swing.JTextField superblockTextField;
        private javax.swing.JProgressBar totalProgressBar;
        private javax.swing.JLabel totalProgressLabel;
        private javax.swing.JButton wfsBrowseButton;
        private javax.swing.JLabel wfsErrorsLabel;
        private javax.swing.JScrollPane wfsErrorsScrollPane;
        private javax.swing.JTextArea wfsErrorsTextArea;
        private javax.swing.JLabel wfsOutputLabel;
        private javax.swing.JScrollPane wfsOutputScrollPane;
        private javax.swing.JTextArea wfsOutputTextArea;
        private javax.swing.JComboBox<String> wipingMethodComboBox;
        private javax.swing.JLabel wipingOptsLabel;
        private javax.swing.JComboBox<String> wipingOrderComboBox;
        // End of variables declaration//GEN-END:variables
}
