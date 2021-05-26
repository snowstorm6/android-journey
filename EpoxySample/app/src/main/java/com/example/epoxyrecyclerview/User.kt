package com.example.epoxyrecyclerview

import kotlin.random.Random

/**
 * Created by NGUYEN VAN SON on 26/05/2021.
 * sonnv@onemount.com
 */

data class User(
    val name: String,
    val email: String,
    var age: Int,
) {
    companion object {
        fun getSampleUsers(): MutableList<User> {
            val usersList: MutableList<User> = mutableListOf()
            for (i in 1..50) {
                usersList.add(User(name = "User $i", email = "user$i@email.com", age = Random.nextInt(18, 50)))
            }

            return usersList
        }
    }
}