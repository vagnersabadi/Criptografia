package br.ufsm.politecnico.csi.seguranca.integridade;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.*;

/**
 * Created by cpol on 10/04/2017.
 */
public class Bob{
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

        //3. Criar hash do Arquivo
        MessageDigest md = null;
        md = MessageDigest.getInstance("SHA-1");
        byte[] arquivoHash = md.digest(barquivo);

        //4. Gera par chaves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair kp = keyGen.generateKeyPair();
        System.out.println("4. Par de chaves gerado.");

        //5. Criar o encriptador e criptografar o arquivo
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, kp.getPrivate());
        System.out.println("6. Criou o encriptador.");

        //5. Criptografar
        System.out.println("    6.1.  Iniciando criptografia arquivo...");
        byte[] assinatura_cripto = cipher.doFinal(arquivoHash);
        System.out.println("    6.2.  Criptografou o arquivo.");

        //7. Conectar à Alice
        Socket s = new Socket("localhost", 3333);
        System.out.println("3. Conectou a Alice.");

        //8. Enviando dados
        ObjetoTroca obj = new ObjetoTroca();
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        obj.setChavePublica(kp.getPublic());
        obj.setNomeArquivo(chooserArquivo.getSelectedFile().getName());
        obj.setArquivo(barquivo);
        obj.setAssinatura(assinatura_cripto);
        out.writeObject(obj);
        out.close();
        s.close();
        System.out.println("8. Envio concluído, conexão fechada!");





    }
}
