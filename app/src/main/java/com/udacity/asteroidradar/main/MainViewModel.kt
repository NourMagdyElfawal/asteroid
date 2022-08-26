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

    private val database = AsteroidDatabase.getDatabase(application)
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
                }
        }
    }



    //get all saved asteroid
    fun viewAllAsteroidsClicked() {
//        val asteroids: LiveData<List<Asteroid>> =
//            Transformations.switchMap(
//                database.asteroidDao.getAllAsteroids()
//            )
//            {
//                asteroids
//            }
        viewModelScope.launch {
            database.asteroidDao.getAllAsteroids()
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }

    fun onMenuClicked(title: String) {
        _itemSelected.value = title
    }

    val selectedItem: LiveData<List<Asteroid>> =
        Transformations.switchMap(_itemSelected) {
        getAsteroidList(it)
    }

    private fun getAsteroidList(it: String?): LiveData<List<Asteroid>> {
        Log.e("title2", it!!)
        if (it.equals("View week asteroids", true)) {
            viewWeekAsteroidsClicked()
        } else if (it.equals("View today asteroids", true)){
            viewTodayAsteroidsClicked()
        } else if (it.equals("View saved asteroids", true)){
            viewAllAsteroidsClicked()
        }

        return asteroids
    }
    // get today asteroids
    fun viewTodayAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getToday())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }

}