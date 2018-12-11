package com.board.applicion.view.search

data class SearchCondition(var id: Long, var name: String, var type: Int,var resultList:List<SearchResult>)

data  class SearchResult(var id:Long,var name:String)