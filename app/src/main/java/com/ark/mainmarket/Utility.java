package com.ark.mainmarket;

import android.content.Context;
import android.content.Intent;

public class Utility {

    public static void updateUI(Context from, Class to){
        Intent intent = new Intent(from, to);
        from.startActivity(intent);
    }
}
