package channel;

import crypto.DBSign;
import org.aion.tetryon.G1;
import org.aion.tetryon.G1Point;
import org.aion.tetryon.PubParam;
import org.aion.tetryon.Util;

import java.math.BigInteger;

public class Channel {

    public BigInteger userValue;
    public BigInteger tumblerValue;
    public DBSign.BldMsg userBlindValue;
    public DBSign.BldMsg tumblerBlindValue;

    public Channel(BigInteger userValue, BigInteger tumblerValue) {
        this.userValue = userValue;
        this.tumblerValue = tumblerValue;
    }

    public static void main(String[] args) throws Exception {

        PubParam pp = new PubParam();
        BigInteger transfer_value = new BigInteger("2");
        BigInteger senderValue = new BigInteger("5");
        BigInteger receiverValue = new BigInteger("10");
        BigInteger tumblerValue = new BigInteger("3");
        BigInteger tumblerValue_ = new BigInteger("3");
        Channel s_t = new Channel(senderValue, tumblerValue);
        Channel r_t = new Channel(receiverValue, tumblerValue_);


        if(!(s_t.open(pp) && r_t.open(pp))){
            System.out.println("open wrong");
        }


    }

    public boolean open(PubParam pp) throws Exception {
        userBlindValue = DBSign.blindMsg(pp, userValue);
        tumblerBlindValue = DBSign.blindMsg(pp, tumblerValue);
        if (checkBlindValue(pp, userBlindValue.x, userBlindValue.r1, userBlindValue.r2, userBlindValue.secBldMsg) && checkBlindValue(pp, tumblerBlindValue.x, tumblerBlindValue.r1, tumblerBlindValue.r2, tumblerBlindValue.secBldMsg)){
            return true;
        }else {
            return false;
        }

    }

    public boolean checkBlindValue(PubParam pp, BigInteger x, BigInteger r1, BigInteger r2, G1Point result) throws Exception {

        G1Point g1_x = G1.mul(pp.G1_g1, x);
        G1Point h1_r1 = G1.mul(pp.G1_h1, r1);
        G1Point h2_r2 = G1.mul(pp.G1_h2, r2);
        G1Point firBldMsg = G1.add(g1_x, h1_r1);
        G1Point secBldMsg = G1.add(firBldMsg, h2_r2);

        if (secBldMsg.equals(result)){
            return true;
        }else {
            return false;
        }
    }


    public void paymentSender(PubParam pp, BigInteger value) throws Exception {
        DBSign.BldMsg transferValue = DBSign.blindMsg(pp , value);


    }



}


