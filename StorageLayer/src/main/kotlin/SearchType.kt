enum class SearchType {
    TITLE, INGREDIENT, ALL
}

data class SearchParam(var searchType: SearchType = SearchType.ALL, var searchValues: List<String> = listOf())