package nl.eetgeenappels.ssssv.veinminer.search

enum class SearchStrategies(val strategy: SearchStrategy) {

    BREADTH_FIRST_SEARCH(BreadthFirstSearch()),
    CUBE_SEARCH(CubeSearch());

    companion object {
        fun fromString(name: String): SearchStrategies? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}