package pisi.unitedmeows.yystal.file;

import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.exception.YEx;
import pisi.unitedmeows.yystal.exception.impl.YexIO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class YFile {

	private String path;
	private File file;

	public YFile(String _path) {
		this(new File(_path));
	}

	public YFile(File _file) {
		file = _file;
		path = file.getAbsolutePath();
	}

	public YFile(String _directory, String _fileName) {
		this(new File(_directory, _fileName));
	}

	public YFile create() {
		try {
			file.createNewFile();
		} catch (IOException e) {
			YYStal.pop(new YexIO("Couldn't create a new file PATH: " + path));
		}
        return this;
	}

	public YFile writeAll(String input) {
		try {
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(input);
			myWriter.close();
		} catch (IOException ex) {
			YYStal.pop(new YexIO("Error while YFile:writeAll (couldn't write to file)"));
		}
        return this;
	}

	public YFile writeAll(List<String> input) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < input.size(); i++) {
			stringBuilder.append(input.get(i));
			stringBuilder.append(System.lineSeparator());
		}
		writeAll(stringBuilder.toString());
        return this;
	}

	public YFile writeAll(String[] input) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			stringBuilder.append(input[i]);
			stringBuilder.append(System.lineSeparator());
		}
		writeAll(stringBuilder.toString());
        return this;
	}

	public YFile write(List<String> input) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < input.size(); i++) {
			stringBuilder.append(input.get(i));
			stringBuilder.append(System.lineSeparator());
		}

		write(stringBuilder.toString());
        return this;
	}

	public YFile write(String[] input) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < input.length; i++) {
			stringBuilder.append(input[i]);
				stringBuilder.append(System.lineSeparator());
		}

		write(stringBuilder.toString());
        return this;
	}

	public YFile write(String input) {
		try {
			FileWriter myWriter = new FileWriter(file, true);

			myWriter.write(input);
			myWriter.close();
		} catch (IOException ex) {
			YYStal.pop(new YexIO("Error while YFile:write (couldn't write to file)"));
		}
        return this;
	}

	public List<String> readAllLines()
	{
		List<String> readContent = new ArrayList<>();
		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String str;
			while ((str = in.readLine()) != null)
			{
				readContent.add(str);
			}
			in.close();
		}
		catch (Exception e)
		{
			YYStal.pop(new YexIO("Error while YFile:readAllText (couldn't read the file)"));
		}
		return readContent;
	}

	public String readAllText()
	{
		StringBuilder stringBuilder = new StringBuilder();

		try
		{
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
			String str;
			while ((str = in.readLine()) != null)
			{
				stringBuilder.append(str).append(System.lineSeparator());
			}
			in.close();
		}
		catch (Exception e)
		{
			YYStal.pop(new YexIO("Error while YFile:readAllText (couldn't read the file)"));
		}
		return stringBuilder.toString();
	}

	public boolean rename(String newName) {
		File newFile = new File(file.getParentFile().getAbsoluteFile(), newName);
		if (file.renameTo(file)) {
			file = newFile;
			return true;
		} else {
			YYStal.pop(new YexIO("Error while YFile:reanme (couldn't rename file to " + newName + ")"));
			return false;
		}
	}

	public File file() {
		return file;
	}

	public boolean delete() {
		return file.delete();
	}
}
