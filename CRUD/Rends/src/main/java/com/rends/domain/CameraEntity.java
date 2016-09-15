package com.rends.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity(name="Camera")
@Table(name="\"CAMERA\"")
public class CameraEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 1000)
    @Column(length = 1000, name="\"enderecoRede\"")
    private String enderecoRede;

    @Size(max = 100)
    @Column(length = 100, name="\"descricao\"")
    private String descricao;

    @ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinTable(name="CAMERA_ESTABELECIMENTOS",
              joinColumns={@JoinColumn(name="CAMERA_ID", referencedColumnName="ID")},
              inverseJoinColumns={@JoinColumn(name="ESTABELECIMENTO_ID", referencedColumnName="ID")})
    private List<EmpresaEntity> estabelecimentos;

    public void setImagemCameras(List<ImagemCameraEntity> imagemCameras) {
        this.imagemCameras = imagemCameras;
    }

    public List<ImagemCameraEntity> getImagemCameras() {
        return imagemCameras;
    }

    @ManyToMany(mappedBy="cameras", fetch=FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ImagemCameraEntity> imagemCameras;

    public String getEnderecoRede() {
        return this.enderecoRede;
    }

    public void setEnderecoRede(String enderecoRede) {
        this.enderecoRede = enderecoRede;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<EmpresaEntity> getEstabelecimentos() {
        return estabelecimentos;
    }

    public void setEstabelecimentos(List<EmpresaEntity> empresas) {
        this.estabelecimentos = empresas;
    }

}
