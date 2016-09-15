package com.rends.domain;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name="ImagensAttachment")
public class ImagensAttachment extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public ImagensAttachment() {
        super();
    }
    
    public ImagensAttachment(Long id, String fileName) {
        this.setId(id);
        this.fileName = fileName;
    }

    @Size(max = 200)
    private String fileName;
    
    @ManyToOne
    @JoinColumn(name = "IMAGENS_ID", referencedColumnName = "ID")
    private ImagensEntity imagens;

    @Lob
    private byte[] content;

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
    
    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ImagensEntity getImagens() {
        return this.imagens;
    }

    public void setImagens(ImagensEntity imagens) {
        this.imagens = imagens;
    }
}
