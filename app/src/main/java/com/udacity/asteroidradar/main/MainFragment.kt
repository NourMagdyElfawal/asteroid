package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
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
        adapter = MainAsteroidAdapter(MainAsteroidAdapter.AsteroidListener {

            viewModel.selectItem(it)
        })
        binding.asteroidRecycler.adapter = adapter
//        submitList of asteroid List at adapter
        viewModel.asteroidList.observe(viewLifecycleOwner) {
            if (it != null) {
                adapter.submitList(it)
            }
        }
        onItemClicked()
        setHasOptionsMenu(true)

        return binding.root
    }

    private fun onItemClicked() {
        viewModel.selectedItem.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.removeNav()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        viewModel.onMenuClicked(item.title.toString())
        viewModel.menuSelectedItem.observe(viewLifecycleOwner) { asteroids ->
            if (asteroids != null) {
                adapter.submitList(asteroids)
            }

        }
        Log.e("title1", item.title.toString())
        return true

    }
}
