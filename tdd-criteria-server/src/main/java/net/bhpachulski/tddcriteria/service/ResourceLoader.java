package net.bhpachulski.tddcriteria.service;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

import net.bhpachulski.tddcriteria.service.impl.TDDCriteriaService;

/**
 *
 * @author bhpachulski
 */
public class ResourceLoader extends Application{
 
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        
        classes.add(TDDCriteriaService.class);
        classes.add(MultiPartFeature.class);
        
        try {
            classes.add(Class.forName("org.glassfish.jersey.jackson.JacksonFeature"));
        } catch (ClassNotFoundException ex) {}
        
        return classes;
    }
}