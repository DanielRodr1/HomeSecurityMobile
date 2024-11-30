package com.upao.HomeSecurity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        LucesFragment(),
        GarageSeguridadFragment(),
        CamaraVigilanciaFragment(),
        FragmentHistorial() // Añade el nuevo fragmento aquí
    )

    private val fragmentTitles = listOf(
        "Luces",
        "Garage y Seguridad",
        "Cámara",
        "Historial" // Título para la nueva pestaña
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

    fun getPageTitle(position: Int): String = fragmentTitles[position]
}
