package com.cuzz.webac.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeChatPayNotificationDTO {

    private String summary;
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("create_time")
    private String createTime;
    private Resource resource;

    @JsonProperty("resource_type")
    private String resourceType;
    private String id;

    // Getters and Setters
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Inner class for Resource
    public static class Resource {

        @JsonProperty("original_type")
        private String originalType;

        private String algorithm;
        private String ciphertext;
        @JsonProperty("associated_data")
        private String associatedData;
        private String nonce;

        // Getters and Setters
        public String getOriginalType() {
            return originalType;
        }

        public void setOriginalType(String originalType) {
            this.originalType = originalType;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getCiphertext() {
            return ciphertext;
        }

        public void setCiphertext(String ciphertext) {
            this.ciphertext = ciphertext;
        }

        public String getAssociatedData() {
            return associatedData;
        }

        public void setAssociatedData(String associatedData) {
            this.associatedData = associatedData;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }
    }
}

