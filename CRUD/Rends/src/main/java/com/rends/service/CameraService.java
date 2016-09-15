package com.rends.service;

import com.rends.domain.CameraEntity;
import com.rends.domain.EstabelecimentoEntity;
import com.rends.domain.ImagemCameraEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class CameraService extends BaseService<CameraEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public CameraService(){
        super(CameraEntity.class);
    }
    
    @Transactional
    public List<CameraEntity> findAllCameraEntities() {
        
        return entityManager.createQuery("SELECT o FROM Camera o ", CameraEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Camera o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(CameraEntity camera) {

        /* This is called before a Camera is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<CameraEntity> findAvailableCameras(EstabelecimentoEntity estabelecimento) {
        return entityManager.createQuery("SELECT o FROM Camera o where o.id not in (select o.id from Camera o join o.estabelecimentos p where p = :p)", CameraEntity.class).setParameter("p", estabelecimento).getResultList();
    }

    @Transactional
    public List<CameraEntity> findCamerasByEstabelecimento(EstabelecimentoEntity estabelecimento) {
        return entityManager.createQuery("SELECT o FROM Camera o where o.id in (select o.id from Camera o join o.estabelecimentos p where p = :p)", CameraEntity.class).setParameter("p", estabelecimento).getResultList();
    }

    @Transactional
    public List<CameraEntity> findAvailableCameras(ImagemCameraEntity imagemCamera) {
        return entityManager.createQuery("SELECT o FROM Camera o where o.id not in (select o.id from Camera o join o.imagemCameras p where p = :p)", CameraEntity.class).setParameter("p", imagemCamera).getResultList();
    }

    @Transactional
    public List<CameraEntity> findCamerasByImagemCamera(ImagemCameraEntity imagemCamera) {
        return entityManager.createQuery("SELECT o FROM Camera o where o.id in (select o.id from Camera o join o.imagemCameras p where p = :p)", CameraEntity.class).setParameter("p", imagemCamera).getResultList();
    }

    @Transactional
    public CameraEntity fetchEstabelecimentos(CameraEntity camera) {
        camera = find(camera.getId());
        camera.getEstabelecimentos().size();
        return camera;
    }
    
}
