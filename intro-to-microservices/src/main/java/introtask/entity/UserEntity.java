package introtask.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.Date;
import java.util.UUID;

@Entity("users")
public class UserEntity {

    @Id
    private ObjectId _id;

    @Indexed(unique = true)
    private UUID uuid;


    @Indexed(unique = true)
    private String email;
    private String firstName;
    private String lastName;
    private String creationDate;
    private String dateOfBirth;

    public UserEntity() {

    }

    public UserEntity(UUID uuid, String email, String firstname, String lastname, String creationDate) {
        this.uuid = uuid;
        this.email = email;
        this.firstName = firstname;
        this.lastName = lastname;
        this.creationDate = creationDate;
    }

    @JsonProperty
    public ObjectId get_id() {
        return _id;
    }
    @JsonProperty
    public void set_id(ObjectId _id) {
        this._id = _id;
    }
    @JsonProperty
    public String getEmail() {
        return email;
    }
    @JsonProperty
    public void setEmail(String email) {
        this.email = email;
    }
    @JsonProperty
    public String getFirstname() {
        return firstName;
    }
    @JsonProperty
    public void setFirstname(String firstname) {
        this.firstName = firstname;
    }
    @JsonProperty
    public String getLastname() {
        return lastName;
    }
    @JsonProperty
    public void setLastname(String lastname) {
        this.lastName = lastname;
    }
    @JsonProperty
    public String getCreationDate() {
        return creationDate;
    }
    @JsonProperty
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    @JsonProperty
    public UUID getUuid() {
        return uuid;
    }
    @JsonProperty
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
    @JsonProperty
    public String getDateOfBirth() { return dateOfBirth; }
    @JsonProperty
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
