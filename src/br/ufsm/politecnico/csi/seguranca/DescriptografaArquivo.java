package br.ufsm.politecnico.csi.seguranca;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

public class DescriptografaArquivo {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

        //1. Selecionar o arquivo
        System.out.println("-> Selecionar arquivo.");
        JFileChooser chooserArquivo = new JFileChooser();
        int escolha = chooserArquivo.showOpenDialog(new JFrame());
        if (escolha != JFileChooser.APPROVE_OPTION) {
            return;
        }

        //2. Ler o arquivo
        System.out.println("-> Lendo o arquivo...");
        File arquivo = new File(chooserArquivo.getSelectedFile().getAbsolutePath());
        FileInputStream fin = new FileInputStream(arquivo);
        byte[] bcripto = new byte[(int) fin.getChannel().size()];
        fin.read(bcripto);

        System.out.println("Lendo a chave");
        FileInputStream inChave = new FileInputStream(
                chooserArquivo.getSelectedFile().getAbsolutePath().replaceAll(".cripto", ".key"));
        byte[] bchave = new byte[(int) inChave.getChannel().size()];
        inChave.read(bchave);
        SecretKeySpec ks = new SecretKeySpec(bchave, "AES");

        //4. Criar o desencriptador
        System.out.println("-> Criar o encriptador");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, ks);

        //5. Criptografar
        System.out.println("-> Inicio criptografia");
        byte[] b_arquivo = cipher.doFinal(bcripto);

        //6. Escrever o arquivo
        System.out.println("-> Escrevendo o arquivo criptografado.");
        File saida = new File(chooserArquivo.getSelectedFile().getAbsolutePath().replaceAll(".cripto", "") + ".new");
        OutputStream fout = new FileOutputStream(saida);
        fout.write(b_arquivo);
        fout.close();
        System.out.println("** Success **");

        System.exit(0);
    }

}
