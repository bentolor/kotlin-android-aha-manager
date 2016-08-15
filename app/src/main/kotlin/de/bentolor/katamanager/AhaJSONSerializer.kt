package de.bentolor.katamanager

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

class AhaJSONSerializer(private val mContext: Context, private val mFileName: String) {

    fun saveAhas(ahas: List<Aha>) {
        val jsonArray = JSONArray()
        ahas.forEach { jsonArray.put(toJSON(it)) }

        mContext.openFileOutput(mFileName, Context.MODE_PRIVATE).use { out ->
            OutputStreamWriter(out).use { writer ->
                writer.write(jsonArray.toString())
            }
        }
    }

    fun loadCrimes(): MutableList<Aha> {
        val ahas = mutableListOf<Aha>()
        try {
            mContext.openFileInput(mFileName).use { fin ->
                BufferedReader(InputStreamReader(fin)).use { reader ->
                    val jsonText = reader.readText()
                    val array = JSONTokener(jsonText).nextValue() as JSONArray
                    for (i in 0..array.length() - 1) {
                        ahas.add(fromJSON(array.getJSONObject(i)))
                    }
                }
            }
        } catch (e: FileNotFoundException) {
            // Ignore this one; it happens when starting fresh
        }
        return ahas
    }


    companion object {
        private val JSON_ID = "id"
        private val JSON_TITLE = "title"
        private val JSON_USEFUL = "useful"
        private val JSON_DATE = "date"

        private fun fromJSON(json: JSONObject): Aha {
            return Aha(id = UUID.fromString(json.getString(JSON_ID)),
                    title = json.getString(JSON_TITLE),
                    date = Date(json.getLong(JSON_DATE)),
                    isUseful = json.getBoolean(JSON_USEFUL))
        }

        private fun toJSON(aha: Aha): JSONObject {
            with(JSONObject()) {
                put(JSON_ID, aha.id.toString())
                put(JSON_TITLE, aha.title)
                put(JSON_USEFUL, aha.isUseful)
                put(JSON_DATE, aha.date.time)
                return this
            }
        }

    }
}
