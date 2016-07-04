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
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import java.util.Random;

/**
 * Useful tools related to @see User
 */
public class UserTools {
	
	private static final String SALT = "zu4cheuthee8ieki2uquaN9Qui2lee";
	
	private static final String ALGO = "MD5";
	
	// Charset used for communication
	private static final Charset CHARSET = Charset.forName("UTF-8");
	
	/**
	 * Check if login password is valid for this user.
	 * 
	 * @param user
	 * @param password
	 * @return valid or not
	 */
	public static boolean login(User user, String password) {
		return plainPwdToHash(user.salt, password).equals(user.password);
	}
	
	/**
	 * Set new password on User object.
	 * Resets the salt as well.
	 * 
	 * @param user
	 * @param plainPassword 
	 */
	public static void newPassword(User user, String plainPassword) {
		user.salt = generateNewCode();
		user.password = plainPwdToHash(user.salt, plainPassword);
	}
	
	private static String plainPwdToHash(String salt, String plainPassword) {
		MessageDigest m;
		try {
			m = MessageDigest.getInstance(ALGO);
		} catch (NoSuchAlgorithmException e) {
			throw new HoursException(String.format("Unknown MessageDigest algorithm: %s", ALGO), e);
		}
		String s = String.format("%s%s%s", SALT, salt, plainPassword);
		m.update(s.getBytes(CHARSET));
		byte[] digestB = m.digest();
		return new BigInteger(1, digestB).toString(16);
	}
	
	/**
	 * Send code mail for user so it can activate its account.
	 * 
	 * @param user
	 * @param hostname hostname including protocol, for example "http://localhost"
	 * @param code unique confirmation code
	 */
	public static void sendCodeMail(User user, String hostname, String code) {
		final String subject = "Reset user request";
		final String message = "Dear user,\n\n"
				+ "User %s has requested to reset your password.\n\n"
				+ "Follow the next link:\n\n"
				+ "%s/hours/confirm.action?code=%s\n\n";
		Settings settings = Settings.getInstance();
		if (user.email == null || user.email.length() == 0) {
			return;
		}
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "localhost");
		
		Session session = Session.getDefaultInstance(properties);
		
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("admin@welmers.net"));
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(user.email));
			msg.setSubject(subject);
			msg.setText(String.format(
				message,
				user.username,
				hostname,
				code
			));
			Transport.send(msg);
		} catch (MessagingException ex) {
			throw new HoursException("Error sending mail", ex);
		}
	
	}
	public static User userWithoutPrivateData(User user) {
		User newUser = new User();
		for (Field field : User.class.getFields()) {
			String name = field.getName();
			if (name.equals("password") || name.equals("salt"))
				continue;
			try {
				field.set(newUser, field.get(user));
			} catch (IllegalArgumentException ex) {
				throw new HoursException("Illegal Argument", ex);
			} catch (IllegalAccessException ex) {
				throw new HoursException("Illegal Access", ex);
			}
		}
		return newUser;
	}
	
	/**
	 * generate random 32 character hex code string
	 * 
	 * @return 32 char String with random code 
	 */
	public static String generateNewCode() {
	
		// TODO can this code simpler? Is it random enough?
		
		// generate two random 64bit longs, 
		Random rnd = new Random();
		long val = rnd.nextLong();
		long val2 = rnd.nextLong();
		
		// turn the two random longs into a 128 bit / 16 byte array
		byte[] codeB = new byte[16];
		for (int i = 7; i >= 0; --i) {
			codeB[i] = (byte)(val & 0xff);
			val >>= 8;
		}
		for (int i = 15; i >= 8; --i) {
			codeB[i] = (byte)(val2 & 0xff);
			val2 >>= 8;
		}
		// return 16 byte array as 32 char hex notation
		return new BigInteger(1, codeB).toString(16);
		
	}
}
