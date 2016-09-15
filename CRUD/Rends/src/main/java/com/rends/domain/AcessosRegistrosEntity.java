package com.rends.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name="AcessosRegistros")
@Table(name="\"ACESSOSREGISTROS\"")
public class AcessosRegistrosEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name="\"datahora\"")
    @Temporal(TemporalType.DATE)
    @NotNull
    private Date datahora;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="ACESSOSREGISTROS_ESTACIONAMENTOS",
              joinColumns={@JoinColumn(name="ACESSOSREGISTROS_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="ESTACIONAMENTO_ID", referencedColumnName="ID")})
    private List<EstacionamentoEntity> estacionamentos;

    @Size(max = 1000)
    @Column(length = 1000, name="\"placaAnalisar\"")
    @NotNull
    private String placaAnalisar;

    @Column(name="\"acessoAutorizado\"")
    private Boolean acessoAutorizado;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="AUTOMOVEL_ID", nullable=true)
    private AutomovelEntity automovel;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="ACESSOSREGISTROS_PERMISSAOACESSOS",
              joinColumns={@JoinColumn(name="ACESSOSREGISTROS_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="PERMISSAOACESSO_ID", referencedColumnName="ID")})
    private List<AcessosPermissaoEntity> permissaoAcessos;

    public Date getDatahora() {
        return this.datahora;
    }

    public void setDatahora(Date datahora) {
        this.datahora = datahora;
    }

    public List<EstacionamentoEntity> getEstacionamentos() {
        return estacionamentos;
    }

    public void setEstacionamentos(List<EstacionamentoEntity> estacionamentos) {
        this.estacionamentos = estacionamentos;
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

    public List<AcessosPermissaoEntity> getPermissaoAcessos() {
        return permissaoAcessos;
    }

    public void setPermissaoAcessos(List<AcessosPermissaoEntity> acessosPermissaos) {
        this.permissaoAcessos = acessosPermissaos;
    }

}
