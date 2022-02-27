package pisi.unitedmeows.yystal.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class YDirectory {

	private File file;

	public YDirectory(File _file) {
		file = _file;
	}

	public YDirectory(String _path) {
		this(new File(_path));
	}

	public YDirectory(String parent, String fileName) {
		this(new File(parent, fileName));
	}

	public YDirectory parent() {
		return new YDirectory(file.getParentFile());
	}

	public List<YFile> listFiles() {
		List<YFile> files = new ArrayList<>();
		for (File listFile : file.listFiles(filter -> filter.isFile())) {
			files.add(new YFile(listFile));
		}
		return files;
	}

	public List<Object> list() {
		List<Object> list = new ArrayList<>();
		for (File obj : file.listFiles(filter -> filter.isDirectory())) {
			list.add(new YDirectory(obj));
		}
		return list;
	}

	public List<YDirectory> listDirectories() {
		List<YDirectory> directories = new ArrayList<>();
		for (File listDirectory : file.listFiles(filter -> filter.isDirectory())) {
			directories.add(new YDirectory(listDirectory));
		}
		return directories;
	}

	public File raw() {
		return file;
	}
}
