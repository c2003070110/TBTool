package com.walk_nie.taobao.montBell;


public class CategoryObject {

	public CategoryObject rootCategory;
	public CategoryObject p02Category;

	public String categoryId;
	public String categoryName;
	public String categoryUrl;
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\"");
		sb.append(rootCategory.categoryId + "|");
		sb.append(rootCategory.categoryName + "|");
		sb.append(p02Category.categoryId + "|");
		sb.append(p02Category.categoryName + "|");
		sb.append(categoryId + "|");
		sb.append(categoryName + "|");
		sb.append("\"");
		return sb.toString();
	}
}
