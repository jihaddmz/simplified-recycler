# Simplified Recycler

Simplified Recycler is an Android library for simplifying the setup for recycler adapters, i.e no need to create adapters for recycler views anymore.

## Installation

1. Add the JitPack repository to your **settings.gradle.kts** file

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```
2. Add the dependency
```kotlin
dependencies {
	  implementation 'com.github.jihaddmz:simplified-recycler:version'
}
```
## Usage

#### *Make sure viewBinding is activated*

```kotlin
    private fun setUpRecycler() {
        val listOfStrings = mutableListOf(Test("A"), Test("B"), Test("C"))

        viewBinding.rv.adapter = simplifiedRecycler(
            ItemRecyclerviewBinding::inflate,
            listOfStrings,
            this,
        ) { item, position ->

            itemBinding.tv.text = item.text

            itemBinding.ivDelete.setOnClickListener {
                clearItem(layoutPosition) // if you want to delete the current item, just call this method and pass
                // the current position/layoutPosition
            }

            itemBinding.ivEdit.setOnClickListener {
                item.text = "Jihad"
                updateItem(position, itemBinding.llForeground) // this method is called whenever you have updated
                // the current item
            }

            itemBinding.iv.setOnClickListener {
                addItem(2, Test("Jihad")) // method responsible for adding an item at specified position
            }

            enableSwipe(itemBinding.llForeground, onSwiped = {
                Toast.makeText(this@MainActivity, "Swiped", Toast.LENGTH_SHORT).show()
            }, onSwipeReset = {
                Toast.makeText(this@MainActivity, "Swiped reset", Toast.LENGTH_SHORT).show()
            }) // for enabling swipe to action on recycler item

        }
    }
```

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

## License

[MIT](https://choosealicense.com/licenses/mit/)
