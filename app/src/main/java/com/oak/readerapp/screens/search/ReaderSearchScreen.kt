package com.oak.readerapp.screens.search

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.oak.readerapp.components.InputField
import com.oak.readerapp.components.ReaderAppBar
import com.oak.readerapp.model.Item
import com.oak.readerapp.navigation.ReaderScreens

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchScreen(navController: NavController, viewModel: BookSearchViewModel = hiltViewModel()) {
    Scaffold(topBar = {
        ReaderAppBar(
            title = "Search Books",
            icon = Icons.Default.ArrowBack,
            navController = navController,
            showProfile = false
        ) { navController.navigate(ReaderScreens.ReaderHomeScreen.name) }
    }) {
        Surface {
            Column {
                SearchForm(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {query ->
                    viewModel.searchBooks(query)
                }
                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController = navController, viewModel)
            }
        }
    }
}

@Composable
fun BookList(navController: NavController, viewModel: BookSearchViewModel = hiltViewModel()) {
    val listOfBooks = viewModel.list

    if (viewModel.isLoading) {
        LinearProgressIndicator()
    }else {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(items = listOfBooks) { book ->
                BookRow(book, navController)
            }
        }
    }
}

@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(
        modifier = Modifier
            .clickable { }
            .fillMaxWidth()
            .height(100.dp)
            .padding(3.dp),
        shape = RectangleShape,
        elevation = 7.dp
    ) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
            val imageUrl: String = book.volumeInfo.imageLinks.smallThumbnail.ifEmpty {
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
                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)

                Text(
                    text = "Author: ${book.volumeInfo.authors.joinToString(", ").ifEmpty { "N/A" }}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Date: ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = book.volumeInfo.categories.joinToString(", "),
                    overflow = TextOverflow.Clip,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String = "Search",
    onSearch: (String) -> Unit = {}
) {
    Column {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
        val keyboardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }

        InputField(
            valueState = searchQueryState,
            labelId = "Search",
            enabled = true,
            onAction = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            })

    }
}