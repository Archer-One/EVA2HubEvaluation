package model;

import crypto.DBRangeProof;
import crypto.DBSign;
import crypto.MySha256;
import org.aion.tetryon.*;

import java.math.BigInteger;
import java.util.Vector;

public class User {
    public static BigInteger q = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");

    public BigInteger userValue;
    public BigInteger tumblerValue;
    public DBSign.BldMsg userBlindValue;
    public DBSign.BldMsg tumblerBlindValue;
    public DBSign.BldMsg transferValue;
    public DBSign.BldMsg tempUser;
    public DBSign.BldMsg tempTumbler;
    public int length;
    public MySha256 mySha256;
    public Vector<G1Point> G2i;

    public User(BigInteger userValue, BigInteger tumblerValue, int length, MySha256 mySha256, Vector<G1Point> g2i) {
        this.userValue = userValue;
        this.tumblerValue = tumblerValue;
        this.length = length;
        this.mySha256 = mySha256;
        G2i = g2i;
    }

    public void blindValue(PubParam pp) throws Exception {
        userBlindValue = DBSign.blindMsg(pp, userValue);
    }

    public boolean checkBlindValue(PubParam pp, BigInteger x, BigInteger r1, BigInteger r2, G1Point tumblerBlindValue) throws Exception {
        G1Point g1_x = G1.mul(pp.G1_g1, x);
        G1Point h1_r1 = G1.mul(pp.G1_h1, r1);
        G1Point h2_r2 = G1.mul(pp.G1_h2, r2);
        G1Point firBldMsg = G1.add(g1_x, h1_r1);
        G1Point secBldMsg = G1.add(firBldMsg, h2_r2);

        if (secBldMsg.equals(tumblerBlindValue)){
            this.tumblerBlindValue = new DBSign.BldMsg(x, r1, r2, firBldMsg, secBldMsg);
            return true;
        }else {
            return false;
        }
    }

    public DBRangeProof.RangPrf[] senderPayment(PubParam pp, BigInteger value) throws Exception {
        DBSign.BldMsg blindValue = DBSign.blindMsg(pp, value);
        transferValue = blindValue;
        DBRangeProof.RangPrf pi_0 = DBRangeProof.genRange(pp, blindValue.secBldMsg, value, blindValue.r1, blindValue.r2, length, G2i, mySha256);

        BigInteger x_u = userBlindValue.x.subtract(value);
        BigInteger r1_u = userBlindValue.r1.subtract(blindValue.r1).mod(q);
        BigInteger r2_u = userBlindValue.r2.subtract(blindValue.r2).mod(q);

        G1Point neg_transferValue = G1.negate(blindValue.secBldMsg);
        G1Point updateValue = G1.add(userBlindValue.secBldMsg, neg_transferValue);
        this.tempUser = new DBSign.BldMsg(x_u, r1_u, r2_u, null, updateValue);

        BigInteger x_t = tumblerBlindValue.x.add(value);
        BigInteger r1_t = tumblerBlindValue.r1.add(blindValue.r1).mod(q);
        BigInteger r2_t = tumblerBlindValue.r2.add(blindValue.r2).mod(q);

        G1Point updateValue_t = G1.add(userBlindValue.secBldMsg, blindValue.secBldMsg);
        this.tempTumbler = new DBSign.BldMsg(x_u, r1_u, r2_u, null, updateValue_t);


        DBRangeProof.RangPrf pi_1 = DBRangeProof.genRange(pp, updateValue, x_u, r1_u, r2_u, length, G2i, mySha256);
        DBRangeProof.RangPrf[] proofs = new DBRangeProof.RangPrf[]{pi_0, pi_1};
        return proofs;
    }

    public DBSign.Sig_b unBldSig(PubParam pp, DBSign.Sig_bb sig_bb, G2Point pk) throws Exception {
        boolean result = DBSign.verify(pp, pk, transferValue, sig_bb);
        if (!result){
            System.out.println("sig_bb check wrong!");
        }

        this.tumblerBlindValue = tempTumbler;
        this.userBlindValue = tempUser;

        DBSign.Sig_b sig_b = DBSign.unBldSig(pp, sig_bb, transferValue);
        return sig_b;
    }


    public void update(PubParam pp, DBSign.BldMsg bldMsg) throws Exception {
        this.userBlindValue.r1 = this.userBlindValue.r1.add(bldMsg.r1).mod(q);
        this.userBlindValue.r2 = this.userBlindValue.r2.add(bldMsg.r2).mod(q);
        this.userBlindValue.x = this.userBlindValue.x.add(bldMsg.x).mod(q);
        this.userBlindValue.secBldMsg = G1.add(this.userBlindValue.secBldMsg, bldMsg.firBldMsg);

        this.tumblerBlindValue.r1 = this.tumblerBlindValue.r1.subtract(bldMsg.r1).mod(q);
        this.tumblerBlindValue.r2 = this.tumblerBlindValue.r2.subtract(bldMsg.r2).mod(q);
        this.tumblerBlindValue.x = this.tumblerBlindValue.x.subtract(bldMsg.x).mod(q);
        G1Point neg_mb = G1.negate(bldMsg.firBldMsg);
        this.tumblerBlindValue.secBldMsg = G1.add(this.tumblerBlindValue.secBldMsg, neg_mb);
    }


}
