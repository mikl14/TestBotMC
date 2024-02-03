package io.projectBot.TestBot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MasterClassList {
    static List<MasterClass> masterClassList = new ArrayList<>();

    public static List<MasterClass> getMasterClassList() {
        return masterClassList;
    }

    MasterClassList(MasterClass[] masterClasses) {
        masterClassList.addAll(Arrays.asList(masterClasses));

        try {
            masterClassList = XmlService.readDataFile();
        } catch (IOException ignored) {

        }
    }

    public static MasterClass findByName(String name) {
        for (MasterClass master : masterClassList) {
            if (master.name.equals(name)) return master;
        }
        return null;
    }
    public String allUsers()
    {
        StringBuffer sb = new StringBuffer();
        for(MasterClass master:masterClassList){
            sb.append(master.userToString()+ '\n');
        }
        return sb.toString();
    }
}
