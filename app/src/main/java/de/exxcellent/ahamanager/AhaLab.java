package de.exxcellent.ahamanager;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class AhaLab {
    private static final String TAG = "AhaLab";
    private static final String FILENAME = "ahas.json";
    private static AhaLab sAhaLab;
    private ArrayList<Aha> mAhas;
    private AhaIntentJSONSerializer mSerializer;
    private Context mAppContext;

    private AhaLab(Context appContext) {
        mAppContext = appContext;
        mSerializer = new AhaIntentJSONSerializer(mAppContext, FILENAME);

        try {
            mAhas = mSerializer.loadCrimes();
        } catch (Exception e) {
            mAhas = new ArrayList<Aha>();
            Log.e(TAG, "Error loading crimes: ", e);
        }

//		CreateCrimes(100);
    }

    public static AhaLab get(Context c) {
        if (sAhaLab == null) {
            sAhaLab = new AhaLab(c.getApplicationContext());
        }
        return sAhaLab;
    }

    public ArrayList<Aha> getCrimes() {
        return mAhas;
    }

    public Aha getCrime(UUID id) {
        for (Aha c : mAhas) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }

    public void addCrime(Aha c) {
        mAhas.add(c);
    }

    public void deleteCrime(Aha c) {
        mAhas.remove(c);
    }

    public boolean saveCrimes() {
        try {
            mSerializer.saveCrimes(mAhas);
            Log.d(TAG, "crimes saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }

    private void CreateCrimes(int count) {
        for (int i = 0; i < 100; i++) {
            Aha c = new Aha();
            c.setTitle("Aha #" + i);
            c.setSolved(i % 2 == 0); // Alternate
            mAhas.add(c);
        }
    }
}
