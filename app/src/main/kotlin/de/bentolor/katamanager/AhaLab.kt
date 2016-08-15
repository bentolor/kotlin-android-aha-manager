package de.bentolor.katamanager

import android.content.Context
import android.util.Log
import java.util.*

class AhaLab private constructor(appContext: Context) {

    var ahas: MutableList<Aha>
        private set

    private val mSerializer: AhaJSONSerializer = AhaJSONSerializer(appContext, FILENAME)

    init {
        try {
            ahas = mSerializer.loadCrimes()
            Log.d(TAG, "${ahas.size} Ahas loaded")
        } catch (e: Exception) {
            ahas = mutableListOf()
            Log.e(TAG, "Error loading Ahas: ", e)
        }

        if (ahas.size == 0) createAhas(7)
    }

    fun getAha(id: UUID): Aha? = ahas.filter { it.id == id }.firstOrNull()

    fun addAha(c: Aha) = ahas.add(c)

    fun deleteAha(c: Aha) = ahas.remove(c)

    fun saveAhas(): Boolean {
        try {
            mSerializer.saveAhas(ahas)
            Log.d(TAG, "Ahas saved to file")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving Ahas: ", e)
            return false
        }
    }

    private fun createAhas(count: Int) {
        for (i in 0..count - 1) {
            val aha = Aha(title = "Kata #$i", isUseful = (i % 2 == 0))
            ahas.add(aha)
        }
    }

    companion object {
        private val TAG = "AhaLab"
        private val FILENAME = "ahas.json"
        private var sAhaLab: AhaLab? = null

        operator fun get(c: Context): AhaLab {
            val ahaLab = sAhaLab ?: AhaLab(c.applicationContext)
            sAhaLab = ahaLab
            return ahaLab
        }
    }
}
