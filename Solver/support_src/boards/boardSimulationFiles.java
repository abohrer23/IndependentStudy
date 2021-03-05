package boards;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFileChooser;

import cse131.ArgsProcessor;
import cse131.FileChoosers;
import cse131.Scanners;

/**
 * @author Dennis Cosgrove (http://www.cse.wustl.edu/~cosgroved/)
 * 
 * Modified by Lane Bohrer for use in Independent Study Spring 2021
 */
public class boardSimulationFiles {
	public static ArgsProcessor createArgsProcessorFromFile(String[] argsFromMain) {
		URL url = boardSimulationFiles.class.getResource("resources");
		String directoryName = url.getFile();
		File directory = new File(directoryName);
		File file = FileChoosers.chooseFile(argsFromMain, directory);
		if (file != null && file.exists()) {
			try {
				return Scanners.createArgsProcessorFromFile(file);
			} catch (FileNotFoundException fnfe) {
				throw new Error(fnfe);
			}
		} else {
			return new ArgsProcessor(new String[] {});
		}
	}
}