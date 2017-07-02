package br.ufsm.politecnico.csi.seguranca.integridade_confidencialidade;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

/**
 * Created by cpol on 12/04/2017.
 */
public class Alice {
    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        //1. Abrindo Socket
        ServerSocket ss = new ServerSocket(3333);
        System.out.println("1. Socket aberto");

        // 2. Gera par de chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        System.out.println("2. Par de chaves gerado.");

        //3. Aguarda Conexão
        while (true) {
            System.out.println("3. Aguardando conexões...");
            Socket s = ss.accept();
            System.out.println("    3.1 Cliente conectado.");

            //4. Enviar chave pública
            ObjetoTroca obj = new ObjetoTroca();
            obj.setChavePublica(kp.getPublic());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(obj);
            out.flush();
            System.out.println("4. Chave publica enviada.");

            //5. Recebe dados de Bob
            ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjetoTroca objetoTroca = (ObjetoTroca) in.readObject();
            System.out.println("5. Dados recebidos.");

            //6. Descriptografar hash com chave publica Bob
            Cipher cipherHASH = Cipher.getInstance("RSA");
            cipherHASH.init(Cipher.DECRYPT_MODE,objetoTroca.getChavePublica());
            byte[] arquivoHashBob = cipherHASH.doFinal(objetoTroca.getAssinatura());
            System.out.println("6. Descriptografou a Assinatura(Hash).");

            //7.Desifrar chave da sessao usando chave privada alice
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
            byte[] c_sessao= cipher.doFinal(objetoTroca.getChaveSessao());
            SecretKeySpec ks = new SecretKeySpec(c_sessao,"AES");
            System.out.println("7. Descriptografou chave de sessão.");

            //8. Descriptografar arquivo com chave de sessão do Bob
            Cipher cipher_arquivo = Cipher.getInstance("AES");
            cipher_arquivo.init(Cipher.DECRYPT_MODE,ks);
            byte[] b_arquivo = cipher_arquivo.doFinal(objetoTroca.getArquivo());
            System.out.println("8. Descriptografou o arquivo.");

            //9. Criar hash do Arquivo recebido de Bob
            MessageDigest md = null;
            md = MessageDigest.getInstance("SHA-1");
            byte[] arquivoHash = md.digest(b_arquivo);
            System.out.println("9. Hash do Arquivo criado.");

            //10. Compara os hash verificar integridade
            System.out.println("10. Comparando Hashs.");
            if(Arrays.equals(arquivoHash,arquivoHashBob))
            {
                System.out.println("    10.1 Hashs Iguais.");
                //11. Escrever o arquivo
                File saida = new File(objetoTroca.getNomeArquivo());
                OutputStream fout = new FileOutputStream(saida);
                fout.write(objetoTroca.getArquivo());
                fout.close();
                System.out.println("11. Arquivo gravado");
            }
            else {
                System.out.println("ERRO! Hashs Invalida.");

            }

            //12. Fechar conexão
            System.out.println("12. Conexão fechada.\n");
            s.close();


        }
    }
}
