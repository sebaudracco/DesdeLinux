package sebaudracco.proyectocoop.mListView;

import android.graphics.Bitmap;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sebaudracco.proyectocoop.R;

public class MyViewHolder implements View.OnLongClickListener,View.OnCreateContextMenuListener {

    TextView nameTxt, nameTxt2, nameTxt3, nameTxt4, nameTxt5;
    ImageView imagemId;
    Bitmap img;
    MyLongClickListener longClickListener;

    public MyViewHolder(View v) {
        nameTxt=   v.findViewById( R.id.nameTxt);
        nameTxt2=  v.findViewById( R.id.nameTxt2);
        nameTxt3=  v.findViewById( R.id.nameTxt3);
        nameTxt4=  v.findViewById( R.id.nameTxt4);
        nameTxt5=  v.findViewById( R.id.nameTxt5);
  //      imagemId=  v.findViewById ( R.id.imagemId);
      //  img=v.findViewById (R.id.img);


        v.setOnLongClickListener(this);
        v.setOnCreateContextMenuListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        this.longClickListener.onItemLongClick();
        return false;
    }

    public void setLongClickListener(MyLongClickListener longClickListener)
    {
        this.longClickListener=longClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Se Requiere : ");

        menu.add(0, 0, 0, "NUEVO REGISTRO");
        menu.add(0,2,0,"EDITAR ULTIMO ESTADO");
        menu.add(0,3,0,"BORRAR");


    }
}
