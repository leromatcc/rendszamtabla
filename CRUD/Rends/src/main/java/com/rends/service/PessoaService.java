package com.rends.service;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.PermissaoEstabelecimentoPessoaEntity;
import com.rends.domain.PessoaAutomovelEntity;
import com.rends.domain.PessoaEntity;

import java.io.Serializable;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
public class PessoaService extends BaseService<PessoaEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public PessoaService(){
        super(PessoaEntity.class);
    }
    
    @Transactional
    public List<PessoaEntity> findAllPessoaEntities() {
        
        return entityManager.createQuery("SELECT o FROM Pessoa o ", PessoaEntity.class).getResultList();
    }
    
    @Override
    @Transactional
    public long countAllEntries() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Pessoa o", Long.class).getSingleResult();
    }
    
    @Override
    protected void handleDependenciesBeforeDelete(PessoaEntity pessoa) {

        /* This is called before a Pessoa is deleted. Place here all the
           steps to cut dependencies to other entities */
        
        this.cutAllPessoaPessoaAutomovelsAssignments(pessoa);
        
    }

    // Remove all assignments from all pessoaAutomovel a pessoa. Called before delete a pessoa.
    @Transactional
    private void cutAllPessoaPessoaAutomovelsAssignments(PessoaEntity pessoa) {
        entityManager
                .createQuery("UPDATE PessoaAutomovel c SET c.pessoa = NULL WHERE c.pessoa = :p")
                .setParameter("p", pessoa).executeUpdate();
    }
    
    @Transactional
    public List<PessoaEntity> findAvailablePessoas(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa) {
        return entityManager.createQuery("SELECT o FROM Pessoa o where o.id not in (select o.id from Pessoa o join o.permissaoEstabelecimentoPessoas p where p = :p)", PessoaEntity.class).setParameter("p", permissaoEstabelecimentoPessoa).getResultList();
    }

    @Transactional
    public List<PessoaEntity> findPessoasByPermissaoEstabelecimentoPessoa(PermissaoEstabelecimentoPessoaEntity permissaoEstabelecimentoPessoa) {
        return entityManager.createQuery("SELECT o FROM Pessoa o where o.id in (select o.id from Pessoa o join o.permissaoEstabelecimentoPessoas p where p = :p)", PessoaEntity.class).setParameter("p", permissaoEstabelecimentoPessoa).getResultList();
    }

    // Find all pessoaAutomovel which are not yet assigned to a pessoa
    @Transactional
    public List<PessoaEntity> findAvailablePessoa(PessoaAutomovelEntity pessoaAutomovel) {
        Long id = -1L;
        if (pessoaAutomovel != null && pessoaAutomovel.getPessoa() != null && pessoaAutomovel.getPessoa().getId() != null) {
            id = pessoaAutomovel.getPessoa().getId();
        }
        return entityManager.createQuery(
                "SELECT o FROM Pessoa o where o.id NOT IN (SELECT p.pessoa.id FROM PessoaAutomovel p where p.pessoa.id != :id)", PessoaEntity.class)
                .setParameter("id", id).getResultList();    
    }

}
