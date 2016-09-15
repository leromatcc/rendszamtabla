package com.rends.service;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.AutomovelTipoEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class AutomovelTipoService extends BaseService<AutomovelTipoEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public AutomovelTipoService(){
        super(AutomovelTipoEntity.class);
    }
    
    @Transactional
    public List<AutomovelTipoEntity> findAllAutomovelTipoEntities() {
        
        return entityManager.createQuery("SELECT o FROM AutomovelTipo o ", AutomovelTipoEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM AutomovelTipo o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(AutomovelTipoEntity automovelTipo) {

        /* This is called before a AutomovelTipo is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<AutomovelTipoEntity> findAvailableAutomovelTipos(AutomovelEntity automovel) {
        return entityManager.createQuery("SELECT o FROM AutomovelTipo o where o.id not in (select o.id from AutomovelTipo o join o.automovels p where p = :p)", AutomovelTipoEntity.class).setParameter("p", automovel).getResultList();
    }

    @Transactional
    public List<AutomovelTipoEntity> findAutomovelTiposByAutomovel(AutomovelEntity automovel) {
        return entityManager.createQuery("SELECT o FROM AutomovelTipo o where o.id in (select o.id from AutomovelTipo o join o.automovels p where p = :p)", AutomovelTipoEntity.class).setParameter("p", automovel).getResultList();
    }

}
