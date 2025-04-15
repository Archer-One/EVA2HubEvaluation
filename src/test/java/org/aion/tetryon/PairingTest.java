package org.aion.tetryon;


import org.junit.Test;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PairingTest {
    @Test
    public void pairingProd2Test() {
        Fp g11x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g11y = new Fp(new BigInteger("23db84b7ae4e35681e833b6a1f6903e28291d154af3ec5ddc787e0e6cb058912", 16));
        G1Point g11 = new G1Point(g11x, g11y);

        Fp g12x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g12y = new Fp(new BigInteger("0c88c9bb32e36ac199cd0a4c6218547b14ef993cb93304af7498ab300d777435", 16));
        G1Point g12 = new G1Point(g12x, g12y);

        Fp2 g2x = new Fp2(new BigInteger("27d2525616cd883a2e952616138e052125201826d45e179a9ae28655338ca2be", 16),
                new BigInteger("2167ff55d36a2ed92eb480b1b9365382ea2facea90c860d63211827f122fdc29", 16));
        Fp2 g2y = new Fp2(new BigInteger("2c6e8b5d5da9a03f2d6b57bf2338168eca1e43409693b43659fe834149e506a9", 16),
                new BigInteger("020401d78e6fe746fe3d9512f9b4eedcfdd7eb5d08e307f1d6ee5d38f9a253ec", 16));
        G2Point g2 = new G2Point(g2x, g2y);

        boolean r = false;

        try {
            long start = System.nanoTime();
            r = Pairing.pairingProd2(g11, g2, g12, g2);
            System.out.println(r);
            long ms = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            System.out.println("ecPair pairingProd2 test took " + ms + " ms");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertTrue(r);
    }


    @Test
    public void pairingProd1Test() throws Exception {
        Fp g11x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g11y = new Fp(new BigInteger("23db84b7ae4e35681e833b6a1f6903e28291d154af3ec5ddc787e0e6cb058912", 16));
        G1Point g1 = new G1Point(g11x, g11y);
        BigInteger x = new BigInteger("3243542354");
        G1Point h1 = G1.mul(g1, x);

        Fp g12x = new Fp(new BigInteger("2bcf154b010dedb450cfea4f635526973f39365ec204e4a8b0e3ecc29abb7e4e", 16));
        Fp g12y = new Fp(new BigInteger("0c88c9bb32e36ac199cd0a4c6218547b14ef993cb93304af7498ab300d777435", 16));
        G1Point g12 = new G1Point(g12x, g12y);

        Fp2 g2x = new Fp2(new BigInteger("27d2525616cd883a2e952616138e052125201826d45e179a9ae28655338ca2be", 16),
                new BigInteger("2167ff55d36a2ed92eb480b1b9365382ea2facea90c860d63211827f122fdc29", 16));
        Fp2 g2y = new Fp2(new BigInteger("2c6e8b5d5da9a03f2d6b57bf2338168eca1e43409693b43659fe834149e506a9", 16),
                new BigInteger("020401d78e6fe746fe3d9512f9b4eedcfdd7eb5d08e307f1d6ee5d38f9a253ec", 16));
        G2Point g2 = new G2Point(g2x, g2y);

        BigInteger filed = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");

        BigInteger r1 = new BigInteger("50");
        BigInteger r1_ = new BigInteger("-50").mod(filed);
        BigInteger r2 = new BigInteger("123");
        BigInteger r2_ = new BigInteger("-123").mod(filed);
        G1Point g1_r1 = G1.mul(g1, r1);
        G1Point h1_r2 = G1.mul(h1, r2);
        G1Point result = G1.add(g1_r1, h1_r2);

        G1Point g1_r1_ = G1.mul(g1, r1_);
        G2Point g2_r2_ = G2.ECTwistMul(g2, r2_);

        G1Point[] g1_list = new G1Point[]{result, g1_r1_, h1};
        G2Point[] g2_list = new G2Point[]{g2, g2, g2_r2_};
        boolean r = false;

        try {
            long start = System.nanoTime();
            r = Pairing.pairing(g1_list, g2_list);
            System.out.println(r);
            long ms = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            System.out.println("ecPair pairingProd2 test took " + ms + " ms");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertTrue(r);
    }
}
