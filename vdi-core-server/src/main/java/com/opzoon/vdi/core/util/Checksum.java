package com.opzoon.vdi.core.util;

import static com.opzoon.vdi.core.util.StringUtils.strcat;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MD5计算工具类.
 */
public abstract class Checksum
{

  private static final Logger logger = LoggerFactory.getLogger(Checksum.class);
  
  private Checksum() {}

  /**
   * 获取byte数组的MD5值.
   * 
   * @param src byte数组.
   * @return MD5值.
   */
  public static String getHex(final byte[] src)
  {
    final byte[] checksum = calculate(src);
    final BigInteger bigInt = new BigInteger(1, checksum);
    final String s = bigInt.toString(16);
    final int sub = 32 - s.length();
    if (sub > 0)
    {
      return strcat(Integer.toString((int) Math.pow(10, sub)).substring(1), s);
    } else
    {
      return s;
    }
  }

  private static byte[] calculate(final byte[] src)
  {
    final MessageDigest digest;
    try
    {
      digest = MessageDigest.getInstance("MD5");
    } catch (final NoSuchAlgorithmException e)
    {
      logger.warn("Shouldn't have been here", e);
      return null;
    }
    digest.update(src, 0, src.length);
    return digest.digest();
  }

}
