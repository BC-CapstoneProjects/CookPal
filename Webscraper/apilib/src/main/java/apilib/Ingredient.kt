package apilib

class Ingredient {
    constructor(text: String, quantity: Double, measure: String, food: String, weight: Double, foodCategory: String){
        this.text = text
        this.quantity = quantity
        this.measure = measure
        this.food = food
        this.weight = weight
        this.foodCategory = foodCategory
    }
    var text: String = ""
    var quantity: Double = 0.0
    var measure: String = ""
    var food: String = ""
    var weight: Double = 0.0
    var foodCategory: String = ""

}