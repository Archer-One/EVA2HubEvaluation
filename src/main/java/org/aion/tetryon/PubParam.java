package org.aion.tetryon;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Properties;

public class PubParam {

    public G1Point G1_g1;
    public G1Point G1_g2;
    public G1Point G1_h1;
    public G1Point G1_h2;
    public G2Point G2_g1;

    public PubParam() throws Exception {
        Properties prop = new Properties();

        //初始化配置文件
        String configFile = "src/main/java/config.properties";
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(configFile));

            prop.load(dataInputStream);
        } catch (IOException e) {
            System.out.println("Can't load config file");
        }


        //初始化公共生成元h
        G1_g1 = Util.deserializeG1(Base64.getDecoder().decode(prop.getProperty("G1_POINT")));
        BigInteger r1 =  Util.getRandom();
        BigInteger r2 =  Util.getRandom();
        BigInteger r3 =  Util.getRandom();
         G1_g2 = G1.mul(G1_g1, r1);
         G1_h1 = G1.mul(G1_g1, r2);
         G1_h2 = G1.mul(G1_g1, r3);


         G2_g1 = Util.deserializeG2(Base64.getDecoder().decode(prop.getProperty("H_POINT")));
    }

    public static void main(String[] args) throws Exception {
        Properties prop = new Properties();

        //初始化配置文件
        String configFile = "src/main/java/config.properties";
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(configFile));
            
            prop.load(dataInputStream);
        } catch (IOException e) {
            System.out.println("Can't load config file");
        }


        //初始化公共生成元h
        G1Point G1_g1 = Util.deserializeG1(Base64.getDecoder().decode(prop.getProperty("G_POINT")));
        BigInteger r1 =  Util.getRandom();
        BigInteger r2 =  Util.getRandom();
        BigInteger r3 =  Util.getRandom();
        G1Point G1_g2 = G1.mul(G1_g1, r1);
        G1Point G1_h1 = G1.mul(G1_g1, r2);
        G1Point G1_h2 = G1.mul(G1_g1, r3);


        G2Point G2_g1 = Util.deserializeG2(Base64.getDecoder().decode(prop.getProperty("H_POINT")));
        System.out.println("hPoint = " + G2_g1);
    }

}
