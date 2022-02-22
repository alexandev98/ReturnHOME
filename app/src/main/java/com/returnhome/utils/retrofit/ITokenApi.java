package com.returnhome.utils.retrofit;

import com.returnhome.models.Cliente;
import com.returnhome.models.RHRespuesta;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ITokenApi {

    @Headers({
            "Content-Type:application/json"
    })

    @PUT("ws_registrarToken.php")
    Call<Void> registrar(@Body Map<String,String> tokenInfo);

    @GET("ws_obtenerToken.php")
    Call<RHRespuesta> obtener(@Query("idCliente") int idCliente);

    @HTTP(method = "DELETE", path = "ws_eliminarToken.php", hasBody = true)
    Call<Void> eliminar(@Body Map<String,String> tokenInfo);

}
