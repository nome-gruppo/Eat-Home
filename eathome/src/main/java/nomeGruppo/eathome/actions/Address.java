package nomeGruppo.eathome.actions;

public class Address {
    private int idAddress;
    private String address;
    private String numberAddress;
    private String city;

    public Address(int idAddress,String address,String numberAddress,String city){
        this.idAddress=idAddress;
        this.address=address;
        this.numberAddress=numberAddress;
        this.city=city;
    }

    public int getIdAddress() {
        return idAddress;
    }

    public void setIdAddress(int idAddress) {
        this.idAddress = idAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumberAddress() {
        return numberAddress;
    }

    public void setNumberAddress(String numberAddress) {
        this.numberAddress = numberAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
