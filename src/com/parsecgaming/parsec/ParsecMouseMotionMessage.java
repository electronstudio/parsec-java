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
public class ParsecMouseMotionMessage extends Structure {
	/** < `true` for relative mode, `false` for absolute mode. See above. */
	public byte relative;
	/** < The absolute horizontal screen coordinate of the cursor  if `relative` is `false`, or the delta (can be negative) if `relative` is `true`. */
	public int x;
	/** < The absolute vertical screen coordinate of the cursor if `relative` is `false`, or the delta (can be negative) if `relative` is `true`. */
	public int y;
	public ParsecMouseMotionMessage() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("relative", "x", "y");
	}
	/**
	 * @param relative < `true` for relative mode, `false` for absolute mode. See above.<br>
	 * @param x < The absolute horizontal screen coordinate of the cursor  if `relative` is `false`, or the delta (can be negative) if `relative` is `true`.<br>
	 * @param y < The absolute vertical screen coordinate of the cursor if `relative` is `false`, or the delta (can be negative) if `relative` is `true`.
	 */
	public ParsecMouseMotionMessage(byte relative, int x, int y) {
		super();
		this.relative = relative;
		this.x = x;
		this.y = y;
	}
	public ParsecMouseMotionMessage(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecMouseMotionMessage implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecMouseMotionMessage implements Structure.ByValue {
		
	};
}
