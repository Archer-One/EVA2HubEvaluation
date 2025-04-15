package org.aion.tetryon;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static java.math.BigInteger.probablePrime;
import static java.math.BigInteger.valueOf;

public class newTest {
    @Test
    public void PairingTest() throws Exception {
//        //g1^ab
//        Fp g11x = new Fp(new BigInteger("1f2b45f958c3425a533b284d3663e4f027d94b5cc42c2a46636ab85c2517080c", 16));
//        Fp g11y = new Fp(new BigInteger("1b83cd401cb8af190a0d1e09a77816a8f95230e4ffca9359bf5bd048d46a585b", 16));
//        G1Point g11 = new G1Point(g11x, g11y);
//
//        //g1^a
//        Fp g12x = new Fp(new BigInteger("145fa1c89c13d0bf9b46dbd1c6048f3906e207d03a514f3734a655b57c0657c8", 16));
//        Fp g12y = new Fp(new BigInteger("0c307a61ab6830cf13ceb2e48c3361f7a9e9a853cd9650ba8978be0935d6c107", 16));
//        G1Point g12 = new G1Point(g12x, g12y);
//
//        //g2
//        Fp2 g21x = new Fp2(new BigInteger("1800deef121f1e76426a00665e5c4479674322d4f75edadd46debd5cd992f6ed", 16),
//                new BigInteger("198e9393920d483a7260bfb731fb5d25f1aa493335a9e71297e485b7aef312c2", 16));
//        Fp2 g21y = new Fp2(new BigInteger("12c85ea5db8c6deb4aab71808dcb408fe3d1e7690c43d37b4ce6cc0166fa7daa", 16),
//                new BigInteger("090689d0585ff075ec9e99ad690c3395bc4b313370b38ef355acdadcd122975b", 16));
//        G2Point g21 = new G2Point(g21x, g21y);
//
//        //g2^b
//        Fp2 g22x = new Fp2(new BigInteger("0f81835c1a9090d5ee44eddbedd50769e08c53593a6fdb337ace08f7cca085f0", 16),
//                new BigInteger("0c061ab658d734c620bffe31be938a9306a94bc4622374a1b6b0e7de4a87890c", 16));
//        Fp2 g22y = new Fp2(new BigInteger("245f165dafe1831c016f71fa4ad0cfdc0276876db9360812ab21f1cf8a511c01", 16),
//                new BigInteger("2d93c1f5357321bc9fe32e8d4d2dc68e1c151f1108a11b7804423cada6e649d5", 16));
//        G2Point g22 = new G2Point(g22x, g22y);
//        GtPoint gt = Pairing.myPairing(g11,g21);
//        GtPoint gt_1 = Pairing.myPairing(g12,g22);
//        System.out.println(gt.toString());
//        System.out.println(gt_1.toString());
//        System.out.println(gt.equals(gt_1));



        BigInteger a = new BigInteger("21888269820417ee499bf7355683168513275934010ab12ad86575",16);
        BigInteger b = a.multiply(new BigInteger("4057135944570762861f7ee499bf70ab12ade332328294",16));

        Fp g1x = new Fp(new BigInteger("1e462d01d1861f7ee499bf70ab12ade335d98586b52db847ee2ec1e790170e04", 16));
        Fp g1y = new Fp(new BigInteger("14bd807f4e64904b29e874fd824ff16e465b5798b19aafe0cae60a2dbcf91333", 16));
        G1Point g1 = new G1Point(g1x, g1y);


        Fp2 g2x = new Fp2(new BigInteger("10857046999023057135944570762232829481370756359578518086990519993285655852781", 10),
                new BigInteger("11559732032986387107991004021392285783925812861821192530917403151452391805634", 10));
        Fp2 g2y = new Fp2(new BigInteger("8495653923123431417604973247489272438418190587263600148770280649306958101930", 10),
                new BigInteger("4082367875863433681332203403145435568316851327593401208105741076214120093531", 10));
        G2Point g2 = new G2Point(g2x, g2y);

        BigInteger filed = new BigInteger("21888242871839275222246405745257275088548364400416034343698204186575808495617");

        BigInteger ab_1 = a.multiply(b).negate().mod(filed);

        G1Point g1_a = G1.mul(g1,a);
        G2Point g2_b = G2.ECTwistMul(g2,b);
        G1Point g1_ab = G1.mul(g1,ab_1);
        boolean r = Pairing.pairingProd2(g1_a, g2_b, g1_ab, g2);
        System.out.println(r);

        System.out.println(g1_a);
        System.out.println(g2_b);
        System.out.println(g1_ab);
        System.out.println(g2);


//        Fp g1x1 = new Fp(new BigInteger("3010198690406615200373504922352659861758983907867017329644089018310584441462", 10));
//        Fp g1y1 = new Fp(new BigInteger("17861058253836152797273815394432013122766662423622084931972383889279925210507", 10));
//        G1Point g11 = new G1Point(g1x1, g1y1);
//
//
//        Fp2 g2x1 = new Fp2(new BigInteger("2725019753478801796453339367788033689375851816420509565303521482350756874229", 10),
//                new BigInteger("7273165102799931111715871471550377909735733521218303035754523677688038059653", 10));
//        Fp2 g2y1 = new Fp2(new BigInteger("2512659008974376214222774206987427162027254181373325676825515531566330959255", 10),
//                new BigInteger("957874124722006818841961785324909313781880061366718538693995380805373202866", 10));
//        G2Point g21 = new G2Point(g2x1, g2y1);
//
//        Fp g1x2 = new Fp(new BigInteger("4503322228978077916651710446042370109107355802721800704639343137502100212473", 10));
//        Fp g1y2 = new Fp(new BigInteger("6132642251294427119375180147349983541569387941788025780665104001559216576968", 10));
//        G1Point g12 = new G1Point(g1x2, g1y2);
//
//        Fp2 g2x2 = new Fp2(new BigInteger("18029695676650738226693292988307914797657423701064905010927197838374790804409", 10),
//                new BigInteger("14583779054894525174450323658765874724019480979794335525732096752006891875705", 10));
//        Fp2 g2y2 = new Fp2(new BigInteger("2140229616977736810657479771656733941598412651537078903776637920509952744750", 10),
//                new BigInteger("11474861747383700316476719153975578001603231366361248090558603872215261634898", 10));
//        G2Point g22 = new G2Point(g2x2, g2y2);
//
//        boolean b = Pairing.pairingProd2(g11,g21,g12,g22);
//        System.out.println(b);
    }
}
