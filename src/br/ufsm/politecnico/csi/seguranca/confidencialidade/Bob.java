package br.ufsm.politecnico.csi.seguranca.confidencialidade;

import javax.crypto.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

        //3. Conectar à Alice
        Socket s = new Socket("localhost", 3333);
        System.out.println("3. Conectou a Alice.");

        //4. Receber a chave pública
        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
        ObjetoTroca obj = (ObjetoTroca) in.readObject();
        System.out.println("4. Recebeu a chave publica.");

        //5. Cria chave de sessão
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128);
        SecretKey aeskey = kgen.generateKey();
        byte[] chave = aeskey.getEncoded();
        System.out.println("5. Criou a Chave de sessão.");

        //6. Criar o encriptador e criptografar o arquivo
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aeskey);
        System.out.println("6. Criou o encriptador.");

        //6. Criptografar
        System.out.println("    6.1.  Iniciando criptografia arquivo...");
        byte[] b_cripto = cipher.doFinal(barquivo);
        System.out.println("    6.2.  Criptografou o arquivo.");

        //7. Criar o encriptador e criptografar a chave de sessão
        Cipher cipherRSA = Cipher.getInstance("RSA");
        cipherRSA.init(Cipher.ENCRYPT_MODE, obj.getChavePublica());
        System.out.println("7. Criou o encriptador para chave de sessão.");

        //7. Criptografar chave sessao com chave publica
        System.out.println("    7.1.  Iniciando criptografia sessão...");
        byte[] c_cripto = cipherRSA.doFinal(aeskey.getEncoded());
        System.out.println("    7.2.  Criptografou a chave de sessão.");

        //8. Enviar o arquivo para Alice
        System.out.println("8. Enviando o arquivo criptografado.");
        ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
        obj = new ObjetoTroca();
        obj.setChaveSessao(c_cripto);
        obj.setArquivo(b_cripto);
        obj.setNomeArquivo(chooserArquivo.getSelectedFile().getName());
        out.writeObject(obj);
        out.close();
        s.close();
        System.out.println("9. Envio concluído, conexão fechada!");


        System.exit(0);

    }

}
