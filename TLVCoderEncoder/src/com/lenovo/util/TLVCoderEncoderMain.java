package com.lenovo.util;

import java.util.List;

import com.lenovo.bean.Notification;

public class TLVCoderEncoderMain {

	public static void main(String[] args) {
//		int tagValue = 0x51;
//		byte[] b = TLVEncoder.encoderTag(tagValue);
//		for (byte bb : b) {
//			System.out.print(bb + " ");
//		}
//		System.out.println("\n");
//		
//		String bStr = TLVDecoder.decoderTag(b);
//		System.out.println(bStr);
//		System.out.println("\n");
//		
//		byte[] length = TLVEncoder.encoderLength(11);
//		for (byte l : length) {
//			System.out.print(toHex(l) + " ");
//		}
//		System.out.println("\n");
//		int decoderLength = TLVDecoder.decoderLength(length);
//		System.out.println(decoderLength +"长度");
		//-------------------------------------------
//		byte[] b1 = TLVEncoder.parseTag(0x1FFFF, 0, 0);
//		for (byte bb : b1) {
//			System.out.print(bb + " ");
//		}
//		
//		System.out.println("\n");
//		int bint1 = TLVDecoder.decoderTag(b1);
//		System.out.println(bint1);
		
		Notification notification = new Notification();
		notification.setPkgName("军哥军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军军军人军军");
		notification.setUserName("君君");
		notification.setAlias("junge");
		
		try {
//			byte[] sourceByte = new byte[300];
//			sourceByte[0] = 0x00;
//			sourceByte[0] += 0x81;
//			sourceByte[1] += 0xC1;
////			for (int i = 2; i < sourceByte.length; i++) {
////				sourceByte[i] = 0x01;
////			}
//			int nLen = TLVDecoder.decoderLength(sourceByte);
//			System.out.print(nLen+" ");
			
//			
			byte[] tlvByteArray =  TLVManager.convertModel2ByteArray(notification, true,TLVManager.CONNECT_STREAM);
			System.out.println(tlvByteArray.length+"字节");
			for (byte l : tlvByteArray) {
				System.out.print("0x"+toHex(l)+" ");
			}
			
			List<TLVObject> tlvObjects = TLVManager.convertTagValue(tlvByteArray);
			for (TLVObject tlvObject : tlvObjects) {
				System.out.println("\n");
				System.out.println("0x"+toHex(tlvObject.getTag()[0])+" tag\n");
				System.out.println(TLVDecoder.encodeHexStr(tlvObject.getTagValue(), true));
				if (tlvObject.getTagLength() > 256) {
					String sssString = new String(TLVManager.subBytes(tlvObject.getTagValue(), 4, tlvObject.getTagValue().length -4),"utf-8");
					System.out.println(sssString);
				}else if (tlvObject.getTagLength() > 127 && tlvObject.getTagLength() < 256) {
					String sssString = new String(TLVManager.subBytes(tlvObject.getTagValue(), 3, tlvObject.getTagValue().length -3),"utf-8");
					System.out.println(sssString);
				}else {
					String sssString = new String(TLVManager.subBytes(tlvObject.getTagValue(), 2, tlvObject.getTagValue().length -2),"utf-8");
					System.out.println(sssString);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 把byte 转化为两位十六进制数  
    public static String toHex(byte b) {  
        String result = Integer.toHexString(b & 0xFF);  
        if (result.length() == 1) {  
            result = '0' + result;  
        }  
        return result;  
    }  
}
