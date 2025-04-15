package crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MySha256 {
    MessageDigest digest;

    public MySha256(){
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
        e.printStackTrace();
        }

    }


    public  BigInteger calSha256(String msg) throws NoSuchAlgorithmException {

        byte[] hashBytes = digest.digest(msg.getBytes());
        return new BigInteger(hashBytes);
    }


}
