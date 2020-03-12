package introtask.dto;

        import introtask.entity.Tag;
        import org.mongodb.morphia.Datastore;

        import javax.validation.constraints.Email;
        import javax.validation.constraints.NotEmpty;
        import javax.validation.constraints.NotNull;
        import java.util.Date;
        import java.util.List;
        import java.util.UUID;

public class UserDto {
    private UUID uuid;

    @Email
    @NotNull
    private String email;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;

    private String creationDate;

    private PaginationDto<Tag> tags;

    private String dateOfBirth;

    public UserDto(){

    }

    public UserDto(String email, String firstName, String lastName) {

        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public UserDto(UUID uuid, String email, String firstName, String lastName, String creationDate) {
        this.uuid = uuid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.creationDate = creationDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID id) {
        this.uuid = id;
    }

    public PaginationDto<Tag> getTags() {
        return tags;
    }

    public void setTags(PaginationDto<Tag> tags) {
        this.tags = tags;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
