import channel.Channel;
import crypto.DBRangeProof;
import crypto.DBSign;
import crypto.MySha256;
import model.Tumbler;
import model.User;
import org.aion.tetryon.G1Point;
import org.aion.tetryon.PubParam;

import java.math.BigInteger;
import java.util.Vector;

public class Start {



    public static void run(int length) throws Exception {
        PubParam pp = new PubParam();
        MySha256 mySha256 = new MySha256();
        Vector<G1Point> G2i = DBRangeProof.getGi(pp, length);
        DBSign.KeyPair keyPair = DBSign.keyGen(pp);

        BigInteger transfer_value = new BigInteger("1");
        BigInteger senderValue = new BigInteger("1000");
        BigInteger receiverValue = new BigInteger("1000");
        BigInteger tumblerValue = new BigInteger("1000");
        BigInteger tumblerValue_ = new BigInteger("1000");
        User sender = new User(senderValue, tumblerValue, length, mySha256, G2i);
        User receiver = new User(receiverValue, tumblerValue_, length, mySha256, G2i);
        Tumbler tumbler = new Tumbler(senderValue, tumblerValue, length, mySha256, G2i, keyPair);
        Tumbler tumbler_ = new Tumbler(receiverValue, tumblerValue_, length, mySha256, G2i, keyPair);

        sender.blindValue(pp);
        receiver.blindValue(pp);
        tumbler.blindValue(pp);
        tumbler_.blindValue(pp);

        if (!sender.checkBlindValue(pp, tumbler.tumblerBlindValue.x, tumbler.tumblerBlindValue.r1, tumbler.tumblerBlindValue.r2, tumbler.tumblerBlindValue.secBldMsg)){
            System.out.println("sender check wrong");
        }

        if (!tumbler.checkBlindValue(pp, sender.userBlindValue.x, sender.userBlindValue.r1, sender.userBlindValue.r2, sender.userBlindValue.secBldMsg)){
            System.out.println("tumbler check wrong");
        }

        if (!receiver.checkBlindValue(pp, tumbler_.tumblerBlindValue.x, tumbler_.tumblerBlindValue.r1, tumbler_.tumblerBlindValue.r2, tumbler_.tumblerBlindValue.secBldMsg)){
            System.out.println("receiver check wrong");
        }

        if (!tumbler_.checkBlindValue(pp, receiver.userBlindValue.x, receiver.userBlindValue.r1, receiver.userBlindValue.r2, receiver.userBlindValue.secBldMsg)){
            System.out.println("tumbler_ check wrong");
        }

        long starTime = System.nanoTime();
        int round = 100;
        for (int i = 0; i < round; i++) {
            //open
            DBRangeProof.RangPrf[] proofs = sender.senderPayment(pp, transfer_value);
            DBSign.Sig_bb sig_bb = tumbler.paymentVerify(pp, proofs);
            DBSign.Sig_b sig_b = sender.unBldSig(pp, sig_bb, keyPair.pk_);
            receiver.update(pp, sender.transferValue);
            tumbler_.verifySig_b(pp, sig_b, sender.transferValue.firBldMsg);
        }
        long endTime = System.nanoTime();
        double avgTime = (endTime-starTime)/(round*1.0) / 1000000;
        System.out.println("length: " +length + " avgTime = " + avgTime);
    }


    public static void main(String[] args) throws Exception {

        int[] lengths = new int[]{10, 10, 10, 20 ,30 , 40, 50};
        for (int length: lengths){
            run(length);
        }

    }


}
