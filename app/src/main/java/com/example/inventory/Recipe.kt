package com.example.inventory

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

class Recipe(

    val id: Int,
    val title: String,
    val image: String,
    val missedIngredients: List<Ingredient>,
    val usedIngredients: List<Ingredient>,
    val unusedIngredients: List<Ingredient>,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(Ingredient.CREATOR)!!,
        parcel.createTypedArrayList(Ingredient.CREATOR)!!,
        parcel.createTypedArrayList(Ingredient.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeList(missedIngredients)
        parcel.writeList(usedIngredients)
        parcel.writeList(unusedIngredients)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}
data class Ingredient(
    val id: Int,
    val name: String,
    val original: String,
    val image: String,
    val amount: Float,
    val unit: String,
    val aisle: String,

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readString()!!,
        parcel.readString()!!,
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(original)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }

}

data class RecipeInformation(
    val readyInMinutes: String,
    val dairyFree: Boolean,
    val glutenFree: Boolean,
    val vegetarian: Boolean,
    val vegan: Boolean,
    )

data class RecipeInstructions(
    val number: Int,
    val step: String,
)