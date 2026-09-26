package com.example.mystudyproject1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawerContentTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center

    ){
        Text(
            fontSize = 32.sp,
            text = stringResource(R.string.menu)
        )
    }
}

@Composable
fun DrawerContentBody(
    itemsList: List<DrawerItem>,
    onItemClick: (DrawerItem) -> Unit,

) {

        LazyColumn {
            items(itemsList) { item ->
                Row(modifier = Modifier
                    //.fillMaxWidth()
                    .clickable{
                        onItemClick(item)
                    },
                    horizontalArrangement = Arrangement.spacedBy(16.dp)

                ){
                    Icon(
                        item.icon,
                        contentDescription = item.contentDescription
                    )
                    Text(
                        text = item.title,
                        style = TextStyle(fontSize = 22.sp)
                    )

                }
                Spacer(modifier = Modifier.padding(16.dp))
                //HorizontalDivider()


            }
        }


}