package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {



    private lateinit var adapter: MainAsteroidAdapter

    private lateinit var binding: FragmentMainBinding

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        //connect the recycler with the adapter and add clickListener
        adapter = MainAsteroidAdapter(MainAsteroidAdapter.AsteroidListener { asteroid ->

            viewModel.onAsteroidClicked(asteroid)
        })
        binding.asteroidRecycler.adapter = adapter
        setupObservers()

        setHasOptionsMenu(true)

        return binding.root
    }

    private fun setupObservers() {
        viewModel.asteroids.observe(viewLifecycleOwner) { asteroids ->
            if (asteroids != null) {
                adapter.submitList(asteroids)
            }

        }

        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner) { asteroid ->
            if (asteroid != null) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
                //to make back
                viewModel.doneNavigating()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.show_week_asteroid_menu -> {
                showOneWeekAsteroid()
                true
            }
            R.id.show_today_asteroid_menu -> {
                showTodayAsteroid()
                true
            }
            R.id.show_saved_asteroid_menu -> {
                showAllAsteroid()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
            private fun showOneWeekAsteroid() {
                viewModel.viewWeekAsteroidsClicked()
            }


            private fun showTodayAsteroid() {
                viewModel.viewTodayAsteroidsClicked()
            }

            private fun showAllAsteroid() {
                viewModel.viewAllAsteroidsClicked()
            }

}
