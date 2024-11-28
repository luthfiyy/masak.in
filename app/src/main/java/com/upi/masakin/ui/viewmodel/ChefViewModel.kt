package com.upi.masakin.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upi.masakin.R
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.repository.ChefRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChefViewModel(private val chefRepository: ChefRepository) : ViewModel() {
    private val _chefs = MutableStateFlow<List<Chef>>(emptyList())
    val chefs: StateFlow<List<Chef>> get() = _chefs
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    init {
        fetchChefs()
        // Only insert sample chefs if the database is empty
        viewModelScope.launch(dispatcher) {
            if (chefRepository.getAllChefs().isEmpty()) {
                insertSampleChefs()
                // Fetch again after inserting
                fetchChefs()
            }
        }
    }

    private fun fetchChefs() {
        viewModelScope.launch(dispatcher) {
            val fetchedChefs = chefRepository.getAllChefs()
            _chefs.value = fetchedChefs
            // Add some logging
            Log.d("ChefViewModel", "Fetched chefs: ${fetchedChefs.size}")
        }
    }

    fun insertSampleChefs() {
        viewModelScope.launch(dispatcher) {
            val existingChefs = chefRepository.getAllChefs()

            if (existingChefs.isEmpty()) {  // Hanya insert jika tidak ada chef
                val desc = "ini contoh untuk deskripsi chef"

                // Tambahkan log sebelum memasukkan data
                Log.d("ChefViewModel", "Inserting sample chefs")

                chefRepository.insertChef(Chef(name = "Chef A", description = desc, image = R.drawable.img_chef1))
                chefRepository.insertChef(Chef(name = "Chef B", description = desc, image = R.drawable.img_chef2))
                chefRepository.insertChef(Chef(name = "Chef C", description = desc, image = R.drawable.img_chef3))
                chefRepository.insertChef(Chef(name = "Chef D", description = desc, image = R.drawable.img_chef4))

                Log.d("ChefViewModel", "Sample chefs inserted")
            } else {
                Log.d("ChefViewModel", "Chefs already exist, skipping insertion.")
            }
        }
    }


}