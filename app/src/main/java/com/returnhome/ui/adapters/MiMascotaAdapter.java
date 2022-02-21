package com.returnhome.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.models.RHRespuesta;
import com.returnhome.ui.activities.mascota.MapaNotificacionMascotaDesaparecidaActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MiMascotaAdapter extends RecyclerView.Adapter<MiMascotaAdapter.MiMascotaViewHolder> {


    //LISTA MASCOTAS SE ENVIAN AL RECYCLERVIEW
    private Button mButtonUpdate;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputBreed;
    private TextInputEditText mTextInputDescription;
    private RadioButton mRadioButtonMalePet;
    private RadioButton mRadioButtonFemalePet;
    private ArrayList<Mascota> mascotaArrayList;
    private BottomSheetDialog mBottomSheetDialog;
    LayoutInflater inflater;
    Context context;

    public MiMascotaAdapter(Context context, ArrayList<Mascota> mascotaArrayList){
        this.context=context;
        this.inflater = LayoutInflater.from(context);
        this.mascotaArrayList = mascotaArrayList;
    }

    @NonNull
    @Override
    //VA A INFLAR LA VISTA
    //RELLENA CADA ELEMENTO DEL RECYCLER VIEW CON UNA VISTA(texto, imagen)
    public MiMascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //SE CREA LA VISTA DE CADA ITEM
        //EL ATTACHTOROOT SERA FALSE PARA NO CARGAR  LOS ITEMS EN EL RECYCLER MUY RAPIDO
        //CON EL INFLATER SE PROCEDE A DIBUJAR LA VISTA
        //COMO PARAMETRO TAMBIEN SE INDICA EL DISEÑO QUE TENDRA CADA ITEM DE LA LISTA
        View view = inflater.inflate(R.layout.cardview_my_pets, parent, false);
        //SE ENVIA LA VISTA AL CONSTRUCTOR DE LA CLASE PETSVIEWHOLDER
        MiMascotaViewHolder petsViewHolder = new MiMascotaViewHolder(view);
        return petsViewHolder;
    }

    //CADA ITEM DEL RECYCLERVIEW HACE USO DE LA VISTA CREADA EN EL VIEWHOLDER
    //DE MANERA QUE TODOS LOS ITEMS COMPARTAN UN MISMO DISEÑO

    //POSITION: POSICION DE CADA ITEM DE LA LISTA

    //VA A BINDEAR A CADA VISTA DEL VIEWHOLDER CON LOS DATOS
    //PARA POBLAR CADA POSICION
    @Override
    public void onBindViewHolder(@NonNull MiMascotaViewHolder holder, int position) {
        //A CADA POSICION DEL RECYCLER TENDRA LA MISMA VISTA CREADA EN ONCREATEVIEWHOLDER
        holder.mTextViewNamePet.setText(mascotaArrayList.get(position).getNombre());
        holder.mTextViewBreedPet.setText(mascotaArrayList.get(position).getRaza());

        holder.mButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, holder);
            }
        });




    }

    private void showMenu(View v, MiMascotaViewHolder holder ) {
        PopupMenu popupMenuCardView = new PopupMenu(v.getContext(), v);
        popupMenuCardView.inflate(R.menu.menu_cardview);

        popupMenuCardView.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.cardview_edit:

                        mBottomSheetDialog = new BottomSheetDialog(context);
                        mBottomSheetDialog.setContentView(R.layout.popup_update);
                        mBottomSheetDialog.setCanceledOnTouchOutside(true);

                        initializeComponents();

                        mButtonUpdate.setText("Actualizar");

                        mTextInputName.setText(mascotaArrayList.get(holder.getBindingAdapterPosition()).getNombre());
                        mTextInputBreed.setText(mascotaArrayList.get(holder.getBindingAdapterPosition()).getRaza());
                        mTextInputDescription.setText(mascotaArrayList.get(holder.getBindingAdapterPosition()).getDescripcion());
                        if(mascotaArrayList.get(holder.getBindingAdapterPosition()).getGenero()=='M'){
                            mRadioButtonMalePet.setChecked(true);
                        }
                        else{
                            mRadioButtonFemalePet.setChecked(true);
                        }

                        mBottomSheetDialog.show();

                        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickUpdate(holder);
                            }
                        });

                        return true;

                    case R.id.cardview_delete:

                        AlertDialog builder = new AlertDialog.Builder(context).create();
                        builder.setTitle("ReturnHOME");
                        builder.setMessage("Esta seguro que desea eliminar su mascota?");
                        builder.setButton(AlertDialog.BUTTON_POSITIVE, "SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deletePet(holder);
                            }
                        });
                        builder.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                builder.cancel();
                            }
                        });

                        builder.show();

                    return true;

                    case R.id.cardview_missing_pet:
                        int idPet = mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota();
                        String petName = mascotaArrayList.get(holder.getBindingAdapterPosition()).getNombre();

                        Intent intent = new Intent(context, MapaNotificacionMascotaDesaparecidaActivity.class);
                        intent.putExtra("idPet",idPet);
                        intent.putExtra("pet_name",petName);
                        context.startActivity(intent);


                        return true;

                    case R.id.cardview_found_pet:
                        int idPet1= mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota();
                        Mascota mascota1 = new Mascota(idPet1,false);
                        updateStatusMissingPet(mascota1);

                        return true;

                    default:
                        return false;
                }
            }
        });
        popupMenuCardView.show();
    }

    private void updateStatusMissingPet(Mascota mascota){

        MascotaController.actualizarMascotaDesaparecida(mascota).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {

                if(response.isSuccessful()){
                    Toast.makeText(context, "Mascota reportada como encontrada", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "La mascota no se encuentra desaparecida", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(context, "Se ha producido un error", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void deletePet(MiMascotaViewHolder holder){
        MascotaController.eliminar(mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota()).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if (response.isSuccessful()) {
                    mascotaArrayList.remove(holder.getBindingAdapterPosition());
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(context, "Eliminación fallida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clickUpdate(MiMascotaViewHolder holder) {
        int idPet = mascotaArrayList.get(holder.getBindingAdapterPosition()).getIdMascota();
        String name = mTextInputName.getText().toString();
        String breed = mTextInputBreed.getText().toString();
        String description = mTextInputDescription.getText().toString();
        char gender = ((mRadioButtonMalePet.isChecked() ? 'M' : 'F'));
        int position = holder.getBindingAdapterPosition();


        if(!name.isEmpty() && !breed.isEmpty()){
                //DATOS INGRESADOS CORRECTAMENTE
            updatePet(new Mascota(idPet,name,breed,gender,description), position);

        }
        else{
            Toast.makeText(context, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();

        }

    }

    private void updatePet(Mascota mascota, int position) {
        MascotaController.actualizar(mascota).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {


                if(response.isSuccessful()){
                    mascotaArrayList.get(position).setNombre(mascota.getNombre());
                    mascotaArrayList.get(position).setRaza(mascota.getRaza());
                    mascotaArrayList.get(position).setDescripcion(mascota.getDescripcion());
                    mascotaArrayList.get(position).setGenero(mascota.getGenero());
                    notifyItemChanged(position);
                    mBottomSheetDialog.dismiss();
                    Toast.makeText(context, "Actualización exitosa", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "No hubo ninguna actualización", Toast.LENGTH_SHORT).show();
                    mBottomSheetDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(context, "Actualización fallida", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });


    }

    //OBTIENE LOS ELEMENTOS EN EL RECYCLER VIEW
    @Override
    public int getItemCount() {
        return mascotaArrayList.size();
    }

    private void initializeComponents(){
        mButtonUpdate = mBottomSheetDialog.findViewById(R.id.btnUpdateAddPet);
        mTextInputName = mBottomSheetDialog.findViewById(R.id.textInputNamePet);
        mTextInputBreed = mBottomSheetDialog.findViewById(R.id.textInputBreed);
        mTextInputDescription = mBottomSheetDialog.findViewById(R.id.textInputDescription);
        mRadioButtonMalePet = mBottomSheetDialog.findViewById(R.id.radioButtonMalePet);
        mRadioButtonFemalePet = mBottomSheetDialog.findViewById(R.id.radioButtonFemalePet);
    }


    //MANEJA LOS ELEMENTOS QUE CONTIENE LA VISTA DE CADA ITEM DENTRO DEL RECYCLER
    public class MiMascotaViewHolder extends RecyclerView.ViewHolder {

        TextView mTextViewNamePet;
        TextView mTextViewBreedPet;
        ImageView mImageViewPet;
        AppCompatImageButton mButtonMenu;



        public MiMascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewNamePet = itemView.findViewById(R.id.namePet);
            mTextViewBreedPet = itemView.findViewById(R.id.breedPet);
            mImageViewPet = itemView.findViewById(R.id.imageView_pet);
            mButtonMenu = itemView.findViewById(R.id.imageButton_menu);


        }


    }


}