package br.ufsm.politecnico.csi.seguranca.integridade_confidencialidade;

import javax.crypto.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.*;

/**
 * Created by cpol on 12/04/2017.
 */
public class Bob {
    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, ClassNotFoundException {

        //1. Selecionar o arquivo
        JFileChooser chooserArquivo = new JFileChooser();
        int escolha = chooserArquivo.showOpenDialog(new JFrame());
        if (escolha != JFileChooser.APPROVE_OPTION) {
            return;
        }
        System.out.println("1. Selecionou arquivo.");

        //2. Ler o arquivo
        File arquivo = new File(chooserArquivo.getSelectedFile().getAbsolutePath());
        FileInputStream fin = new FileInputStream(arquivo);
        byte[] barquivo = new byte[(int) fin.getChannel().size()];
        fin.read(barquivo);
        System.out.println("2. Leu o arquivo.");

        //3. Gera par chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        System.out.println("3. Par de chaves gerado.");

        //4. Criar hash do Arquivo
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-1");
        byte[] arquivoHash = md.digest(barquivo);
        System.out.println("4. Hash do Arquivo Criado.");

        //5. Conectar à Alice
        Socket s = new Socket("localhost", 3333);
        System.out.println("5. Conectou a Alice.");

        //5. Receber a chave pública Alice
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        ObjetoTroca obj = (ObjetoTroca) in.readObject();
        System.out.println("5. Recebeu a chave pública.");

        //6. Criar o encriptador e criptografar o hash com chave privada Bob
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, kp.getPrivate());
        byte[] assinatura_cripto = cipher.doFinal(arquivoHash);
        System.out.println("    6.2.  Criptografou o Hash.");

        //7. Cria chave de sessão para arquivo
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey aeskey = kgen.generateKey();
        byte[] chave_sessao = aeskey.getEncoded();
        System.out.println("7. Criou a Chave de sessão.");

        //8. Criar o encriptador e criptografar arquivo com chave de sessão
        Cipher cipher_arquivo = Cipher.getInstance("AES");
        cipher_arquivo.init(Cipher.ENCRYPT_MODE, aeskey);
        byte[] arquivo_cripto = cipher_arquivo.doFinal(barquivo);
        System.out.println("8. Criptografou o arquivo.");

        //9. Criar o encriptador e criptografar ch sessao com ch publica Alice
        Cipher cipher_sessao = Cipher.getInstance("RSA");
        cipher_sessao.init(Cipher.ENCRYPT_MODE, obj.getChavePublica());
        byte[] sessao_cripto = cipher_sessao.doFinal(aeskey.getEncoded());
        System.out.println("9. Criptografou chave de sessão.");



        //10. Envia para Alice(Arquivo,ch sessão,hash(TodosCripto)e chave publica
        System.out.println("10. Enviando o arquivo criptografado.");
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        obj = new ObjetoTroca();
        obj.setChaveSessao(sessao_cripto);
        obj.setArquivo(arquivo_cripto);
        obj.setChavePublica(kp.getPublic());
        obj.setAssinatura(assinatura_cripto);
        obj.setNomeArquivo(chooserArquivo.getSelectedFile().getName());
        out.writeObject(obj);
        out.close();

        //11. Fecha Conexão
        s.close();
        System.out.println("11. Envio concluído, conexão fechada!");

    }
}
