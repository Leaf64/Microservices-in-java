package introtask.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotEmpty;

public class JobIntervals {

    @NotEmpty
    private int birthdayJob;

    @NotEmpty
    private int zodiacJob;

    @JsonProperty
    public int  getBirthdayJob() {
        return birthdayJob;
    }
    @JsonProperty
    public void setBirthdayJob(int  birthdayJob) {
        this.birthdayJob = birthdayJob;
    }
    @JsonProperty
    public int  getZodiacJob() {
        return zodiacJob;
    }
    @JsonProperty
    public void  setZodiacJob(int  zodiacJob) {
        this.zodiacJob = zodiacJob;
    }
}
