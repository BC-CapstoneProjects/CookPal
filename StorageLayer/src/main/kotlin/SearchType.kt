enum class SearchType {
    KEYWORD, INGREDIENT, EVERYWHERE
}

data class SearchParam(var searchType: SearchType = SearchType.EVERYWHERE, var searchValues: List<String> = listOf())