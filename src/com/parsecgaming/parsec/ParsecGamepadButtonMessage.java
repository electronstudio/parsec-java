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
public class ParsecGamepadButtonMessage extends Structure {
	/**
	 * @see ParsecLibrary.ParsecGamepadButton
	 * < `ParsecGamepadButton` button.<br>
	 * C type : ParsecGamepadButton
	 */
	public int button;
	/** < Unique client-provided index identifying the gamepad. */
	public int id;
	/** < `true` if the button was pressed, `false` if released. */
	public byte pressed;
	/** C type : uint8_t[3] */
	public byte[] __pad = new byte[3];
	public ParsecGamepadButtonMessage() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("button", "id", "pressed", "__pad");
	}
	/**
	 * @param button @see ParsecGamepadButton<br>
	 * < `ParsecGamepadButton` button.<br>
	 * C type : ParsecGamepadButton<br>
	 * @param id < Unique client-provided index identifying the gamepad.<br>
	 * @param pressed < `true` if the button was pressed, `false` if released.<br>
	 * @param __pad C type : uint8_t[3]
	 */
	public ParsecGamepadButtonMessage(int button, int id, byte pressed, byte __pad[]) {
		super();
		this.button = button;
		this.id = id;
		this.pressed = pressed;
		if ((__pad.length != this.__pad.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.__pad = __pad;
	}
	public ParsecGamepadButtonMessage(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecGamepadButtonMessage implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecGamepadButtonMessage implements Structure.ByValue {
		
	};
}