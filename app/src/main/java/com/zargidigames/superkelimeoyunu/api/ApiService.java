package com.zargidigames.superkelimeoyunu.api;

import com.zargidigames.superkelimeoyunu.model.Level;
import com.zargidigames.superkelimeoyunu.model.Question;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by ilimturan on 08/11/15.
 */
public interface ApiService {

    @Headers({
            "api_key: "+ApiConfig.API_KEY
    })
    @GET("/getWordGameQuestions/{level}")
    public void getQuestions(@Path("level") Integer level, Callback<List<Question>> response);


    //http://zargidigames.com/index.php/wordgameapi/getWordGames/turkish
    @Headers({
            "api_key: "+ApiConfig.API_KEY
    })
    @GET("/getWordGames/{lang}")
    public void getLevels(@Path("lang") String lang, Callback<List<Level>> response);
}
