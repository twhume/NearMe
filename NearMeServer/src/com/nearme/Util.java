package com.nearme;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * Class for holding generally useful static methods
 * 
 * @author twhume
 *
 */
public class Util {

	/**
	 * Helper method; takes a list of IdentityHashes and returns an array of Strings.
	 * We do this to avoid passing IdentityHashes down to the client and confusing it
	 * with yet another data-type.
	 */
	
	static String[] hashListAsStringArray(List<IdentityHash> l) {
		String[] s = new String[l.size()];
		for (int i=0; i<l.size(); i++) {
			s[i] = l.get(i).getHash();
		}
		return s;
	}

	/**
	 * Just used for logging; takes a list of IdentityHashes and returns a string of them, comma-separated
	 * 
	 * @param perms
	 * @return
	 */
	
	static String hashListAsString(List<IdentityHash> perms) {
		StringBuffer sb = new StringBuffer();
		for (IdentityHash ih: perms) {
			sb.append("'");
			sb.append(ih.getHash());
			sb.append("',");
		}
		if (perms.size()>0) sb.setLength(sb.length()-1);
		return sb.toString();
	}

	/**
	 * Method cribbed from http://stackoverflow.com/questions/309424/in-java-how-do-a-read-convert-an-inputstream-in-to-a-string
	 * TODO: move into a proper utility class
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}

}
