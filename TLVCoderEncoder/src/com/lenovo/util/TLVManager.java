package com.lenovo.util;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuzhijun tlv�Ĺ�����
 * */
public class TLVManager {
	/**
	 * �ǿͻ��˻��Ƿ����
	 * */
	private final static int IS_CLIENT = 0x51;
	private final static int IS_SERVER = 0x50;
	/**
	 * ������������
	 * */
	private final static int STRING_TYPE = 0x0D;
	private final static int INTEGER_TYPE = 0x06;
	private final static int BOOLEN_TYPE = 0x01;
	/**
	 * �����ĸ�����
	 * */
	public final static int CONNECT_STREAM = 0x10;
	public final static int REGISTER_STREAM = 0x12;
	public final static int LOGIN_STREAM = 0x13;
	public final static int SET_IOS_MESSAGE_STREAM = 0x14;
	public final static int SET_ALIAS = 0x15;
	public final static int SET_TAG = 0x16;
	public final static int SEND_MESSAGE = 0x17;
	public final static int SEND_NOTIFICATION = 0x19;
	public final static int MESSAGE_CALLBACK = 0x1A;
	
	private static List<TLVObject> tlvObjects = new ArrayList<TLVObject>();
	/**
	 * ������ת��ΪTLV�����byte����
	 * 
	 * @param t
	 *            ��Ҫת���Ķ���
	 * @param isClient
	 *            �ǿͻ��˻��Ƿ����
	 * @param streamCode
	 *            �����ĸ�����
	 * @throws Exception
	 * */
	public static <T> byte[] convertModel2ByteArray(T t, boolean isClient,
			int streamCode) throws Exception {
		byte[] tag = null;
		int tempTagLength = 0;
		List<TLVObject> list = convertModel2TLV(t, streamCode);
		if (isClient) {
			tag = TLVEncoder.encoderTag(IS_CLIENT);
		} else {
			tag = TLVEncoder.encoderTag(IS_SERVER);
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (int i = 0; i < list.size(); i++) {
			byte[] tempTagValue = list.get(i).make();
			tempTagLength += tempTagValue.length;
			bos.write(tempTagValue);
		}
		byte[] tagLength = TLVEncoder.encoderLength(tempTagLength);
		byte[] tagValue = bos.toByteArray();

		byte[] tlvByteArray = new byte[tag.length + tagLength.length
				+ tagValue.length];
		System.arraycopy(tag, 0, tlvByteArray, 0, tag.length);
		System.arraycopy(tagLength, 0, tlvByteArray, tag.length,
				tagLength.length);
		System.arraycopy(tagValue, 0, tlvByteArray, tag.length
				+ tagLength.length, tagValue.length);
		return tlvByteArray;
	}

	/**
	 * ������byte�����е�tagvalue��List<>
	 * */
	public static List<TLVObject> convertTagValue(byte[] tagValue){
		List<TLVObject> tlvObjects = new ArrayList<TLVObject>();
		TLVObject tlvObject = convertByteArray(tagValue);
		
		if (tlvObject.getTagLength() == 0) {
			tlvObjects.add(tlvObject);
			return tlvObjects;
		}
		
		tlvObjects.addAll(convertSubTagValue(tlvObject.getTagValue()));
		return tlvObjects;
	}
	
	/**
	 * ������������TagValue
	 * */
	public static TLVObject convertByteArray(byte[] receiveByte){
		TLVObject tlvObject = new TLVObject();
		//��ȡtag
		byte[] tag = new byte[1];
		tag[0] = receiveByte[0];
		tlvObject.setTag(tag);
		//��ȡ����
		byte[] subReceiveByte = subBytes(receiveByte, 1, receiveByte.length -1);
		int tagLength = TLVDecoder.decoderLength(subReceiveByte);
		tlvObject.setTagLength(tagLength);
		
		//��ȡtagValue
		int len = 2;
		if(0x00 == ((subReceiveByte[0] & 0xF1) ^ 0x81))
		{
			len += 1;
		}else if(0x00 == ((subReceiveByte[0] & 0xF2) ^ 0x82)){
			len += 2;
		}else if(0x00 == ((subReceiveByte[0] & 0xF3) ^ 0x83)){
			len += 3;
		}else if(0x00 == ((subReceiveByte[0] & 0xF4) ^ 0x84)){
			len += 4;
		}
		byte[] tagValue = subBytes(receiveByte, len, receiveByte.length -len); 
		tlvObject.setTagValue(tagValue);
		
		return tlvObject;
	}
	
	private static List<TLVObject> convertSubTagValue(byte[] subTagValue){
		TLVObject tlvObject = new TLVObject();
		byte[] tag = new byte[1];
		tag[0] = subTagValue[0];
		tlvObject.setTag(tag);
		
		byte[] subReceiveByte = subBytes(subTagValue, 1, subTagValue.length -1);
		int tagLength = TLVDecoder.decoderLength(subReceiveByte);
		tlvObject.setTagLength(tagLength);
		
		if (tagLength <= 0) {
			byte[] blankByte = new byte[1];
			blankByte[0] = 0x00;
			tlvObject.setTagValue(blankByte);
			tlvObjects.add(tlvObject);
			return tlvObjects;
		}
		
		int len = 2;
		if(0x00 == ((subReceiveByte[0] & 0xF1) ^ 0x81))
		{
			len += 1;
		}else if(0x00 == ((subReceiveByte[0] & 0xF2) ^ 0x82)){
			len += 2;
		}else if(0x00 == ((subReceiveByte[0] & 0xF3) ^ 0x83)){
			len += 3;
		}else if(0x00 == ((subReceiveByte[0] & 0xF4) ^ 0x84)){
			len += 4;
		}
		byte[] tagValue = subBytes(subTagValue, len, tagLength);
		tlvObject.setTagValue(tagValue);
		tlvObjects.add(tlvObject);
		
		int subTagValueLength = subTagValue.length;
		if (tlvObject.make().length < subTagValueLength) {
			byte[] subBytes = subBytes(subTagValue, tagLength + len, subTagValueLength - tagLength -len);
			convertSubTagValue(subBytes);
		}
		
		return tlvObjects;
	}

	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		for (int i = begin; i < begin + count; i++) {
			bs[i - begin] = src[i];
		}
		return bs;
	}

	/**
	 * ������ƴ�ӳɷ���TLV��ʽ��List�б�
	 * 
	 * @param t
	 *            ��Ҫת���Ķ���
	 * @param streamCode
	 *            �����ĸ�����
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * */
	private static <T> List<TLVObject> convertModel2TLV(T t, int streamCode)
			throws IllegalArgumentException, IllegalAccessException {
		String str = null;
		List<TLVObject> list = new ArrayList<TLVObject>();
		Class<? extends Object> clazz = t.getClass();
		Field[] declaredFields = clazz.getDeclaredFields();

		for (Field field : declaredFields) {
			field.setAccessible(true);
			str = (String) field.get(t);
			byte[] strByteArray = TLVEncoder.str2ByteArray(str);
			int tagLength = strByteArray.length;
			byte[] tag = null;
			if (field.getType().isAssignableFrom(String.class)) {
				tag = TLVEncoder.encoderTag(STRING_TYPE);
			} else if (field.getType().isAssignableFrom(Number.class)) {
				tag = TLVEncoder.encoderTag(INTEGER_TYPE);
			} else if (field.getType().isAssignableFrom(Boolean.class)) {
				tag = TLVEncoder.encoderTag(BOOLEN_TYPE);
			}
			TLVObject tlvObject = new TLVObject(tag, tagLength, strByteArray);
			byte[] tagValue = tlvObject.make();
			TLVObject mergeTlvObject = new TLVObject(
					TLVEncoder.encoderTag(streamCode), tagValue.length,
					tagValue);
			list.add(mergeTlvObject);
		}
		return list;
	}
}
