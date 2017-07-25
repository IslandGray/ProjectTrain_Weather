package com.wcp.data;

/**
 * Created by DELL-pc on 2017/7/24 0024.
 */

public class CityData {
    private String Province;
    private String City;

    public CityData(String pro,String city){
        Province=pro;
        City=city;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }
}
