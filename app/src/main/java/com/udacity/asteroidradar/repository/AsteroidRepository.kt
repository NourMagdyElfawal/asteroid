@file:Suppress("BlockingMethodInNonBlockingContext")

package com.udacity.asteroidradar.repository

import android.util.Log
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {
//    val nasaApiKey = BuildConfig.NASA_API_KEY

    suspend fun refreshAsteroidFromRepository(
        startDate: String = getToday(),
        endDate: String = getSeventhDay()
    ) {
        var asteroidList: ArrayList<Asteroid>
//      you have to run the disk I/O in the I/O dispatcher.
//      This dispatcher is designed to offload blocking I/O tasks to a shared pool of threads using
        withContext(Dispatchers.IO) {
                val asteroidResponseBody: ResponseBody = Network.service.getAsteroidsAsync(
                    startDate, endDate,
                    Constants.API_KEY
                ).await()
                asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.string()))
                database.asteroidDao.insertAll(*asteroidList.asDomainModel())

        }
    }
}