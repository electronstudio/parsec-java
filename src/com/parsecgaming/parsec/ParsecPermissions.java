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
public class ParsecPermissions extends Structure {
	/**  The guest can send gamepad input. */
	public byte gamepad;
	/**  The guest can send keyboard input. */
	public byte keyboard;
	/**  The guest can send mouse button. */
	public byte mouse;
	/** C type : uint8_t[1] */
	public byte[] __pad = new byte[1];
	public ParsecPermissions() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("gamepad", "keyboard", "mouse", "__pad");
	}
	/**
	 * @param gamepad  The guest can send gamepad input.<br>
	 * @param keyboard  The guest can send keyboard input.<br>
	 * @param mouse  The guest can send mouse button.<br>
	 * @param __pad  type : uint8_t[1]
	 */
	public ParsecPermissions(byte gamepad, byte keyboard, byte mouse, byte __pad[]) {
		super();
		this.gamepad = gamepad;
		this.keyboard = keyboard;
		this.mouse = mouse;
		if ((__pad.length != this.__pad.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.__pad = __pad;
	}
	public ParsecPermissions(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecPermissions implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecPermissions implements Structure.ByValue {
		
	};
}
