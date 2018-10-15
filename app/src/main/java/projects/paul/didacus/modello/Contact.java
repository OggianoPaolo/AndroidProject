package projects.paul.didacus.modello;

/*
    Classe che implementa l'interfaccia Contact
*/


public class Contact {

    private long   idContact;
    private String name;
    private String surname;
    private long   phone;
    private String address;
    private byte[] image;
    private String company;
    private String mail;
    private String job;

    // == Costruttore di default
    public Contact(){}

    // == Costruttore
    public Contact(long id, String name, String surname, long phone, String address, byte[] image, String company, String mail, String job) {
        this.idContact = id;
        this.name      = name;
        this.surname   = surname;
        this.phone     = phone;
        this.address   = address;
        this.image     = image;
        this.company   = company;
        this.mail      = mail;
        this.job       = job;
    }

    public long getIdContact() {return idContact;}

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public long getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public byte[] getImage() { return image; }

    public String getCompany() { return company; }

    public String getMail() { return mail; }

    public String getJob() { return job; }


    public void setIdContact(long idContact) {
        this.idContact = idContact;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImage(byte[] image) { this.image = image; }

    public void setCompany(String company) { this.company = company; }

    public void setMail(String mail){ this.mail = mail; }

    public void setJob(String job){ this.job = job; }
}
