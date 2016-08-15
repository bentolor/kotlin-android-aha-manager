package de.bentolor.ahamanager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class AhaLab {
    private static final String TAG = "AhaLab";
    private static final String FILENAME = "ahas.json";
    private static AhaLab sAhaLab;
    private ArrayList<Aha> mAhas;
    private AhaJSONSerializer mSerializer;

    private AhaLab(Context appContext) {
        mSerializer = new AhaJSONSerializer(appContext, FILENAME);

        try {
            mAhas = mSerializer.loadCrimes();
        } catch (Exception e) {
            mAhas = new ArrayList<Aha>();
            Log.e(TAG, "Error loading Ahas: ", e);
        }

        createAhas(7);
    }

    public static AhaLab get(Context c) {
        if (sAhaLab == null) {
            sAhaLab = new AhaLab(c.getApplicationContext());
        }
        return sAhaLab;
    }

    public ArrayList<Aha> getAhas() {
        return mAhas;
    }

    public Aha getAha(UUID id) {
        for (Aha c : mAhas) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void addAha(Aha c) {
        mAhas.add(c);
    }

    public void deleteAha(Aha c) {
        mAhas.remove(c);
    }

    public boolean saveAhas() {
        try {
            mSerializer.saveAhas(mAhas);
            Log.d(TAG, "Ahas saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving Ahas: ", e);
            return false;
        }
    }

    private void createAhas(int count) {
        for (int i = 0; i < count; i++) {
            Aha aha = new Aha();
            aha.setTitle("Aha #" + i);
            aha.setUseful((i % 2) == 0); // Alternate
            mAhas.add(aha);
        }
    }
}
