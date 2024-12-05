package com.upi.masakin.ui.viewmodel.chef

import android.app.Application
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
    }


    private fun fetchChefs() {
        viewModelScope.launch(dispatcher) {
            val fetchedChefs = chefRepository.getAllChefs()
            if (fetchedChefs.isEmpty()) {
                insertSampleChefs()
            } else {
                _chefs.value = fetchedChefs
            }
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

            val updatedChefs = chefRepository.getAllChefs()
            _chefs.value = updatedChefs
        }
    }


}