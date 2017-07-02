package br.ufsm.politecnico.csi.seguranca.integridade_confidencialidade;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * Created by cpol on 12/04/2017.
 */
public class ObjetoTroca implements Serializable {
    private PublicKey chavePublica;
    private String nomeArquivo;
    private byte[] arquivo;
    private byte[] chaveSessao;
    private byte[] assinatura;

    public PublicKey getChavePublica() {
        return chavePublica;
    }

    public void setChavePublica(PublicKey chavePublica) {
        this.chavePublica = chavePublica;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public byte[] getArquivo() {
        return arquivo;
    }

    public void setArquivo(byte[] arquivo) {
        this.arquivo = arquivo;
    }

    public byte[] getChaveSessao() {
        return chaveSessao;
    }

    public void setChaveSessao(byte[] chaveSessao) {
        this.chaveSessao = chaveSessao;
    }

    public byte[] getAssinatura() {
        return assinatura;
    }

    public void setAssinatura(byte[] assinatura) {
        this.assinatura = assinatura;
    }
}
