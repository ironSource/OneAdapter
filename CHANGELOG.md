Changelog
=========

Version 2.1.3
-------------
* Fixed: timing bug when paging would sometimes not start when initial data was below threshold

Version 2.1.2
-------------
* Fixed: issue #43
* Added: isRebinding flag to onBind Metadata

Version 2.1.1
-------------
* Fixed: unable to update data from a background thread
* Fixed: paging not triggered when RecyclerView can't scroll
* Fixed: paging triggers on EmptyIndicator

Version 2.1.0
-------------
* Updated: internal diffing mechanism to fix the famous inconsistency crash
* Updated: removed deprecated Bintray publish and moved to Jitpack

Version 2.0.2
-------------
* Added: new selection trigger - Manual
* Added: support for Android view binding
* Fixed: selection metadata not updated correctly

Version 2.0.1
-------------
* Added: new selection trigger - Manual
* Fixed: selection metadata not updated correctly

Version 2.0.0
-------------
* Breaking Changes: Kotlin is now the first priority of this library and as such comes a full API change, every Module, Hook and State is now created using dedicated DSLs
* Added:
    * Ability to trigger selection with click and long click
    * More "update" function to OneAdapter API
    * More tests
* Fixed: bug in paging threshold not calculated correctly

Version 1.5.2
-------------
* Improved: ItemSelectionModule new abilities:
    * Start manual selection
    * New callbacks: onSelectionStarted, onSelectionEnded
    * New queries: isSelectionActive & isPositionSelected
* Improved: SwipeEventHook with Up and Down swipe direction support
* Breaking Changes:
    * Right and Left swiping direction changed to Start and End for better support LTR and RTL

Version 1.5.1
-------------
* Fixed: recycler view crash due to race condition with background and ui threads while request multiple diffing quickly
* Breaking Changes:
    * Wrapped event hooks models with Item object in order to get additional information like item position, isSelected, etc... (using the Metadata object)


Version 1.5.0
-------------
* Fixed: clear() not triggering onBind of emptiness module when the adapter was already empty
* Breaking Changes: 
    * Wrapped each model with Item object in all module's OnBind and onUnbind call in order to get additional information like item position, isSelected, etc... (using the Metadata object) 
    * Removed modules actions class, each module now holds its own actions


Version 1.4.1
-------------
* Added: ability to get view types from the adapter to support GridLayoutManager.SpanSizeLookup
* Added: more tests to cover paging


Version 1.4.0
-------------
* Added: support for ButterKnife and DataBinding
* Added: more utility functions like item visibility
* Added: examples for nested RecyclerView, ButterKnife and DataBinding in the sample project
* Fixed Bug: Sync issues with background and ui threads when using nested RecyclerViews with OneAdapter
* Breaking Changes: 
    * ViewBinder getRootView function changed to a property
    * Added HookConfig for SwipeEventHook to specify the supported swipe directions


Version 1.3.0
-------------
* Added: First Bind Animation Config
* Improved: threading mechanism
* Added: common UI tests
* Breaking Changes: 
    * RecyclerView is now a constructor parameter instead of an attachment function
    * OnUnbind functions now includes the model as a parameter


Version 1.2.0
-------------
* Change: Diffable is now mandatory when working with OneAdapter
* Bug Fixes:
    * Paging Module not working properly with some layout manager
    * onUnbind call now called when the view is recycled instead of detached from window


Version 1.1.1
-------------
* Removed the res folder to prevent conflicts


Version 1.1.0
-------------
* Event Hooks:
	* Swipe Event Hook
* Bug fixes related to the diffing mechanism
* Removed unnecessary lines from manifest


Version 1.0.0
-------------
First Release Includes:
* Modules:
	* Item Module
	* Emptiness Module
	* Paging Module
	* Selection Module
* Event Hooks:
	* Click Event Hook
* Diffable Interface
