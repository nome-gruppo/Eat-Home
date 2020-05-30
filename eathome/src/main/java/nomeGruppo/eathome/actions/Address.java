package nomeGruppo.eathome.actions;

public class Address {

    private static final String DIVIDER = ", ";
    private static final int TOKEN_NUM = 3;

    private int idAddress;
    private String city;
    private String street;
    private String numberAddress;


    public Address(int idAddress, String city, String street, String numberAddress) {
        this.idAddress = idAddress;
        this.city = city;
        this.street = street;
        this.numberAddress = numberAddress;
    }

    public Address(String city, String street, String numberAddress) {
        this.city = city;
        this.street = street;
        this.numberAddress = numberAddress;
    }

    public Address(String fullAddress) {
        String[] tokens = fullAddress.split(DIVIDER);

        if (tokens.length != TOKEN_NUM) {
            throw new IllegalArgumentException();
        } else {
            this.city = tokens[0];
            this.street = tokens[1];
            this.numberAddress = tokens[2];
        }
    }

    public String getFullAddress() {
        return city + DIVIDER + street + DIVIDER + numberAddress;
    }


    public int getIdAddress() {
        return idAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumberAddress() {
        return numberAddress;
    }

    public String getCity() {
        return city;
    }
}
