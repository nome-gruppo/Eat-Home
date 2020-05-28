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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumberAddress() {
        return numberAddress;
    }

    public String getCity() {
        return city;
    }
}
