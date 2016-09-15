package com.rends.service;

import com.rends.domain.EstabelecimentoVisitaEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class EstabelecimentoVisitaService extends BaseService<EstabelecimentoVisitaEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public EstabelecimentoVisitaService(){
        super(EstabelecimentoVisitaEntity.class);
    }
    
    @Transactional
    public List<EstabelecimentoVisitaEntity> findAllEstabelecimentoVisitaEntities() {
        
        return entityManager.createQuery("SELECT o FROM EstabelecimentoVisita o ", EstabelecimentoVisitaEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM EstabelecimentoVisita o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(EstabelecimentoVisitaEntity estabelecimentoVisita) {

        /* This is called before a EstabelecimentoVisita is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

}
