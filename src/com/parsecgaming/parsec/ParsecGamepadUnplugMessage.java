package com.parsecgaming.parsec;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class ParsecGamepadUnplugMessage extends Structure {
	/** < Unique client-provided index identifying the gamepad. */
	public int id;
	public ParsecGamepadUnplugMessage() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("id");
	}
	/** @param id < Unique client-provided index identifying the gamepad. */
	public ParsecGamepadUnplugMessage(int id) {
		super();
		this.id = id;
	}
	public ParsecGamepadUnplugMessage(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecGamepadUnplugMessage implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecGamepadUnplugMessage implements Structure.ByValue {
		
	};
}