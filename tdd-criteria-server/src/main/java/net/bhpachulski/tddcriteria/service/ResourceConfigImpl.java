package net.bhpachulski.tddcriteria.service;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author bhpachulski
 */
public class ResourceConfigImpl extends ResourceConfig {

    public ResourceConfigImpl () {
        super(MultiPartFeature.class);
    }
}
