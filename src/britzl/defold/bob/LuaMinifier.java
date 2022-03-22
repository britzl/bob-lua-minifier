package britzl.defold.bob;

import com.dynamo.bob.pipeline.ILuaObfuscator;

public class LuaMinifier implements ILuaObfuscator {

	public String obfuscate(String input) {
		return "print('HELLO FROM LUA MINIFIER')\n" + input;
	}
}