package de.bentolor.katamanager

import android.content.Context

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.ArrayList
import java.util.Date
import java.util.UUID

class AhaJSONSerializer(private val mContext: Context, private val mFileName: String) {

    @Throws(JSONException::class, IOException::class)
    fun saveAhas(ahas: List<Aha>) {
        // Build an array in JSON
        val array = JSONArray()
        for (aha in ahas) {
            array.put(toJSON(aha))
        }

        // Write the file to disk
        var writer: Writer? = null
        try {
            val out = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE)
            writer = OutputStreamWriter(out)
            writer.write(array.toString())
        } finally {
            if (writer != null) {
                writer.close()
            }
        }

    }

    @Throws(IOException::class, JSONException::class)
    fun loadCrimes(): ArrayList<Aha> {
        val ahas = ArrayList<Aha>()
        var reader: BufferedReader? = null
        try {
            // Open and read the file into a StringBuilder
            val `in` = mContext.openFileInput(mFileName)
            reader = BufferedReader(InputStreamReader(`in`))
            val jsonString = StringBuilder()
            var line: String = reader.readLine()
            while (line != null) {
                // Line breaks are omitted and irrelevant
                jsonString.append(line)
                line = reader.readLine()
            }
            // Parse the JSON using JSONTokener
            val array = JSONTokener(jsonString.toString()).nextValue() as JSONArray
            //Build the array of ahas from JSONObjects
            for (i in 0..array.length() - 1) {
                ahas.add(createAha(array.getJSONObject(i)))
            }
        } catch (e: FileNotFoundException) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null) {
                reader.close()
            }
        }
        return ahas
    }

    @Throws(JSONException::class)
    private fun toJSON(aha: Aha): JSONObject {
        val json = JSONObject()
        json.put(JSON_ID, aha.id.toString())
        json.put(JSON_TITLE, aha.title)
        json.put(JSON_USEFUL, aha.isUseful)
        json.put(JSON_DATE, aha.date)
        return json
    }

    companion object {
        private val JSON_ID = "id"
        private val JSON_TITLE = "title"
        private val JSON_USEFUL = "useful"
        private val JSON_DATE = "date"

        @Throws(JSONException::class)
        private fun createAha(json: JSONObject): Aha {
            val id = UUID.fromString(json.getString(JSON_ID))
            val title = json.getString(JSON_TITLE)
            val date = Date(json.getLong(JSON_DATE))
            val solved = json.getBoolean(JSON_USEFUL)

            return Aha(id, title, date, solved)
        }
    }
}
