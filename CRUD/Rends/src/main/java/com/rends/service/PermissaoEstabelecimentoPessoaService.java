package com.rends.service;

import com.rends.domain.EstabelecimentoVisitaEntity;
import com.rends.domain.PermissaoEstabelecimentoPessoaEntity;
import com.rends.domain.PessoaEntity;
import com.rends.service.PessoaService;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class PermissaoEstabelecimentoPessoaService extends BaseService<PermissaoEstabelecimentoPessoaEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public PermissaoEstabelecimentoPessoaService(){
        super(PermissaoEstabelecimentoPessoaEntity.class);
    }
    
    @Transactional
    public List<PermissaoEstabelecimentoPessoaEntity> findAllPermissaoEstabelecimentoPessoaEntities() {
        
        return entityManager.createQuery("SELECT o FROM PermissaoEstabelecimentoPessoa o ", PermissaoEstabelecimentoPessoaEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM PermissaoEstabelecimentoPessoa o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa) {

        /* This is called before a PermissaoEstabelecimentoPessoa is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<PermissaoEstabelecimentoPessoaEntity> findAvailablePermissaoEstabelecimentoPessoas(PessoaEntity pessoa) {
        return entityManager.createQuery("SELECT o FROM PermissaoEstabelecimentoPessoa o where o.id not in (select o.id from PermissaoEstabelecimentoPessoa o join o.pessoas p where p = :p)", PermissaoEstabelecimentoPessoaEntity.class).setParameter("p", pessoa).getResultList();
    }

    @Transactional
    public List<PermissaoEstabelecimentoPessoaEntity> findPermissaoEstabelecimentoPessoasByPessoa(PessoaEntity pessoa) {
        return entityManager.createQuery("SELECT o FROM PermissaoEstabelecimentoPessoa o where o.id in (select o.id from PermissaoEstabelecimentoPessoa o join o.pessoas p where p = :p)", PermissaoEstabelecimentoPessoaEntity.class).setParameter("p", pessoa).getResultList();
    }

    @Transactional
    public List<PermissaoEstabelecimentoPessoaEntity> findAvailablePermissaoAcessos(EstabelecimentoVisitaEntity estabelecimentoVisita) {
        return entityManager.createQuery("SELECT o FROM PermissaoEstabelecimentoPessoa o where o.id not in (select o.id from PermissaoEstabelecimentoPessoa o join o.estabelecimentoVisitas p where p = :p)", PermissaoEstabelecimentoPessoaEntity.class).setParameter("p", estabelecimentoVisita).getResultList();
    }

    @Transactional
    public List<PermissaoEstabelecimentoPessoaEntity> findPermissaoAcessosByEstabelecimentoVisita(EstabelecimentoVisitaEntity estabelecimentoVisita) {
        return entityManager.createQuery("SELECT o FROM PermissaoEstabelecimentoPessoa o where o.id in (select o.id from PermissaoEstabelecimentoPessoa o join o.estabelecimentoVisitas p where p = :p)", PermissaoEstabelecimentoPessoaEntity.class).setParameter("p", estabelecimentoVisita).getResultList();
    }

    @Transactional
    public PermissaoEstabelecimentoPessoaEntity fetchPessoas(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa) {
        permissaoEstabelecimentoPessoa = find(permissaoEstabelecimentoPessoa.getId());
        permissaoEstabelecimentoPessoa.getPessoas().size();
        return permissaoEstabelecimentoPessoa;
    }
    
}
