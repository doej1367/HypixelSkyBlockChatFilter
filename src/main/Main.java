package main;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.swing.JTextArea;

public class Main {
	private static Main main;

	private MainWindow frame;
	private JTextArea consoleOut;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main = new Main();
					main.frame = new MainWindow(main);
					main.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void analyze(String filePath, JTextArea consoleOut) {
		// TODO Auto-generated method stub
		this.consoleOut = consoleOut;
		consoleOut("File path:" + filePath + "\n");
		try {
			// check if the file exists
			File test = new File(filePath);
			if (!test.exists()) {
				consoleOut("[ Failure ]  File doesn't exist\n");
				return;
			}
			// unzip the file
			// https://stackoverflow.com/questions/1080381/gzipinputstream-reading-line-by-line
			GZIPInputStream inputFile = new GZIPInputStream(new FileInputStream(filePath));
			Reader decoder = new InputStreamReader(inputFile, "UTF-8");
			// read the unzipped file
			BufferedReader br = new BufferedReader(decoder);
			List<PlacedEye> placed = br.lines().filter(a -> a.contains("placed a Summoning Eye!"))
					.map(a -> a.replace(" Brace yourselves!", ""))
					.map(a -> new PlacedEye(a.split(" ")[5],
							Integer.parseInt("" + a.split(" ")[10].split("/")[0].charAt(1))))
					.collect(Collectors.toList());
			Spawning spawning = new Spawning();
			List<Spawning> spawnings = new ArrayList<>();
			for (PlacedEye e : placed) {
				switch (e.getNumber()) {
				case 1:
					spawning = new Spawning();
					spawning.add(e);
					break;
				case 8:
					spawning.add(e);
					spawnings.add(spawning);
					spawning.calcPlacingProbabilities();
					spawning = new Spawning();
					break;
				default:
					spawning.add(e);
					break;
				}
			}
			List<String> names = placed.stream().map(a -> a.getName()).distinct().collect(Collectors.toList());
			List<String> result = new ArrayList<>();
			int sure = 0;
			int maybe = 0;
			for (String n : names) {
				int c = 0;
				int m = 0;
				for (PlacedEye e : placed) {
					if (e.getName().equalsIgnoreCase(n)) {
						double p = e.getProbability();
						if (p >= 1.0) {
							c += 1;
							m += (int) p - 1;
						} else if (p == 0.0)
							;
						else
							m += Math.max(1, (int) p - 1);
					}
				}
				if (c + m > 0) {
					result.add(n + ": " + c + " eyes placed" + ((m > 0) ? ", + (" + m + " unsure)" : ""));
					sure += c;
					maybe += m;
				}
			}
			if (!frame.getChckbx_ResultsOnly().isSelected())
				placed.stream().forEach(
						a -> consoleOut(a.getName() + ": " + a.getNumber() + ", " + a.getProbability() + "\n"));
			consoleOut("\n");
			consoleOut("Result:\n");
			result.stream().forEach(a -> consoleOut(a + "\n"));
			consoleOut("\n");
			consoleOut("Final result:\n");
			consoleOut(sure + " placed for sure (= " + (sure / 8.0) + " dragons)\n" + "unsure about " + maybe
					+ " more (= " + ((sure + maybe) / 8.0) + " dragons)\n");
			consoleOut("In the end there were " + (spawnings.size() * 8) + " eyes placed (= " + spawnings.size()
					+ " dragons)\n");
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			for (StackTraceElement s : e.getStackTrace())
				consoleOut(s + "\n");
		}
		consoleOut("\n");
	}

	private static void consoleOut(String s) {
		main.consoleOut.append(s);
	}
}
