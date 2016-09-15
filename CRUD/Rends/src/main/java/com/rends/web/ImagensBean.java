package com.rends.web;

import com.rends.domain.ImagensAttachment;
import com.rends.domain.ImagensEntity;
import com.rends.domain.ImagensImage;
import com.rends.service.ImagensAttachmentService;
import com.rends.service.ImagensService;
import com.rends.service.security.SecurityWrapper;
import com.rends.web.util.MessageFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.MimetypesFileTypeMap;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

import org.apache.commons.io.IOUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

@Named("imagensBean")
@ViewScoped
public class ImagensBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ImagensBean.class.getName());
    
    private List<ImagensEntity> imagensList;

    private ImagensEntity imagens;
    
    private List<ImagensAttachment> imagensAttachments;
    
    @Inject
    private ImagensService imagensService;
    
    @Inject
    private ImagensAttachmentService imagensAttachmentService;
    
    UploadedFile uploadedImage;
    byte[] uploadedImageContents;
    
    public void prepareNewImagens() {
        reset();
        this.imagens = new ImagensEntity();
        // set any default values now, if you need
        // Example: this.imagens.setAnything("test");
    }

    public String persist() {

        if (imagens.getId() == null && !isPermitted("imagens:create")) {
            return "accessDenied";
        } else if (imagens.getId() != null && !isPermitted(imagens, "imagens:update")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            
            if (this.uploadedImage != null) {
                try {

                    BufferedImage image;
                    try (InputStream in = new ByteArrayInputStream(uploadedImageContents)) {
                        image = ImageIO.read(in);
                    }
                    image = Scalr.resize(image, Method.BALANCED, 300);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageOutputStream imageOS = ImageIO.createImageOutputStream(baos);
                    Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(
                            uploadedImage.getContentType());
                    ImageWriter imageWriter = (ImageWriter) imageWriters.next();
                    imageWriter.setOutput(imageOS);
                    imageWriter.write(image);
                    
                    baos.close();
                    imageOS.close();
                    
                    logger.log(Level.INFO, "Resized uploaded image from {0} to {1}", new Object[]{uploadedImageContents.length, baos.toByteArray().length});
            
                    ImagensImage imagensImage = new ImagensImage();
                    imagensImage.setContent(baos.toByteArray());
                    imagens.setImage(imagensImage);
                } catch (Exception e) {
                    FacesMessage facesMessage = MessageFactory.getMessage(
                            "message_upload_exception");
                    FacesContext.getCurrentInstance().addMessage(null, facesMessage);
                    FacesContext.getCurrentInstance().validationFailed();
                    return null;
                }
            }
            
            if (imagens.getId() != null) {
                imagens = imagensService.update(imagens);
                message = "message_successfully_updated";
            } else {
                imagens = imagensService.save(imagens);
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
        
        imagensList = null;

        FacesMessage facesMessage = MessageFactory.getMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        
        return null;
    }
    
    public String delete() {
        
        if (!isPermitted(imagens, "imagens:delete")) {
            return "accessDenied";
        }
        
        String message;
        
        try {
            imagensService.delete(imagens);
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
        imagens = null;
        imagensList = null;
        
        imagensAttachments = null;
        
        uploadedImage = null;
        uploadedImageContents = null;
        
    }

    public void handleImageUpload(FileUploadEvent event) {
        
        Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByMIMEType(
                event.getFile().getContentType());
        if (!imageWriters.hasNext()) {
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_image_type_not_supported");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            return;
        }
        
        this.uploadedImage = event.getFile();
        this.uploadedImageContents = event.getFile().getContents();
        
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
    
    public byte[] getUploadedImageContents() {
        if (uploadedImageContents != null) {
            return uploadedImageContents;
        } else if (imagens != null && imagens.getImage() != null) {
            imagens = imagensService.lazilyLoadImageToImagens(imagens);
            return imagens.getImage().getContent();
        }
        return null;
    }
    
    public List<ImagensAttachment> getImagensAttachments() {
        if (this.imagensAttachments == null && this.imagens != null && this.imagens.getId() != null) {
            // The byte streams are not loaded from database with following line. This would cost too much.
            this.imagensAttachments = this.imagensAttachmentService.getAttachmentsList(imagens);
        }
        return this.imagensAttachments;
    }
    
    public void handleAttachmentUpload(FileUploadEvent event) {
        
        ImagensAttachment imagensAttachment = new ImagensAttachment();
        
        try {
            // Would be better to use ...getFile().getContents(), but does not work on every environment
            imagensAttachment.setContent(IOUtils.toByteArray(event.getFile().getInputstream()));
        
            imagensAttachment.setFileName(event.getFile().getFileName());
            imagensAttachment.setImagens(imagens);
            imagensAttachmentService.save(imagensAttachment);
            
            // set imagensAttachment to null, will be refreshed on next demand
            this.imagensAttachments = null;
            
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_successfully_uploaded");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occured", e);
            FacesMessage facesMessage = MessageFactory.getMessage(
                    "message_upload_exception");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            // Set validationFailed to keep the dialog open
            FacesContext.getCurrentInstance().validationFailed();
        }
    }

    public StreamedContent getAttachmentStreamedContent(
            ImagensAttachment imagensAttachment) {
        if (imagensAttachment != null && imagensAttachment.getContent() == null) {
            imagensAttachment = imagensAttachmentService
                    .find(imagensAttachment.getId());
        }
        return new DefaultStreamedContent(new ByteArrayInputStream(
                imagensAttachment.getContent()),
                new MimetypesFileTypeMap().getContentType(imagensAttachment
                        .getFileName()), imagensAttachment.getFileName());
    }

    public String deleteAttachment(ImagensAttachment attachment) {
        
        imagensAttachmentService.delete(attachment);
        
        // set imagensAttachment to null, will be refreshed on next demand
        this.imagensAttachments = null;
        
        FacesMessage facesMessage = MessageFactory.getMessage(
                "message_successfully_deleted", "Attachment");
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        return null;
    }
    
    public ImagensEntity getImagens() {
        if (this.imagens == null) {
            prepareNewImagens();
        }
        return this.imagens;
    }
    
    public void setImagens(ImagensEntity imagens) {
        this.imagens = imagens;
    }
    
    public List<ImagensEntity> getImagensList() {
        if (imagensList == null) {
            imagensList = imagensService.findAllImagensEntities();
        }
        return imagensList;
    }

    public void setImagensList(List<ImagensEntity> imagensList) {
        this.imagensList = imagensList;
    }
    
    public boolean isPermitted(String permission) {
        return SecurityWrapper.isPermitted(permission);
    }

    public boolean isPermitted(ImagensEntity imagens, String permission) {
        
        return SecurityWrapper.isPermitted(permission);
        
    }
    
}
