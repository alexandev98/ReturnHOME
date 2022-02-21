package com.returnhome.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.returnhome.R;
import com.returnhome.controllers.MascotaController;
import com.returnhome.models.Mascota;
import com.returnhome.ui.adapters.MiMascotaAdapter;
import com.returnhome.utils.AppConfig;
import com.returnhome.models.RHRespuesta;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MiMascotaFragment extends Fragment implements View.OnClickListener {

    private MiMascotaAdapter mMiMascotaAdapter;
    private RecyclerView mRecyclerViewPets;
    private AppConfig mAppConfig;
    private FloatingActionButton mFloatingButtonAdd;
    private BottomSheetDialog mBottomSheetDialog;
    private Button mButtonUpdateAddPet;
    private TextInputEditText mTextInputName;
    private TextInputEditText mTextInputBreed;
    private TextInputEditText mTextInputDescription;
    private RadioButton mRadioButtonMalePet;
    private ArrayList<Mascota> mascotaArrayList;

    public MiMascotaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pets, container, false);

        mascotaArrayList = new ArrayList<>();
        mRecyclerViewPets = view.findViewById(R.id.recyclerViewMyPets);
        mAppConfig = new AppConfig(getContext());
        mFloatingButtonAdd = view.findViewById(R.id.fab_addPet);

        initializeComponents();

        mFloatingButtonAdd.setOnClickListener(this);

        mButtonUpdateAddPet.setOnClickListener(this);

        getPets();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab_addPet:

                mButtonUpdateAddPet.setText("Actualizar");
                mBottomSheetDialog.show();
                break;

            case R.id.btnUpdateAddPet:
                clickAdd();
                break;
        }
    }

    private void initializeComponents(){
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        mBottomSheetDialog.setContentView(R.layout.popup_update);
        mBottomSheetDialog.setCanceledOnTouchOutside(true);

        mButtonUpdateAddPet = mBottomSheetDialog.findViewById(R.id.btnUpdateAddPet);
        mTextInputName = mBottomSheetDialog.findViewById(R.id.textInputNamePet);
        mTextInputBreed = mBottomSheetDialog.findViewById(R.id.textInputBreed);
        mTextInputDescription = mBottomSheetDialog.findViewById(R.id.textInputDescription);
        mRadioButtonMalePet = mBottomSheetDialog.findViewById(R.id.radioButtonMalePet);

    }

    private void clickAdd() {
        String name = mTextInputName.getText().toString();
        String breed = mTextInputBreed.getText().toString();
        String description = mTextInputDescription.getText().toString();
        char gender = ((mRadioButtonMalePet.isChecked() ? 'M' : 'F'));


        if(!name.isEmpty() && !breed.isEmpty()){
            //DATOS INGRESADOS CORRECTAMENTE
            createPet(new Mascota(name,breed,gender,description, false, mAppConfig.obtenerIdCliente()));
        }
        else{
            Toast.makeText(getContext(), "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPet(Mascota mascota) {
        MascotaController.registrar(mascota).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascota.setIdMascota(response.body().getMascota().getIdMascota());
                    mascotaArrayList.add(mascota);
                    showList(mascotaArrayList);
                    mBottomSheetDialog.dismiss();
                    mTextInputName.getText().clear();
                    mTextInputName.clearFocus();
                    mTextInputBreed.getText().clear();
                    mTextInputBreed.clearFocus();
                    mTextInputDescription.getText().clear();
                    mTextInputDescription.clearFocus();
                    mRadioButtonMalePet.setChecked(true);
                }
                else{
                    Toast.makeText(getContext(), "Mascota añadida con éxito", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {
                Toast.makeText(getContext(), "Ocurrio un problema al agregar", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getPets() {
        int idClient = mAppConfig.obtenerIdCliente();

        MascotaController.obtener(idClient, 1).enqueue(new Callback<RHRespuesta>() {
            @Override
            public void onResponse(Call<RHRespuesta> call, Response<RHRespuesta> response) {
                if(response.isSuccessful()){
                    mascotaArrayList = response.body().getMascotas();
                    showList(mascotaArrayList);
                }

            }

            @Override
            public void onFailure(Call<RHRespuesta> call, Throwable t) {

            }
        });
    }

    private void showList(ArrayList<Mascota> mascotas) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //MOSTRAR LOS VIEWS DE LA LISTA DE MANERA LINEAL
        mRecyclerViewPets.setLayoutManager(linearLayoutManager);
        //SE ENVIA LA LISTA DE MASCOTAS A PETPROVIDER
        mMiMascotaAdapter = new MiMascotaAdapter(getContext(), mascotas);
        mRecyclerViewPets.setAdapter(mMiMascotaAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}