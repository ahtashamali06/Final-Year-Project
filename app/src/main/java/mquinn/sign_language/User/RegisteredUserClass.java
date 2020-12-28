package mquinn.sign_language.User;

public class RegisteredUserClass {

    String name, email, dateofbirth, gender, phoneNo;

    public RegisteredUserClass() {
    }

    public RegisteredUserClass(String name, String email, String dateofbirth, String gender, String phoneNo) {
        this.name = name;
        this.email = email;
        this.dateofbirth = dateofbirth;
        this.gender = gender;
        this.phoneNo = phoneNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateofbirth() {
        return dateofbirth;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
