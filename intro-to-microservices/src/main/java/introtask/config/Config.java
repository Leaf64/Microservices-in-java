package introtask.config;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class Config extends Configuration {



    @JsonProperty
    private Credentials credentials;


    @JsonProperty
    private TagServiceConfig tagService;

    @JsonProperty
    private JobIntervals jobIntervals;

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public TagServiceConfig getTagServiceConfig() {
        return tagService;
    }

    public void setTagServiceConfig(TagServiceConfig tagServiceConfig) {
        this.tagService = tagServiceConfig;
    }

    public JobIntervals getJobIntervals() {
        return jobIntervals;
    }

    public void setJobIntervals(JobIntervals jobIntervals) {
        this.jobIntervals = jobIntervals;
    }
}

