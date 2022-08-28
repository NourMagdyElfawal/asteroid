@file:Suppress("BlockingMethodInNonBlockingContext")

package com.udacity.asteroidradar.repository

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

    suspend fun refreshAsteroidFromRepository(
        todayDate: String = getTodayDate(),
        afterSevenDayDate: String = getAfterSevenDayDate()
    ) {
        var asteroidList: ArrayList<Asteroid>
//      you have to run the disk I/O in the I/O dispatcher.
//      This dispatcher is designed to offload blocking I/O tasks to a shared pool of threads using
        withContext(Dispatchers.IO) {
            try {
                val asteroidResponseBody: ResponseBody = AsteroidApi.retrofitService.getAsteroids(
                    todayDate, afterSevenDayDate
                )
                asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.string()))
                val asteroids=asteroidList.asDomainModel()
                database.asteroidDao.insertAll(* asteroids  )
            }catch (e:Exception){
            println("Exception refreshing Asteroids: $e.message")
        }

        }
    }
}