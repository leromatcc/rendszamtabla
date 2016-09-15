package com.rends.service;

import com.rends.domain.EstabelecimentoEntity;
import com.rends.domain.EstabelecimentoVisitaEntity;

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
        
        this.cutAllEstabelecimentoEstabelecimentoVisitasAssignments(estabelecimento);
        
    }

    // Remove all assignments from all estabelecimentoVisita a estabelecimento. Called before delete a estabelecimento.
    @Transactional
    private void cutAllEstabelecimentoEstabelecimentoVisitasAssignments(EstabelecimentoEntity estabelecimento) {
        entityManager
                .createQuery("UPDATE EstabelecimentoVisita c SET c.estabelecimento = NULL WHERE c.estabelecimento = :p")
                .setParameter("p", estabelecimento).executeUpdate();
    }
    
    // Find all estabelecimentoVisita which are not yet assigned to a estabelecimento
    @Transactional
    public List<EstabelecimentoEntity> findAvailableEstabelecimento(EstabelecimentoVisitaEntity estabelecimentoVisita) {
        Long id = -1L;
        if (estabelecimentoVisita != null && estabelecimentoVisita.getEstabelecimento() != null && estabelecimentoVisita.getEstabelecimento().getId() != null) {
            id = estabelecimentoVisita.getEstabelecimento().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Estabelecimento o where o.id NOT IN (SELECT p.estabelecimento.id FROM EstabelecimentoVisita p where p.estabelecimento.id != :id)", EstabelecimentoEntity.class)
                .setParameter("id", id).getResultList();    
    }

}
