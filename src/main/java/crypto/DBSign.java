package crypto;

import org.aion.tetryon.*;

import java.math.BigInteger;


public class DBSign {

    public static void main(String[] args) throws Exception {
        PubParam pp = new PubParam();
        BigInteger x = new BigInteger("5");
        KeyPair keyPair = keyGen(pp);
        BldMsg bldMsg = blindMsg(pp, x);
        Sig_bb sig_bb = sign(pp, keyPair.sk, bldMsg);
        verify(pp, keyPair.pk_, bldMsg, sig_bb);
        
        Sig_b sig_b = unBldSig(pp, sig_bb, bldMsg);
        boolean result = verify(pp, keyPair.pk_, bldMsg, sig_b);
        System.out.println("result = " + result);
    }

    public static KeyPair keyGen(PubParam pp){
        BigInteger sk = Util.getRandom();
        BigInteger sk_ = Util.getNegate(sk);
        G2Point pk = G2.ECTwistMul(pp.G2_g1, sk);
        G2Point pk_ = G2.ECTwistMul(pp.G2_g1, sk_);
        return new KeyPair(sk, pk, pk_);
    }

    public static BldMsg blindMsg(PubParam pp, BigInteger x) throws Exception {
        BigInteger r1 = Util.getRandom();
        BigInteger r2 = Util.getRandom();

        G1Point g1_x = G1.mul(pp.G1_g1, x);
        G1Point h1_r1 = G1.mul(pp.G1_h1, r1);
        G1Point h2_r2 = G1.mul(pp.G1_h2, r2);
        G1Point firBldMsg = G1.add(g1_x, h1_r1);
        G1Point secBldMsg = G1.add(firBldMsg, h2_r2);

        return new BldMsg(x, r1, r2, firBldMsg, secBldMsg);
    }

    public static Sig_bb sign(PubParam pp, BigInteger sk, BldMsg bldMsg) throws Exception {
        G1Point g2_sk = G1.mul(pp.G1_g2, sk);
        BigInteger w = Util.getRandom();
        BigInteger w_ = Util.getNegate(w);
        G1Point secBldMsg_w = G1.mul(bldMsg.secBldMsg, w);
        G1Point sig_bb_0 = G1.add(g2_sk, secBldMsg_w);
        G2Point sig_bb_1 = G2.ECTwistMul(pp.G2_g1, w_);
        G1Point sig_bb_2 = G1.mul(pp.G1_h2, w);
        return new Sig_bb(sig_bb_0, sig_bb_1, sig_bb_2);
    }

    public static Sig_bb sign(PubParam pp, BigInteger sk, G1Point secBldMsg) throws Exception {
        G1Point g2_sk = G1.mul(pp.G1_g2, sk);
        BigInteger w = Util.getRandom();
        BigInteger w_ = Util.getNegate(w);
        G1Point secBldMsg_w = G1.mul(secBldMsg, w);
        G1Point sig_bb_0 = G1.add(g2_sk, secBldMsg_w);
        G2Point sig_bb_1 = G2.ECTwistMul(pp.G2_g1, w_);
        G1Point sig_bb_2 = G1.mul(pp.G1_h2, w);
        return new Sig_bb(sig_bb_0, sig_bb_1, sig_bb_2);
    }
    
    public static Sig_b unBldSig(PubParam pp, Sig_bb sig_bb, BldMsg bldMsg) throws Exception {
        BigInteger neg_r2 = Util.getNegate(bldMsg.r2);
        G1Point sig_bb_2_neg_r2 = G1.mul(sig_bb.sig_bb_2, neg_r2);
        BigInteger w_bar = Util.getRandom();
        G1Point firBldMsg_w_bar = G1.mul(bldMsg.firBldMsg, w_bar);
        G1Point temp1 = G1.add(sig_bb.sig_bb_0, sig_bb_2_neg_r2);
        G1Point sig_b_0 = G1.add(temp1, firBldMsg_w_bar);

        BigInteger neg_w_bar = Util.getNegate(w_bar);
        G2Point g2_new_w_bar = G2.ECTwistMul(pp.G2_g1, neg_w_bar);
        G2Point sig_b_1 = G2.ECTwistAdd(sig_bb.sig_bb_1, g2_new_w_bar);
        
        return new Sig_b(sig_b_0, sig_b_1);
    }


    public static boolean verify(PubParam pp, G2Point pk, BldMsg bldMsg, Sig_bb sig_bb) throws Exception {
        G1Point[] g1_list = new G1Point[]{sig_bb.sig_bb_0, pp.G1_g2, bldMsg.secBldMsg};
        G2Point[] g2_list = new G2Point[]{pp.G2_g1, pk, sig_bb.sig_bb_1};
        boolean result = Pairing.pairing(g1_list, g2_list);
        return result;

    }


    public static boolean verify(PubParam pp, G2Point pk, BldMsg bldMsg, Sig_b sig_b) throws Exception {
        G1Point[] g1_list = new G1Point[]{sig_b.sig_b_0, pp.G1_g2, bldMsg.firBldMsg};
        G2Point[] g2_list = new G2Point[]{pp.G2_g1, pk, sig_b.sig_b_1};
        boolean result = Pairing.pairing(g1_list, g2_list);
        return result;
    }

    public static boolean verify(PubParam pp, G2Point pk, G1Point firBldMsg, Sig_b sig_b) throws Exception {
        G1Point[] g1_list = new G1Point[]{sig_b.sig_b_0, pp.G1_g2, firBldMsg};
        G2Point[] g2_list = new G2Point[]{pp.G2_g1, pk, sig_b.sig_b_1};
        boolean result = Pairing.pairing(g1_list, g2_list);
        return result;
    }

    public static class KeyPair{
        public BigInteger sk;
        public G2Point pk;
        public G2Point pk_;

        public KeyPair(BigInteger sk, G2Point pk, G2Point pk_) {
            this.sk = sk;
            this.pk = pk;
            this.pk_ = pk_;
        }
    }


    public static class BldMsg{
        public BigInteger x;
        public BigInteger r1;
        public BigInteger r2;
        public G1Point firBldMsg;
        public G1Point secBldMsg;

        public BldMsg(BigInteger x, BigInteger r1, BigInteger r2, G1Point firBldMsg, G1Point secBldMsg) {
            this.x = x;
            this.r1 = r1;
            this.r2 = r2;
            this.firBldMsg = firBldMsg;
            this.secBldMsg = secBldMsg;
        }
    }

    public static class Sig_bb{
        public G1Point sig_bb_0;
        public G2Point sig_bb_1;
        public G1Point sig_bb_2;

        public Sig_bb(G1Point sig_bb_0, G2Point sig_bb_1, G1Point sig_bb_2) {
            this.sig_bb_0 = sig_bb_0;
            this.sig_bb_1 = sig_bb_1;
            this.sig_bb_2 = sig_bb_2;
        }
    }

    public static class Sig_b{
        public G1Point sig_b_0;
        public G2Point sig_b_1;

        public Sig_b(G1Point sig_b_0, G2Point sig_b_1) {
            this.sig_b_0 = sig_b_0;
            this.sig_b_1 = sig_b_1;
        }
    } 
}
