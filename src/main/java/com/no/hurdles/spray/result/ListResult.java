package com.no.hurdles.spray.result;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @description: 前端列表展示（用于分页）
 * @author : chenwei
 * @param <T>
 */
public class ListResult<T> {
	private Integer total;
	private List<T> list;

	private ListResult(List<T> list, int total) {
		this.total = total;
		this.list = list;
	}

	public static ListResult ok(List list, Integer total) {

		if(null == total){
			total = 0;
		}
		if (CollectionUtils.isEmpty(list)){
			return okEmpty();
		}
		if(0 == total){
			total = list.size();
		}
		return new ListResult(list, total);
	}

	public static ListResult ok(List list) {
		return ok(list, CollectionUtils.isEmpty(list) ? 0 : list.size());
	}

	public static ListResult okEmpty() {
		return new ListResult(Collections.EMPTY_LIST, 0);
	}

	public int getTotal() {
		return total;
	}

	public List<T> getList() {
		return list;
	}

}