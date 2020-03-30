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
public class ParsecClientStatus extends Structure {
	/**
	 * Latency performance metrics.<br>
	 * C type : ParsecMetrics
	 */
	public ParsecMetrics metrics;
	/**  Client is currently experiencing network failure. */
	public byte networkFailure;
	/**  `true` if the client had to fallback to software decoding after being unable to internally initialize a hardware accelerated decoder. */
	public byte decoderFallback;
	/** C type : uint8_t[1] */
	public byte[] __pad = new byte[1];
	public ParsecClientStatus() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("metrics", "networkFailure", "decoderFallback", "__pad");
	}
	/**
	 * @param metrics  Latency performance metrics.<br>
	 * C type : ParsecMetrics<br>
	 * @param networkFailure  Client is currently experiencing network failure.<br>
	 * @param decoderFallback  `true` if the client had to fallback to software decoding after being unable to internally initialize a hardware accelerated decoder.<br>
	 * @param __pad C type : uint8_t[1]
	 */
	public ParsecClientStatus(ParsecMetrics metrics, byte networkFailure, byte decoderFallback, byte __pad[]) {
		super();
		this.metrics = metrics;
		this.networkFailure = networkFailure;
		this.decoderFallback = decoderFallback;
		if ((__pad.length != this.__pad.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.__pad = __pad;
	}
	public ParsecClientStatus(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecClientStatus implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecClientStatus implements Structure.ByValue {
		
	};
}
