package com.rends.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="Estabelecimento")
@Table(name="\"ESTABELECIMENTO\"")
public class EstabelecimentoEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Column(length = 50, name="\"descricao\"")
    private String descricao;

    public void setCameras(List<CameraEntity> cameras) {
        this.cameras = cameras;
    }

    public List<CameraEntity> getCameras() {
        return cameras;
    }

    @ManyToMany(mappedBy="estabelecimentos", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<CameraEntity> cameras;

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
