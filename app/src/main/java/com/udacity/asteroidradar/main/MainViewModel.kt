package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel (application: Application): AndroidViewModel(application) {

    private val database = AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)


    private var _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids


//    // The internal MutableLiveData that stores the status of the most recent request
//    private val _status = MutableLiveData<String>()
//
//    // The external immutable LiveData for the request status
//    val status: LiveData<String> = _status


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _navigateToDetailFragment = MutableLiveData<Asteroid>()
    val navigateToDetailFragment: LiveData<Asteroid>
        get() = _navigateToDetailFragment

    private val _itemSelected = MutableLiveData<String>()


    init {
        viewWeekAsteroidsClicked()
        getPictureOfDay()
        viewModelScope.launch {
            try {
                asteroidRepository.refreshAsteroidFromRepository()
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



    private fun getPictureOfDay() {
        viewModelScope.launch {
        try {
            val pictureOfDay = AsteroidApi.retrofitService.getPhoto()
            println( "Success: get  photo retrieved")
            if (pictureOfDay.mediaType == "image") {
                 _pictureOfDay.value=pictureOfDay
            }else{
                _pictureOfDay.value=null
            }
        }catch (e:Exception){
            println("Exception refreshing image: $e.message")
        }
    }
    }

    // get for 7days asteroids
    fun viewWeekAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroidsByCloseApproachDate(getToday(), getSeventhDay())
                .collect { asteroids ->
                    _asteroids.value = asteroids
                }
        }
    }



    //get all saved asteroid
    fun viewAllAsteroidsClicked() {
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