package com.android.gymaround

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.gymaround.ui.theme.GymAroundTheme

@Composable
fun GymsScreen(onItemClick: (Int) -> Unit) {

    val vm: GymViewModel = viewModel()
    LazyColumn() {
        items(vm.state) { gym ->
            GymItem(
                gym = gym,
                onClick = { id -> vm.toggleFavoriteState(id) },
                onItemClick = { id -> onItemClick(id) }
            )
        }
    }
}

@Composable
fun GymItem(gym: Gym, onClick: (Int) -> Unit, onItemClick: (Int) -> Unit) {

    val icon = if (gym.isFavourite) {
        Icons.Filled.Favorite
    } else {
        Icons.Filled.FavoriteBorder
    }

    Card(elevation = 4.dp, modifier = Modifier
        .padding(8.dp)
        .clickable { onItemClick(gym.id) }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            DefaultIcon(Icons.Filled.Place, Modifier.weight(.15f), "Jetpack Logo")
            GymDetails(gym, Modifier.weight(.70f))
            DefaultIcon(icon, Modifier.weight(.15f), "Favorite Gym") {
                onClick(gym.id)
            }
        }
    }
}


@Composable
fun DefaultIcon(
    icon: ImageVector,
    modifier: Modifier,
    contentDescription: String,
    onClick: () -> Unit = {},
) {

    Image(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier
            .padding(8.dp)
            .clickable {
                onClick()
            },
        colorFilter = ColorFilter.tint(Color.DarkGray)
    )
}

@Composable
fun GymDetails(gym: Gym, modifier: Modifier, horizontal: Alignment.Horizontal = Alignment.Start) {
    Column(modifier = modifier, horizontalAlignment = horizontal) {
        Text(text = gym.name,
            style = MaterialTheme.typography.h6,
            color = Color.Magenta
        )
        CompositionLocalProvider(
            LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = gym.address,
                style = MaterialTheme.typography.body2,

                )
        }
    }
}