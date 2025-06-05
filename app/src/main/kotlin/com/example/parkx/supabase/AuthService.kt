package com.example.parkx.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object AuthService {
    suspend fun signIn(email: String, password: String): String {
        SupabaseManager.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return "Sign In successful"
    }

    suspend fun signUp(email: String, password: String, name: String, surname: String): String {
        SupabaseManager.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("name", name)
                put("surname", surname)
            }
        }
        return "Sign Up successful"
    }

    suspend fun signOut(): String {
        SupabaseManager.client.auth.signOut()
        return "Sign Out successful"
    }
}