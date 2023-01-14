package com.miu.mdp.data.repository

import com.miu.mdp.data.SharedPreferenceHelper
import com.miu.mdp.data.local.AppDatabase
import com.miu.mdp.data.local.mock.getUserDetailMock
import com.miu.mdp.data.mapper.toUserModel
import com.miu.mdp.data.mapper.toUserEntity
import com.miu.mdp.domain.model.User
import com.miu.mdp.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val appDatabase: AppDatabase
) :
    UserRepository {

    private val dao = appDatabase.userDao()

    override suspend fun login(username: String, password: String): User? {
        val user = dao.login(username, password) ?: return null
        sharedPreferenceHelper.user = user.toUserModel()
        return user.toUserModel()
    }

    override suspend fun register(user: User): Boolean {
        val existingUser = dao.getUser(user.username)
        if (existingUser != null && existingUser.username == user.username) {
            return false
        }
        dao.insert(user.toUserEntity())
        // add mock data
        appDatabase.userDetailDao().insert(getUserDetailMock(user))
        return true
    }

    override suspend fun getUser(): User? {
        return sharedPreferenceHelper.user
    }

    override suspend fun logout() {
        sharedPreferenceHelper.user = null
    }
}

