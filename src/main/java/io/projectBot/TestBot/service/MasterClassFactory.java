package io.projectBot.TestBot.service;

import java.util.ArrayList;
import java.util.List;

public class MasterClassFactory {
    static public MasterClass[] createMasterClasses(String[] masternames,int maxUsers)
    {
        List<MasterClass> masterList = new ArrayList<>();
        for (String name:masternames) {
            MasterClass master = new MasterClass(name,maxUsers);
            masterList.add(master);
        }
        return masterList.toArray(new MasterClass[0]);
    }

    static public MasterClass[] createMasterClasses(String[] masternames,int[] maxUsers)
    {
        List<MasterClass> masterList = new ArrayList<>();
        for (int i = 0 ; i < masternames.length;i++) {
            MasterClass master = new MasterClass(masternames[i],maxUsers[i]);
            masterList.add(master);
        }
        return masterList.toArray(new MasterClass[0]);
    }
}
