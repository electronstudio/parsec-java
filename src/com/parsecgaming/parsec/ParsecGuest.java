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
public class ParsecGuest extends Structure {
	/** < Guest ID passed to various host functions. */
	public int id;
	/** < The guest is also the owner of the host computer. */
	public byte owner;
	/**
	 * < UTF-8 guest name string.<br>
	 * C type : char[255]
	 */
	public byte[] name = new byte[255];
	/**
	 * < Unique connection ID valid while `state` is `GUEST_WAITING`, otherwise filled with zeroes.<br>
	 * C type : char[54]
	 */
	public byte[] attemptID = new byte[54];
	/**
	 * < `ParsecGuestState` describing connection state.<br>
	 * C type : ParsecGuestState
	 */
	public int state;
	/**
	 * < Structure describing allowed input.<br>
	 * C type : ParsecPermissions
	 */
	public ParsecPermissions perms;
	public ParsecGuest() {
		super();
	}
	protected List<String> getFieldOrder() {
		return Arrays.asList("id", "owner", "name", "attemptID", "state", "perms");
	}
	/**
	 * @param id < Guest ID passed to various host functions.<br>
	 * @param owner < The guest is also the owner of the host computer.<br>
	 * @param name < UTF-8 guest name string.<br>
	 * C type : char[255]<br>
	 * @param attemptID < Unique connection ID valid while `state` is `GUEST_WAITING`, otherwise filled with zeroes.<br>
	 * C type : char[54]<br>
	 * @param state @see ParsecGuestState<br>
	 * < `ParsecGuestState` describing connection state.<br>
	 * C type : ParsecGuestState<br>
	 * @param perms < Structure describing allowed input.<br>
	 * C type : ParsecPermissions
	 */
	public ParsecGuest(int id, byte owner, byte name[], byte attemptID[], int state, ParsecPermissions perms) {
		super();
		this.id = id;
		this.owner = owner;
		if ((name.length != this.name.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.name = name;
		if ((attemptID.length != this.attemptID.length)) 
			throw new IllegalArgumentException("Wrong array size !");
		this.attemptID = attemptID;
		this.state = state;
		this.perms = perms;
	}
	public ParsecGuest(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends ParsecGuest implements Structure.ByReference {
		
	};
	public static class ByValue extends ParsecGuest implements Structure.ByValue {
		
	};
}
