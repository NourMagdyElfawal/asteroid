package com.udacity.asteroidradar.main

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel (application: Application): AndroidViewModel(application) {

    private val database = AsteroidDatabase.getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val todayDate= getTodayDate()
    private val afterSevenDayDate= getAfterSevenDayDate()


    private var _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList


    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay
//this is for menu
    private val _menuItemSelected = MutableLiveData<String>()

//select asteroid from the list
    private val _mutableSelectedItem = MutableLiveData<Asteroid>()
    val selectedItem: LiveData<Asteroid>
    get() = _mutableSelectedItem

    fun selectItem(asteroid: Asteroid) {
        _mutableSelectedItem.value = asteroid
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun removeNav() {
        _mutableSelectedItem.postValue(null)
    }

/////////////////////////////////////


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





    @SuppressLint("NullSafeMutableLiveData")
    private fun getPictureOfDay() {
        viewModelScope.launch {
        try {
            val pictureOfDay = AsteroidApi.retrofitService.getPhoto()
            if (pictureOfDay.mediaType == "image") {
                 _pictureOfDay.value=pictureOfDay
            }else{
                _pictureOfDay.postValue(null)
            }
        }catch (e:Exception){
            println("Exception refreshing image: $e.message")
        }
    }
    }

    // get for 7days asteroids
    fun viewWeekAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroidsByDate(todayDate, afterSevenDayDate)
                .collect {
                    _asteroidList.value = it
                }
        }
    }



    //get all saved asteroid
    fun viewAllAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAllAsteroids().collect {
                    _asteroidList.value = it
                }
        }
    }

    fun onMenuClicked(title: String) {
        _menuItemSelected.value = title
    }

    val menuSelectedItem: LiveData<List<Asteroid>> =
        Transformations.switchMap(_menuItemSelected) {
        getAsteroidList(it)
    }

    private fun getAsteroidList(it: String?): LiveData<List<Asteroid>> {
        if (it.equals("View week asteroids", true)) {
            viewWeekAsteroidsClicked()
        } else if (it.equals("View today asteroids", true)){
            viewTodayAsteroidsClicked()
        } else if (it.equals("View saved asteroids", true)){
            viewAllAsteroidsClicked()
        }

        return asteroidList
    }
    // get today asteroids
    fun viewTodayAsteroidsClicked() {
        viewModelScope.launch {
            database.asteroidDao.getAsteroidsByDate(todayDate, todayDate)
                .collect {
                    _asteroidList.value = it
                }
        }
    }

}