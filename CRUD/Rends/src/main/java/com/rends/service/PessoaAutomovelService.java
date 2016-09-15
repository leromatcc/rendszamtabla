package com.rends.service;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.PessoaAutomovelEntity;
import com.rends.service.AutomovelService;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class PessoaAutomovelService extends BaseService<PessoaAutomovelEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public PessoaAutomovelService(){
        super(PessoaAutomovelEntity.class);
    }
    
    @Transactional
    public List<PessoaAutomovelEntity> findAllPessoaAutomovelEntities() {
        
        return entityManager.createQuery("SELECT o FROM PessoaAutomovel o ", PessoaAutomovelEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM PessoaAutomovel o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(PessoaAutomovelEntity pessoaAutomovel) {

        /* This is called before a PessoaAutomovel is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<PessoaAutomovelEntity> findAvailablePessoaAutomovels(AutomovelEntity automovel) {
        return entityManager.createQuery("SELECT o FROM PessoaAutomovel o where o.id not in (select o.id from PessoaAutomovel o join o.automovels p where p = :p)", PessoaAutomovelEntity.class).setParameter("p", automovel).getResultList();
    }

    @Transactional
    public List<PessoaAutomovelEntity> findPessoaAutomovelsByAutomovel(AutomovelEntity automovel) {
        return entityManager.createQuery("SELECT o FROM PessoaAutomovel o where o.id in (select o.id from PessoaAutomovel o join o.automovels p where p = :p)", PessoaAutomovelEntity.class).setParameter("p", automovel).getResultList();
    }

    @Transactional
    public PessoaAutomovelEntity fetchAutomovels(PessoaAutomovelEntity pessoaAutomovel) {
        pessoaAutomovel = find(pessoaAutomovel.getId());
        pessoaAutomovel.getAutomovels().size();
        return pessoaAutomovel;
    }
    
}
