package main;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 309146006810341944L;
	private JPanel contentPane;
	private String filePath;
	private JTextArea consoleOut;
	private JCheckBox chckbx_ResultsOnly;
	private JTextArea lineFilter_textField;
	private JTextArea chckbxOnlyNumbers;
	private JCheckBox chckbx_Timestamp;
	private JLabel label_writtenLines;

	public JCheckBox getChckbx_ResultsOnly() {
		return chckbx_ResultsOnly;
	}

	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 * 
	 * @param main
	 */
	public MainWindow(Main main) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 779, 529);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new GridLayout(5, 1, 2, 2));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(3, 3));

		JLabel lblLogFilePath = new JLabel("Log file path (drag'n'drop .log.gz-file here):");
		lblLogFilePath.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(lblLogFilePath, BorderLayout.WEST);

		JTextArea filePath_textField = new JTextArea();
		filePath_textField.setText("C:\\Users\\User\\AppData\\Roaming\\.minecraft");
		filePath = filePath_textField.getText();
		filePath_textField.setLineWrap(true);
		// https://stackoverflow.com/questions/9669530/drag-and-drop-file-path-to-java-swing-jtextfield
		filePath_textField.setDropTarget(new DropTarget() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable()
							.getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						filePath_textField.setText(file.getAbsolutePath());
						filePath = file.getAbsolutePath();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		filePath_textField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				filePath = filePath_textField.getText();
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		panel_1.add(filePath_textField, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new BorderLayout(3, 3));

		JLabel lblFilter = new JLabel("Filter for containing / Exclude characters with a regex:");
		lblFilter.setHorizontalAlignment(SwingConstants.LEFT);
		panel_2.add(lblFilter, BorderLayout.WEST);

		lineFilter_textField = new JTextArea();
		lineFilter_textField.setText(" coins as interest!");
		lineFilter_textField.setLineWrap(true);
		panel_2.add(lineFilter_textField, BorderLayout.CENTER);

		chckbxOnlyNumbers = new JTextArea();
		chckbxOnlyNumbers.setText("[^0-9]");
		lineFilter_textField.setLineWrap(true);
		panel_2.add(chckbxOnlyNumbers, BorderLayout.EAST);
		chckbxOnlyNumbers.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel.add(panel_3);

		JButton btnAnalyze = new JButton("Filter");
		btnAnalyze.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				consoleOut.setText("");
				resetWrittenLines();
				main.analyze(getFilePath(), consoleOut);
			}
		});
		panel_3.add(btnAnalyze);

		chckbx_Timestamp = new JCheckBox("Timestamp");
		chckbx_Timestamp.setHorizontalAlignment(SwingConstants.CENTER);
		panel_3.add(chckbx_Timestamp);

		JPanel panel_4 = new JPanel();
		panel.add(panel_4);

		JButton btnEyeanalyzer = new JButton("Sum Eye and Loot Analyzer");
		btnEyeanalyzer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				consoleOut.setText("");
				resetWrittenLines();
				main.analyzeEyes(getFilePath(), consoleOut);
			}
		});
		panel_4.add(btnEyeanalyzer);

		chckbx_ResultsOnly = new JCheckBox("only show the final results");
		panel_4.add(chckbx_ResultsOnly);
		chckbx_ResultsOnly.setSelected(true);
		chckbx_ResultsOnly.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panel_5 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
		flowLayout.setVgap(10);
		panel.add(panel_5);

		JLabel lblLinesWritten = new JLabel("Lines written: ");
		panel_5.add(lblLinesWritten);

		label_writtenLines = new JLabel("0");
		panel_5.add(label_writtenLines);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		consoleOut = new JTextArea();
		scrollPane.setViewportView(consoleOut);
	}

	private int getWrittenLines() {
		//TODO parsing is ugly and slow. replace by integer variable later
		return Integer.parseInt(label_writtenLines.getText());
	}

	private void setWrittenLines(int writtenLines) {
		this.label_writtenLines.setText("" + writtenLines);
		// this.label_writtenLines.paint(label_writtenLines.getGraphics());
	}
	
	public void incWrittenLines() {
		setWrittenLines(getWrittenLines() + 1);
	}
	public void resetWrittenLines() {
		setWrittenLines(0);
	}

	public String getExcludedChars() {
		return chckbxOnlyNumbers.getText();
	}

	public String getLineFilter() {
		return lineFilter_textField.getText();
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean getTimeAndDate() {
		return chckbx_Timestamp.isSelected();
	}

}
