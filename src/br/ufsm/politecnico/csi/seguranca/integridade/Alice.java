package br.ufsm.politecnico.csi.seguranca.integridade;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by cpol on 10/04/2017.
 */
public class Alice{

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        //1. Abrindo Socket
        ServerSocket ss = new ServerSocket(3333);
        System.out.println("1. Socket aberto");

        while (true)
        {
            System.out.println("2. Aguardando conexões...");
            Socket s = ss.accept();
            System.out.println("    2.1 Cliente conectado.");

            //3. Lendo o arquivo do socket
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjetoTroca objetoTroca = (ObjetoTroca) in.readObject();
            System.out.println("3. Dados recebido.");

            //4. Criar hash do Arquivo
            MessageDigest md = null;
            md = MessageDigest.getInstance("SHA-1");
            byte[] arquivoHashAlice = md.digest(objetoTroca.getArquivo());
            System.out.println("4. Hash criado.");

            //5. Criar o desencriptador
            Cipher cipherAES = Cipher.getInstance("RSA");
            cipherAES.init(Cipher.DECRYPT_MODE,objetoTroca.getChavePublica());

            //6. Desifrar arquivo com chave de sessao
            byte[] arquivoHashBob = cipherAES.doFinal(objetoTroca.getAssinatura());
            System.out.println("7. Descriptografou a assinatura.");

            //9. Fechar conexão
            System.out.println("9. Conexão fechada.");
            s.close();

            //10. Compara
            if(Arrays.equals(arquivoHashAlice,arquivoHashBob))
            {
                //9. Escrever o arquivo
                File saida = new File(objetoTroca.getNomeArquivo());
                OutputStream fout = new FileOutputStream(saida);
                fout.write(objetoTroca.getArquivo());
                fout.close();
                System.out.println("10. Arquivo gravado\n");
            }
            else {
                System.out.println("ERRO! Assinatura Invalida\n");

            }


        }
    }
}
