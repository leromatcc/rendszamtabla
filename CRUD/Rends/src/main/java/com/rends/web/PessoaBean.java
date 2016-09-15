package com.rends.web;

import com.rends.domain.EnderecoEntity;
import com.rends.domain.PessoaDocumentoTipo;
import com.rends.domain.PessoaEntity;
import com.rends.domain.PessoaTelefoneTIpo;
import com.rends.service.EnderecoService;
import com.rends.service.PessoaService;
import com.rends.service.security.SecurityWrapper;
import com.rends.web.util.MessageFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@Named("pessoaBean")
@ViewScoped
public class PessoaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PessoaBean.class.getName());
    
    private List<PessoaEntity> pessoaList;

    private PessoaEntity pessoa;
    
    @Inject
    private PessoaService pessoaService;
    
    @Inject
    private EnderecoService enderecoService;
    
    private DualListModel<EnderecoEntity> enderecos;
    private List<String> transferedEnderecoIDs;
    private List<String> removedEnderecoIDs;
    
    public void prepareNewPessoa() {
        reset();
        this.pessoa = new PessoaEntity();
        // set any default values now, if you need
        // Example: this.pessoa.setAnything("test");
    }

    public String persist() {

        if (pessoa.getId() == null && !isPermitted("pessoa:create")) {
            return "accessDenied";
        } else if (pessoa.getId() != null && !isPermitted(pessoa, "pessoa:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (pessoa.getId() != null) {
                pessoa = pessoaService.update(pessoa);
                message = "message_successfully_updated";
            } else {
                pessoa = pessoaService.save(pessoa);
                message = "message_successfully_created";
            }
        } catch (OptimisticLockException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_optimistic_locking_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_save_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
        
        pessoaList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(pessoa, "pessoa:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            pessoaService.delete(pessoa);
            message = "message_successfully_deleted";
            reset();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occured", e);
            message = "message_delete_exception";
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
        FacesContext.getCurrentInstance().addMessage(null, MessageFactory.getMessage(message));
        
        return null;
    }
    
    public void reset() {
        pessoa = null;
        pessoaList = null;
        
        enderecos = null;
        transferedEnderecoIDs = null;
        removedEnderecoIDs = null;
        
    }

    public DualListModel<EnderecoEntity> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(DualListModel<EnderecoEntity> enderecos) {
        this.enderecos = enderecos;
    }
    
    public List<EnderecoEntity> getFullEnderecosList() {
        List<EnderecoEntity> allList = new ArrayList<>();
        allList.addAll(enderecos.getSource());
        allList.addAll(enderecos.getTarget());
        return allList;
    }
    
    public void onEnderecosDialog(PessoaEntity pessoa) {
        // Prepare the endereco PickList
        this.pessoa = pessoa;
        List<EnderecoEntity> selectedEnderecosFromDB = enderecoService
                .findEnderecosByPessoa(this.pessoa);
        List<EnderecoEntity> availableEnderecosFromDB = enderecoService
                .findAvailableEnderecos(this.pessoa);
        this.enderecos = new DualListModel<>(availableEnderecosFromDB, selectedEnderecosFromDB);
        
        transferedEnderecoIDs = new ArrayList<>();
        removedEnderecoIDs = new ArrayList<>();
    }
    
    public void onEnderecosPickListTransfer(TransferEvent event) {
        // If a endereco is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((EnderecoEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedEnderecoIDs.add(id);
                removedEnderecoIDs.remove(id);
            } else if (event.isRemove()) {
                removedEnderecoIDs.add(id);
                transferedEnderecoIDs.remove(id);
            }
        }
        
    }
    
    public void updateEndereco(EnderecoEntity endereco) {
        // If a new endereco is created, we persist it to the database,
        // but we do not assign it to this pessoa in the database, yet.
        enderecos.getTarget().add(endereco);
        transferedEnderecoIDs.add(endereco.getId().toString());
    }
    
    public void onEnderecosSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<EnderecoEntity> selectedEnderecosFromDB = enderecoService.findEnderecosByPessoa(this.pessoa);
            List<EnderecoEntity> availableEnderecosFromDB = enderecoService.findAvailableEnderecos(this.pessoa);

            // Because enderecos are lazily loaded, we need to fetch them now
            this.pessoa = pessoaService.fetchEnderecos(this.pessoa);
            
            for (EnderecoEntity endereco : selectedEnderecosFromDB) {
                if (removedEnderecoIDs.contains(endereco.getId().toString())) {
                    
                    this.pessoa.getEnderecos().remove(endereco);
                    
                }
            }
    
            for (EnderecoEntity endereco : availableEnderecosFromDB) {
                if (transferedEnderecoIDs.contains(endereco.getId().toString())) {
                    
                    this.pessoa.getEnderecos().add(endereco);
                    
                }
            }
            
            this.pessoa = pessoaService.update(this.pessoa);
            
            FacesMessage facesMessage = MessageFactory.getMessage("message_changes_saved");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
            
            reset();

        } catch (OptimisticLockException e) {
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_optimistic_locking_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        } catch (PersistenceException e) {
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_picklist_save_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
    }
    
    public SelectItem[] getDocumentoTipoSelectItems() {
        SelectItem[] items = new SelectItem[PessoaDocumentoTipo.values().length];

        int i = 0;
        for (PessoaDocumentoTipo documentoTipo : PessoaDocumentoTipo.values()) {
            items[i++] = new SelectItem(documentoTipo, getLabelForDocumentoTipo(documentoTipo));
        }
        return items;
    }
    
    public String getLabelForDocumentoTipo(PessoaDocumentoTipo value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_pessoa_documentoTipo_" + value);
        return label == null? value.toString() : label;
    }
    
    public SelectItem[] getTelefoneTIpoSelectItems() {
        SelectItem[] items = new SelectItem[PessoaTelefoneTIpo.values().length];

        int i = 0;
        for (PessoaTelefoneTIpo telefoneTIpo : PessoaTelefoneTIpo.values()) {
            items[i++] = new SelectItem(telefoneTIpo, getLabelForTelefoneTIpo(telefoneTIpo));
        }
        return items;
    }
    
    public String getLabelForTelefoneTIpo(PessoaTelefoneTIpo value) {
        if (value == null) {
            return "";
        }
        String label = MessageFactory.getMessageString(
                "enum_label_pessoa_telefoneTIpo_" + value);
        return label == null? value.toString() : label;
    }
    
    public PessoaEntity getPessoa() {
        if (this.pessoa == null) {
            prepareNewPessoa();
        }
        return this.pessoa;
    }
    
    public void setPessoa(PessoaEntity pessoa) {
        this.pessoa = pessoa;
    }
    
    public List<PessoaEntity> getPessoaList() {
        if (pessoaList == null) {
            pessoaList = pessoaService.findAllPessoaEntities();
        }
        return pessoaList;
    }

    public void setPessoaList(List<PessoaEntity> pessoaList) {
        this.pessoaList = pessoaList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(PessoaEntity pessoa, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
