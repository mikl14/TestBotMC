package io.projectBot.TestBot.service;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlService {
    public static void writeItemsToCategory() {
        MasterClass[] masterCategories = MasterClassList.getMasterClassList().toArray(new MasterClass[0]);
        Map<String, Integer> masterNames = new HashMap();
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("data.json", false);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (MasterClass masterClass : masterCategories) {
            masterNames.put(masterClass.name, masterClass.getMax_users());
        }
        String[] categories = masterNames.keySet().toArray(new String[0]);

        for (int i = 0; i < categories.length; i++) {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (String item : MasterClassList.findByName(categories[i]).getUsers()) {
                jsonArray.put(item);
            }
            jsonObject.put("category", categories[i]);
            jsonObject.put("size", masterNames.get(categories[i]).intValue());
            jsonObject.put("items", jsonArray);

            try {
                fileWriter = new FileWriter("data.json", true);
                fileWriter.write(jsonObject.toString() + "\n");
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public static List<MasterClass> readDataFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("data.json"));

        List<MasterClass> masterlist = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            JSONObject jsonObject = new JSONObject(line);
            String category = jsonObject.getString("category");
            JSONArray items = jsonObject.getJSONArray("items");
            MasterClass masterClass = new MasterClass(category);
            masterClass.setUsers(items);
            masterlist.add(masterClass);

        }
        bufferedReader.close();
        return masterlist;
    }
}
