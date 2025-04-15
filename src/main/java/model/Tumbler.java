package model;

import crypto.DBRangeProof;
import crypto.DBSign;
import crypto.MySha256;
import org.aion.tetryon.G1;
import org.aion.tetryon.G1Point;
import org.aion.tetryon.PubParam;

import java.math.BigInteger;
import java.util.Vector;

public class Tumbler {
    public static BigInteger q = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");

    public BigInteger userValue;
    public BigInteger tumblerValue;
    public DBSign.BldMsg userBlindValue;
    public DBSign.BldMsg tumblerBlindValue;
    public int length;
    public MySha256 mySha256;
    public Vector<G1Point> G2i;
    public DBSign.KeyPair keyPair;

    public Tumbler(BigInteger userValue, BigInteger tumblerValue, int length, MySha256 mySha256, Vector<G1Point> g2i, DBSign.KeyPair keyPair) {
        this.userValue = userValue;
        this.tumblerValue = tumblerValue;
        this.length = length;
        this.mySha256 = mySha256;
        G2i = g2i;
        this.keyPair = keyPair;
    }

    public void blindValue(PubParam pp) throws Exception {
        tumblerBlindValue = DBSign.blindMsg(pp, tumblerValue);
    }

    public boolean checkBlindValue(PubParam pp, BigInteger x, BigInteger r1, BigInteger r2, G1Point userBlindValue) throws Exception {
        G1Point g1_x = G1.mul(pp.G1_g1, x);
        G1Point h1_r1 = G1.mul(pp.G1_h1, r1);
        G1Point h2_r2 = G1.mul(pp.G1_h2, r2);
        G1Point firBldMsg = G1.add(g1_x, h1_r1);
        G1Point secBldMsg = G1.add(firBldMsg, h2_r2);

        if (secBldMsg.equals(userBlindValue)){
            this.userBlindValue = new DBSign.BldMsg(x, r1, r2, firBldMsg, secBldMsg);
            return true;
        }else {
            return false;
        }
    }

    public DBSign.Sig_bb paymentVerify(PubParam pp, DBRangeProof.RangPrf[] proofs) throws Exception {
        G1Point com = proofs[0].com;

        G1Point neg_com = G1.negate(com);
        G1Point updateValue = G1.add(userBlindValue.secBldMsg, neg_com);
        if (!updateValue.equals(proofs[1].com)){
            System.out.println("check wrong!");
            return null;
        }
        this.userBlindValue.secBldMsg = updateValue;
        this.tumblerBlindValue.secBldMsg = G1.add(tumblerBlindValue.secBldMsg, com);

        boolean result1 = DBRangeProof.verify(pp, length, G2i, mySha256, proofs[0]);
        boolean result2 = DBRangeProof.verify(pp, length, G2i, mySha256, proofs[1]);
        if (!result1 || !result2){
            System.out.println("check wrong!");
            return null;
        }

        DBSign.Sig_bb sig_bb = DBSign.sign(pp, keyPair.sk, com);

        return sig_bb;
    }

    public boolean verifySig_b(PubParam pp, DBSign.Sig_b sig_b, G1Point m_b) throws Exception {
        boolean result = DBSign.verify(pp, keyPair.pk_, m_b, sig_b);
        if (result==false){
            System.out.println("sig_b check wrong!");
        }

        G1Point neg_mb = G1.negate(m_b);
        this.tumblerBlindValue.secBldMsg = G1.add(this.tumblerBlindValue.secBldMsg, neg_mb);
        this.userBlindValue.secBldMsg = G1.add(this.userBlindValue.secBldMsg, m_b);
        return true;
    }
}
