package com.upi.masakin.ui.viewmodel.chef

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upi.masakin.R
import com.upi.masakin.data.entities.Chef
import com.upi.masakin.data.repository.ChefRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChefViewModel(
    private val chefRepository: ChefRepository,
    private val dispatcher: CoroutineDispatcher,
    private val context: Application,
) : ViewModel() {
    private val _chefs = MutableStateFlow<List<Chef>>(emptyList())
    val chefs: StateFlow<List<Chef>> get() = _chefs


    init {
        fetchChefs()
        viewModelScope.launch(dispatcher) {
            val existingChefs = chefRepository.getAllChefs()
            if (existingChefs.isEmpty()) {
                insertSampleChefs()
            }
        }
    }

    private fun fetchChefs() {
        viewModelScope.launch(dispatcher) {
            val fetchedChefs = chefRepository.getAllChefs()
            _chefs.value = fetchedChefs
            Log.d("ChefViewModel", "Fetched chefs: ${fetchedChefs.size}")
        }
    }

    private fun insertSampleChefs() {
        viewModelScope.launch(dispatcher) {
            val sampleChefs = listOf(
                Chef(
                    name = "Chef A",
                    description = context.getString(R.string.contoh_desk_chef),
                    image = R.drawable.img_chef1
                ),
                Chef(
                    name = "Chef B",
                    description = context.getString(R.string.contoh_desk_chef),
                    image = R.drawable.img_chef2
                ),
                Chef(
                    name = "Chef C",
                    description = context.getString(R.string.contoh_desk_chef),
                    image = R.drawable.img_chef3
                ),
                Chef(
                    name = "Chef D",
                    description = context.getString(R.string.contoh_desk_chef),
                    image = R.drawable.img_chef4
                )
            )

            sampleChefs.forEach { chef ->
                chefRepository.insertChef(listOf(chef))
            }
        }
    }
}