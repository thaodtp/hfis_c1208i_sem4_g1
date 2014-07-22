/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import biz.ResourceManager;
import entity.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Thao
 */
@ManagedBean
@ViewScoped
public class ResourceUI {

    @EJB
    private ResourceManager resourceManager;
    
    private Map<String, Integer> typeS = new LinkedHashMap<String, Integer>();
    private String title;
    private String content;
    private int type;
    private List<Resource> resources;
    private String msg;
    private int id;
    /**
     * Creates a new instance of ResourceUI
     */
    public ResourceUI() {
        typeS.put("Software", 1);
        typeS.put("Hardware", 2);
        typeS.put("Syllabus", 3);
        typeS.put("E-Learning", 4);
    }
    
    public List<Resource> getSoftware() {
        return resourceManager.displaySoftware();
    }
    
    public List<Resource> getHardware() {
        return resourceManager.displayHardware();
    }
    
    public List<Resource> getSyllabus() {
        return resourceManager.displaySyllabus();
    }
    
    public List<Resource> getELearning() {
        return resourceManager.displayELearning();
    }
    
    public String create() {
        try {
            resources = resourceManager.getAllResource();
            if (title.equals("")) {
                msg = " Please enter resource's title!!!";
                return null;
            }
            for (Resource resource : resources) {
                if (resource.getTitle().toLowerCase().equals(title.toLowerCase())) {
                    msg = " Title is exist";
                    return "";
                }
            }
            resourceManager.create(new Resource(title, content, type));
            return "/admin/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't add this software";
            Logger.getLogger(ResourceUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public String edit() {
        try {
            resourceManager.edit(id, title, content, type);
            return "/success.xhtml";
        } catch (Exception ex) {
            msg = "Can't update this resource";
            Logger.getLogger(ResourceUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    
    public String delete() {
        try {
            resourceManager.delete(id);
            return "/admin/success.xhtml?faces-redirect=true";
        } catch (Exception ex) {
            msg = "Can't delete this resource";
            Logger.getLogger(ResourceUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }
    

    public Map<String, Integer> getTypeS() {
        return typeS;
    }

    public void setTypeS(Map<String, Integer> typeS) {
        this.typeS = typeS;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        Resource target = resourceManager.getResourceById(id);
        if (target != null) {
            title = target.getTitle();
            content = target.getContent();
            type = target.getType();
        }
    }
    
    
    
    


    
    
    
    
    
    
    
    
    
    
    
    
 
    
}
