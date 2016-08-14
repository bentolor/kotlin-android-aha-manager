package de.exxcellent.ahamanager;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AhaJSONSerializer {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_USEFUL = "useful";
    private static final String JSON_DATE = "date";

    private final Context mContext;
    private final String mFileName;

    public AhaJSONSerializer(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
    }

    public void saveAhas(List<Aha> ahas)
            throws JSONException, IOException {
        // Build an array in JSON
        JSONArray array = new JSONArray();
        for (Aha aha : ahas) {
            array.put(toJSON(aha));
        }

        // Write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }

    public ArrayList<Aha> loadCrimes() throws IOException, JSONException {
        ArrayList<Aha> ahas = new ArrayList<Aha>();
        BufferedReader reader = null;
        try {
            // Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
            // Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //Build the array of ahas from JSONObjects
            for (int i = 0; i < array.length(); i++) {
                ahas.add(createAha(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return ahas;
    }

    private static Aha createAha(JSONObject json) throws JSONException {
        UUID id = UUID.fromString(json.getString(JSON_ID));
        String title = json.getString(JSON_TITLE);
        Date  date = new Date(json.getLong(JSON_DATE));
        boolean   solved = json.getBoolean(JSON_USEFUL);

        return new Aha(id, title, date, solved);
    }

    private JSONObject toJSON(Aha aha) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, aha.getId().toString());
        json.put(JSON_TITLE, aha.getTitle());
        json.put(JSON_USEFUL, aha.isUseful());
        json.put(JSON_DATE, aha.getDate());
        return json;
    }
}
