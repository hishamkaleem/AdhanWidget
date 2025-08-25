package com.widgetfiles.widget.location

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await

object LocationRepo {
    private fun hasPerm(ctx: Context): Boolean {
        val c = androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val f = androidx.core.content.ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION)
        return c == android.content.pm.PackageManager.PERMISSION_GRANTED || f == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    @android.annotation.SuppressLint("MissingPermission")
    suspend fun getLatLng(ctx: Context): Pair<Double, Double>? {
        if (!hasPerm(ctx)) return null

        // Try Fused (works offline)
        try {
            val fused = com.google.android.gms.location.LocationServices.getFusedLocationProviderClient(ctx)
            val token = com.google.android.gms.tasks.CancellationTokenSource()
            val current = kotlinx.coroutines.withTimeoutOrNull(2000) {
                fused.getCurrentLocation(
                    com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    token.token
                ).await()
            }
            val loc = current ?: fused.lastLocation.await()
            if (loc != null) return loc.latitude to loc.longitude
        } catch (_: Exception) { /* fall back */ }

        // Fallback: framework LocationManager
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        val providers = listOf(
            android.location.LocationManager.GPS_PROVIDER,
            android.location.LocationManager.NETWORK_PROVIDER,
            android.location.LocationManager.PASSIVE_PROVIDER
        )
        val best = providers.mapNotNull { p -> runCatching { lm.getLastKnownLocation(p) }.getOrNull() }
            .maxByOrNull { it.time }
        return best?.let { it.latitude to it.longitude }
    }
}
