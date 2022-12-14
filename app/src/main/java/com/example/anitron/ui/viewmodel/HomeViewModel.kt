package com.example.anitron.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State as NormalState
import com.example.anitron.data.datasource.Movie
import com.example.anitron.data.datasource.SearchWidgetState
import com.example.anitron.data.datasource.State
import com.example.anitron.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _movieList = MutableStateFlow(Result(state = State.Loading, movieSelection = listOf(), seriesSelection = listOf(), upcomingMoviesSelection = listOf(), upcomingSeriesSelection = listOf(), onTheatres = listOf()))
    var movieList = _movieList
    var cachedList = movieList.value

    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    val searchWidgetState: NormalState<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> =
        mutableStateOf(value = "")
    val searchTextState: NormalState<String> = _searchTextState

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun searchMovies(value: List<Movie>){
        //_movieList.value = Result(state = State.Loading, movieSelection = value)
    }
    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }
    fun getSearchTextState():String{
        return _searchTextState.value
    }

    fun closeSearchBar(){
        movieList.update { cachedList }
    }

    fun getHomeScreenMoviesAndSeries() {
        viewModelScope.launch{
            try{
                var popularMoviesResponses = repository.getPopMovies("1")
                var popularSeriesResponses = repository.getPopSeries("1")
                var upcomingMoviesResponses = repository.getUpcMovies("1")
                var upcomingSeriesResponses = repository.getOnAir("1")
                var onTheatres = repository.getOnTheatres("1")


                if(popularMoviesResponses.isSuccessful && popularMoviesResponses.body() != null && popularSeriesResponses.isSuccessful && popularSeriesResponses.body() != null){
                        _movieList.emit(
                            Result(
                                state = State.Success,
                                movieSelection = popularMoviesResponses.body()!!.mList,
                                seriesSelection = popularSeriesResponses.body()!!.mList,
                                upcomingMoviesSelection = upcomingMoviesResponses.body()!!.mList,
                                upcomingSeriesSelection = upcomingSeriesResponses.body()!!.mList,
                                onTheatres = onTheatres.body()!!.mList
                            )
                        )
                }
                else{
                    _movieList.emit(Result(state = State.Failed, movieSelection = listOf(), seriesSelection = listOf(), upcomingMoviesSelection = listOf(), upcomingSeriesSelection = listOf(), onTheatres = listOf() ))
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", e.message ?: "", e)
                _movieList.emit(Result(state = State.Failed, movieSelection = listOf(), seriesSelection = listOf(), upcomingMoviesSelection = listOf(), upcomingSeriesSelection = listOf(), onTheatres = listOf()))
            }
        }
    }

    /*fun getSearchMovies(searchText: String) {
      viewModelScope.launch{
          try {
          val response = repository.getAllMovies(searchText)
          if(response.isSuccessful && response.body() != null){
              var response = response.body()
              if(response != null){
                  _movieList.emit(
                      Result(
                          state = State.Success,
                          movieSerieSelection = response.mList
                      )
                  )
              }

          }
          else{
              _movieList.emit(Result(state = State.Failed, movieSerieSelection = listOf()))
          }
        } catch (e: Exception) {
            Log.e("ProductViewModel", e.message ?: "", e)
            _movieList.emit(Result(state = State.Failed, movieSerieSelection = listOf()))
        }
      }
    }*/
}

data class Result(val state: State, val movieSelection: List<Movie>, val seriesSelection: List<Movie>, val upcomingMoviesSelection: List<Movie>, val upcomingSeriesSelection: List<Movie>, val onTheatres: List<Movie>)
