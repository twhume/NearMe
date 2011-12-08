package com.nearme;

import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.apache.log4j.Logger;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.UTMRef;

/**
 * Class for holding generally useful static methods
 * 
 * @author twhume
 *
 */
public class Util {

	private static Logger logger = Logger.getLogger(Util.class);
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
	 * 
	 * @param is
	 * @return
	 */
	public static String convertStreamToString(InputStream is) { 
	    return new Scanner(is).useDelimiter("\\A").next();
	}

	/**
	 * Alters the input position, adding x metres horizontally and y metres vertically, 
	 * then converting back and saving.
	 * 
	 * We use the JCoord library from http://www.jstott.me.uk/jcoord/ for this
	 * 
	 * @param in
	 * @param metres
	 * @return
	 */
	
	public static Position smudge(Position in, int x, int y) {
		LatLng n1 = new LatLng(in.getLatitude(), in.getLongitude());
		UTMRef u1 = n1.toUTMRef();
		UTMRef u2 = new UTMRef(u1.getEasting() + x, u1.getNorthing() + y, u1.getLatZone(), u1.getLngZone());
		LatLng n2 = u2.toLatLng();
		return new Position(n2.getLat(), n2.getLng());
	}
	
	public static Position smudgeRandomly(Position in, int smudgeFactor) {
		Random r = new Random();
		int xFactor = (Math.abs(r.nextInt()) % smudgeFactor) * (r.nextBoolean() ? 1 : -1);
		int yFactor = (Math.abs(r.nextInt()) % smudgeFactor) * (r.nextBoolean() ? 1 : -1);
		logger.debug("smudgeRandomly: "+xFactor+","+yFactor);
		return smudge(in, xFactor, yFactor);
	}
}
