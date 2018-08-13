package com.base.test.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.trs.search.annotations.HybaseDocument;
import com.trs.search.annotations.HybaseId;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@HybaseDocument(indexName = "app_original_20160321")
public class AppOriginal{

	@HybaseId
	private String id;
}

