package com.solusi247.fatkhul.chanthelbeta.activities;

/**
 * Created by 247 on 27/03/2018.
 */

public interface ChanthelInterface {
    void GetData(String pid);
    void DeleteData (String pid);
    void CreateFolder (String pid);
    void RenameData (String pid);
    void OpenData (String pid);
}

