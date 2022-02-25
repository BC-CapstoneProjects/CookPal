package apilib

interface apiInterface {
    fun retrieveRecipes(keyword: String, numPages: Int, numDrivers: Int, writeLocation: String)
    fun retrieveRecipes(keyword: String, numPages: Int, numDrivers: Int)
}