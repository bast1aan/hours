/*
 * Hours
 * Copyright (C) 2016 Bastiaan Welmers, bastiaan@welmers.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package bast1aan.hours;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Useful tools related to @see User
 */
public class UserTools {
	
	private static final String SALT = "zu4cheuthee8ieki2uquaN9Qui2lee";
	
	private static final String ALGO = "MD5";
	
	// Charset used for communication
	private static final Charset CHARSET = Charset.forName("UTF-8");
	
	public static boolean login(User user, String password) {
		
		MessageDigest m;
		try {
			m = MessageDigest.getInstance(ALGO);
		} catch (NoSuchAlgorithmException e) {
			throw new HoursException(String.format("Unknown MessageDigest algorithm: %s", ALGO), e);
		}
		String s = String.format("%s%s%s", SALT, user.salt, password);
		m.update(s.getBytes(CHARSET));
		byte[] digestB = m.digest();
		String digest = new BigInteger(1, digestB).toString(16);
		return digest.equals(user.password);
	}

}
