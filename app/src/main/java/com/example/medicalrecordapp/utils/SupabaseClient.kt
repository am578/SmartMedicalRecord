package com.example.medicalrecordapp.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://gdafxpoxsnwjbcudbvfa.supabase.co",
        supabaseKey = "sb_publishable_a235qDaQCqLD_QNnZssIOA_dPaPbWDT"
    ) {
        install(Storage)
    }

    val storage get() = client.storage
}