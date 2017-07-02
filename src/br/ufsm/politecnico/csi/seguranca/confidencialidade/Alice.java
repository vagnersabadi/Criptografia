package br.ufsm.politecnico.csi.seguranca.confidencialidade;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


public class Alice {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        //1. Abrindo Socket
        ServerSocket ss = new ServerSocket(3333);
        System.out.println("1. Socket aberto");

        //2. Gera par chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        System.out.println("2. Par de chaves gerado.");

        while (true) {
            System.out.println("3. Aguardando conexões...");
            Socket s = ss.accept();
            System.out.println("    3.1 Cliente conectado.");

            //4. Enviando a chave publica
            ObjetoTroca obj = new ObjetoTroca();
            obj.setChavePublica(kp.getPublic());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(obj);
            out.flush();
            System.out.println("4. Chave publica enviada.");

            //5. Lendo o arquivo do socket
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjetoTroca objetoTroca = (ObjetoTroca) in.readObject();
            System.out.println("5. Arquivo recebido.");

            //6. Criar o desencriptador
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());

            //6.Desifrar chave da sessao usando chave privada alice
            byte[] c_sessao= cipher.doFinal(objetoTroca.getChaveSessao());
            SecretKeySpec ks = new SecretKeySpec(c_sessao,"AES");
            System.out.println("5. Descriptografou chave de sessão.");

            //7. Criar o desencriptador
            Cipher cipherAES = Cipher.getInstance("AES");
            cipherAES.init(Cipher.DECRYPT_MODE,ks);

            //7. Desifrar arquivo com chave de sessao
            byte[] b_arquivo = cipherAES.doFinal(objetoTroca.getArquivo());
            System.out.println("7. Descriptografou o arquivo.");

            //8. Fechar conexão
            System.out.println("8. Conexão fechada.");
            s.close();

            //9. Escrever o arquivo
            File saida = new File(objetoTroca.getNomeArquivo());
            OutputStream fout = new FileOutputStream(saida);
            fout.write(b_arquivo);
            fout.close();
            System.out.println("9. Arquivo gravado\n");
        }

    }

}
