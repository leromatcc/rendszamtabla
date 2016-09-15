package com.rends.service;

import com.rends.domain.ImagensAttachment;
import com.rends.domain.ImagensEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class ImagensAttachmentService extends BaseService<ImagensAttachment> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public ImagensAttachmentService(){
        super(ImagensAttachment.class);
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM ImagensAttachment o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAttachmentsByImagens(ImagensEntity imagens) {
        entityManager
                .createQuery("DELETE FROM ImagensAttachment c WHERE c.imagens = :p")
                .setParameter("p", imagens).executeUpdate();
    }
    
    @Transactional
    public List<ImagensAttachment> getAttachmentsList(ImagensEntity imagens) {
        if (imagens == null || imagens.getId() == null) {
            return new ArrayList<>();
        }
        // The byte streams are not loaded from database with following line. This would cost too much.
        return entityManager.createQuery("SELECT NEW com.rends.domain.ImagensAttachment(o.id, o.fileName) FROM ImagensAttachment o WHERE o.imagens.id = :id", ImagensAttachment.class).setParameter("id", imagens.getId()).getResultList();
    }
}
