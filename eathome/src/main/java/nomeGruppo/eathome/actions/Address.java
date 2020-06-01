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
        this.city = city.trim();
        this.street = street.trim();
        this.numberAddress = numberAddress.trim();
    }

    public Address(String city, String street, String numberAddress) {
        this.city = city.trim();
        this.street = street.trim();
        this.numberAddress = numberAddress.trim();
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
