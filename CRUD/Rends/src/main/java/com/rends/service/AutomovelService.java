package com.rends.service;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.AutomovelTipoEntity;
import com.rends.domain.EstabelecimentoVisitaEntity;
import com.rends.domain.PessoaAutomovelEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class AutomovelService extends BaseService<AutomovelEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public AutomovelService(){
        super(AutomovelEntity.class);
    }
    
    @Transactional
    public List<AutomovelEntity> findAllAutomovelEntities() {
        
        return entityManager.createQuery("SELECT o FROM Automovel o ", AutomovelEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Automovel o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(AutomovelEntity automovel) {

        /* This is called before a Automovel is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllAutomovelEstabelecimentoVisitasAssignments(automovel);
        
    }

    // Remove all assignments from all estabelecimentoVisita a automovel. Called before delete a automovel.
    @Transactional
    private void cutAllAutomovelEstabelecimentoVisitasAssignments(AutomovelEntity automovel) {
        entityManager
                .createQuery("UPDATE EstabelecimentoVisita c SET c.automovel = NULL WHERE c.automovel = :p")
                .setParameter("p", automovel).executeUpdate();
    }
    
    @Transactional
    public List<AutomovelEntity> findAvailableAutomovels(AutomovelTipoEntity automovelTipo) {
        return entityManager.createQuery("SELECT o FROM Automovel o where o.id not in (select o.id from Automovel o join o.automovelTipos p where p = :p)", AutomovelEntity.class).setParameter("p", automovelTipo).getResultList();
    }

    @Transactional
    public List<AutomovelEntity> findAutomovelsByAutomovelTipo(AutomovelTipoEntity automovelTipo) {
        return entityManager.createQuery("SELECT o FROM Automovel o where o.id in (select o.id from Automovel o join o.automovelTipos p where p = :p)", AutomovelEntity.class).setParameter("p", automovelTipo).getResultList();
    }

    @Transactional
    public List<AutomovelEntity> findAvailableAutomovels(PessoaAutomovelEntity pessoaAutomovel) {
        return entityManager.createQuery("SELECT o FROM Automovel o where o.id not in (select o.id from Automovel o join o.pessoaAutomovels p where p = :p)", AutomovelEntity.class).setParameter("p", pessoaAutomovel).getResultList();
    }

    @Transactional
    public List<AutomovelEntity> findAutomovelsByPessoaAutomovel(PessoaAutomovelEntity pessoaAutomovel) {
        return entityManager.createQuery("SELECT o FROM Automovel o where o.id in (select o.id from Automovel o join o.pessoaAutomovels p where p = :p)", AutomovelEntity.class).setParameter("p", pessoaAutomovel).getResultList();
    }

    // Find all estabelecimentoVisita which are not yet assigned to a automovel
    @Transactional
    public List<AutomovelEntity> findAvailableAutomovel(EstabelecimentoVisitaEntity estabelecimentoVisita) {
        Long id = -1L;
        if (estabelecimentoVisita != null && estabelecimentoVisita.getAutomovel() != null && estabelecimentoVisita.getAutomovel().getId() != null) {
            id = estabelecimentoVisita.getAutomovel().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Automovel o where o.id NOT IN (SELECT p.automovel.id FROM EstabelecimentoVisita p where p.automovel.id != :id)", AutomovelEntity.class)
                .setParameter("id", id).getResultList();    
    }

    @Transactional
    public AutomovelEntity fetchAutomovelTipos(AutomovelEntity automovel) {
        automovel = find(automovel.getId());
        automovel.getAutomovelTipos().size();
        return automovel;
    }
    
}
