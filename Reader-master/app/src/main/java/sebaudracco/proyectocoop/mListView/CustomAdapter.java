package sebaudracco.proyectocoop.mListView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import sebaudracco.proyectocoop.R;
import sebaudracco.proyectocoop.mDataObject.Spacecraft;

import static sebaudracco.proyectocoop.R.drawable.img_base;

public class CustomAdapter extends BaseAdapter {

    Context c;
    ArrayList<Spacecraft> spacecrafts;
    LayoutInflater inflater;
    Spacecraft spacecraft;
    ImageView ima;

    public CustomAdapter(Context c, ArrayList<Spacecraft> spacecrafts) {
        this.c = c;
        this.spacecrafts = spacecrafts;
    }

    @Override
    public int getCount() {
        return spacecrafts.size();
    }

    @Override
    public Object getItem(int position) {
        return spacecrafts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(inflater==null)
        {
            inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView==null)
        {
            convertView=inflater.inflate( R.layout.model,parent,false);
        }

        //BIND DATA
        MyViewHolder holder=new MyViewHolder(convertView);
        holder.nameTxt.setText(spacecrafts.get(position).getId());
        holder.nameTxt2.setText(spacecrafts.get(position).getName());
        holder.nameTxt3.setText(spacecrafts.get(position).getAddress());
        holder.nameTxt4.setText(spacecrafts.get(position).getMedidor());
        holder.nameTxt5.setText(spacecrafts.get(position).getNueva());
        // Aqui muestro la foto!
        holder.imagemId.setImageBitmap(spacecrafts.get(position).getBitmap2());






        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(c, spacecrafts.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.setLongClickListener(new MyLongClickListener() {
            @Override
            public void onItemLongClick() {
                spacecraft= (Spacecraft) getItem(position);
            }
        });

        return convertView;
    }



    //EXPOSE NAME AND ID
    public String getSelectedItemID()
    {
        return spacecraft.getId();
    }
    public String getSelectedItemName()
    {
        return spacecraft.getName();
    }
    public String getSelectedItemAddress()
    {
        return spacecraft.getAddress();
    }
    public String getSelectedItemNuevo()
    {
        return spacecraft.getNueva();
    }
    public String getSelectedItemMedidor(){return  spacecraft.getMedidor ();}
    public Bitmap getSelectedItemFoto(){return  spacecraft.getBitmap2 ();}
}
