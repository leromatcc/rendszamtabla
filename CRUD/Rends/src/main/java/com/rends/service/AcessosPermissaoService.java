package com.rends.service;

import com.rends.domain.AcessosPermissaoEntity;
import com.rends.domain.AcessosRegistrosEntity;
import com.rends.domain.PessoaEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class AcessosPermissaoService extends BaseService<AcessosPermissaoEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public AcessosPermissaoService(){
        super(AcessosPermissaoEntity.class);
    }
    
    @Transactional
    public List<AcessosPermissaoEntity> findAllAcessosPermissaoEntities() {
        
        return entityManager.createQuery("SELECT o FROM AcessosPermissao o ", AcessosPermissaoEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM AcessosPermissao o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(AcessosPermissaoEntity acessosPermissao) {

        /* This is called before a AcessosPermissao is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<AcessosPermissaoEntity> findAvailableAcessosPermissaos(PessoaEntity pessoa) {
        return entityManager.createQuery("SELECT o FROM AcessosPermissao o where o.id not in (select o.id from AcessosPermissao o join o.pessoas p where p = :p)", AcessosPermissaoEntity.class).setParameter("p", pessoa).getResultList();
    }

    @Transactional
    public List<AcessosPermissaoEntity> findAcessosPermissaosByPessoa(PessoaEntity pessoa) {
        return entityManager.createQuery("SELECT o FROM AcessosPermissao o where o.id in (select o.id from AcessosPermissao o join o.pessoas p where p = :p)", AcessosPermissaoEntity.class).setParameter("p", pessoa).getResultList();
    }

    @Transactional
    public List<AcessosPermissaoEntity> findAvailablePermissaoAcessos(AcessosRegistrosEntity acessosRegistros) {
        return entityManager.createQuery("SELECT o FROM AcessosPermissao o where o.id not in (select o.id from AcessosPermissao o join o.acessosRegistross p where p = :p)", AcessosPermissaoEntity.class).setParameter("p", acessosRegistros).getResultList();
    }

    @Transactional
    public List<AcessosPermissaoEntity> findPermissaoAcessosByAcessosRegistros(AcessosRegistrosEntity acessosRegistros) {
        return entityManager.createQuery("SELECT o FROM AcessosPermissao o where o.id in (select o.id from AcessosPermissao o join o.acessosRegistross p where p = :p)", AcessosPermissaoEntity.class).setParameter("p", acessosRegistros).getResultList();
    }

    @Transactional
    public AcessosPermissaoEntity fetchPessoas(AcessosPermissaoEntity acessosPermissao) {
        acessosPermissao = find(acessosPermissao.getId());
        acessosPermissao.getPessoas().size();
        return acessosPermissao;
    }
    
}
