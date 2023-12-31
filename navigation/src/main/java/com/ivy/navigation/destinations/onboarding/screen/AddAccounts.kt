package com.ivy.navigation.destinations.onboarding.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import com.ivy.navigation.Screen

object AddAccounts : Screen<Unit> {
    override val route: String = "onboarding/accounts"
    override val arguments: List<NamedNavArgument> = emptyList()

    override fun destination(arg: Unit): String = route
    override fun parse(entry: NavBackStackEntry) {}
}