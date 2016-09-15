package com.rends.web;

import com.rends.domain.AutomovelEntity;
import com.rends.domain.PessoaAutomovelEntity;
import com.rends.domain.PessoaEntity;
import com.rends.service.AutomovelService;
import com.rends.service.PessoaAutomovelService;
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
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@Named("pessoaAutomovelBean")
@ViewScoped
public class PessoaAutomovelBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(PessoaAutomovelBean.class.getName());
    
    private List<PessoaAutomovelEntity> pessoaAutomovelList;

    private PessoaAutomovelEntity pessoaAutomovel;
    
    @Inject
    private PessoaAutomovelService pessoaAutomovelService;
    
    @Inject
    private AutomovelService automovelService;
    
    @Inject
    private PessoaService pessoaService;
    
    private DualListModel<AutomovelEntity> automovels;
    private List<String> transferedAutomovelIDs;
    private List<String> removedAutomovelIDs;
    
    private List<PessoaEntity> availablePessoaList;
    
    public void prepareNewPessoaAutomovel() {
        reset();
        this.pessoaAutomovel = new PessoaAutomovelEntity();
        // set any default values now, if you need
        // Example: this.pessoaAutomovel.setAnything("test");
    }

    public String persist() {

        if (pessoaAutomovel.getId() == null && !isPermitted("pessoaAutomovel:create")) {
            return "accessDenied";
        } else if (pessoaAutomovel.getId() != null && !isPermitted(pessoaAutomovel, "pessoaAutomovel:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (pessoaAutomovel.getId() != null) {
                pessoaAutomovel = pessoaAutomovelService.update(pessoaAutomovel);
                message = "message_successfully_updated";
            } else {
                pessoaAutomovel = pessoaAutomovelService.save(pessoaAutomovel);
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
        
        pessoaAutomovelList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(pessoaAutomovel, "pessoaAutomovel:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            pessoaAutomovelService.delete(pessoaAutomovel);
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
        pessoaAutomovel = null;
        pessoaAutomovelList = null;
        
        automovels = null;
        transferedAutomovelIDs = null;
        removedAutomovelIDs = null;
        
        availablePessoaList = null;
        
    }

    // Get a List of all pessoa
    public List<PessoaEntity> getAvailablePessoa() {
        if (this.availablePessoaList == null) {
            this.availablePessoaList = pessoaService.findAvailablePessoa(this.pessoaAutomovel);
        }
        return this.availablePessoaList;
    }
    
    // Update pessoa of the current pessoaAutomovel
    public void updatePessoa(PessoaEntity pessoa) {
        this.pessoaAutomovel.setPessoa(pessoa);
        // Maybe we just created and assigned a new pessoa. So reset the availablePessoaList.
        availablePessoaList = null;
    }
    
    public DualListModel<AutomovelEntity> getAutomovels() {
        return automovels;
    }

    public void setAutomovels(DualListModel<AutomovelEntity> automovels) {
        this.automovels = automovels;
    }
    
    public List<AutomovelEntity> getFullAutomovelsList() {
        List<AutomovelEntity> allList = new ArrayList<>();
        allList.addAll(automovels.getSource());
        allList.addAll(automovels.getTarget());
        return allList;
    }
    
    public void onAutomovelsDialog(PessoaAutomovelEntity pessoaAutomovel) {
        // Prepare the automovel PickList
        this.pessoaAutomovel = pessoaAutomovel;
        List<AutomovelEntity> selectedAutomovelsFromDB = automovelService
                .findAutomovelsByPessoaAutomovel(this.pessoaAutomovel);
        List<AutomovelEntity> availableAutomovelsFromDB = automovelService
                .findAvailableAutomovels(this.pessoaAutomovel);
        this.automovels = new DualListModel<>(availableAutomovelsFromDB, selectedAutomovelsFromDB);
        
        transferedAutomovelIDs = new ArrayList<>();
        removedAutomovelIDs = new ArrayList<>();
    }
    
    public void onAutomovelsPickListTransfer(TransferEvent event) {
        // If a automovel is transferred within the PickList, we just transfer it in this
        // bean scope. We do not change anything it the database, yet.
        for (Object item : event.getItems()) {
            String id = ((AutomovelEntity) item).getId().toString();
            if (event.isAdd()) {
                transferedAutomovelIDs.add(id);
                removedAutomovelIDs.remove(id);
            } else if (event.isRemove()) {
                removedAutomovelIDs.add(id);
                transferedAutomovelIDs.remove(id);
            }
        }
        
    }
    
    public void updateAutomovel(AutomovelEntity automovel) {
        // If a new automovel is created, we persist it to the database,
        // but we do not assign it to this pessoaAutomovel in the database, yet.
        automovels.getTarget().add(automovel);
        transferedAutomovelIDs.add(automovel.getId().toString());
    }
    
    public void onAutomovelsSubmit() {
        // Now we save the changed of the PickList to the database.
        try {
            
            List<AutomovelEntity> selectedAutomovelsFromDB = automovelService.findAutomovelsByPessoaAutomovel(this.pessoaAutomovel);
            List<AutomovelEntity> availableAutomovelsFromDB = automovelService.findAvailableAutomovels(this.pessoaAutomovel);

            // Because automovels are lazily loaded, we need to fetch them now
            this.pessoaAutomovel = pessoaAutomovelService.fetchAutomovels(this.pessoaAutomovel);
            
            for (AutomovelEntity automovel : selectedAutomovelsFromDB) {
                if (removedAutomovelIDs.contains(automovel.getId().toString())) {
                    
                    this.pessoaAutomovel.getAutomovels().remove(automovel);
                    
                }
            }
    
            for (AutomovelEntity automovel : availableAutomovelsFromDB) {
                if (transferedAutomovelIDs.contains(automovel.getId().toString())) {
                    
                    this.pessoaAutomovel.getAutomovels().add(automovel);
                    
                }
            }
            
            this.pessoaAutomovel = pessoaAutomovelService.update(this.pessoaAutomovel);
            
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
    
    public PessoaAutomovelEntity getPessoaAutomovel() {
        if (this.pessoaAutomovel == null) {
            prepareNewPessoaAutomovel();
        }
        return this.pessoaAutomovel;
    }
    
    public void setPessoaAutomovel(PessoaAutomovelEntity pessoaAutomovel) {
        this.pessoaAutomovel = pessoaAutomovel;
    }
    
    public List<PessoaAutomovelEntity> getPessoaAutomovelList() {
        if (pessoaAutomovelList == null) {
            pessoaAutomovelList = pessoaAutomovelService.findAllPessoaAutomovelEntities();
        }
        return pessoaAutomovelList;
    }

    public void setPessoaAutomovelList(List<PessoaAutomovelEntity> pessoaAutomovelList) {
        this.pessoaAutomovelList = pessoaAutomovelList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(PessoaAutomovelEntity pessoaAutomovel, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
