package com.udacity.asteroidradar.repository

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.ArrayList

//its job to communicate between database and network
class AsteroidRepository(private val database: AsteroidDatabase) {
    suspend fun refreshAsteroidFromRepository(
        startDate: String = getToday(),
        endDate: String = getSeventhDay()
    ) {
        var asteroidList: ArrayList<Asteroid>
//      you have to run the disk I/O in the I/O dispatcher.
//      This dispatcher is designed to offload blocking I/O tasks to a shared pool of threads using
        withContext(Dispatchers.IO) {
// 1           get response body from network using coroutine
            val asteroidResponseBody: ResponseBody = Network.service.getAsteroidsAsync(
                startDate, endDate,
                Constants.API_KEY
            ).await()
// 2           parse responseBody
            asteroidList = parseAsteroidsJsonResult(JSONObject(asteroidResponseBody.toString()))
// 3           save responseBody to database
            database.asteroidDao.insertAll(*asteroidList.asDomainModel())

        }

    }

}
