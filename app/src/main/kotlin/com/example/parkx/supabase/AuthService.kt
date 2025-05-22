package com.example.parkx.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

object AuthService {
    suspend fun signIn(email: String, password: String): String {
        SupabaseManager.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return "Sign In successful"
    }

    suspend fun signUp(email: String, password: String): String {
        SupabaseManager.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        return "Sign Up successful"
    }

    suspend fun signOut(): String {
        SupabaseManager.client.auth.signOut()
        return "Sign Out successful"
    }
}