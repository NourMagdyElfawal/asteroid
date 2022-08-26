package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.getPictureOfDay
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel (application: Application): AndroidViewModel(application) {

    private val database=AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)


    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailFragment = MutableLiveData<Asteroid>()
    val navigateToDetailFragment: LiveData<Asteroid>
        get() = _navigateToDetailFragment



    init {
        viewWeekAsteroidsClicked()
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroidFromRepository()
            } catch (e: Exception) {
                println("Exception refreshing data: $e.message")
            }
        }

        viewModelScope.launch {
            try {
                refreshPictureOfDay()
            } catch (e: Exception) {
                println("Exception refreshing data: $e.message")
            }
        }


    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetailFragment.value = asteroid
    }

    fun doneNavigating() {
        _navigateToDetailFragment.value = null
    }


    private suspend fun refreshPictureOfDay() {
        _pictureOfDay.value = getPictureOfDay()!!
    }
// get for 7days asteroids
    fun viewWeekAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.
            getAsteroidsByCloseApproachDate(getToday(), getSeventhDay())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                    Log.e("asteroid", asteroids.size.toString())
                }
        }
    }

    // get today asteroids
    fun viewTodayAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.
            getAsteroidsByCloseApproachDate(getToday(), getToday())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }


    //get all asteroid
    fun viewAllAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.
            getAllAsteroids()
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }




}