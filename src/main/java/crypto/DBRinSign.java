package crypto;

import org.aion.tetryon.G1;
import org.aion.tetryon.G1Point;
import org.aion.tetryon.PubParam;
import org.aion.tetryon.Util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class DBRinSign {

    public static BigInteger q = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");


    public static void main(String[] args) throws Exception {
        PubParam pp = new PubParam();
        KeyPair keyPair = keyGen(pp);
        BigInteger r1 = Util.getRandom();
        BigInteger r2 = Util.getRandom();
        G1Point pk0 = G1.mul(pp.G1_h1, r1);
        G1Point pk1 = G1.mul(pp.G1_h2, r1);
        G1Point pk2 = G1.mul(pp.G1_h2, r2);
        G1Point[] pk_list = new G1Point[]{pk0, keyPair.pk, pk1, pk2};
        int index = 1;
        String m = "test";
        MySha256 mySha256 = new MySha256();

        Sig sig = sign(pp, keyPair.sk0, keyPair.sk1, index, pk_list, m, mySha256);
        boolean result = verify(pp, pk_list, m, mySha256, sig);
        System.out.println("result = " + result);
    }

    public static KeyPair keyGen(PubParam pp) throws Exception {
        BigInteger sk0 = Util.getRandom();
        BigInteger sk1 = Util.getRandom();
        G1Point h1_sk0 = G1.mul(pp.G1_h1, sk0);
        G1Point h2_sk1 = G1.mul(pp.G1_h2, sk1);
        G1Point pk = G1.add(h1_sk0, h2_sk1);
        return new KeyPair(sk0, sk1, pk);
    }

    public static Sig sign(PubParam pp, BigInteger sk0, BigInteger sk1, int index, G1Point[] pk_list, String m, MySha256 mySha256) throws Exception {
        int l = pk_list.length;
        ArrayList<BigInteger[]> beta_list = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            beta_list.add(null);
        }


        String pk_list_str = getPkListStr(pk_list);
        String pre_hash_input = pk_list_str + m;
        BigInteger one = new BigInteger("1");
        BigInteger two = new BigInteger("2");
        G1Point h1_ = G1.HashToG1(one, pk_list_str);
        G1Point h2_ = G1.HashToG1(two, pk_list_str);
        G1Point h1__sk0 = G1.mul(h1_, sk0);
        G1Point h2__sk1 = G1.mul(h2_, sk1);
        G1Point pk_ = G1.add(h1__sk0, h2__sk1);

        BigInteger alpha = Util.getRandom();
        BigInteger alpha_ = Util.getRandom();

        G1Point h1_alpha = G1.mul(pp.G1_h1, alpha);
        G1Point h2_alpha = G1.mul(pp.G1_h2, alpha_);
        G1Point z = G1.add(h1_alpha, h2_alpha);

        G1Point h1__alpha = G1.mul(h1_, alpha);
        G1Point h2__alpha_ = G1.mul(h2_, alpha_);
        G1Point z_ = G1.add(h1__alpha, h2__alpha_);

        String hash_input = pre_hash_input + Util.g1PointToString(z) + Util.g1PointToString(z_);
        
        BigInteger v_j = mySha256.calSha256(hash_input).mod(q);
        BigInteger preV = v_j;
        BigInteger v0 = preV;
        for (int i = index+1; ; i++) {
            
            int k = i% l;
            if (k==index){
                break;
            }
            BigInteger beta = Util.getRandom();
            BigInteger beta_ = Util.getRandom();
            BigInteger[] betas = new BigInteger[]{beta, beta_};
            beta_list.set(k, betas);
            
            G1Point h1_beta = G1.mul(pp.G1_h1, beta);
            G1Point h2_beta_ = G1.mul(pp.G1_h2, beta_);
            G1Point Z = G1.add(h1_beta, h2_beta_);
            G1Point pk_preV =G1.mul(pk_list[k], preV);
            Z = G1.add(Z,pk_preV);

            G1Point h1__beta = G1.mul(h1_, beta);
            G1Point h2__beta_ = G1.mul(h2_, beta_);
            G1Point Z_ = G1.add(h1__beta, h2__beta_);
            G1Point pk__preV = G1.mul(pk_, preV);
            Z_ = G1.add(Z_, pk__preV);

            hash_input = pre_hash_input + Util.g1PointToString(Z) + Util.g1PointToString(Z_);
            preV = mySha256.calSha256(hash_input).mod(q);
            if (k==0){
                v0 = preV;
            }
        }
        BigInteger sk0_prev = sk0.multiply(preV).mod(q);
        BigInteger sk1_prev = sk1.multiply(preV).mod(q);
        BigInteger beta = alpha.subtract(sk0_prev).mod(q);
        BigInteger beta_ = alpha_.subtract(sk1_prev).mod(q);


        BigInteger[] betas = new BigInteger[]{beta, beta_};
        beta_list.set(index, betas);

        return new Sig(v0, pk_, beta_list);


    }


    public static boolean verify(PubParam pp, G1Point[] pk_list, String m, MySha256 mySha256, Sig sig) throws Exception {
        String pk_list_str = getPkListStr(pk_list);
        String pre_hash_input = pk_list_str + m;
        BigInteger one = new BigInteger("1");
        BigInteger two = new BigInteger("2");
        G1Point h1_ = G1.HashToG1(one, pk_list_str);
        G1Point h2_ = G1.HashToG1(two, pk_list_str);
        int len = pk_list.length;
        BigInteger preV = sig.v0;
        G1Point pk_ = sig.pk_;
        for (int i = 1; i < len + 1; i++) {
            int k = i % len;
            BigInteger[] betas = sig.beta_list.get(k);
            BigInteger beta = betas[0];
            BigInteger beta_ = betas[1];

            G1Point h1_beta = G1.mul(pp.G1_h1, beta);
            G1Point h2_beta_ = G1.mul(pp.G1_h2, beta_);
            G1Point Z = G1.add(h1_beta, h2_beta_);
            G1Point pk_preV = G1.mul(pk_list[k], preV);
            Z = G1.add(Z, pk_preV);

            G1Point h1__beta = G1.mul(h1_, beta);
            G1Point h2__beta_ = G1.mul(h2_, beta_);
            G1Point Z_ = G1.add(h1__beta, h2__beta_);
            G1Point pk__preV = G1.mul(pk_, preV);
            Z_ = G1.add(Z_, pk__preV);
            String hash_input = pre_hash_input + Util.g1PointToString(Z) + Util.g1PointToString(Z_);
            preV = mySha256.calSha256(hash_input).mod(q);

            if (k == 0) {
                if (preV.equals(sig.v0)) {
                    return true;
                } else {
                    return false;
                }
            }
        }


        return true;
    }

    public static String getPkListStr(G1Point[] pk_list) {
        String str = "";
        for (G1Point g1Point : pk_list) {
            String g_str = Util.g1PointToString(g1Point);
            str += g_str;
        }

        return str;
    }


    public static class KeyPair {
        public BigInteger sk0;
        public BigInteger sk1;
        public G1Point pk;

        public KeyPair(BigInteger sk0, BigInteger sk1, G1Point pk) {
            this.sk0 = sk0;
            this.sk1 = sk1;
            this.pk = pk;
        }
    }

    public static class Sig {
        public BigInteger v0;
        public G1Point pk_;
        public ArrayList<BigInteger[]> beta_list;

        public Sig(BigInteger v0, G1Point pk_, ArrayList<BigInteger[]> beta_list) {
            this.v0 = v0;
            this.pk_ = pk_;
            this.beta_list = beta_list;
        }
    }
}
