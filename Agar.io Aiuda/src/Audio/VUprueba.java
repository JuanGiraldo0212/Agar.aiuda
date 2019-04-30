package Audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class VUprueba {

	public byte[] aux (String filename) throws IOException {
		File file = new File(filename);
		byte[] fileContent = Files.readAllBytes(file.toPath());
		return fileContent;
	}
	
}
