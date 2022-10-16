package com.prs;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JsonManipulator {

    public static void main(String[] args) throws IOException {
        List<String> input = new ArrayList<>();
        String field = "address.city";
        String operator = "IN";
        String value = "Kolkata,Delhi";
        input.add(field);
        input.add(operator);
        input.add(value);
        List<Integer> ans = apiResponseParser(input, input.size());
        System.out.println(ans);
    }

    public static List<Integer> apiResponseParser(List<String> inputList, int size) throws IOException {

        List<Integer> ans = new ArrayList<Integer>();
        try {
            if (size < 3) return null;

            String field = inputList.get(0);
            String operator = inputList.get(1);
            String value = inputList.get(2);
            String[] valArr = null;
            if (operator.equals("IN")) {
                valArr = value.split("\\,");
            }
            else {
                valArr = new String[1];
                valArr[0] = value;
            }

            String[] fieldArr = null;
            if (field.contains(".")) {
                fieldArr = field.split("\\.");
            }
            else {
                fieldArr = new String[1];
                fieldArr[0] = field;
            }

            JsonArray jsonArray = JsonParser.parseString(getJson()).getAsJsonArray();

            int n = jsonArray.size();
            for (int i =0; i<n ; i++) {
                JsonObject jsonObj = jsonArray.get(i).getAsJsonObject();

                JsonObject nestedObj = jsonObj;
                if(fieldArr.length > 1) {
                    for (int k =0; k < fieldArr.length -1; k++)
                        nestedObj = nestedObj.get(fieldArr[k]).getAsJsonObject();
                }
                String jsonValue = nestedObj.get(fieldArr[fieldArr.length-1]).getAsString();
                if (operator.equals("EQUALS")) {
                    if (jsonValue.equals(valArr[0])) {
                        ans.add(jsonObj.get("id").getAsInt());
                    }
                }
                else if (operator.equals("IN")) {
                    for (String s : valArr)
                        if (jsonValue.equals(s)) {
                            ans.add(jsonObj.get("id").getAsInt());
                        }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return ans;
    }

    public static String getJson() throws Exception{
        //https://raw.githubusercontent.com/arcjsonapi/expressionDataService/main/test1
        URL url = new URL ("https://raw.githubusercontent.com/arcjsonapi/ApiSampleData/master/api/users");
        HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
        httpCon.setRequestMethod("GET");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpCon.getInputStream()))){
            for (String line; (line = reader.readLine()) != null;) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}


