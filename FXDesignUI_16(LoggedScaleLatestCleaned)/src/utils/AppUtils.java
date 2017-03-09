package utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;

import sun.management.VMManagement;

public class AppUtils {

	public static final boolean checkIPv4(final String ip) {
	    boolean isIPv4;
	    try {
	    final InetAddress inet = InetAddress.getByName(ip);
	    isIPv4 = inet.getHostAddress().equals(ip)
	            && inet instanceof Inet4Address;
	    } catch (final UnknownHostException e) {
	    isIPv4 = false;
	    }
	    return isIPv4;
	}

	public static final int getPID() {
		try {
			RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
			Field jvm = runtime.getClass().getDeclaredField("jvm");
			jvm.setAccessible(true);
			VMManagement mgmt = (VMManagement) jvm.get(runtime);
			Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
			pid_method.setAccessible(true);
			return ((Integer) pid_method.invoke(mgmt));
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	public static String getProgramPath() throws UnsupportedEncodingException {
	      URL url = AppUtils.class.getProtectionDomain().getCodeSource().getLocation();
	      String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
	      String parentPath = new File(jarPath).getParentFile().getPath();
	      return parentPath;
	}

	public static byte[] subBytes(final byte[] source, final int srcBegin) {
		return subBytes(source, srcBegin, source.length);
	}

	//srcBegin is inclusive
	//srcEnd is exclusive
	public static byte[] subBytes(final byte[] source, final int srcBegin, final int srcEnd) {
		if(srcEnd > source.length)
			return null;
		byte[] dest = new byte[srcEnd - srcBegin];
		getBytes(source, srcBegin, srcEnd, dest, 0);
		return dest;
	}

	public static void getBytes(final byte[] source, final int srcBegin, final int srcEnd, final byte[] dest, final int dstBegin) {
		System.arraycopy(source, srcBegin, dest, dstBegin, srcEnd-srcBegin);
	}
	
	public static String fixedLengthString(String string, int length) {
	    return String.format("%1$-"+length+ "s", string);
	}
}
