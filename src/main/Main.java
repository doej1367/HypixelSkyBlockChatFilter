package main;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	private ArrayList<String> folderBlacklist = new ArrayList<>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					main = new Main();
					main.folderBlacklist.add("assets");
					main.folderBlacklist.add("bin");
					main.folderBlacklist.add("crash-reports");
					main.folderBlacklist.add("libraries");
					main.folderBlacklist.add("resourcepacks");
					main.folderBlacklist.add("resources");
					main.folderBlacklist.add("saves");
					main.folderBlacklist.add("screenshots");
					main.folderBlacklist.add("server-resource-packs");
					main.folderBlacklist.add("stats");
					main.folderBlacklist.add("texturepacks");
					main.folderBlacklist.add("texturepacks-mp-cache");
					main.folderBlacklist.add("versions");
					main.frame = new MainWindow(main);
					main.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void analyze(String filePath, JTextArea consoleOut) {
		this.consoleOut = consoleOut;
		// check if the file exists
		File test = new File(filePath);
		if (!test.exists()) {
			consoleOut("[ Failure ]  File or folder doesn't exist\n");
			return;
		}
		if (test.isDirectory())
			listFilesForFolder(test, 0);
		if (test.isFile())
			getLinesFiltered(filePath).stream().forEach(a -> consoleOut(a + "\n"));
	}

	private List<String> getLinesFiltered(String filePath) {
		// unzip the file
		// https://stackoverflow.com/questions/1080381/gzipinputstream-reading-line-by-line
		GZIPInputStream inputFile;
		try {
			inputFile = new GZIPInputStream(new FileInputStream(filePath));
			Reader decoder = new InputStreamReader(inputFile, "UTF-8");
			// read the unzipped file
			BufferedReader br = new BufferedReader(decoder);
			List<String> result = br.lines().filter(a -> a.contains("[CHAT]"))
					.filter(a -> a.contains(main.frame.getLineFilter())).map(a -> getTimeAndDate(filePath, a))
					.map(a -> removeExcludedChars(a.replaceAll(" \\[Client thread/INFO\\]: \\[CHAT\\] ", "")))
					.collect(Collectors.toList());
			br.close();
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getTimeAndDate(String filePath, String a) {
		if (main.frame.getTimeAndDate())
			return new File(filePath).getName().substring(0, 10).replaceAll("-", ".") + " "
					+ a.replaceFirst("\\[", "").replaceFirst("\\]", ";");
		else
			return a;
	}

	private String removeExcludedChars(String input) {
		if (main.frame.getTimeAndDate())
			return input.substring(0, 20) + " " + input.substring(20).replaceAll(main.frame.getExcludedChars(), "");
		else
			return input.split("]", 2)[1].replaceAll(main.frame.getExcludedChars(), "");
	}

	private void listFilesForFolder(final File folder, int mode) {
		if (folder == null)
			return;
		if (!folder.exists()) {
			consoleOut("[ Failure ]  Folder doesn't exist\n");
			return;
		}
		if (folder.isFile()) {
			if (folder.getName().contains(".log.gz")) {
				analyze(folder.getAbsolutePath(), consoleOut);
			}
		} else if (folder.isDirectory()) {
			for (final File fileEntry : folder.listFiles()) {
				if (fileEntry.isDirectory()) {
					if (!folderBlacklist.contains(fileEntry.getName()))
						listFilesForFolder(fileEntry, mode);
				} else {
					if (fileEntry.getName().contains(".log.gz")) {
						if (mode == 0)
							analyze(fileEntry.getAbsolutePath(), consoleOut);
						else if (mode == 1)
							analyzeEyes(fileEntry.getAbsolutePath(), consoleOut);
					}
				}
			}
		}
	}

	public void analyzeEyes(String filePath, JTextArea consoleOut) {
		this.consoleOut = consoleOut;
		try {
			// check if the file exists
			File test = new File(filePath);
			if (!test.exists()) {
				consoleOut("[ Failure ]  File doesn't exist\n");
				return;
			}
			if (test.isDirectory())
				listFilesForFolder(test, 1);
			if (test.isFile()) {
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
				List<PlacedInTotal> result = new ArrayList<>();
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
						result.add(new PlacedInTotal(n, c, m));
						sure += c;
						maybe += m;
					}
				}

				if (result.isEmpty()) {
					br.close();
					return;
				}
				consoleOut("File path:" + filePath + "\n");
				if (!frame.getChckbx_ResultsOnly().isSelected())
					placed.stream().forEach(
							a -> consoleOut(a.getName() + ": " + a.getNumber() + ", " + a.getProbability() + "\n"));
				// consoleOut("\n");
				consoleOut("Result:\n");
				result.stream().sorted().forEach(a -> consoleOut(a + "\n"));
				// consoleOut("\n");
				consoleOut("Final result:\n");
				consoleOut(sure + " placed for sure (= " + (sure / 8.0) + " dragons)\n" + "unsure about " + maybe
						+ " more (= " + ((sure + maybe) / 8.0) + " dragons)\n");
				consoleOut("In the end there were " + (spawnings.size() * 8) + " eyes placed (= " + spawnings.size()
						+ " dragons)\n");
				br.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			for (StackTraceElement s : e.getStackTrace())
				consoleOut(s + "\n");
		}
		consoleOut("\n");
	}

	private static void consoleOut(String s) {
		main.consoleOut.append(s);
		if (s.contains("\n"))
			main.frame.incWrittenLines();
	}
}
