package sebaudracco.proyectocoop.mDataObject;



// OBJETO SOCIO

import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

public class Spacecraft {

    String name;
    String id;
    String address;
    String medidor;
    String nueva;
    ImageView foto;
    Bitmap img;

    public Spacecraft() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedidor(){return medidor;}

    public void  setMedidor(String medidor) {this.medidor=medidor;}


    public String getNueva() {
        return nueva;
    }

    public void setNueva(String nueva) {
        this.nueva = nueva;
    }

    public ImageView getBitmap(){return foto;}

    public void  setBitmap(ImageView foto) {this.foto=foto;}

    public void add() {
        Spacecraft spacecraft = new Spacecraft ();
    }


    public void setBitmap2(Bitmap img) { this.img=img; }
    public  Bitmap getBitmap2() {return img;}


}
