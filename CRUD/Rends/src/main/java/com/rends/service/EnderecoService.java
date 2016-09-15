package com.rends.service;

import com.rends.domain.EmpresaEntity;
import com.rends.domain.EnderecoEntity;
import com.rends.domain.PessoaEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class EnderecoService extends BaseService<EnderecoEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public EnderecoService(){
        super(EnderecoEntity.class);
    }
    
    @Transactional
    public List<EnderecoEntity> findAllEnderecoEntities() {
        
        return entityManager.createQuery("SELECT o FROM Endereco o ", EnderecoEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Endereco o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(EnderecoEntity endereco) {

        /* This is called before a Endereco is deleted. Place here all the
           steps to cut dependencies to other entities */
        
    }

    @Transactional
    public List<EnderecoEntity> findAvailableEnderecos(PessoaEntity pessoa) {
        return entityManager.createQuery("SELECT o FROM Endereco o where o.id not in (select o.id from Endereco o join o.pessoas p where p = :p)", EnderecoEntity.class).setParameter("p", pessoa).getResultList();
    }

    @Transactional
    public List<EnderecoEntity> findEnderecosByPessoa(PessoaEntity pessoa) {
        return entityManager.createQuery("SELECT o FROM Endereco o where o.id in (select o.id from Endereco o join o.pessoas p where p = :p)", EnderecoEntity.class).setParameter("p", pessoa).getResultList();
    }

    @Transactional
    public List<EnderecoEntity> findAvailableEnderecos(EmpresaEntity empresa) {
        return entityManager.createQuery("SELECT o FROM Endereco o where o.id not in (select o.id from Endereco o join o.empresas p where p = :p)", EnderecoEntity.class).setParameter("p", empresa).getResultList();
    }

    @Transactional
    public List<EnderecoEntity> findEnderecosByEmpresa(EmpresaEntity empresa) {
        return entityManager.createQuery("SELECT o FROM Endereco o where o.id in (select o.id from Endereco o join o.empresas p where p = :p)", EnderecoEntity.class).setParameter("p", empresa).getResultList();
    }

}
