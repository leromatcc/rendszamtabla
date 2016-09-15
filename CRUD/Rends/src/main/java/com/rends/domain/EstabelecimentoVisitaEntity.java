package com.rends.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name="EstabelecimentoVisita")
@Table(name="\"ESTABELECIMENTOVISITA\"")
public class EstabelecimentoVisitaEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="\"datahora\"")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date datahora;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="ESTABELECIMENTO_ID", nullable=true)
    private EstabelecimentoEntity estabelecimento;

    @Size(max = 1000)
    @Column(length = 1000, name="\"placaAnalisar\"")
    @NotNull
    private String placaAnalisar;

    @Column(name="\"acessoAutorizado\"")
    private Boolean acessoAutorizado;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="AUTOMOVEL_ID", nullable=true)
    private AutomovelEntity automovel;

    public Date getDatahora() {
        return this.datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public EstabelecimentoEntity getEstabelecimento() {
        return this.estabelecimento;
    }

    public void setEstabelecimento(EstabelecimentoEntity estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public String getPlacaAnalisar() {
        return this.placaAnalisar;
    }

    public void setPlacaAnalisar(String placaAnalisar) {
        this.placaAnalisar = placaAnalisar;
    }

    public Boolean getAcessoAutorizado() {
        return this.acessoAutorizado;
    }

    public void setAcessoAutorizado(Boolean acessoAutorizado) {
        this.acessoAutorizado = acessoAutorizado;
    }

    public AutomovelEntity getAutomovel() {
        return this.automovel;
    }

    public void setAutomovel(AutomovelEntity automovel) {
        this.automovel = automovel;
    }

}
