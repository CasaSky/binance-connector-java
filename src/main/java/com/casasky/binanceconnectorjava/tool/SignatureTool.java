package com.casasky.binanceconnectorjava.tool;

import com.casasky.binanceconnectorjava.exception.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class SignatureTool {

	private SignatureTool() {
	}

	private static String bytesToHex(byte[] hash) {
		var hexString = new StringBuilder(2 * hash.length);
		for (byte h : hash) {
			var hex = Integer.toHexString(0xff & h);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String sign(String data, String key) {
		var secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
		try {
			var mac = Mac.getInstance(secretKeySpec.getAlgorithm());
			mac.init(secretKeySpec);
			return bytesToHex(mac.doFinal(data.getBytes()));
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new SignatureException();
		}
	}

} 