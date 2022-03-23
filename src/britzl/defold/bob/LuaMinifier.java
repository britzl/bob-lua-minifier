package britzl.defold.bob;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


import com.dynamo.bob.pipeline.ILuaObfuscator;

public class LuaMinifier implements ILuaObfuscator {

	private static String minifier_path = null;


	private static File writeToTempFile(String input) throws IOException {
		File tempFile = File.createTempFile("luamin", "");
		Files.write(tempFile.toPath(), input.getBytes());
		return tempFile;
	}

	private static File writeToTempFile(InputStream in) throws IOException {
		File tempFile = File.createTempFile("luamin", "");
		Files.copy(in, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return tempFile;
	}


	private String getMinifierPath() throws IOException {
		if (minifier_path != null) {
			return minifier_path;
		}
		InputStream in = getClass().getResourceAsStream("/minify.lua");
		File out = writeToTempFile(in);
		minifier_path = out.getAbsolutePath();
		return minifier_path;
	}



	public String obfuscate(String input) {
		try {
			File inputFile = writeToTempFile(input);

			List<String> options = new ArrayList<String>();
			options.add("lua");
			options.add(getMinifierPath());
			options.add("minify");
			options.add(inputFile.getAbsolutePath());

			ProcessBuilder pb = new ProcessBuilder(options).redirectErrorStream(true);
			Process p = pb.start();
			int ret = p.waitFor();

			InputStream is = p.getInputStream();
			byte[] output_bytes = new byte[is.available()];
			is.read(output_bytes);
			is.close();
			String output = new String(output_bytes);

			inputFile.delete();

			if (ret != 0) {
				System.err.println("Obfuscation failed, return code: " + ret + " " + output);
				return null;
			}
			return output;
		}
		catch(Exception e) {
			System.err.println("Obfuscation failed, " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
