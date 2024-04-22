package com.komsije.booking.service;

import com.komsije.booking.dto.CertificateDto;
import com.komsije.booking.dto.SignedCertificateDto;
import com.komsije.booking.exceptions.InvalidDigitalSignatureException;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Service
public class CertificateServiceImpl {
    private final WebClient webClient;

    public CertificateServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://localhost:8081").build();
    }

    public CertificateDto downloadCertificate(String alias){
        SignedCertificateDto certificatePemDTO = sendCertificateDownloadRequest(alias);

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, getPublicKeyFromPem());

            String hash1 = hashSHA256(certificatePemDTO.getPemCertificate());
            String hash2 = new String(cipher.doFinal(certificatePemDTO.getDigitalSignature()), StandardCharsets.UTF_8);

            if(hash1.equals(hash2)){
                return new CertificateDto(certificatePemDTO.getPemCertificate());
            }else{
                throw new InvalidDigitalSignatureException("Invalid digital signature");
            }

        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }

    }

    private String hashSHA256(String input){

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = md.digest(input.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public SignedCertificateDto sendCertificateDownloadRequest(String alias) {
        return webClient.get()
                .uri("/certificate/download/"+alias)
                .retrieve()
                .bodyToMono(SignedCertificateDto.class)
                .block();
    }

    public String getPemFile() {

        try {
            return  new String(Files.readAllBytes(new File("src/main/resources/https/booking-public.pem").toPath()),
                    Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicKey getPublicKeyFromPem() {
        String pem = getPemFile();
        String publicKeyPEM = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.decodeBase64(publicKeyPEM);

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
            return keyFactory.generatePublic(keySpec);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
