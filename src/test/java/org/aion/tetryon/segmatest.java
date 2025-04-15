package org.aion.tetryon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RunWith(JUnit4.class)
public class segmatest {
    private static BigInteger hashToBigInteger(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input);
            return new BigInteger(1, hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not found", e);
        }
    }

    @Test
    public void generateProof() throws Exception {
        Fp ax = new Fp(new BigInteger("222480c9f95409bfa4ac6ae890b9c150bc88542b87b352e92950c340458b0c09", 16));
        Fp ay = new Fp(new BigInteger("2976efd698cf23b414ea622b3f720dd9080d679042482ff3668cb2e32cad8ae2", 16));
        G1Point G = new G1Point(ax, ay);
        BigInteger q = new BigInteger("21888242871839275222246405745257275088696311157297823662689037894645226208583");

        BigInteger sk = Util.getRandom();
        G1Point pk = G1.mul(G, sk);

//        long start = System.currentTimeMillis();
//        for (int i=0;i<80;i++) {
//            BigInteger r = Util.getRandom(); // 随机生成 r
//            G1Point C = G1.mul(G, r); // 承诺值 C = r * G
//            byte[] cBytes = C.toString().getBytes();  // 使用字节表示
//            BigInteger c = hashToBigInteger(cBytes).mod(q); // 挑战值 c = H(C, Y)
//            BigInteger z = r.add(c.multiply(sk)).mod(q); // 响应值 z = r + c * x
//        }
//        long end = System.currentTimeMillis();
//        float proofGenTime = (float) (end - start) ;
//
//        System.out.println("Proof generation time: " + proofGenTime + " ms");


//        BigInteger r = Util.getRandom(); // 随机生成 r
//        G1Point C = G1.mul(G, r); // 承诺值 C = r * G
//        byte[] cBytes = C.toString().getBytes();  // 使用字节表示
//        BigInteger c = hashToBigInteger(cBytes).mod(q); // 挑战值 c = H(C, Y)
//        BigInteger z = r.add(c.multiply(sk)).mod(q); // 响应值 z = r + c * x
//
//        long start0 = System.currentTimeMillis();
//        for (int i=0;i<80;i++) {
//            G1Point left = G1.mul(G, c);
//            G1Point right = G1.add(C, G1.mul(pk, z));
//        }
//        long end0 = System.currentTimeMillis();
//        float proofVerifyTime = (float) (end0 - start0) ;
//        System.out.println("Proof verification time: " + proofVerifyTime + " ms");

        int j = 4;
        //setup

        long start0 = System.currentTimeMillis();
        for(int round = 0; round<50;round++){
            for(int i = 0; i<j ; i++){
                BigInteger r = Util.getRandom(); // 随机生成 r
                G1Point C = G1.mul(G, r); // 承诺值 C = r * G
                byte[] cBytes = C.toString().getBytes();  // 使用字节表示
                BigInteger c = hashToBigInteger(cBytes).mod(q); // 挑战值 c = H(C, Y)
                BigInteger z = r.add(c.multiply(sk)).mod(q); // 响应值 z = r + c * x

                G1Point left = G1.mul(G, c);
                G1Point right = G1.add(C, G1.mul(pk, z));

                for(int t=0 ; t<3 ; t++ ){
                    G1Point g1Point = G1.add(C, G1.mul(pk, z));
                }
            }
        }
        long end0 = System.currentTimeMillis() ;
        float setupTime = (float) (end0 - start0) / 50;
        System.out.println("setup time_" + j + ":" + setupTime + " ms");


        long start1 = System.currentTimeMillis();
        for(int round = 0; round<50;round++){
            for(int i = 0; i<j ; i++){
                for(int t=0 ; t<2 ; t++ ) {
                    BigInteger r = Util.getRandom(); // 随机生成 r
                    G1Point C = G1.mul(G, r); // 承诺值 C = r * G
                    byte[] cBytes = C.toString().getBytes();  // 使用字节表示
                    BigInteger c = hashToBigInteger(cBytes).mod(q); // 挑战值 c = H(C, Y)
                    BigInteger z = r.add(c.multiply(sk)).mod(q); // 响应值 z = r + c * x

                    G1Point left = G1.mul(G, c);
                    G1Point right = G1.add(C, G1.mul(pk, z));
                }

                for(int t=0 ; t<8 ; t++ ){
                    G1Point g1Point = G1.add(G, G1.mul(pk, sk));
                }
            }
        }
        long end1 = System.currentTimeMillis() ;
        float lockTime = (float) (end1 - start1) / 50;
        System.out.println("lock time_" + j + ":" + lockTime + " ms");



        long start2 = System.currentTimeMillis();
        for(int round = 0; round<50;round++){
            for(int i = 0; i<j ; i++){
                G.equals(G);

                for(int t=0 ; t<2 ; t++ ){
                    G1Point g1Point = G1.add(G, G1.mul(pk, sk));//操作性能测试，本代码无意义
                }
            }
        }
        long end2 = System.currentTimeMillis() ;
        float releaseTime = (float) (end2 - start2) / 50;
        System.out.println("release time_" + j + ":" + releaseTime + " ms");
//        G1Point left = G1.mul(G, c);
//        G1Point right = G1.add(C, G1.mul(pk, z));

//        // 调试输出，检查计算的中间结果
//        System.out.println("C: " + C);
//        System.out.println("c: " + c);
//        System.out.println("z: " + z);
//        System.out.println("left: " + left);
//        System.out.println("right: " + right);
//
//        // 验证等式
//        boolean b = left.equals(right);
//        System.out.println("Verification result: " + b);
//
//        assertTrue(b); // 验证证明是否正确
    }

    private static G1Point generatePublicKey(G1Point G, BigInteger sk) throws Exception {
        G1Point pk = G1.mul(G,sk);
        return pk;
    }


}