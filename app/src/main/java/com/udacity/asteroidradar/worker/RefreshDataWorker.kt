package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.AsteroidDatabase.Companion.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {
    //doWork will make refresh for database from api in the background
    //to make it updated and work when the user open the app
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroidFromRepository()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }

    }
    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

}