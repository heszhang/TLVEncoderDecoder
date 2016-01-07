package com.lenovo.util;

/**
 * @author yuzhijun TLV������
 * */
public class TLVDecoder {

	/**
	 * ���ڽ���ʮ�������ַ��������Сд�ַ�����
	 */
	private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	/**
	 * ���ڽ���ʮ�������ַ�������Ĵ�д�ַ�����
	 */
	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * TLV��Tag�ֽ����ݳ���
	 */
	public static int decoderLength(byte[] lenBytes) {
		int sum = 0;
		int len = 1;
		int index = 0;
		if (0x00 == ((lenBytes[0] & 0xF1) ^ 0x81)
				|| 0x00 == ((lenBytes[0] & 0xF2) ^ 0x82)
				|| 0x00 == ((lenBytes[0] & 0xF3) ^ 0x83)
				|| 0x00 == ((lenBytes[0] & 0xF4) ^ 0x84)) {
			len = lenBytes[0] & 0x0F;
			len += 1;
			index = 1;
		}

		for (int i = index; i < len; i++) {
			int shift = (len - 1 - i) * 8;
			sum += (lenBytes[i] & 0x000000FF) << shift;
		}
		return sum;
	}

	/**
	 * ���ֽ�����ת��Ϊʮ�������ַ���
	 *
	 * @param data
	 *            byte[]
	 * @param toLowerCase
	 *            <code>true</code> ������Сд��ʽ �� <code>false</code> �����ɴ�д��ʽ
	 * @return ʮ������String
	 */
	public static String encodeHexStr(byte[] data, boolean toLowerCase) {
		return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * ���ֽ�����ת��Ϊʮ�������ַ�����
	 *
	 * @param data
	 *            byte[]
	 * @param toLowerCase
	 *            <code>true</code> ������Сд��ʽ �� <code>false</code> �����ɴ�д��ʽ
	 * @return ʮ������char[]
	 */
	public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	}

	/**
	 * ���ֽ�����ת��Ϊʮ�������ַ���
	 *
	 * @param data
	 *            byte[]
	 * @param toDigits
	 *            ���ڿ��������char[]
	 * @return ʮ������String
	 */
	private static String encodeHexStr(byte[] data, char[] toDigits) {
		return new String(encodeHex(data, toDigits));
	}

	/**
	 * ���ֽ�����ת��Ϊʮ�������ַ�����
	 *
	 * @param data
	 *            byte[]
	 * @param toDigits
	 *            ���ڿ��������char[]
	 * @return ʮ������char[]
	 */
	private static char[] encodeHex(byte[] data, char[] toDigits) {
		int l = data.length;
		char[] out = new char[l << 1];
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	// ��byte ת��Ϊ��λʮ��������
	public static String toHex(byte b) {
		String result = Integer.toHexString(b & 0xFF);
		if (result.length() == 1) {
			result = '0' + result;
		}
		return result;
	}
}
