package com.hlt.jzwebsite.utils.java;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Created by lxb on 2020/2/22.
 * 邮箱：2072301410@qq.com
 * TIP：
 *  * Android RSA分段加解密，及私钥生成签名公钥验签 - HelloMoney_的博客 - CSDN博客
 *  * https://blog.csdn.net/HelloMoney_/article/details/52870977
 */
public class RSAUtils2 {
    private static String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding"; // 加密算法
    private static int KEYBIT = 2048;// 密钥位数
    private static int RESERVEBYTES = 11;// 加密block需要预留字节数
    private static int decryptBlock = KEYBIT / 8; // 每段解密block数，256 bytes
    private static int encryptBlock = decryptBlock - RESERVEBYTES; // 每段加密block数245bytes
    //### 私钥（客户端） ###
    public static final String privateKey="MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDbCka5uXFey7oL" +
            "wm3ZgR7hhQ5FUkoIfwxPwhaEwTDhvQn4w26WF4T/Pl+eDj1lj4uPbpYzdJAtJley" +
            "NW6u42L79soTvimkAB0PC0RUYOaNDAkgHKwnDrB8Jdo2ffd8CoGr78JjgxiY4x1s" +
            "+urhRXkaGYy+7hdkmGdMOde8heqcFNSY/5qgoR/nvr4Y3LPaETTgma3Mxge/EBYf" +
            "Pfwd6QStCUaoHULk8Hig10rwvV5Csy4wL2k71JbWEr71LxWN93MR0dx7iBfNt0Bc" +
            "JwvubjH5EP7/EcApDVZm/3r8DAqA5t/UDzat3WrI0ABCfIM14qXg47Ih0iUYWBAq" +
            "2roiQXgnAgMBAAECggEAMWzMMLRLhJxryqOArgeYtEKVycabtfcVDBo7gpElOoqx" +
            "aVSP64Y7GG02ni+QYw5U63SEp++xaL6yIyAzbXsZpsk1J+dM9higdfiQ3olO/Jyu" +
            "onRV1CpYYGsaJhmecnrcb7OWJWY7cqQsy56CyXonrV/+hkarOKO7C1Qx/fQp0DXo" +
            "IQvv7RGphNBZ3O++8Cm2mhMaS1ypmTpkENFBvF8izV9fjhmVriH/snkJH+T7GmV/" +
            "VjwRdJDvFLWVMnsY+VJGArR9hYQ4CgtIS0CIYajEhkvpIrTlNgo03gzD+twHp7nN" +
            "u2+hAJ6UdKg6lcptYJF2oNZY0ujOtrw5cppt43vSoQKBgQD2C/40irmaRaMVcg3a" +
            "X5QdBPsBLHTu0VBgbnkMD/C4c3uw7cEAjtxtAgAP3Ck4noEtJe8LHilLT5bHRGfQ" +
            "/Mjui6aqmFinZZ+olLWyCxMtROJoYmDMk/5ksvI88Yz6FrlWfMcwttDq+ZHq9i4y" +
            "CxZydb0OWC6Zg//7iRvYgl765QKBgQDj5puXXu5DZE8PzgrRcpVZtsB1rAmM4cUE" +
            "675QsV+yAbQjcpo8zS+Sx3meOT4F8BPr7MNimxmtnhXd7h/pS2U7qTuaItxjEoRE" +
            "gJcqWQ1+uvXi4Agh/ryhX9I1zqsPhM/kPRVCRf7ol0fKaK3SHo+xwGAD8pqVoMHi" +
            "YUfFVt7aGwKBgC7OGz59gU8ynhJvTulSs0dN8j3H6jqgAM1HVqmPwrgj58zBfayQ" +
            "gKgP8FkUBZdqt6ISDEYuJ6bW7hTcE2+zFroiQMNFFeBKObQFDgdfifdbmkJHLKje" +
            "Ik+fpusm/C168yjbtbyzmkyIy1RKHoGQkGWXti4rYjn73U9JRs3KBy1tAoGAUVyR" +
            "7Z+Jhmu4Qhc4C4BRy36QBTKPEknD0bbr8djxJhavnfwbDlvegRNP2unqt6n3IsIb" +
            "GwfuSepnOhz6LYqqMHuBeMy7S29f4yjZTtgOFlFe2UlL/f74jtArtuP6dAbhRUV9" +
            "YnSkxZrEmYF3lM0uVbItVRt2jraeoVhDjDMwSkMCgYB2AilsGtMxN7o1nTDkspxX" +
            "qthJRWqB70wbY1DBbxMknYHvzmFRPCyR82GCVxNRBD9Msb2YBDFKP+4cAzR83yc2" +
            "+jTc32CQrxuwwJjneEOmnmWFMFY+iC/y81w2E370/6LBPila3Sftl1OsLEycFL30" +
            "tGRB4pAFErUPLlBDHX+DvQ==";
    // 公钥（服务端）
    public static final String publicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5MHkq7s2VSkCcsOUAdxx" +
            "iOdeQTB5O3SxwApmxd/OVykRbcdCz5yNmffR5oDL7m18oR4nE3HnVvD1XSKkHA2l" +
            "8qWbOp7DOJEXugx/K7sIWquOdU1LF9triiIfSK5oxrbfrWpiCVrnyiAbn957/c6X" +
            "Gd8rdNMLzkglJ5WMYTr2nSt8VPySqsC6+SMWVzd0lZU2hSqFbPVSmTAsQYv9IDLs" +
            "zqZoV43sb9OxIz1lUxVz9DTkbkV12acDb1ymc6lafSvgqdr/NgedE/Sx2S4tavzb" +
            "aEKTxWyI0DrTd9cj5QabqbeFTDHWwZSxh/qrEQ1kVuRfVjb2A5j5PNZm21wGrsJG" +
            "BQIDAQAB";
    /**
     * 获取公钥
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 获取私钥
     *
     * @param key
     *            pkcs#8
     * @return
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String getKeyString(Key key) {
        byte[] keyBytes = key.getEncoded();
        String s = Base64.encodeToString(keyBytes, Base64.DEFAULT);
        return s;
    }


    public static String encode(byte[] data) {
        return encode(publicKey,data);
    }
    /**
     * 加密
     *
     * @param pubKey
     *            公钥
     * @param data
     *            加密报文 getBytes()
     * @return
     */
    public static String encode(String pubKey, byte[] data) {
        // 计算分段加密的block数 (向上取整)
        int nBlock = (data.length / encryptBlock);
        if ((data.length % encryptBlock) != 0) { // 余数非0，block数再加1
            nBlock += 1;
        }
        // 输出buffer, 大小为nBlock个decryptBlock
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * decryptBlock);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            PublicKey publicKey = getPublicKey(pubKey);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            for (int offset = 0; offset < data.length; offset += encryptBlock) {
                // block大小: encryptBlock 或剩余字节数
                int inputLen = (data.length - offset);
                if (inputLen > encryptBlock) {
                    inputLen = encryptBlock;
                }
                // 得到分段加密结果
                byte[] encryptedBlock = cipher.doFinal(data, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(encryptedBlock);
            }
            return Base64.encodeToString(outbuf.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                outbuf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] decode(String data) {
        return decode(privateKey,Base64.decode(data, Base64.DEFAULT));
    }

    /**
     * 解密
     *
     * @param priKey
     *            pkcs#8
     * @param data
     *            解密报文 "string".getBytes();
     * @return new String( byte[] )
     */
    public static byte[] decode(String priKey, byte[] data) {
        // 计算分段加密的block数 (向上取整)
        int nBlock = (data.length / encryptBlock);
        if ((data.length % encryptBlock) != 0) { // 余数非0，block数再加1
            nBlock += 1;
        }
        ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * encryptBlock);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            PrivateKey privateKey = getPrivateKey(priKey);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            for (int offset = 0; offset < data.length; offset += decryptBlock) {
                // block大小: decryptBlock 或剩余字节数
                int inputLen = (data.length - offset);
                if (inputLen > decryptBlock) {
                    inputLen = decryptBlock;
                }
                // 得到分段加密结果
                byte[] decryptedBlock = cipher.doFinal(data, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(decryptedBlock);
            }
            return outbuf.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                outbuf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 普通解密
     * @param key
     * @param data
     * @return new String()
     */
    public static byte[] decode(String key, String data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            PrivateKey privateKey = getPrivateKey(key);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(Base64.decode(data.getBytes(), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 普通加密
     * @param key
     * @param data getBytes()
     * @return Bsae64.enCodeToString()
     */
    public static byte[] encode2(String key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            PublicKey publicKey = getPublicKey(key);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
