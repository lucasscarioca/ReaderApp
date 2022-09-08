package com.oak.readerapp.screens.stats

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.oak.readerapp.components.ReaderAppBar
import com.oak.readerapp.model.Item
import com.oak.readerapp.model.MBook
import com.oak.readerapp.navigation.ReaderScreens
import com.oak.readerapp.screens.home.HomeScreenViewModel
import com.oak.readerapp.utils.formatDate
import java.util.*

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun StatsScreen(navController: NavController, viewModel: HomeScreenViewModel = hiltViewModel()) {

    var books: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppBar(
            title = "Book Stats",
            icon = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ){navController.navigate(ReaderScreens.ReaderHomeScreen.name)}
    }) {
        Surface() {

            books = if (!viewModel.data.value.data.isNullOrEmpty()) {
                viewModel.data.value.data!!.filter { mBook ->
                    mBook.userId == currentUser?.uid
                }
            } else {
                emptyList()
            }

            Column {
                Row {
                    Box(modifier = Modifier
                        .size(45.dp)
                        .padding(2.dp)){
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "icon")
                    }
                    Text(text = "Hi, ${currentUser?.email.toString().split("@")[0].uppercase(Locale.getDefault())}")
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    elevation = 5.dp
                ) {
                    val readBooksList: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                        books.filter { mBook ->
                            mBook.userId == currentUser?.uid && mBook.finishedReading != null
                        }
                    } else {
                        emptyList()
                    }
                    
                    val readingBooks = books.filter { mBook -> 
                        mBook.startedReading != null && mBook.finishedReading == null
                    }
                    
                    Column(
                        modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Your Stats", style = MaterialTheme.typography.h5)
                        Divider()
                        Text(text = "You're reading: ${readingBooks.size} books")
                        Text(text = "You've read: ${readBooksList.size} books")
                    }
                }

                if (viewModel.data.value.loading == true) {
                    LinearProgressIndicator()
                } else {
                    Divider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        //filter books by finished ones
                        val readBooks: List<MBook> = if (!viewModel.data.value.data.isNullOrEmpty()) {
                            viewModel.data.value.data!!.filter { mBook ->
                                mBook.userId == currentUser?.uid && mBook.finishedReading != null
                            }
                        }else {
                            emptyList()
                        }

                        items(items = readBooks) { book ->
                            StatsBookRow(book = book, navController = navController)
                        }
                    }

                }
            }

        }
    }

}

@Composable
fun StatsBookRow(book: MBook, navController: NavController) {
    Card(
        modifier = Modifier
            .clickable {
                navController.navigate(ReaderScreens.UpdateScreen.name + "/${book.googleBookId}")
            }
            .fillMaxWidth()
            .height(100.dp)
            .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp
    ) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
            val imageUrl: String = book.photoUrl.toString().ifEmpty {
                "http://books.google.com/books/content?id=6DQACwAAQBAJ&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api"
            }
            Image(
                painter = rememberAsyncImagePainter(model = imageUrl),
                contentDescription = "book image",
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .padding(end = 4.dp)
            )
            Column {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                    if (book.rating!! >= 4){
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Thumbs up",
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else if (book.rating!! >= 3) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "Thumbs up",
                            tint = Color.Gray.copy(alpha = 0.5f)
                        )
                    }else if(book.rating!! >= 1){
                        Icon(
                            imageVector = Icons.Default.ThumbDown,
                            contentDescription = "Thumbs up",
                            tint = Color.Red.copy(alpha = 0.5f)
                        )
                    }else {
                        Box{}
                    }
                }

                Text(
                    text = "Author: ${if (book.authors.isNullOrEmpty()) "N/A" else book.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Started: ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Finished: ${formatDate(book.finishedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

            }
        }
    }
}