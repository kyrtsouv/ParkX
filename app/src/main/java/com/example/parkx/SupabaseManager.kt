package com.example.parkx

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

interface JavaResultCallback<T> {
    fun onSuccess(value: T)
    fun onError(exception: Throwable)
}

@Serializable
data class Message(
    val id: Int, val content: String
)

object CoroutineExecutor {
    @JvmStatic
    fun <T> runSuspend(
        block: suspend () -> T, callback: JavaResultCallback<T>
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val value = block()
                withContext(Dispatchers.Main) {
                    callback.onSuccess(value)
                }
            } catch (e: Throwable) {
                withContext(Dispatchers.Main) {
                    callback.onError(e)
                }
            }
        }
    }
}

object SupabaseManager {
    lateinit var client: SupabaseClient
        private set

    fun init() {
        client = createSupabaseClient(
            supabaseUrl = "https://tzqwqwlnomlcmgeittpa.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InR6cXdxd2xub21sY21nZWl0dHBhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDQwNTQ5OTEsImV4cCI6MjA1OTYzMDk5MX0.X_FYxqGdpEzAtnC9aWtPwvWmvZPHKVIm3RAZBun1k0o"
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    @JvmStatic
    fun signIn(email: String, password: String, callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend({ suspendedSignIn(email, password) }, callback)
    }

    private suspend fun suspendedSignIn(email: String, password: String): String {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return "Sign In successful"
    }

    @JvmStatic
    fun signUp(email: String, password: String, callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend({ suspendedSignIn(email, password) }, callback)
    }

    private suspend fun suspendedSignUp(email: String, password: String): String {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return "Sign Up successful"
    }

    @JvmStatic
    fun signOut(callback: JavaResultCallback<String>) {
        CoroutineExecutor.runSuspend(
            { suspendedSignOut() }, callback
        )
    }

    private suspend fun suspendedSignOut(): String {
        client.auth.signOut()
        return "Sign Out successful"
    }

    @JvmStatic
    fun getMessages(callback: JavaResultCallback<List<String>>) {
        CoroutineExecutor.runSuspend(
            { suspendedGetMessages() }, callback
        )
    }

    private suspend fun suspendedGetMessages(): List<String> {
        val result = client.from("messages").select().decodeList<Message>()
        val messages = mutableListOf<String>()
        for (msg in result) {
            messages.add(msg.content)
        }
        return messages
    }
}