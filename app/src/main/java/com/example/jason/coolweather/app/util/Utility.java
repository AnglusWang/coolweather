package com.example.jason.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.jason.coolweather.app.db.CoolWeatherDB;
import com.example.jason.coolweather.app.entity.City;
import com.example.jason.coolweather.app.entity.County;
import com.example.jason.coolweather.app.entity.Province;
import com.example.jason.coolweather.app.entity.WeatherInfo;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jason on 4/9/2016.
 * 工具类，用于解析和处理 服务器返回的省市县数据(格式：代号|城市，代号|城市)
 */
public class Utility {

    /**
     * 解析和处理服务器返回的 省级 数据
     */
    public synchronized static boolean handleProvinceResponse(
            CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {

                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的 市级 数据
     */
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,
                                               String response, int ProvinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(ProvinceId);
                    //将解析处理的数据存储到City表
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的 县级 数据
     */
    public static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                 String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allcounties = response.split(",");
            if (allcounties != null && allcounties.length > 0) {
                for (String c : allcounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
     */
    public static void handleWeatherResponse(Context context, String response) {

        Gson gson = new Gson();
        WeatherInfo info = gson.fromJson(response, WeatherInfo.class);

        String cityName = info.getRetData().getCity();
        String weatherCode = info.getRetData().getCitycode();
        String temp1 = info.getRetData().getL_tmp();
        String temp2 = info.getRetData().getH_tmp();
        String weatherDesc = info.getRetData().getWeather();
        String publishTime = info.getRetData().getTime();

        saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
                weatherDesc, publishTime);

//        try {
//            JSONObject jsonObject = new JSONObject(response);
//            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
//
//            String cityName = weatherInfo.getString("city");
//            String weatherCode = weatherInfo.getString("cityid");
//            String temp1 = weatherInfo.getString("temp1");
//            String temp2 = weatherInfo.getString("temp2");
//            String weatherDesc = weatherInfo.getString("weather");
//            String publishTime = weatherInfo.getString("pubtime");
//
//            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2,
//                    weatherDesc, publishTime);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * 将服务返回的所有天气信息存储到SharedPreferences文件中
     */
    private static void saveWeatherInfo(Context context, String cityName,
                                        String weatherCode, String temp1, String temp2, String weatherDesc,
                                        String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desc", weatherDesc);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}


