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
import java.io.File;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;

public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 309146006810341944L;
	private JPanel contentPane;
	private String filePath;
	private JTextArea consoleOut;
	private JCheckBox chckbx_ResultsOnly;

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
		setBounds(100, 100, 690, 529);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(splitPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new GridLayout(3, 1, 0, 0));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));

		JLabel lblLogFilePath = new JLabel("Log File Path (drag'n'drop .log.gz-file here):");
		lblLogFilePath.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(lblLogFilePath, BorderLayout.WEST);

		JTextArea filePath_textField = new JTextArea();
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

		panel_1.add(filePath_textField, BorderLayout.CENTER);

		JPanel panel_3 = new JPanel();
		panel.add(panel_3);

		JButton btnAnalyze = new JButton("Analyze");
		btnAnalyze.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				main.analyze(getFilePath(), consoleOut);
			}
		});
		panel_3.add(btnAnalyze);
		
		chckbx_ResultsOnly = new JCheckBox("only show the final results");
		chckbx_ResultsOnly.setSelected(true);
		panel.add(chckbx_ResultsOnly);
		chckbx_ResultsOnly.setHorizontalAlignment(SwingConstants.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
		
				consoleOut = new JTextArea();
				scrollPane.setViewportView(consoleOut);
	}

	public String getFilePath() {
		return filePath;
	}

}
