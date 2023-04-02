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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventory.databinding.ItemListFragmentBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class ItemListFragment : Fragment() {
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao(),
            (activity?.application as InventoryApplication).database.labelDao()
        )
    }

    private var _binding: ItemListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ItemListAdapter {
            val action =
                ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id)
            this.findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerView.adapter = adapter
        // Attach an observer on the allItems list to update the UI automatically when the data
        // changes.
        viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
            items.let {
                adapter.submitList(it)
            }
        }

        binding.floatingActionButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }

        binding.recipesButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToRecipeListFragment()
            this.findNavController().navigate(action)
        }

        binding.grocerySearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                p0?.let {
                    viewModel.getItems(p0)
                }
                return true
            }

        })

        binding.All.setOnClickListener{
            viewModel.getAllItems()
        }

        binding.Fruits.setOnClickListener{
            viewModel.filterItems("Fruits")
        }

        binding.Vegetables.setOnClickListener{
            viewModel.filterItems("Vegetables")
        }

        binding.Canned.setOnClickListener{
            viewModel.filterItems("Canned Goods")
        }

        binding.Dairy.setOnClickListener{
            viewModel.filterItems("Dairy")
        }

        binding.Meat.setOnClickListener{
            viewModel.filterItems("Meat")
        }

        binding.Seafood.setOnClickListener{
            viewModel.filterItems("Seafood")
        }

        binding.Deli.setOnClickListener{
            viewModel.filterItems("Deli")
        }

        binding.Condiments.setOnClickListener{
            viewModel.filterItems("Condiments")
        }

        binding.Snacks.setOnClickListener{
            viewModel.filterItems("Snacks")
        }

        binding.Bakery.setOnClickListener{
            viewModel.filterItems("Bakery")
        }

        binding.Beverages.setOnClickListener{
            viewModel.filterItems("Beverages")
        }

        binding.PRC.setOnClickListener{
            viewModel.filterItems("Pasta, Rice, and Cereal")
        }

        binding.Frozen.setOnClickListener{
            viewModel.filterItems("Frozen")
        }

        binding.foodBankButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=food donation")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getItems()
    }
}
