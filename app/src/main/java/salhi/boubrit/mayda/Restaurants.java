package salhi.boubrit.mayda;

import android.location.Address;

public class Restaurants {
    public String name;
    public String proprietaire;
    public String num_tel;
    public String adressRes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(String proprietaire) {
        this.proprietaire = proprietaire;
    }

    public String getNum_tel() {
        return num_tel;
    }

    public void setNum_tel(String num_tel) {
        this.num_tel = num_tel;
    }

    public String getAdressRes() {
        return adressRes;
    }

    public void setAdressRes(String adressRes) {
        this.adressRes = adressRes;
    }

    public Restaurants(){

    }

    public Restaurants(String name,String proprietaire, String num_tel, String adressRes){
         this.name = name ;
         this.proprietaire = proprietaire;
         this.num_tel = num_tel;
        this.adressRes = adressRes;
    }


}
