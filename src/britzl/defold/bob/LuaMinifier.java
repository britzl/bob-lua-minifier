package britzl.defold.bob;

import java.io.FileOutputStream;
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


	private static void writeToFile(File file, String content) throws IOException {
		FileOutputStream fo = new FileOutputStream(file);
		fo.write(content.getBytes());
		fo.close();
	}


	private String getMinifierPath() throws IOException {
		if (minifier_path != null) {
			return minifier_path;
		}
		InputStream in = getClass().getResourceAsStream("/minify.lua");
		File out = File.createTempFile("luamin", ".lua");
		Files.copy(in, out.toPath(), StandardCopyOption.REPLACE_EXISTING);
		minifier_path = out.getAbsolutePath();
		return minifier_path;
	}



	public String obfuscate(String input) {
		try {
			File outputFile = File.createTempFile("luamin", ".min.lua");
			File inputFile = File.createTempFile("luamin", ".lua");

			writeToFile(inputFile, input);

			List<String> options = new ArrayList<String>();
			options.add("lua");
			options.add(getMinifierPath());
			options.add("minify");
			options.add(inputFile.getAbsolutePath());
			options.add(">");
			options.add(outputFile.getAbsolutePath());

			ProcessBuilder pb = new ProcessBuilder(options).redirectErrorStream(true);
			Process p = pb.start();
			int ret = p.waitFor();
			if (ret != 0) {
				inputFile.delete();
				outputFile.delete();
				System.err.println("Obfuscation failed, return code: " + ret);
				return null;
			}

			String output = Files.readString(outputFile.toPath());
			System.out.println("output " + output);
			outputFile.delete();
			inputFile.delete();
			return output;
		}
		catch(Exception e) {
			System.err.println("Obfuscation failed, " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
