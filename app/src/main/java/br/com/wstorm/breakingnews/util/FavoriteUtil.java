package br.com.wstorm.breakingnews.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.com.wstorm.breakingnews.model.News;

/**
 * Created by wstorm on 12/22/15.
 */
public class FavoriteUtil {

    public static boolean insert(Context context, News news) {

        List<News> newsList = FavoriteUtil.list(context);

        if (newsList == null) {

            newsList = new ArrayList<>();

        }

        if (!newsList.contains(news)) {

            newsList.add(news);

            return FavoriteUtil.save(context, newsList);

        }

        return false;

    }

    public static boolean delete(Context context, News news) {

        List<News> newsList = FavoriteUtil.list(context);

        if (newsList.remove(news)) {

            return FavoriteUtil.save(context, newsList);

        }

        return false;

    }

    public static List<News> list(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences("breaking_news", Context.MODE_PRIVATE);
        String json = sharedPref.getString("favorites", "");

        GsonBuilder b = new GsonBuilder();
        b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        Gson gson = b.create();

        Type listType = new TypeToken<ArrayList<News>>() {}.getType();
        List<News> newsList = gson.fromJson(json, listType);

        return newsList;

    }

    private static boolean save(Context context, List<News> newsList) {

        SharedPreferences sharedPref = context.getSharedPreferences("breaking_news", Context.MODE_PRIVATE);
        String json = sharedPref.getString("favorites", "");

        GsonBuilder b = new GsonBuilder();
        b.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");
        Gson gson = b.create();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("favorites", gson.toJson(newsList));
        return editor.commit();

    }

}
