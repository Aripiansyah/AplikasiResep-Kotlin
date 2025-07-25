package com.example.reciperealm

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.reciperealm.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Siapkan ViewPager dan Adapter-nya
        setupViewPager()

        // Hubungkan ViewPager dengan BottomNavigationView
        setupNavigation()

        // Fungsi search tetap ada
        setupSearch()
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
    }

    private fun setupNavigation() {
        // 1. Atur listener untuk saat item di navigasi bawah diklik
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            // Ganti halaman di ViewPager sesuai item yang diklik
            when (item.itemId) {
                R.id.navigation_home -> binding.viewPager.currentItem = 0
                R.id.navigation_favorite -> binding.viewPager.currentItem = 1
                R.id.navigation_profile -> binding.viewPager.currentItem = 2
            }
            // Atur tampilan toolbar
            setupToolbarForFragment(item.itemId)
            true
        }

        // 2. Atur listener untuk saat halaman di-swipe di ViewPager
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Ganti item yang aktif di navigasi bawah sesuai halaman
                when (position) {
                    0 -> binding.bottomNavigationView.selectedItemId = R.id.navigation_home
                    1 -> binding.bottomNavigationView.selectedItemId = R.id.navigation_favorite
                    2 -> binding.bottomNavigationView.selectedItemId = R.id.navigation_profile
                }
            }
        })
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                // Dapatkan fragment yang sedang aktif dari adapter
                val currentFragment = supportFragmentManager.findFragmentByTag("f" + binding.viewPager.currentItem)

                when (currentFragment) {
                    is HomeFragment -> currentFragment.performSearch(query)
                    is FavoriteFragment -> currentFragment.performSearch(query)
                }
            }
        })
    }

    private fun setupToolbarForFragment(itemId: Int) {
        when (itemId) {
            R.id.navigation_profile -> {
                binding.logoImageView.visibility = View.GONE
                binding.searchEditText.visibility = View.GONE
                binding.logoCenteredImageView.visibility = View.VISIBLE
            }
            else -> {
                binding.logoImageView.visibility = View.VISIBLE
                binding.searchEditText.visibility = View.VISIBLE
                binding.logoCenteredImageView.visibility = View.GONE
            }
        }
    }
}