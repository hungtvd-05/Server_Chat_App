package com.app.server;

//import java.security.KeyPair;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;

//import java.security.KeyPairGenerator;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.SecureRandom;
//import java.security.Signature;
//public class Server {
//
//    public static void main(String[] args) throws Exception {
////        KeyPairGenerator keygen = KeyPairGenerator.getInstance("DSA");
////        keygen.initialize(2048, new SecureRandom()); // Kích thước khóa an toàn
////        KeyPair keys = keygen.generateKeyPair();
////        PublicKey pubkey = keys.getPublic();
////        PrivateKey privkey = keys.getPrivate();
////
////        // Tạo chữ ký số
////        Signature signaIg = Signature.getInstance("SHA256withDSA"); // Dùng SHA-256
////        signaIg.initSign(privkey);
////        String message = "Mùa thu vàng hoa cúc";
////        System.out.println("Send: " + message);
////        signaIg.update(message.getBytes("UTF-8")); // Hỗ trợ ký tự Unicode
////        byte[] signature = signaIg.sign();
////
////        // Xác minh chữ ký
////        Signature verifyalg = Signature.getInstance("SHA256withDSA");
////        verifyalg.initVerify(pubkey);
////        verifyalg.update(message.getBytes("UTF-8"));
////        System.out.println("Receive: " + message);
////        if (verifyalg.verify(signature)) {
////            System.out.println("Signature verified successfully");
////        } else {
////            System.out.println("Signature verification failed");
////        }
//
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
//        kpg.initialize(2048, SecureRandom.getInstanceStrong()); // Nguồn ngẫu nhiên mạnh
//        KeyPair kp = kpg.genKeyPair();
//        PublicKey pubKey = kp.getPublic();
//        PrivateKey priKey = kp.getPrivate();
//        System.out.println("Generate key successfully");
//
//        // Mã hóa tin nhắn
//        String msg = "Mùa thu vàng hoa cúc";
//        Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        c.init(Cipher.ENCRYPT_MODE, pubKey);
//        byte[] encryptOut = c.doFinal(msg.getBytes("UTF-8")); // Hỗ trợ Unicode
//        String strEncrypt = Base64.getEncoder().encodeToString(encryptOut);
//        System.out.println("Sau khi mã hóa: " + strEncrypt);
//
//        // Giải mã tin nhắn
//        c.init(Cipher.DECRYPT_MODE, priKey);
//        byte[] decryptOut = c.doFinal(Base64.getDecoder().decode(strEncrypt));
//        System.out.println("Sau khi giải mã: " + new String(decryptOut, "UTF-8"));
//
//    }
//}
