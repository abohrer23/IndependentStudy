package boards;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

import cse131.ArgsProcessor;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 * Modified by Lane Bohrer for use in Independent Study Spring 2021
 */
public class boardSimulationFiles {
	public static ArgsProcessor createArgsProcessorFromFile(String[] argsFromMain)
			throws FileNotFoundException {
		URL url = boardSimulationFiles.class.getResource("resources");
		String directoryName = url.getFile();
		File directory = new File(directoryName);
		File file = null;
		if (argsFromMain.length > 0) {
			file = new File(argsFromMain[0]);
			if( file.exists()) {
				// pass
			} else {
				file = new File(directory, argsFromMain[0]);
				if (file.exists()) {
					// pass
				} else {
					file = null;
				}
			}
		}
		if (file == null) {
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(directory);
			fc.showOpenDialog(null);
			file = fc.getSelectedFile();
		}
		if (file != null) {
			List<String> list = new LinkedList<>();
			Scanner scanner = new Scanner(file);
			try {
				while (scanner.hasNext()) {
					String s = scanner.next();
					list.add(s);
				}
			} finally {
				scanner.close();
			}
			String[] argsForArgsProcessor = list.toArray(new String[list.size()]);
			return new ArgsProcessor(argsForArgsProcessor);
		} else {
			return new ArgsProcessor(new String[] {});
		}
	}
}