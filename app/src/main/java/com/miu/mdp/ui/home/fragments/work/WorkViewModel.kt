package com.miu.mdp.ui.home.fragments.work

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miu.mdp.domain.model.ExperienceDTO
import com.miu.mdp.domain.repository.ExperienceRepository
import com.miu.mdp.utils.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkViewModel @Inject constructor(
    private val experienceRepository: ExperienceRepository
) : ViewModel() {

    private val _workExperienceLiveDataDTO =
        MutableLiveData<List<ExperienceDTO>>(listOf())
    val workExperienceLiveDataDTO: LiveData<List<ExperienceDTO>> = _workExperienceLiveDataDTO

    private val _addWorkStateLiveData = SingleLiveData<AddWorkState>()
    val addWorkState: SingleLiveData<AddWorkState> = _addWorkStateLiveData

    fun getWorkExperience(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val workExperienceList = experienceRepository.getExperience(email)
            _workExperienceLiveDataDTO.postValue(workExperienceList)
        }
    }

    fun addWorkExperience(experienceDTO: ExperienceDTO) = viewModelScope.launch(Dispatchers.IO) {
        _addWorkStateLiveData.postValue(AddWorkState.Loading)
        try {
            // simulate 1 second delay
            delay(1000)
            experienceRepository.addWorkExperience(experienceDTO)
            _addWorkStateLiveData.postValue(AddWorkState.Success)
        } catch (e: Exception) {
            _addWorkStateLiveData.postValue(AddWorkState.Error(e.message ?: "Error"))
        }
    }

    fun removeWorkExperience(experienceDTO: ExperienceDTO) = viewModelScope.launch(Dispatchers.IO) {
        experienceRepository.deleteExperience(experienceDTO)
        getWorkExperience(email = experienceDTO.email)
    }

}