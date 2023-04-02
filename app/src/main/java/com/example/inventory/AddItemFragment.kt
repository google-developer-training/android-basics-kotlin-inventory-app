/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.inventory

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventory.data.Item
import com.example.inventory.databinding.FragmentAddItemBinding
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*

/**
 * Fragment to add or update an item in the Inventory database.
 */
class AddItemFragment : Fragment() {

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    // to share the ViewModel across fragments.
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao(),
            (activity?.application as InventoryApplication).database
                .labelDao()
        )
    }
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    lateinit var item: Item

    // Binding object instance corresponding to the fragment_add_item.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    // For file upload and camera
    private val pickImage = 100
    private val REQUEST_IMAGE_CAPTURE = 101
    private var imagePath: Uri? = null
    private var imageBitmap: Bitmap? = null
    private var imageByte: ByteArray? = null
    private var bos: ByteArrayOutputStream? = ByteArrayOutputStream();
    private var foodNameFound: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        val nameValid = (viewModel.isEntryValid(
            binding.name.text.toString()
        ))
        val expiryDateValid = (viewModel.isEntryValid(
            binding.expiryDate.text.toString()
        ))
        val quantityValid = (viewModel.isEntryValid(
            binding.quantity.text.toString()
        ))
        var formValid = true
        if (!nameValid || !foodNameFound || !expiryDateValid || !quantityValid) {
            formValid = false
            if (!foodNameFound) {
                binding.name.error = "Please enter a food name in the list"
            }
            if (!nameValid) {
                binding.name.error = "ingredient name cannot be empty"
            }
            if (!expiryDateValid) {
                binding.expiryDate.error = "expiry date cannot be empty"
            }
            if (!quantityValid) {
                binding.quantity.error = "quantity cannot be empty"
            }

        }
        return formValid
    }

    /**
     * Binds views with the passed in [item] information.
     */
    private fun bind(item: Item) {

        // Use the uploaded user image if this is the add screen, otherwise take from database
        var loadImageByte = if (navigationArgs.itemId > 0) {
            // Check if the user added an image with this item
            if (item.imageByte == null) {
                null
            } else {
                BitmapFactory.decodeByteArray(item.imageByte, 0, item.imageByte!!.size)
            }
        } else {
            BitmapFactory.decodeByteArray(imageByte, 0, imageByte!!.size)
        }

        binding.apply {
            name.setText(item.name, TextView.BufferType.SPANNABLE)
            expiryDate.setText(item.expiryDate, TextView.BufferType.SPANNABLE)
            label.setText(item.label.toString(), TextView.BufferType.SPANNABLE)
            quantity.setText(item.quantity.toString(), TextView.BufferType.SPANNABLE)
            unit.setText(item.unit.toString(), TextView.BufferType.SPANNABLE)
            binding.imageView.setImageBitmap(loadImageByte)
            saveAction.setOnClickListener {
                binding.name.clearFocus()
                updateItem()
            }
        }

        if (item.imageByte == null) {
            binding.imageView.visibility = View.GONE
        } else {
            binding.imageView.visibility = View.VISIBLE
            imageByte = item.imageByte
        }

    }

    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewLabel(binding.label.text.toString())
            viewModel.addNewItem(
                binding.name.text.toString(),
                binding.expiryDate.text.toString(),
                binding.label.text.toString(),
                binding.quantity.text.toString(),
                binding.unit.text.toString(),
                imageByte
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.addNewLabel(this.binding.label.text.toString())
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.name.text.toString(),
                this.binding.expiryDate.text.toString(),
                this.binding.label.text.toString(),
                this.binding.quantity.text.toString(),
                this.binding.unit.text.toString(),
                this.imageByte,
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Calls the Date Picker pop up for setting expiry date.
     */
    private fun callDatePicker() {
        val expiryDate = binding.expiryDate
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                var month = (monthOfYear + 1).toString()
                var day = dayOfMonth.toString()
                if (monthOfYear < 10) {
                    month = "0$month"
                }
                if (dayOfMonth < 10) {
                    day = "0$day"
                }
                val selectedDate = "$year-${month}-$day"
                expiryDate.setText(selectedDate)
            }

        val datePickerDialog =
            DatePickerDialog(requireContext(), dateSetListener, year, month, day)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private val MAX_DECIMAL_PLACES = 2 // Set the maximum number of decimal places here

    private fun setUpQuantityText() {
        val editQuantity = binding.quantity

        editQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Nothing to do here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nothing to do here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val decimalIndex = s?.indexOf(".") ?: -1

                if (s != null) {
                    if (decimalIndex != -1 && s.length - decimalIndex - 1 > MAX_DECIMAL_PLACES) {
                        // Too many decimal places, remove the extra ones
                        val truncatedString = s.substring(0, decimalIndex + MAX_DECIMAL_PLACES + 1)
                        editQuantity.setText(truncatedString)
                        editQuantity.setSelection(truncatedString.length)
                    }
                }
            }
        })
    }

    private fun checkNameLabel() {
        val foodName = binding.name
        Log.e("foodName", foodName.text.toString())

        foodName.onFocusChangeListener = OnFocusChangeListener { _, b ->
            if (!b) {
                // on focus off
                val listAdapter = binding.name.adapter

                for (i in 0 until listAdapter.count) {
                    val temp = listAdapter.getItem(i).toString()
                    if (foodName.text.toString().lowercase().compareTo(temp) === 0) {
                        foodNameFound = true
                        return@OnFocusChangeListener
                    }
                }
                foodNameFound = false
                binding.name.error = "Please enter a food name in the list"
            }
        }
    }


    // File upload: Stores the image the user selects from their gallery into the 'imagePath' variable
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // For rendering and post-processing (for database storage)
        if (resultCode == RESULT_OK && (requestCode == pickImage || requestCode == REQUEST_IMAGE_CAPTURE)) {

            // For processing the gallery-retrieved image
            if (requestCode == pickImage) {
                imagePath = data?.data
                imageBitmap = if (Build.VERSION.SDK_INT >= 28) {
                    val source =
                        ImageDecoder.createSource(requireActivity().contentResolver, imagePath!!)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imagePath!!)
                }

            // For processing the camera image
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageBitmap = data?.extras?.get("data") as Bitmap
            }

            binding.imageView.setImageBitmap(imageBitmap)
            bos = ByteArrayOutputStream();
            imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 33, bos)
            imageByte = bos?.toByteArray();
            binding.imageView.visibility = View.VISIBLE
        }
    }

    /**
     * Called when the view is created.
     * The itemId Navigation argument determines the edit item  or add new item.
     * If the itemId is positive, this method retrieves the information from the database and
     * allows the user to update it.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val ingredientsListFromCSV = ArrayList<String>()
        val inputStream: InputStream = resources.openRawResource(R.raw.ingredients_list)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.defaultCharset()))
        reader.readLines().forEach {
            ingredientsListFromCSV.add(it)
        }
//         Adding Dropdown options for ingredient name, label and unit
        val labels = arrayOf(
            "Fruits",
            "Vegetables",
            "Meat",
            "Canned Goods",
            "Dairy",
            "Meat",
            "Seafood",
            "Deli",
            "Condiments",
            "Snacks",
            "Bakery",
            "Beverages",
            "Pasta, Rice, and Cereal",
            "Frozen"
        )
        
        val units = arrayOf("Grams", "Kilograms", "Litres", "Ounces", "Pounds", "Count")
        val namesArray = ArrayAdapter(requireContext(), R.layout.list_item, ingredientsListFromCSV)
        val unitsArray = ArrayAdapter(requireContext(), R.layout.list_item, units)
        binding.name.setAdapter(namesArray)
        binding.unit.setAdapter(unitsArray)
        binding.unit.setText("Count", false)

        // Add default labels
        labels.forEach { viewModel.addNewLabel(it) }
        // populate labels dropdown from table
        viewModel.allLabels.observe(this.viewLifecycleOwner) { labels ->
            labels?.let {
                val labelsArray = ArrayAdapter(requireContext(), R.layout.list_item, it)
                binding.label.setAdapter(labelsArray)
            }
        }

        val expiryDate = binding.expiryDate
        expiryDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                callDatePicker()
            }
        }
        val quantity = binding.quantity
        quantity.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setUpQuantityText()
            }
        }
        val foodName = binding.name
        foodName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                checkNameLabel()
            }
        }
        val id = navigationArgs.itemId

        // Protocol for editing an existing item
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }

        // Protocol for adding a new item
        } else {
            binding.saveAction.setOnClickListener {
                binding.name.clearFocus()
                addNewItem()
            }
        }

        // Opens the phone's gallery
        binding.uploadPhoto.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        // Opens the phone's camera tool
        binding.takePhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
