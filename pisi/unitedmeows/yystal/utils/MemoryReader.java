package pisi.unitedmeows.yystal.utils;


import java.awt.image.BufferedImage;
import java.io.*;

public class MemoryReader extends DataInputStream {

	private ByteArrayInputStream inputStream;

	public MemoryReader(byte[] data) {
		super(null);
		inputStream = new ByteArrayInputStream(data);
		in = inputStream;
	}

	public String readString() {
		try {
			return super.readUTF();
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public int read()  {
		try {
			return super.read();
		} catch (Exception ex){
			return -1;
		}
	}



	public BufferedImage readImage() {
		try {
			int length = readInt();
			byte[] bytes = new byte[length];
			read(bytes);
			return ImageUtils.toBufferedImage(bytes);
		} catch (IOException e) {
			return null;
		}
	}

	public byte[] getBytes() {
		byte[] array = new byte[inputStream.available()];
		try {
			inputStream.read(array);

			return array;
		} catch (IOException e) {
			return null;
		}
	}

}
