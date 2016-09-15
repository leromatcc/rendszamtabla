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
import javax.validation.constraints.Size;

@Entity(name="PermissaoEstabelecimentoPessoa")
@Table(name="\"PERMISSAOESTABELECIMENTOPESSOA\"")
public class PermissaoEstabelecimentoPessoaEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="PERMISSAOESTABELECIMENTOPESSOA_PESSOAS",
              joinColumns={@JoinColumn(name="PERMISSAOESTABELECIMENTOPESSOA_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="PESSOA_ID", referencedColumnName="ID")})
    private List<PessoaEntity> pessoas;

    @OneToOne(optional=true, cascade=CascadeType.DETACH)
    @JoinColumn(name="ESTABELECIMENTO_ID", nullable=true)
    private EstabelecimentoEntity estabelecimento;

    @Column(name="\"dataInicio\"")
    @Temporal(TemporalType.DATE)
    private Date dataInicio;

    @Column(name="\"dataFinal\"")
    @Temporal(TemporalType.DATE)
    private Date dataFinal;

    @Column(name="\"ativo\"")
    private Boolean ativo;

    public void setEstabelecimentoVisitas(List<EstabelecimentoVisitaEntity> estabelecimentoVisitas) {
        this.estabelecimentoVisitas = estabelecimentoVisitas;
    }

    public List<EstabelecimentoVisitaEntity> getEstabelecimentoVisitas() {
        return estabelecimentoVisitas;
    }

    @ManyToMany(mappedBy="permissaoAcessos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<EstabelecimentoVisitaEntity> estabelecimentoVisitas;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<PessoaEntity> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaEntity> pessoas) {
        this.pessoas = pessoas;
    }

    public EstabelecimentoEntity getEstabelecimento() {
        return this.estabelecimento;
    }

    public void setEstabelecimento(EstabelecimentoEntity estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public Date getDataInicio() {
        return this.dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFinal() {
        return this.dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Boolean getAtivo() {
        return this.ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

}
