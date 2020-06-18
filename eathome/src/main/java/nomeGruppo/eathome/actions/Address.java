package nomeGruppo.eathome.actions;

import androidx.annotation.Nullable;

public class Address {

    private static final String DIVIDER = ", ";

    private int idAddress;
    private final String city;
    private final String street;
    private final String numberAddress;

    public Address(String city, String street, String numberAddress) {
        this.city = city.trim();
        this.street = street.trim();
        this.numberAddress = numberAddress.trim();
    }

    public Address(int idAddress, String city, String street, String numberAddress) {
        this(city, street, numberAddress);
        this.idAddress = idAddress;
    }

    public Address(int idAddress, Address address) {
        this(address.getCity(), address.getStreet(), address.getNumberAddress());
        this.idAddress = idAddress;
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

    public String getNumberAddress() {
        return numberAddress;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj instanceof Address) {
            final String objCity = ((Address) obj).getCity().toLowerCase();
            final String objStreet = ((Address) obj).getStreet().toLowerCase();
            final String objAddressNum = ((Address) obj).getNumberAddress().toLowerCase();

            return objCity.equals(this.city.toLowerCase()) && objStreet.equals(this.street.toLowerCase()) && objAddressNum.equals(this.numberAddress.toLowerCase());
        } else {
            return false;
        }

    }
}
