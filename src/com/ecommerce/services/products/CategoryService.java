package com.ecommerce.services.products;
import com.ecommerce.common.enums.ProductCategory;
import java.util.Arrays;
import java.util.List;
public class CategoryService {
  public List<ProductCategory> getAllowedCategories(){return Arrays.asList(ProductCategory.values());}
  public boolean isAllowedCategory(String value){
    if(value==null)return false;
    try{ProductCategory.valueOf(value.trim().toUpperCase());return true;}catch(IllegalArgumentException e){return false;}
  }
}
