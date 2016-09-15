package com.rends.service;

import com.rends.domain.AcessosPermissaoEntity;
import com.rends.domain.AcessosRegistrosEntity;
import com.rends.domain.EstacionamentoEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class AcessosRegistrosService extends BaseService<AcessosRegistrosEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public AcessosRegistrosService(){
        super(AcessosRegistrosEntity.class);
    }
    
    @Transactional
    public List<AcessosRegistrosEntity> findAllAcessosRegistrosEntities() {
        
        return entityManager.createQuery("SELECT o FROM AcessosRegistros o ", AcessosRegistrosEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM AcessosRegistros o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(AcessosRegistrosEntity acessosRegistros) {

        /* This is called before a AcessosRegistros is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<AcessosRegistrosEntity> findAvailableAcessosRegistross(EstacionamentoEntity estacionamento) {
        return entityManager.createQuery("SELECT o FROM AcessosRegistros o where o.id not in (select o.id from AcessosRegistros o join o.estacionamentos p where p = :p)", AcessosRegistrosEntity.class).setParameter("p", estacionamento).getResultList();
    }

    @Transactional
    public List<AcessosRegistrosEntity> findAcessosRegistrossByEstacionamento(EstacionamentoEntity estacionamento) {
        return entityManager.createQuery("SELECT o FROM AcessosRegistros o where o.id in (select o.id from AcessosRegistros o join o.estacionamentos p where p = :p)", AcessosRegistrosEntity.class).setParameter("p", estacionamento).getResultList();
    }

    @Transactional
    public List<AcessosRegistrosEntity> findAvailableAcessosRegistross(AcessosPermissaoEntity acessosPermissao) {
        return entityManager.createQuery("SELECT o FROM AcessosRegistros o where o.id not in (select o.id from AcessosRegistros o join o.permissaoAcessos p where p = :p)", AcessosRegistrosEntity.class).setParameter("p", acessosPermissao).getResultList();
    }

    @Transactional
    public List<AcessosRegistrosEntity> findAcessosRegistrossByPermissaoAcesso(AcessosPermissaoEntity acessosPermissao) {
        return entityManager.createQuery("SELECT o FROM AcessosRegistros o where o.id in (select o.id from AcessosRegistros o join o.permissaoAcessos p where p = :p)", AcessosRegistrosEntity.class).setParameter("p", acessosPermissao).getResultList();
    }

    @Transactional
    public AcessosRegistrosEntity fetchEstacionamentos(AcessosRegistrosEntity acessosRegistros) {
        acessosRegistros = find(acessosRegistros.getId());
        acessosRegistros.getEstacionamentos().size();
        return acessosRegistros;
    }
    
    @Transactional
    public AcessosRegistrosEntity fetchPermissaoAcessos(AcessosRegistrosEntity acessosRegistros) {
        acessosRegistros = find(acessosRegistros.getId());
        acessosRegistros.getPermissaoAcessos().size();
        return acessosRegistros;
    }
    
}
