package de.bentolor.katamanager

import android.content.Context
import android.util.Log

import java.util.ArrayList
import java.util.UUID

class AhaLab private constructor(private val mAppContext: Context) {
    var ahas: ArrayList<Aha>? = null
        private set
    private val mSerializer: AhaJSONSerializer

    init {
        mSerializer = AhaJSONSerializer(mAppContext, FILENAME)

        try {
            ahas = mSerializer.loadCrimes()
        } catch (e: Exception) {
            ahas = ArrayList<Aha>()
            Log.e(TAG, "Error loading crimes: ", e)
        }

        createAhas(7)
    }

    fun getAha(id: UUID): Aha? {
        for (c in ahas!!) {
            if (c.id == id)
                return c
        }
        return null
    }

    fun addCrime(c: Aha) {
        ahas!!.add(c)
    }

    fun deleteCrime(c: Aha) {
        ahas!!.remove(c)
    }

    fun saveCrimes(): Boolean {
        try {
            mSerializer.saveAhas(ahas!!)
            Log.d(TAG, "crimes saved to file")
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving crimes: ", e)
            return false
        }

    }

    private fun createAhas(count: Int) {
        for (i in 0..count - 1) {
            val aha = Aha()
            aha.title = "Kata #" + i
            aha.isUseful = i % 2 == 0 // Alternate
            ahas!!.add(aha)
        }
    }

    companion object {
        private val TAG = "AhaLab"
        private val FILENAME = "ahas.json"
        private var sAhaLab: AhaLab? = null

        operator fun get(c: Context): AhaLab {
            if (sAhaLab == null) {
                sAhaLab = AhaLab(c.applicationContext)
            }
            return sAhaLab!!
        }
    }
}
