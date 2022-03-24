package com.dynamo.bob.pipeline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


import com.dynamo.bob.pipeline.LuaBuilderPluginParams;
import com.dynamo.bob.pipeline.LuaBuilderPlugin;


@LuaBuilderPluginParams(name="LuaMinifier")
public class LuaMinifier extends LuaBuilderPlugin {

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
		// InputStream in = getClass().getResourceAsStream("/minify.lua");
		File out = writeToTempFile(LuaMinifierSource.get());
		minifier_path = out.getAbsolutePath();
		return minifier_path;
	}

	@Override
	public String build(String input) throws Exception {
		try {
			File inputFile = writeToTempFile(input);

			// command line arguments to launch lua-minify
			List<String> options = new ArrayList<String>();
			options.add("lua");
			options.add(getMinifierPath());
			options.add("minify");
			options.add(inputFile.getAbsolutePath());

			// launch the process
			ProcessBuilder pb = new ProcessBuilder(options).redirectErrorStream(true);
			Process p = pb.start();
			int ret = p.waitFor();

			// get all of the output from the process
			InputStream is = p.getInputStream();
			byte[] output_bytes = new byte[is.available()];
			is.read(output_bytes);
			is.close();

			// this is either the obfuscated code or the error output
			String output = new String(output_bytes);

			inputFile.delete();

			if (ret != 0) {
				System.err.println(output);
				throw new Exception("Unable to run lua-minify, return code: " + ret);
			}
			return output;
		}
		catch(Exception e) {
			throw new Exception("Unable to run lua-minify, ", e);
		}
	}
}