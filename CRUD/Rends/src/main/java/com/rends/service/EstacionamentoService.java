package com.rends.service;

import com.rends.domain.AcessosRegistrosEntity;
import com.rends.domain.EmpresaEntity;
import com.rends.domain.EstacionamentoEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class EstacionamentoService extends BaseService<EstacionamentoEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public EstacionamentoService(){
        super(EstacionamentoEntity.class);
    }
    
    @Transactional
    public List<EstacionamentoEntity> findAllEstacionamentoEntities() {
        
        return entityManager.createQuery("SELECT o FROM Estacionamento o ", EstacionamentoEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Estacionamento o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(EstacionamentoEntity estacionamento) {

        /* This is called before a Estacionamento is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<EstacionamentoEntity> findAvailableEstacionamentos(EmpresaEntity empresa) {
        return entityManager.createQuery("SELECT o FROM Estacionamento o where o.id not in (select o.id from Estacionamento o join o.empresas p where p = :p)", EstacionamentoEntity.class).setParameter("p", empresa).getResultList();
    }

    @Transactional
    public List<EstacionamentoEntity> findEstacionamentosByEmpresa(EmpresaEntity empresa) {
        return entityManager.createQuery("SELECT o FROM Estacionamento o where o.id in (select o.id from Estacionamento o join o.empresas p where p = :p)", EstacionamentoEntity.class).setParameter("p", empresa).getResultList();
    }

    @Transactional
    public List<EstacionamentoEntity> findAvailableEstacionamentos(AcessosRegistrosEntity acessosRegistros) {
        return entityManager.createQuery("SELECT o FROM Estacionamento o where o.id not in (select o.id from Estacionamento o join o.acessosRegistross p where p = :p)", EstacionamentoEntity.class).setParameter("p", acessosRegistros).getResultList();
    }

    @Transactional
    public List<EstacionamentoEntity> findEstacionamentosByAcessosRegistros(AcessosRegistrosEntity acessosRegistros) {
        return entityManager.createQuery("SELECT o FROM Estacionamento o where o.id in (select o.id from Estacionamento o join o.acessosRegistross p where p = :p)", EstacionamentoEntity.class).setParameter("p", acessosRegistros).getResultList();
    }

    @Transactional
    public EstacionamentoEntity fetchEmpresas(EstacionamentoEntity estacionamento) {
        estacionamento = find(estacionamento.getId());
        estacionamento.getEmpresas().size();
        return estacionamento;
    }
    
}
