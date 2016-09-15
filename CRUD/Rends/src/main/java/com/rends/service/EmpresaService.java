package com.rends.service;

import com.rends.domain.AcessosPermissaoEntity;
import com.rends.domain.CameraEntity;
import com.rends.domain.EmpresaEntity;
import com.rends.domain.EnderecoEntity;
import com.rends.domain.EstacionamentoEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class EmpresaService extends BaseService<EmpresaEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public EmpresaService(){
        super(EmpresaEntity.class);
    }
    
    @Transactional
    public List<EmpresaEntity> findAllEmpresaEntities() {
        
        return entityManager.createQuery("SELECT o FROM Empresa o ", EmpresaEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Empresa o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(EmpresaEntity empresa) {

        /* This is called before a Empresa is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllEmpresaAcessosPermissaosAssignments(empresa);
        
    }

    // Remove all assignments from all acessosPermissao a empresa. Called before delete a empresa.
    @Transactional
    private void cutAllEmpresaAcessosPermissaosAssignments(EmpresaEntity empresa) {
        entityManager
                .createQuery("UPDATE AcessosPermissao c SET c.empresa = NULL WHERE c.empresa = :p")
                .setParameter("p", empresa).executeUpdate();
    }
    
    @Transactional
    public List<EmpresaEntity> findAvailableEmpresas(EnderecoEntity endereco) {
        return entityManager.createQuery("SELECT o FROM Empresa o where o.id not in (select o.id from Empresa o join o.enderecos p where p = :p)", EmpresaEntity.class).setParameter("p", endereco).getResultList();
    }

    @Transactional
    public List<EmpresaEntity> findEmpresasByEndereco(EnderecoEntity endereco) {
        return entityManager.createQuery("SELECT o FROM Empresa o where o.id in (select o.id from Empresa o join o.enderecos p where p = :p)", EmpresaEntity.class).setParameter("p", endereco).getResultList();
    }

    @Transactional
    public List<EmpresaEntity> findAvailableEstabelecimentos(CameraEntity camera) {
        return entityManager.createQuery("SELECT o FROM Empresa o where o.id not in (select o.id from Empresa o join o.cameras p where p = :p)", EmpresaEntity.class).setParameter("p", camera).getResultList();
    }

    @Transactional
    public List<EmpresaEntity> findEstabelecimentosByCamera(CameraEntity camera) {
        return entityManager.createQuery("SELECT o FROM Empresa o where o.id in (select o.id from Empresa o join o.cameras p where p = :p)", EmpresaEntity.class).setParameter("p", camera).getResultList();
    }

    @Transactional
    public List<EmpresaEntity> findAvailableEmpresas(EstacionamentoEntity estacionamento) {
        return entityManager.createQuery("SELECT o FROM Empresa o where o.id not in (select o.id from Empresa o join o.estacionamentos p where p = :p)", EmpresaEntity.class).setParameter("p", estacionamento).getResultList();
    }

    @Transactional
    public List<EmpresaEntity> findEmpresasByEstacionamento(EstacionamentoEntity estacionamento) {
        return entityManager.createQuery("SELECT o FROM Empresa o where o.id in (select o.id from Empresa o join o.estacionamentos p where p = :p)", EmpresaEntity.class).setParameter("p", estacionamento).getResultList();
    }

    // Find all acessosPermissao which are not yet assigned to a empresa
    @Transactional
    public List<EmpresaEntity> findAvailableEmpresa(AcessosPermissaoEntity acessosPermissao) {
        Long id = -1L;
        if (acessosPermissao != null && acessosPermissao.getEmpresa() != null && acessosPermissao.getEmpresa().getId() != null) {
            id = acessosPermissao.getEmpresa().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Empresa o where o.id NOT IN (SELECT p.empresa.id FROM AcessosPermissao p where p.empresa.id != :id)", EmpresaEntity.class)
                .setParameter("id", id).getResultList();    
    }

    @Transactional
    public EmpresaEntity fetchEnderecos(EmpresaEntity empresa) {
        empresa = find(empresa.getId());
        empresa.getEnderecos().size();
        return empresa;
    }
    
}
