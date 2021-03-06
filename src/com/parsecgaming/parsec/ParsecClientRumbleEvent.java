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
public class ParsecClientRumbleEvent extends Structure {
	/**  Unique client-assigned index identifying the gamepad connected to the client. */
	public int gamepadID;
	/**  8-bit unsigned value for large motor vibration. */
	public byte motorBig;
	/**  8-bit unsigned value for small motor vibration. */
	public byte motorSmall;
	/** C type : uint8_t[2] */
	public byte[] __pad = new byte[2];
	public ParsecClientRumbleEvent() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("gamepadID", "motorBig", "motorSmall", "__pad");
	}
	/**
	 * @param gamepadID  Unique client-assigned index identifying the gamepad connected to the client.<br>
	 * @param motorBig  8-bit unsigned value for large motor vibration.<br>
	 * @param motorSmall 8-bit unsigned value for small motor vibration.<br>
	 * @param __pad C type : uint8_t[2]
	 */
	public ParsecClientRumbleEvent(int gamepadID, byte motorBig, byte motorSmall, byte __pad[]) {
		super();
		this.gamepadID = gamepadID;
		this.motorBig = motorBig;
		this.motorSmall = motorSmall;
		if ((__pad.length != this.__pad.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.__pad = __pad;
	}
	public ParsecClientRumbleEvent(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecClientRumbleEvent implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecClientRumbleEvent implements Structure.ByValue {
		
	};
}
