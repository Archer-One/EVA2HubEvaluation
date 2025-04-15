package crypto;

import org.aion.tetryon.G1;
import org.aion.tetryon.G1Point;
import org.aion.tetryon.PubParam;
import org.aion.tetryon.Util;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Vector;

public class DBRangeProof {
    public static BigInteger q = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");

    public static void main(String[] args) throws Exception {
        int length = 10;
        MySha256 mySha256 = new MySha256();
        BigInteger x = new BigInteger("5");
        PubParam pp = new PubParam();
        DBSign.BldMsg bldMsg = DBSign.blindMsg(pp, x);
        Vector<G1Point> G2i = getGi(pp, length);
        RangPrf rangPrf = genRange(pp, bldMsg.secBldMsg, x, bldMsg.r1, bldMsg.r2, length, G2i, mySha256);
        boolean result = verify(pp, length, G2i, mySha256, rangPrf);
        System.out.println("result = " + result);
    }


    public static Vector<G1Point> getGi(PubParam pp, int length) throws Exception {
        Vector<G1Point> G2i = new Vector<>(length);
        BigInteger two = new BigInteger("2");
        for (int i = 0; i < length; i++) {
            BigInteger i_ = new BigInteger(Integer.toString(i));
            BigInteger e = two.modPow(i_, q);
            G1Point g1_i = G1.mul(pp.G1_g1, e);
            G2i.add(g1_i);
        }
        return G2i;
    }

    public static RangPrf genRange(PubParam pp, G1Point com, BigInteger x, BigInteger r1, BigInteger r2, int length, Vector<G1Point> G2i, MySha256 mySha256) throws Exception {
        Vector<BigInteger> xArray = toByteArray(x, length);
        Vector<BigInteger> r1_list = getRs(r1, length);
        Vector<BigInteger> r2_list = getRs(r2, length);
        Vector<DBRinSign.Sig> sig_list = new Vector<DBRinSign.Sig>(length);
        Vector<G1Point> bitValue_list = new Vector<G1Point>(length);

        String m = "test";

        BigInteger one = BigInteger.ONE;
        for (int i = 0; i < length; i++) {
            BigInteger r1_i = r1_list.get(i);
            BigInteger r2_i = r2_list.get(i);

            G1Point h1_r1 = G1.mul(pp.G1_h1, r1_i);
            G1Point h2_r2 = G1.mul(pp.G1_h2, r2_i);

            G1Point bitValueCom = G1.add(h1_r1, h2_r2);
            BigInteger bitValue = xArray.get(i);
            G1Point g2i = G2i.get(i);
            int index = 0;
            if (bitValue.equals(one)){
                bitValueCom = G1.add(g2i, bitValueCom);
                index = 1;
            }
            bitValue_list.add(bitValueCom);
            G1Point neg_g2i = G1.negate(g2i);
            G1Point bitValueCom_ = G1.add(bitValueCom, neg_g2i);
            G1Point[] pk_list = new G1Point[]{bitValueCom, bitValueCom_};
            DBRinSign.Sig sig_i = DBRinSign.sign(pp, r1_i, r2_i, index, pk_list, m, mySha256);

            sig_list.add(sig_i);
        }

        return new RangPrf(com, bitValue_list, sig_list);
    }

    public static boolean verify(PubParam pp,  int length, Vector<G1Point> G2i, MySha256 mySha256, RangPrf rangPrf) throws Exception {
        G1Point com = rangPrf.com;
        G1Point sum = rangPrf.bitValue_list.get(0);
        for (int i = 1; i < length; i++) {
            sum = G1.add(sum, rangPrf.bitValue_list.get(i));
        }
        if (!sum.equals(com)){
            return false;
        }

        String m = "test";

        for (int i = 0; i < length; i++) {

            G1Point bitValueCom = rangPrf.bitValue_list.get(i);
            G1Point g2i = G2i.get(i);

            G1Point neg_g2i = G1.negate(g2i);
            G1Point bitValueCom_ = G1.add(bitValueCom, neg_g2i);
            G1Point[] pk_list = new G1Point[]{bitValueCom, bitValueCom_};
            boolean result = DBRinSign.verify(pp, pk_list, m, mySha256, rangPrf.sig_list.get(i));
            if (result == false){
                return false;
            }
        }

        return true;
    }


    public static Vector<BigInteger> getRs(BigInteger r, int length){
        BigInteger sum = new BigInteger("0");
        Vector<BigInteger> rArray = new Vector<>(length);
        for (int i = 0; i < length-1; i++) {
            BigInteger r_i = Util.getRandom();
            sum = sum.add(r_i).mod(q);
            rArray.add(r_i);
        }
        BigInteger last = r.subtract(sum).mod(q);
        rArray.add(last);

        return rArray;
    }

    public static Vector<BigInteger> toByteArray(BigInteger v, int length){
        Vector<BigInteger> vByte = new Vector<>(length);
        String vString = v.toString(2);
        String preStr = "0";
        for (int i = 0; i < length- vString.length(); i++) {
            vByte.add(new BigInteger("0"));
        }
        for (int i = 0; i < vString.length(); i++) {
            vByte.add(new BigInteger(String.valueOf(vString.charAt(i))));
        }
        Collections.reverse(vByte);
        return vByte;

    }

    public static class RangPrf{
        public G1Point com;
        public Vector<G1Point> bitValue_list;
        public Vector<DBRinSign.Sig> sig_list;

        public RangPrf(G1Point com, Vector<G1Point> bitValue_list, Vector<DBRinSign.Sig> sig_list) {
            this.com = com;
            this.bitValue_list = bitValue_list;
            this.sig_list = sig_list;
        }
    }
}
