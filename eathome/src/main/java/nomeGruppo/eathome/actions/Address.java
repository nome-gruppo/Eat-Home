package nomeGruppo.eathome.actions;

import androidx.annotation.Nullable;

public class Address {

    private static final String DIVIDER = ", ";
    private static final int TOKEN_NUM = 3;

    private int idAddress;
    private String city;
    private String street;
    private String numberAddress;

    public Address(String city, String street, String numberAddress) {
        this.city = city.trim();
        this.street = street.trim();
        this.numberAddress = numberAddress.trim();
    }

    public Address(int idAddress, String city, String street, String numberAddress) {
        this(city, street, numberAddress);
        this.idAddress = idAddress;
    }

    public Address(int idAddress, Address address){
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

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumberAddress() {
        return numberAddress;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if(obj instanceof Address){
            final String objCity = ((Address) obj).getCity().toLowerCase();
            final String objStreet = ((Address) obj).getStreet().toLowerCase();
            final String objAddressNum = ((Address) obj).getNumberAddress().toLowerCase();

            if(objCity.equals(this.city.toLowerCase()) && objStreet.equals(this.street.toLowerCase()) && objAddressNum.equals(this.numberAddress.toLowerCase())){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }

    }
}
