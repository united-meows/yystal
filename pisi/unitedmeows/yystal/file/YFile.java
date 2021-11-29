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
		path = _path;
		file = new File(_path);
	}

	public void create() {
		try {
			file.createNewFile();
		} catch (IOException e) {
			YYStal.pop(new YexIO("Couldn't create a new file PATH: " + path));
		}
	}

	public void writeAll(String input) {
		try {
			FileWriter myWriter = new FileWriter(file);

			myWriter.write(input);
			myWriter.close();
		} catch (IOException ex) {
			YYStal.pop(new YexIO("Error while YFile:writeAll (couldn't write to file)"));
		}
	}

	public void writeAll(List<String> input) {
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < input.size(); i++) {
			stringBuilder.append(input.get(i));
			if (i < input.size() - 1)
				stringBuilder.append(System.lineSeparator());
		}
		writeAll(stringBuilder.toString());
	}

	public void write(String input) {
		try {
			FileWriter myWriter = new FileWriter(file, true);

			myWriter.write(input);
			myWriter.close();
		} catch (IOException ex) {
			YYStal.pop(new YexIO("Error while YFile:write (couldn't write to file)"));
		}
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
