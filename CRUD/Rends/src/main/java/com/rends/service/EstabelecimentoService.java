package com.rends.service;

import com.rends.domain.EstabelecimentoEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class EstabelecimentoService extends BaseService<EstabelecimentoEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public EstabelecimentoService(){
        super(EstabelecimentoEntity.class);
    }
    
    @Transactional
    public List<EstabelecimentoEntity> findAllEstabelecimentoEntities() {
        
        return entityManager.createQuery("SELECT o FROM Estabelecimento o ", EstabelecimentoEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Estabelecimento o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(EstabelecimentoEntity estabelecimento) {

        /* This is called before a Estabelecimento is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

}
