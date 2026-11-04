package com.example.pratiqueretrofitsnackbar

import android.content.res.Configuration
import android.health.connect.datatypes.units.Length
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pratiqueretrofitsnackbar.ui.theme.PratiqueRetroFitSnackBarTheme
import data.Livre
import data.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PratiqueRetroFitSnackBarTheme(dynamicColor = false) {
                App()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()

    val context = LocalContext.current

    val scrollState = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .nestedScroll(scrollState.nestedScrollConnection),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Recherche livre") },
                actions = {
                    IconButton(onClick = {
                        scope.launch(Dispatchers.Main) {
                            val result = snackbarHostState.showSnackbar(
                                message = "Retourner au menu?",
                                actionLabel = "Oui",
                                duration = SnackbarDuration.Short
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    Toast.makeText(context, "Retour au menu", Toast.LENGTH_SHORT)
                                        .show()
                                    navController.navigate("homeScreen")
                                }

                                SnackbarResult.Dismissed -> {
                                    Toast.makeText(context, "Vous avez annulez", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }) {
                        Icon(painterResource(R.drawable.home), "Home", tint = Color.Black)
                    }
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(painterResource(R.drawable.back), "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                scrollBehavior = scrollState
            )
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "homeScreen",
            modifier = Modifier.padding(innerPadding)
        )
        {
            composable("homeScreen") {
                HomePage(navController)
            }
            composable("bookScreen") {
                BookScreen(navController)
            }
            composable("todoScreen") {
                TodoScreen(navController)
            }

        }
    }
}

@Composable
fun HomePage(navController: NavController) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Bienvenue à la bibliothèque",
            fontSize = 50.sp,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp
        )
        Button(
            onClick = {
                navController.navigate("bookScreen")
            },
            modifier = Modifier.size(250.dp, 50.dp)
        ) {
            Text("Rechercher des livres")
        }
        Button(
            onClick = {
                navController.navigate("todoScreen")
            },
            modifier = Modifier.size(250.dp, 50.dp)
        ) {
            Text("Voir Todos")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(navController: NavController) {
    val viewModel: BookViewModel = viewModel()
    val books by viewModel.books.collectAsState()

    var query by remember { mutableStateOf("") }

    val orientation = LocalConfiguration.current

    Column(Modifier.fillMaxSize()) {
        if (orientation.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                BookSearchBar(
                    modifier = Modifier.weight(1f),
                    query = query,
                    onQueryChanged = { query = it },
                    viewModel = viewModel
                )
                BookList(
                    modifier = Modifier
                        .weight(1f)
                        .padding(15.dp),
                    books = books
                )
            }
        } else {
            BookSearchBar(
                query = query,
                onQueryChanged = { query = it },
                viewModel = viewModel
            )
            BookList(books = books)
        }
    }
}

@Composable
fun BookSearchBar(
    modifier: Modifier = Modifier.fillMaxWidth(),
    query: String,
    onQueryChanged: (String) -> Unit,
    viewModel: BookViewModel
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            label = { Text("Search") }
        )

        IconButton(onClick = { viewModel.loadBooks(query) }) {
            Icon(painterResource(R.drawable.back), "Back", tint = Color.Black)
        }
    }
}

@Composable
fun BookList(
    modifier: Modifier = Modifier.padding(15.dp),
    books: List<Livre>
) {
    LazyColumn(modifier) {
        items(books) { book ->
            Column() {
                Text(book.titre, fontWeight = FontWeight.Bold)
                Text("${book.auteurs}")
                Text("${book.anneePublication}")
            }
            HorizontalDivider(Modifier.padding(top = 4.dp, bottom = 4.dp))
        }
    }
}


@Composable
fun TodoScreen(navController: NavController) {
    val viewModel: TodosViewModel = viewModel()
    val todos by viewModel.todos.collectAsState()
    var query by remember { mutableStateOf("") }
    val todosToShow = todos.filterTodos(query)

    Column(Modifier.padding(16.dp)) {
        SearchBar(
            query = query,
            onQueryChanged = { query = it },
            viewModel = viewModel
        )
        LazyColumn {
            items(todosToShow) { todo ->
                Text(
                    text = "${todo.titre} (${if (todo.complete) "done" else "pending"})"
                )
                HorizontalDivider(Modifier.padding(top = 4.dp, bottom = 4.dp))
            }
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier.fillMaxWidth(),
    query: String,
    onQueryChanged: (String) -> Unit,
    viewModel: TodosViewModel
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            label = { Text("Search") }
        )

        IconButton(onClick = { viewModel.loadTodos() }) {
            Icon(painterResource(R.drawable.back), "Back")
        }
    }
}

fun List<Todo>.filterTodos(query: String): List<Todo> {
    var filtered = this
    if (!query.isEmpty()) {
        filtered = filter { t ->
            val titreMatch = t.titre.contains(query, true)
            val completeMatch = t.complete.toString().contains(query, true)
            val idMatch = t.idTodo.toString().contains(query, true)
            val userIdMatch = t.idUtilisateur.toString().contains(query, true)

            titreMatch || completeMatch || idMatch || userIdMatch
        }
    }
    return filtered
}